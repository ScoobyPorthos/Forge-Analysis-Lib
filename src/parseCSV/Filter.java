package parseCSV;

import java.util.HashMap;
import java.util.Set;


public class Filter {
	private HashMap <String, Double> mySelection;// pair nom de colonne et valeur attendue
	public Filter(HashMap <String, Double> mySelection) {
		this.mySelection = mySelection;
	}
	
	@SuppressWarnings("unlikely-arg-type") //TODO fix the type issue 
	public boolean valid(Line l){
		// retourne vraie si la ligne contient la valeur à la colonne demandée
		boolean res = true;
		
		Set<String> keys = this.mySelection.keySet();		
		
		for(String key :keys)
		{
			
			String value =((l.getStringValue(key)!=null)? l.getStringValue(key):Double.toString(l.getDoubleValue(key)));
			if(this.mySelection.values().contains(value) && res)
				res=true;
			else
				res=false;
		}
		if(res)
		{
			try {
				System.out.println("oui");
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return res;
	}
		
	
}