package parseUNV;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Container {

	@SuppressWarnings("unused")
	private String name;
	private int containerID;
	private HashMap<Integer,List<String>> values = new HashMap<>();
	
	Container(int containerID,String name,ArrayList<String> lines) throws Exception
	{
		this.name = name;
		this.containerID = containerID;
		int i = 0;
		String last_line = "";
		for(String line : lines)
		{
			if(i%2==1)
			{
				String[] data = (last_line+" "+line.trim()).split(" ");
				List<String> work_line = IntStream.range(0,data.length).filter(a->data[a].trim().length()>0).mapToObj(a->data[a].trim()).collect(Collectors.toList());
				this.values.put(Integer.parseInt(work_line.get(0)),IntStream.range(0,work_line.size()).filter(a->a>0).mapToObj(a->work_line.get(a)).collect(Collectors.toList()));
			}
			last_line = line.trim();
			i++;
		}
		switch (this.containerID) {
		case 2411:
			if(!this.values.entrySet().stream().allMatch(e -> e.getValue().size()==6))
				throw new Exception("File corrupt has each record require 1+6=7 fields, Please check your data");
			break;
		case 2412:
			if(!this.values.entrySet().stream().allMatch(e -> e.getValue().size()>5))
				throw new Exception("File corrupt has each record require more than 6 fields, Please check your data");
			break;
		default:
			System.out.println("Sorry, this kind of container is not supported yet !");
			break;
		}
	}
	@Override
	public String toString() {
		return this.values.entrySet().stream().map(entry -> entry.getKey().toString()+";"+(String) entry.getValue().stream().map(Object::toString).collect(Collectors.joining(";"))).collect(Collectors.joining("\n"));
	}
	public HashMap<Integer,List<String>> getValues() {
		return this.values;
	}
	public String toUNV()
	{
		String res="-1\n";
		
		switch (this.containerID) {
		case 2411:
			res += "\t2411\n"+this.values.entrySet().stream().map(e -> "\t"+e.getKey()+"\t"+e.getValue().get(0)+"\t"+e.getValue().get(1)+"\t"+e.getValue().get(2)+"\n\t"+e.getValue().get(3)+"\t"+e.getValue().get(4)+"\t"+e.getValue().get(5)).collect(Collectors.joining("\n"));
			break;
		case 2412:
			res += "\t2412\n"+this.values.entrySet().stream().map(e -> "\t"+e.getKey()+"\t"+e.getValue().get(0)+"\t"+e.getValue().get(1)+"\t"+e.getValue().get(2)+"\t"+e.getValue().get(3)+"\t"+e.getValue().get(4)+"\n\t"+e.getValue().get(5)+"\t"+e.getValue().get(6)+"\t"+e.getValue().get(7)+"\t"+e.getValue().get(8)).collect(Collectors.joining("\n"));
			break;
		default:
			System.out.println("Sorry, this kind of container is not supported yet !");
			break;
		}
		
		return res+"\n-1\n";
	}
}