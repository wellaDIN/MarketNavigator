package utility;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.StringTokenizer;

import maze.Coordinate;

public class Utility {

	@SuppressWarnings({ "resource", "deprecation" })
	public static int[][] mazeFileToMatrix (String s){
		int width;
		int height;
		int[][] maze = null;
		File file = new File(s);
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		DataInputStream dis = null;
		try {
			fis = new FileInputStream(file);
			bis = new BufferedInputStream(fis);
			dis = new DataInputStream(bis);
			StringTokenizer dimensions = new StringTokenizer(dis.readLine());
			width = Integer.parseInt(dimensions.nextToken());
			height = Integer.parseInt(dimensions.nextToken());
			maze = new int[height][width];
			int j=0;
			while(dis.available()!=0){
				StringTokenizer line = new StringTokenizer(dis.readLine());
				for(int i=0; i<width;i++){
					maze[j][i]= Integer.parseInt(line.nextToken());
				}
				j++;
			}
		} catch (FileNotFoundException e){
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return maze;
	}

	@SuppressWarnings("resource")
	public static Coordinate mazeCoordinatesToStartingPoint(String s) {
		int x;
		int y;
		Coordinate startingPoint = null;
		File file = new File(s);
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		DataInputStream dis = null;
		try {
			fis = new FileInputStream(file);
			bis = new BufferedInputStream(fis);
			dis = new DataInputStream(bis);
			@SuppressWarnings("deprecation")
			String[] coordinates = dis.readLine().split(", |;");
			x = Integer.parseInt(coordinates[1]);
			y = Integer.parseInt(coordinates[0]);
			startingPoint = new Coordinate(x,y);
		} catch (FileNotFoundException e){
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return startingPoint;
	}

	@SuppressWarnings({ "resource", "deprecation" })
	public static Coordinate mazeCoordinatesToEndingPoint(String s) {
		int x;
		int y;
		Coordinate endingPoint = null;
		File file = new File(s);
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		DataInputStream dis = null;
		try {
			fis = new FileInputStream(file);
			bis = new BufferedInputStream(fis);
			dis = new DataInputStream(bis);
			dis.readLine();
			String[] coordinates = dis.readLine().split(", |;");
			x = Integer.parseInt(coordinates[1]);
			y = Integer.parseInt(coordinates[0]);
			endingPoint = new Coordinate(x,y);
		} catch (FileNotFoundException e){
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return endingPoint;
	}
}
