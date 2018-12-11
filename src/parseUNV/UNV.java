package parseUNV;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import parseUNV.Field.Type;


public class UNV {

	private File file;
	@SuppressWarnings("unused")
	private String header="";
	private Container nodes;
	private Container elements;
	
	public ArrayList<Field> fields = new ArrayList<>();
	
	public static Function<String, List<String>> splitCSV = (line) -> {
  	  String[] p = line.split(",");
  	  return Arrays.asList(p);
  	};
  	
  	public static Function<String[], List<String>> splitUNV = (line) -> {
    	  String[] p = (line[0]+" "+line[1]).split(" ");
    	  return IntStream.range(0, p.length).filter(i->!Objects.equals(p[i].trim(),"")).mapToObj(i->p[i].trim()).collect(Collectors.toList());
    	};
	
	public static void main(String[] args) {
		
		ArgumentParser parser = ArgumentParsers.newFor("UNV parsing tool").build()
                .defaultHelp(true)
                .description("Read and extract data from an UNV File");
        parser.addArgument("-a", "--action")
                .help("What do you want to do ???")
                .choices("mesh", "csv2unv", "data","variablesNames")
                .setDefault("mesh");
        parser.addArgument("-f", "--field")
        		.help("Export a CSV file to an UNV file : 0 for Temperature and 1 for Stress")
        		.choices("0","1")
        		.setDefault(false);
        parser.addArgument("--input-file")
				.help("Replace the Field ID x by the data in the replacing file")
				.setDefault(false);
        parser.addArgument("-o","--output")
				.help("Output name for the new unv file")
				.setDefault(false);
        parser.addArgument("-d", "--data")
				.help("Retrieve de data Field #ID into a CSV file")
				.setDefault(false);
        parser.addArgument("unvFile")
                .help("path to the unvFile");

        Namespace ns = null;
        try {
            ns = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }
		
        File f = new File(ns.get("unvFile").toString());;
        UNV data;	
        
		switch(ns.get("action").toString())
		{
		case "mesh":
			
			data = new UNV(f);
			
			System.out.println("Export Mesh from "+ns.get("unvFile").toString());
			data.writeCoordinate();
			data.writeElements();
			
		break;
		case "csv2unv":
			
			if(Objects.equals(ns.get("output").toString(), "false") || Objects.equals(ns.get("input_file").toString(), "false") || Objects.equals(ns.get("field").toString(), "false")) {
				System.out.println("Missing Arguments .... [-a replace] requires [-o] [--input-file] [-r]");
				System.exit(1);
			}

			System.out.println("Replace field ID "+ns.get("field").toString()+" from "+ns.get("unvFile").toString()+" by "+ns.get("input_file").toString());
			
			UNV.csv2unv(ns.get("input_file").toString(), Integer.parseInt(ns.get("field").toString()),ns.get("output").toString());
			

		break;
		case "data": // Retrieve data into an CSV file -d file ID
			 
			data = new UNV(f);
			
			System.out.println("Exporting data ID "+ns.get("data").toString()+" from "+ns.get("unvFile").toString());
			if(ns.get("data").toString().split(",").length>1)
				data.writeData(ns.get("data").toString().split(","));
			else
				data.writeData(Integer.parseInt(ns.get("data").toString()));
		
		break;
		case "variablesNames":
			data = new UNV(f);
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
   	 
	   	FileInputStream inputStream = null;
		Scanner sc = null;
   	 
		try {		
			inputStream = new FileInputStream(file.getAbsolutePath());
			sc = new Scanner(inputStream, "UTF-8");
			
			@SuppressWarnings("unused")
			int lineNumber = 0;
		    
			int delimeter = 0;
		    int active_label = 0;
		    
		    ArrayList<String> container = new ArrayList<>();
		    
		    String last_line = "";

		    while (sc.hasNextLine()){
		    	String line = sc.nextLine();
				if(line.trim().equals("-1"))
					delimeter++;
				
				if(delimeter%2==1 && last_line.equals("-1"))
				{	
					active_label = Integer.parseInt(line.trim());	
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
						this.fields.add(new Field(Type.UNV,container));
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

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
   }
    
    public void writeUNV(String simName) //TODO DELETE and ReWrite
    {
    	System.out.println(this.fields.get(1).toUNV());
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
    	System.out.println(this.fields.get(ID).getName());
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
    	ExecutorService service = Executors.newFixedThreadPool(10);
    	IntStream.range(0, ID.length).forEach(i -> service.submit(() -> {
        	
        	System.out.println("Task ID : " + i + " started by "+ Thread.currentThread().getName()+"...");
        	this.writeData(Integer.parseInt(ID[i]));
			System.out.println("Task ID : " + i + " terminated");
			
        }));
    	service.shutdown();
    }
    
    public static void csv2unv(String csvfile,int fieldID,String output) {
    	
    	 ArrayList<String> inputList = new ArrayList<String>();
    	 
    	    try{
    	      File inputF = new File(csvfile);
    	      InputStream inputFS = new FileInputStream(inputF);
    	      BufferedReader br = new BufferedReader(new InputStreamReader(inputFS));

    	      inputList = (ArrayList<String>) br.lines().skip(1).collect(Collectors.toList());
    	      
    	      Field csvField = new Field(Type.CSV,inputList);
    	      csvField.setIdField(fieldID);
    	      csvField.property = "CSV2UNV File convertion from "+csvfile+ " to be pasted where need be in the original UNV file\n";
    	      
    	      
    	      FileWriter fr;
		      try {
					fr = new FileWriter(output);
					fr.write(csvField.toUNV());
			        fr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		    	System.out.println(output+" written");
    	      
    	      br.close();
    	    } catch (IOException e) {
    	    	e.printStackTrace();
    	    }
    	
    }
    
    
	public String getListField() {
		return this.fields.stream().map(field -> field.getIdField()+"-"+field.getName()).collect(Collectors.joining("\n"));
	}
}
