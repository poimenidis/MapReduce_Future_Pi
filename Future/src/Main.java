import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

public class Main {

	public static void main(String[] args) throws ExecutionException {
		// TODO Auto-generated method stub

		// numStep is the number of pieces that belong in [0,1] area. 
		int numSteps = 100000000;
		
		// pi variable. It will take a value after add table's elements. 
		double pi = 0;
		// in cores the system saves the number of CPUs
		int cores = Runtime.getRuntime().availableProcessors();
		System.out.println("Number of cores: " + cores);

		//In this program number of threads are the same as number of cores
		int numThreads = cores;
		
		//initialize shared data. Assign 0 to every table's position.
		double[] a = new double[numThreads];
		for (int i = 0; i < numThreads; i++)
			a[i] = 0;

		// get current time
		long start = System.currentTimeMillis();

		// Create an array of threads
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(cores);
		ArrayList<Future<String>> future = new ArrayList<Future<String>>();

		
		
		
		//Execute all threads. PiThread gets a number-id of thread(i), the number of threads, the shared array and numSteps
		for (int i = 0; i < numThreads; i++) {
			int finalI = i;
			Future<String> future1 =(Future<String>) executor.submit(() -> {
				PiClass piClass = new PiClass(finalI, numThreads, a, numSteps);
				piClass.Execute();
			});
			
			future.add(future1);
		}

		//Wait for threads to terminate
		for(Future<String> f:future) {
			try {
				f.get();
			} catch (InterruptedException e) {
			}
		}


		// add value of every table's position to pi variable. 
		for (int i = 0; i < numThreads; i++) {
			pi += a[i];
		}
		
        /* end timing and print result */
        long endTime = System.currentTimeMillis();
        System.out.printf("sequential program results with %d steps\n", numSteps);
        System.out.printf("computed pi = %22.20f\n" , pi);
        System.out.printf("difference between estimated pi and Math.PI = %22.20f\n", Math.abs(pi - Math.PI));
        System.out.printf("time to compute = %f seconds\n", (double) (endTime - start) / 1000);

	}

}

//PiComputation class. It has an Id, the number of threads, a shared table, the numSteps,
//and the values myStart and myStop which help to calculate pi
class PiClass {
	private int myId;
	private int numThreads;
	private double [] table;
	private int numSteps;
	private int myStart;
	private int myStop;

	  
	public PiClass(int myId, int numThreads, double[] table, int numSteps) {
		this.myId = myId;
		this.numThreads = numThreads;
		this.table = table;
		this.numSteps = numSteps;
			
		/* From exercise we already know that we can compute pi by its parts. 
		 * So the system asks to every thread to calculate its part.
		 * Their part will begin from myStart to myStop.*/
		myStart = myId * (numSteps / this.numThreads);
		myStop = myStart + (numSteps / this.numThreads);
		if (myId == (this.numThreads - 1))
			myStop = numSteps;
	}

	public void Execute() {
		double step = 1.0 / (double)numSteps;
		double sum = 0;
		  /* do computation */
		for (long i = myStart; i < myStop; ++i) {
			double x = ((double)i+0.5)*step;
	        sum += 4.0/(1.0+x*x);
		}
		double pi = sum * step;
		table[myId] = pi;
	  }
  
  
}
