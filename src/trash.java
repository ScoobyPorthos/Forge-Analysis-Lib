import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class trash {

	public static void main(String[] args) throws IOException {
		String folder ="G:\\Mon Drive\\LFW\\McAndrew 27 6750\\Friction_DRX\\Top";
		int start_file_num = 1;
			
		File[] dirFiles = trash.loadFiles(new File(folder));
		List<File>files = IntStream.range(start_file_num-1, dirFiles.length).mapToObj(i -> dirFiles[i]).collect(Collectors.toList());
		
		String res = files.stream().map(file -> trash.getLine(file, 30681-1,466, Arrays.asList(3,5))).collect(Collectors.joining("\n"));
		
		FileWriter fr;
		try {
			fr = new FileWriter(folder+"\\..\\End_PositionTop.csv");
			fr.write("X,Z,T");
	    	fr.write("\n");
	    	fr.write(res);
	        fr.close();
	        System.out.println(folder+"\\..\\End_PositionTop.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}    
		
		
		/*String file =".\\VTF\\hcenterline\\billet_hcenterline1.vtf";
		int start = 699;
		
		FileInputStream inputStream = new FileInputStream(new File(file));
	    inputStream.skip(718+1+4*11+6*3+1);
	    Scanner sc = new Scanner(inputStream, "UTF-8");
	    System.out.println(inputStream.rea);
		*/
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
	
	static String getLine(File file,int goto_line, int lineLength, List<Integer> selection){
		
		String res = "";
		
		
		FileInputStream inputStream = null;
		Scanner sc = null;
		try {
		    inputStream = new FileInputStream(file.getAbsolutePath());
		    inputStream.skip(inputStream.available()-lineLength);
		    sc = new Scanner(inputStream, "UTF-8");
		    if (sc.hasNextLine()) {
		        String[] line = sc.nextLine().split("\\t");
		        res = IntStream.range(0, line.length).filter(a->selection.contains(a+1)).mapToObj(a-> line[a].trim()).collect(Collectors.joining(","));
		        System.out.println(file.getName()+" => "+ res);
		    }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		    if (inputStream != null) {
		        try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		    }
		    if (sc != null) {
		        sc.close();
		    }
		}
		return res;
	}
	
}
