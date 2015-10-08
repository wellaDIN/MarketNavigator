package robot;

import maze.Maze;

public class Controller {

	private static final int MAX_ITERATIONS = 100;
	private static final int ANTS_PER_ITERATIONS = 5;
	private static final int PHEROMONE = 4;
	private static final int EVAPORATION = 3;
	private static final int CONVERGENCE = 3;
	private static final String MAZE_FILE = "file/easy_maze.txt";
	private static final String MAZE_COORDINATES = "file/easy_coordinates.txt";
	
	public static void main(String[] args) {
		Maze maze = new Maze(MAZE_FILE,MAZE_COORDINATES);
		maze.print();
	}

}
