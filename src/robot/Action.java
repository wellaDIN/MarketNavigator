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
}
