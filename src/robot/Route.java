package robot;

import java.util.ArrayList;
import java.util.List;

import maze.Coordinate;

public class Route {
	
	private final Coordinate startingPoint;
	private final Coordinate objectivePoint;
	private List<Action> actions;
	
	public Route(Coordinate startingPoint, Coordinate endingPoint) {
		this.startingPoint=startingPoint;
		this.objectivePoint=endingPoint;
		actions = new ArrayList<Action>();
	}

	public void addAction(Action a){
		actions.add(a);
		return;
	}

	public int getLength(){
		return actions.size();
	}

	public Coordinate getObjectivePoint() {
		return objectivePoint;
	}
	
	public Action getLastAction(){
		if(actions.size()==0){
			return null;
		}
		return this.actions.get(actions.size()-1);
	}

	public List<Action> getActionList(){
		return this.actions;
	}
	
	public void print() {
		System.out.print("STARTING POINT -> ");
		for(int i=0;i<actions.size();i++){
			System.out.print(actions.get(i) + " -> ");
		}
		System.out.println("GOAL");
		System.out.println("Route length: " + this.getLength());
	}

	public Coordinate getStartingPoint() {
		return this.startingPoint;
	}


	public void abort() {
		this.actions.clear();
	}
}
