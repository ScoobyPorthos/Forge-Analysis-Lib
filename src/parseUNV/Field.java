package parseUNV;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class Field {
	
	private int idField;
	public String property="";
	private String name;
	
	private HashMap<Integer,ArrayList<String>> values = new HashMap<>();
	
	Field (ArrayList<String> container)
	{
		int i = 0;
		String last_line="";
		
		for(String line : container)
		{
			if(i<13)
			{
				if(i==0)
					this.idField = Integer.parseInt(line.trim());
				else if(i==1)
					this.name = line.trim();
				
				this.property += line+"\n"; 
			}
			else
			{
				if(i%2==0)
				{
					String[] data = (last_line+" "+line.trim()).split(" ");
					List<String> work_line = IntStream.range(0,data.length).filter(a->data[a].trim().length()>0).mapToObj(a->data[a].trim()).collect(Collectors.toList());
					this.values.put(Integer.parseInt(work_line.get(0)),(ArrayList<String>)IntStream.range(0,work_line.size()).filter(a->a>0).mapToObj(a->work_line.get(a)).collect(Collectors.toList()));
				}
				last_line = line.trim();
			}
			
			i++;
		}
	}
	public void setValues(HashMap<Integer, ArrayList<String>> values) {
		this.values = values;
	}
	public void setIdField(int idField) {
		this.idField = idField;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return this.name;
	}
	
	public int getIdField() {
		return this.idField;
	}
	@Override
	public String toString() {
		return this.values.entrySet().stream().map(entry -> entry.getKey().toString()+";"+(String) entry.getValue().stream().map(Object::toString).collect(Collectors.joining(";"))).collect(Collectors.joining("\n"));
	}
	public HashMap<Integer,ArrayList<String>> getValues() {
		return this.values;
	}
	public String toUNV() {
		String res = "-1\n\t 2414\n"+this.property;
		switch(this.idField)
		{
		case 0:
			res += this.values.entrySet().stream().map(entry -> "\t"+entry.getKey().toString()+" \n\t"+(String) entry.getValue().stream().map(Object::toString).collect(Collectors.joining("\t"))).collect(Collectors.joining("\n"));
			break;
		case 1:
			res += this.values.entrySet().stream().map(entry -> "\t"+entry.getKey().toString()+" \t 6 \n\t"+(String) entry.getValue().stream().map(Object::toString).collect(Collectors.joining("\t"))).collect(Collectors.joining("\n"));
			break;
		default:
			System.out.println("Unknown Type ! ");
			break;
		}
		return res+"\n-1\n";
	}
}
