package maze;

import robot.Action;


public class Coordinate {

	private int x;
	private int y;

	public Coordinate(int x, int y) {
		this.x=x;
		this.y=y;
	}
	
	public int getX() {
		return x;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setY(int y) {
		this.y = y;
	}

	public String print() {
		return ("(" + x + ", " + y + ")");
	}

	public boolean isEqual(int i, int j) {
		if(this.x==i && this.y==j){
			return true;
		} else {
			return false;
		}
	}

	public Coordinate getNeighbor(Action action) {
		switch(action){
			case South :
				return(new Coordinate(this.x+1,this.y));
			case North :
				return(new Coordinate(this.x-1,this.y));
			case East :
				return(new Coordinate(this.x,this.y+1));
			case West :
				return(new Coordinate(this.x,this.y-1));
			default:
				return null;
		}
	}

	public boolean equalsTo(Coordinate point) {
		if(this.x==point.x && this.y==point.y){
			return true;
		} else {
			return false;
		}
	}
	
	
	
}
