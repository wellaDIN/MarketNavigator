package robot;

import java.util.List;

import maze.Coordinate;
import maze.Maze;
import maze.Path;

public class Controller {

	private static final int MAX_ITERATIONS = 100;
	private static final int ANTS_PER_ITERATION = 2;
	private static final int PHEROMONE = 100;
	private static final double EVAPORATION_CONSTANT = 0.1;
	private static final int CONVERGENCE = 3;
	private static final String MAZE_FILE = "file/stupid_maze.txt";
	private static final String MAZE_COORDINATES = "file/stupid_coordinates.txt";
	
	public static void main(String[] args) {
		Maze maze = new Maze(MAZE_FILE,MAZE_COORDINATES);
		maze.print();
		int iteration=1;
		while(!convergence_criterion() && iteration<=MAX_ITERATIONS){
			ants_colonization(maze);
			iteration++;
		}
	}

	private static void ants_colonization(Maze maze) {
		Ant[] ants = new Ant[ANTS_PER_ITERATION];
		int[] D_pheromone = new int[ANTS_PER_ITERATION];
		Coordinate startingPoint = maze.getStartingPoint();
		Coordinate endingPoint = maze.getEndingPoint();
		List<Path> paths = maze.getPaths();
		for(int i=0; i<ANTS_PER_ITERATION;i++){
			ants[i] = new Ant(startingPoint, endingPoint, paths);
		}
		for(int i=0; i<ANTS_PER_ITERATION;i++){
			ants[i].colonize();
			//TODO Round in the proper way
			D_pheromone[i] = PHEROMONE / ants[i].getRouteLength();
			System.out.println(D_pheromone[i]);
		}
		//Updating pheromones
		apply_evaporation(paths);
		release_pheromone(paths,ants,D_pheromone);
		print(paths);
	}

	private static void release_pheromone(List<Path> paths, Ant[] ants, int[] d_pheromone) {
		for(int i=0;i<ants.length;i++){
			Route route = ants[i].getRoute();
			List<Action> actions = route.getActionList();
			Coordinate point = route.getStartingPoint();
			for(int j=0; j<actions.size(); j++){
				Path path = findByPointAndAction(paths,point,actions.get(j));
				path.increasePheromone(d_pheromone[i]);
				point = path.otherPoint(point);
			}
		}
	}

	private static Path findByPointAndAction(List<Path> paths, Coordinate point, Action action) {
		for(int i=0;i<paths.size();i++){
			if(paths.get(i).matchesWith(point,action)){
				return paths.get(i);
			}
		}
		return null;
	}

	private static void apply_evaporation(List<Path> paths) {
		for(int i=0;i<paths.size();i++){
			paths.get(i).pheromoneEvaporation(EVAPORATION_CONSTANT);
		}
	}

	private static boolean convergence_criterion() {
		return false;
	}
	
	

	private static void print(List<Path> paths) {
		for(int i=0;i<paths.size();i++){
			paths.get(i).print();
		}
	}


}
