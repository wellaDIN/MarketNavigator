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
	private static final int MAX_ITERATIONS = 10;
	private static List<Product> products;
	private static Coordinate startingPoint;
	private static Coordinate endingPoint;
	private static int[][] distances;
	private static Route[][] routes;
	private static List<Chromosome> chromosomes = new ArrayList<Chromosome>();
	
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
		while(counter<MAX_ITERATIONS){
			Chromosome father = selectBestChromosome(chromosomes);
			List<Chromosome> temp = new ArrayList<Chromosome>();
			for(int i=0;i<chromosomes.size();i++){
				if(!chromosomes.get(i).equals(father)){
					temp.add(chromosomes.get(i));
				}
			}
			Chromosome mother = selectBestChromosome(temp);
			for(int i=0;i<chromosomes.size();i++){
				if(chromosomes.get(i).equals(mother)){
					mother = chromosomes.get(i);
				}
			}
			Chromosome[] children = crossOver(father,mother);
			//MUTATION
			Boolean c1 = true;
			Boolean c2 = true;
			for(int i=0;i<chromosomes.size();i++){
				if(chromosomes.get(i).equals(children[0])){
					c1=false;
				}
				if(chromosomes.get(i).equals(children[1])){
					c2=false;
				}
			}
			if(c1){
				chromosomes.add(children[0]);
			}
			if(c2){
				chromosomes.add(children[1]);
			}
			while(chromosomes.size()>POPULATION_SIZE){
				removeWorstChromosome(chromosomes);
			}
			counter++;
		}
		print(chromosomes);
	}

	private static void removeWorstChromosome(List<Chromosome> chromosomes) {
		double[] fitness = new double[chromosomes.size()];
		for(int i=0;i<fitness.length;i++){
			fitness[i] = evaluateFitness(chromosomes.get(i));
		}
		int min = 0;
		for(int i=1;i<fitness.length;i++){
			if(fitness[i]<fitness[min]){
				min=i;
			}
		}
		chromosomes.remove(chromosomes.get(min));
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
		int[] genesFather = father.getGenes().clone();
		int[] genesMother = mother.getGenes().clone();
		int size = genesFather.length;
		Random generator = new Random();
		int k = generator.nextInt(size);
		System.out.println(k);
		int fatherGene = genesFather[k];
		int motherGene = genesMother[k];
		children[0] = new Chromosome(size,genesFather);
		children[1] = new Chromosome(size,genesMother);
		int index = findGene(children[0].getGenes(),motherGene);
		children[0].getGenes()[index]=fatherGene;
		children[0].getGenes()[k]=motherGene;
		index = findGene(children[1].getGenes(),fatherGene);
		children[1].getGenes()[index]=motherGene;
		children[1].getGenes()[k]=fatherGene;
		return children;
	}

	private static int findGene(int[] genes, int motherGene) {
		for(int i=0;i<genes.length;i++){
			if(genes[i]==motherGene){
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
