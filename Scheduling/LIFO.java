/**
 * The LIFO class is the replacement algorithm that uses the last in page and it becomes the first out page. 
 * It has methods identifying if there is a page fault and if yes, then the replacement algorithm.   
 * It implements from the interface FrameTable because it inherited.                                                                                                                                                                                                                                                                                                                                                                                                                                               
 * 
 * @author Christina Liu 
 *
 */

import java.util.ArrayList;

public class LIFO implements FrameTable{
	int frameNum;
	//we use an arrayList to keep track of the indexes of the FrameTables
	ArrayList<int[]> frameTable; //each frame contains page num, process num, and last referenced time
	
	//constructor
	public LIFO(int frameNum) {
		this.frameNum = frameNum;
		frameTable = new ArrayList<int[]>();
	}
	
	/**
	 * Checks if the Frame has a page fault or not    
	 * @param the page number, the process number, and the current time
	 * @return true if the frame has a page fault and false if not 
	 */
	@Override
	public boolean hasPageFault (int pageNum, int processNum, int time) {
		for (int i =0; i < frameTable.size(); i++) { //go through the number of frames
			int[] framePage = frameTable.get(i);
			if ((framePage[0]==pageNum)&&(framePage[1]==processNum)) { //if page is in frame table then no page fault 
				return false;
			}
		}
		return true;  //if cannot find demand paging then page fault occurred
	}
	
	/**
	 * Last In First Out replacement algorithm   
	 * @param the array of processes, the page number, the process number, and the current time 
	 */
	@Override
	public void replace(Process[] processes, int pageNum, int processNum, int time) {
		if (frameNum == frameTable.size()) {
			int index = frameTable.size()-1;
			int[] evict_frame = frameTable.get(index);  //get the evicted frame (which is the last one)
			
			//process the frame that was evicted
			int evict_processNum = evict_frame[1];
			Process evict_process = processes[evict_processNum - 1]; //get which process was evicted 
			evict_process.evict++; //evicted time increases
			int loadTime = evict_frame[2];
			int residency = time - loadTime; //calculate residency time
			evict_process.residency += residency;
			
			// remove the first process in the queue
			frameTable.remove(index);
		} 
		
		//input new page (initialize it) to the last in first out queue
		int[] replaced = {pageNum, processNum, time}; 
		frameTable.add(replaced);
	}
}
