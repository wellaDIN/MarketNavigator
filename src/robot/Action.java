package robot;

public enum Action {
	East(0),
	North(1),
	West(2),
	South(3);
	
	private int numericValue;
	
	Action(int numericValue){
		this.numericValue=numericValue;
	}
	
	public int getNumber() {
		return numericValue;
	}

	public Action opposite() {
		switch(this.numericValue){
			case 0:
				return West;
			case 1:
				return South;
			case 2:
				return East;
			case 3:
				return North;
			default:
				return null;
		}
	}
}
