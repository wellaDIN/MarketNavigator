package maze;

public class OpenSpace {

	public Coordinate entry;
	public Coordinate exit;
	
	public OpenSpace(Coordinate entry, Coordinate exit) {
		this.entry = entry;
		this.exit = exit;
	}
	
	public void print(){
		System.out.println(entry.print() + " and " + exit.print() + " are part of the same open space.");
	}
	
	public boolean equals(Coordinate entry2, Coordinate exit2){
		Boolean b1 = (entry.equalsTo(entry2) && exit.equalsTo(exit2));
		Boolean b2 = (entry.equalsTo(exit2) && exit.equalsTo(entry2));
		if(b1 || b2){
			return true;
		} else {
			return false;
		}
	}
	
}
