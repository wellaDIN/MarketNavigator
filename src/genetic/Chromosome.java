package genetic;

import java.util.Random;

public class Chromosome {

	private int size;
	private int[] genes;

	public int[] getGenes() {
		return genes;
	}

	public void setGenes(int[] genes) {
		this.genes = genes;
	}

	public Chromosome(int size) {
		this.size = size;
		genes = new int[size];
	}

	public Chromosome(int size, int[] genes) {
		this.size = size;
		this.genes = genes;
	}

	public void randomGeneration() {
		Random generator = new Random();
		for(int i=0;i<size;i++){
			Boolean ok = true;
			int p = generator.nextInt(size)+1;
			for(int j=0;j<i;j++){
				if(genes[j]==p){
					ok = false;
				}
			}
			if(ok){
				genes[i]=p;
			} else {
				i--;
			}
		}
		
	}
	
	public void print(){
		System.out.print("[\t");
		for(int i=0;i<size;i++){
			System.out.print(genes[i] + "\t" );
		}
		System.out.println("]");
	}
	
	public boolean equals(Chromosome other){
		if(other.size!=this.size){
			return false;
		}
		for(int i=0;i<size;i++){
			if(genes[i]!=other.genes[i]){
				return false;
			}
		}
		return true;
	}
}
