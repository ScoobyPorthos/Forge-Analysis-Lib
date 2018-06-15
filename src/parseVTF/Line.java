package parseVTF;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class Line {
	
	private int number;
	private ArrayList<String> columns = new ArrayList<>();
	private HashMap<Integer,Cell> data = new HashMap<>();
	
	public Line(int num,String line, ArrayList<String> columns)
	{
		this.number = num;
		this.columns = columns;
		List<String> values = Arrays.asList(line.split("\t")); 
		this.parseLine(values);
	}
	
	public Line(int num,String line, ArrayList<String> columns,ArrayList<Integer> selection)
	{
		this.number = num;
		/*List<String> fields = IntStream.range(0, columns.size())
									.filter(i -> selection.contains(i+1))
									.mapToObj(i -> columns.get(i))
									.collect(Collectors.toList());
		this.columns.addAll(fields);*/
		this.columns = columns;
		List<String> values = IntStream.range(0,line.trim().split("\t").length)
				.filter(i -> selection.contains(i+1) && !line.trim().split("\t")[i].isEmpty())
				.mapToObj(i -> line.split("\t")[i])
				.collect(Collectors.toList());
		this.parseLine(values);
	}
	
	public Line(int num,ArrayList<String> columns,HashMap<Integer,Cell> data)
	{
		this.number = num;
		this.columns = columns;
		this.data = data;
	}
	
	private void parseLine(List<String> values)
	{	
		int a=0;
		for(String val : values)
		{
			val = val.trim();
			try
			{
				this.data.put(a,new Cell(Double.parseDouble(val),columns.get(a),this.number));
				a++;
			}
			catch(NumberFormatException e)
			{
				this.data.put(a,new Cell(val,columns.get(a),this.number));
				a++;
			}
		}
	}
	
	public double getDoubleValue(String column){
		return this.data.get(this.columns.indexOf(column)).getdoubleValue();
	}
	
	public String getStringValue(String column){
		return this.data.get(this.columns.indexOf(column)).getStringValue();
	}
	
	public Cell getCell(String column){
		return this.data.get(this.columns.indexOf(column));
		}
	
	public Line extract(ArrayList<String> column) 
	{
			HashMap<Integer, Cell> res = new HashMap<>();
			this.data.entrySet().stream()
					.filter(map -> column.contains(map.getValue().getColumnName()))
					.forEach(map -> res.put(map.getKey(), map.getValue()));
			ArrayList<String> new_columns = new ArrayList<>();
			new_columns.addAll(this.columns.stream().filter(p -> column.contains(p)).collect(Collectors.toList()));
			return new Line(this.number,new_columns,res) ;
	}
	public Line extract() 
	{
			HashMap<Integer, Cell> res = new HashMap<>();
			this.data.entrySet().stream()
					.forEach(map -> res.put(map.getKey(), map.getValue()));
			return new Line(this.number,this.columns,res) ;
	}
	public void addCell(HashMap<Integer,Cell> newCell)
	{
		newCell.entrySet().stream().forEach(map -> this.data.put(this.data.size()+map.getKey(), map.getValue()));
	}
	public String getCSVLine() {
		
		String res = this.data.values().stream().map(Object::toString).collect(Collectors.joining(", "));
		return res;
	}
	public HashMap<Integer, Cell> getData() {
		return data;
	}
	public void display()
	{
		System.out.print("|\t");
		for(String head: this.columns)
			System.out.print(head+"\t|\t");
		System.out.print("\n|\t");
		for(Cell val : this.data.values())
			System.out.print(val.toString()+"\t|\t");
		System.out.print("\n");
	}
	
}
