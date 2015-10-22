package genetic;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import robot.Controller;
import robot.Route;
import maze.Coordinate;
import maze.Maze;
import utility.Utility;

public class Genetic {

	private static final String PRODUCTS_FILE = "file/tsp_products.txt";
	private static final String MAZE_FILE = "file/easy_maze.txt";
	private static final String MAZE_COORDINATES = "file/easy_coordinates.txt";
	private static final int POPULATION_SIZE = 3;
	private static final int ELITISTS_NUMBER = 1;
	private static final int MAX_ITERATIONS = 100;
	private static final double MUTATION_THRESHOLD = 0.5;
	private static List<Product> products;
	private static Coordinate startingPoint;
	private static Coordinate endingPoint;
	private static int[][] distances;
	private static Route[][] routes;
	private static List<Chromosome> chromosomes = new ArrayList<Chromosome>();
	private static List<Chromosome> elitists = new ArrayList<Chromosome>();
	
	public static void main(String[] args) {
		products = Utility.productsFileToProductsList(PRODUCTS_FILE);
		int size = products.size()+2;
		distances = new int[size][size];
		routes = new Route[size][size];
		for(int i=0;i<distances.length;i++){
			distances[i][i]=0;
		}
		Maze m = new Maze(MAZE_FILE,MAZE_COORDINATES);
		startingPoint = m.getStartingPoint();
		endingPoint = m.getEndingPoint();
		routes[0][size-1] = routes[size-1][0] = Controller.ACO(m, startingPoint, endingPoint);
		distances[0][size-1] = distances[size-1][0] = routes[0][size-1].getLength();
		for(int i=0;i<products.size();i++){
			Maze maze = new Maze(MAZE_FILE,MAZE_COORDINATES);
			startingPoint = maze.getStartingPoint();
			endingPoint = maze.getEndingPoint();
			routes[0][i+1] = routes[i+1][0] = Controller.ACO(maze, startingPoint, products.get(i).getCoordinate());
			distances[0][i+1] = distances[i+1][0] = routes[0][i+1].getLength();
		}
		for(int i=0;i<products.size();i++){
			Maze maze = new Maze(MAZE_FILE,MAZE_COORDINATES);
			startingPoint = maze.getStartingPoint();
			endingPoint = maze.getEndingPoint();
			routes[size-1][i+1] = routes[i+1][size-1] = Controller.ACO(maze, products.get(i).getCoordinate(), endingPoint);
			distances[size-1][i+1] = distances[i+1][size-1] = routes[size-1][i+1].getLength();
		}
		for(int i=0;i<products.size();i++){
			for(int j=i+1;j<products.size();j++){
				Maze maze = new Maze(MAZE_FILE,MAZE_COORDINATES);
				routes[i+1][j+1] = routes[j+1][i+1] = Controller.ACO(maze, products.get(i).getCoordinate(), products.get(j).getCoordinate());
				distances[i+1][j+1] = distances[j+1][i+1] = routes[i+1][j+1].getLength();
			}
		}
		createFileWithMatrix();
		for(int i=0;i<POPULATION_SIZE;i++){
			chromosomes.add(new Chromosome(size-2));
			Boolean ok = false;
			while(!ok){
				ok = true;
				chromosomes.get(i).randomGeneration();
				for(int j=0;j<chromosomes.size();j++){
					if(j!=i){
						if(chromosomes.get(i).equals(chromosomes.get(j))){
							ok=false;
						}
					}
				}
			}
		}
		int counter=0;
		print(chromosomes);
		findElitists(chromosomes);
		while(counter<MAX_ITERATIONS){
			List<Chromosome> newPopulation = new ArrayList<Chromosome>();
			newPopulation.addAll(elitists);
			while(newPopulation.size()!=chromosomes.size()){
				Chromosome[] parents = selectParents(chromosomes);
				Chromosome[] children = crossOver(parents[0],parents[1]);
				Random generator = new Random();
				Double p1 = generator.nextDouble();
				Double p2 = generator.nextDouble();
				if(p1<MUTATION_THRESHOLD){
					mutate(children[0]);
				}
				if(p2<MUTATION_THRESHOLD){
					mutate(children[1]);
				}
				newPopulation.add(children[0]);
				newPopulation.add(children[1]);
			}
			elitists.clear();
			chromosomes.clear();
			chromosomes = newPopulation;
			findElitists(chromosomes);
			counter++;
		}
		print(chromosomes);
		createFileWithFinalRoute();
	}

	
	private static void createFileWithFinalRoute() {
		File finalRouteFile = new File("file/output/TSPFinalRoute.txt");
		try {
			if(finalRouteFile.createNewFile()){
				System.out.println("OK");
			} else{
				PrintWriter writer = new PrintWriter("file/output/TSPFinalRoute.txt");
				writer.print("");
				writer.close();
			}
			PrintWriter writer = new PrintWriter("file/output/TSPFinalRoute.txt");
			Chromosome bestChromosome = selectBestChromosome(chromosomes);
			int[] genes = bestChromosome.getGenes();
			Route start = routes[0][genes[0]];
			Route end = routes[genes[genes.length-1]][products.size()+1];
			int length = start.getLength() + end.getLength();
			Route[] paths = new Route[genes.length-1];
			for(int i=0;i<paths.length;i++){
				paths[i] = routes[genes[i]][genes[i+1]];
				length += paths[i].getLength();
			}
			writer.println(length);
			writer.println(startingPoint.getY() + ", " + startingPoint.getX() + ";");
			for(int i=0;i<start.getActionList().size();i++){
				writer.println(start.getActionList().get(i).getNumber() + ";");
			}
			writer.println("take product #" + genes[0] + ";");
			for(int i=0;i<paths.length;i++){
				for(int j=0;j<paths[i].getActionList().size();j++){
					writer.println(paths[i].getActionList().get(j).getNumber() + ";");
				}
				writer.println("take product #" + genes[i+1] + ";");
			}
			for(int i=0;i<end.getActionList().size();i++){
				writer.println(end.getActionList().get(i).getNumber() + ";");
			}
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static Chromosome mutate(Chromosome chromosome) {
		int[] genes = chromosome.getGenes().clone();
		int size = genes.length;
		int[] newGenes =genes.clone();
		Random generator = new Random();
		int city_index = generator.nextInt(size);
		int value = genes[city_index];
		int new_position = city_index;
		while(new_position==city_index){
			new_position = generator.nextInt(size);
		}
		for(int i=city_index;i<size-1;i++){
			newGenes[i] = newGenes[i+1];
		}
		newGenes[size-1]=0;
		for(int i=size-1;i>new_position;i--){
			newGenes[i]=newGenes[i-1];
		}
		newGenes[new_position] = value;
		return new Chromosome(newGenes.length,newGenes);
	}

	private static void findElitists(List<Chromosome> chromosomes) {
		List<Chromosome> temp = new ArrayList<Chromosome>();
		for(int i=0;i<chromosomes.size();i++){
			temp.add(chromosomes.get(i));
		}
		for(int i=0;i<ELITISTS_NUMBER;i++){
			Chromosome best = selectBestChromosome(temp);
			elitists.add(best);
			temp.remove(best);
		}		
	}

	private static Chromosome[] selectParents(List<Chromosome> chromosomes) {
		double[] probabilities = new double[chromosomes.size()];
		double sum = 0.0;
		for(int i=0;i<probabilities.length;i++){
			probabilities[i] = evaluateFitness(chromosomes.get(i));
			sum += probabilities[i];
		}
		for(int i=0;i<probabilities.length;i++){
			probabilities[i] /= sum;
		}
		for(int i=1;i<probabilities.length;i++){
			probabilities[i] +=probabilities[i-1];
		}
		Random generator = new Random();
		int fatherIndex = findIndex(probabilities,generator.nextDouble());
		int motherIndex = fatherIndex;
		while(motherIndex==fatherIndex){
			motherIndex = findIndex(probabilities, generator.nextDouble());
		}
		Chromosome[] parents = new Chromosome[2];
		parents[0] = chromosomes.get(fatherIndex);
		parents[1] = chromosomes.get(motherIndex);
		return parents;
	}

	private static int findIndex(double[] probabilities, double nextDouble) {
		for(int i=0;i<probabilities.length;i++){
			if(nextDouble<probabilities[i]){
				return i;
			}
		}
		return -1;
	}

	private static void print(List<Chromosome> chromosomes) {
		System.out.println("CHROMOSOMES");
		for(int i=0;i<chromosomes.size();i++){
			chromosomes.get(i).print();
			System.out.println(evaluateFitness(chromosomes.get(i)));
		}
	}

	private static Chromosome[] crossOver(Chromosome father, Chromosome mother) {
		Chromosome[] children = new Chromosome[2];
		int[] fatherGenes = father.getGenes().clone();
		int[] motherGenes = mother.getGenes().clone();
		int size = fatherGenes.length;
		Random generator = new Random();
		int k = generator.nextInt(size-1)+1;
		for(int i=0;i<k;i++){
			int toBeChanged = mother.getGenes()[i];
			int index = findIndex(fatherGenes,toBeChanged);
			fatherGenes[index]=fatherGenes[i];
			fatherGenes[i]=toBeChanged;
		}
		for(int i=0;i<k;i++){
			int toBeChanged = father.getGenes()[i];
			int index = findIndex(motherGenes,toBeChanged);
			motherGenes[index]=motherGenes[i];
			motherGenes[i]=toBeChanged;
		}
		children[0]=new Chromosome(fatherGenes.length,fatherGenes);
		children[1]=new Chromosome(motherGenes.length,motherGenes);
		return children;
	}

	private static int findIndex(int[] array, int value) {
		for(int i=0;i<array.length;i++){
			if(array[i]==value){
				return i;
			}
		}
		return -1;
	}



	private static Chromosome selectBestChromosome(List<Chromosome> chromosomes) {
		double[] fitness = new double[chromosomes.size()];
		for(int i=0;i<fitness.length;i++){
			fitness[i] = evaluateFitness(chromosomes.get(i));
		}
		int max = 0;
		for(int i=1;i<fitness.length;i++){
			if(fitness[i]>fitness[max]){
				max=i;
			}
		}
		return chromosomes.get(max);
	}


	private static double evaluateFitness(Chromosome chromosome) {
		int fitness = 0;
		int[] genes = chromosome.getGenes();
		fitness += distances[0][genes[0]];
		for(int i=0;i<genes.length-1;i++){
			fitness += distances[genes[i]][genes[i+1]];
		}
		int size = genes.length;
		fitness += distances[genes[size-1]][size+1];
		return 1.0/fitness;
	}



	private static void createFileWithMatrix() {
		File finalRouteFile = new File("file/output/matrix.txt");
		try {
			if(finalRouteFile.createNewFile()){
				System.out.println("OK");
			} else{
				PrintWriter writer = new PrintWriter("file/output/matrix.txt");
				writer.print("");
				writer.close();
			}
			PrintWriter writer = new PrintWriter("file/output/matrix.txt");
			for(int i=0;i<distances.length;i++){
				String s = "";
				for(int j=0;j<distances.length;j++){
					s = s +  (distances[i][j] + "\t");
				}
				writer.println(s);
			}
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
