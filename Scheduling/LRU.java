/**
 * The LRU class is the replacement algorithm that uses the least recently used page. 
 * It has methods identifying if there is a page fault and if yes, then the replacement algorithm.   
 * It implements from the interface FrameTable because it inherited.                                                                                                                                                                                                                                                                                                                                                                                                                                               
 * 
 * @author Christina Liu 
 *
 */

public class LRU implements FrameTable{
	int frameNum;
	int frameTable[][]; //each frame contains page num, process num, and last referenced time
	
	//constructor
	public LRU(int frameNum) {
		this.frameNum = frameNum;
		frameTable = new int[frameNum][4];
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
				frameTable[i][2] = time;
				return false;
			}
		}
		return true; //if cannot find demand paging then page fault occurred
	}
	
	/**
	 * Least recently used replacement algorithm   
	 * @param the array of processes, the page number, the process number, and the current time 
	 */
	@Override
	public void replace(Process[] processes, int pageNum, int processNum, int time) {
		int LRT=time; //least recent time
		int replaced =0;
		
		//checking for unused frame, then using that frame to search beginning at the highest address
		for (int i = frameNum-1; i >=0; i--) {
			if ((frameTable[i][0]==0)&&(frameTable[i][1]==0)) {
				frameTable[i][0] = pageNum;
				frameTable[i][1] = processNum;
				frameTable[i][2] = time; //this is the least recent time
				frameTable[i][3] = time; //loading the current time
				return;
			}
			
			//this is the least recently used frame and the recent time should be the largest 
			else if (LRT > frameTable[i][2]) {
				replaced = i;
				LRT = frameTable[i][2];
			}
		}
		//process the frame that was evicted
		int evict_processNum = frameTable[replaced][1];
		
		//get which process was evicted
		Process evict_process = processes[evict_processNum - 1];
		evict_process.evict++; //evict time increases
		int loadTime = frameTable[replaced][3];
		int residency = time - loadTime; //calculate residency time
		evict_process.residency += (residency);	
		
		//input new page (initialize it)
		frameTable[replaced][0] = pageNum;
		frameTable[replaced][1] = processNum;
		frameTable[replaced][2] = time;
		frameTable[replaced][3] = time;  //same as in the above loop
	}
}
