package parseCSV;
//import java.util.ArrayList;


public class Operation {
	String column1;
	String column2;
	OperationType operator;
	
	public Operation(String column1, String column2, OperationType operator) { 
		//on assume que c'est C1 OPERATOR C2
		this.column1 = column1;
		this.column2 = column2;
		this.operator = operator;
	}
	
	public double operationResult(Line l){ // ArrayList<String> c ne me semble pas utile puisque qu'on a column1 & column2
		// retourne le résultat de operateur appliquée à column1 et column2;
		double res=0;
		if(OperationType.addition.equals(this.operator))
		{
			res = l.getDoubleValue(this.column1) + l.getDoubleValue(this.column2);
		}
		else if(OperationType.soustraction.equals(this.operator))
		{
			res = l.getDoubleValue(this.column1) - l.getDoubleValue(this.column2);
		}
		else if(OperationType.multiplication.equals(this.operator))
		{
			res = l.getDoubleValue(this.column1) * l.getDoubleValue(this.column2);
		}
		else if(OperationType.division.equals(this.operator))
		{
			res = l.getDoubleValue(this.column1) / l.getDoubleValue(this.column2);
		}
		else if(OperationType.emissionCO2.equals(this.operator))
		{
			res = 0.0769*l.getDoubleValue(this.column1) -8.2006*l.getDoubleValue(this.column2)+358.45;
		}
		return res;
	}
}
