/**
 * The ComparatorByRatio class is responsible for calculating the ratio in the 
 * HPRN scheduling algorithm. 
 * 
 * @author Christina Liu 
 *
 */

import java.util.Comparator;

public class ComparatorByRatio implements Comparator<Object>{

	@Override
	public int compare(Object proc1, Object proc2) {
		//cast objects so we can compare
		Process pro1 = (Process)proc1;
		Process pro2 = (Process)proc2;
		
		if(pro1.getRatio()>pro2.getRatio()) {
			return -1;
		}
		else if(pro1.getRatio()<pro2.getRatio()) {
			return 1;
		}
		else {
			int priority1 = pro1.sortedPriority;
			int priority2 = pro2.sortedPriority;
			if(priority1>priority2) {
				return 1;
			}
			else {
				return -1;
			}
		}
	}
	
}
