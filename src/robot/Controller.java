package robot;

import java.util.List;

import maze.Coordinate;
import maze.Maze;
import maze.Path;

public class Controller {

	//Definition of the constant for the algorithm and the problem
	private static final int MAX_ITERATIONS = 100;
	private static final int ANTS_PER_ITERATION = 2;
	private static final int PHEROMONE = 100;
	private static final double EVAPORATION_CONSTANT = 0.1;
	private static final int CONVERGENCE = 3;
	private static final String MAZE_FILE = "file/stupid_maze.txt";
	private static final String MAZE_COORDINATES = "file/stupid_coordinates.txt";
	
	/*
	 * Importing the maze files and managing the main loop, considering the
	 * number of iterations and the convergence criterion
	 */
	public static void main(String[] args) {
		Maze maze = new Maze(MAZE_FILE,MAZE_COORDINATES);
		maze.print();
		int iteration=1;
		while(!convergence_criterion() && iteration<=MAX_ITERATIONS){
			ants_colonization(maze);
			iteration++;
		}
	}

	/**
	 * Managing the ants colonization. A number of ants are created and
	 * then they are given the initial position and the structure of the maze
	 * in order to go through it. When they achieve the goal (final position)
	 * the pheromone is 'released' and some 'evaporates'.
	 * @param maze is the labyrinth
	 */
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

	/**
	 * For each path walked by each ant, the specific amount of pheromone
	 * is released
	 * @param paths is the list of paths of the maze
	 * @param ants is the array of ants per iteration
	 * @param d_pheromone is the amount of pheromone released by each ant
	 */
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

	/**
	 * It finds a path w.r.t. a point and an action to be performed
	 * @param paths is the list of the paths
	 * @param point is one of the point of the path
	 * @param action is the performed action
	 * @return
	 */
	private static Path findByPointAndAction(List<Path> paths, Coordinate point, Action action) {
		for(int i=0;i<paths.size();i++){
			if(paths.get(i).matchesWith(point,action)){
				return paths.get(i);
			}
		}
		return null;
	}

	/**
	 * It apply the evaporation constant to all the paths
	 * @param paths is the list of paths
	 */
	private static void apply_evaporation(List<Path> paths) {
		for(int i=0;i<paths.size();i++){
			paths.get(i).pheromoneEvaporation(EVAPORATION_CONSTANT);
		}
	}

	private static boolean convergence_criterion() {
		return false;
	}
	
	
	/**
	 * It print the list of paths with the respective pheromone
	 * @param paths is the list of paths
	 */
	private static void print(List<Path> paths) {
		for(int i=0;i<paths.size();i++){
			paths.get(i).print();
		}
	}


}
