/**
 * The Process class stores the input of a process, like the size, number of page faults, eviction times, 
 * residency times, and the next word. It also has the method for calculating the next word to reference. 
 * 
 * @author Christina Liu 
 *
 */

import java.util.Scanner;

public class Process {
	
	int processSize; //size of process
	int pageFault; //number of page faults
	int evict; //if there are no free frames, a resident page is evicted using the replacement algorithm 
	int residency; //the time (measured in memory references) that the page was evicted minus the time it was loaded
	int next; //the next word
	
	//constructor
	public Process(int S, int k) {
		this.processSize = S;
		this.next=(111*k)%S; //process k begins by referencing word 111*k mod S
		
		//initialize variables to 0
		this.pageFault=0;
		this.evict=0;
		this.residency=0;
	}
	
	/**
	 * Find the next reference word based on the four cases with probabilities A, B, C, and 1-A-B-C  
	 * @param 3 doubles: probabilities A,B,C and a random Scanner 
	 */
	public void next(double A, double B, double C, Scanner random) {
		int r = random.nextInt();
		
		//the four cases from the spec
		double y = r/(Integer.MAX_VALUE + 1d);
		if (y < A) { //case 1 with probability A
			next = (next+1)%processSize;
		}
		else if (y < A+B) { //case 2 with probability B
			next = (next-5)%processSize;
		}
		else if (y < A+B+C) { //case 3 with probability C
			next = (next+4)%processSize;
		}
		else { //else y >=A+B+C case 4 with probability 1-A-B-C
			next = random.nextInt()%processSize;
		}
	}
}
