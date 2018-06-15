package parseVTF;


public class Cell {
	private String columnName;
	private int lineNumber;
	private double dValue;
	private String sValue = null;
	
	public Cell(String value, String column, int line) {
		this.sValue = value.trim();
		this.columnName = column;
		this.lineNumber = line;
	}

	public Cell(double value, String column, int line) {
		this.dValue = value;
		this.columnName = column;
		this.lineNumber = line;
	}
	public double getdoubleValue() {
			return this.dValue;
	}
	public String getStringValue() {
		return this.sValue;
	}
	public int getLineNumber() {
		return this.lineNumber;
	}
	public void setcolomnName(String name)
	{
		this.columnName = name;
	}
	public String getColumnName() {
		return columnName;
	}
	public String toString(){
		if(this.sValue==null)
			return Double.toString(this.dValue);
		else
			return this.sValue;
	}

}
