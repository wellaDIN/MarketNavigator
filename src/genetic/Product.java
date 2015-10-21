package genetic;

import maze.Coordinate;

public class Product {

	private int ID;
	private Coordinate coordinate;
	
	public int getID() {
		return ID;
	}



	public void setID(int iD) {
		ID = iD;
	}



	public Coordinate getCoordinate() {
		return coordinate;
	}



	public void setCoordinate(Coordinate coordinate) {
		this.coordinate = coordinate;
	}



	public Product(int iD, Coordinate coordinate) {
		super();
		ID = iD;
		this.coordinate = coordinate;
	}
	
}
