package nl.tudelft.otsim.Simulators.MacroSimulator.TestCases;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import nl.tudelft.otsim.Events.Scheduler;
import nl.tudelft.otsim.GUI.FakeGraphicsPanel;
import nl.tudelft.otsim.Simulators.MacroSimulator.MacroSimulator;


public class MultiThreadedScheduler extends RecursiveAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int mNrSchedulers;
	private Scheduler[] mSchedulers;
	private double mTime;
	public MultiThreadedScheduler(double time, Scheduler...schedulers) {
		mNrSchedulers = schedulers.length;
		mSchedulers = schedulers;
		mTime = time;
	}
	public static void main(String[] args) {
		String network = "EndTime:\t70200.00\nSeed:\t1\n"
				+ "Roadway:	0	from	0	to	1	speedlimit	100	lanes	2	vertices	(0.000,0.000,0.000)	(500.000,0.000,0.000)	ins	outs	1	3\n"
				+ "Roadway:	1	from	1	to	2	speedlimit	120	lanes	2	vertices	(500.000,0.000,0.000)	(2000.000,0.000,0.000)	ins	0	outs	2\n"
				+ "Roadway:	2	from	2	to	3	speedlimit	100	lanes	2	vertices	(2000.000,0.000,0.000)	(2500.000,0.000,0.000)	ins	1	4	outs\n"
				+ "Roadway:	3	from	1	to	5	speedlimit	100	lanes	2	vertices	(500.000,0.000,0.000)	(500.000,-500.000,0.000)	ins	0	outs	6\n"
				+ "Roadway:	4	from	7	to	2	speedlimit	100	lanes	2	vertices	(2000.000,-500.000,0.000)	(2000.000,0.000,0.000)	ins	7	outs	2\n"
				+ "Roadway:	5	from	4	to	5	speedlimit	100	lanes	1	vertices	(0.000,-500.000,0.000)	(500.000,-500.000,0.000)	ins	outs	6\n"
				+ "Roadway:	6	from	5	to	6	speedlimit	100	lanes	2	vertices	(500.000,-500.000,0.000)	(1500.000,-500.000,0.000)	ins	3	5	outs	7\n"
				+ "Roadway:	7	from	6	to	7	speedlimit	90	lanes	2	vertices	(1500.000,-500.000,0.000)	(2000.000,-500.000,0.000)	ins	6	outs	4\n";
		String detectors = "Detector:	0	(1500.000,0.0,0.000)\n"
				+ "Detector:	1	(500.000,-250.000,0.000)\n"
				+ "Detector:	2	(1000.000,-500.000,0.000)\n"
				+ "Detector:	3	(2000.000,-250.000,0.000)\n";
		double[] inflowTruth = new double[]{1800.0,1600.0};
		String inflows = "Inflow:	0	[0.000/"+inflowTruth[0]/1.5+":1800/"+inflowTruth[0]+":2400.000/"+inflowTruth[0]+":3900/"+inflowTruth[0]/2+":4200/"+inflowTruth[0]/2+"]\n"
				+ "Inflow: [0.000/"+inflowTruth[1]/1.5+":2100/"+inflowTruth[1]+":3000.000/"+inflowTruth[1]+":4800/"+inflowTruth[1]/2+":5100/"+inflowTruth[1]/2+"]\n";
		String turns1 = "Turn:	0	0.6\n";
		String turns2 = "Turn:	0	0.5\n";

		String configExperiment1 = network+detectors+inflows+turns1;
		String configExperiment2 = network+detectors+inflows+turns2;
		int nrExperiments = 20;
		double time = 70000;
		int nrTests = 20;
		long totalTime = 0;
		for (int t = 0; t<nrTests; t++) {

			Scheduler[] schedulers = new Scheduler[nrExperiments];
			for (int i =0; i< nrExperiments; i++) {
				schedulers[i] = new Scheduler(MacroSimulator.simulatorType, new FakeGraphicsPanel(), configExperiment1);
			}

			long beginA = System.nanoTime();
			for (Scheduler s: schedulers) {
				s.stepUpTo(time);
			}
			long endA = System.nanoTime();
			System.out.println("T="+(endA - beginA)/1000000);
			totalTime+= (endA - beginA)/1000000 ;


			/*Scheduler[] schedulers1 = new Scheduler[nrExperiments];
		for (int i =0; i< nrExperiments; i++) {
			schedulers1[i] = new Scheduler(MacroSimulator.simulatorType, new FakeGraphicsPanel(), configExperiment1);
		}

		long beginA1 = System.nanoTime();
		for (Scheduler s: schedulers1) {
			s.stepUpTo(time);
		}
		long endA1 = System.nanoTime();
		System.out.println("A2: " + (endA1 - beginA1) );

		Scheduler[] schedulers2 = new Scheduler[nrExperiments];
		for (int i =0; i< nrExperiments; i++) {
			schedulers2[i] = new Scheduler(MacroSimulator.simulatorType, new FakeGraphicsPanel(), configExperiment2);
		}

		long beginB = System.nanoTime();
		for (Scheduler s: schedulers2) {
			s.stepUpTo(time);
		}
		long endB = System.nanoTime();

		System.out.println("B1: " + (endB - beginB) );

		Scheduler[] schedulers3 = new Scheduler[nrExperiments];
		for (int i =0; i< nrExperiments; i++) {
			schedulers3[i] = new Scheduler(MacroSimulator.simulatorType, new FakeGraphicsPanel(), configExperiment2);
		}

		long beginB3 = System.nanoTime();
		for (Scheduler s: schedulers3) {
			s.stepUpTo(time);
		}
		long endB3 = System.nanoTime();

		System.out.println("B2: " + (endB3 - beginB3) );*/

		/*	Scheduler[] schedulersC = new Scheduler[nrExperiments];
		for (int i =0; i< nrExperiments; i++) {
			schedulersC[i] = new Scheduler(MacroSimulator.simulatorType, new FakeGraphicsPanel(), configExperiment2);
		}

		long beginC = System.nanoTime();
		predictUntil(time, schedulersC);
		long endC = System.nanoTime();
		totalTime+= (endC - beginC)/1000000;*/
		}

		System.out.println("totalTime: " + (totalTime) );
	}
	public static void predictUntil(double time, Scheduler...schedulers) {
		int processors = Runtime.getRuntime().availableProcessors();
		//System.out.println(Integer.toString(processors) + " processor"
		//		+ (processors != 1 ? "s are " : " is ")
		//		+ "available");
		MultiThreadedScheduler mts= new MultiThreadedScheduler(time, schedulers);
		//ForkJoinPool pool = new ForkJoinPool();
		long startTime = System.currentTimeMillis();
		FJPool.fjPool.invoke(mts);
		long endTime = System.currentTimeMillis();
		//pool.shutdown();
		//System.out.println("Prediction steps took " + (endTime - startTime) + 
			//	" milliseconds.");

	}
	protected void computeDirectly() {
		for (Scheduler s: mSchedulers) {
			s.stepUpTo(mTime);
		}

	}
	protected static int sTreshold = 2;
	@Override
	protected void compute() {
		if (mNrSchedulers <= sTreshold) {
			computeDirectly();
			return;
		}
		int split = (int) Math.round(((double) mNrSchedulers)/2);
		//invokeAll(new MultiThreadedScheduler(mTime,Arrays.copyOfRange(mSchedulers, 0, split)),new MultiThreadedScheduler(mTime,Arrays.copyOfRange(mSchedulers, split, mNrSchedulers)));
		MultiThreadedScheduler left = new MultiThreadedScheduler(mTime,Arrays.copyOfRange(mSchedulers, 0, split));
		MultiThreadedScheduler right =new MultiThreadedScheduler(mTime,Arrays.copyOfRange(mSchedulers, split, mNrSchedulers));
		left.fork();
		right.compute();
		left.join();
	}

}
