package GLView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class VTF {

	private File file;
	private int id;
	
	private HashMap<Integer,ArrayList<Double>> values = new HashMap<>();
	private HashMap<Integer,ArrayList<Double>> elements = new HashMap<>();
	private boolean element = false;
	
	
	public static void main(String[] args) { // ID_part_to_extract path/2/VTF_file path/2/Extracted_RESULTS
		File f = new File(args[1]);
		VTF data = new VTF(f,Integer.parseInt(args[0]));
		data.writeValues(args[2]);
	}
	
	VTF(File f,int id)
	{
		this.file = f;
		this.id = id;
		this.readFile();
	}

	private void readFile() {
		FileReader fr;
		try {
			fr = new FileReader(this.file);
			BufferedReader br = new BufferedReader(fr);
			
		    String container = "";
		    int id=0;
		    Pattern patternContainer = Pattern.compile("^\\*(NODES|ELEMENTS|RESULTS) ([0-9]+)$");
		    Pattern patternElement = Pattern.compile("^%(PER_ELEMENT) #([0-9]+)$");
			Pattern patternComment = Pattern.compile("^%.+$");
			Matcher matcherContainer;
			Matcher matcherComment;
			Matcher matcherElements;
			Boolean delimeterContainer = false;
			int a = 0;
			
			for (String line = br.readLine(); line != null; line = br.readLine()) {
				
				matcherContainer = patternContainer.matcher(line.trim());
				matcherComment = patternComment.matcher(line.trim());
				matcherElements = patternElement.matcher(line.trim());
				
		        if(matcherContainer.find())
		        {
		        	container = matcherContainer.group(1).trim();
		        	id = Integer.parseInt(matcherContainer.group(2));
		        	delimeterContainer = true;
		        	a = 0;
		        }
		        else
		        	delimeterContainer = false;
		        
		        if(matcherElements.find() && !this.element && Integer.parseInt(matcherElements.group(2))==this.id)
		        	this.element = true;
		        
		        
		        if(this.id==id && !delimeterContainer && !matcherComment.find() && line.trim().length()>0)
		        {
		        	String[] data = line.trim().split(" ");
		        	if(container.equals("NODES"))
		        	{
		        		this.values.put(a,(ArrayList<Double>)IntStream.range(0, data.length).mapToObj(i->Double.parseDouble(data[i])).collect(Collectors.toList()));
		        		a++;
		        	}
		        	if(container.equals("ELEMENTS"))
		        	{
		        		this.elements.put(a,(ArrayList<Double>)IntStream.range(0, data.length).mapToObj(i->Double.parseDouble(data[i])).collect(Collectors.toList()));
		        		a++;
		        	}
		        	else if(container.equals("RESULTS"))
		        	{	
		        		if(this.element)
		        			this.elements.get(a).add(Double.parseDouble(line.trim()));
		        		else
		        			this.values.get(a).add(Double.parseDouble(line.trim()));
		        		a++;
		        	}
		        }	
		    }
		        br.close();
		        fr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeValues(String name)
	{
		FileWriter fr;
		try {
			if(this.element)
	    	{
				File path = new File(name);
				fr = new FileWriter(path.getParent()+"\\Elements"+path.getName());
				fr.write("Nodes;...;Value");
		    	fr.write("\n");
		    	fr.write(this.elements.values().stream().map(i-> i.stream().map(e->e.toString()).collect(Collectors.joining(";"))).collect(Collectors.joining("\n")));
		        fr.close();
	    	}
			fr = new FileWriter(name);
			fr.write("ID;X;Y;Z;Value");
	    	fr.write("\n");
	    	fr.write(this.values.values().stream().map(i-> i.stream().map(e->e.toString()).collect(Collectors.joining(";"))).collect(Collectors.joining("\n")));
	        fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}    	
	}
	
}
