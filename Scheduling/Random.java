/**
 * The Random class is the replacement algorithm that uses a random page. 
 * It has methods identifying if there is a page fault and if yes, then the replacement algorithm.   
 * It implements from the interface FrameTable because it inherited.                                                                                                                                                                                                                                                                                                                                                                                                                                               
 * 
 * @author Christina Liu 
 *
 */

import java.util.Scanner;

public class Random implements FrameTable{
	int frameNum;
	Scanner random;
	int frameTable[][]; //each frame contains page num, process num, and last referenced time
	
	//constructor
	public Random(int frameNum, Scanner random) {
		this.frameNum = frameNum;
		this.random= random;
		frameTable = new int[frameNum][3];
	}
	
	/**
	 * Checks if the Frame has a page fault or not    
	 * @param the page number, the process number, and the current time
	 * @return true if the frame has a page fault and false if not 
	 */
	@Override
	public boolean hasPageFault (int pageNum, int processNum, int time) {
		for (int i =0; i < frameNum; i++) { //go through the number of frames
			if ((frameTable[i][0] == pageNum) && (frameTable[i][1] == processNum)) { //if page is in frame table then no page fault 
				return false;
			}
		}
		return true; //if cannot find demand paging then page fault occurred
	}
	
	/**
	 * Random replacement algorithm   
	 * @param the array of processes, the page number, the process number, and the current time 
	 */
	@Override
	public void replace(Process[] processes, int pageNum, int processNum, int time) {

		//checking for unused frame, then using that frame to search beginning at the highest address
		for (int i = frameNum-1; i >=0; i--) {
			if ((frameTable[i][0]==0)&&(frameTable[i][1]==0)) {
				frameTable[i][0] = pageNum;
				frameTable[i][1] = processNum; 
				frameTable[i][2] = time; 
				return;
			}
		}
		//evict a random page 
		int randomNum = random.nextInt(); //get random number
		int evict_frame = randomNum % frameNum; //get evicted frame
		
		//process the frame that was evicted
		int evict_processNum = frameTable[evict_frame][1];
		
		//get which process was evicted
		Process evict_process = processes[evict_processNum - 1];
		evict_process.evict++; //evict time increases
		int loadTime = frameTable[evict_frame][2];
		int residencyTime = time - loadTime; //calculate residency time
		evict_process.residency += residencyTime;
		
		//input new page (initialize it)
		frameTable[evict_frame][0] = pageNum;
		frameTable[evict_frame][1] = processNum;
		frameTable[evict_frame][2] = time;  //same as in the above loop
	}
}
