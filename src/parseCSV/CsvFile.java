package parseCSV;

/**
 * ^@author Samuel Bertrand & Claire Fougerouse (B1)
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class CsvFile {
    private File file;  
    private ArrayList<String> subjects = new ArrayList<>();
    private HashMap<Integer, Line> content = new HashMap<>();

    @SuppressWarnings("unused")
	public static void main(String[] args) 
    {
    	ArrayList<String> myColumn = new ArrayList<String>();
    	
    	myColumn.add("a[m/s^2]");
    	myColumn.add("x[m]");
    	myColumn.add("v[m/s]");
    	myColumn.add("id");
    	
    	HashMap<String, Double> myValue;
    	//Filter selection = null;
    	File f = new File("FieldExport.txt");
    	CsvFile f2 = new CsvFile(f);
    	
    	f2.display();
    	
    }
    
	public CsvFile(File file) { 
		this.file = file;
		this.readFile();
    }
	
	public CsvFile(File file, ArrayList<String> selectedColumns, Filter selection) { 
		this.file = file;
		this.readFile(selectedColumns,selection);
	}
	
	public HashMap<Integer, Line> getContent() {
		return content;
	}
	public ArrayList<String> getSubjects() {
		return subjects;
	}
    private void readFile(){ // lecture du fichier et enregistrement des données.
    	 FileReader fr;
 		try {
 			fr = new FileReader(file);
 			BufferedReader br = new BufferedReader(fr);
 			String subjectLine = br.readLine(); 
 			if(subjectLine!=null)
	 			for (String s : subjectLine.split(","))
	 				subjects.add(s.trim());
 		    int lineNumber = 1;
 			for (String line = br.readLine(); line != null; line = br.readLine()) {
 		            content.put(lineNumber++, new Line(lineNumber,line,subjects));		  
 		        }
 		        br.close();
 		        fr.close();
 		} catch (FileNotFoundException e) {
 			e.printStackTrace();
 		} catch (IOException e) {
 			e.printStackTrace();
 		}
    }
    private void readFile(ArrayList<String> selectedColumns, Filter selection){
    	FileReader fr;
 		try {
 			fr = new FileReader(file);
 			BufferedReader br = new BufferedReader(fr);
 			String subjectLine = br.readLine(); 
 			for (String s : subjectLine.split(","))
 				subjects.add(s.trim());
 		    int lineNumber = 1;
 			for (String line = br.readLine(); line != null; line = br.readLine()) {
 				Line l = new Line(lineNumber++,line,subjects);
 				if(selection.valid(l))
 		            content.put(lineNumber,l);		  
 		        }
 		        br.close();
 		        fr.close();
 		} catch (FileNotFoundException e) {
 			e.printStackTrace();
 		} catch (IOException e) {
 			e.printStackTrace();
 		} 
    }
    private void writeFile(File fileW){// ecriture au format csv des données enregistrées
            FileWriter fr;
            String recordedTitle = "";
    		try {
    			fr = new FileWriter(fileW);
    			int i;
    			for ( i = 0; i < subjects.size()-1; i++)
    				recordedTitle += subjects.get(i) + ",";
    			recordedTitle +=  subjects.get(i) +"\n";
    			fr.write(recordedTitle);
    			
    	        for (Line line : content.values()) {
    	            fr.write(line.getCSVLine());
    	        }
    	        fr.close();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
        }
    public void createFile(String directory, String name){ // create only csv file
    	File new_file = new File(directory+name+".csv");
    	if(!new_file.exists())
    		new_file.mkdir();
    	this.writeFile(new_file);
   }
    
    /*public void swap(String c1, String c2){ 
    	// permutte deux colonnes (noms et valeurs)
    	for(Line line : content.values())
    		line.swap(c1, c2);
    	
    	int index1 = this.subjects.indexOf(c1);
		int index2 = this.subjects.indexOf(c2);
		
		this.subjects.remove(index1);
		this.subjects.remove(index2);
		
		this.subjects.add(index1,c2);
		this.subjects.add(index2,c1);
    }*/

    public void display(){// affiche à l'écran le contenu enregistré
    	System.out.print("|\t");
    	for(String head : this.subjects)
    		System.out.print(head+"\t|\t");
    	System.out.println("");
    	for(Line line : this.content.values())
    		line.display();
    }
    
    public void addColumn(String name, Operation op){
    	// ajoute une colonne (name) résultat de l'operation op
    	
    	this.subjects.add(name);
    	
    	for(Line l:content.values())
    		l.addValue(name, op);
    	
    }
    
   
    

}
