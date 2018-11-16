package parseUNV;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;


public class UNV {

	private File file;
	@SuppressWarnings("unused")
	private String header="";
	private Container nodes;
	private Container elements;
	
	public ArrayList<Field> fields = new ArrayList<>();
	
	public static void main(String[] args) {
		
		ArgumentParser parser = ArgumentParsers.newFor("UNV parsing tool").build()
                .defaultHelp(true)
                .description("Read and extract data from an UNV File");
        parser.addArgument("-a", "--action")
                .help("What do you want to do ???")
                .choices("mesh", "replace", "extract","field")
                .setDefault("mesh");
        parser.addArgument("-r", "--replace-ID")
        		.help("Replace the Field ID x by the data in the replacing file")
        		.setDefault(Arguments.SUPPRESS);
        parser.addArgument("--replace-file")
				.help("Replace the Field ID x by the data in the replacing file")
				.setDefault(Arguments.SUPPRESS);
        parser.addArgument("-o","--output-name")
				.help("Output name for the new unv file")
				.setDefault(Arguments.SUPPRESS);
        parser.addArgument("-d", "--data-ID")
				.help("Retrieve de data Field #ID into a CSV file")
				.setDefault(Arguments.SUPPRESS);
        parser.addArgument("unvFile")
                .help("path to the unvFile");

        Namespace ns = null;
        try {
            ns = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }
		
		File f = new File(ns.get("unvFile").toString());
		UNV data = new UNV(f);
				
		switch(ns.get("action").toString())
		{
		case "mesh":
			System.out.println("Export Mesh from "+ns.get("unvFile").toString());
			data.writeCoordinate();
			data.writeElements();
			break;
		case "replace":
			
			CopyOption[] options = new CopyOption[]{
				      StandardCopyOption.REPLACE_EXISTING,
				      StandardCopyOption.COPY_ATTRIBUTES
				    }; 
			try {
				Files.copy(Paths.get(f.getPath()), Paths.get(f.getParent()+"\\"+ns.get("output-name").toString()+".unv"),options);
				System.out.println(f.getParent()+"\\"+ns.get("output-name").toString()+".unv has been created");
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			System.out.println("Replace field ID "+ns.get("replace_ID").toString()+" from "+ns.get("unvFile").toString()+" by "+ns.get("replace_file").toString());
			data.replaceField(Integer.parseInt(ns.get("replace_ID").toString()),ns.get("replace_file").toString());
			data.writeUNV(ns.get("output-name").toString());
			break;
		case "extract": // Retrieve data into an CSV file -d file ID
			
			System.out.println("Exporting data ID "+ns.get("data_ID").toString()+" from "+ns.get("unvFile").toString());
			if(ns.get("data_ID").toString().split(",").length>1)
				data.writeData(ns.get("data_ID").toString().split(","));
			else
				data.writeData(Integer.parseInt(ns.get("data_ID").toString()));
			break;
		case "field":
			System.out.println(data.getListField());
			break;
		default:
			System.out.println("Sorry unknown action. Please refer to the help with -h");
			break;
		}
		
	}


	public UNV(File file)
	{
		this.file = file;
		this.readFile();
	}
	
    private void readFile(){ 
   	 FileReader fr;
		try {
			fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
		  
			@SuppressWarnings("unused")
			int lineNumber = 0;
		    
			int delimeter = 0;
		    int active_label = 0;
		    
		    ArrayList<String> container = new ArrayList<>();
		    
		    String last_line = "";
		    //int key = 0;
		    
		    
			for (String line = br.readLine(); line != null; line = br.readLine()) {
				if(line.trim().equals("-1"))
					delimeter++;
				
				if(delimeter%2==1 && last_line.equals("-1"))
				{	
					active_label = Integer.parseInt(line.trim());	
					//key = lineNumber;
				}
				else if(delimeter%2==0)
				{
					
					switch(active_label)
					{
					case 15:
						//System.out.println("Nodes");
						break;
					case 151:
						this.header = "-1\n151\n"+container.stream().map(e->e).collect(Collectors.joining("\n"))+"\n-1\n";
						break;
					case 164:
						this.header += "-1\n164\n"+container.stream().map(e->e).collect(Collectors.joining("\n"))+"\n-1\n";
						break;
					case 2411 :
						//System.out.println("Coordinates");
						try {
							this.nodes = new Container(2411,"Nodes",container);
						} catch (Exception e1) {
							System.out.println(e1.getMessage());
							e1.printStackTrace();
						}
						//System.out.println(this.nodes.toUNV());
						break;
					case 781 :
						//System.out.println("Coordinates");
						try {
							this.nodes = new Container(781,"Nodes",container);
						} catch (Exception e1) {
							System.out.println(e1.getMessage());
							e1.printStackTrace();
						}
						break;
					case 2412 : 
						//System.out.println("Elements");
						try {
							this.elements = new Container(2412,"Elements",container);
						} catch (Exception e1) {
							System.out.println(e1.getMessage());
							e1.printStackTrace();
						}
						break;
					case 780 :
						//System.out.println("Elements");
						try {
							this.elements = new Container(780,"Elements",container);
						} catch (Exception e1) {
							System.out.println(e1.getMessage());
							e1.printStackTrace();
						}
						break;
					case 55 :
						//System.out.println("Field_nodes");
						break;
					case 56 :
						//System.out.println("Field_elements");
						break;
					case 2414 :
						//System.out.println("Field");
						this.fields.add(new Field(container));
						break;
					default:
						System.out.println(active_label+" is not supported yet !");
						break;
					}
					
					container = new ArrayList<>();
				}
				else
				{
					if(!line.trim().equals("-1"))
						container.add(line.trim());
				}
				last_line = line.trim();
				lineNumber++;
		    }
		        br.close();
		        fr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
   }
    
    public void writeUNV(String simName)
    {
    	FileWriter fr;
    	try {
    		String name = this.file.getParent()+"\\stress4"+simName+".unv";
			fr = new FileWriter(name);
			fr.write(this.fields.get(1).toUNV());
	        fr.close();
	        System.out.println(name+" written");
	        name = this.file.getParent()+"\\temp4"+simName+".unv";
			fr = new FileWriter(name);
			fr.write(this.fields.get(0).toUNV());
	        fr.close();
	        System.out.println(name+" written");
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    public void writeCoordinate()
    {
    	FileWriter fr;
    	String name =this.file.getPath()+"_nodes.nod";
    	try {
			fr = new FileWriter(name);
			fr.write(this.nodes.toString());
	        fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	System.out.println(name+" written");
    }
    public void writeElements()
    {
    	FileWriter fr;
    	String name = this.file.getPath()+"_elements.elm";
    	try {
			fr = new FileWriter(name);
			fr.write(this.elements.toString());
	        fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	System.out.println(name+" written");
    }
    public void writeData(int ID)
    {
    	System.out.println(this.fields.get(ID).property);
    	FileWriter fr;
    	String name = this.file.getPath()+"_Data"+ID+".csv";
    	try {
			fr = new FileWriter(name);
			fr.write(this.fields.get(ID).toString());
	        fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	System.out.println(name+" written");
    	
    }
    public void writeData(String[] ID)
    {
    	for(String id:ID)
    		this.writeData(Integer.parseInt(id));
    }
    public void replaceField(int label, String file)
    {
    	FileReader fr;
    	HashMap<Integer,ArrayList<String>> content = new HashMap<>();
    	
		try {
			fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
		    int  a = 0;
			for (String line = br.readLine(); line != null; line = br.readLine()) {
				if(a>0)
				{
					String[] data = line.trim().split(",");
					content.put(a,(ArrayList<String>)IntStream.range(0,data.length).filter(i->data[i].trim().length()>0).mapToObj(i->data[i].trim()).collect(Collectors.toList()));
				}
				a++;
			}
	        br.close();
	        fr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	this.fields.get(label).setValues(content);
    	
    }
	public String getListField() {
		return this.fields.stream().map(field -> field.getIdField()+"-"+field.getName()).collect(Collectors.joining("\n"));
	}
}
