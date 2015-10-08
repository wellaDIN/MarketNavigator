package maze;

import robot.Action;

public class Path {

	
	private Coordinate firstPoint;
	private Coordinate lastPoint;
	private Action firstToLastAction;
	private double pheromone;
	
	public void pheromoneEvaporation(double constant){
		this.pheromone*=(1-constant);
		return;
	}
	
	public double getPheromone() {
		return pheromone;
	}

	public Path(int i, int j, Action action) {
		firstPoint = new Coordinate(i,j);
		firstToLastAction = action;
		lastPoint = firstPoint.getNeighbor(action);
		this.pheromone = 1;
	}

	public Action getAction (Coordinate point){
		if(point.equalsTo(firstPoint)){
			return firstToLastAction;
		}
		if(point.equalsTo(lastPoint)){
			return firstToLastAction.opposite();
		}
		return null;
	}
	
	public void print() {
		System.out.println("Path from " + firstPoint.print() + " to " + lastPoint.print() + " contains " + this.pheromone + " pheronome.");
	}

	public boolean equalsTo(Path newPath) {
		if(this.firstPoint.equalsTo(newPath.firstPoint) && this.lastPoint.equalsTo(newPath.lastPoint)){
			return true;
		}
		if(this.firstPoint.equalsTo(newPath.lastPoint) && this.lastPoint.equalsTo(newPath.firstPoint)){
			return true;
		}
		return false;
	}
	
	public boolean contains(Coordinate point){
		if(this.firstPoint.equalsTo(point) || this.lastPoint.equalsTo(point)){
			return true;
		} else {
			return false;
		}
	}

	public Coordinate otherPoint(Coordinate point) {
		if(firstPoint.equalsTo(point)){
			return lastPoint;
		}
		if(lastPoint.equalsTo(point)){
			return firstPoint;
		}
		return null;
	}

	public boolean matchesWith(Coordinate point, Action action) {
		if(point.equalsTo(firstPoint) && action.equals(firstToLastAction)){
			return true;
		}
		if(point.equalsTo(lastPoint) && action.equals(firstToLastAction.opposite())){
			return true;
		}
		return false;
	}

	public void increasePheromone(int i) {
		this.pheromone+=i;
	}
}
