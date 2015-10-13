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
	private List<Coordinate> visitedPositions;
	private boolean deadEndMode;

	public Ant(Coordinate startingPoint, Coordinate endingPoint, List<Path> paths) {
		route = new Route(startingPoint, endingPoint);
		currentPosition = startingPoint;
		visitedPositions = new ArrayList<Coordinate>();
		visitedPositions.add(currentPosition);
		this.pathsTable=paths;
		deadEndMode = false;
	}

	public void colonize() {
		while(!currentPosition.equalsTo(route.getObjectivePoint())){
			List<Path> possiblePaths = getPossiblePaths();
			List<Path> pathsAvoidingLastStep = avoid_last_step_repetition((ArrayList<Path>) possiblePaths);
			if(deadEndMode){
				if(pathsAvoidingLastStep.size()==1){
					Path decision = pathsAvoidingLastStep.get(0);
					decision.setAsToBeAvoided();
					route.addAction(decision.getAction(currentPosition));
					currentPosition = decision.otherPoint(currentPosition);
					continue;
				}
				if(pathsAvoidingLastStep.size()>1){
					deadEndMode=false;
				}
			}
			if(pathsAvoidingLastStep.size()==0){
				Path decision = possiblePaths.get(0);
				decision.setAsToBeAvoided();
				deadEndMode = true;
				route.addAction(decision.getAction(currentPosition));
				currentPosition = decision.otherPoint(currentPosition);
				continue;
			}
			possiblePaths = pathsAvoidingLastStep;
			List<Path> pathsAvoidingDead = avoid_dead(possiblePaths);
			if(pathsAvoidingDead.size()!=0){
				possiblePaths = pathsAvoidingDead;
			}
			List<Path> pathsAvoidingLoop = avoid_loop(possiblePaths);
			if(pathsAvoidingLoop.size()!=0){
				possiblePaths = pathsAvoidingLoop;
			}
			double[] probabilities = getProbabilities(possiblePaths);
			Path decision = possiblePaths.get(choose_path(probabilities));
			route.addAction(decision.getAction(currentPosition));
			currentPosition = decision.otherPoint(currentPosition);
			if(visitedPositions.contains(currentPosition)){
				mark_loop(currentPosition);
			} else {
			visitedPositions.add(currentPosition);
			}
		}
	}

	private List<Path> avoid_loop(List<Path> possiblePaths) {
		List<Path> paths = new ArrayList<Path>();
		for(int i=0;i<possiblePaths.size();i++){
			if(!possiblePaths.get(i).isPartOfALoop()){
				paths.add(possiblePaths.get(i));
			}
		}
		return paths;
	}

	private void mark_loop(Coordinate end) {
		List<Action> actions = this.route.getActionList();
		Coordinate currentPoint = end;
		for(int i=actions.size()-1;i>=0;i--){
			Path p = findByPointAndOppositeAction(pathsTable,currentPoint,actions.get(i));
			currentPoint = p.otherPoint(currentPoint);
			p.markAsInALoop();
			if(p.contains(end)){
				return;
			}
		}
	}
	
	private static Path findByPointAndOppositeAction(List<Path> paths, Coordinate point, Action action) {
		for(int i=0;i<paths.size();i++){
			if(paths.get(i).matchesWith(point,action.opposite())){
				return paths.get(i);
			}
		}
		return null;
	}

	private List<Path> avoid_dead(List<Path> possiblePaths) {
		List<Path> paths = new ArrayList<Path>();
		for(int i=0;i<possiblePaths.size();i++){
			if(!possiblePaths.get(i).isToAvoid()){
				paths.add(possiblePaths.get(i));
			}
		}
		return paths;
	}

	private List<Path> avoid_last_step_repetition(ArrayList<Path> p) {
		@SuppressWarnings("unchecked")
		ArrayList<Path> possiblePaths = (ArrayList<Path>) p.clone();
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
		System.out.print("[ ");
		for(int i=0;i<probabilities.length;i++){
			System.out.print(probabilities[i] + " ");
		}
		System.out.println("]");
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
