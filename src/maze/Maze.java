package maze;

import java.util.ArrayList;
import java.util.List;

import robot.Action;
import utility.Utility;

public class Maze {

	private int width;
	private int height;
	private int[][] maze;
	private Coordinate startingPoint;
	private Coordinate endingPoint;
	private List<Path> paths = new ArrayList<Path>();
	
	public Maze(String mazeFile, String mazeCoordinates) {
		this.maze = Utility.mazeFileToMatrix(mazeFile);
		this.height = maze.length;
		this.width = maze[0].length;
		this.startingPoint = Utility.mazeCoordinatesToStartingPoint(mazeCoordinates);
		this.endingPoint = Utility.mazeCoordinatesToEndingPoint(mazeCoordinates);
		initialize_paths();
	}
	
	
	public Coordinate getStartingPoint() {
		return startingPoint;
	}


	public void setStartingPoint(Coordinate startingPoint) {
		this.startingPoint = startingPoint;
	}


	public Coordinate getEndingPoint() {
		return endingPoint;
	}


	public void setEndingPoint(Coordinate endingPoint) {
		this.endingPoint = endingPoint;
	}


	private void initialize_paths() {
		for(int i=0;i<height;i++){
			for(int j=0;j<width;j++){
				if(this.maze[i][j]==1){
					List<Action> possible_actions = getPossibleDirections(i,j);
					for(int k=0;k<possible_actions.size();k++){
						Path newPath = new Path(i,j,possible_actions.get(k));
						if(!alreadyContained(paths,newPath)){
							paths.add(newPath);
						}
					}
				}
			}
		}
	}


	private boolean alreadyContained(List<Path> paths, Path newPath) {
		for(int i=0;i<paths.size();i++){
			if(paths.get(i).equalsTo(newPath)){
				return true;
			}
		}
		return false;
	}


	private List<Action> getPossibleDirections(int i, int j) {
		List<Action> possible_actions = new ArrayList<Action>();
		if(i!=0){
			if(maze[i-1][j]==1){
				possible_actions.add(Action.North);
			}
		}
		if(i!=this.height-1){
			if(maze[i+1][j]==1){
				possible_actions.add(Action.South);
			}
		}
		if(j!=0){
			if(maze[i][j-1]==1){
				possible_actions.add(Action.West);
			}
		}
		if(j!=this.width-1){
			if(maze[i][j+1]==1){
				possible_actions.add(Action.East);
			}
		}
		return possible_actions;
	}


	public void print() {
		System.out.println("MAZE\n");
		for(int i=0;i<height;i++){
			for(int j=0;j<width;j++){
				System.out.print(maze[i][j] + " ");
			}
			System.out.println("");
		}
		System.out.println("\nStarting Point : " + startingPoint.print());
		System.out.println("Ending Point : " + endingPoint.print());
	}
	
	public List<Path> getPaths() {
		return paths;
	}
	
}
