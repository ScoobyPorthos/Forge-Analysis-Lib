package JMP;

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



public class JMP {
	
	File file; 
	private ArrayList<String> subjects = new ArrayList<>();
    private HashMap<Integer, ArrayList<String>> content = new HashMap<>();
    
	public static void main(String[] args) {

		File f = new File(args[0]);
		JMP data = new JMP(f);
		data.save();
	}
	
	JMP(File f)
	{
		this.file = f;
		this.readFile();
	}
	
	private void readFile(){ 
	   	 FileReader fr;
			try {
				fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);
			    boolean read = false;
			    boolean getHeader = true;
			    String oldLine = "";
			    int  a = 0;
				for (String line = br.readLine(); line != null; line = br.readLine()) {
					Matcher matcher = Pattern.compile("^col").matcher(oldLine.trim());
					if(matcher.find())
					{
						if(getHeader)
						{
							String[] col = oldLine.trim().split(",");
							this.subjects.addAll(IntStream.range(0,col.length).mapToObj(i->col[i]).collect(Collectors.toList()));
						}
						read =true;
						getHeader = false;
					}
					if(line.trim().length()==0)
						read =false;
					if(read)
					{
						String[] data = line.trim().split(" ");
						this.content.put(a,(ArrayList<String>) IntStream.range(0,data.length).filter(i->data[i].trim().length()>0).mapToObj(i->data[i].trim()).collect(Collectors.toList()));
						a++;
					}
					oldLine = line;
					
				}
		        br.close();
		        fr.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		public void save()
		{
			FileWriter fr;
			try {
				fr = new FileWriter(this.file.getPath()+".csv");
				fr.write(this.subjects.stream().map(Object::toString).collect(Collectors.joining("; ")));
		    	fr.write("\n");
		    	fr.write(this.content.entrySet().stream().map(entry -> (String) entry.getValue().stream().map(Object::toString).collect(Collectors.joining(";"))).collect(Collectors.joining("\n")));
		        fr.close();
		        System.out.println(this.file.getPath()+".csv"+" written");
			} catch (IOException e) {
				e.printStackTrace();
			}    	
		}
}
