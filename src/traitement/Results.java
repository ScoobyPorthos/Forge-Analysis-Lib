package traitement;


import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import parseVTF.VTF;

public class Results {

	public static void main(String[] args) {
		

		ArgumentParser parser = ArgumentParsers.newFor("Results").build()
                .defaultHelp(true)
                .description("Extract Data from a VTF file into a separate file");
		
		parser.addArgument("-a","--action")
			.choices("variablesNames","data","coordinates")
			.setDefault("VariablesNames")
			.help("What do you want to do ?");

		parser.addArgument("-s", "--selector")
				.setDefault(Arguments.SUPPRESS)
                .help("field ID to extact with comma separated value ([-a Data] need to be selected)");
		
		parser.addArgument("--thread")
			.setDefault(10)
	        .help("thread number used to process the data");
        
		/*parser.addArgument("-r", "--reference")
			.setDefault(Arguments.SUPPRESS)
			.help("field ID to take as reference ([-a Data] need to be selected)");*/
        
		parser.addArgument("folder")
                .help("Folder hosting the VTF Collection");
        
        
        Namespace ns = null;
        try {
            ns = parser.parseArgs(args);
            
            switch (ns.get("action").toString()) {
    		case "data":
    			ArrayList<Integer> selection = new ArrayList<>();
        		String [] selector = ns.get("selector").toString().split(",");
        		selection.addAll(IntStream.range(0, selector.length).filter(i->!selector[i].isEmpty()).mapToObj(i->Integer.parseInt(selector[i])).collect(Collectors.toList()));
        		//@SuppressWarnings("unused")
        		File[] files = new File(ns.get("folder").toString()).listFiles(Results.vtfFilter);
        		Arrays.sort(files,Results.sensorOrder);
        		
        		ExecutorService service = Executors.newFixedThreadPool(Integer.parseInt(ns.get("thread").toString()));
                IntStream.range(0, files.length).forEach(i -> service.submit(() -> {
                	
                	System.out.println("Task ID : " + i + " started by "+ Thread.currentThread().getName()+"...");
                	VTF data = new VTF(files[i]);
        			data = new VTF(files[i],selection);
        			data.save(files[i].getParent()+"\\"+files[i].getName()+".csv");
        			System.out.println("Task ID : " + i + " terminated");
        			
                }));
        		service.shutdown();
    			break;
    		case "variablesNames":
    			Results variableNames = new Results(new File(ns.get("folder").toString()));
    			variableNames.getVariablesNames();
    			break;
    		case "coordinates":
    			Results coordinates = new Results(new File(ns.get("folder").toString()));
    			coordinates.getCoordinates();
    			break;
    		default:
    			break;
    		}
           
            
        } catch (ArgumentParserException e) {
            parser.handleError(e);
        }

	}
	
	private File dir;
	private Integer ref;
	private VTF reference;
	private ArrayList<Integer> selection = new ArrayList<>();
	
	
	public static FilenameFilter vtfFilter = new FilenameFilter() {
		public boolean accept(File dir, String name) {
			return name.toLowerCase().endsWith(".vtf");
		}
	};
	
	private static Comparator<File> sensorOrder = new Comparator<File>(){
		@Override
		public int compare(File f1, File f2) {
			Matcher matcher = Pattern.compile("\\d+").matcher(f1.getName());
			matcher.find();
			int s1 = Integer.valueOf(matcher.group());
			matcher = Pattern.compile("\\d+").matcher(f2.getName());
			matcher.find();
			int s2 = Integer.valueOf(matcher.group());

			return Integer.valueOf(s1).compareTo(Integer.valueOf(s2));  
		}};
	
	public Results(File dir)
	{
		this.dir = dir;
	}
	
	public Results(File dir,Integer ref, ArrayList<Integer> selection)
	{
		this.dir = dir;
		this.ref = ref;
		this.selection = selection;
		this.loadFile();
	}
	private void loadFile() // TODO Fix this to have a better efficency !!!!
	{
		File[] results;

		try {    

			// create new file
			results = this.dir.listFiles(Results.vtfFilter);
			
			Arrays.sort(results,Results.sensorOrder);
			
			this.loadReference(results[0]);

			VTF res = null;
			VTF data = null;
			for(Integer s:this.selection)
			{
				
				res = this.reference.clone();
				System.out.println("Extacting ID "+s+" ...");
				
				ArrayList<Integer> select = new ArrayList<>();
				select.add(s);
				
				for(File file:results)
				{
					System.out.println("\t + "+file.getName());
					data = new VTF(file,new ArrayList<>(Arrays.asList(s)));
					res.addColumns(data.getColumn());
				}
				
				res.save(this.dir.getPath()+"\\"+this.reference.getSubjects().stream().map(e -> e.replaceAll("\"", "")).collect(Collectors.joining("_"))+s+".csv");
				
				System.out.println("\t => ID "+s+" extracted in "+this.reference.getSubjects().stream().map(e -> e.replaceAll("\"", "")).collect(Collectors.joining("_"))+s+".csv");
			}
			System.out.println("Finished ! ");

		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	private void loadReference(File file)
	{
		ArrayList<Integer> selection = new ArrayList<>();
		selection.add(this.ref);
		this.reference = new VTF(file,selection);
		System.out.println("Reference ("+this.reference.getSubjects().stream().map(e -> e.replaceAll("\"", "")).collect(Collectors.joining(""))+") has been loaded\n");
	}
	
	public void getVariablesNames()
	{
		File[] results=null;

		results = this.dir.listFiles(Results.vtfFilter);

		VTF data = new VTF(results[0]);
		data.getData();
		
	}
	public HashMap<Integer,ArrayList<Double>> getCoordinates()
	{
		File[] results;

		try {    

			// create new file
			results = this.dir.listFiles(Results.vtfFilter);
			Arrays.sort(results,Results.sensorOrder);
			
			
			for(File file:results)
			{
				VTF data = new VTF(file);
				System.out.println(file.getName()+" : ("+
						data.content.get(0).getDoubleValue("\"X\"")+","+data.content.get(0).getDoubleValue("\"Z\"")+") -> ("+
						data.content.get(data.content.size()-1).getDoubleValue("\"X\"")+","+data.content.get(data.content.size()-1).getDoubleValue("\"Z\"")+")");
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}	
		return null;	
	}
}
