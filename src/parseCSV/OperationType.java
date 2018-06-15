package parseCSV;

public enum OperationType {
	addition("+"), 
	soustraction("-"), 
	multiplication("*"), 
	division("/"), 
	emissionCO2("[CO2]");
	
	private String signe;
	
	private OperationType( String signe) {
		this.signe = signe;
	}
	
	public String getSigne() {
		return signe;
	}
	
}
