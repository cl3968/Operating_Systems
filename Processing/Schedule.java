/**
 * The Schedule class is responsible for reading from a file input and
 * simulate scheduling in order to see how the time required depends on the scheduling
 * algorithm and the request patterns.
 *
 * 
 * 
 * @author Christina Liu and in collaboration with Sonam Tailor 
 *
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Schedule{

	static int indRand = 0;
	static ArrayList<Integer> random = new ArrayList<Integer>();
	static Boolean verbose = false;

	public static void main(String[] args) throws FileNotFoundException{
		//verify that the command line argument exists 
			if (args.length == 0 ) {
				System.err.println("Usage Error: the program expects file name as an argument.\n");
				System.exit(1);
			}

			File scheduling;
			if (args[0].equals("--verbose")){
					verbose = true;
					scheduling = new File(args[1]);
			}
			else{
				scheduling = new File(args[0]);
			}
			
			//verify that command line argument contains a name of an existing file  
			if (!scheduling.exists()){
				System.err.println("Error: the file "+scheduling.getAbsolutePath()+" does not exist.\n");
				System.exit(1);
			}
			
			if (!scheduling.canRead()){
				System.err.println("Error: the file "+scheduling.getAbsolutePath()+
												" cannot be opened for reading.\n");
				System.exit(1);
			}
			
			//open the file for reading 
			Scanner inSchedule = null; 
			
			try {
				inSchedule = new Scanner (scheduling ) ;
			} catch (FileNotFoundException e) {
				System.err.println("Error: the file "+scheduling.getAbsolutePath()+
												" cannot be opened for reading.\n");
				System.exit(1);
			}
			
			File random_numbers = new File("random-numbers.txt");
			Scanner random_num = new Scanner(new FileReader(random_numbers));
			while (random_num.hasNext()) {
				random.add(random_num.nextInt());
			}
			random_num.close();
			
			int numberofProcs = inSchedule.nextInt();

			Queue<Process> allProcesses = new LinkedList<Process>();
			Queue<Process> FCFS1 = new LinkedList<Process>();
			Queue<Process> RR1 = new LinkedList<Process>();
			Queue<Process> SJF1 = new LinkedList<Process>();
			Queue<Process> HPRN1 = new LinkedList<Process>();

			int[] index = new int[numberofProcs];

			for (int i =0; i < numberofProcs; i++) {
				int a = inSchedule.nextInt();
				int b = inSchedule.nextInt();
				int c = inSchedule.nextInt();
				int m = inSchedule.nextInt();
				index[i] = a;
				
				Process process1 = new Process( a,b,c,m, 0, c, i);
				Process process2 = new Process( a,b,c,m, 0, c, i);
				Process process3 = new Process( a,b,c,m, 0, c, i);
				Process process4 = new Process( a,b,c,m, 0, c, i);
				Process process5 = new Process( a,b,c,m, 0, c, i);
				
				allProcesses.add(process1);
				FCFS1.add(process2);
				RR1.add(process3);
				SJF1.add(process4);
				HPRN1.add(process5);
			}
			Arrays.sort(index);
			System.out.print("The original input was: " + numberofProcs);
			for (Process p: allProcesses) {
				System.out.print(" ");
				System.out.print("(");
				System.out.print(p.a + " ");
				System.out.print(p.b + " ");
				System.out.print(p.c + " ");
				System.out.print(p.m);
				System.out.print(")");
			}
			System.out.println();
			System.out.print("The (sorted) input is: " + numberofProcs);
			Queue<Process> temp = new LinkedList<Process>();
			int i = 0; 
			for(int j = 0; j < index.length; j++) {
				for(Process p: allProcesses) {
					if(p.a == index[j]) {
						temp.add(p);
						break;
					}
				}
				i++;
			}
			allProcesses=temp;
			for (Process p: allProcesses) {
				System.out.print(" ");
				System.out.print("(");
				System.out.print(p.a + " ");
				System.out.print(p.b + " ");
				System.out.print(p.c + " ");
				System.out.print(p.m);
				System.out.print(")");
			}
			
			FCFS1=allProcesses;RR1=allProcesses;SJF1=allProcesses;HPRN1=allProcesses;
			Arrays.sort(index);
			System.out.println("The scheduling algorithm used was First Come First Served");
			FCFS(FCFS1, numberofProcs, verbose, index);
			System.out.println("-------------------------------------------------");
			System.out.println("The scheduling algorithm used was Round Robbin");
			RR(RR1, numberofProcs, verbose, index);			
			System.out.println("-------------------------------------------------");
			System.out.println("The scheduling algorithm used was Shortest Job First");
			SJF(SJF1, numberofProcs, verbose, index);
			System.out.println("-------------------------------------------------");
			System.out.println("The scheduling algorithm used was Highest Penalty Ratio Next");
			HPRN(HPRN1, numberofProcs, verbose, index);
	}
	
	public static void FCFS(Queue<Process> processes, int numberofProcs, Boolean verbose, int[] index){
		float CPUTime =0;
		int cycle=0;
		Process addToEnd;
		Queue<Process> finish = new LinkedList<Process>();
		System.out.println();
		if (verbose) {
			System.out.println("This detailed printout gives the state and remaining burst for each process");	
		} 
		while (cycle<=index[0]) {
			if(verbose) {
				System.out.print("\nBefore cycle \t"+ cycle+":");
			
			for(Process p: processes) {
				System.out.printf("\t"+p.getState()+"%2s",0);
			}
			}
			cycle++;
		}
		int[] states = new int[numberofProcs];
		int[] statenum = new int[numberofProcs];
		while(!processes.isEmpty()) {
			if (verbose) {
			System.out.print("\nBefore cycle \t"+cycle+":");
			}

			for (Process p: processes) {	
				//case that it is first element of queue and in state running, ready, or unstarted
				if(p.getState() != "blocked" && processes.peek()==p && cycle>p.a) {
					if(p.CPUBurst == 0 && p.IOBurst == 0) {
						
						p.setCPUburst(p.b);
						indRand++;
					}
					p.state = 2;
					//store state before decrementing CPU burst
					statenum[p.processNum]=p.CPUBurst;
					p.CPUBurst--;
					CPUTime++;
					//decrease total remaining time
					p.remaining-=1;
					
					states[p.processNum]=2;
					
					if(p.CPUBurst== 0 && p.IOBurst!=0) {
						p.state = 3;
					}
				}
				
				//unstarted or ready state
				else if(p.state == 0 || p.state == 1) {
					if (p.state == 0 && cycle<=p.a) {
						states[p.processNum] = 0;
						//if it is unstarted integer state is 0
						statenum[p.processNum] = 0;
					}
					else {
						p.state = 1;
						p.waitTime+=1;
						states[p.processNum]=1;
						statenum[p.processNum]=0;
					}
				}
				
				//case that process is blocked
				else {
					//store integer state before decrementing
					statenum[p.processNum] = p.IOBurst;
					p.IOBurst-=1;
					p.IOTime+=1;
					states[p.processNum]=3;
					if(p.IOBurst==0) {
						p.state = 1;
					}
				}
			}
			if (!processes.isEmpty() && (processes.peek().getState() == "blocked" || processes.peek().getState() == "unstarted")) {
				addToEnd = processes.poll();
				processes.add(addToEnd);
				
			
				while(processes.peek().getState()=="unstarted" && (cycle<=processes.peek().a)) {
						states[processes.peek().processNum]= 0;
						addToEnd = processes.poll();
						processes.add(addToEnd);
				
					}
			}
			if (verbose) {
				for(int i = 0; i < states.length; i++) {
					if(states[i]==1) {
						System.out.printf("\t%3s\t %2s", "ready",statenum[i]);
					}
					else if(states[i]==4) {
						System.out.printf("\t%3s %s","terminated",statenum[i]);
					}
					else if(states[i]==0) {
						System.out.printf("\t%3s %s","unstarted",statenum[i]);
					}
					else if(states[i]==2) {
						System.out.printf("\t%3s %3s","running",statenum[i]);
					}
					else if(states[i]==3) {
						System.out.printf("\t%3s %3s","blocked",statenum[i]);
					}
				}
			}
			
			for(Process p: processes) {
				if (p!=null) {
					if (p.remaining == 0) {
						p.state = 4;
						states[p.processNum]=4;
						statenum[p.processNum]= 0;
						p.finishingT = cycle;
						//add to list of completed
						finish.add(p);
						processes.remove(p);
					}
				}
			}
			cycle++;
			
		}
		System.out.println("\nThe scheduling algorithm used was First Come First Served. ");
		System.out.println("");
		
		
		float totalturn = 0;
		float totalWait = 0;
		float totalIO = 0;
		
		//need to sort the finishedProc 
		Queue<Process> finishedProc = new LinkedList<Process>();
		for(int j = 0; j < index.length; j++) {
			for (Process p : finish) {
				if(p.a == index[j]) {
					finishedProc.add(p);
					finish.remove(p);
					break;
				}
			}
		}

		int i = 0;
		for (Process p: finishedProc) {
			System.out.println("Process "+i+":");
			System.out.print("(A,B,C,M) = ");
			System.out.print("("+p.a +","+p.b+","+p.c+","+p.m+")");
			System.out.println("\nFinishing time: "+p.finishingT);
			int turnaround = p.finishingT - p.a;
			System.out.println("Turnaround time: " + turnaround);
			System.out.println("I/O time: "+ p.IOTime);
			System.out.println("Waiting time: " + p.waitTime);
	
			System.out.println("");

			totalturn += (p.finishingT - p.a);
			totalWait += p.waitTime;
			totalIO += p.IOTime;
			i++;
		}
		
		int finished =  cycle-1;
		System.out.println();
		System.out.println("Summary Data:");
		System.out.println("\tFinishing time: "+finished);
		float cpuUtilization = CPUTime/finished;
		float ioutilization = totalIO/finished;
		System.out.printf("\tCPU Utilization: %6f\n",cpuUtilization);
		System.out.printf("\tI/O Utilization: %6f\n", ioutilization);
		float throughput =  (float)((100*numberofProcs)/(float)(finished));
		System.out.printf("\tThroughput: %6f processes per hundred cycles\n",throughput);
		float avgTurnaround = totalturn/(float)numberofProcs;
		System.out.printf("\tAverage turnaround time: %6f\n", avgTurnaround);
		float avgWait = totalWait/numberofProcs;
		System.out.printf("\tAverage waiting time: %6f\n", avgWait);
	}

	//quantum 2
	public static void RR(Queue<Process> processes, int numberofProcs, Boolean verbose, int[] index){
		float CPUTime =0;
		int cycle=0;
		Process addToEnd;
		Queue<Process> finish = new LinkedList<Process>();
		System.out.println();
		if (verbose) {
			System.out.println("This detailed printout gives the state and remaining burst for each process");	
		} 
		while (cycle<=index[0]) {
			if(verbose) {
				System.out.print("\nBefore cycle \t"+ cycle+":");
			
			for(Process p: processes) {
				System.out.printf("\t"+p.getState()+"%2s",0);
			}
			}
			cycle++;
		}
		int[] states = new int[numberofProcs];
		int[] statenum = new int[numberofProcs];
		while(!processes.isEmpty()) {
			if (verbose) {
			System.out.print("\nBefore cycle \t"+cycle+":");
			}

			for (Process p: processes) {	
				//case that it is first element of queue and in state running, ready, or unstarted
				if(p.getState() != "blocked" && processes.peek()==p && cycle>p.a) {
					if(p.CPUBurst == 0 && p.IOBurst == 0) {
						
						p.setCPUburst(p.b);
						indRand++;
						//our quantum is 2 so we can only run for a max of 2 on the CPU
						if(p.CPUBurst > 2) {
							p.CPUBurst = 2;
							p.setIOburst(2);
						}
					}
					p.state = 2;
					//store state before decrementing CPU burst
					statenum[p.processNum]=p.CPUBurst;
					p.CPUBurst--;
					CPUTime++;
					//decrease total remaining time
					p.remaining-=1;
					
					states[p.processNum]=2;
					
					if(p.CPUBurst== 0 && p.IOBurst!=0) {
						p.state = 3;
					}
				}
				
				//unstarted or ready state
				else if(p.state == 0 || p.state == 1) {
					if (p.state == 0 && cycle<=p.a) {
						states[p.processNum] = 0;
						//if it is unstarted integer state is 0
						statenum[p.processNum] = 0;
					}
					else {
						p.state = 1;
						p.waitTime+=1;
						states[p.processNum]=1;
						statenum[p.processNum]=0;
					}
				}
				
				//case that process is blocked
				else {
					//store integer state before decrementing
					statenum[p.processNum] = p.IOBurst;
					p.IOBurst-=1;
					p.IOTime+=1;
					states[p.processNum]=3;
					if(p.IOBurst==0) {
						p.state = 1;
					}
				}
			}
			if (!processes.isEmpty() && (processes.peek().getState() == "blocked" || processes.peek().getState() == "unstarted")) {
				addToEnd = processes.poll();
				processes.add(addToEnd);
				
			
				while(processes.peek().getState()=="unstarted" && (cycle<=processes.peek().a)) {
						states[processes.peek().processNum]= 0;
						addToEnd = processes.poll();
						processes.add(addToEnd);
				
					}
			}
			if (verbose) {
				for(int i = 0; i < states.length; i++) {
					if(states[i]==1) {
						System.out.printf("\t%3s\t %2s", "ready",statenum[i]);
					}
					else if(states[i]==4) {
						System.out.printf("\t%3s %s","terminated",statenum[i]);
					}
					else if(states[i]==0) {
						System.out.printf("\t%3s %s","unstarted",statenum[i]);
					}
					else if(states[i]==2) {
						System.out.printf("\t%3s %3s","running",statenum[i]);
					}
					else if(states[i]==3) {
						System.out.printf("\t%3s %3s","blocked",statenum[i]);
					}
				}
			}
			
			for(Process p: processes) {
				if (p!=null) {
					if (p.remaining == 0) {
						p.state = 4;
						states[p.processNum]=4;
						statenum[p.processNum]= 0;
						p.finishingT = cycle;
						//add to list of completed
						finish.add(p);
						processes.remove(p);
					}
				}
			}
			cycle++;
			
		}
		System.out.println("\nThe scheduling algorithm used was Round Robin. ");
		System.out.println("");
		
		
		float totalturn = 0;
		float totalWait = 0;
		float totalIO = 0;
		
		//need to sort the finishedProc 
		Queue<Process> finishedProc = new LinkedList<Process>();
		for(int j = 0; j < index.length; j++) {
			for (Process p : finish) {
				if(p.a == index[j]) {
					finishedProc.add(p);
					finish.remove(p);
					break;
				}
			}
		}

		int i = 0;
		for (Process p: finishedProc) {
			System.out.println("Process "+i+":");
			System.out.print("(A,B,C,M) = ");
			System.out.print("("+p.a +","+p.b+","+p.c+","+p.m+")");
			System.out.println("\nFinishing time: "+p.finishingT);
			int turnaround = p.finishingT - p.a;
			System.out.println("Turnaround time: " + turnaround);
			System.out.println("I/O time: "+ p.IOTime);
			System.out.println("Waiting time: " + p.waitTime);
	
			System.out.println("");

			totalturn += (p.finishingT - p.a);
			totalWait += p.waitTime;
			totalIO += p.IOTime;
			i++;
		}
		
		int finished =  cycle-1;
		System.out.println();
		System.out.println("Summary Data:");
		System.out.println("\tFinishing time: "+finished);
		float cpuUtilization = CPUTime/finished;
		float ioutilization = totalIO/finished;
		System.out.printf("\tCPU Utilization: %6f\n",cpuUtilization);
		System.out.printf("\tI/O Utilization: %6f\n", ioutilization);
		float throughput =  (float)((100*numberofProcs)/(float)(finished));
		System.out.printf("\tThroughput: %6f processes per hundred cycles\n",throughput);
		float avgTurnaround = totalturn/(float)numberofProcs;
		System.out.printf("\tAverage turnaround time: %6f\n", avgTurnaround);
		float avgWait = totalWait/numberofProcs;
		System.out.printf("\tAverage waiting time: %6f\n", avgWait);
	}

	public static void SJF(Queue<Process> processes, int numberofProcs, Boolean verbose, int[] index){
		float CPUTime =0;
		int cycle=0;
		Process addToEnd;
		Queue<Process> finish = new LinkedList<Process>();
		System.out.println();
		if (verbose) {
			System.out.println("This detailed printout gives the state and remaining burst for each process");	
		} 
		while (cycle<=index[0]) {
			if(verbose) {
				System.out.print("\nBefore cycle \t"+ cycle+":");
			
			for(Process p: processes) {
				System.out.printf("\t"+p.getState()+"%2s",0);
			}
			}
			cycle++;
		}
		int[] states = new int[numberofProcs];
		int[] statenum = new int[numberofProcs];
		while(!processes.isEmpty()) {
			if (verbose) {
			System.out.print("\nBefore cycle \t"+cycle+":");
			}

			for (Process p: processes) {	
				//case that it is first element of queue and in state running, ready, or unstarted
				if(p.getState() != "blocked" && processes.peek()==p && cycle>p.a) {
					if(p.CPUBurst == 0 && p.IOBurst == 0) {
						
						p.setCPUburst(p.b);
						indRand++;
					}
					p.state = 2;
					//store state before decrementing CPU burst
					statenum[p.processNum]=p.CPUBurst;
					p.CPUBurst--;
					CPUTime++;
					//decrease total remaining time
					p.remaining-=1;
					
					states[p.processNum]=2;
					
					if(p.CPUBurst== 0 && p.IOBurst!=0) {
						p.state = 3;
					}
				}
				
				//unstarted or ready state
				else if(p.state == 0 || p.state == 1) {
					if (p.state == 0 && cycle<=p.a) {
						states[p.processNum] = 0;
						//if it is unstarted integer state is 0
						statenum[p.processNum] = 0;
					}
					else {
						p.state = 1;
						p.waitTime+=1;
						states[p.processNum]=1;
						statenum[p.processNum]=0;
					}
				}
				
				//case that process is blocked
				else {
					//store integer state before decrementing
					statenum[p.processNum] = p.IOBurst;
					p.IOBurst-=1;
					p.IOTime+=1;
					states[p.processNum]=3;
					if(p.IOBurst==0) {
						p.state = 1;
					}
				}
			}
			if (!processes.isEmpty() && (processes.peek().getState() == "blocked" || processes.peek().getState() == "unstarted")) {
				addToEnd = processes.poll();
				processes.add(addToEnd);
				
			
				while(processes.peek().getState()=="unstarted" && (cycle<=processes.peek().a)) {
						states[processes.peek().processNum]= 0;
						addToEnd = processes.poll();
						processes.add(addToEnd);
				
					}
			}
			if (verbose) {
				for(int i = 0; i < states.length; i++) {
					if(states[i]==1) {
						System.out.printf("\t%3s\t %2s", "ready",statenum[i]);
					}
					else if(states[i]==4) {
						System.out.printf("\t%3s %s","terminated",statenum[i]);
					}
					else if(states[i]==0) {
						System.out.printf("\t%3s %s","unstarted",statenum[i]);
					}
					else if(states[i]==2) {
						System.out.printf("\t%3s %3s","running",statenum[i]);
					}
					else if(states[i]==3) {
						System.out.printf("\t%3s %3s","blocked",statenum[i]);
					}
				}
			}
			
			for(Process p: processes) {
				if (p!=null) {
					if (p.remaining == 0) {
						p.state = 4;
						states[p.processNum]=4;
						statenum[p.processNum]= 0;
						p.finishingT = cycle;
						//add to list of completed
						finish.add(p);
						processes.remove(p);
					}
				}
			}
			cycle++;
			
		}
		System.out.println("\nThe scheduling algorithm used was Shortest Job First. ");
		System.out.println("");
		
		
		float totalturn = 0;
		float totalWait = 0;
		float totalIO = 0;
		
		//need to sort the finishedProc 
		Queue<Process> finishedProc = new LinkedList<Process>();
		for(int j = 0; j < index.length; j++) {
			for (Process p : finish) {
				if(p.a == index[j]) {
					finishedProc.add(p);
					finish.remove(p);
					break;
				}
			}
		}

		int i = 0;
		for (Process p: finishedProc) {
			System.out.println("Process "+i+":");
			System.out.print("(A,B,C,M) = ");
			System.out.print("("+p.a +","+p.b+","+p.c+","+p.m+")");
			System.out.println("\nFinishing time: "+p.finishingT);
			int turnaround = p.finishingT - p.a;
			System.out.println("Turnaround time: " + turnaround);
			System.out.println("I/O time: "+ p.IOTime);
			System.out.println("Waiting time: " + p.waitTime);
	
			System.out.println("");

			totalturn += (p.finishingT - p.a);
			totalWait += p.waitTime;
			totalIO += p.IOTime;
			i++;
		}
		
		int finished =  cycle-1;
		System.out.println();
		System.out.println("Summary Data:");
		System.out.println("\tFinishing time: "+finished);
		float cpuUtilization = CPUTime/finished;
		float ioutilization = totalIO/finished;
		System.out.printf("\tCPU Utilization: %6f\n",cpuUtilization);
		System.out.printf("\tI/O Utilization: %6f\n", ioutilization);
		float throughput =  (float)((100*numberofProcs)/(float)(finished));
		System.out.printf("\tThroughput: %6f processes per hundred cycles\n",throughput);
		float avgTurnaround = totalturn/(float)numberofProcs;
		System.out.printf("\tAverage turnaround time: %6f\n", avgTurnaround);
		float avgWait = totalWait/numberofProcs;
		System.out.printf("\tAverage waiting time: %6f\n", avgWait);
	}

	public static void HPRN(Queue<Process> processes, int numberofProcs, Boolean verbose, int[] index){
		float CPUTime =0;
		int cycle=0;
		Process addToEnd;
		Queue<Process> finish = new LinkedList<Process>();
		Queue<Process> ready = new ConcurrentLinkedQueue<Process>();
		ArrayList<Process> blocked = new ArrayList<Process>();
		ArrayList<Process> unstarted = new ArrayList<Process>();
		Process currentRun = null;
		int numofFinish = 0; 

		int[] states = new int[numberofProcs];
		int[] statenum = new int[numberofProcs];
		//an array of processes to store the sorted input ones --> note that with this algorithm the process order is always changing
		Process processIn[] = new Process[numberofProcs];


		for (int j =0; j <numberofProcs; j++) {	
			Process p = processes.poll();
			processIn[j] = new Process(p.a, p.b, p.c, p.m, p.state, p.c, p.processNum);
			if(processIn[j].a == 0) {
				ready.add(processIn[j]);
			}
			//otherwise add it to the unstarted processes
			else {
				unstarted.add(processIn[j]);
			}
		}
		while(numofFinish < numberofProcs) {
			//if the process start time = the cycle num add to ready instead of unstarted 
			for(int j = 0; j < unstarted.size();j++) {
				if(unstarted.get(j).a == cycle) {
					Process getAdd = unstarted.get(j);
					getAdd.setcurrCycle(cycle);
					getAdd.state = 1;
					ready.add(getAdd);
				}
			}
			cycle++;
		
		//now if we have no process running then we get it from ready
		if(currentRun == null) {
			currentRun = ready.poll(); //removes the process from head 
			//currRun.state = "running";
			//make sure isnt null
			if(currentRun != null) {
				currentRun.setCPUburst(currentRun.b);
			}
		}
		
		//case that it is not null so we run
		if(currentRun != null) {
			currentRun.CPUBurst--;
			currentRun.remaining--;
			CPUTime++;
			currentRun.setcurrCycle(cycle);
		}
		
		if (verbose) {
			System.out.print("Before cycle "+cycle+":");
			
			//updating array states to print - I will check if the process is stored in the specific array
			for(Process p: processIn) {
				if(blocked.contains(p)) {
					states[p.processNum] = 3;
					statenum[p.processNum] = p.IOBurst;
				}
				else if(ready.contains(p)) {
					states[p.processNum] = 1;
					statenum[p.processNum]=0;
				}
				else if(finish.contains(p)) {
					states[p.processNum] = 4;
					statenum[p.processNum] = 0;
				}
				else if(unstarted.contains(p) && cycle <= p.a) {
					states[p.processNum] = 0;
					statenum[p.processNum] = 0;
				}
				else {
					states[p.processNum] = 2;
					statenum[p.processNum]=p.CPUBurst+1; //because we subtracted 1 before
				}
			}
			if (verbose) {
				for(int i = 0; i < states.length; i++) {
					if(states[i]==1) {
						System.out.printf("\t%3s\t %2s", "ready",statenum[i]);
					}
					else if(states[i]==4) {
						System.out.printf("\t%3s %s","terminated",statenum[i]);
					}
					else if(states[i]==0) {
						System.out.printf("\t%3s %s","unstarted",statenum[i]);
					}
					else if(states[i]==2) {
						System.out.printf("\t%3s %3s","running",statenum[i]);
					}
					else if(states[i]==3) {
						System.out.printf("\t%3s %3s","blocked",statenum[i]);
					}
				}
			}
			System.out.println();
		}
		
		//if a process is stored in the ready table --> increment the wait counter
		if(!ready.isEmpty()) {
			for(Process p: ready) {
				p.waitTime++;
				p.setcurrCycle(cycle);
			}
		}
		
		//case of state = blocked
		if(!blocked.isEmpty()) {
			Process[] processArr = blocked.toArray(new Process[0]);
			ArrayList<Process> addThis = new ArrayList<Process>();
			for(int j = 0; j < processArr.length; j++) {
				Process p = processArr[j];
				p.IOBurst--;
				p.IOTime++;
				p.setcurrCycle(cycle);
				
				if(p.IOBurst == 0) {
					ready.add(p);
					blocked.remove(p);
				}
			}
			ready.addAll(addThis);
		}
		
		//case of state = running
		if(currentRun!=null) {
			//done running
			if(currentRun.remaining == 0) {
				currentRun.finishingT = cycle;
				finish.add(currentRun);
				numofFinish++;
				currentRun = null;
			}
			//case that CPU burst done
			else if(currentRun.CPUBurst <=0 && currentRun.remaining!=0){
				//check
				blocked.add(currentRun);
				currentRun = null;
			}
			
		}
		
		//THIS IS THE main portion of the algorithm -- sorting by ratio
		if(!ready.isEmpty()) {
			//make a temporary array to sort
			ArrayList<Process> temp = new ArrayList<Process>();
			while(!ready.isEmpty()) {
				temp.add(ready.poll());
			}
			Collections.sort(temp, new ComparatorByRatio());
			ready.addAll(temp);
		}
		}

		System.out.println("\nThe scheduling algorithm used was Highest Penalty Ratio Next. ");
		System.out.println("");
		
		
		float totalturn = 0;
		float totalWait = 0;
		float totalIO = 0;
		
		//need to sort the finishedProc 
		Queue<Process> finishedProc = new LinkedList<Process>();
		for(int j = 0; j < index.length; j++) {
			for (Process p : finish) {
				if(p.a == index[j]) {
					finishedProc.add(p);
					finish.remove(p);
					break;
				}
			}
		}

		int i = 0;
		for (Process p: finishedProc) {
			System.out.println("Process "+i+":");
			System.out.print("(A,B,C,M) = ");
			System.out.print("("+p.a +","+p.b+","+p.c+","+p.m+")");
			System.out.println("\nFinishing time: "+p.finishingT);
			int turnaround = p.finishingT - p.a;
			System.out.println("Turnaround time: " + turnaround);
			System.out.println("I/O time: "+ p.IOTime);
			System.out.println("Waiting time: " + p.waitTime);
	
			System.out.println("");

			totalturn += (p.finishingT - p.a);
			totalWait += p.waitTime;
			totalIO += p.IOTime;
			i++;
		}
		
		int finished =  cycle-1;
		System.out.println();
		System.out.println("Summary Data:");
		System.out.println("\tFinishing time: "+finished);
		float cpuUtilization = CPUTime/finished;
		float ioutilization = totalIO/finished;
		System.out.printf("\tCPU Utilization: %6f\n",cpuUtilization);
		System.out.printf("\tI/O Utilization: %6f\n", ioutilization);
		float throughput =  (float)((100*numberofProcs)/(float)(finished));
		System.out.printf("\tThroughput: %6f processes per hundred cycles\n",throughput);
		float avgTurnaround = totalturn/(float)numberofProcs;
		System.out.printf("\tAverage turnaround time: %6f\n", avgTurnaround);
		float avgWait = totalWait/numberofProcs;
		System.out.printf("\tAverage waiting time: %6f\n", avgWait);
	}
}