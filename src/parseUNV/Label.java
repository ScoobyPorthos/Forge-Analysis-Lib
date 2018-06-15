package parseUNV;

public enum Label {
	Nodes(15),Coordinates(2411),Elements(2412),Field(2414);
	
	private String name;
	
	Label(int label)
	{
		
		switch(label)
		{
		case 15:
			this.name = "Nodes";
			break;
		case 2411 :
			this.name = "Coordinates";
			break;
		case 781 :
			this.name = "Coordinates";
			break;
		case 2412 : 
			this.name = "Elements";
			break;
		case 780 :
			this.name = "Elements";
			break;
		case 55 :
			this.name = "Field_nodes";
			break;
		case 56 :
			this.name = "Field_elements";
			break;
		case 2414 :
			this.name = "Field";
			break;
		default:
			this.name = "";
			break;
		}
	}
	@Override
	public String toString() {
		return this.name;
	}
}
