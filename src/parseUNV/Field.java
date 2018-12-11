package parseUNV;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Field {
	
	private int idField;
	public String property="";
	private String name;
	
	public enum Type {UNV,CSV};
	
	private List<List<String>> values = new ArrayList<>();
	
	Field (Type containerType,ArrayList<String> container)
	{
		switch (containerType) {
		case UNV:
			this.readFromUNV(container);
			break;
		case CSV:
			this.readFromCSV(container);
			break;
		default:
			System.out.println("unknown container type");
			break;
		}
	}
	private void readFromUNV(ArrayList<String> container) {
		
		this.idField = Integer.parseInt(container.stream().findFirst().get());
		this.name = container.stream().skip(1).findFirst().get();
		ArrayList<String[]>lines = container.stream().skip(13).filter(e->!Objects.equals(e,"")).collect(
				() -> new ArrayList<>(), 
		        (acc, next) -> {
		            if(acc.isEmpty()) {
		                acc.add(new String[] {next.trim(), null});
		            }else if(acc.get(acc.size() - 1)[1]==null) {
		            	acc.get(acc.size() - 1)[1] = next.trim();
		            }else{
		                acc.add(new String[] {next.trim(), null});
		            }
		        },
		        ArrayList::addAll
		    );
			
		if(lines.size()>0)
			this.values = lines.stream().map(UNV.splitUNV).collect(Collectors.toList());
	}
	private void readFromCSV(ArrayList<String> container) {
		this.values = container.stream().map(UNV.splitCSV).collect(Collectors.toList());
	}
	public void setValues(List<List<String>> values) {
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
		return this.values.stream().map(entry -> entry.stream().collect(Collectors.joining(";"))).collect(Collectors.joining("\n"));
	}
	public List<List<String>> getValues() {
		return this.values;
	}
	public String toUNV() {
		String res = "-1\n\t 2414\n"+this.property;
		switch(this.idField)
		{
		case 0:
			res += this.values.stream().map(entry -> "\t"+ entry.get(0) +" \n\t"+entry.stream().skip(1).collect(Collectors.joining("\t"))).collect(Collectors.joining("\n"));
			break;
		case 1:
			res += this.values.stream().map(entry -> "\t"+entry.get(0)+" \t 6 \n\t"+entry.stream().skip(1).collect(Collectors.joining("\t"))).collect(Collectors.joining("\n"));
			break;
		default:
			System.out.println("Unknown Type ! ");
			break;
		}
		return res+"\n-1\n";
	}
}
