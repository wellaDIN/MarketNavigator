package robot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import maze.Coordinate;
import maze.Path;

public class Ant {

	private Route route;
	private Coordinate currentPosition;
	private List<Path> pathsTable;

	public Ant(Coordinate startingPoint, Coordinate endingPoint, List<Path> paths) {
		route = new Route(startingPoint, endingPoint);
		currentPosition = startingPoint;
		this.pathsTable=paths;
	}

	public void colonize() {
		while(!currentPosition.equalsTo(route.getObjectivePoint())){
			List<Path> possiblePaths = getPossiblePaths();
			//TODO Understand if this can be done
			if(possiblePaths.size()>1){
				possiblePaths = avoid_last_step_repetition(possiblePaths);
			}
			double[] probabilities = getProbabilities(possiblePaths);
			Path decision = possiblePaths.get(choose_path(probabilities));
			route.addAction(decision.getAction(currentPosition));
			currentPosition = decision.otherPoint(currentPosition);
		}
		route.print();
	}

	private List<Path> avoid_last_step_repetition(List<Path> possiblePaths) {
		Action toAvoid = route.getLastAction();
		if(toAvoid==null){
			return possiblePaths;
		}
		toAvoid = toAvoid.opposite();
		for(int i=0;i<possiblePaths.size();i++){
			if(possiblePaths.get(i).getAction(currentPosition).equals(toAvoid)){
				possiblePaths.remove(i);
				return possiblePaths;
			}
		}
		return null;
	}

	private int choose_path(double[] probabilities) {
		Random generator = new Random();
		double p = generator.nextDouble();
		for(int i=1;i<probabilities.length;i++){
			probabilities[i]+=probabilities[i-1];
		}
		for(int i=0;i<probabilities.length;i++){
			if(p<probabilities[i]){
				return i;
			}
		}
		return -1;
	}

	//TODO Apply correct formula
	private double[] getProbabilities(List<Path> possiblePaths) {
		double[] probabilities = new double[possiblePaths.size()];
		double sum=0;
		for(int i=0;i<possiblePaths.size();i++){
			probabilities[i] = possiblePaths.get(i).getPheromone();
			sum+=probabilities[i];
		}
		for(int i=0;i<probabilities.length;i++){
			probabilities[i]/=sum;
		}
		return probabilities;
	}

	private List<Path> getPossiblePaths() {
		List<Path> p = new ArrayList<Path>();
		for(int i=0; i<pathsTable.size();i++){
			Path path = pathsTable.get(i); 
			if(path.contains(currentPosition)){
				p.add(path);
			}
		}
		return p;
	}

	public int getRouteLength() {
		return this.route.getLength();
	}

	public Route getRoute() {
		return this.route;
	}
	
}
