/**
 * The FrameTable interface allows implementation of 3 different classes
 * that correspond to the three replacement algorithms: least recently used, 
 * last in first out, and random. All replacement algorithms need a detecting 
 * page fault function and a replace function. 
 * 
 * @author Christina Liu 
 *
 */

public interface FrameTable {
	
	//method to see if page fault has occurred (true) or not (false)
	boolean hasPageFault(int pageNum, int processNum, int time);
	
	//the specific replacement algorithm: lru, lifo, or random
	void replace(Process[] processes, int pageNum, int processNum, int time);
}
