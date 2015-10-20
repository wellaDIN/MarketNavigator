package robot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import maze.Coordinate;
import maze.OpenSpace;
import maze.Path;

public class Ant {

	private static final int BONUS_PERCENTAGE_PER_NON_LOOP = 30;
	private static final int BONUS_PER_OPEN_SPACE = 40;
	private Route route;
	private Coordinate currentPosition;
	private List<Path> pathsTable;
	private List<OpenSpace> openSpaces;
	private List<Coordinate> visitedPositions;
	private Coordinate lastExit = null;
	private boolean deadEndMode;
	private Coordinate previousExit = null;
	private boolean openSpaceMode = false;
	private Coordinate openSpaceDestination = null;
	
	public Ant(Coordinate startingPoint, Coordinate endingPoint, List<Path> paths, List<OpenSpace> openSpaces) {
		this.pathsTable=paths;
		this.openSpaces=openSpaces;
		startingPoint = findInPathsTable(startingPoint);
		endingPoint = findInPathsTable(endingPoint);
		route = new Route(startingPoint, endingPoint);
		currentPosition = startingPoint;
		visitedPositions = new ArrayList<Coordinate>();
		visitedPositions.add(currentPosition);
		if(!currentPosition.hasBeenMarked()){
			analyze(currentPosition);
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

	//TODO Improve this function
	private void analyze(Coordinate point) {
		point.setAsMarked(true);
		List<Path> paths = getPossiblePaths(point);
		if(point.equalsTo(route.getObjectivePoint())){
			if(paths.size()>=2){
				point.setAnExit(true);
				return;
			}
		}
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
		if(s1>2 || s2>2){
			if(p1.getX()==p2.getX() || p1.getY()==p2.getY()){
				if((s1>3 && s2>=2) || (s1>=2 && s2>3)){
					point.setAnExit(true);
					return;
				}
			} else{
				/*
				 * Here we should deal with angles of open spaces..
				 * 
				 * int x;
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
				}*/
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
			//System.out.println(currentPosition.print());
			if(currentPosition.isAnExit()){
				if(this.lastExit==null){
					lastExit = currentPosition;
				} else {
					if(!lastExit.equalsTo(currentPosition)){
						previousExit = lastExit;
						if(!alreadyAnOpenSpace(lastExit, currentPosition)){
							this.openSpaces.add(new OpenSpace(lastExit, currentPosition));
						}
						lastExit=currentPosition;
					}
				}
				/*
				if(openSpaceMode){
					Coordinate temp = currentPosition;
					List<Path> pathsAvoidingLastStep = avoid_last_step_repetition((ArrayList<Path>) getPossiblePaths());
					Path firstPath = pathsAvoidingLastStep.get(0);
					Action action = firstPath.getAction(currentPosition);
					route.addAction(action);
					currentPosition = firstPath.otherPoint(currentPosition);
					if(!currentPosition.hasBeenMarked()){
						analyze(currentPosition);
					}
					if(!navigate_in_white_space(firstPath, action)){
						break;
					}
					previousExit = temp;
				} else {
					break;
				}*/
			}
			/*
			if(currentPosition.equalsTo(route.getObjectivePoint())){
				break;
			}*/
			List<Path> possiblePaths = getPossiblePaths();
			List<Path> pathsAvoidingLastStep = avoid_last_step_repetition((ArrayList<Path>) possiblePaths);
			if(deadEndMode){
				if(pathsAvoidingLastStep.size()==1){
					Path decision = pathsAvoidingLastStep.get(0);
					decision.setAsToBeAvoided();
					route.addAction(decision.getAction(currentPosition));
					currentPosition = decision.otherPoint(currentPosition);
					//System.out.println(currentPosition.print());
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
				//System.out.println(currentPosition.print());
				continue;
			}
			possiblePaths = pathsAvoidingLastStep;
			List<Path> pathsAvoidingDead = avoid_dead(possiblePaths);
			if(pathsAvoidingDead.size()!=0){
				possiblePaths = pathsAvoidingDead;
			}
			double[] probabilities = getProbabilities(possiblePaths, avoid_loop(possiblePaths));
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
		//System.out.println("Una formica ha raggiunto la fine del labirinto!");
		/*
		if(currentPosition.isAnExit()){
			if(this.lastExit==null){
				lastExit = currentPosition;
			} else {
				if(!lastExit.equalsTo(currentPosition)){
					if(!alreadyAnOpenSpace(lastExit, currentPosition)){
						this.openSpaces.add(new OpenSpace(lastExit, currentPosition));
					}
					lastExit=currentPosition;
				}
			}
		}*/
	}

	private boolean alreadyAnOpenSpace(Coordinate a, Coordinate b) {
		for(int i=0; i<this.openSpaces.size(); i++){
			if (openSpaces.get(i).equals(a,b)){
				return true;
			}
		}
		return false;
	}

	private boolean navigate_in_white_space(Path toAvoid, Action action) {
		//TODO Use pheromone in the probability for finding the exit
		boolean horizontal = true;
		switch(action){
			case North:
				horizontal=false;
			case South:
				horizontal=false;
			default:
				break;
		}
		Coordinate destination = findExit(toAvoid.otherPoint(currentPosition));
		if(destination==null){
			return false;
		}
		if(destination!=null){
			int x_distance = destination.getX()-currentPosition.getX();
			int y_distance = destination.getY()-currentPosition.getY();
			boolean x_positive=true;
			boolean y_positive=true;
			if(x_distance<0){
				//TODO Check if it works;
				x_distance = -x_distance;
				x_positive=false;
			}
			if(y_distance<0){
				y_distance = -y_distance;
				y_positive=false;
			}
			while(!currentPosition.equalsTo(destination)){
				if(horizontal){
					int counter=0;
					while(x_distance!=0){
						counter++;
						int x_update = try_to_move(x_distance,x_positive,Action.North,Action.South, toAvoid);
						if(x_update==-1){
							x_distance = destination.getX()-currentPosition.getX();
							if(!avoid_obstacle(false, y_positive, toAvoid)){
								avoid_obstacle(false,!y_positive,toAvoid);
							}
							y_distance = destination.getY()-currentPosition.getY();
						} else {
							x_distance = x_update;
						}
						if(counter>5){
							break;
						}
					}
					counter=0;
					while(y_distance!=0){
						counter++;
						int y_update = try_to_move(y_distance,y_positive,Action.West,Action.East, toAvoid);
						if(y_update==-1){
							y_distance = destination.getY()-currentPosition.getY();
							if(!avoid_obstacle(false, x_positive, toAvoid)){
								avoid_obstacle(false,!x_positive,toAvoid);
							}
							x_distance = destination.getX()-currentPosition.getX();
						} else {
							y_distance = y_update;
						}
						if(counter>5){
							break;
						}
					}
				} else {
					int counter=0;
					while(y_distance!=0){
						counter++;
						int y_update = try_to_move(y_distance,y_positive,Action.West,Action.East, toAvoid);
						if(y_update==-1){
							y_distance = destination.getY()-currentPosition.getY();
							if(!avoid_obstacle(false, x_positive, toAvoid)){
								avoid_obstacle(false,!x_positive,toAvoid);
							}
							x_distance = destination.getX()-currentPosition.getX();
						} else {
							y_distance = y_update;
						}
						if(counter>5){
							break;
						}
					}
					counter=0;
					while(x_distance!=0){
						counter++;
						int x_update = try_to_move(x_distance,x_positive,Action.North,Action.South, toAvoid);
						if(x_update==-1){
							x_distance = destination.getX()-currentPosition.getX();
							if(!avoid_obstacle(false, y_positive, toAvoid)){
								avoid_obstacle(false,!y_positive,toAvoid);
							}
							y_distance = destination.getY()-currentPosition.getY();
						} else {
							x_distance = x_update;
						}
						if(counter>5){
							break;
						}
					}
				}
				if(x_distance==0 && y_distance==0){
					break;
				}
			}
		}	
		return true;
	}

	//If moveInVertical is true it means that we have to change x, otherwise y
	private boolean avoid_obstacle(boolean moveInVertical, boolean positive, Path toAvoid) {
		List<Path> p = getPossiblePaths(currentPosition);
		Path decision = null;
		if(moveInVertical){
			if(positive){
				decision = findByPointAndOppositeAction(p, currentPosition, Action.South);
				if(decision==null || decision.equalsTo(toAvoid)){
					return false;
				}
				route.addAction(decision.getAction(currentPosition));
				currentPosition = decision.otherPoint(currentPosition);
				//System.out.println(currentPosition.print());
				return true;
			} else {
				decision = findByPointAndOppositeAction(p, currentPosition, Action.North);
				if(decision==null || decision.equalsTo(toAvoid)){
					return false;
				}
				route.addAction(decision.getAction(currentPosition));
				currentPosition = decision.otherPoint(currentPosition);
				//System.out.println(currentPosition.print());
				return true;
			}
		} else {
			if(positive){
				decision = findByPointAndOppositeAction(p, currentPosition, Action.West);
				if(decision==null || decision.equalsTo(toAvoid)){
					return false;
				}
				route.addAction(decision.getAction(currentPosition));
				currentPosition = decision.otherPoint(currentPosition);
				//System.out.println(currentPosition.print());
				return true;
			} else {
				decision = findByPointAndOppositeAction(p, currentPosition, Action.East);
				if(decision==null || decision.equalsTo(toAvoid)){
					return false;
				}
				route.addAction(decision.getAction(currentPosition));
				currentPosition = decision.otherPoint(currentPosition);
				//System.out.println(currentPosition.print());
				return true;
			}
		}
	}

	private int try_to_move(int distance, boolean positive, Action positiveAction, Action negativeAction, Path toAvoid) {
		while(distance>0){
			List<Path> p = getPossiblePaths(currentPosition);
			Path decision = null;
			if(positive){
				decision = findByPointAndOppositeAction(p, currentPosition, positiveAction);
			} else {
				decision = findByPointAndOppositeAction(p, currentPosition, negativeAction);
			}
			if(decision==null || decision.equalsTo(toAvoid)){
				return -1;
			}
			route.addAction(decision.getAction(currentPosition));
			currentPosition = decision.otherPoint(currentPosition);
			//System.out.println(currentPosition.print());
			distance--;
		}	
		return distance;
	}

	private Coordinate findExit(Coordinate c) {
		//System.out.println("Sto lavorando con la exit " + c.print());
		List<Coordinate> possibleExites = new ArrayList<Coordinate>();
		for(int i=0;i<openSpaces.size();i++){
			Coordinate c1 = openSpaces.get(i).entry;
			Coordinate c2 = openSpaces.get(i).exit;
			if(c1.equalsTo(c)){
				possibleExites.add(c2);
			}
			if(c2.equalsTo(c)){
				possibleExites.add(c1);
			}
		}
		if(possibleExites.contains(previousExit)){
			possibleExites.remove(previousExit);
		}
		int size = possibleExites.size();
		if(size==0){
			//System.out.println("Non ci sono possibili uscite");
			return null;
		}
		//System.out.println("Possibili uscite sono: ");
		for(int i=0;i<possibleExites.size();i++){
			//System.out.println(possibleExites.get(i).print());
		}
		double[] probabilities = new double[size];
		double sum = 0.0;
		for(int i=0;i<possibleExites.size();i++){
			double d = distance(possibleExites.get(i),route.getObjectivePoint());
			if(d==0){
				probabilities[i]=10000;
			} else {
				probabilities[i]=1.0/d;
			}
			sum+=probabilities[i];
		}
		Random generator = new Random();
		double p = generator.nextDouble();
		for(int i=1;i<probabilities.length;i++){
			probabilities[i]+=probabilities[i-1];
		}
		for(int i=0;i<probabilities.length;i++){
			probabilities[i]/=sum;
		}
		for(int i=0;i<probabilities.length;i++){
			if(p<probabilities[i]){
				return possibleExites.get(i);
			}
		}
		return possibleExites.get(0);
		
	}

	private double distance(Coordinate point, Coordinate objectivePoint) {
		int x1 = point.getX();
		int y1 = point.getY();
		int x2 = objectivePoint.getX();
		int y2 = objectivePoint.getY();
		return Math.sqrt(Math.pow(x1-x2, 2)+Math.pow(y1-y2, 2));
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

	private double[] getProbabilities(List<Path> possiblePaths, List<Path> betterPaths) {
		Action advicedHorizontalAction = null;
		Action advicedVerticalAction = null;
		if(!openSpaceMode){
			if(currentPosition.isAnExit()){
				Coordinate bestDirection = this.findExit(currentPosition);
				if(bestDirection!=null){
					openSpaceDestination = bestDirection;
					openSpaceMode  = true;
				}
			}
		}
		if(openSpaceDestination!=null){
			if(openSpaceDestination.equalsTo(currentPosition)){
				//System.out.println("Open Space attraversato! Destinazione raggiunta!");
				openSpaceMode = false;
				openSpaceDestination = null;
				return getProbabilities(possiblePaths, betterPaths);
			} else {
				if(currentPosition.isAnExit()){
					//System.out.println("ERRORE. SI STA CERCANDO DI USCIRE DA UN OPENSPACE DIVERSAMENTE DA QUANTO CONSIGLIATO!");
				}
			}
		}
		if(openSpaceDestination!=null){
			if(openSpaceDestination.getX()>currentPosition.getX()){
				advicedVerticalAction = Action.South;
			}
			if(openSpaceDestination.getX()<currentPosition.getX()){
				advicedVerticalAction = Action.North;
			}
			if(openSpaceDestination.getY()>currentPosition.getY()){
				advicedHorizontalAction = Action.East;
			}
			if(openSpaceDestination.getY()<currentPosition.getY()){
				advicedHorizontalAction = Action.West;
			}
		}
		double[] probabilities = new double[possiblePaths.size()];
		double sum=0;
		for(int i=0;i<possiblePaths.size();i++){
			Path workingPath = possiblePaths.get(i);
			probabilities[i] = workingPath.getPheromone();
			if(betterPaths.contains(workingPath)){
				probabilities[i]+=probabilities[i]*100/BONUS_PERCENTAGE_PER_NON_LOOP;
			}
			if(advicedVerticalAction!=null){
				if(workingPath.getAction(currentPosition).equals(advicedVerticalAction)){
					//System.out.println("Applico il bonus per andare verso " + advicedVerticalAction);
					probabilities[i]+=probabilities[i]*100/BONUS_PER_OPEN_SPACE;
				}
			}
			if(advicedHorizontalAction!=null){
				if(workingPath.getAction(currentPosition).equals(advicedHorizontalAction)){
					//System.out.println("Applico il bonus per andare verso " + advicedHorizontalAction);
					probabilities[i]+=probabilities[i]*100/BONUS_PER_OPEN_SPACE;
				}
			}
			sum+=probabilities[i];
		}
		for(int i=0;i<probabilities.length;i++){
			probabilities[i]/=sum;
			//System.out.println( possiblePaths.get(i).getAction(currentPosition) + " -> " + probabilities[i]);
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
