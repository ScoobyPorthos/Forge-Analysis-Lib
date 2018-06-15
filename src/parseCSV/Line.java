package parseCSV;
import java.util.ArrayList;


public class Line {
	
	private int number;
	private ArrayList<String> columns;
	private ArrayList<Cell> data = new ArrayList<>();
	
	public Line(int num,String line, ArrayList<String> columns) {// creation d'une ligne
		int i=0;
		this.number = num;
		this.columns = columns;
		for(String val : line.split(","))
		{
			try
			{
				this.data.add(new Cell(Double.parseDouble(val),columns.get(i),this.number));
				i++;
			}
			catch(NumberFormatException e)
			{
				this.data.add(new Cell(val,columns.get(i),this.number));
				i++;
			}
		}
	}
	
	public double getDoubleValue(String column){// retourne la valeur pour la colonne demandée
		return this.data.get(this.columns.indexOf(column)).getdoubleValue();
	}
	
	public String getStringValue(String column){// retourne la valeur pour la colonne demandée
		return this.data.get(this.columns.indexOf(column)).getStringValue();
	}
	
	public Cell getCell(String column){// retourne la cellule pour la colonne demandée
		return this.data.get(this.columns.indexOf(column));
		}
	
	public String getCSVLine() {// retourne la ligne au format csv
		String res = this.data.get(0).toString()+",";
		for(int i=1;i<this.data.size();i++)
		{
			res = res.concat(this.data.get(i).toString());
			res = res.concat(",");
		}
		
		return res+this.data.get(this.data.size()-1).toString()+"\n";
	}

	public void addValue(String name, Operation op) // de même ArrayList<String> subjects ne me semble pas utile
	{
		// ajoute une nouvelle valeur (colonne name) résultat de Operation
		this.data.add(new Cell(op.operationResult(this),name,this.number));
		
	}

	/*public void swap(String c1, String c2) {
		// permutte les valeurs de deux lignes
		Cell swap_cell1 = this.getCell(c1);
		Cell swap_cell2 = this.getCell(c2);
		
		int index1 = this.data.indexOf(swap_cell1);
		int index2 = this.data.indexOf(swap_cell2);
		
		this.data.remove(index1);
		this.data.remove(index2);
		
		swap_cell1.setcolomnName(c2);
		swap_cell2.setcolomnName(c1);
		
		this.data.add(index2,swap_cell1);
		this.data.add(index1,swap_cell2);
		
		index1 = this.columns.indexOf(c1);
		index2 = this.columns.indexOf(c2);
		
		this.columns.remove(index1);
		this.columns.remove(index2);
		
		this.columns.add(index1,c2);
		this.columns.add(index2,c1);
	}*/
	public void display()
	{
		System.out.print("|\t");
		for(Cell val : this.data)
			System.out.print(val.toString()+"\t|\t");
		System.out.print("\n");
	}
	
}
