package parseVTF;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class VTF{

	public static void main(String[] args) {


		ArgumentParser parser = ArgumentParsers.newFor("VTFTool").build()
                .defaultHelp(true)
                .description("Extract Data from a VTF file into a separate file");
		
		parser.addArgument("-g","--get")
			.choices("variablesNames","data","coordinates")
			.setDefault("VariablesNames")
			.help("What do you want to do ?");
		
		parser.addArgument("--thread")
			.setDefault(10)
			.help("thread number used to process the data");

		parser.addArgument("-s", "--selector")
				.setDefault(Arguments.SUPPRESS)
                .help("field ID to extact with comma separated value ([-a Data] need to be selected)");
		
		parser.addArgument("-f", "--files")
			.action(Arguments.storeTrue())
			.setDefault(false)
	        .help("Is the inputs are files");
		
		parser.addArgument("-d", "--directories")
			.action(Arguments.storeTrue())
			.setDefault(false)
			.help("Is the inputs are directories");

		parser.addArgument("inputs")
				.nargs("+")
                .help("VTF file or VTF directories");
        
        
        Namespace ns = null;
        try {
            ns = parser.parseArgs(args);
            switch (ns.get("get").toString()) {
    		case "data":
    			ExecutorService service = Executors.newFixedThreadPool(Integer.parseInt(ns.get("thread").toString()));
    			String [] selector = ns.get("selector").toString().split(",");
    			List<Integer> selection  = IntStream.range(0, selector.length).filter(i->!selector[i].isEmpty()).mapToObj(i->Integer.parseInt(selector[i])).collect(Collectors.toList());
    			
    			if(ns.getBoolean("files")) {
    				List<String> files = ns.getList("inputs");
    				
    				IntStream.range(0, files.size()).forEach(i -> service.submit(() -> {
                    	
                    	System.out.println("Task ID : " + i + " started by "+ Thread.currentThread().getName()+"...");
                    	VTF data;
                    	File file =  new File(files.get(i));
                    	if(selection.isEmpty())
                    		data = new VTF(file);
                    	else
                    		data = new VTF(file,selection);
            			
            			data.save(files.get(i)+".csv");
            			System.out.println("Task ID : " + i + " terminated");
            			
                    }));
            		service.shutdown();
    			}
    			else if(ns.getBoolean("directories"))
    			{
    				List<String> dirs = ns.getList("inputs");
    				
    				for(String dir : dirs)
    				{
    					System.out.println(dir);
    					File[] files = new File(dir).listFiles(VTF.vtfFilter);
    					
    					IntStream.range(0, files.length).forEach(i -> service.submit(() -> {
                        	
                        	System.out.println("Task ID : " + i + " started by "+ Thread.currentThread().getName()+"...");
                        	VTF data;
                        	if(selection.isEmpty())
                        		data = new VTF(files[i]);
                        	else
                        		data = new VTF(files[i],selection);
                			
                			data.save(files[i].getPath()+".csv");
                			System.out.println("Task ID : " + i + " terminated");
                			
                        }));
                		service.shutdown();
    				}
    				System.out.println();
    			}
    			break;
    		case "variablesNames":
    			if(ns.getBoolean("files")) {
    				VTF variableNames = new VTF(new File(ns.getList("inputs").get(0).toString()));
        			variableNames.getData();
    			}
    			else if(ns.getBoolean("directories"))
    			{
    				List<String> dirs = ns.getList("inputs");
    				
    				for(String dir : dirs)
    				{
    					System.out.println(dir);
    					File[] files = new File(dir).listFiles(VTF.vtfFilter);
        				VTF variableNames = new VTF(files[0]) ;
            			variableNames.getData();
    				}
    				System.out.println();
    			}
    			else
    				System.out.println("Please specify if you are using files inputs [-f] or directories inputs [-d] ");
    			break;
    		case "coordinates":
    			//Results coordinates = new Results(new File(ns.<String> getList("file").get(0).toString()));
    			//coordinates.getCoordinates();
    			break;
    		default:
    			break;
    		}
           
            
        } catch (ArgumentParserException e) {
            parser.handleError(e);
        }
        
	}
	
	
	private File file;
	private List<String> subjects = new ArrayList<>();
	private List<Integer> selection = new ArrayList<>();
	public List<List<Double>> content = new ArrayList<>();
	public int dataLine = 0;
	
	public Function<String, List<String>> splitVTFstring = (line) -> {
	  	  String[] p = line.split("\t");
	  	  return IntStream.range(0, p.length).filter(i-> this.selection.isEmpty() || this.selection.contains(i)).mapToObj(i->p[i].replace('\"', ' ').trim().replace(' ', '_')).collect(Collectors.toList());
	  	};
  	public static Function<String, Double> parseDouble = (s) -> {
  		Double d;
  		try{
  	      d = Double.parseDouble(s);
  	    }catch(NumberFormatException nfe){
  	      d = Double.NaN;
  	    }
  		return d;
	  	};
  	public Function<String, List<Double>> splitVTFnum = (line) -> {
	  	  String[] p = line.split("\t");
	  	  return IntStream.range(0, p.length).filter(i-> this.selection.isEmpty() || this.selection.contains(i)).mapToObj(i->p[i]).map(VTF.parseDouble).collect(Collectors.toList());
	  	};
	
  	public static FilenameFilter vtfFilter = new FilenameFilter() {
		public boolean accept(File dir, String name) {
			return name.toLowerCase().endsWith(".vtf");
		}
	};
	
	public VTF(File file)
	{
		this.file = file;
		this.readFile();
	}
	public VTF(File file, List<Integer> selection)
	{
		this.selection = selection;
		this.file = file;
		this.readFile();
	}
	
	public File getFile() {
		return this.file;
	}
	private void readFile()
	{   	 
	    try{
	      InputStream inputFS = new FileInputStream(this.file);
	      BufferedReader br = new BufferedReader(new InputStreamReader(inputFS));
	      this.subjects = br.lines().skip(7).findFirst().map(this.splitVTFstring).get();
	      this.content =  br.lines().skip(9).map(this.splitVTFnum).collect(Collectors.toList());
	      br.close();
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
	}
	public List<String> getSubjects() {
		return this.subjects;
	}
	public void save(String name)
	{
		FileWriter fr;
		try {
			fr = new FileWriter(name);
			fr.write(this.subjects.stream().collect(Collectors.joining(",")));
	    	fr.write("\n");
	    	fr.write(this.content.stream().map(l-> l.stream().map(Objects::toString).collect(Collectors.joining(","))).collect(Collectors.joining("\n")));
	        fr.close();
	        System.out.println(name+" written");
		} catch (IOException e) {
			e.printStackTrace();
		}    	
	}
	public void getData()
	{
		System.out.println(IntStream.range(0, this.subjects.size()).mapToObj(i -> i+"\t->\t"+this.subjects.get(i)).collect(Collectors.joining("\n")));
	}
}
