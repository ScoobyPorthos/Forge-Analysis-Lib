import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class trash {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String folder ="D:\\Users\\Scooby\\Documents\\ETS\\LFW\\1.3 - Forge\\vtf\\McA2015a_27_6750\\Friction_DRX-110618#1-Center";
		int start_file_num = 54;
		
		
		File[] dirFiles = trash.loadFiles(new File(folder));
		List<File>files = IntStream.range(start_file_num-1, dirFiles.length).mapToObj(i -> dirFiles[i]).collect(Collectors.toList());
		
		String res = files.stream().map(file -> trash.getLine(file, 30720-1, Arrays.asList(3,5,7))).collect(Collectors.joining("\n"));
		
		FileWriter fr;
		try {
			fr = new FileWriter(folder+"\\End_Temperature.csv");
			fr.write("X,Z,T");
	    	fr.write("\n");
	    	fr.write(res);
	        fr.close();
	        System.out.println(folder+"\\End_Temperature.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}    
		
	}
	
	static File[] loadFiles(File dir) {
		File[] results = null;

		try {    

			// create new file
			results = dir.listFiles(new FilenameFilter() {
											public boolean accept(File dir, String name) {
												return name.toLowerCase().endsWith(".vtf");
											}
										});
			
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

		} catch(Exception e) {
			e.printStackTrace();
		}
		return results;
	}
	
	static String getLine(File file,int goto_line, List<Integer> selection){
		
		String res = "";
		FileReader fr;
		
		System.out.println("Reading "+file.getName()+" ...");
		
		try {
			fr = new FileReader(file.getAbsolutePath());
			BufferedReader br = new BufferedReader(fr);
			int i=0;
			for (i=0;i<goto_line;i++)
				br.readLine();
			String[] line = br.readLine().trim().split("\\t");
			
			res = IntStream.range(0, line.length).filter(a->selection.contains(a+1)).mapToObj(a-> line[a].trim()).collect(Collectors.joining(","));
			
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		return res;
	}
	
}
