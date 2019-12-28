/**
 * The Process class is responsible for storing information for each process. 
 * It works in conjunction with the Schedule class.
 * 
 * 
 * @author Christina Liu 
 *
 */
public class Process{
	int a;
	int b;
	int c;
	int m;
	int remaining;
	int finishingT =0;
	int IOTime=0;
	int waitTime=0;
	int CPUBurst;
	int IOBurst;
	//unstarted = "0", ready = "1", running "2", blocked "3", terminated "4"
	int state =0;
	int processNum=0;

	int sortedPriority;
	int currCycle;

	public Process(int a, int b, int c, int m, int state, int remaining, int processNum){
		this.a=a;
		this.b=b;
		this.c=c;
		this.m=m;
		this.state=state;
		this.remaining=remaining;
		this.processNum=processNum;
	}

	public String getState(){
		if (this.state ==0){
			return "unstarted";
		}
		else if (this.state == 1){
			return "ready";
		}
		else if (this.state == 2){
			return "running";
		}
		else if (this.state == 3){
			return "blocked";
		}
		else if (this.state == 4){
			return "terminated";
		}
		return null;
	}
	
	public void setCPUburst(int U) {
		int val = randomOS(U);
		if (val>this.remaining) {
			this.CPUBurst = remaining;
		}
		else {
			this.CPUBurst = val;
		}
		setIOburst(this.CPUBurst);
	}

	public int randomOS(int U) {
		int num = Schedule.random.get(Schedule.indRand);
		int val = 1+(num%U);
		setIOburst(val);
		return val;
		
	}
	
	public void setIOburst(int burst) {
		this.IOBurst = burst*this.m;
	}
	
	public void setcurrCycle(int cycle) {
		this.currCycle = cycle;
	}
	
	//function used for HPRN
	public double getRatio() {
		if((this.c - this.remaining) == 0) {
			return (double)currCycle - this.a;
		}
		else {
			return (double)(currCycle - this.a)/(this.c-this.remaining);
		}
	}
}
