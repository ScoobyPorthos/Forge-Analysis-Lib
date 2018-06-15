package traitement;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

import parseVTF.VTF;

public class Results {

	public static void main(String[] args) {
		
		ArgumentParser parser = ArgumentParsers.newFor("VTFTool").build()
                .defaultHelp(true)
                .description("Extract Data from a VTF file into a separate file");
        parser.addArgument("-s", "--selector")
                .help("field ID to extact with comma separated value");
        parser.addArgument("-r", "--reference")
        .help("field ID to take as reference");
        parser.addArgument("folder")
                .help("Folder hosting the VTF Collection");
        
        
        Namespace ns = null;
        try {
            ns = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }
		
        ArrayList<Integer> selection = new ArrayList<>();
		String [] selector = ns.get("selector").toString().split(",");
		selection.addAll(IntStream.range(0, selector.length).filter(i->!selector[i].isEmpty()).mapToObj(i->Integer.parseInt(selector[i])).collect(Collectors.toList()));
		
		@SuppressWarnings("unused")
		Results results = new Results(new File(ns.get("folder").toString()),Integer.parseInt(ns.get("reference").toString()),selection);
	}
	
	private File dir;
	private Integer ref;
	private VTF reference;
	private ArrayList<Integer> selection = new ArrayList<>();
	
	public Results(File dir,Integer ref, ArrayList<Integer> selection)
	{
		this.dir = dir;
		this.ref = ref;
		this.selection = selection;
		this.loadFile();
	}
	private void loadFile()
	{
		File[] results;

		try {    

			// create new file
			results = this.dir.listFiles(new FilenameFilter() {
											public boolean accept(File dir, String name) {
												return name.toLowerCase().endsWith(".vtf");
											}
										});
						
			System.out.println(this.dir.listFiles().length);
			System.exit(1);
			
			Arrays.sort(results,new Comparator<File>(){
								@Override
								public int compare(File f1, File f2) {
									Matcher matcher = Pattern.compile("\\d+").matcher(f1.getName());
									matcher.find();
									int s1 = Integer.valueOf(matcher.group());
									matcher = Pattern.compile("\\d+").matcher(f2.getName());
									matcher.find();
									int s2 = Integer.valueOf(matcher.group());

									return Integer.valueOf(s1).compareTo(Integer.valueOf(s2));  
								}});
			
			this.loadReference(results[0]);
			for(Integer s:this.selection)
			{
				System.out.println("ID "+s.toString()+" is loading ...");
				VTF res = new VTF();
				res.addColumns(this.reference.getColumn());
				ArrayList<Integer> select = new ArrayList<>();
				select.add(s);
				VTF data = new VTF();
				for(File file:results)
				{
					data = new VTF(file,select);
					res.addColumns(data.getColumn());
					System.out.println("\t + "+file.getName()+" has been loaded [x]");
				}
				String filename = data.getSubjects().stream().map(e -> e.replaceAll("\"", "")).collect(Collectors.joining(" VS "));
				res.save(this.dir.getPath()+"\\"+filename+".csv");
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
	
}
