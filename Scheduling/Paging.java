/**
 * The Paging class is the main class. It reads in the input and parses
 * it through to the different replacement algorithms and it contains
 * the methods corresponding to the job mix number. It also includes the 
 * print output, which gives the results we are looking for. 
 * 
 * @author Christina Liu 
 *
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;


public class Paging {

	//global static variable
	static Process[] processes; //array of process

	/**
	 * Reads in the input from command line and parses the input to the corresponding
	 * replacement algorithm: lru, lifo, or random. Then parse it through functions 
	 * for run according to the number of processes and then printing the output.    
	 * @param a String array of all the arguments in command line  
	 */
	public static void main(String[] args) throws FileNotFoundException {
		
		if (args.length !=7) { //checking if there are a valid number of arguments 
			System.out.println("Invalid number of arguments.");
			System.exit(0);
		}
		else { //reads the input 
			int machine_size = Integer.parseInt(args[0]);
			int page_size = Integer.parseInt(args[1]);
			int process_size = Integer.parseInt(args[2]);
			int job_mix_num = Integer.parseInt(args[3]);
			int num_of_references = Integer.parseInt(args[4]);
			String replacement_algo = args[5];
			int lvl_of_debugging_out = Integer.parseInt(args[6]);
			
			//printing the input data 
			System.out.println();
			System.out.println("The machine size is " + machine_size + ".");
			System.out.println("The page size is " + page_size + ".");
			System.out.println("The process size is " + process_size + ".");
			System.out.println("The job mix number is " + job_mix_num + ".");
			System.out.println("The number of references per process is " + num_of_references + ".");
			System.out.println("The replacement algorithm is " + replacement_algo + ".");
			System.out.println("The level of debugging output is " + lvl_of_debugging_out + ".");
			System.out.println();
		
			//read the random numbers file
			File random_numbers = new File("random-numbers.txt");
			Scanner random_num = new Scanner(new FileReader(random_numbers));
		
			int frame_number = machine_size/page_size;
			
			//parse into replacement algorithms 
			if(replacement_algo.contains("lru")){
				FrameTable frame = new LRU(frame_number);
				run(job_mix_num, process_size, num_of_references, page_size, random_num, frame);
			}
			else if(replacement_algo.contains("lifo")){
				FrameTable frame = new LIFO(frame_number);
				run(job_mix_num, process_size, num_of_references, page_size, random_num, frame);
			}
			else if(replacement_algo.contains("random")){
				FrameTable frame = new Random(frame_number,random_num);
				run(job_mix_num, process_size, num_of_references, page_size, random_num, frame);
			}
			else{
				System.out.println("Error! Redo input.");
			}	
			
			//print the output 
			//initializing these variables 
			int totalFault = 0;
			int totalResidency = 0;
			int totalEvict = 0;
			
			//printing total fault times and total residency times 
			int i=0;
			for (Process p: processes) {
				int pageFault = p.pageFault;
				int residency = p.residency;
				int evict = p.evict;
				if (evict == 0) {
					System.out.println("Process " + (i + 1) + " had " + pageFault + " faults.\n\tWith no evictions, the average residence is undefined.");
				} 
				else {
					double averageResidency = (double) residency / evict;
					System.out.println("Process " + (i + 1) + " had " + pageFault + " faults and " + averageResidency + " average residency.");
				}
				totalFault += pageFault;
				totalResidency += residency;
				totalEvict += evict;
				i++;
			}                                                                                                                                                                                                                                
			//printing the total number of faults and the average residency time
			if (totalEvict == 0) {
				System.out.println("\nThe total number of faults is " + totalFault + ".\n\tWith no evictions, the overall average residency is undifined.");
			} 
			else {
				double totalAverageResidency = (double)totalResidency / totalEvict;
				System.out.println("\nThe total number of faults is "+ totalFault+ " and the overall average residency is " + totalAverageResidency + ".");
			}
			System.out.println();
		}
		}
	
	/**
	 * Parses the input based on the job mix number 
	 * @param 4 integers: job mix number, process size, number of references, and page size, and a Scanner variable, random number text file, and a FrameTable variable  
	 */
	public static void run(int job_mix, int process_size, int num_of_references, int page_size, Scanner random_num, FrameTable frame) {
		if (job_mix > 1) { //if job mix number is greater than one then parse input into four processes
			
			//initialize the process array for 4 processes
			processes = new Process[4];
			for (int i=0; i<4; i++) {
				processes[i] = new Process(process_size, i+1);
			}
			processNum4(job_mix, num_of_references, page_size, random_num, frame); //parse into this function
		}
		else if (job_mix == 1) { //if job mix number is greater than one then parse input into four processes
			
			//initialize the process array for 1 processes
			processes = new Process[1];
			processes[0] = new Process(process_size, 1);
			processNum1(num_of_references, page_size, random_num, frame); //parse into this function
		}
		else {
			System.out.println("Job Mix Number Cannot be Negative!"); 
		}
	}

	/**
	 * If there are four processes, got to check if job mix number is 2, 3, or 4, and the corresponding cases   
	 * @param 3 integers: job mix number, number of references, page size, and a Scanner variable for random numbers, and a FrameTable variable  
	 */
	//run this in cases where job mix num is 2,3,4
	public static void processNum4(int job_mix, int num_of_references, int page_size, Scanner random_num, FrameTable frame) {
		int total = num_of_references/3; //the total number of processes (quantum is 3)
		
		//initialize the probabilities
		double A[]=new double[4];
		double B[]=new double[4];
		double C[]=new double[4];
		
		//these cases are all from the spec
		if (job_mix==2) { //if job mix number is 2 
			for (int i =0; i < 4; i++) {
				A[i]=1; B[i]=0; C[i]=0;
			}
		}
		else if (job_mix==3) { //if job mix number is 3 (fully random references)
			for (int i =0; i < 4; i++) {
				A[i]=0; B[i]=0; C[i]=0;
			}
		}
		else if (job_mix ==4) { //if job mix number is 4
			A[0] = 0.75; B[0] = 0.25; C[0] = 0;
			A[1] = 0.75; B[1] = 0; C[1] = 0.25;
			A[2] = 0.75; B[2] = 0.125; C[2] = 0.125;
			A[3] = 0.5; B[3] = 0.125; C[3] = 0.125;
		}
		
		//running all processes corresponding to their probabilities 
		for (int i =0; i <=total; i++) {
			for(int j=0; j< 4; j++){
				reference(num_of_references, page_size, j+1,A[j],B[j],C[j],i,total, random_num, frame);
			}
		}
	}
	
	/**
	 * Simulate quantum references for each job 
	 * @param 3 integers: number of references, page size, process number, and 3 doubles: probabilites A,B,C, 
	 * and the process number, and the total number of processes, and a Scanner variable for random numbers and a FrameTable variable 
	 */
	public static void reference(int num_of_references, int page_size, int processNumber, double A, double B, double C, int current , int total, Scanner random_num, FrameTable frame){
		int q; //quantum needed
		if(current!=total){ //if this process is not last process
			q = 3; //use all quantum
		}
		else{
			q = num_of_references % 3; //use as many to complete the process
		}
		for (int ref = 0; ref < q; ref++) { //from spec
			int time = 12 * current + ref + 1 + (processNumber - 1) * q;                                                                                   ;
			int pageNumber = processes[processNumber - 1].next / page_size;
			
			//check if a page fault occurs and then replace if true
			if (frame.hasPageFault(pageNumber, processNumber, time)) {
				frame.replace(processes, pageNumber, processNumber, time);
				processes[processNumber - 1].pageFault++;
			}
			
			//reference the next reference
			processes[processNumber - 1].next(A, B, C, random_num);
		}
	}
	
	/**
	 * If there is one process (the simplest and fully sequential case)   
	 * @param 2 integers: number of references and page size, and Scanner variable for random numbers, and FrameTable variable  
	 */
	//run this in cases where job mix num is 1
	public static void processNum1(int num_of_references, int page_size, Scanner random_num, FrameTable frame) {
		for (int i =1; i <=num_of_references; i++) { //for each run time that is less than or equal to the number of references
			int pageNum = processes[0].next / page_size;
			if (frame.hasPageFault(pageNum, 1, i)) { //check if there is a page fault 
				frame.replace(processes, pageNum, 1, i); //if yes, replace
				processes[0].pageFault++;
			}
			processes[0].next(1, 0, 0, random_num); //create next reference
		}
	}

}