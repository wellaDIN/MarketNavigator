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
		this.pathsTable=paths;
		startingPoint = findInPathsTable(startingPoint);
		endingPoint = findInPathsTable(endingPoint);
		route = new Route(startingPoint, endingPoint);
		currentPosition = startingPoint;
		visitedPositions = new ArrayList<Coordinate>();
		visitedPositions.add(currentPosition);
		if(!currentPosition.hasBeenMarked()){
			analyze(currentPosition);
			System.out.println(currentPosition.isAnExit());
		}
		deadEndMode = false;
	}

	private Coordinate findInPathsTable(Coordinate point) {
		for(int i=0; i<pathsTable.size();i++){
			if(pathsTable.get(i).firstPoint.equalsTo(point)){
				return pathsTable.get(i).firstPoint;
			}
			if(pathsTable.get(i).lastPoint.equalsTo(point)){
				return pathsTable.get(i).lastPoint;
			}
		}
		return null;
	}

	private void analyze(Coordinate point) {
		point.setAsMarked(true);
		List<Path> paths = getPossiblePaths(point);
		if(point.equalsTo(route.getObjectivePoint()) || point.equalsTo(route.getStartingPoint())){
			if(paths.size()==1){
				if(getPossiblePaths(paths.get(0).otherPoint(point)).size()>2){
					point.setAnExit(true);
					return;
				}
			}
		}
		if(paths.size()!=2){
			return;
		}
		Coordinate p1 = paths.get(0).otherPoint(point);
		Coordinate p2 = paths.get(1).otherPoint(point);
		int s1 = getPossiblePaths(p1).size();
		int s2 = getPossiblePaths(p2).size();
		if(s1>2 ^ s2>2){
			point.setAnExit(true);
			return;
		}
		if(s1>2 && s2>2){
			if(p1.getX()==p2.getX() || p1.getY()==p2.getY()){
				point.setAnExit(true);
				return;
			} else{
				int x;
				int y;
				if(p1.getX()==point.getX()){
					x=p1.getY();
					y=p2.getX();
				} else {
					x=p1.getX();
					y=p2.getY();
				}
				if(!thereIs(pathsTable, x,y)){
					point.setAnExit(true);
					return;
				}
			}
			
		}
	}
	
	private boolean thereIs(List<Path> p, int x, int y) {
		for(int i=0; i<p.size();i++){
			Coordinate c1 = p.get(i).firstPoint;
			Coordinate c2 = p.get(i).lastPoint;
			if((c1.getX()==x && c1.getY()==y) || (c2.getX()==x && c2.getY()==y)){
				return true;
			}
		}
		return false;
	}

	private List<Path> getPossiblePaths(Coordinate point){
		List<Path> p = new ArrayList<Path>();
		for(int i=0; i<pathsTable.size();i++){
			Path path = pathsTable.get(i); 
			if(path.contains(point)){
				p.add(path);
			}
		}
		return p;
	}

	public void colonize() {
		while(!currentPosition.equalsTo(route.getObjectivePoint())){
			System.out.println(currentPosition.print());
			//TODO Deal with obstacles
			if(currentPosition.isAnExit()){
				Coordinate destination = findExit(currentPosition);
				if(destination!=null){
					int x_distance = destination.getX()-currentPosition.getX();
					int y_distance = destination.getY()-currentPosition.getY();
					boolean x_positive=true;
					boolean y_positive=true;
					if(x_distance<0){
						x_distance = -x_distance;
						x_positive=false;
					}
					if(y_distance<0){
						y_distance = -y_distance;
						y_positive=false;
					}
					while(!currentPosition.equalsTo(destination)){
						if(x_distance>y_distance){
							while(x_distance>0){
								List<Path> p = getPossiblePaths(currentPosition);
								Path decision = null;
								if(x_positive){
									decision = findByPointAndOppositeAction(p, currentPosition, Action.North);
								} else {
									decision = findByPointAndOppositeAction(p, currentPosition, Action.South);
								}
								if(decision==null){
									break;
								}
								route.addAction(decision.getAction(currentPosition));
								currentPosition = decision.otherPoint(currentPosition);
								System.out.println(currentPosition.print());
								x_distance--;
							}
							while(y_distance>0){
								List<Path> p = getPossiblePaths(currentPosition);
								Path decision = null;
								if(y_positive){
									decision = findByPointAndOppositeAction(p, currentPosition, Action.West);
								} else {
									decision = findByPointAndOppositeAction(p, currentPosition, Action.East);
								}	
								if(decision==null){
									break;
								}
								route.addAction(decision.getAction(currentPosition));
								currentPosition = decision.otherPoint(currentPosition);
								System.out.println(currentPosition.print());
								y_distance--;
							}
						} else {
							while(y_distance>0){
								List<Path> p = getPossiblePaths(currentPosition);
								Path decision = null;
								if(y_positive){
									decision = findByPointAndOppositeAction(p, currentPosition, Action.West);
								} else {
									decision = findByPointAndOppositeAction(p, currentPosition, Action.East);
								}								
								if(decision==null){
									break;
								}
								route.addAction(decision.getAction(currentPosition));
								currentPosition = decision.otherPoint(currentPosition);
								System.out.println(currentPosition.print());
								y_distance--;
							}
							while(x_distance>0){
								List<Path> p = getPossiblePaths(currentPosition);
								Path decision = null;
								if(x_positive){
									decision = findByPointAndOppositeAction(p, currentPosition, Action.North);
								} else {
									decision = findByPointAndOppositeAction(p, currentPosition, Action.South);
								}
								route.addAction(decision.getAction(currentPosition));
								currentPosition = decision.otherPoint(currentPosition);
								System.out.println(currentPosition.print());
								x_distance--;
							}
						}
						if(x_distance==0 && y_distance==0){
							break;
						}
					}
				}
			}
			if(currentPosition.equalsTo(route.getObjectivePoint())){
				break;
			}
			List<Path> possiblePaths = getPossiblePaths();
			List<Path> pathsAvoidingLastStep = avoid_last_step_repetition((ArrayList<Path>) possiblePaths);
			if(deadEndMode){
				if(pathsAvoidingLastStep.size()==1){
					Path decision = pathsAvoidingLastStep.get(0);
					decision.setAsToBeAvoided();
					route.addAction(decision.getAction(currentPosition));
					currentPosition = decision.otherPoint(currentPosition);
					if(!currentPosition.hasBeenMarked()){
						analyze(currentPosition);
					}
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
			if(!currentPosition.hasBeenMarked()){
				analyze(currentPosition);
			}
			if(visitedPositions.contains(currentPosition)){
				mark_loop(currentPosition);
			} else {
			visitedPositions.add(currentPosition);
			}
		}
	}

	private Coordinate findExit(Coordinate c) {
		List<Coordinate> exits = new ArrayList<Coordinate>();
		for(int i=0;i<pathsTable.size();i++){
			Coordinate c1 = pathsTable.get(i).firstPoint;
			Coordinate c2 = pathsTable.get(i).lastPoint;
			if(c1.isAnExit() && !pathsTable.contains(c1)){
				exits.add(c1);
			}
			if(c2.isAnExit() && !pathsTable.contains(c2)){
				exits.add(c2);
			}
		}
		for(int i=0; i<exits.size();i++){
			//TODO Change in find the best exit to go
			if(exits.get(i).getX()==7 && exits.get(i).getY()==7){
				return exits.get(i);
			}
		}
		return null;
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
