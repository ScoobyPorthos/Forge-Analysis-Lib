package parseVTF;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import traitement.Results;

public class VTF implements Cloneable{

	public static void main(String[] args) {


		ArgumentParser parser = ArgumentParsers.newFor("VTFTool").build()
                .defaultHelp(true)
                .description("Extract Data from a VTF file into a separate file");
		
		parser.addArgument("-a","--action")
			.choices("variablesNames","data","coordinates")
			.setDefault("VariablesNames")
			.help("What do you want to do ?");

		parser.addArgument("-s", "--selector")
				.setDefault(Arguments.SUPPRESS)
                .help("field ID to extact with comma separated value ([-a Data] need to be selected)");

		parser.addArgument("file")
				.nargs("+")
                .help("VTF file");
        
        
        Namespace ns = null;
        try {
            ns = parser.parseArgs(args);
            
            switch (ns.get("action").toString()) {
    		case "data":
    			ArrayList<Integer> selection = new ArrayList<>();
    			String [] selector = ns.get("selector").toString().split(",");
    			selection.addAll(IntStream.range(0, selector.length).filter(i->!selector[i].isEmpty()).mapToObj(i->Integer.parseInt(selector[i])).collect(Collectors.toList()));
    			
    			VTF vtfFile;
    			for(String name : ns.<String> getList("file"))
    			{
    				File file = new File(name);
    				if(selection.isEmpty())
    					vtfFile = new VTF(file);
    				else
    					vtfFile = new VTF(file,selection);

    				vtfFile.save(file.getParent()+"\\"+file.getName()+".csv");
    			}
    			break;
    		case "variablesNames":
    			Results variableNames = new Results(new File(ns.<String> getList("file").get(0).toString()));
    			variableNames.getVariablesNames();
    			break;
    		case "coordinates":
    			Results coordinates = new Results(new File(ns.<String> getList("file").get(0).toString()));
    			coordinates.getCoordinates();
    			break;
    		default:
    			break;
    		}
           
            
        } catch (ArgumentParserException e) {
            parser.handleError(e);
        }
        
        
        
        
	}
	
	
	private File file;
	//private ArrayList<String> type = new ArrayList<>();
	private ArrayList<String> subjects = new ArrayList<>();
	private ArrayList<Integer> selection = new ArrayList<>();
	public HashMap<Integer, Line> content = new HashMap<>();
	public int dataLine = 0;
	
	public VTF()
	{
		
	}
	
	public VTF(File file)
	{
		this.file = file;
		this.readFile();
	}
	public VTF(File file, ArrayList<Integer> selection)
	{
		this.selection = selection;
		this.file = file;
		this.readFile();
	}
	
	public VTF clone() throws CloneNotSupportedException {
        return (VTF) super.clone();
    }
	
	public File getFile() {
		return this.file;
	}
	private void readFile()
	{
		FileInputStream inputStream = null;
		Scanner sc = null;
		try {
			inputStream = new FileInputStream(file.getAbsolutePath());
			sc = new Scanner(inputStream, "UTF-8");
		    
		    int i = 0;
		    int action = 0;
		    while (sc.hasNextLine()){
		    	String line = sc.nextLine();
				switch(action)
				{
				case 1:
					//this.exctractType(line.trim());
					break;
				case 2 : 
					this.exctractSubject(line.trim());
					break;
				case 3:
					if(this.selection.isEmpty())
						content.put(i, new Line(i,line.trim(),this.subjects));
					else
						content.put(i, new Line(i,line.trim(),this.subjects,this.selection));
					i++;
					break;
				}
				if(line.trim().equals("%VARIABLE_TYPES"))
					action = 1;
				else if(line.trim().equals("%VARIABLE_NAMES"))
					action = 2;
				else if(line.trim().equals("%DATA"))
				{
					action = 3;
				}
				else if(action !=3)
					action = 0;
				
		    }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
	}
	/*private void exctractType(String line)
	{
		List<String> subject = IntStream.range(0,line.split("\t").length)
				.filter(i -> !line.split("\t")[i].isEmpty())
				.mapToObj(i -> line.split("\t")[i])
				.collect(Collectors.toList());
		this.type.addAll(subject);
	}*/
	private void exctractSubject(String line)
	{
		if(!this.selection.isEmpty())
		{
			List<String> subject = IntStream.range(0,line.split("\t").length)
					.filter(i -> this.selection.contains(i+1) && !line.split("\t")[i].isEmpty())
					.mapToObj(i -> line.split("\t")[i].trim())
					.collect(Collectors.toList());
			this.subjects.addAll(subject);
		}
		else
		{
			List<String> subject = IntStream.range(0,line.split("\t").length)
					.filter(i -> !line.split("\t")[i].isEmpty())
					.mapToObj(i -> line.split("\t")[i].trim())
					.collect(Collectors.toList());
			this.subjects.addAll(subject);
		}
	}
	public HashMap<Integer, Line> getColumn(ArrayList<String> column)
	{
		HashMap<Integer, Line> values = new HashMap<>();
		this.content.entrySet().stream().forEach(entry -> values.put(entry.getKey(), entry.getValue().extract(column)));
		return values;
	}
	public HashMap<Integer, Line> getColumn()
	{
		HashMap<Integer, Line> values = new HashMap<>();
		this.content.entrySet().stream().forEach(entry -> values.put(entry.getKey(), entry.getValue().extract()));
		return values;
	}
	public void addColumns(HashMap<Integer, Line> colmuns)
	{
		if(this.content.isEmpty())
			colmuns.entrySet().stream().forEach(entry -> this.content.put(entry.getKey(),entry.getValue()));
		else
			colmuns.entrySet().stream().forEach(entry -> this.content.get(entry.getKey()).addCell(entry.getValue().getData()));
	}
	public ArrayList<String> getSubjects() {
		return this.subjects;
	}
	public void save(String name)
	{
		FileWriter fr;
		try {
			fr = new FileWriter(name);
			fr.write(this.subjects.stream().map(s->s.replaceAll("\"","").trim().replaceAll("\\s","_")).collect(Collectors.joining(",")));
	    	fr.write("\n");
	    	fr.write(this.content.values().stream().map(i-> i.getCSVLine()).collect(Collectors.joining("\n")));
	        fr.close();
	        System.out.println(name+" written");
		} catch (IOException e) {
			e.printStackTrace();
		}    	
	}
	public void getData()
	{
		System.out.println(IntStream.range(0, this.subjects.size()).mapToObj(i -> i+" -> "+this.getSubjects().get(i).substring(1, this.getSubjects().get(i).length()-1)).collect(Collectors.joining("\n")));
	}
}
