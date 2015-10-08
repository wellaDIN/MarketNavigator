package maze;

import robot.Action;

public class Path {

	
	private Coordinate firstPoint;
	private Coordinate lastPoint;
	private int pheromone;
	
	public Path(int i, int j, Action action) {
		firstPoint = new Coordinate(i,j);
		lastPoint = firstPoint.getNeighbor(action);
		this.pheromone = 1;
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
	
}
