package nl.tudelft.otsim.Simulators.MacroSimulator.TestCases;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;

import org.ojalgo.matrix.BasicMatrix;
import org.ojalgo.matrix.PrimitiveMatrix;
import org.ojalgo.matrix.jama.JamaMatrix;

import nl.tudelft.otsim.Events.Scheduler;
import nl.tudelft.otsim.GUI.FakeGraphicsPanel;
import nl.tudelft.otsim.GUI.GraphicsPanel;
import nl.tudelft.otsim.GUI.Main;
import nl.tudelft.otsim.GUI.StandAlone;
import nl.tudelft.otsim.Simulators.MacroSimulator.Link;
import nl.tudelft.otsim.Simulators.MacroSimulator.MacroCell;
import nl.tudelft.otsim.Simulators.MacroSimulator.MacroSimulator;
import nl.tudelft.otsim.Simulators.MacroSimulator.Model;
import nl.tudelft.otsim.Simulators.MacroSimulator.Nodes.NodeBoundaryIn;
import nl.tudelft.otsim.Simulators.MacroSimulator.Nodes.NodeDetector;
import nl.tudelft.otsim.Simulators.MacroSimulator.Nodes.NodeInterior;
import nl.tudelft.otsim.Simulators.MacroSimulator.TestCases.AssimilationMethod.AssimilationMethodType;
import nl.tudelft.otsim.Utilities.JamaExtension;
import Jama.CholeskyDecomposition;
import Jama.Matrix;
import JamaSparseMatrix.SparseMatrix;
import JamaSparseMatrix.SparseMatrixCompressedRS;
import JamaSparseMatrix.SparseMatrixDirectRS;

public class TestEnKF {
	protected int nrCells;
	protected int nrLinks;
	protected ArrayList<MacroCell> locDetSpeed;
	protected ArrayList<MacroCell> locDetFlow;
	Model macromodel;
	protected Matrix F;
	protected Matrix P;
	protected Matrix Q;
	protected Matrix R;
	protected ArrayList<NodeDetector> detectors;
	protected int nrStateVariablesPerCell;
	protected int nrStateVariablesPerLink;
	protected int nrInflowNodes;
	protected int nrStateVariables;
	protected int nrSpeedObservations;
	protected int nrFlowObservations;
	protected int nrObservations;
	protected int[] indicesStateCells = new int[2];
	protected int[] indicesStateLinks = new int[2];
	protected int[] indicesStateInflowNodes = new int[2];
	double beta = 0;
	boolean adaptable = false;
	boolean linkStates = true;
	protected Matrix baseR;
	protected int nrEnsembles;
	protected ArrayList<Scheduler> ensembles = new ArrayList<Scheduler>();
	protected AssimilationConfiguration config;
	static private Random r = new Random(9);
	static private Random r2 = new Random();
	static private int r2seed = 79;
	static private Random r3 = new Random();
	HashMap<MacroCell, LinkedHashSet<MacroCell>> surroundingCellsMap;
	HashMap<NodeDetector, LinkedHashSet<Object>> surroundingCellsMap2;
	HashMap<Object, LinkedHashSet<NodeDetector>> surroundingObservationsMap;
	LinkedHashSet<MacroCell> cellsWithDetectors;
	ArrayList<Integer[]> correspondingIndicesOfDetectors = new ArrayList<Integer[]>();
	ArrayList<ArrayList<Integer>> affectedDetectorsIndices = new ArrayList<ArrayList<Integer>>();
	int[] detectorIndices1 = new int[1];
	static ArrayList<Integer[]> correspondingIndicesOfStateObjects = new ArrayList<Integer[]>();
	ArrayList<Object> stateVariables;
	Matrix Schur = new Matrix(1,1);
	SparseMatrix SparseSchur = new SparseMatrixDirectRS(1,1);
	double ri=1.0;
	int nr = 0;
	static ArrayList<int[]> routesInt;
	static double[] cellLengths2;
	static ArrayList<double[]> cellLengths;
	static int run = 0;
	static int[] exportRoute = new int[]{0};
	static boolean extendedOutput;
	public static void main(String[] args) {
		double inflowTruth = (2200.0);
		String network = "EndTime:\t7200.00\nSeed:\t1\n"
				+ "Roadway:	0	from	1	to	2	speedlimit	100	lanes	2	vertices	(0.000,-0.250,0.000)	(3000.000,-0.250,0.000)	ins	outs	1\n"
				+ "Roadway:	1	from	2	to	3	speedlimit	100	lanes	1	vertices	(3000.000,-0.250,0.000)	(4000.000,-0.250,0.000)	ins	0	outs\n";
		String detectors = "Detector:	0	(000.000,-0.250,0.000)\n"
				+ "Detector:	1	(250.000,-0.250,0.000)\n"
				+ "Detector:	2	(750.000,-0.250,0.000)\n"
				+ "Detector:	3	(1250.000,-0.250,0.000)\n"
				+ "Detector:	4	(1750.000,-0.250,0.000)\n"
				+ "Detector:	5	(2250.000,-0.250,0.000)\n"
				+ "Detector:	6	(2750.000,-0.250,0.000)\n"
				+ "Detector:	7	(3250.000,-0.250,0.000)\n"
				+ "Detector:	8	(3750.000,-0.250,0.000)\n";
		/*String network = "EndTime:\t7200.00\nSeed:\t1\n"
				+ "Roadway:	0	from	1	to	2	speedlimit	120	lanes	2	vertices	(0.000,-0.250,0.000)	(3000.000,-0.250,0.000)	ins	outs	1\n"
				+ "Roadway:	1	from	2	to	3	speedlimit	120	lanes	1	vertices	(3000.000,-0.250,0.000)	(4000.000,-0.250,0.000)	ins	0	outs\n";
		String detectors = "Detector:	0	(000.000,-0.250,0.000)\n"
				+ "Detector:	1	(250.000,-0.250,0.000)\n"
				+ "Detector:	2	(750.000,-0.250,0.000)\n"
				+ "Detector:	3	(1250.000,-0.250,0.000)\n"
				+ "Detector:	4	(1750.000,-0.250,0.000)\n"
				+ "Detector:	5	(2250.000,-0.250,0.000)\n"
				+ "Detector:	6	(2750.000,-0.250,0.000)\n"
				+ "Detector:	7	(3250.000,-0.250,0.000)\n"
				+ "Detector:	8	(3750.000,-0.250,0.000)\n";*/

		String[] networksplit = network.split("\n");
		String networkAfterSplit1 = networksplit[0]+"\n"+networksplit[1]+"\n";
		String networkAfterSplit2 = networksplit[0]+"\n"+networksplit[1]+"\n";
		for (int i = 2; i<networksplit.length; i++) {
			networkAfterSplit1 += networksplit[i].concat("	fd	0.02	0.125	22.222	SMULDERS\n");
			networkAfterSplit2 += networksplit[i].concat("	fd	0.018	0.130	22.222	SMULDERS\n");
		}
		double inflow2 = (2600.0);
		//String pattern1 = "[0.000/"+inflowTruth+":1500.000/"+inflowTruth+":2100/"+inflowTruth/2+":3600/"+inflowTruth/2+"]";
		String pattern1 = "[0.000/"+inflowTruth/1.5+":1800/"+inflowTruth+":2400.000/"+inflowTruth+":3900/"+inflowTruth/2+":4200/"+inflowTruth/2+"]";


		//String pattern2 = "[0.000/"+inflow2+":1500.000/"+inflow2+":2100/"+inflow2/2+":3600/"+inflow2/2+"]";

		//String pattern2 = "[0.000/"+inflow2+":1200.000/"+inflow2+":2300/"+inflow2/2.1+":3600/"+inflow2/2+"]";
		String pattern2 = "[0.000/"+inflow2/1.5+":1800/"+inflow2+":2400.000/"+inflow2+":3900/"+inflow2/2+":4200/"+inflow2/2+"]";

		//String pattern2 = "[0.000/"+inflow2/1.5+"]";

		String otsimConfigurationTruth = networkAfterSplit1
				+ "TrafficClass	passengerCar_act	4.000	140.000	-6.000	0.900000	600.000\nTripPattern	numberOfTrips:	"+pattern1+"	LocationPattern:	[z1, z2]	Fractions	passengerCar_act:1.000000\nTripPatternPath	numberOfTrips:	"+pattern1+"	NodePattern:	[origin ID=1 (0.00m, 0.00m, 0.00m), destination ID=2 (3500.00m, 0.00m, 0.00m)]\nPath:	1.00000	nodes:	1	2	3\n"
				+ detectors;

		//double inflow2 = (2500.0);
		String otsimConfiguration2 = networkAfterSplit2
				+ "TrafficClass	passengerCar_act	4.000	140.000	-6.000	0.900000	600.000\nTripPattern	numberOfTrips:	"+pattern2+"	LocationPattern:	[z1, z2]	Fractions	passengerCar_act:1.000000\nTripPatternPath	numberOfTrips:	"+pattern2+"	NodePattern:	[origin ID=1 (0.00m, 0.00m, 0.00m), destination ID=2 (3500.00m, 0.00m, 0.00m)]\nPath:	1.00000	nodes:	1	2	3\n"
				+ detectors;
		//
		/*String otsimConfiguration = "EndTime:	3600.00\nSeed:	1\n"
				+ "Roadway:	0	from	10	to	8	speedlimit	130	lanes	3	vertices	(8032.358,-5.250,0.000)	(8035.027,-5.250,0.000)	(9985.293,-5.250,0.000)	(10000.000,-5.250,0.000)	ins	10	outs	8\n"
				+ "Roadway:	1	from	2	to	9	speedlimit	130	lanes	1	vertices	(9601.050,-301.400,0.000)	(9986.834,-12.062,0.000)	(10001.050,-1.400,0.000)	ins	outs	9\n"
				+ "Roadway:	2	from	1	speedlimit	130	lanes	3	vertices	(0.000,-5.250,0.000)	(7600.000,-5.250,0.000)	ins	outs	3\n"
				+ "Roadway:	3	to	11	speedlimit	130	lanes	4	vertices	(7600.000,-5.250,0.000)	(7994.873,-7.000,0.000)	(8000.000,-7.000,0.000)	ins	2	outs	10	11\n"
				+ "Roadway:	4	from	12	to	5	speedlimit	130	lanes	1	vertices	(8030.950,-25.400,0.000)	(8031.011,-25.446,0.000)	(8398.950,-301.400,0.000)	ins	11	outs\n"
				+ "Roadway:	5	from	7	speedlimit	130	lanes	4	vertices	(10000.000,-7.000,0.000)	(10003.332,-7.000,0.000)	(10400.000,-7.000,0.000)	ins	8	9	outs	6\n"
				+ "Roadway:	6	speedlimit	130	lanes	3	vertices	(10400.000,-7.000,0.000)	(13000.000,-5.250,0.000)	ins	5	outs	7\n"
				+ "Roadway:	7	to	6	speedlimit	130	lanes	2	vertices	(13000.000,-5.250,0.000)	(15000.000,-3.500,0.000)	ins	6	outs\n"
				+ "Roadway:	8	from	8	to	7	speedlimit	130	lanes	3	vertices	(9985.293,-5.350,0.000)	(9985.363,-5.350,0.000)	(9985.434,-5.350,0.000)	(9985.575,-5.350,0.000)	(9985.857,-5.350,0.000)	(9986.420,-5.350,0.000)	(9987.548,-5.350,0.000)	(9989.803,-5.350,0.000)	(9994.313,-5.350,0.000)	(9998.822,-5.350,0.000)	(10001.077,-5.350,0.000)	(10002.205,-5.350,0.000)	(10002.768,-5.350,0.000)	(10003.050,-5.350,0.000)	(10003.191,-5.350,0.000)	(10003.262,-5.350,0.000)	(10003.332,-5.350,0.000)	ins	0	outs	5\n"
				+ "Roadway:	9	from	9	to	7	speedlimit	130	lanes	1	vertices	(9986.351,-12.423,0.000)	(9989.448,-11.421,0.000)	(9992.589,-10.045,0.000)	(9995.598,-8.342,0.000)	(9996.277,-7.935,0.000)	(9997.031,-8.403,0.000)	(9999.202,-9.095,0.000)	(10000.884,-9.906,0.000)	(10003.020,-10.974,0.000)	ins	1	outs	5\n"
				+ "Roadway:	10	from	11	to	10	speedlimit	130	lanes	3	vertices	(7994.873,-5.350,0.000)	(7994.952,-5.350,0.000)	(7995.030,-5.350,0.000)	(7995.187,-5.350,0.000)	(7995.501,-5.350,0.000)	(7996.128,-5.350,0.000)	(7997.383,-5.350,0.000)	(7999.893,-5.350,0.000)	(8004.912,-5.350,0.000)	(8014.950,-5.350,0.000)	(8024.988,-5.350,0.000)	(8030.007,-5.350,0.000)	(8032.517,-5.350,0.000)	(8033.772,-5.350,0.000)	(8034.399,-5.350,0.000)	(8034.713,-5.350,0.000)	(8034.870,-5.350,0.000)	(8034.948,-5.350,0.000)	(8035.027,-5.350,0.000)	ins	3	outs	0\n"
				+ "Roadway:	11	from	11	to	12	speedlimit	130	lanes	1	vertices	(7997.186,-12.132,0.000)	(7999.595,-11.660,0.000)	(8001.638,-11.401,0.000)	(8003.688,-11.276,0.000)	(8005.755,-11.288,0.000)	(8007.847,-11.441,0.000)	(8009.972,-11.743,0.000)	(8011.315,-11.906,0.000)	(8011.974,-11.833,0.000)	(8013.252,-13.836,0.000)	(8015.773,-16.257,0.000)	(8018.451,-18.560,0.000)	(8021.216,-20.682,0.000)	(8024.059,-22.626,0.000)	(8026.973,-24.396,0.000)	(8029.859,-25.950,0.000)	(8032.725,-27.368,0.000)	ins	3	outs	4\n"
				+ "TrafficClass	PassengerCar	4.000	160.000	-6.000	0.000000	0.000\n"
				+ "TrafficClass	Truck	15.000	85.000	-6.000	0.000000	0.000\n"
				+ "TripPattern	numberOfTrips:	[0.000/4500.000000][0.000/1.000000]	LocationPattern:	[z1, z3]	Fractions	PassengerCar:0.900000	Truck:0.100000\n"
				+ "TripPatternPath	numberOfTrips:	[0.000/4500.000000][0.000/1.000000]	NodePattern:	[origin1 ID=1 (0.00m, 0.00m, 0.00m), destination1 ID=6 (15000.00m, 0.00m, 0.00m)]\n"
				+ "Path:	1.000000	nodes:	1	11a	10	8	7	6\n"
				+ "TripPattern	numberOfTrips:	[0.000/1750.000000][0.000/1.000000]	LocationPattern:	[z2, z3]	Fractions	PassengerCar:0.900000	Truck:0.100000\n"
				+ "TripPatternPath	numberOfTrips:	[0.000/1750.000000][0.000/1.000000]	NodePattern:	[origin2 ID=2 (9600.00m, -300.00m, 0.00m), destination1 ID=6 (15000.00m, 0.00m, 0.00m)]\n"
				+ "Path:	1.000000	nodes:	2	9	7	6\n"
				+ "TripPattern	numberOfTrips:	[0.000/1000.000000][0.000/1.000000]	LocationPattern:	[z1, z4]	Fractions	PassengerCar:0.900000	Truck:0.100000\n"
				+ "TripPatternPath	numberOfTrips:	[0.000/1000.000000][0.000/1.000000]	NodePattern:	[origin1 ID=1 (0.00m, 0.00m, 0.00m), destination2 ID=5 (8400.00m, -300.00m, 0.00m)]\n"
				+ "Path:	1.000000	nodes:	1	11a	12	5";*/
		//System.out.println(inflowBoundary);

		int nrSteps = 120;
		Matrix[] obsTest = TestEKF2.generateTruthData(otsimConfigurationTruth, nrSteps, 60.0);

		performExperiment(obsTest, otsimConfiguration2);

	}
	public static long[][][] performExperiment(Matrix[] obsTest, ExperimentConfiguration experimentConfiguration, String prefix) {

		run = 0;
		exportRoute = experimentConfiguration.getExportRoute(); 
		extendedOutput = experimentConfiguration.getExtendedOutput();
		int[] ind = new int[(obsTest[0].getRowDimension()-1)/30 +1];
		for (int i = 0; i< ind.length; i++) {
			ind[i] = i*30;
		}
		Matrix obs1a = obsTest[0].getMatrix(ind, 0, obsTest[0].getColumnDimension()-1);
		//int nrSteps = obs1a.getRowDimension();
		//Matrix dens = obsTest[2];

		double[] stdArray = new double[obs1a.getColumnDimension()];
		double initErrorSpeedObs = 2.25;
		double initErrorFlowObs= 0.0016;
		/*double initErrorSpeedObs = 0;
		double initErrorFlowObs= 0.000;*/
		//Arrays.fill(stdArray, 0, nrSpeedObservations, Math.sqrt(initErrorSpeedObs)); //2.5
		//Arrays.fill(stdArray, nrSpeedObservations, nrObservations, Math.sqrt(initErrorFlowObs)); //0.06
		Arrays.fill(stdArray, 0, obs1a.getColumnDimension()/2, Math.sqrt(initErrorSpeedObs)); //2.5
		Arrays.fill(stdArray, obs1a.getColumnDimension()/2, obs1a.getColumnDimension(), Math.sqrt(initErrorFlowObs)); //0.06
		int i = 0;
		r3.setSeed(24);
		long[][][] computationTime = new long[experimentConfiguration.getNetworkConfigurations().size()][experimentConfiguration.getRunConfigurations().size()][obs1a.getRowDimension()];
		for (String networkConfig: experimentConfiguration.getNetworkConfigurations()) {
			Matrix obs1 = generateWhiteNoise(obs1a,stdArray);
			int j = 0;
			r2seed = r3.nextInt();
			for (EnKFRunConfiguration runConfig: experimentConfiguration.getRunConfigurations()) {
				computationTime[i][j]= performRun(obs1, runConfig, networkConfig, obsTest, prefix, experimentConfiguration.forecastsNeeded(), i);
				System.out.println("Experiment: ("+i+","+j+") in "+computationTime[i][j][obs1a.getRowDimension()-1]+" ms");
				j++;
			}
			i++;
		}
		return computationTime;
	}
	public static void performExperiment(Matrix[] obsTest, String configExperiment) {
		ArrayList<ErrorConfiguration> list = new ArrayList<ErrorConfiguration>();
		/*list.add(new ErrorConfiguration(StateDefinition.K_CELL,0.005,1.00));
		list.add(new ErrorConfiguration(StateDefinition.INFLOW_NODE,0.05,1.01));
		list.add(new ErrorConfiguration(StateDefinition.TF_NODE,0.05,1.00));
		 */
		list.add(new ErrorConfiguration(StateDefinition.K_CELL,0.000,1.00));
		list.add(new ErrorConfiguration(StateDefinition.INFLOW_NODE,0.00,1.00));
		list.add(new ErrorConfiguration(StateDefinition.TF_NODE,0.00,1.00));

		EnKFRunConfiguration runConfig = new EnKFRunConfiguration(AssimilationMethod.DENKF, 0, list,3);

		Matrix obs1a = obsTest[0];
		//int nrSteps = obs1a.getRowDimension();
		//Matrix dens = obsTest[2];

		double[] stdArray = new double[obs1a.getColumnDimension()];
		double initErrorSpeedObs = 2.25;
		double initErrorFlowObs= 0.0016;
		//Arrays.fill(stdArray, 0, nrSpeedObservations, Math.sqrt(initErrorSpeedObs)); //2.5
		//Arrays.fill(stdArray, nrSpeedObservations, nrObservations, Math.sqrt(initErrorFlowObs)); //0.06
		Arrays.fill(stdArray, 0, obs1a.getColumnDimension()/2, Math.sqrt(initErrorSpeedObs)); //2.5
		Arrays.fill(stdArray, obs1a.getColumnDimension()/2, obs1a.getColumnDimension(), Math.sqrt(initErrorFlowObs)); //0.06


		Matrix obs1 = generateWhiteNoise(obs1a,stdArray);
		performRun(obs1, runConfig, configExperiment, obsTest, "", true, 0);





	}
	public static long[] performRun(Matrix obs1, EnKFRunConfiguration runConfig, String configExperiment, Matrix[] obsTest, String prefix, boolean forecastsNeeded, int network) {
		boolean showOutput = true;
		long startTime = System.nanoTime();
		System.out.println(runConfig);
		int nrSteps = obs1.getRowDimension();
		int nrModelSteps = obsTest[0].getRowDimension();
		int nrModelStepsPerAssStep = (nrModelSteps-1)/(nrSteps-1);
		boolean openGUI =true;
		Scheduler scheduler;
		if (openGUI) {
			if (run == 0) {
				StandAlone.main(new String[]{"GenerateEvent=SelectTab 5"});
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			scheduler = new Scheduler(MacroSimulator.simulatorType, Main.mainFrame.graphicsPanel, configExperiment);
		} else {
			scheduler = new Scheduler(MacroSimulator.simulatorType, new FakeGraphicsPanel(), configExperiment);

		}
		//		Container frame = new StandAlone();
		//        frame.add(Main.mainFrame = new Main(frame), BorderLayout.CENTER);
		//        //Main.initialized = true;
		//        Main.mainFrame.setVisible(true);
		//        Main.main(new String[]{});



		//Scheduler scheduler2 = new Scheduler(MacroSimulator.simulatorType, , configExperiment);
		//Scheduler scheduler2 = new Scheduler(MacroSimulator.simulatorType, new FakeGraphicsPanel(), otsimConfiguration);
		// Do something with the scheduler
		//System.out.println(scheduler.nextDue());

		Model macromodel = (Model) scheduler.getSimulator().getModel();

		//Model macromodel2 = (Model) scheduler2.getSimulator().getModel();

		macromodel.init();
		//macromodel2.init();
		//Main.mainFrame.actionPerformed(new ActionEvent());
		//scheduler2.stepUpTo(800);
		//System.out.println(scheduler.nextDue());
		//scheduler.deQueueEvent();
		//System.out.println(scheduler.nextDue());

		TestEnKF test = new TestEnKF();
		test.macromodel = macromodel;
		test.init(configExperiment, runConfig);
		//ArrayList<Matrix[]> results = new ArrayList<Matrix[]>();
		ArrayList<ArrayList<Matrix[]>> forecasts = new ArrayList<ArrayList<Matrix[]>>();

		ArrayList<ArrayList<MacroCell>> routes = macromodel.getRoutes().getExtendedRoutes();
		routesInt = new ArrayList<int[]>(routes.size());
		cellLengths = new ArrayList<double[]>(routes.size());
		for (ArrayList<MacroCell> route:routes) {
			LinkedHashSet<MacroCell> cells = new LinkedHashSet<MacroCell>();
			cells.addAll(route);
			double[] length = new double[cells.size()];
			for (int c = 0; c<cells.size();c++) {
				length[c] = route.get(c).l;
				if (length[c]>200) {
					System.out.println("lange cell");
				}
			}

			int[] ind = (test.config.getIndices(macromodel,cells));

			routesInt.add(ind);
			cellLengths.add(length);
		}
		cellLengths2 = new double[macromodel.getCells().size()];
		for (MacroCell c: macromodel.getCells()) {
			cellLengths2[macromodel.getCells().indexOf(c)] = c.l;
		}
		//test.update(obs1.getMatrix(0, 0,0,obs1.getColumnDimension()-1).transpose());
		for (NodeDetector nd: macromodel.getDetectors()) {
			nd.addMeasurements(0);
		}
		long[] totalTime = new long[2*nrSteps-1];
		StateDefinition[] defOut = new StateDefinition[]{StateDefinition.K_CELL, StateDefinition.V_CELL, StateDefinition.TRAFFICREGIME_CELL};
		Matrix[][] res = new Matrix[nrModelSteps][defOut.length];
		double[][] res2 = new double[nrModelSteps-1][defOut.length];
		Matrix[][] forecast = new Matrix[3600/2][defOut.length];
		//boolean extendedOutput = true;
		StateDefinition[] extDef = new StateDefinition[]{StateDefinition.K_CELL, StateDefinition.V_CELL, StateDefinition.MIN1_DETECTOR};
		Matrix[][] extOut = new Matrix[extDef.length][test.nrEnsembles];
		double[] forecastResult = null;
		for (int i = 1; i < nrSteps; i++) {
			//double time = 0;
			long t0 = System.nanoTime();
			for (int j = 1; j<=(nrModelStepsPerAssStep);j++) {
				//macromodel.getCells().get(exportRoute.get(271))
				scheduler.stepUpTo((i-1)*60.0+j*test.macromodel.dt);
				int t = (i-1)*nrModelStepsPerAssStep + j-1;
				//System.out.println("t"+t);
				//res[t] = AssimilationConfiguration.getOutput(macromodel, new StateDefinition[]{StateDefinition.K_CELL, StateDefinition.INFLOW_NODE, StateDefinition.TF_NODE});
				res[t] = AssimilationConfiguration.getOutput(macromodel, new StateDefinition[]{StateDefinition.K_CELL});

				Matrix[] indicators =  AssimilationConfiguration.getOutput(macromodel, defOut);
				res2[t] = CalcOutput.calcOutputExtended(obsTest[1].getMatrix(t, t, 0, obsTest[1].getColumnDimension()-1),obsTest[2].getMatrix(t,t, 0, obsTest[2].getColumnDimension()-1),obsTest[3].getMatrix(t,t, 0, obsTest[3].getColumnDimension()-1), indicators[0].transpose(), indicators[1].transpose(), indicators[2].transpose(), cellLengths2, 2); 
				//res2[t] = CalcOutput.calcOutputExtended(obsTest[1].getMatrix(t+1, t+1, 0, obsTest[1].getColumnDimension()-1),obsTest[2].getMatrix(t+1,t+1, 0, obsTest[2].getColumnDimension()-1),obsTest[3].getMatrix(t+1,t+1, 0, obsTest[3].getColumnDimension()-1), indicators[0].transpose(), indicators[1].transpose(), indicators[2].transpose(), cellLengths2, 2); 

				//res2[t] = CalcOutput.calcOutputExtended(obsTest[1].getMatrix(t+1, t+1, 0, obsTest[1].getColumnDimension()-1),obsTest[2].getMatrix(t+1,t+1, 0, obsTest[2].getColumnDimension()-1),obsTest[3].getMatrix(t+1,t+1, 0, obsTest[3].getColumnDimension()-1), indicators[0].transpose(), indicators[1].transpose(), indicators[2].transpose(), cellLengths2, 2); 

			}
			scheduler.getGraphicsPanel().repaint();
			if (showOutput)
				System.out.println("update main simulator = " + (System.nanoTime()- t0)/1000000);
			long t1 = System.nanoTime();
			double time = scheduler.getSimulatedTime();
			if (showOutput)
				System.out.println("time = "+time);
			/*for (Scheduler s: test.ensembles) {
				s.stepUpTo(i*60.0);
			}*/
			if (time == 240 & showOutput)
				System.out.println("time = "+time);
			MultiThreadedScheduler.predictUntil(i*60.0, test.ensembles.toArray(new Scheduler[test.ensembles.size()])); 
			if (showOutput)
				System.out.println("prediction = " + (System.nanoTime()- t1)/1000000);
			long startTime2 = System.nanoTime();
			Matrix[] output = test.update((obs1.getMatrix(i,i,0,obs1.getColumnDimension()-1)).transpose());
			long endTime2 = System.nanoTime();
			if (showOutput)
				System.out.println("correction = " + (endTime2-startTime2)/1000000);

			totalTime[i-1] = (endTime2-startTime2)/1000000;
			totalTime[nrSteps -1 + i-1] = (startTime2-t1)/1000000;
			/*if (endTime2-startTime2 > 15000)
				System.out.println("lange tijd");*/
			output[output.length-2].set(0, 0, time); 
			scheduler.getGraphicsPanel().repaint();
			//output[output.length-1].setMatrix(0,0,dens.getMatrix(i, i, 0, obs1.getColumnDimension()-1));
			//results.add(output);
			/*if (forecastsNeeded) {
				if ((i % 5.0) == 0) {
					ArrayList<Matrix[]> forecast = generateForecastValues(test,output[11],10,60*5.0);
					forecasts.add(forecast);
					//System.out.println("forecasts");
				}
			}*/
			if (forecastsNeeded) {
				if (time == 1200) {
					if (showOutput)
						System.out.println("begin forecasts");
					forecast = generateForecastValues(test,output[11],3600);
					int cells = obsTest[1].getColumnDimension();
					Matrix FXa = new Matrix((forecast.length), cells,0);
					Matrix FV = new Matrix((forecast.length), cells,0);
					Matrix FTr = new Matrix((forecast.length), cells,0);
					for (int k = 0; k< forecast.length; k++) {
						Matrix[] m1 = forecast[k];
						FXa.setMatrix(k, k, 0, cells-1,m1[0].transpose());
						FV.setMatrix(k, k, 0, cells-1,m1[1].transpose());
						FTr.setMatrix(k, k, 0, cells-1,m1[2].transpose());
						//times.add(m1[19].get(0,0));
					}
					int beginFHorizon = 1200/2;
					//int endFHorizon = beginFHorizon + forecast.length-1;
					int[] endFHorizons = new int[]{
							/*beginFHorizon + 149,
							beginFHorizon + 449,
							beginFHorizon + 899,
							beginFHorizon + 1599,*/
							beginFHorizon + 149,
							beginFHorizon + 449,
							beginFHorizon + 899,
							beginFHorizon + 1799,
					};
					forecastResult = new double[endFHorizons.length*13];
					int i1 = 0;
					for (int endFHorizon: endFHorizons) {
						double[] fr = CalcOutput.calcOutput(obsTest[1].getMatrix(beginFHorizon, endFHorizon, 0, cells-1),obsTest[2].getMatrix(beginFHorizon, endFHorizon, 0, cells-1),obsTest[3].getMatrix(beginFHorizon, endFHorizon, 0, cells-1), FXa.getMatrix(0, endFHorizon-beginFHorizon, 0, cells-1), FV.getMatrix(0, endFHorizon-beginFHorizon, 0, cells-1), FTr.getMatrix(0, endFHorizon-beginFHorizon, 0, cells-1), cellLengths2, 2); 
						System.arraycopy(fr, 0, forecastResult, i1, 13);
						i1=i1+13;
					}

					/*	for (int[] route: routesInt)
						test.exportFCtoMatlab(obsTest[1].getMatrix(beginFHorizon, endFHorizons[endFHorizons.length-1], route),FXa.getMatrix(0,FXa.getRowDimension()-1,route),"ENKF" + prefix + "_"+run+"_"+routesInt.indexOf(route),runConfig);
					 */
					if (network==0)
						test.exportFCtoMatlab(obsTest[1].getMatrix(beginFHorizon, endFHorizons[endFHorizons.length-1], 0,obsTest[1].getColumnDimension()-1),FXa,"FC" + prefix + "_"+run,runConfig);

					if (showOutput)
						System.out.println("end forecasts");

				}
			}

			/*if (extendedOutput) {
				if (time == 1800) {
					int n = 0;
					for (Scheduler s: test.ensembles) {
						int j = 0;
						for (StateDefinition sd: extDef) {
							extOut[j][n] = AssimilationConfiguration.getOutput((Model) s.getSimulator().getModel(), sd)[0];
						j++;
						}
						n++;
					}
				}
			}*/

		}

		//res[nrModelSteps-1] = new Matrix[]{obsTest[3].getMatrix(1, obsTest[3].getRowDimension()-1, 0, obsTest[3].getColumnDimension()-1),obsTest[2].getMatrix(1, obsTest[2].getRowDimension()-1, 0, obsTest[2].getColumnDimension()-1),obsTest[4].getMatrix(1, obsTest[4].getRowDimension()-1, 0, obsTest[4].getColumnDimension()-1)};
		res[nrModelSteps-1] = new Matrix[]{obsTest[1].getMatrix(0, obsTest[1].getRowDimension()-2, 0, obsTest[1].getColumnDimension()-1),obsTest[2].getMatrix(0, obsTest[2].getRowDimension()-2, 0, obsTest[2].getColumnDimension()-1),obsTest[3].getMatrix(0, obsTest[3].getRowDimension()-2, 0, obsTest[3].getColumnDimension()-1)};
		//res[nrModelSteps-1] = new Matrix[]{obsTest[1].getMatrix(1, obsTest[1].getRowDimension()-1, 0, obsTest[1].getColumnDimension()-1)};
		//res[nrModelSteps-1] = new Matrix[]{obsTest[1].getMatrix(1, obsTest[1].getRowDimension()-1, 0, obsTest[1].getColumnDimension()-1),obsTest[4].getMatrix(1, obsTest[4].getRowDimension()-1, 0, obsTest[4].getColumnDimension()-1),obsTest[5].getMatrix(1, obsTest[5].getRowDimension()-1, 0, obsTest[5].getColumnDimension()-1)};

		//results.add(obsTest);
		//System.out.println(results);



		//Random rnd = new Random(2);
		//	int[] route = new int[]{2958, 2959, 2960, 2961, 2962, 2963, 2964, 2965, 2966, 2967, 2968, 2969, 2970, 2971, 2972, 2973, 2974, 2975, 1875, 1876, 1877, 1878, 1879, 1880, 1881, 1882, 1883, 1884, 1885, 1886, 1887, 1888, 1889, 1890, 1891, 1892, 1893, 1894, 1895, 1896, 1897, 1898, 1899, 1900, 1901, 1902, 1903, 1904, 1437, 1438, 1439, 1440, 2646, 2647, 2648, 2649, 2650, 2651, 2652, 2653, 2654, 2655, 2656, 2657, 2658, 2659, 2660, 2661, 2662, 2663, 2664, 2665, 2666, 2667, 2668, 2669, 2670, 2671, 2672, 2673, 2674, 2675, 2676, 2677, 2678, 2679, 4473, 4474, 4475, 4476, 4477, 4478, 4479, 4480, 4481, 4482, 4483, 4484, 4485, 4486, 4487, 4488, 4489, 4490, 3566, 3567, 3568, 3569, 3570, 3571, 3572, 3573, 3574, 3575, 3576, 3577, 3578, 3579, 3580, 3581, 3582, 3583, 3584, 3585, 3586, 3587, 3588, 3589, 3590, 3591, 3592, 3593, 3594, 3595, 3596, 3597, 3598, 3599, 3600, 3601, 3602, 3603, 3604, 3605, 3606, 3607, 3608, 3609, 3610, 3611, 3612, 3613, 3614, 3615, 3616, 3617, 3618, 3619, 3620, 3621, 3622, 3623, 3624, 3625, 3626, 3627, 3628, 3629, 3630, 3631, 3632, 3633, 3634, 3635, 3636, 3637, 3638, 3639, 3640, 3641, 3642, 3643, 3644, 3645, 3646, 3647, 3648, 3649, 3650, 3651, 3652, 3653, 3654, 3655, 3656, 3657, 3658, 3659, 3660, 3661, 3662, 3663, 3664, 3665, 3666, 3667, 3668, 3669, 3670, 3671, 3672, 3673, 2596, 2597, 2598, 2599, 2600, 2601, 2602, 2603, 2604, 2605, 2606, 2607, 2608, 2609, 2610, 2611, 2612, 2613, 3778, 3779, 3780, 3781, 3782, 3783, 3784, 3785, 3786, 3787, 3788, 3789, 3790, 3791, 3792, 3793, 3794, 3795, 3796, 3797, 3798, 3799, 3800, 3801, 3802, 3803, 3804, 3805, 3806, 3807, 3808, 3809, 3810, 3811, 3812, 3813, 3814, 3815, 3816, 3817, 3818, 3819, 3217, 3218, 3219, 3220, 3221, 3222, 3223, 3224, 3225, 3226, 3227, 3228, 3229, 3230, 3231, 2181, 2182, 2183, 2184, 2185, 2186, 2187, 2188, 2189, 2190, 2191, 2192, 2193, 2194, 2195, 2196, 2197, 2198, 2199, 2200, 2201, 2202, 2203, 2204, 553, 554, 555, 556, 557, 558, 559, 560, 561, 562, 563, 564, 565, 566, 567, 568, 569, 570, 571, 572, 573, 574, 575, 576, 2552, 2553, 2554, 2555, 2556, 2557, 2558, 2559, 2560, 2561, 2562, 2563, 2564, 2565, 2566, 1313, 1314, 1315, 1316, 1317, 1318, 1319, 1320, 1321, 1322, 1323, 442, 443, 444, 445, 446, 447, 448, 449, 2097, 2098, 2099, 2100, 2101, 2102, 2103, 2104, 2105, 2106, 2107, 2108, 2109, 2110, 2111, 3401, 3402, 3403, 3404, 3405, 3406, 3407, 3408, 3409, 4491, 4492, 4493, 4494, 4495, 4496, 4497, 4498, 4499, 4500, 4501, 4502, 4503, 4504, 4505, 4506, 4507, 4508, 4509, 4510, 4511, 4512, 4513, 4514, 4515, 4177, 4178, 4179, 4180, 4181, 4182, 4183, 4184, 2841, 2842, 2843, 2844, 2845, 2846, 2847, 2848, 2849, 2850, 2851, 2852, 4277, 4278, 4279, 4280, 4281, 4282, 4283, 4284, 4285, 4286, 4287, 4288, 4289, 4290, 4291, 4292, 4293, 4294, 4295, 4296, 4297, 4298, 4299, 4300, 4301, 4302, 4303, 4304, 4305, 4306, 4307, 4308, 4309, 4310, 4311, 4312, 4313, 4314, 4315, 4316, 4317, 4318, 4319, 4320, 4321, 4322, 4323, 4324, 4325, 4326, 4327, 4328, 4329, 4330, 4331, 4332, 4333, 4334, 4335, 4336, 4337, 4338, 4339, 4340, 4341, 4342, 4343, 4344, 4345, 4346, 4347, 4348, 4349, 4350, 4351, 4352, 4353, 4354, 4355, 4356, 4357, 4358, 4359};

		/*
		int[] route = new int[obsTest[1].getColumnDimension()];
		for (int i=0; i< obsTest[1].getColumnDimension(); i++)
			route[i] = i;*/
		int[] route = exportRoute;
		//if (network == 0)
		//test.exportToMatlab2(res2,forecast,"ENKF" + prefix + "_"+run,true);
		//test.exportSummarizedToMatlab(results,forecasts,"SumENKF" + prefix + "_"+run,true);

		//test.exportSummarizedToMatlab2(res,forecast,1200/2,"SumENKFOld" + prefix + "_"+run,false);
		//test.exportSummarizedToMatlab2(res,null,1200/2,"SumENKFOld" + prefix + "_"+run,false);
		test.exportSummarizedResultsToMatlab(res2,forecastResult,"SumENKF" + prefix + "_"+run,forecastsNeeded,runConfig);
		//test.exportEnsemblesToMatlab(extOut,"ENKFEnsembles" + prefix + "_"+run);

		//goede export:
		if (network == 0 && extendedOutput)
			test.exportToMatlab3(res,route,"ENKF" + prefix + "_"+run, runConfig);

		//TestEKF.exportForecastsToMatlab(forecasts, "FC" + prefix + "_"+run, false);
		run++;
		long endTime = System.nanoTime();
		totalTime[2*nrSteps-2] = (endTime - startTime)/1000000;

		return totalTime;
	}
	public static Matrix[] generateTruthValues(String configuration, int nrSteps, double timestep) {
		Scheduler tmpScheduler;
		boolean openGUI = false;
		if (openGUI) {
			if (run == 0) {
				StandAlone.main(new String[]{"GenerateEvent=SelectTab 5"});
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			tmpScheduler = new Scheduler(MacroSimulator.simulatorType, Main.mainFrame.graphicsPanel, configuration);
		} else {
			tmpScheduler = new Scheduler(MacroSimulator.simulatorType, new FakeGraphicsPanel(),configuration);

		}

		//Scheduler tmpScheduler = new Scheduler(MacroSimulator.simulatorType, new FakeGraphicsPanel(), configuration);
		Model tmpMacromodel = (Model) tmpScheduler.getSimulator().getModel();
		tmpMacromodel.init();
		//StateDefinition[] defOut = new StateDefinition[]{StateDefinition.MIN1_DETECTOR, StateDefinition.K_CELL, StateDefinition.V_CELL, StateDefinition.TRAFFICREGIME_CELL};
		StateDefinition[] defOut = new StateDefinition[]{StateDefinition.MIN1_DETECTOR, StateDefinition.K_CELL, StateDefinition.V_CELL, StateDefinition.TRAFFICREGIME_CELL, StateDefinition.INFLOW_NODE, StateDefinition.TF_NODE};

		Matrix[] res = new Matrix[defOut.length];
		int nrCells = tmpMacromodel.getCells().size();
		res[0] = new Matrix(nrSteps,tmpMacromodel.getDetectors().size()*2);
		res[1] = new Matrix(nrSteps,nrCells);
		res[2] = new Matrix(nrSteps,nrCells);
		res[3] = new Matrix(nrSteps,nrCells);
		res[4] = new Matrix(nrSteps,tmpMacromodel.getInflowNodes().size());
		res[5] = new Matrix(nrSteps,tmpMacromodel.getJunctionNodes().size());
		double[] times = new double[nrSteps];
		//double time = 0;
		long bt = System.nanoTime();
		for (int j = 1; j<=(nrSteps);j++) {
			tmpScheduler.stepUpTo(j*tmpMacromodel.dt);
			if (openGUI) 
				tmpScheduler.getGraphicsPanel().repaint();
			for (int s = 0; s <defOut.length; s++) {
				/*System.out.println(res[s].getColumnDimension() +" : " +res[s].getRowDimension());
				System.out.println(AssimilationConfiguration.getOutput(tmpMacromodel, defOut[s])[0].getColumnDimension() +" : " +AssimilationConfiguration.getOutput(tmpMacromodel, defOut[s])[0].getRowDimension());
				System.out.println(j-1);
				System.out.println(res[s].getColumnDimension()-1);*/
				res[s].setMatrix(j-1,j-1, 0,res[s].getColumnDimension()-1, AssimilationConfiguration.getOutput(tmpMacromodel, defOut[s])[0].transpose());

			}
			if ((j % 100) == 0) {
				System.out.println("time " + (System.nanoTime()-bt));
				bt = System.nanoTime();
			}
			times[j-1] = j*tmpMacromodel.dt;
		}
		return res;
	}
	public static Matrix[][] generateForecastValues(TestEnKF test, Matrix newState, double timeinterval) {
		Scheduler scheduler = test.ensembles.get(0);
		String config = "Offset:	-"+scheduler.getSimulatedTime()+"\n"+scheduler.getConfiguration();
		Scheduler sch = new Scheduler(MacroSimulator.simulatorType, new FakeGraphicsPanel(),  config);
		Model modelScheduler = (Model) scheduler.getSimulator().getModel();
		Model modelSch = (Model) sch.getSimulator().getModel();
		modelSch.init();
		double[] state= test.config.saveStateToArray(modelScheduler);
		//System.out.println("state: "+Arrays.toString(state));
		//System.out.println("state2: "+Arrays.toString((newState.transpose()).getArray()[0]));
		//System.out.println("state3: "+Arrays.toString(test.config.saveStateToArray(modelSch)));
		//System.out.println("inflows: "+Arrays.toString(inflow));
		test.config.restoreState((newState.transpose()).getArray()[0], modelSch);
		//System.out.println("state3: "+Arrays.toString(test.config.saveStateToArray(modelSch)));

		for (NodeDetector nd: modelSch.getDetectors()) {
			nd.addMeasurements(0);
		}	
		int nrSteps = (int) (timeinterval/modelSch.dt);
		StateDefinition[] defOut = new StateDefinition[]{StateDefinition.K_CELL, StateDefinition.V_CELL, StateDefinition.TRAFFICREGIME_CELL};
		Matrix[][] res = new Matrix[(int) (nrSteps)][defOut.length];

		//double time = 0;
		for (int j = 1; j<=(nrSteps);j++) {
			sch.stepUpTo(j*test.macromodel.dt);
			res[j-1] = AssimilationConfiguration.getOutput(modelSch, defOut);
		}
		return res;
	}	
	public static ArrayList<Matrix[]> generateForecastValues(TestEnKF test, Matrix newState, int nrSteps, double timestep) {
		Scheduler scheduler = test.ensembles.get(0);
		String config = "Offset:	-"+scheduler.getSimulatedTime()+"\n"+scheduler.getConfiguration();
		Scheduler sch = new Scheduler(MacroSimulator.simulatorType, new FakeGraphicsPanel(),  config);
		Model modelScheduler = (Model) scheduler.getSimulator().getModel();
		Model modelSch = (Model) sch.getSimulator().getModel();
		modelSch.init();
		double[] state= test.config.saveStateToArray(modelScheduler);
		//System.out.println("state: "+Arrays.toString(state));
		//System.out.println("state2: "+Arrays.toString((newState.transpose()).getArray()[0]));
		//System.out.println("state3: "+Arrays.toString(test.config.saveStateToArray(modelSch)));
		//System.out.println("inflows: "+Arrays.toString(inflow));
		test.config.restoreState((newState.transpose()).getArray()[0], modelSch);
		//System.out.println("state3: "+Arrays.toString(test.config.saveStateToArray(modelSch)));

		for (NodeDetector nd: modelSch.getDetectors()) {
			nd.addMeasurements(0);
		}

		return TestEKF2.generateTruthData(sch, modelSch, nrSteps, timestep, scheduler.getSimulatedTime());
	}
	//public void init()
	public void init(String configuration2) {
		//throw new Error("Not to be used!");
		ArrayList<ErrorConfiguration> list = new ArrayList<ErrorConfiguration>();
		/*list.add(new ErrorConfiguration(StateDefinition.K_CELL,0.005,1.00));
		list.add(new ErrorConfiguration(StateDefinition.INFLOW_NODE,0.05,1.01));
		list.add(new ErrorConfiguration(StateDefinition.TF_NODE,0.05,1.00));
		 */
		list.add(new ErrorConfiguration(StateDefinition.K_CELL,0.000,1.00));
		list.add(new ErrorConfiguration(StateDefinition.INFLOW_NODE,0.00,1.00));
		list.add(new ErrorConfiguration(StateDefinition.TF_NODE,0.00,1.00));

		EnKFRunConfiguration runConfig = new EnKFRunConfiguration(AssimilationMethod.DENKF, 0, list,3);

		init(configuration2, runConfig);
	}
	public void init(String configuration2, EnKFRunConfiguration runConfig) {
		nrCells = macromodel.getCells().size();
		nrLinks = macromodel.getLinks().size();
		locDetSpeed = new ArrayList<MacroCell>();
		locDetFlow = new ArrayList<MacroCell>();
		detectors = macromodel.getDetectors();
		LinkedHashSet<NodeDetector> lhsdetectors = new LinkedHashSet<NodeDetector>();
		lhsdetectors.addAll(detectors);
		surroundingCellsMap = new HashMap<MacroCell, LinkedHashSet<MacroCell>>();
		HashMap<MacroCell, LinkedHashSet<MacroCell>> surroundingCellsExtendedMap = new HashMap<MacroCell, LinkedHashSet<MacroCell>>();
		surroundingCellsMap2 = new HashMap<NodeDetector, LinkedHashSet<Object>>();
		surroundingObservationsMap = new HashMap<Object, LinkedHashSet<NodeDetector>>();
		HashMap<Object, LinkedHashSet<NodeDetector>> surroundingObservationsExtendedMap = new HashMap<Object, LinkedHashSet<NodeDetector>>();
		//int[] indicesCellsWithDetectors;
		cellsWithDetectors = new LinkedHashSet<MacroCell>();

		for (NodeDetector n: detectors) {

			locDetSpeed.add(n.getClosestCell());
			locDetFlow.add(n.getClosestCell());


		}

		nrSpeedObservations = locDetSpeed.size();
		nrFlowObservations = locDetFlow.size();
		nrObservations = nrSpeedObservations + nrFlowObservations;
		switch (runConfig.getAssimilationMethod().getType()) {
		case GLOBAL: break;
		case LOCAL: 

			int nrSurroundingCells = runConfig.getNrSurroundingCells();
			int inflowTFWidth = runConfig.getInflowTFWidth();
			// init
			for (MacroCell m: macromodel.getCells()) {
				surroundingObservationsMap.put(m, new LinkedHashSet<NodeDetector>());
				surroundingObservationsExtendedMap.put(m, new LinkedHashSet<NodeDetector>());
			}
			//for (Object o: stateVariables) {
			//	surroundingObservationsMap.put(m, new LinkedHashSet<NodeDetector>());
			//}
			for (NodeDetector n: detectors) {
				cellsWithDetectors.add(n.getClosestCell());
			}
			for (MacroCell m: cellsWithDetectors) {

				surroundingCellsMap.put(m, returnSurroundingCellsIncludingDiverges(m,nrSurroundingCells));
				surroundingCellsExtendedMap.put(m, returnSurroundingCellsIncludingDiverges(m,inflowTFWidth));




			}

			for (NodeDetector n: detectors) {
				surroundingCellsMap2.put(n, new LinkedHashSet<Object>());
				//cellsWithDetectors.add(n.getClosestCell());
				LinkedHashSet<MacroCell> cells = surroundingCellsMap.get(n.getClosestCell());
				for (MacroCell c: cells) {
					surroundingObservationsMap.get(c).add(n);
				}
				LinkedHashSet<MacroCell> cellsExt = surroundingCellsExtendedMap.get(n.getClosestCell());
				for (MacroCell c: cellsExt) {
					surroundingObservationsExtendedMap.get(c).add(n);
				}
			}
			for (NodeBoundaryIn n: macromodel.getInflowNodes()) {
				for (MacroCell c: n.cellsOut) {
					surroundingObservationsMap.put(n, surroundingObservationsExtendedMap.get(c));
					//surroundingObservationsMap.put(n, lhsdetectors);
				}
			}
			for (NodeInterior n: macromodel.getJunctionNodes()) {
				for (MacroCell c: n.cellsOut) {
					surroundingObservationsMap.put(n, surroundingObservationsExtendedMap.get(c));
					//surroundingObservationsMap.put(n, lhsdetectors);
				}
			}
			for (Link l: macromodel.getLinks()) {
				surroundingObservationsMap.put(l, new LinkedHashSet<NodeDetector>());
				for (MacroCell c: l.correspondingCells) {
					surroundingObservationsMap.put(l, surroundingObservationsExtendedMap.get(c));
					//surroundingObservationsMap.get(l).addAll(surroundingObservationsMap.get(c));
				}
			}
			for (Object o: surroundingObservationsMap.keySet()) {
				for (NodeDetector n: surroundingObservationsMap.get(o)) {
					surroundingCellsMap2.get(n).add(o);
				}
			}




		}
		//surroundingObservationsMap.




		nrEnsembles =runConfig.getEnsembleSize();
		ArrayList<ErrorConfiguration> list = runConfig.getErrorConfigurations();
		/*list.add(new ErrorConfiguration(StateDefinition.K_CELL,0.005,1.00));
		list.add(new ErrorConfiguration(StateDefinition.INFLOW_NODE,0.05,1.01));
		list.add(new ErrorConfiguration(StateDefinition.TF_NODE,0.05,1.00));

		list.add(new ErrorConfiguration(StateDefinition.K_CELL,0.000,1.00));
		list.add(new ErrorConfiguration(StateDefinition.INFLOW_NODE,0.00,1.00));
		list.add(new ErrorConfiguration(StateDefinition.TF_NODE,0.00,1.00));

		list.add(new ErrorConfiguration(StateDefinition.K_CELL,0,1.3));
		list.add(new ErrorConfiguration(StateDefinition.INFLOW_NODE,0,1.4));
		//list.add(new ErrorConfiguration(StateDefinition.KCRI_LINK,0.001,1.01));
		//list.add(new ErrorConfiguration(StateDefinition.VCRI_LINK,1.0,1.01));
		//list.add(new ErrorConfiguration(StateDefinition.KJAM_LINK,0.005,1.01));
		 */		

		//list.add(new ErrorConfiguration(StateDefinition.KCRI_CELL,0.001));

		ErrorConfiguration[] list2 = new ErrorConfiguration[list.size()];
		list2 = (ErrorConfiguration[]) list.toArray(list2);


		//String[] c = new String[]{"density","inflow","speedLimitPerLink"};
		this.config = new AssimilationConfiguration(list2, macromodel, runConfig.getAssimilationMethod());
		stateVariables = config.getStateVariables(macromodel);


		r2.setSeed(r2seed);
		r.setSeed(r2.nextInt());


		for (int i = 0; i<nrEnsembles; i++) {
			ensembles.add(new Scheduler(MacroSimulator.simulatorType, new FakeGraphicsPanel(), configuration2));
			Model m = (Model) ensembles.get(i).getSimulator().getModel();

			Matrix rnd = new Matrix(1,config.nrStateVariables,0);
			/*double[] er = new double[config.nrStateVariables];
			double errDens = 0.01;
			double errInflow = 0.2;
			double errSpeedLimit = 1;
			Arrays.fill(er, 0,nrCells,errDens);
			Arrays.fill(er, nrCells,nrCells+nrLinks,errSpeedLimit);
			Arrays.fill(er, nrCells+nrLinks,config.nrStateVariables,errInflow);*/
			double[] er = this.config.getInitialErrorArray();

			rnd = generateWhiteNoise(rnd, er);
			rnd = rnd.plus(new Matrix(config.saveStateToArray(m),1));
			double[] rnd2 = rnd.getArrayCopy()[0];

			config.restoreState(rnd2, (m));
			for (NodeDetector nd: m.getDetectors()) {
				nd.addMeasurements(0);
			}
		}
		correspondingIndicesOfDetectors = new ArrayList<Integer[]>();
		if (runConfig.getAssimilationMethod().getType() == AssimilationMethodType.LOCAL) {
			for (int i = 0; i<nrObservations; i++) {
				NodeDetector detector;
				if (i < nrSpeedObservations) {
					detector = detectors.get(i);
					//indices = config.getIndicesObject(macromodel, surroundingCellsMap2.get(detectors.get(i1)));
				} else {
					detector = detectors.get(i-nrSpeedObservations);
					//indices = config.getIndicesObject(macromodel, surroundingCellsMap2.get(detectors.get(i1-nrSpeedObservations)));

				}
				int[] indices = config.getIndicesObject(macromodel, surroundingCellsMap2.get(detector));
				//List<Integer[]> integerList = new ArrayList<Integer[]>();
				//integerList.addAll(Arrays.asList(indices));
				correspondingIndicesOfDetectors.add(buildIntArray(indices));
				
			}
			for (Object o: stateVariables) {
				int[] indices = config.getIndicesDetector(macromodel, surroundingObservationsMap.get(o));
				correspondingIndicesOfStateObjects.add(buildIntArray(indices));
			}
			detectorIndices1 = config.getIndices(macromodel, cellsWithDetectors);
			int index = 0;
			affectedDetectorsIndices = new ArrayList<ArrayList<Integer>>();
			for (int i = 0; i<detectors.size(); i++) {
				affectedDetectorsIndices.add(new ArrayList<Integer>());
			}
			for (MacroCell c: cellsWithDetectors) {
				
				LinkedHashSet<NodeDetector> dets = surroundingObservationsMap.get(c);
				for (NodeDetector d: dets) {
					ArrayList<Integer> tmpList = affectedDetectorsIndices.get(detectors.indexOf(d));
					tmpList.add(index);
					//tmpList.add(index+cellsWithDetectors.size());
					/*ArrayList<Integer> tmpList2 = affectedDetectorsIndices.get(detectors.indexOf(d)+cellsWithDetectors.size());
					tmpList2.add(index);
					tmpList2.add(index+cellsWithDetectors.size());*/
				}
				index++;
				
			}
			
			Schur = new Matrix(config.nrStateVariables,nrObservations);
			
			for (int tmpi = 0; tmpi<nrObservations; tmpi++) {
				int[] indices = buildIntArray(correspondingIndicesOfDetectors.get(tmpi));
				Schur.setMatrix(indices, tmpi,tmpi, new Matrix(indices.length,1,1.0));
				
			}
			System.out.println("test");
			SparseSchur = new SparseMatrixDirectRS(Schur.getArray());
		}
	}
	public Matrix[] update(Matrix observations) {
		boolean showOutput = false;

		int N = nrEnsembles;
		Matrix D = new Matrix(observations.getRowDimension(),N);

		double[] stdArray = new double[nrObservations];
		double initErrorSpeedObs = 2.25;
		double initErrorFlowObs= 0.0016;
		//double initErrorSpeedObs = Double.MAX_VALUE;
		//double initErrorFlowObs= Double.MAX_VALUE;
		//Arrays.fill(stdArray, 0, nrSpeedObservations, Math.sqrt(initErrorSpeedObs)); //2.5
		//Arrays.fill(stdArray, nrSpeedObservations, nrObservations, Math.sqrt(initErrorFlowObs)); //0.06
		Arrays.fill(stdArray, 0, nrSpeedObservations, Math.sqrt(initErrorSpeedObs)); //2.5
		Arrays.fill(stdArray, nrSpeedObservations, nrObservations, Math.sqrt(initErrorFlowObs)); //0.06

		//Matrix.identity(3, 3).

		Matrix R = new Matrix(nrObservations, nrObservations);
		//double initErrorSpeedObs = 2.5*2.5;
		//double initErrorFlowObs= 0.06*0.06;
		R.setMatrix(0,nrSpeedObservations-1,0,nrSpeedObservations-1,Matrix.identity(nrSpeedObservations, nrSpeedObservations).times(initErrorSpeedObs));
		R.setMatrix(nrSpeedObservations,nrObservations-1,nrSpeedObservations,nrObservations-1,Matrix.identity(nrFlowObservations, nrFlowObservations).times(initErrorFlowObs));
		Matrix Rinv = new Matrix(nrObservations, nrObservations);
		if (initErrorSpeedObs == Double.MAX_VALUE) {
			Rinv.setMatrix(0,nrSpeedObservations-1,0,nrSpeedObservations-1,Matrix.identity(nrSpeedObservations, nrSpeedObservations).times(0));
		} else {
			Rinv.setMatrix(0,nrSpeedObservations-1,0,nrSpeedObservations-1,Matrix.identity(nrSpeedObservations, nrSpeedObservations).times(1/initErrorSpeedObs));
		}
		if (initErrorFlowObs == Double.MAX_VALUE) {
			Rinv.setMatrix(nrSpeedObservations,nrObservations-1,nrSpeedObservations,nrObservations-1,Matrix.identity(nrFlowObservations, nrFlowObservations).times(0));
		} else {
			Rinv.setMatrix(nrSpeedObservations,nrObservations-1,nrSpeedObservations,nrObservations-1,Matrix.identity(nrFlowObservations, nrFlowObservations).times(1/initErrorFlowObs));
		}

		

		Matrix obsNoised;
		r.setSeed(r2.nextInt());

		for (int i = 0; i<N; i++) {
			obsNoised = generateWhiteNoise(observations, stdArray);
			D.setMatrix(0, nrObservations-1, i, i, obsNoised);
		}
		Matrix D1 = observations.minus(D.times(new Matrix(N,1,1.0/N)));
		D = D.plus(D1.times(new Matrix(1,N,1)));
		//Matrix D3 = D.minus(D2);
		Matrix X = new Matrix(config.nrStateVariables, N);
		;
		Matrix HX = new Matrix(nrObservations, N);
		for (Scheduler s: ensembles){
			Model m = (Model) s.getSimulator().getModel();
			//double[] x = m.saveStateToArray("density");
			double[] x = config.saveStateToArray(m);
			for (double i: x) {
				if (i == 0) {
					//System.out.println("stop");
					//x = config.saveStateToArray(m);
				}
			}
			double[][] y = new double[][]{x};
			//double[0][] res = x;
			X.setMatrix(0,x.length-1,ensembles.indexOf(s),ensembles.indexOf(s), (new Matrix(new double[][]{x})).transpose());
			Matrix y1 = new Matrix(nrObservations,1);
			for (NodeDetector nd: m.getDetectors() ) {
				double[] obs= nd.getMeasurements(m.t()-60, m.t(), m.dt);
				//double[] obs= nd.getInstantMeasurements();
				y1.set(m.getDetectors().indexOf(nd),0, obs[1]);
				y1.set(nrSpeedObservations + m.getDetectors().indexOf(nd),0, obs[0]);
			}
			HX.setMatrix(0,nrObservations-1,ensembles.indexOf(s),ensembles.indexOf(s),y1);

		}




		Matrix A1 = X.times(new Matrix(N,1,1.0));
		Matrix A2 = A1.times(new Matrix(1,N,1.0));
		Matrix A3 = A2.times(1.0/N);
		Matrix A = X.minus(A3);


		Matrix HA = new Matrix(nrObservations, N);
		//int[] range = new int[]{0,nrObservations-1};
		Matrix gemH = new Matrix(nrObservations, 1);
		Matrix gemX = new Matrix(config.nrStateVariables,1);
		for (int j = 0; j<N; j++) {
			Matrix tmp = HX.getMatrix(0,nrObservations-1, j,j);
			Matrix tmp2 = X.getMatrix(0,config.nrStateVariables-1, j,j);
			gemH = gemH.plus(tmp);
			gemX = gemX.plus(tmp2);
		}
		gemH = gemH.times(1.0/N);
		gemX = gemX.times(1.0/N);
		Matrix tmpH2 = new Matrix(nrObservations, 1);
		Matrix tmpX2 = new Matrix(config.nrStateVariables, 1);
		Matrix varH = new Matrix(nrObservations, 1);
		Matrix varX = new Matrix(config.nrStateVariables,1);
		Matrix stdH = new Matrix(nrObservations, 1);
		Matrix stdX = new Matrix(config.nrStateVariables,1);
		for (int j = 0; j<N; j++) {
			Matrix tmpH = (HX.getMatrix(0,nrObservations-1, j,j)).minus(gemH);
			Matrix tmpX = (X.getMatrix(0,config.nrStateVariables-1, j,j)).minus(gemX);
			tmpH2 = tmpH2.plus(tmpH.arrayTimes(tmpH));
			tmpX2 = tmpX2.plus(tmpX.arrayTimes(tmpX));
		}

		varH = tmpH2.times(1.0/(N-1));
		varX = tmpX2.times(1.0/(N-1));
		for (int j=0; j<nrObservations; j++) {
			stdH.set(j, 0, Math.sqrt(varH.get(j, 0)));
		}
		for (int j=0; j<config.nrStateVariables; j++) {
			stdX.set(j, 0, Math.sqrt(varX.get(j, 0)));
		}
		//Matrix HA2 = new Matrix(nrObservations, 1);
		for (int i = 0; i<N; i++) {


			HA.setMatrix(0,nrObservations-1, i, i, HX.getMatrix(0,nrObservations-1,i,i).minus(gemH));
		}
		/*double[] inflowBefore = gemX.getMatrix(4645,4693, 0,0).transpose().getArray()[0];
		System.out.println("Before: "+Arrays.toString(inflowBefore));
		 */
		Matrix Y = null;
		Matrix P = null;
		CholeskyDecomposition L;
		Matrix M = null;
		Matrix Z = null;
		Matrix diff;
		Matrix Xa = null;
		/*ArrayList<Object> objects1;
		objects1 = config.getStateVariables(macromodel);
		Matrix ob= new Matrix(objects1.size(),nrObservations,0);
		int n=0;
		for (Object o: objects1) {
			int[] ind = config.getIndicesDetector(macromodel, surroundingObservationsMap.get(o));
			ob.setMatrix(n,n,ind,new Matrix(1,ind.length,1));
			n++;
		}*/

		switch (config.assimilationMethod) {
		case DENKF: 


			//boolean Denkf = true;
			//if (Denkf) {
			/*System.out.println(X);
			Matrix Y = D.minus(HX);
			Matrix P = R.plus((HA.times(HA.transpose())).times(1.0/(N-1)));
			CholeskyDecomposition L = P.chol();
			Matrix M = L.solve(Y);
			Matrix Z = (HA.transpose()).times(M);
			Matrix diff = (A.times(Z)).times(1.0/(N-1));
			Matrix Xa = X.plus(diff);*/
			//System.out.println(X);
			Matrix D2 = observations.times(new Matrix(1,N,1.0));
			Y = observations.minus(gemH);
			/*P = R.plus((HA.times(HA.transpose())).times(1.0/(N-1)));
			L = P.chol();
			M = L.solve(Y);
			Z = (HA.transpose()).times(M);
			//diff = (A.times(Z)).times(1.0/(N-1));
			 */

			//Matrix M2 = L.solve(Matrix.identity(nrObservations, nrObservations));

			/*	
			long td1 = System.nanoTime();
			Matrix M2 = solveInversePStraightForward(R,HA,N, Matrix.identity(nrObservations, nrObservations));
			System.out.println("Inverse: " + (System.nanoTime()-td1)/1000000);


			Matrix K = (A.times((HA.transpose()).times(M2))).times(1.0/(N-1));
			Matrix diff2 = K.times(Y);

			Matrix Xa2 = gemX.plus(diff2);

			Matrix Xa4 = X.plus((K.times(0.5)).times(D2.minus(HX)));
			Matrix gemXa4 = Xa4.times(new Matrix(N,1,1.0/N));

			//Matrix KHA = K.times(HA);
			//Matrix Aa = A.minus(K.times(HA).times(0.5));
			Xa = Xa4.plus((Xa2.minus(gemXa4).times(new Matrix(1,N,1.0))));;*/

			long td2 = System.nanoTime();
			//Matrix M2a = solveInversePStraightForward(R,HA,N, Y);
			//Matrix M2b = solveInversePStraightForward(R,HA,N, D2.minus(HX));
			Matrix[] M2a =  solveInversePStraightForwardMult(R,HA,N, Y,D2.minus(HX));


			if (showOutput)
				System.out.println("Inverse: " + (System.nanoTime()-td2)/1000000);
			Matrix diff2a = (A.times((HA.transpose()).times(M2a[0]))).times(1.0/(N-1)); 
			Matrix Xa2a = gemX.plus(diff2a);
			long td3 = System.nanoTime();

			if (showOutput)
				System.out.println("Inverse: " + (System.nanoTime()-td3)/1000000);
			Matrix Xa4a = X.plus(((A.times((HA.transpose()).times(M2a[1]))).times(1.0/(N-1)).times(0.5)));
			Matrix gemXa4a = Xa4a.times(new Matrix(N,1,1.0/N));
			Xa = Xa4a.plus((Xa2a.minus(gemXa4a).times(new Matrix(1,N,1.0))));;

			//Xatest.plus((gemXa.minus(gemXatest).times(new Matrix(1,N,1.0))));
			//System.out.println("t");




			//} 
			break;
		case DENKF_SMW: 

			Matrix D22 = observations.times(new Matrix(1,N,1.0));
			Y = observations.minus(gemH);
			//Matrix 
			/*Matrix T = JamaExtension.diagTimesRight((HA.transpose()),Rinv);

			Matrix Q = JamaExtension.plusIdentity((T.times(HA)).times(1.0/(N-1)));

			L = Q.chol();
			if (!L.isSPD()) {
				System.out.println("not SPD");
				Matrix q = Q.minus(Q.transpose());
				Q = (Q.minus(q.times(0.5)));
				L=Q.chol();
			}

			//L = q2.chol();

			//CholeskyDecomposition L2 = Q2.chol();;;


			Matrix W = (L.solve(Matrix.identity(N, N))).times(T);


			M = JamaExtension.diagTimes(Rinv, JamaExtension.plusIdentity((HA.times(W)).times(-1.0/(N-1))));
			 */
			long t11 = System.nanoTime();
			//M = solveInversePShermanMorrisonWoodbury(Rinv, HA, N, Matrix.identity(nrObservations, nrObservations));
			Matrix[] Mdsmw = solveInversePShermanMorrisonWoodburyMult2(Rinv, HA, N, Y,D22.minus(HX));


			if (showOutput)
				System.out.println("Inverse: " + (System.nanoTime()-t11)/1000000);
			Z = (HA.transpose()).times(Mdsmw[0]);
			//Matrix M2 = L.solve(Matrix.identity(nrObservations, nrObservations));
			//Matrix Kd = (A.times(Z)).times(1.0/(N-1));
			Matrix diff2d =  (A.times(Z)).times(1.0/(N-1));
			Matrix Xa2d = gemX.plus(diff2d);

			//Matrix Xa4d = X.plus((Kd.times(0.5)).times(D22.minus(HX)));
			Matrix Xa4d = X.plus((A.times((HA.transpose().times(Mdsmw[1].times(0.5))))).times(1.0/(N-1)));
			Matrix gemXa4d = Xa4d.times(new Matrix(N,1,1.0/N));

			Xa = Xa4d.plus((Xa2d.minus(gemXa4d).times(new Matrix(1,N,1.0))));;

			/*long t11 = System.nanoTime();
			M = solveInversePShermanMorrisonWoodbury(Rinv, HA, N, Y);
			System.out.println("Inverse: " + (System.nanoTime()-t11)/1000000);
			Z = (HA.transpose()).times(M);
			//Matrix M2 = L.solve(Matrix.identity(nrObservations, nrObservations));
			Matrix Kd = (A.times(Z)).times(1.0/(N-1));
			Matrix diff2d = Kd;
			Matrix Xa2d = gemX.plus(diff2d);
			Matrix MT =solveInversePShermanMorrisonWoodbury(Rinv, HA, N, D22.minus(HX)); 
			Matrix Xa4d = X.plus(((A.times((HA.transpose()).times(MT))).times(1.0/(N-1)).times(0.5)));
			Matrix gemXa4d = Xa4d.times(new Matrix(N,1,1.0/N));

			Xa = Xa4d.plus((Xa2d.minus(gemXa4d).times(new Matrix(1,N,1.0))));;*/


			//} 
			break;
		case ENKF:
			//boolean localized = true; 
			//if (!localized) {
			/*System.out.println(X);
		Matrix Y = D.minus(HX);
		Matrix P = R.plus((HA.times(HA.transpose())).times(1.0/(N-1)));
		CholeskyDecomposition L = P.chol();
		Matrix M = L.solve(Y);
		Matrix Z = (HA.transpose()).times(M);
		Matrix diff = (A.times(Z)).times(1.0/(N-1));
		Matrix Xa = X.plus(diff);*/
			//System.out.println(X);
			Y = D.minus(HX);
			/*P = R.plus((HA.times(HA.transpose())).times(1.0/(N-1)));
			L = P.chol();
			M = L.solve(Y);*/
			long tl11 = System.nanoTime();
			M = solveInversePStraightForward(R,HA,N,Y);
			if (showOutput)
				System.out.println("Inverse: " + (System.nanoTime()-tl11)/1000000);
			Z = (HA.transpose()).times(M);
			/*int nrExtra = 100;

			Matrix Ainf = new Matrix(A.getRowDimension()+nrExtra , A.getColumnDimension());
			Ainf.setMatrix(0, A.getRowDimension()-1, 0, A.getColumnDimension()-1, A);
			Matrix Ainf2 = TestEnKF.generateWhiteNoise(new Matrix(nrExtra,A.getColumnDimension()),1.0);
			Matrix gemAinf = Ainf2.times(new Matrix(N,1,1.0/N)).times(new Matrix(1,N,1.0));
			Ainf2 = Ainf2.minus(gemAinf);
			Matrix varAinf = (Ainf2.arrayTimes(Ainf2)).times(new Matrix(N,1,1.0/(N-1)));

			for (int i=0; i<varAinf.getRowDimension(); i++) {
				varAinf.set(i, 0, 1.0/Math.sqrt(varAinf.get(i, 0)));
			}
			Ainf2.arrayTimesEquals(varAinf.times(new Matrix(1,N,1.0)));
			System.out.println(A.getRowDimension()+", "+(A.getRowDimension()+nrExtra-1)+", 0, "+ (A.getColumnDimension()-1));
			Ainf.setMatrix(A.getRowDimension(), A.getRowDimension()+nrExtra-1, 0, A.getColumnDimension()-1, Ainf2);


			 */		diff = (A.times(Z)).times(1.0/(N-1));
			 /*Matrix diff3 = (Ainf.times(Z)).times(1.0/(N-1));
			Matrix infl = diff3.getMatrix(A.getRowDimension(), A.getRowDimension()+nrExtra-1 ,0,N-1);
			Matrix geminfl = infl.times(new Matrix(N,1,1.0/N)).times(new Matrix(1,N,1.0));
			Matrix infl2 = infl.minus(geminfl);
			Matrix varinfl = (infl2.arrayTimes(infl2)).times(new Matrix(N,1,1.0/(N-1)));
			Matrix stdinfl = varinfl.copy();
			for (int i=0; i<varinfl.getRowDimension(); i++) {
				stdinfl.set(i, 0, Math.sqrt(varinfl.get(i, 0)));
			}
			Matrix infl3 = (new Matrix(1,nrExtra,1.0/nrExtra)).times(stdinfl);
			if (nr >2) 
			ri = 1.0/infl3.get(0,0);
			else
				ri = 1.05;

			nr++;
			System.out.println("r:"+ri);
			  */			
			 //Matrix M2 = L.solve(Matrix.identity(nrObservations, nrObservations));
			 // Matrix K = (A.times((HA.transpose()).times(M2))).times(1.0/(N-1));
			 // Matrix diff2 = K.times(Y);
			 Xa = X.plus(diff);

			 break;
		case ENKF_SMW:

			Y = D.minus(HX);
			/*Matrix Te = JamaExtension.diagTimesRight((HA.transpose()),Rinv);
			Matrix Qe = JamaExtension.plusIdentity((Te.times(HA)).times(1.0/(N-1)));
			L = Qe.chol();
			if (!L.isSPD()) {
				System.out.println("not SPD");
				Matrix q = Qe.minus(Qe.transpose());
				Qe = (Qe.minus(q.times(0.5)));
				L=Qe.chol();
			}
			Matrix We = (L.solve(Te.times(Y)));
			M = JamaExtension.diagTimes(Rinv, JamaExtension.plusIdentity((HA.times(We)).times(-1.0/(N-1))));
			 */			long ts11 = System.nanoTime();
			 //M = solveInversePShermanMorrisonWoodbury(Rinv,HA,N,Matrix.identity(nrObservations, nrObservations));
			 M = solveInversePShermanMorrisonWoodbury2(Rinv,HA,N,Y);
			 if (showOutput)
				 System.out.println("Inverse: " + (System.nanoTime()-ts11)/1000000);
			 // Z = (HA.transpose()).times(M.times(Y));
			 Z = (HA.transpose()).times(M);
			 diff = (A.times(Z)).times(1.0/(N-1));
			

			 Xa = X.plus(diff);


			 break;
		case ENKF_SCHUR:

			
			Y = D.minus(HX);
		
			M = solveInversePStraightForward(R,HA,N,Y);
			
			//Z = (A.times(HA.transpose())).times(M).times(1.0/(N-1));;
			//Z = JamaExtension.sparseMatrixSchurMultiplication(Schur, A.times(HA.transpose()));
			 Z = JamaExtension.arrayTimes(SparseSchur , A.times(HA.transpose())).toMatrix();
			diff = (Z.times(M)).times(1.0/(N-1));
			//diff = (Schur.arrayTimes(A.times(HA.transpose())).times(M)).times(1.0/(N-1));
			
			 Xa = X.plus(diff);
			break;
		case ENKF_SCHUR_SMW:

			Y = D.minus(HX);
			/*Matrix Te = JamaExtension.diagTimesRight((HA.transpose()),Rinv);
			Matrix Qe = JamaExtension.plusIdentity((Te.times(HA)).times(1.0/(N-1)));
			L = Qe.chol();
			if (!L.isSPD()) {
				System.out.println("not SPD");
				Matrix q = Qe.minus(Qe.transpose());
				Qe = (Qe.minus(q.times(0.5)));
				L=Qe.chol();
			}
			Matrix We = (L.solve(Te.times(Y)));
			M = JamaExtension.diagTimes(Rinv, JamaExtension.plusIdentity((HA.times(We)).times(-1.0/(N-1))));
			 */		
			 //M = solveInversePShermanMorrisonWoodbury(Rinv,HA,N,Matrix.identity(nrObservations, nrObservations));
			 M = solveInversePShermanMorrisonWoodbury2(Rinv,HA,N,Y);
			
			 // Z = (HA.transpose()).times(M.times(Y));
			 //Z = ;
			 //diff = (Schur.arrayTimes(A.times((HA.transpose())))).times(M).times(1.0/(N-1));
			// Z = JamaExtension.sparseMatrixSchurMultiplication(Schur, A.times(HA.transpose()));
			 Z = JamaExtension.arrayTimes(SparseSchur , A.times(HA.transpose())).toMatrix();
				diff = (Z.times(M)).times(1.0/(N-1));
			 Xa =X.plus(diff);


			 break;
		case LENKF_MEASUREMENT:
			// per measurement

			Matrix backupHX = HX.copy();
			Matrix backupX = X.copy();
			Matrix backupA = A.copy();
			Matrix backupHA = HA.copy();
			int[] detectorIndices = config.getIndices(macromodel, cellsWithDetectors);
			for (int i1 = 0; i1 < nrObservations; i1++) {
				Matrix HX2 = HX.getMatrix(i1, i1, 0, N-1);
				Matrix D21 = D.getMatrix(i1, i1, 0, N-1);
				double R2 = R.get(i1, i1);
				Matrix HA2 = HA.getMatrix(i1, i1, 0, N-1);

				Y = D21.minus(HX2);
				/*int[] indices;
				NodeDetector detector; 
				if (i1 < nrSpeedObservations) {
					detector = detectors.get(i1);
					//indices = config.getIndicesObject(macromodel, surroundingCellsMap2.get(detectors.get(i1)));
				} else {
					detector = detectors.get(i1-nrSpeedObservations);
					//indices = config.getIndicesObject(macromodel, surroundingCellsMap2.get(detectors.get(i1-nrSpeedObservations)));

				}
				indices = config.getIndicesObject(macromodel, surroundingCellsMap2.get(detector));*/
				NodeDetector detector; 
				if (i1 < nrSpeedObservations) {
					detector = detectors.get(i1);
					//indices = config.getIndicesObject(macromodel, surroundingCellsMap2.get(detectors.get(i1)));
				} else {
					detector = detectors.get(i1-nrSpeedObservations);
					//indices = config.getIndicesObject(macromodel, surroundingCellsMap2.get(detectors.get(i1-nrSpeedObservations)));

				}
				int[] indices = buildIntArray(correspondingIndicesOfDetectors.get(i1));
				LinkedHashSet<MacroCell> cellAtDetector =  new LinkedHashSet<MacroCell>();
				cellAtDetector.add(detector.getClosestCell());
			//	int[] indexCellAtDetector = config.getIndices(macromodel, cellAtDetector);

				/*ArrayList<Integer> cellsToBeAdjusted = new ArrayList<Integer>();
				ArrayList<Integer> cellsToBeAdjustedIndex = new ArrayList<Integer>();
				int tmp = 0;
				for (int index: detectorIndices) {

					for (int index2: indices) {

						if (index == index2) {
							cellsToBeAdjusted.add(index);
							cellsToBeAdjustedIndex.add(tmp);
							//break;
						}

					}
					tmp++;

				}*/
				ArrayList<Integer> cellsToBeAdjustedIndex;
				if (i1 < nrSpeedObservations)
					cellsToBeAdjustedIndex= affectedDetectorsIndices.get(i1);
				else
					cellsToBeAdjustedIndex= affectedDetectorsIndices.get(i1-nrSpeedObservations);
				ArrayList<Integer> cellsToBeAdjusted = new ArrayList<Integer>();
				for (int ind: cellsToBeAdjustedIndex) {
					if (ind < detectorIndices1.length)
						cellsToBeAdjusted.add(detectorIndices1[ind]);
					//else
						//cellsToBeAdjusted.add(detectorIndices1[ind-detectorIndices1.length]);
				}
				//Arrays.asList(indices).;


				//Integer[] result = s1.toArray(new Integer[s1.size()]);


				/*if (i1 < nrSpeedObservations) {
					indices = config.getIndices(macromodel, surroundingCellsMap.get(detectors.get(i1).getClosestCell()));
				} else {
					indices = config.getIndices(macromodel, surroundingCellsMap.get(detectors.get(i1-nrSpeedObservations).getClosestCell()));

				}*/

				Matrix Xtest = X.getMatrix(indices,0,N-1);
				Matrix Atest = A.getMatrix(indices,0,N-1);
				Matrix bX = backupX.getMatrix(indices,0,N-1);
				Matrix bA = backupA.getMatrix(indices,0,N-1);
				Matrix bHX = backupHX.getMatrix(i1, i1,0,N-1);
				Matrix bHA = backupHA.getMatrix(i1, i1,0,N-1);

				double bp1 = (bHA.times(bHA.transpose()).get(0, 0))*(1.0/(N-1));
				Matrix Kster = bA.times(bHA.transpose()).times(bp1);

				Matrix difference = bX.minus(Xtest);


				double p1 = (HA2.times(HA2.transpose()).get(0, 0))*(1.0/(N-1));




				double p = p1+R2;
				//L = P.chol();
				M = Y.times(1.0/p);
				Z = (HA2.transpose()).times(M);
				diff = (Atest.times(Z)).times(1.0/(N-1));


				double HK = p1/(p);




				Matrix K1 = (Atest.times((HA2.transpose()).times(1.0/p))).times(1.0/(N-1));
				Matrix diff21 = K1.times(Y);

				Xa = Xtest.plus(diff);
				//System.out.println("test");
				//System.out.println(Xa.cond());
				//Matrix HXa = HX2.plus(Y.times(HK));
				//System.out.println("X="+Xtest.getArray());
				X.setMatrix(indices, 0, N-1, Xa);
				Matrix tmpA = Xa.minus((Xa.times(new Matrix(N,1,1.0)).times(new Matrix(1,N,1.0/N))));
				A.setMatrix(indices,0,N-1,tmpA);
				//HX.setMatrix(i1, i1, 0, N-1,HXa);
				//Matrix meanXa = HXa.times(new Matrix(N,1,1.0/N));
				//Matrix XAa = HXa.minus(new Matrix(1,N,1.0).times(meanXa.get(0, 0)));
				//HA.setMatrix(i1, i1, 0, N-1,XAa);

				//System.out.println(i1);


				for (int in = 0; in<cellsToBeAdjusted.size(); in++) {

					int index = cellsToBeAdjustedIndex.get(in);
					boolean kalman = false;
					for (int t = 0; t<2; t++) {
						index += (t)*nrSpeedObservations;
						int ctobA = cellsToBeAdjusted.get(in).intValue();
						Matrix HAster = backupHA.getMatrix(index, index, 0, N-1);
						double p1ster = (HAster.times(HAster.transpose()).get(0, 0))*(1.0/(N-1));
						//double pster = p1ster + R.get(index, index);
						Matrix X1 = X.getMatrix(ctobA,ctobA,0,N-1);
						Matrix Xb = backupX.getMatrix(ctobA,ctobA,0,N-1);
						Matrix HXb = backupHX.getMatrix(index,index,0,N-1);
						Matrix Ab = backupA.getMatrix(ctobA,ctobA,0,N-1);
						Matrix HXa2;
						double p2ster = (Ab.times(Ab.transpose()).get(0, 0))*(1.0/(N-1));
						if (kalman) {
							if (p1ster > 1e-8) {
								Matrix K2 = (Ab.times(HAster.transpose()).times((1.0/(N-1)))).times(1.0/p1ster);
								Matrix diffster = X1.minus(Xb);
								HXa2 = HXb.plus(diffster.times(1.0/K2.get(0, 0)));
							} else {
								HXa2 = (Matrix) HXb.clone();
							}
						} else {

							if (p1ster > 1e-8 && p2ster>1e-8) {
								Matrix xb = new Matrix(2,N,1.0);
								xb.setMatrix(0, 0, 0, N-1, Xb);
								Matrix K2 = ((xb.times(xb.transpose())).inverse()).times(xb.times(HXb.transpose()));
								double k = K2.get(0, 0);
								Matrix diffster = X1.minus(Xb);
								HXa2 = HXb.plus(diffster.times(k));
							} else {
								HXa2 = (Matrix) HXb.clone();
							}


						}




						HX.setMatrix(index, index, 0, N-1,HXa2);
						Matrix meanXa2 = HXa2.times(new Matrix(N,1,1.0/N));
						Matrix XAa2 = HXa2.minus(new Matrix(1,N,1.0).times(meanXa2.get(0, 0)));
						HA.setMatrix(index, index, 0, N-1,XAa2);
					}
					//					System.out.println(in);

				}



			}
			Xa = X.copy();
			X = backupX.copy();
			//HX = HXa;
			//System.out.println("stop");

			break;
			// per grid point
		case LENKF_GRID:
			//boolean testlocalized2 = false;

			//if (testlocalized2) {

			diff = new Matrix(config.nrStateVariables,N,0.0);
			//	ArrayList<Object> stateVariables = config.getStateVariables(macromodel);
			int i1=0;
			for (Object o: stateVariables) {
				//int[] indices = config.getIndicesDetector(macromodel, surroundingObservationsMap.get(o));
				int[] indices = buildIntArray(correspondingIndicesOfStateObjects.get(i1));
				//if (o  instanceof NodeBoundaryIn ) {
				//System.out.println("node");
				//}
				Matrix HX2 = HX.getMatrix(indices, 0, N-1);
				Matrix D21 = D.getMatrix(indices, 0, N-1);
				Matrix R2 = R.getMatrix(indices, indices);
				Matrix HA2 = HA.getMatrix(indices,0, N-1);

				Y = D21.minus(HX2);
				//int[] indices2 = config.getIndices(macromodel, surroundingCellsMap.get(detectors.get(i).getClosestCell()));
				Matrix Xtest = X.getMatrix(i1,i1,0,N-1);
				Matrix Atest = A.getMatrix(i1,i1,0,N-1);

				//P = (HA2.times(HA2.transpose())).times(1.0/(N-1)).plus(R2);
				//L = P.chol();
				//M = L.solve(Y);

				M = solveInversePStraightForward(R2,HA2,N,Y);
				Z = (HA2.transpose()).times(M);
				diff.setMatrix(i1, i1, 0, N-1,(Atest.times(Z)).times(1.0/(N-1)));


				//Matrix M2 = L.solve(Matrix.identity(indices.length, indices.length));

				//Matrix K = (Atest.times((HA2.transpose()).times(M2))).times(1.0/(N-1));
				//Matrix KHA = K.times(HA2);
				//Matrix Aa = Atest.minus(KHA.times(0.5));

				//Matrix diff2 = K.times(Y);

				i1++;
			}
			Xa = X.plus(diff);

			break;
		case LENKF_GRID_PARALLEL:
			

			diff = new Matrix(config.nrStateVariables,N,0.0);
			int[] stateIndices = new int[config.nrStateVariables];
			    for (int i = 0; i < config.nrStateVariables; i++) {
			    	stateIndices[i] = i;
			    }
		
			diff = MultiThreadedUpdate.computeTotal(stateIndices, HX, D, R, HA, X, A, AssimilationMethod.LENKF_GRID_PARALLEL);
			 //System.out.println(diff.norm1());
			Xa = X.plus(diff);

			break;
		case LENKF_GRID_SMW:
			//boolean testlocalized2 = false;

			//if (testlocalized2) {

			diff = new Matrix(config.nrStateVariables,N,0.0);
			//	ArrayList<Object> stateVariables = config.getStateVariables(macromodel);
			int i1s=0;
			for (Object o: stateVariables) {
				//int[] indices = config.getIndicesDetector(macromodel, surroundingObservationsMap.get(o));
				int[] indices = buildIntArray(correspondingIndicesOfStateObjects.get(i1s));
				//if (o  instanceof NodeBoundaryIn ) {
				//System.out.println("node");
				//}
				Matrix HX2 = HX.getMatrix(indices, 0, N-1);
				Matrix D21 = D.getMatrix(indices, 0, N-1);
				Matrix Rinv2 = Rinv.getMatrix(indices, indices);
				Matrix HA2 = HA.getMatrix(indices,0, N-1);

				Y = D21.minus(HX2);
				//int[] indices2 = config.getIndices(macromodel, surroundingCellsMap.get(detectors.get(i).getClosestCell()));
				Matrix Xtest = X.getMatrix(i1s,i1s,0,N-1);
				Matrix Atest = A.getMatrix(i1s,i1s,0,N-1);

				//M = solveInversePShermanMorrisonWoodbury(Rinv2,HA2,N,Matrix.identity(indices.length, indices.length));
				//Z = (HA2.transpose()).times(M).times(Y);
				M = solveInversePShermanMorrisonWoodbury2(Rinv2,HA2,N,Y);
				Z = (HA2.transpose()).times(M);


				diff.setMatrix(i1s, i1s, 0, N-1,(Atest.times(Z)).times(1.0/(N-1)));


				i1s++;
			}
			Xa = X.plus(diff);

			break;
		case LENKF_GRID_SMW_PARALLEL:

			diff = new Matrix(config.nrStateVariables,N,0.0);
			int[] stateIndices2 = new int[config.nrStateVariables];
			    for (int i = 0; i < config.nrStateVariables; i++) {
			    	stateIndices2[i] = i;
			    }
		
			diff = MultiThreadedUpdate.computeTotal(stateIndices2, HX, D, Rinv, HA, X, A, AssimilationMethod.LENKF_GRID_SMW_PARALLEL);
		
			Xa = X.plus(diff);

		

			break;
		case DENKF_MEASUREMENT:
			// per measurement
			boolean kalman = false;
			Matrix backupHX1 = HX.copy();
			Matrix backupX1 = X.copy();
			Matrix backupA1 = A.copy();
			Matrix backupHA1 = HA.copy();
			long tt1 = 0;
			long tt2 = 0;
			long tt3 = 0;
			long tt4 = 0;
			long tt5 = 0;
			long tt6 = 0;
			long t1 = System.nanoTime();
			//int[] detectorIndices1 = config.getIndices(macromodel, cellsWithDetectors);
			tt1+=(System.nanoTime() - t1);
			for (int i11 = 0; i11 < nrObservations; i11++) {
				long t2 = System.nanoTime();
				Matrix HX2 = HX.getMatrix(i11, i11, 0, N-1);
				Matrix gemH2 = HX2.times(new Matrix(N,1,1.0/N));
				Matrix D21 = observations.getMatrix(i11, i11, 0, 0);
				double R2 = R.get(i11, i11);
				Matrix HA2 = HA.getMatrix(i11, i11, 0, N-1);


				/*int[] indices;
				NodeDetector detector; 
				if (i11 < nrSpeedObservations) {
					detector = detectors.get(i11);
					//indices = config.getIndicesObject(macromodel, surroundingCellsMap2.get(detectors.get(i1)));
				} else {
					detector = detectors.get(i11-nrSpeedObservations);
					//indices = config.getIndicesObject(macromodel, surroundingCellsMap2.get(detectors.get(i1-nrSpeedObservations)));

				}


				LinkedHashSet<Object> objects = surroundingCellsMap2.get(detector);



				//hier gaat het langzaam:
				indices = config.getIndicesObject(macromodel, objects);*/
				NodeDetector detector; 
				if (i11 < nrSpeedObservations) {
					detector = detectors.get(i11);
					//indices = config.getIndicesObject(macromodel, surroundingCellsMap2.get(detectors.get(i1)));
				} else {
					detector = detectors.get(i11-nrSpeedObservations);
					//indices = config.getIndicesObject(macromodel, surroundingCellsMap2.get(detectors.get(i1-nrSpeedObservations)));

				}
				int[] indices = buildIntArray(correspondingIndicesOfDetectors.get(i11));

				tt2 +=((System.nanoTime() - t2));
				long t3 = System.nanoTime();
				//System.out.println("update with nr of objects: " + indices.length);
				LinkedHashSet<MacroCell> cellAtDetector =  new LinkedHashSet<MacroCell>();
				cellAtDetector.add(detector.getClosestCell());


				//int[] indexCellAtDetector = config.getIndices(macromodel, cellAtDetector);
/*
				ArrayList<Integer> cellsToBeAdjusted = new ArrayList<Integer>();
				ArrayList<Integer> cellsToBeAdjustedIndex = new ArrayList<Integer>();

				int tmp = 0;

				for (int index: detectorIndices1) {

					for (int index2: indices) {

						if (index == index2) {
							cellsToBeAdjusted.add(index);
							cellsToBeAdjustedIndex.add(tmp);
						}

					}
					tmp++;

				}*/
				ArrayList<Integer> cellsToBeAdjustedIndex;
				if (i11 < nrSpeedObservations)
					cellsToBeAdjustedIndex= affectedDetectorsIndices.get(i11);
				else
					cellsToBeAdjustedIndex= affectedDetectorsIndices.get(i11-nrSpeedObservations);
				ArrayList<Integer> cellsToBeAdjusted = new ArrayList<Integer>();
				for (int ind: cellsToBeAdjustedIndex) {
					if (ind < detectorIndices1.length)
						cellsToBeAdjusted.add(detectorIndices1[ind]);
					//else
						//cellsToBeAdjusted.add(detectorIndices1[ind-detectorIndices1.length]);
				}
				
				
				tt3+=((System.nanoTime() - t3));
				long t4 = System.nanoTime();
				/*if (indexCellAtDetector[0] == 1 || cellsToBeAdjusted.contains(Integer.valueOf(1))) {
					System.out.println("1");
				}*/
				//Arrays.asList(indices).;


				//Integer[] result = s1.toArray(new Integer[s1.size()]);


				/*if (i1 < nrSpeedObservations) {
					indices = config.getIndices(macromodel, surroundingCellsMap.get(detectors.get(i1).getClosestCell()));
				} else {
					indices = config.getIndices(macromodel, surroundingCellsMap.get(detectors.get(i1-nrSpeedObservations).getClosestCell()));

				}*/

				/*	Matrix D2 = observations.times(new Matrix(1,N,1.0));
				Y = observations.minus(gemH);
				P = R.plus((HA.times(HA.transpose())).times(1.0/(N-1)));
				L = P.chol();
				M = L.solve(Y);
				Z = (HA.transpose()).times(M);
				diff = (A.times(Z)).times(1.0/(N-1));

				Matrix M2 = L.solve(Matrix.identity(nrObservations, nrObservations));
				Matrix K = (A.times((HA.transpose()).times(M2))).times(1.0/(N-1));
				Matrix diff2 = K.times(Y);
				Matrix Xa2 = gemX.plus(diff);
				Matrix KHA = K.times(HA);
				Matrix Aa = A.minus(K.times(HA).times(0.5));
				Xa = Aa.plus(Xa2.times(new Matrix(1,N,1.0)));
				 */
				Matrix Xtest = X.getMatrix(indices,0,N-1);
				Matrix Atest = A.getMatrix(indices,0,N-1);
				Matrix bX = backupX1.getMatrix(indices,0,N-1);
				Matrix bA = backupA1.getMatrix(indices,0,N-1);
				Matrix bHX = backupHX1.getMatrix(i11, i11,0,N-1);
				Matrix bHA = backupHA1.getMatrix(i11, i11,0,N-1);

				Matrix gemXtest = Xtest.times(new Matrix(N,1,1.0/N));
				//double bp1 = (bHA.times(bHA.transpose()).get(0, 0))*(1.0/(N-1));
				//Matrix Kster = bA.times(bHA.transpose()).times(bp1);

				//Matrix difference = bX.minus(Xtest);

				/*for (int n1 = 0; n1<Xtest.getRowDimension(); n1++) {
					for (int n2 = 0; n2<Xtest.getColumnDimension(); n2++) {
						if (Double.isNaN(Xtest.get(n1, n2))) {
							throw new Error("NaN");
						}
					}
				}*/
				double p1 = (HA2.times(HA2.transpose()).get(0, 0))*(1.0/(N-1));




				double p = p1+R2;
				//L = P.chol();

				//M = Y.times(1.0/p);
				//Z = (HA2.transpose()).times(M);
				//diff = (Atest.times(Z)).times(1.0/(N-1));


				double HK = p1/(p);




				Matrix K1 = (Atest.times((HA2.transpose()).times(1.0/p))).times(1.0/(N-1));
				Matrix K1ster = (Atest.times((HA2.transpose()).times(1.0/p1))).times(1.0/(N-1));

				/*	Matrix X1 = X.getMatrix(cellsToBeAdjusted.get(in),cellsToBeAdjusted.get(in),0,N-1);
				Matrix Xb = backupX1.getMatrix(cellsToBeAdjusted.get(in),cellsToBeAdjusted.get(in),0,N-1);
				Matrix HXb = backupHX1.getMatrix(index,index,0,N-1);
				Matrix Ab = backupA1.getMatrix(cellsToBeAdjusted.get(in),cellsToBeAdjusted.get(in),0,N-1);
				Matrix HXa2;
				if (p1ster > 1e-10) {
				Matrix K2 = (Ab.times(HAster.transpose()).times((1.0/(N-1)))).times(1.0/p1ster);
				Matrix diffster = X1.minus(Xb);
				 HXa2= HXb.plus(diffster.times(1.0/K2.get(0, 0)));
				} else {
					 HXa2 = HXb;
				}*/


				Y = D21.minus(gemH2);
				Matrix diff21 = K1.times(Y);
				Matrix gemXa = gemXtest.plus(diff21);
				//Matrix Aa2 = Atest.minus((K1.times(HA2)).times(0.5));
				//Xa = Aa2.plus(gemXa.times(new Matrix(1,N,1.0)));
				// [[0.03447662118489043], [0.013320544676564081], [0.00410414430795031], [0.034406070256340585], [0.0029632116475088307], [0.011939192682160763], [0.010437568806505704], [0.0037610520563387084], [0.011289575787508825], [6.195762103775732E-4], [0.009370559113572692], [0.01273710671261402], [0.013273961855043842], [0.012952103859057314], [0.0031910894234574356], [0.008090963850979677], [0.03437085910974878], [0.03426522037427359], [0.0018514481019520441], [0.03430051758442306], [0.012410980781536646], [0.013300535331373636], [0.00661960276059038], [0.0033366030134525785], [0.013171489612808293], [0.01325343913795488], [6.157476113936438E-4], [0.013222120757445865], [0.013288723890552542], [0.8252173757965332], [0.005004056068635874], [0.0032301131864624286], [7.647709764522483E-4], [0.002758089276522995], [0.013329875954937178], [9.312447736359911E-4], [0.013088067465112934], [0.013310888898146273], [0.034335684947940065], [0.003343313671538651], [0.03451196214557965], [0.0031853783124922612], [0.034441323968745365]]
				Matrix Xatest = Xtest.plus((K1.times(0.5)).times((D21.times(new Matrix(1,N,1.0))).minus(HX2)));
				//Matrix Xatest2 = Xtest.plus((K1.times(1)).times((D21.times(new Matrix(1,N,1.0))).minus(HX2)));
				Matrix gemXatest = Xatest.times(new Matrix(N,1,1.0/N));
				//Matrix gemXatest2 = Xatest2.times(new Matrix(N,1,1.0/N));

				Xa = Xatest.plus((gemXa.minus(gemXatest).times(new Matrix(1,N,1.0))));
				//Matrix gX = Xa.times(new Matrix(N,1,1.0/N));
				//Matrix HXa = HX2.plus(Y.times(HK));
				//System.out.println("X="+Xtest.getArray());
				X.setMatrix(indices, 0, N-1, Xa);
				Matrix tmpA = Xa.minus((Xa.times(new Matrix(N,1,1.0)).times(new Matrix(1,N,1.0/N))));
				A.setMatrix(indices,0,N-1,tmpA);
				//Matrix A1 = X.times(new Matrix(N,1,1.0));
				//Matrix A2 = A1.times(new Matrix(1,N,1.0));
				//Matrix A3 = A2.times(1.0/N);
				//Matrix A = X.minus(A3);

				//HX.setMatrix(i11, i11, 0, N-1,HXa);
				//Matrix meanXa = HXa.times(new Matrix(N,1,1.0/N));
				//Matrix XAa = HXa.minus(new Matrix(1,N,1.0).times(meanXa.get(0, 0)));
				//	HA.setMatrix(i11, i11, 0, N-1,XAa);

				//System.out.println(i1);
				/*for (double[] d: Xa.getArray()) {
					for (double dd: d) {
						if (dd<-1e-03) {
							//System.out.println("negative Xa");
						}
					}
				}*/
				/*for (int n1 = 0; n1<Xa.getRowDimension(); n1++) {
					for (int n2 = 0; n2<Xa.getColumnDimension(); n2++) {
						if (Double.isNaN(Xa.get(n1, n2))) {
							throw new Error("NaN");
						}
					}
				}*/
				tt4 +=((System.nanoTime() - t4));
				long t5 = System.nanoTime();
				for (int in = 0; in<cellsToBeAdjusted.size(); in++) {

					int index = cellsToBeAdjustedIndex.get(in);
					for (int t = 0; t<2; t++) {
						index += (t)*nrSpeedObservations;
						Matrix HAster = backupHA1.getMatrix(index, index, 0, N-1);
						double p1ster = (HAster.times(HAster.transpose()).get(0, 0))*(1.0/(N-1));
						//double pster = p1ster + R.get(index, index);
						int ctobA = cellsToBeAdjusted.get(in).intValue();
						
						Matrix X1 = X.getMatrix(ctobA,ctobA,0,N-1);
						Matrix Xb = backupX1.getMatrix(ctobA,ctobA,0,N-1);
						Matrix HXb = backupHX1.getMatrix(index,index,0,N-1);
						Matrix Ab = backupA1.getMatrix(ctobA,ctobA,0,N-1);
						Matrix HXa2;
						double p2ster = (Ab.times(Ab.transpose()).get(0, 0))*(1.0/(N-1));
						
						
						if (kalman) {
							if (p1ster > 1e-7) {
								Matrix K2 = (Ab.times(HAster.transpose()).times((1.0/(N-1)))).times(1.0/p1ster);
								Matrix diffster = X1.minus(Xb);
								HXa2 = HXb.plus(diffster.times(1.0/K2.get(0, 0)));
							} else {
								HXa2 = (Matrix) HXb.clone();
							}
						} else {

							if ((p1ster > 1e-8) && (p2ster > 1e-8) ) {
								Matrix xb = new Matrix(2,N,1.0);
								xb.setMatrix(0, 0, 0, N-1, Xb);

								Matrix K2 = ((xb.times(xb.transpose())).inverse()).times(xb.times(HXb.transpose()));
								double k = K2.get(0, 0);
								Matrix diffster = X1.minus(Xb);
								HXa2 = HXb.plus(diffster.times(k));
							} else {
								HXa2 = (Matrix) HXb.clone();
							}


						}
						





						HX.setMatrix(index, index, 0, N-1,HXa2);
						Matrix meanXa2 = HXa2.times(new Matrix(N,1,1.0/N));
						Matrix XAa2 = HXa2.minus(new Matrix(1,N,1.0).times(meanXa2.get(0, 0)));
						HA.setMatrix(index, index, 0, N-1,XAa2);
					}
					//					System.out.println(in);

				}
				



				tt5 +=((System.nanoTime() - t5));
			}
			
			long t6 = System.nanoTime();
			Xa = X.copy();
			X = backupX1.copy();
			tt6 +=((System.nanoTime() - t6));
			//HX = HXa;
			//System.out.println("stop");
			System.out.println("part 1: "+(tt1/1000000));
			System.out.println("part 2: "+(tt2/1000000));
			System.out.println("part 3: "+(tt3/1000000));
			System.out.println("part 4: "+(tt4/1000000));
			System.out.println("part 5: "+(tt5/1000000));
			
			System.out.println("part 6: "+(tt6/1000000));
			break;
			// per grid point
		case DENKF_GRID:
			//boolean testlocalized2 = false;

			//if (testlocalized2) {

			diff = new Matrix(config.nrStateVariables,N,0.0);
			//	ArrayList<Object> stateVariables = config.getStateVariables(macromodel);
			int i11=0;
			for (Object o: stateVariables) {
				//int[] indices = config.getIndicesDetector(macromodel, surroundingObservationsMap.get(o));
				int[] indices = buildIntArray(correspondingIndicesOfStateObjects.get(i11));
				if (o  instanceof NodeBoundaryIn ) {
					//System.out.println("node");
				}
				Matrix HX2 = HX.getMatrix(indices, 0, N-1);
				Matrix gemHX = HX2.times(new Matrix(N,1,1.0/N));
				Matrix D21 = observations.getMatrix(indices, 0, 0);
				Matrix R2 = R.getMatrix(indices, indices);
				Matrix HA2 = HA.getMatrix(indices,0, N-1);

				Y = D21.minus(gemHX);
				//int[] indices2 = config.getIndices(macromodel, surroundingCellsMap.get(detectors.get(i).getClosestCell()));
				Matrix Xtest = X.getMatrix(i11,i11,0,N-1);
				Matrix Atest = A.getMatrix(i11,i11,0,N-1);
				//P = (HA2.times(HA2.transpose())).times(1.0/(N-1)).plus(R2);
				//L = P.chol();
				//M = L.solve(Y);
				//Z = (HA2.transpose()).times(M);



				//Matrix M21 = L.solve(Matrix.identity(indices.length, indices.length));
				/*Matrix M21 = solveInversePStraightForward(R2,HA2,N,Matrix.identity(indices.length, indices.length));



				Matrix K1 = (Atest.times((HA2.transpose()).times(M21))).times(1.0/(N-1));
				//Matrix KHA = K.times(HA2);
				//Matrix Aa = Atest.minus(KHA.times(0.5));

				//Matrix diff2 = K.times(Y);
				Matrix diff21 = (K1.times(Y));
				Matrix gemXtest = Xtest.times(new Matrix(N,1,1.0/N));
				Matrix gemXa2 = gemXtest.plus(diff21);
				Matrix diff3 = (((K1.times(0.5)).times((D21.times(new Matrix(1,N,1.0))).minus(HX2))));
				Matrix Xa3= Xtest.plus(diff3);

				Matrix gemXa3 = Xa3.times(new Matrix(N,1,1.0/N));

				Matrix Xa21 = Xa3.plus((gemXa2.minus(gemXa3).times(new Matrix(1,N,1.0))));


				//Matrix Aa2 = Atest.minus((K1.times(HA2)).times(0.5));
				//Xa2 = Aa2.plus(gemXa2.times(new Matrix(1,N,1.0)));


				diff.setMatrix(i11, i11, 0, N-1,Xa21.minus(Xtest));*/

				//Matrix M21 = solveInversePStraightForward(R2,HA2,N,Y);
				//Matrix M21t = solveInversePStraightForward(R2,HA2,N,(D21.times(new Matrix(1,N,1.0))).minus(HX2));

				Matrix[] M21 = solveInversePStraightForwardMult(R2,HA2,N,Y,(D21.times(new Matrix(1,N,1.0))).minus(HX2));


				//Matrix K1 = (Atest.times((HA2.transpose()).times(M21))).times(1.0/(N-1));
				Matrix K1 = (Atest.times((HA2.transpose()).times(M21[0]))).times(1.0/(N-1));

				//Matrix KHA = K.times(HA2);
				//Matrix Aa = Atest.minus(KHA.times(0.5));

				//Matrix diff2 = K.times(Y);
				Matrix diff21 = (K1);
				Matrix gemXtest = Xtest.times(new Matrix(N,1,1.0/N));
				Matrix gemXa2 = gemXtest.plus(diff21);
				Matrix diff3 = (Atest.times((HA2.transpose()).times(M21[1]))).times(1.0/(N-1)).times(0.5);
				Matrix Xa3= Xtest.plus(diff3);

				Matrix gemXa3 = Xa3.times(new Matrix(N,1,1.0/N));

				Matrix Xa21 = Xa3.plus((gemXa2.minus(gemXa3).times(new Matrix(1,N,1.0))));


				//Matrix Aa2 = Atest.minus((K1.times(HA2)).times(0.5));
				//Xa2 = Aa2.plus(gemXa2.times(new Matrix(1,N,1.0)));


				diff.setMatrix(i11, i11, 0, N-1,Xa21.minus(Xtest));

				i11++;
			}
			Xa = X.plus(diff);

			break;
		case DENKF_GRID_SMW:
			//boolean testlocalized2 = false;

			//if (testlocalized2) {
			long beginDENKF_GRID_SMW = System.nanoTime();
			long totalDENKF_GRID_SMW = 0;
			long totalGetIndices = 0;
			long totalFormMatrices = 0;
			long totalUpdate = 0;
			long totalInversion = 0;
			long totalKalmanGain = 0;
			long totalCorrection = 0;
			long totalSetDiff = 0;
			long totalTQ = 0;
			long totalQchol = 0;
			long totalQcholRedo = 0;
			long totalSolve = 0;
			diff = new Matrix(config.nrStateVariables,N,0.0);
			//	ArrayList<Object> stateVariables = config.getStateVariables(macromodel);
			int i111=0;
			for (Object o: stateVariables) {
				long beginGetIndices = System.nanoTime();
				//int[] indices = config.getIndicesDetector(macromodel, surroundingObservationsMap.get(o));
				int[] indices = buildIntArray(correspondingIndicesOfStateObjects.get(i111));
				totalGetIndices += System.nanoTime()-beginGetIndices;
				if (o  instanceof NodeBoundaryIn ) {
					//System.out.println("node");
				}
				long beginFormMatrices = System.nanoTime();
				Matrix HX2 = HX.getMatrix(indices, 0, N-1);
				Matrix gemHX = HX2.times(new Matrix(N,1,1.0/N));
				Matrix D21 = observations.getMatrix(indices, 0, 0);
				Matrix Rinv2 = Rinv.getMatrix(indices, indices);
				Matrix HA2 = HA.getMatrix(indices,0, N-1);
				totalFormMatrices += System.nanoTime()-beginFormMatrices;


				//int[] indices2 = config.getIndices(macromodel, surroundingCellsMap.get(detectors.get(i).getClosestCell()));
				Matrix Xtest = X.getMatrix(i111,i111,0,N-1);
				Matrix Atest = A.getMatrix(i111,i111,0,N-1);
				//P = (HA2.times(HA2.transpose())).times(1.0/(N-1)).plus(R2);
				//L = P.chol();
				//M = L.solve(Y);
				//Z = (HA2.transpose()).times(M);
				totalFormMatrices += System.nanoTime()-beginFormMatrices;
				long beginUpdate = System.nanoTime();
				Y = D21.minus(gemHX);
				//Matrix M21 = L.solve(Matrix.identity(indices.length, indices.length));
				long beginInversion = System.nanoTime();
				//Matrix M21 = solveInversePShermanMorrisonWoodbury(Rinv2,HA2,N,Matrix.identity(indices.length, indices.length));
				Matrix[] M21 = solveInversePShermanMorrisonWoodburyMult2(Rinv2,HA2,N,Y,(D21.times(new Matrix(1,N,1.0))).minus(HX2));


				/*
				Matrix[] M21test = solveInversePShermanMorrisonWoodburyExt(Rinv2,HA2,N,Matrix.identity(indices.length, indices.length));
				totalTQ += M21test[1].get(0,0);
				totalQchol += M21test[1].get(0,1);
				totalQcholRedo += M21test[1].get(0,2);
				totalSolve+= M21test[1].get(0,3);
				Matrix M21 = M21test[0];*/
				totalInversion += System.nanoTime()-beginInversion;
				long beginKalmanGain = System.nanoTime();
				//Matrix K1 = (Atest.times((HA2.transpose()).times(M21))).times(1.0/(N-1));
				totalKalmanGain += System.nanoTime()-beginKalmanGain;
				//Matrix KHA = K.times(HA2);
				//Matrix Aa = Atest.minus(KHA.times(0.5));

				//Matrix diff2 = K.times(Y);
				long beginCorrection = System.nanoTime();
				//Matrix diff21 = (K1.times(Y));
				Matrix diff21 = (Atest.times((HA2.transpose()).times(M21[0]))).times(1.0/(N-1));
				Matrix gemXtest = Xtest.times(new Matrix(N,1,1.0/N));
				Matrix gemXa2 = gemXtest.plus(diff21);
				Matrix diff3 = (Atest.times((HA2.transpose()).times(M21[1]))).times(1.0/(N-1)).times(0.5);
				Matrix Xa3= Xtest.plus(diff3);

				Matrix gemXa3 = Xa3.times(new Matrix(N,1,1.0/N));

				Matrix Xa22 = Xa3.plus((gemXa2.minus(gemXa3).times(new Matrix(1,N,1.0))));
				totalCorrection += System.nanoTime()-beginCorrection;

				//Matrix Aa2 = Atest.minus((K1.times(HA2)).times(0.5));
				//Xa2 = Aa2.plus(gemXa2.times(new Matrix(1,N,1.0)));

				long beginSetDiff = System.nanoTime();
				diff.setMatrix(i111, i111, 0, N-1,Xa22.minus(Xtest));
				totalSetDiff += System.nanoTime()-beginSetDiff;
				totalUpdate += System.nanoTime()-beginUpdate;
				i111++;
			}
			Xa = X.plus(diff);
			totalDENKF_GRID_SMW = System.nanoTime()-beginDENKF_GRID_SMW;
/*
			System.out.println("totalDENKF_GRID_SMW: " + totalDENKF_GRID_SMW);
			System.out.println("totalGetIndices: " + totalGetIndices/1000000);
			System.out.println("totalFormMatrices: " + totalFormMatrices/1000000);
			System.out.println("totalUpdate: " + totalUpdate/1000000);
			System.out.println("totalInversion: " + totalInversion/1000000);
			System.out.println("totalKalmanGain: " + totalKalmanGain/1000000);
			System.out.println("totalCorrection: " + totalCorrection/1000000);
			System.out.println("totalSetDiff: " +totalSetDiff/1000000);
			System.out.println("totalTQ: " +totalTQ/1000000);
			System.out.println("totalQchol: " +totalQchol/1000000);
			System.out.println("totalQcholRedo: " +totalQcholRedo/1000000);
			System.out.println("totalSolve: " +totalSolve/1000000);*/

			break;
		case DENKF_GRID_PARALLEL:
			Matrix D221 = observations.times(new Matrix(1,N,1.0));
			Y = observations.minus(gemH);
			diff = new Matrix(config.nrStateVariables,N,0.0);
			int[] stateIndices3 = new int[config.nrStateVariables];
			    for (int i = 0; i < config.nrStateVariables; i++) {
			    	stateIndices3[i] = i;
			    }
		
			diff = MultiThreadedUpdate.computeTotal(stateIndices3, HX, D221, R, HA, X, A, AssimilationMethod.DENKF_GRID_PARALLEL);
		
			Xa = X.plus(diff);

			
			break;
		case DENKF_GRID_SMW_PARALLEL:
			Matrix D222 = observations.times(new Matrix(1,N,1.0));
			Y = observations.minus(gemH);

			diff = new Matrix(config.nrStateVariables,N,0.0);
			int[] stateIndices4 = new int[config.nrStateVariables];
			    for (int i = 0; i < config.nrStateVariables; i++) {
			    	stateIndices4[i] = i;
			    }
		
			diff = MultiThreadedUpdate.computeTotal(stateIndices4, HX, D222, Rinv, HA, X, A, AssimilationMethod.DENKF_GRID_SMW_PARALLEL);
		
			Xa = X.plus(diff);


			break;
		case NO_ASSIMILATION:
			Xa = X.copy();
			break;
		}

		Matrix AXa = ((Xa.times(new Matrix(N,1,1.0))).times(new Matrix(1,N,1.0))).times(1.0/N);
		//Matrix AXa2 = Xa.minus(((Xa.times(new Matrix(N,1,1.0))).times(new Matrix(1,N,1.0))).times(1.0/N));
		//Matrix AX2 = X.minus(((X.times(new Matrix(N,1,1.0))).times(new Matrix(1,N,1.0))).times(1.0/N));

		//double r = 1.05;
		//
		//Matrix r1 = new Matrix(config.nrStateVariables,1,ri);
		//Matrix r1 = Matrix.identity(config.nrStateVariables, 1).times(r);
		Matrix r1 = new Matrix(config.getInflationFactorArray(),config.nrStateVariables);
		Matrix r2 = r1.times(new Matrix(1,N,1.0));
		//Matrix r2a = r2.minus(new Matrix(config.nrStateVariables,N,1.0));
		//Matrix r2b = r2.minus(new Matrix(config.nrStateVariables,N,2.0));
		Xa = ((Xa.minus(AXa)).arrayTimes(r2)).plus(AXa);
		/*for (double i: Xa.getArray()[Xa.getRowDimension()-1]) {
			if (i <= 0) {
				//System.out.println("stop");
				//x = config.saveStateToArray(m);
			}
		}
		double alpha = 0.8;
		double te=1;*/
		//Xa = AXa2.arrayTimes(r2b).plus(AX2.arrayTimes(r2a)).plus(AXa);
		//Xa = AXa2.times(1-alpha).plus(AX2.times(alpha)).plus(AXa);
		//System.out.println(X);
		Matrix gemXa = new Matrix(config.nrStateVariables,1);
		for (int j = 0; j<N; j++) {

			Matrix tmp2 = Xa.getMatrix(0,config.nrStateVariables-1, j,j);

			gemXa = gemXa.plus(tmp2);
		}
		gemXa = gemXa.times(1.0/N);

		Matrix varXa = new Matrix(config.nrStateVariables,1);
		Matrix tmpXa2 = new Matrix(config.nrStateVariables, 1);
		Matrix stdXa = new Matrix(config.nrStateVariables,1);
		for (int j = 0; j<N; j++) {
			Matrix tmpXa = (Xa.getMatrix(0,config.nrStateVariables-1, j,j)).minus(gemXa);
			tmpXa2 = tmpXa2.plus(tmpXa.arrayTimes(tmpXa));
		}
		varXa = tmpXa2.times(1.0/(N-1));
		for (int j=0; j<config.nrStateVariables; j++) {
			stdXa.set(j, 0, Math.sqrt(varXa.get(j, 0)));
		}
		/*double[] inflowAfter = gemXa.getMatrix(4645,4693, 0,0).transpose().getArray()[0];
		System.out.println("After: "+Arrays.toString(inflowAfter));
		 */		for (Scheduler s: ensembles){
			 Model m = (Model) s.getSimulator().getModel();
			 int i = ensembles.indexOf(s);
			 double[] copy = Xa.getMatrix(0, config.nrStateVariables-1,i,i).getColumnPackedCopy();
			 config.restoreState(copy, m);
		 }
		 config.restoreState((gemXa.transpose()).getArray()[0], macromodel);
		 double[] speeds = config.saveStateToArray(macromodel,StateDefinition.V_CELL);

		 Matrix gemV = new Matrix(speeds,speeds.length);

		 Matrix[] output = AssimilationConfiguration.getOutput(macromodel, StateDefinition.K_CELL, StateDefinition.V_CELL, StateDefinition.TRAFFICREGIME_CELL); 
		/*Matrix diffXXa =  X.minus(Xa);
		System.out.println(diffXXa.norm1() + " & " + diffXXa.norm2() + " & " + diffXXa.normF() + " & " + diffXXa.normInf());
		double[] error = new double[(gemXa.transpose()).getArray()[0].length];
		double[] after = config.saveStateToArray(macromodel);
		double totalError = 0;
		for (int i = 0; i<error.length; i++) {
			error[i] = (gemXa.transpose()).getArray()[0][i] - after[i];
			totalError+= Math.abs(error[i]);
		}
		System.out.println(totalError);
		
		 if (diffXXa.normF() > 0.0000001 || totalError > 0.0000001){
			System.out.println("high error");
		}
		Matrix Xr = gemX.getMatrix(exportRoute, 0,0);
		Matrix Xar = gemXa.getMatrix(exportRoute, 0,0);*/
		 /*double[] inflowAfter2 = AssimilationConfiguration.getOutput(macromodel, StateDefinition.INFLOW_NODE)[0].transpose().getArray()[0];
		System.out.println("After2: "+Arrays.toString(inflowAfter2));
		  */	return new Matrix[]{X,gemV,Xa,P,M,Z,observations, D, HX,gemH,gemX,gemXa,varH,varX,varXa,stdH,stdX,stdXa,output[2],new Matrix(1,1),new Matrix(1,1)};
	}
	static public Matrix generateWhiteNoise(Matrix source, double std) {
		int i = source.getRowDimension();
		double[] stdArray = new double[i];

		Arrays.fill(stdArray,std);
		return generateWhiteNoise(source, stdArray);
	}
	static public Matrix generateWhiteNoise(Matrix source, double[] std) {
		//Random rnd = r;
		Matrix noise = new Matrix(source.getRowDimension(), source.getColumnDimension());
		if (noise.getRowDimension() == std.length) {
			for (int i=0; i<noise.getRowDimension(); i++) {
				for (int j=0; j<noise.getColumnDimension(); j++) {
					noise.set(i, j, r.nextGaussian()*std[i]);
				}
			} 
		} else {
			for (int i=0; i<noise.getRowDimension(); i++) {
				for (int j=0; j<noise.getColumnDimension(); j++) {
					noise.set(i, j, r.nextGaussian()*std[j]);
				}
			}
		}
		Matrix result = noise.plus(source);
		return result;
	}
	public void exportSummarizedToMatlab(ArrayList<Matrix[]> results, ArrayList<ArrayList<Matrix[]>> forecasts, String filename, boolean plot) {


		/*Matrix m1 = results2.get(results2.size()-1)[0];
				int nrRows = m1.getRowDimension();
				out.println("%TTs" + i);
				for (int k = 0; k<nrRows/2; k++) {
					out.println("f_ttTime{"+(i+1)+"}("+(k+1)+",:)=" + Arrays.toString(m1.getArray()[k*2]) + ";");
					out.println("f_ttValues{"+(i+1)+"}("+(k+1)+",:)=" + Arrays.toString(m1.getArray()[k*2+1]) + ";");

				}*/


		PrintWriter out;
		if (filename.isEmpty())
			filename = "testEnKFSum";
		try {

			out = new PrintWriter(filename+".m");
			//out.println("clear;");
			Matrix[] m = results.get(results.size()-1);
			//Matrix truth = ;
			/*for (int i = 0; i < m[3].getRowDimension();i++) {
				out.println("truth("+(i+1)+",:)="+Arrays.toString(m[3].getArray()[i])+";");
				out.println("truthV("+(i+1)+",:)="+Arrays.toString(m[2].getArray()[i])+";");
				out.println("truthObs("+(i+1)+",:)="+Arrays.toString(m[0].getArray()[i])+";");
				out.println("truthTR("+(i+1)+",:)="+Arrays.toString(m[4].getArray()[i])+";");
			}*/
			Matrix Xa = new Matrix((results.size()-1), nrCells,0);
			Matrix V = new Matrix((results.size()-1), nrCells,0);
			Matrix Tr = new Matrix((results.size()-1), nrCells,0);
			ArrayList<Double> times = new ArrayList<Double>();
			for (int k = 0; k< results.size()-1; k++) {
				Matrix[] m1 = results.get(k);
				Xa.setMatrix(k, k, 0, nrCells-1,m1[11].transpose());
				V.setMatrix(k, k, 0, nrCells-1,m1[1].transpose());
				Tr.setMatrix(k, k, 0, nrCells-1,m1[18].transpose());
				times.add(m1[19].get(0,0));
			}

			/*double RMSEK = CalcOutput.calcRMSEGlobal(m[3].getMatrix(1, m[3].getRowDimension()-1, 0, nrCells-1), Xa);
			double RMSEV = CalcOutput.calcRMSEGlobal(m[2].getMatrix(1, m[2].getRowDimension()-1, 0, nrCells-1), V);
			double TRE = CalcOutput.calcTRE(m[4].getMatrix(1, m[4].getRowDimension()-1, 0, nrCells-1), Tr, cellLengths2, 60);
			double MAPEK = CalcOutput.calcMAPEGlobal(m[3].getMatrix(1, m[3].getRowDimension()-1, 0, nrCells-1), Xa);
			double MAPEV = CalcOutput.calcMAPEGlobal(m[2].getMatrix(1, m[2].getRowDimension()-1, 0, nrCells-1), V);
			double[] RMSEKState = CalcOutput.calcRMSEStateDependent(m[3].getMatrix(1, m[3].getRowDimension()-1, 0, nrCells-1), Xa, m[4].getMatrix(1, m[4].getRowDimension()-1, 0, nrCells-1));
			double[] RMSEVState = CalcOutput.calcRMSEStateDependent(m[2].getMatrix(1, m[2].getRowDimension()-1, 0, nrCells-1), V, m[4].getMatrix(1, m[4].getRowDimension()-1, 0, nrCells-1));
			double[] MAPEKState = CalcOutput.calcMAPEStateDependent(m[3].getMatrix(1, m[3].getRowDimension()-1, 0, nrCells-1), Xa, m[4].getMatrix(1, m[4].getRowDimension()-1, 0, nrCells-1));
			double[] MAPEVState = CalcOutput.calcMAPEStateDependent(m[2].getMatrix(1, m[2].getRowDimension()-1, 0, nrCells-1), V, m[4].getMatrix(1, m[4].getRowDimension()-1, 0, nrCells-1));

			double[] res = new double[]{RMSEK,MAPEK,RMSEV,MAPEV,TRE,RMSEKState[0],MAPEKState[0],RMSEVState[0],MAPEVState[0],RMSEKState[1],MAPEKState[1],RMSEVState[1],MAPEVState[1]};
			 */
			double[] res = CalcOutput.calcOutput(m[3].getMatrix(1, m[3].getRowDimension()-1, 0, nrCells-1), m[2].getMatrix(1, m[2].getRowDimension()-1, 0, nrCells-1), m[4].getMatrix(1, m[4].getRowDimension()-1, 0, nrCells-1), Xa, V, Tr, cellLengths2, 60); 
			//System.out.println(Arrays.toString(res));
			//System.out.println(Arrays.toString(res2));
			/*
			out.println("RMSEK="+RMSEK+";");
			out.println("RMSEV="+RMSEV+";");
			out.println("TRE="+TRE+";");
			out.println("MAPEK="+MAPEK+";");
			out.println("MAPEV="+MAPEV+";");*/
			//out.println("result=" + Arrays.toString(res) +";");

			//out.println("close all;");


			int nrOutput = res.length;
			Matrix[] FE = new Matrix[nrOutput];
			for (int i = 0; i<nrOutput; i++) {
				FE[i]= new Matrix(forecasts.size(),forecasts.get(0).size()-1,-1);
			}
			//Matrix FE_X = new Matrix(forecasts.size(),forecasts.get(0).size()-1,-1);
			//Matrix FE_V = new Matrix(forecasts.size(),forecasts.get(0).size()-1,-1);
			//Matrix FE_TR = new Matrix(forecasts.size(),forecasts.get(0).size()-1,-1);
			for (int i = 0; i< forecasts.size(); i++){
				ArrayList<Matrix[]> results1 = forecasts.get(i);
				ArrayList<Integer> indicesTimes = new ArrayList<Integer>();

				//ArrayList<Double[]> f_x = new ArrayList<Double[]>();
				for (int k = 0; k< results1.size()-1; k++) {
					Matrix[] m1 = results1.get(k);

					//ArrayList<Double[]> densities = new ArrayList<Double[]>();
					//for (double t: m1[10].getArray()[0]) {
					if (times.indexOf(m1[10].getArray()[0][0]) >0) {
						indicesTimes.add(times.indexOf(m1[10].getArray()[0][0]));
					}
				}
				int[] ind = new int[indicesTimes.size()];
				for (int i1 = 0; i1<indicesTimes.size(); i1++) {
					ind[i1] = indicesTimes.get(i1)+1;
				}
				Matrix[] F_E = new Matrix[nrOutput];
				for (int j = 0; j<nrOutput; j++) {
					F_E[j]= new Matrix(indicesTimes.size(),nrCells);
				}
				//Matrix f_x = new Matrix(indicesTimes.size(),nrCells);

				//Matrix f_v = new Matrix(indicesTimes.size(),nrCells);
				//Matrix f_tr = new Matrix(indicesTimes.size(),nrCells);
				for (int k = 0; k< results1.size()-1; k++) {
					Matrix[] m1 = results1.get(k);
					if (k < F_E[0].getRowDimension()) {
						/*f_x.setMatrix(k, k, 0, nrCells-1, (m1[5].transpose()).getMatrix(0, 0, 0, nrCells-1));
						f_v.setMatrix(k, k, 0, nrCells-1, (m1[8].transpose()).getMatrix(0, 0, 0, nrCells-1));
						f_tr.setMatrix(k, k, 0, nrCells-1, (m1[9].transpose()).getMatrix(0, 0, 0, nrCells-1));

					double f_RMSEK = CalcOutput.calcRMSEGlobal(m[3].getMatrix(ind[k],ind[k], 0, nrCells-1), f_x.getMatrix(k, k, 0, nrCells-1));
					double f_RMSEV =  CalcOutput.calcRMSEGlobal(m[2].getMatrix(ind[k],ind[k], 0, nrCells-1), f_v.getMatrix(k, k, 0, nrCells-1));
					double f_RMSETR = CalcOutput.calcTRE(m[4].getMatrix(ind[k],ind[k], 0, nrCells-1), f_tr.getMatrix(k, k, 0, nrCells-1),cellLengths2,60);*/
						double[] resf = CalcOutput.calcOutput(m[3].getMatrix(ind[k],ind[k], 0, nrCells-1), m[2].getMatrix(ind[k],ind[k], 0, nrCells-1), m[4].getMatrix(ind[k],ind[k], 0, nrCells-1), (m1[5].transpose()).getMatrix(0, 0, 0, nrCells-1), (m1[8].transpose()).getMatrix(0, 0, 0, nrCells-1), (m1[9].transpose()).getMatrix(0, 0, 0, nrCells-1), cellLengths2, 60); 
						for (int i1 = 0; i1<nrOutput; i1++) {
							FE[i1].set(i, k, resf[i1]);
						}
						/*FE_X.set(i, k, f_RMSEK);
					FE_V.set(i, k, f_RMSEV);
					FE_TR.set(i, k, f_RMSETR);*/
					}

					//ArrayList<Double[]> densities = new ArrayList<Double[]>();
					//for (double t: m1[10].getArray()[0]) {

				}
			}
			double[][] f = new double[nrOutput][FE[0].getColumnDimension()];
			//double[] fk = new double[FE_X.getColumnDimension()];
			//double[] fv = new double[FE_X.getColumnDimension()];
			//double[] ftr = new double[FE_X.getColumnDimension()];
			for (int j=0; j< FE[0].getColumnDimension(); j++) {
				double[] gem = new double[nrOutput];
				/*for (int k=0; k<nrOutput; k++) {
				double gemX = 0;
				double gemV = 0;
				double gemTR = 0;
				}*/
				int k =0;
				for (int i = 0; i< FE[0].getRowDimension(); i++) {

					if (!(FE[0].get(i, j) == -1)) {
						for (int o = 0; o<nrOutput; o++) {
							gem[o] += FE[o].get(i, j);


						}
						/*

						gemX += FE_X.get(i, j);
						gemV += FE_V.get(i, j);
						gemTR += FE_TR.get(i, j);*/
						k++;
					}


				}
				for (int o = 0; o<nrOutput; o++) {
					f[o][j] += gem[o]/k;


				}
				/*fk[j] =  gemX/k;
				fv[j] =  gemV/k;
				ftr[j] = gemTR/k;*/
			}
			double[] fres = new double[nrOutput*f[0].length];
			int pos=0;
			for (double[] a: f) {
				System.arraycopy(a, 0, fres, pos, a.length);
				pos += a.length;
			}
			//out.println("fk="+Arrays.toString(fk)+";");
			//out.println("fv="+Arrays.toString(fv)+";");
			//out.println("ftr="+Arrays.toString(ftr)+";");


			double[] finalresults = new double[res.length + fres.length];
			System.arraycopy(res, 0, finalresults, 0, res.length);
			System.arraycopy(fres, 0, finalresults, res.length, fres.length);
			//System.arraycopy(fv, 0, finalresults, res.length+fk.length, fv.length);
			//System.arraycopy(ftr, 0, finalresults, res.length+fk.length+fv.length, ftr.length);

			//out.println("result=" + Arrays.toString(CalcOutput.roundToSignificantFigures(5, finalresults)) +";");
			out.println("result=" + Arrays.toString(finalresults) +";");
			//double[] f_x = 
			//}
			//f_x.add(new Double[](){m1[5].transpose().getArray()[0]);


			/*out.println("%k=" + k);
					out.println("f_time{"+(i+1)+"}("+(k+1)+",:)=" + Arrays.toString(m1[10].getArray()[0]) + ";");
					//return new Matrix[]{xmin,y,F,Pnew,observations,x,P};
					//out.println("f_xmin{"+(i+1)+"}("+(k+1)+",:)="+Arrays.toString(m[0].transpose().getArray()[0])+";");
					//out.println("f_ymin{"+(i+1)+"}("+(k+1)+",:)="+Arrays.toString(m[1].transpose().getArray()[0])+";");
					out.println("f_x{"+(i+1)+"}("+(k+1)+",:)="+Arrays.toString(m1[5].transpose().getArray()[0])+";");
					//out.println("f_obs{"+(i+1)+"}("+(k+1)+",:)="+Arrays.toString(m[4].transpose().getArray()[0])+";");
					out.println("f_y{"+(i+1)+"}("+(k+1)+",:)="+Arrays.toString(m1[7].transpose().getArray()[0])+";");
					out.println("f_v{"+(i+1)+"}("+(k+1)+",:)="+Arrays.toString(m1[8].transpose().getArray()[0])+";");
					out.println("f_tr{"+(i+1)+"}("+(k+1)+",:)="+Arrays.toString(m1[9].transpose().getArray()[0])+";");*/

			/*for (int i = 0; i<m[0].getRowDimension(); i++) {
					out.println("xmin["+k+1+"]("+(i+1)+",:)="+Arrays.toString(m[0].getArray()[i]));
				}*/
			//m[0].pr;

			//ArrayList<Matrix[]> results2 = forecasts.get(i);


			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void exportSummarizedToMatlab2(Matrix[][] results, Matrix[][] forecast, int pointOfForecast, String filename, boolean fc) {


		/*Matrix m1 = results2.get(results2.size()-1)[0];
				int nrRows = m1.getRowDimension();
				out.println("%TTs" + i);
				for (int k = 0; k<nrRows/2; k++) {
					out.println("f_ttTime{"+(i+1)+"}("+(k+1)+",:)=" + Arrays.toString(m1.getArray()[k*2]) + ";");
					out.println("f_ttValues{"+(i+1)+"}("+(k+1)+",:)=" + Arrays.toString(m1.getArray()[k*2+1]) + ";");

				}*/


		PrintWriter out;
		if (filename.isEmpty())
			filename = "testEnKFSum";
		try {
			out= new PrintWriter(new BufferedWriter(new FileWriter(filename+".m")));

			//out.println("clear;");
			Matrix[] m = results[(results.length-1)];
			//Matrix truth = ;
			/*for (int i = 0; i < m[3].getRowDimension();i++) {
				out.println("truth("+(i+1)+",:)="+Arrays.toString(m[3].getArray()[i])+";");
				out.println("truthV("+(i+1)+",:)="+Arrays.toString(m[2].getArray()[i])+";");
				out.println("truthObs("+(i+1)+",:)="+Arrays.toString(m[0].getArray()[i])+";");
				out.println("truthTR("+(i+1)+",:)="+Arrays.toString(m[4].getArray()[i])+";");
			}*/
			Matrix Xa = new Matrix((results.length-1), nrCells,0);
			Matrix V = new Matrix((results.length-1), nrCells,0);
			Matrix Tr = new Matrix((results.length-1), nrCells,0);
			ArrayList<Double> times = new ArrayList<Double>();
			for (int k = 0; k< results.length-1; k++) {
				Matrix[] m1 = results[k];
				Xa.setMatrix(k, k, 0, nrCells-1,m1[0].transpose());
				V.setMatrix(k, k, 0, nrCells-1,m1[1].transpose());
				Tr.setMatrix(k, k, 0, nrCells-1,m1[2].transpose());
				//times.add(m1[19].get(0,0));
			}

			//for () {


			/*double RMSEK = CalcOutput.calcRMSEGlobal(m[3].getMatrix(1, m[3].getRowDimension()-1, 0, nrCells-1), Xa);
			double RMSEV = CalcOutput.calcRMSEGlobal(m[2].getMatrix(1, m[2].getRowDimension()-1, 0, nrCells-1), V);
			double TRE = CalcOutput.calcTRE(m[4].getMatrix(1, m[4].getRowDimension()-1, 0, nrCells-1), Tr, cellLengths2, 60);
			double MAPEK = CalcOutput.calcMAPEGlobal(m[3].getMatrix(1, m[3].getRowDimension()-1, 0, nrCells-1), Xa);
			double MAPEV = CalcOutput.calcMAPEGlobal(m[2].getMatrix(1, m[2].getRowDimension()-1, 0, nrCells-1), V);
			double[] RMSEKState = CalcOutput.calcRMSEStateDependent(m[3].getMatrix(1, m[3].getRowDimension()-1, 0, nrCells-1), Xa, m[4].getMatrix(1, m[4].getRowDimension()-1, 0, nrCells-1));
			double[] RMSEVState = CalcOutput.calcRMSEStateDependent(m[2].getMatrix(1, m[2].getRowDimension()-1, 0, nrCells-1), V, m[4].getMatrix(1, m[4].getRowDimension()-1, 0, nrCells-1));
			double[] MAPEKState = CalcOutput.calcMAPEStateDependent(m[3].getMatrix(1, m[3].getRowDimension()-1, 0, nrCells-1), Xa, m[4].getMatrix(1, m[4].getRowDimension()-1, 0, nrCells-1));
			double[] MAPEVState = CalcOutput.calcMAPEStateDependent(m[2].getMatrix(1, m[2].getRowDimension()-1, 0, nrCells-1), V, m[4].getMatrix(1, m[4].getRowDimension()-1, 0, nrCells-1));

			double[] res = new double[]{RMSEK,MAPEK,RMSEV,MAPEV,TRE,RMSEKState[0],MAPEKState[0],RMSEVState[0],MAPEVState[0],RMSEKState[1],MAPEKState[1],RMSEVState[1],MAPEVState[1]};
			 */
			double[] res = CalcOutput.calcOutput(m[0],m[1],m[2], Xa, V, Tr, cellLengths2, 2); 
			//System.out.println(Arrays.toString(res));
			//System.out.println(Arrays.toString(res2));
			/*
			out.println("RMSEK="+RMSEK+";");
			out.println("RMSEV="+RMSEV+";");
			out.println("TRE="+TRE+";");
			out.println("MAPEK="+MAPEK+";");
			out.println("MAPEV="+MAPEV+";");*/
			//out.println("result=" + Arrays.toString(res) +";");

			//out.println("close all;");

			if (fc) {
				Matrix FXa = new Matrix((forecast.length), nrCells,0);
				Matrix FV = new Matrix((forecast.length), nrCells,0);
				Matrix FTr = new Matrix((forecast.length), nrCells,0);
				for (int k = 0; k< forecast.length; k++) {
					Matrix[] m1 = forecast[k];
					FXa.setMatrix(k, k, 0, nrCells-1,m1[0].transpose());
					FV.setMatrix(k, k, 0, nrCells-1,m1[1].transpose());
					FTr.setMatrix(k, k, 0, nrCells-1,m1[2].transpose());
					//times.add(m1[19].get(0,0));
				}
				int beginFHorizon = pointOfForecast;
				//int endFHorizon = beginFHorizon + forecast.length-1;
				int[] endFHorizons = new int[]{
						beginFHorizon + 149,
						beginFHorizon + 449,
						beginFHorizon + 899,
						beginFHorizon + 1599,
				};
				double fres[] = new double[endFHorizons.length*13];
				int i = 0;
				for (int endFHorizon: endFHorizons) {
					double[] fr = CalcOutput.calcOutput(m[0].getMatrix(beginFHorizon, endFHorizon, 0, nrCells-1),m[1].getMatrix(beginFHorizon, endFHorizon, 0, nrCells-1),m[2].getMatrix(beginFHorizon, endFHorizon, 0, nrCells-1), FXa.getMatrix(0, endFHorizon-beginFHorizon, 0, nrCells-1), FV.getMatrix(0, endFHorizon-beginFHorizon, 0, nrCells-1), FTr.getMatrix(0, endFHorizon-beginFHorizon, 0, nrCells-1), cellLengths2, 2); 
					System.arraycopy(fr, 0, fres, i, 13);
					i=i+13;
				}



				/*int nrOutput = res.length;
				Matrix[] FE = new Matrix[nrOutput];
				for (int i = 0; i<nrOutput; i++) {
					FE[i]= new Matrix(forecasts.size(),forecasts.get(0).size()-1,-1);
				}
				//Matrix FE_X = new Matrix(forecasts.size(),forecasts.get(0).size()-1,-1);
				//Matrix FE_V = new Matrix(forecasts.size(),forecasts.get(0).size()-1,-1);
				//Matrix FE_TR = new Matrix(forecasts.size(),forecasts.get(0).size()-1,-1);
				for (int i = 0; i< forecasts.size(); i++){
					ArrayList<Matrix[]> results1 = forecasts.get(i);
					ArrayList<Integer> indicesTimes = new ArrayList<Integer>();

					//ArrayList<Double[]> f_x = new ArrayList<Double[]>();
					for (int k = 0; k< results1.size()-1; k++) {
						Matrix[] m1 = results1.get(k);

						//ArrayList<Double[]> densities = new ArrayList<Double[]>();
						//for (double t: m1[10].getArray()[0]) {
						if (times.indexOf(m1[10].getArray()[0][0]) >0) {
							indicesTimes.add(times.indexOf(m1[10].getArray()[0][0]));
						}
					}
					int[] ind = new int[indicesTimes.size()];
					for (int i1 = 0; i1<indicesTimes.size(); i1++) {
						ind[i1] = indicesTimes.get(i1)+1;
					}
					Matrix[] F_E = new Matrix[nrOutput];
					for (int j = 0; j<nrOutput; j++) {
						F_E[j]= new Matrix(indicesTimes.size(),nrCells);
					}
					//Matrix f_x = new Matrix(indicesTimes.size(),nrCells);

					//Matrix f_v = new Matrix(indicesTimes.size(),nrCells);
					//Matrix f_tr = new Matrix(indicesTimes.size(),nrCells);
					for (int k = 0; k< results1.size()-1; k++) {
						Matrix[] m1 = results1.get(k);
						if (k < F_E[0].getRowDimension()) {
							f_x.setMatrix(k, k, 0, nrCells-1, (m1[5].transpose()).getMatrix(0, 0, 0, nrCells-1));
						f_v.setMatrix(k, k, 0, nrCells-1, (m1[8].transpose()).getMatrix(0, 0, 0, nrCells-1));
						f_tr.setMatrix(k, k, 0, nrCells-1, (m1[9].transpose()).getMatrix(0, 0, 0, nrCells-1));

					double f_RMSEK = CalcOutput.calcRMSEGlobal(m[3].getMatrix(ind[k],ind[k], 0, nrCells-1), f_x.getMatrix(k, k, 0, nrCells-1));
					double f_RMSEV =  CalcOutput.calcRMSEGlobal(m[2].getMatrix(ind[k],ind[k], 0, nrCells-1), f_v.getMatrix(k, k, 0, nrCells-1));
					double f_RMSETR = CalcOutput.calcTRE(m[4].getMatrix(ind[k],ind[k], 0, nrCells-1), f_tr.getMatrix(k, k, 0, nrCells-1),cellLengths2,60);
							double[] resf = CalcOutput.calcOutput(m[3].getMatrix(ind[k],ind[k], 0, nrCells-1), m[2].getMatrix(ind[k],ind[k], 0, nrCells-1), m[4].getMatrix(ind[k],ind[k], 0, nrCells-1), (m1[5].transpose()).getMatrix(0, 0, 0, nrCells-1), (m1[8].transpose()).getMatrix(0, 0, 0, nrCells-1), (m1[9].transpose()).getMatrix(0, 0, 0, nrCells-1), cellLengths2, 60); 
							for (int i1 = 0; i1<nrOutput; i1++) {
								FE[i1].set(i, k, resf[i1]);
							}
							FE_X.set(i, k, f_RMSEK);
					FE_V.set(i, k, f_RMSEV);
					FE_TR.set(i, k, f_RMSETR);
						}

						//ArrayList<Double[]> densities = new ArrayList<Double[]>();
						//for (double t: m1[10].getArray()[0]) {

					}
				}
				double[][] f = new double[nrOutput][FE[0].getColumnDimension()];
				//double[] fk = new double[FE_X.getColumnDimension()];
				//double[] fv = new double[FE_X.getColumnDimension()];
				//double[] ftr = new double[FE_X.getColumnDimension()];
				for (int j=0; j< FE[0].getColumnDimension(); j++) {
					double[] gem = new double[nrOutput];
					for (int k=0; k<nrOutput; k++) {
				double gemX = 0;
				double gemV = 0;
				double gemTR = 0;
				}
					int k =0;
					for (int i = 0; i< FE[0].getRowDimension(); i++) {

						if (!(FE[0].get(i, j) == -1)) {
							for (int o = 0; o<nrOutput; o++) {
								gem[o] += FE[o].get(i, j);


							}


						gemX += FE_X.get(i, j);
						gemV += FE_V.get(i, j);
						gemTR += FE_TR.get(i, j);
							k++;
						}


					}
					for (int o = 0; o<nrOutput; o++) {
						f[o][j] += gem[o]/k;


					}
					fk[j] =  gemX/k;
				fv[j] =  gemV/k;
				ftr[j] = gemTR/k;
				}
				double[] fres = new double[nrOutput*f[0].length];
				int pos=0;
				for (double[] a: f) {
					System.arraycopy(a, 0, fres, pos, a.length);
					pos += a.length;
				}
				//out.println("fk="+Arrays.toString(fk)+";");
				//out.println("fv="+Arrays.toString(fv)+";");
				//out.println("ftr="+Arrays.toString(ftr)+";");

				 */
				double[] finalresults = new double[res.length + fres.length];
				System.arraycopy(res, 0, finalresults, 0, res.length);
				System.arraycopy(fres, 0, finalresults, res.length, fres.length);
				out.println("result=" + Arrays.toString(finalresults) +";");
			} else {
				out.println("result=" + Arrays.toString(res) +";");
			}
			//System.arraycopy(fv, 0, finalresults, res.length+fk.length, fv.length);
			//System.arraycopy(ftr, 0, finalresults, res.length+fk.length+fv.length, ftr.length);

			//out.println("result=" + Arrays.toString(CalcOutput.roundToSignificantFigures(5, finalresults)) +";");

			//double[] f_x = 
			//}
			//f_x.add(new Double[](){m1[5].transpose().getArray()[0]);


			/*out.println("%k=" + k);
					out.println("f_time{"+(i+1)+"}("+(k+1)+",:)=" + Arrays.toString(m1[10].getArray()[0]) + ";");
					//return new Matrix[]{xmin,y,F,Pnew,observations,x,P};
					//out.println("f_xmin{"+(i+1)+"}("+(k+1)+",:)="+Arrays.toString(m[0].transpose().getArray()[0])+";");
					//out.println("f_ymin{"+(i+1)+"}("+(k+1)+",:)="+Arrays.toString(m[1].transpose().getArray()[0])+";");
					out.println("f_x{"+(i+1)+"}("+(k+1)+",:)="+Arrays.toString(m1[5].transpose().getArray()[0])+";");
					//out.println("f_obs{"+(i+1)+"}("+(k+1)+",:)="+Arrays.toString(m[4].transpose().getArray()[0])+";");
					out.println("f_y{"+(i+1)+"}("+(k+1)+",:)="+Arrays.toString(m1[7].transpose().getArray()[0])+";");
					out.println("f_v{"+(i+1)+"}("+(k+1)+",:)="+Arrays.toString(m1[8].transpose().getArray()[0])+";");
					out.println("f_tr{"+(i+1)+"}("+(k+1)+",:)="+Arrays.toString(m1[9].transpose().getArray()[0])+";");*/

			/*for (int i = 0; i<m[0].getRowDimension(); i++) {
					out.println("xmin["+k+1+"]("+(i+1)+",:)="+Arrays.toString(m[0].getArray()[i]));
				}*/
			//m[0].pr;

			//ArrayList<Matrix[]> results2 = forecasts.get(i);


			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void exportSummarizedResultsToMatlab(double[][] results, double[] forecastResult, String filename, boolean fc, EnKFRunConfiguration runConfig) {



		PrintWriter out;
		if (filename.isEmpty())
			filename = "testEnKFSum";
		try {
			out= new PrintWriter(new BufferedWriter(new FileWriter(filename+".m")));
			//out.println("%config="+runConfig.toString());
			System.out.println(runConfig.toString());
			//out.println("clear;");
			Matrix tmp = new Matrix(results);
			double[][] results2 = (tmp.transpose()).getArray();

			double[] res = CalcOutput.calcSumIndicators(Arrays.copyOfRange(results2, 0, 13),Arrays.copyOfRange(results2, 13, 26));
			System.out.println(Arrays.toString(res));

			//double[] res = CalcOutput.calcOutput(m[0],m[1],m[2], Xa, V, Tr, cellLengths2, 2); 

			//boolean fc = false;
			if (fc) {
				/*Matrix FXa = new Matrix((forecast.length), nrCells,0);
				Matrix FV = new Matrix((forecast.length), nrCells,0);
				Matrix FTr = new Matrix((forecast.length), nrCells,0);
				for (int k = 0; k< forecast.length; k++) {
					Matrix[] m1 = forecast[k];
					FXa.setMatrix(k, k, 0, nrCells-1,m1[0].transpose());
					FV.setMatrix(k, k, 0, nrCells-1,m1[1].transpose());
					FTr.setMatrix(k, k, 0, nrCells-1,m1[2].transpose());
					//times.add(m1[19].get(0,0));
				}
				int beginFHorizon = pointOfForecast;
				//int endFHorizon = beginFHorizon + forecast.length-1;
				int[] endFHorizons = new int[]{
						beginFHorizon + 149,
						beginFHorizon + 449,
						beginFHorizon + 899,
						beginFHorizon + 1599,
				};
				double fres[] = new double[endFHorizons.length*13];
				int i = 0;
				for (int endFHorizon: endFHorizons) {
					double[] fr = CalcOutput.calcOutput(m[0].getMatrix(beginFHorizon, endFHorizon, 0, nrCells-1),m[1].getMatrix(beginFHorizon, endFHorizon, 0, nrCells-1),m[2].getMatrix(beginFHorizon, endFHorizon, 0, nrCells-1), FXa.getMatrix(0, endFHorizon-beginFHorizon, 0, nrCells-1), FV.getMatrix(0, endFHorizon-beginFHorizon, 0, nrCells-1), FTr.getMatrix(0, endFHorizon-beginFHorizon, 0, nrCells-1), cellLengths2, 2); 
					System.arraycopy(fr, 0, fres, i, 13);
					i=i+13;
				}

				 */
				double[] finalresults = new double[res.length + forecastResult.length];
				System.arraycopy(res, 0, finalresults, 0, res.length);
				System.arraycopy(forecastResult, 0, finalresults, res.length, forecastResult.length);
				out.println("result=" + Arrays.toString(finalresults) +";");
			} else {
				out.println("result=" + Arrays.toString(res) +";");
			}


			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void exportEnsemblesToMatlab(Matrix[][] results, String filename) {
		PrintWriter out;
		if (filename.isEmpty())
			filename = "testEnKFEnsembles";
		try {
			out= new PrintWriter(new BufferedWriter(new FileWriter(filename+".m")));
			int i = 1;
			for (Matrix[] m: results) {
				int j = 1;
				for (Matrix m1: m) {
					out.println("X"+i+"("+j+",:)="+Arrays.toString(m1.transpose().getArray()[0])+";");
					j++;
				}
				i++;
			}

			out.close();


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void exportFCtoMatlab(Matrix truth, Matrix forecast, String filename, EnKFRunConfiguration runConfig) {
		PrintWriter out;
		if (filename.isEmpty())
			filename = "testEnKF";
		try {
			boolean extendedOutput=false;
			out = new PrintWriter(new BufferedWriter(new FileWriter(filename+".m")));
			//out.println("clear;");
			out.println("%config="+runConfig.toString());
			out.println("nrCells="+nrCells+";");
			/*
			for (int[] route: routesInt) {
				int r = routesInt.indexOf(route);
				out.println("CL{"+(r+1)+"}="+Arrays.toString(cellLengths.get(r))+";");
			for (int i = 0; i< truth.getRowDimension();i++) {

				out.println("FX{"+(r+1)+"}("+(i+1)+",:)="+Arrays.toString((truth.getMatrix(i, i,route)).getArray()[0])+";");
				out.println("TruthX{"+(r+1)+"}("+(i+1)+",:)="+Arrays.toString((forecast.getMatrix(i, i,route)).getArray()[0])+";");

			}
			}*/
			for (int i = 0; i< truth.getRowDimension();i++) {

				out.println("FX("+(i+1)+",:)="+Arrays.toString((forecast.getMatrix(i, i,0,truth.getColumnDimension()-1)).getArray()[0])+";");
				out.println("TruthX("+(i+1)+",:)="+Arrays.toString((truth.getMatrix(i, i,0,forecast.getColumnDimension()-1)).getArray()[0])+";");

			}

			out.close();
			System.out.println("test");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void  exportToMatlab2(Matrix[][] results, Matrix[][] forecast, String filename, boolean plot) {
		PrintWriter out;
		if (filename.isEmpty())
			filename = "testEnKF";
		try {
			boolean extendedOutput=false;
			out = new PrintWriter(filename+".m");
			//out.println("clear;");
			out.println("nrCells="+nrCells+";");

			//Matrix[] m = results[(results.length-1)];
			//Matrix truth = ;
			/*for (int i = 0; i < m[3].getRowDimension();i++) {
				out.println("truth("+(i+1)+",:)="+Arrays.toString(m[3].getArray()[i])+";");
				out.println("truthV("+(i+1)+",:)="+Arrays.toString(m[2].getArray()[i])+";");
				out.println("truthObs("+(i+1)+",:)="+Arrays.toString(m[0].getArray()[i])+";");
				out.println("truthTR("+(i+1)+",:)="+Arrays.toString(m[4].getArray()[i])+";");
			}*/

			for (int i = 0; i< results.length-1;i++) {
				Matrix[] m = results[i]; 
				out.println("X("+(i+1)+",:)="+Arrays.toString((m[0].transpose()).getArray()[0])+";");
				out.println("V("+(i+1)+",:)="+Arrays.toString((m[1].transpose()).getArray()[0])+";");
				out.println("TR("+(i+1)+",:)="+Arrays.toString((m[2].transpose()).getArray()[0])+";");
			}
			Matrix[] m1 = results[(results.length-1)];
			for (int i = 0; i< m1[0].getRowDimension();i++) {

				out.println("TruthX("+(i+1)+",:)="+Arrays.toString(m1[0].getArray()[i])+";");
				out.println("TruthV("+(i+1)+",:)="+Arrays.toString(m1[1].getArray()[i])+";");
				out.println("TruthTR("+(i+1)+",:)="+Arrays.toString(m1[2].getArray()[i])+";");
			}
			for (int i = 0; i< forecast.length;i++) {
				Matrix[] m = forecast[i]; 
				out.println("FX("+(i+1)+",:)="+Arrays.toString((m[0].transpose()).getArray()[0])+";");
				out.println("FV("+(i+1)+",:)="+Arrays.toString((m[1].transpose()).getArray()[0])+";");
				out.println("FTR("+(i+1)+",:)="+Arrays.toString((m[2].transpose()).getArray()[0])+";");
			}
			/*Matrix Xa = new Matrix((results.length-1), nrCells,0);
			Matrix V = new Matrix((results.length-1), nrCells,0);
			Matrix Tr = new Matrix((results.length-1), nrCells,0);
			ArrayList<Double> times = new ArrayList<Double>();
			for (int k = 0; k< results.length-1; k++) {
				Matrix[] m1 = results[k];
				Xa.setMatrix(k, k, 0, nrCells-1,m1[0].transpose());
				V.setMatrix(k, k, 0, nrCells-1,m1[1].transpose());
				Tr.setMatrix(k, k, 0, nrCells-1,m1[2].transpose());
				//times.add(m1[19].get(0,0));
			}
			out.println("nrTimes = "+(results.size()-1) + ";");
			if (plot) {
				out.println("close all; ");
				out.println("t = (1:nrTimes)*60;");
				out.println("len = " + Arrays.toString(cellLengths2) + ";");
				out.println("cumlen = cumsum(len);");
				out.println("bottom = min(min(min(truthObs(:,1:"+nrSpeedObservations+"))),min(min(gemH(:,1:"+nrSpeedObservations+"))));");
				out.println("top = max(max(max(truthObs(:,1:"+nrSpeedObservations+"))),max(max(gemH(:,1:"+nrSpeedObservations+"))));");
				out.println("subplot(3,1,1);");
				out.println("imagesc(truthObs(2:end,1:"+nrSpeedObservations+")',[bottom top]);");
				out.println("subplot(3,1,2);");
				out.println("imagesc(gemH(:,1:"+nrSpeedObservations+")',[bottom top]);");



				out.println("subplot(3,1,3);");
				out.println("imagesc(truthObs(2:end,1:"+nrSpeedObservations+")'-gemH(:,1:"+nrSpeedObservations+")');");

				out.println("figure;");
				out.println("bottom = min(min(min(truthObs(:,"+(nrSpeedObservations+1)+":"+(nrObservations)+"))),min(min(gemH(:,"+(nrSpeedObservations+1)+":"+(nrObservations)+"))));");
				out.println("top = max(max(max(truthObs(:,"+(nrSpeedObservations+1)+":"+(nrObservations)+"))),max(max(gemH(:,"+(nrSpeedObservations+1)+":"+(nrObservations)+"))));");

				out.println("subplot(3,1,1);");
				out.println("imagesc(truthObs(2:end,"+(nrSpeedObservations+1)+":"+(nrObservations)+")',[bottom top]);");
				out.println("subplot(3,1,2);");
				out.println("imagesc(gemH(:,"+(nrSpeedObservations+1)+":"+(nrObservations)+")',[bottom top]);");



				out.println("subplot(3,1,3);");
				out.println("imagesc(truthObs(2:end,"+(nrSpeedObservations+1)+":"+(nrObservations)+")'-gemH(:,"+(nrSpeedObservations+1)+":"+(nrObservations)+")');");

				out.println("figure;");
				out.println("bottom = min(min(min(truth(2:end,1:"+(nrCells)+"))),min(min(gemX(:,1:"+(nrCells)+"))));");
				out.println("top = max(max(max(truth(2:end,1:"+(nrCells)+"))),max(max(gemX(:,1:"+(nrCells)+"))));");

				out.println("subplot(3,1,1);");
				out.println("uimagesc(t,cumlen,truth(2:end,1:"+(nrCells)+")',[bottom top]);");

				out.println("subplot(3,1,2);");
				out.println("uimagesc(t,cumlen,gemX(:,1:"+(nrCells)+")',[bottom top]);");
				out.println("subplot(3,1,3);");
				out.println("uimagesc(t,cumlen,truth(2:end,1:"+(nrCells)+")'-gemX(:,1:"+(nrCells)+")');");

				for (int i=0; i< routesInt.size(); i++) {
					out.println("r{"+(i+1)+"} = "+Arrays.toString(routesInt.get(i))+ " + 1;");
					out.println("lengths{"+(i+1)+"} = " + Arrays.toString(cellLengths.get(i)) + ";");
					out.println("cumlengths{"+(i+1)+"} = cumsum(lengths{"+(i+1)+"});");
					out.println("figure;");
					out.println("subplot(3,1,1);");
					out.println("uimagesc(t,cumlengths{"+(i+1)+"},truth(2:end,r{"+(i+1)+"})',[bottom top]);");

					out.println("subplot(3,1,2);");
					out.println("uimagesc(t,cumlengths{"+(i+1)+"},gemX(:,r{"+(i+1)+"})',[bottom top]);");
					out.println("subplot(3,1,3);");
					out.println("uimagesc(t,cumlengths{"+(i+1)+"},truth(2:end,r{"+(i+1)+"})'-gemX(:,r{"+(i+1)+"})');");
				}
				LinkedHashSet<MacroCell> cellsAtDetectors = new LinkedHashSet<MacroCell>();
				for (NodeDetector n: macromodel.getDetectors()) {
					cellsAtDetectors.add(n.getClosestCell());
				}
				out.println("detectorsAt = "+Arrays.toString(config.getIndices(macromodel, cellsAtDetectors))+";");
			 */

			/*out.println("figure;hold on");

				for (int j=0; j< nrObservations; j++ ) {
					out.println("subplot("+nrObservations+",1,"+(j+1)+")");
					out.println("plot(time(1:nrTimes),y(:,"+(j+1)+"),time(1:nrTimes),obs(:,"+(j+1)+"),time(1:nrTimes),ya(:,"+(j+1)+"))");
				}
				out.println("hold off");*/

			out.close();
			System.out.println("test");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void  exportToMatlab3(Matrix[][] results, int[] route, String filename, EnKFRunConfiguration runConfig) {
		PrintWriter out;
		if (filename.isEmpty())
			filename = "testEnKF";
		try {
			boolean extendedOutput=false;
			out = new PrintWriter(new BufferedWriter(new FileWriter(filename+".m")));
			//out.println("clear;");
			out.println("%config="+runConfig.toString());
			out.println("nrCells="+nrCells+";");

			//Matrix[] m = results[(results.length-1)];
			//Matrix truth = ;
			/*for (int i = 0; i < m[3].getRowDimension();i++) {
				out.println("truth("+(i+1)+",:)="+Arrays.toString(m[3].getArray()[i])+";");
				out.println("truthV("+(i+1)+",:)="+Arrays.toString(m[2].getArray()[i])+";");
				out.println("truthObs("+(i+1)+",:)="+Arrays.toString(m[0].getArray()[i])+";");
				out.println("truthTR("+(i+1)+",:)="+Arrays.toString(m[4].getArray()[i])+";");
			}*/

			for (int i = 0; i< results.length-1;i++) {
				Matrix[] m = results[i]; 
				/*Matrix m0 = m[0];
				m0.getColumnDimension(); //n
				Matrix m1 = (m[0].transpose()).getMatrix(0, 0,route);*/

				out.println("X("+(i+1)+",:)="+Arrays.toString((m[0].transpose().getMatrix(0, 0,route)).getArray()[0])+";");
				//out.println("In("+(i+1)+",:)="+Arrays.toString((m[1].transpose()).getArray()[0])+";");
				//out.println("TF("+(i+1)+",:)="+Arrays.toString((m[2].transpose()).getArray()[0])+";");

				//out.println("V("+(i+1)+",:)="+Arrays.toString((m[1].transpose()).getArray()[0])+";");
				//out.println("TR("+(i+1)+",:)="+Arrays.toString((m[2].transpose()).getArray()[0])+";");
			}
			Matrix[] m1 = results[(results.length-1)];
			for (int i = 0; i< m1[0].getRowDimension();i++) {

				out.println("TruthX("+(i+1)+",:)="+Arrays.toString(m1[0].getMatrix(i, i,route).getArray()[0])+";");
				//out.println("TruthIn("+(i+1)+",:)="+Arrays.toString(m1[1].getArray()[i])+";");
				//out.println("TruthTF("+(i+1)+",:)="+Arrays.toString(m1[2].getArray()[i])+";");

				//out.println("TruthV("+(i+1)+",:)="+Arrays.toString(m1[1].getArray()[i])+";");
				//out.println("TruthTR("+(i+1)+",:)="+Arrays.toString(m1[2].getArray()[i])+";");
			}
			for (int i = 0; i< m1[1].getRowDimension();i++) {

				out.println("TruthV("+(i+1)+",:)="+Arrays.toString(m1[1].getMatrix(i, i,route).getArray()[0])+";");
				//out.println("TruthIn("+(i+1)+",:)="+Arrays.toString(m1[1].getArray()[i])+";");
				//out.println("TruthTF("+(i+1)+",:)="+Arrays.toString(m1[2].getArray()[i])+";");

				//out.println("TruthV("+(i+1)+",:)="+Arrays.toString(m1[1].getArray()[i])+";");
				//out.println("TruthTR("+(i+1)+",:)="+Arrays.toString(m1[2].getArray()[i])+";");
			}
			/*Matrix Xa = new Matrix((results.length-1), nrCells,0);
			Matrix V = new Matrix((results.length-1), nrCells,0);
			Matrix Tr = new Matrix((results.length-1), nrCells,0);
			ArrayList<Double> times = new ArrayList<Double>();
			for (int k = 0; k< results.length-1; k++) {
				Matrix[] m1 = results[k];
				Xa.setMatrix(k, k, 0, nrCells-1,m1[0].transpose());
				V.setMatrix(k, k, 0, nrCells-1,m1[1].transpose());
				Tr.setMatrix(k, k, 0, nrCells-1,m1[2].transpose());
				//times.add(m1[19].get(0,0));
			}
			out.println("nrTimes = "+(results.size()-1) + ";");
			if (plot) {
				out.println("close all; ");
				out.println("t = (1:nrTimes)*60;");
				out.println("len = " + Arrays.toString(cellLengths2) + ";");
				out.println("cumlen = cumsum(len);");
				out.println("bottom = min(min(min(truthObs(:,1:"+nrSpeedObservations+"))),min(min(gemH(:,1:"+nrSpeedObservations+"))));");
				out.println("top = max(max(max(truthObs(:,1:"+nrSpeedObservations+"))),max(max(gemH(:,1:"+nrSpeedObservations+"))));");
				out.println("subplot(3,1,1);");
				out.println("imagesc(truthObs(2:end,1:"+nrSpeedObservations+")',[bottom top]);");
				out.println("subplot(3,1,2);");
				out.println("imagesc(gemH(:,1:"+nrSpeedObservations+")',[bottom top]);");



				out.println("subplot(3,1,3);");
				out.println("imagesc(truthObs(2:end,1:"+nrSpeedObservations+")'-gemH(:,1:"+nrSpeedObservations+")');");

				out.println("figure;");
				out.println("bottom = min(min(min(truthObs(:,"+(nrSpeedObservations+1)+":"+(nrObservations)+"))),min(min(gemH(:,"+(nrSpeedObservations+1)+":"+(nrObservations)+"))));");
				out.println("top = max(max(max(truthObs(:,"+(nrSpeedObservations+1)+":"+(nrObservations)+"))),max(max(gemH(:,"+(nrSpeedObservations+1)+":"+(nrObservations)+"))));");

				out.println("subplot(3,1,1);");
				out.println("imagesc(truthObs(2:end,"+(nrSpeedObservations+1)+":"+(nrObservations)+")',[bottom top]);");
				out.println("subplot(3,1,2);");
				out.println("imagesc(gemH(:,"+(nrSpeedObservations+1)+":"+(nrObservations)+")',[bottom top]);");



				out.println("subplot(3,1,3);");
				out.println("imagesc(truthObs(2:end,"+(nrSpeedObservations+1)+":"+(nrObservations)+")'-gemH(:,"+(nrSpeedObservations+1)+":"+(nrObservations)+")');");

				out.println("figure;");
				out.println("bottom = min(min(min(truth(2:end,1:"+(nrCells)+"))),min(min(gemX(:,1:"+(nrCells)+"))));");
				out.println("top = max(max(max(truth(2:end,1:"+(nrCells)+"))),max(max(gemX(:,1:"+(nrCells)+"))));");

				out.println("subplot(3,1,1);");
				out.println("uimagesc(t,cumlen,truth(2:end,1:"+(nrCells)+")',[bottom top]);");

				out.println("subplot(3,1,2);");
				out.println("uimagesc(t,cumlen,gemX(:,1:"+(nrCells)+")',[bottom top]);");
				out.println("subplot(3,1,3);");
				out.println("uimagesc(t,cumlen,truth(2:end,1:"+(nrCells)+")'-gemX(:,1:"+(nrCells)+")');");

				for (int i=0; i< routesInt.size(); i++) {
					out.println("r{"+(i+1)+"} = "+Arrays.toString(routesInt.get(i))+ " + 1;");
					out.println("lengths{"+(i+1)+"} = " + Arrays.toString(cellLengths.get(i)) + ";");
					out.println("cumlengths{"+(i+1)+"} = cumsum(lengths{"+(i+1)+"});");
					out.println("figure;");
					out.println("subplot(3,1,1);");
					out.println("uimagesc(t,cumlengths{"+(i+1)+"},truth(2:end,r{"+(i+1)+"})',[bottom top]);");

					out.println("subplot(3,1,2);");
					out.println("uimagesc(t,cumlengths{"+(i+1)+"},gemX(:,r{"+(i+1)+"})',[bottom top]);");
					out.println("subplot(3,1,3);");
					out.println("uimagesc(t,cumlengths{"+(i+1)+"},truth(2:end,r{"+(i+1)+"})'-gemX(:,r{"+(i+1)+"})');");
				}
				LinkedHashSet<MacroCell> cellsAtDetectors = new LinkedHashSet<MacroCell>();
				for (NodeDetector n: macromodel.getDetectors()) {
					cellsAtDetectors.add(n.getClosestCell());
				}
				out.println("detectorsAt = "+Arrays.toString(config.getIndices(macromodel, cellsAtDetectors))+";");
			 */

			/*out.println("figure;hold on");

				for (int j=0; j< nrObservations; j++ ) {
					out.println("subplot("+nrObservations+",1,"+(j+1)+")");
					out.println("plot(time(1:nrTimes),y(:,"+(j+1)+"),time(1:nrTimes),obs(:,"+(j+1)+"),time(1:nrTimes),ya(:,"+(j+1)+"))");
				}
				out.println("hold off");*/

			out.close();
			System.out.println("test");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void  exportToMatlab(ArrayList<Matrix[]> results, String filename, boolean plot) {
		PrintWriter out;
		if (filename.isEmpty())
			filename = "testEnKF";
		try {
			boolean extendedOutput=false;
			out = new PrintWriter(filename+".m");
			//out.println("clear;");
			out.println("nrCells="+nrCells+";");

			for (int k = 0; k< results.size()-1; k++) {
				Matrix[] m = results.get(k);
				//return new Matrix[]{X,Y,Xa,P,M,Z,observations, D, HX,gemH,gemX,gemXa,new Matrix(1,1)};
				out.println("%k=" + k);
				out.println("time("+(k+1)+",:)=" + Arrays.toString(m[19].getArray()[0]) + ";");
				out.println("gemH("+(k+1)+",:)="+Arrays.toString(m[9].transpose().getArray()[0])+";");
				out.println("gemX("+(k+1)+",:)="+Arrays.toString(m[10].transpose().getArray()[0])+";");
				out.println("gemXa("+(k+1)+",:)="+Arrays.toString(m[11].transpose().getArray()[0])+";");
				out.println("varH("+(k+1)+",:)="+Arrays.toString(m[12].transpose().getArray()[0])+";");
				out.println("varX("+(k+1)+",:)="+Arrays.toString(m[13].transpose().getArray()[0])+";");
				out.println("varXa("+(k+1)+",:)="+Arrays.toString(m[14].transpose().getArray()[0])+";");
				out.println("stdH("+(k+1)+",:)="+Arrays.toString(m[15].transpose().getArray()[0])+";");
				out.println("stdX("+(k+1)+",:)="+Arrays.toString(m[16].transpose().getArray()[0])+";");
				out.println("stdXa("+(k+1)+",:)="+Arrays.toString(m[17].transpose().getArray()[0])+";");
				out.println("gemV("+(k+1)+",:)="+Arrays.toString(m[1].transpose().getArray()[0])+";");
				out.println("gemTR("+(k+1)+",:)="+Arrays.toString(m[18].transpose().getArray()[0])+";");
				/*for (int i =0; i<nrEnsembles; i++) {
				out.println("D{"+(k+1)+"}("+(i+1)+",:)="+Arrays.toString(m[7].transpose().getArray()[i])+";");
				}*/


				/*for (int i =0; i<nrEnsembles; i++) {
				//return new Matrix[]{xmin,y,F,Pnew,observations,x,P};
				out.println("X_e{"+(i+1)+"}("+(k+1)+",:)="+Arrays.toString(m[0].transpose().getArray()[i])+";");
				out.println("Y_e{"+(i+1)+"}("+(k+1)+",:)="+Arrays.toString(m[1].transpose().getArray()[i])+";");
				out.println("D_e{"+(i+1)+"}("+(k+1)+",:)="+Arrays.toString(m[7].transpose().getArray()[i])+";");
				out.println("HX_e{"+(i+1)+"}("+(k+1)+",:)="+Arrays.toString(m[8].transpose().getArray()[i])+";");
				out.println("Xa_e{"+(i+1)+"}("+(k+1)+",:)="+Arrays.toString(m[2].transpose().getArray()[i])+";");
				//out.println("obs("+(k+1)+",:)="+Arrays.toString(m[4].transpose().getArray()[0])+";");
				//out.println("ya("+(k+1)+",:)="+Arrays.toString(m[7].transpose().getArray()[0])+";");
				}*/
				out.println("obs("+(k+1)+",:)="+Arrays.toString(m[6].transpose().getArray()[0])+";");

				if (extendedOutput) {
					for (int i = 0; i<m[0].getRowDimension(); i++) {
						out.println("xmin{"+(k+1)+"}("+(i+1)+",:)="+Arrays.toString(m[0].getArray()[i])+";");
						out.println("xa{"+(k+1)+"}("+(i+1)+",:)="+Arrays.toString(m[2].getArray()[i])+";");
					}
				}
				//m[0].pr;
			}
			Matrix[] m = results.get(results.size()-1);
			//Matrix truth = ;
			for (int i = 0; i < m[3].getRowDimension();i++) {
				out.println("truth("+(i+1)+",:)="+Arrays.toString(m[3].getArray()[i])+";");
				out.println("truthV("+(i+1)+",:)="+Arrays.toString(m[2].getArray()[i])+";");
				out.println("truthObs("+(i+1)+",:)="+Arrays.toString(m[0].getArray()[i])+";");
				out.println("truthTR("+(i+1)+",:)="+Arrays.toString(m[4].getArray()[i])+";");
			}
			/*out.println("X=mean(reshape(cell2mat(X_e), [ size(X_e{1}), length(X_e) ]), ndims(X_e{1})+1);");
			out.println("Y=mean(reshape(cell2mat(Y_e), [ size(Y_e{1}), length(Y_e) ]), ndims(Y_e{1})+1);");
			out.println("D=mean(reshape(cell2mat(D_e), [ size(D_e{1}), length(D_e) ]), ndims(D_e{1})+1);");
			out.println("HX=mean(reshape(cell2mat(HX_e), [ size(HX_e{1}), length(HX_e) ]), ndims(HX_e{1})+1);");
			out.println("Xa=mean(reshape(cell2mat(Xa_e), [ size(Xa_e{1}), length(Xa_e) ]), ndims(Xa_e{1})+1);");
			 */
			/*Matrix m = results.get(results.size()-1)[0];
			int nrRows = m.getRowDimension();
			out.println("%TTs");
			for (int k = 0; k<nrRows/2; k++) {
				out.println("ttTime("+(k+1)+",:)=" + Arrays.toString(m.getArray()[k*2]) + ";");
				out.println("ttValues("+(k+1)+",:)=" + Arrays.toString(m.getArray()[k*2+1]) + ";");

			}*/
			out.println("nrTimes = "+(results.size()-1) + ";");
			if (plot) {
				out.println("close all; ");
				out.println("t = (1:nrTimes)*60;");
				out.println("len = " + Arrays.toString(cellLengths2) + ";");
				out.println("cumlen = cumsum(len);");
				out.println("bottom = min(min(min(truthObs(:,1:"+nrSpeedObservations+"))),min(min(gemH(:,1:"+nrSpeedObservations+"))));");
				out.println("top = max(max(max(truthObs(:,1:"+nrSpeedObservations+"))),max(max(gemH(:,1:"+nrSpeedObservations+"))));");
				out.println("subplot(3,1,1);");
				out.println("imagesc(truthObs(2:end,1:"+nrSpeedObservations+")',[bottom top]);");
				out.println("subplot(3,1,2);");
				out.println("imagesc(gemH(:,1:"+nrSpeedObservations+")',[bottom top]);");



				out.println("subplot(3,1,3);");
				out.println("imagesc(truthObs(2:end,1:"+nrSpeedObservations+")'-gemH(:,1:"+nrSpeedObservations+")');");

				out.println("figure;");
				out.println("bottom = min(min(min(truthObs(:,"+(nrSpeedObservations+1)+":"+(nrObservations)+"))),min(min(gemH(:,"+(nrSpeedObservations+1)+":"+(nrObservations)+"))));");
				out.println("top = max(max(max(truthObs(:,"+(nrSpeedObservations+1)+":"+(nrObservations)+"))),max(max(gemH(:,"+(nrSpeedObservations+1)+":"+(nrObservations)+"))));");

				out.println("subplot(3,1,1);");
				out.println("imagesc(truthObs(2:end,"+(nrSpeedObservations+1)+":"+(nrObservations)+")',[bottom top]);");
				out.println("subplot(3,1,2);");
				out.println("imagesc(gemH(:,"+(nrSpeedObservations+1)+":"+(nrObservations)+")',[bottom top]);");



				out.println("subplot(3,1,3);");
				out.println("imagesc(truthObs(2:end,"+(nrSpeedObservations+1)+":"+(nrObservations)+")'-gemH(:,"+(nrSpeedObservations+1)+":"+(nrObservations)+")');");

				out.println("figure;");
				out.println("bottom = min(min(min(truth(2:end,1:"+(nrCells)+"))),min(min(gemX(:,1:"+(nrCells)+"))));");
				out.println("top = max(max(max(truth(2:end,1:"+(nrCells)+"))),max(max(gemX(:,1:"+(nrCells)+"))));");

				out.println("subplot(3,1,1);");
				out.println("uimagesc(t,cumlen,truth(2:end,1:"+(nrCells)+")',[bottom top]);");

				out.println("subplot(3,1,2);");
				out.println("uimagesc(t,cumlen,gemX(:,1:"+(nrCells)+")',[bottom top]);");
				out.println("subplot(3,1,3);");
				out.println("uimagesc(t,cumlen,truth(2:end,1:"+(nrCells)+")'-gemX(:,1:"+(nrCells)+")');");

				for (int i=0; i< routesInt.size(); i++) {
					out.println("r{"+(i+1)+"} = "+Arrays.toString(routesInt.get(i))+ " + 1;");
					out.println("lengths{"+(i+1)+"} = " + Arrays.toString(cellLengths.get(i)) + ";");
					out.println("cumlengths{"+(i+1)+"} = cumsum(lengths{"+(i+1)+"});");
					out.println("figure;");
					out.println("subplot(3,1,1);");
					out.println("uimagesc(t,cumlengths{"+(i+1)+"},truth(2:end,r{"+(i+1)+"})',[bottom top]);");

					out.println("subplot(3,1,2);");
					out.println("uimagesc(t,cumlengths{"+(i+1)+"},gemX(:,r{"+(i+1)+"})',[bottom top]);");
					out.println("subplot(3,1,3);");
					out.println("uimagesc(t,cumlengths{"+(i+1)+"},truth(2:end,r{"+(i+1)+"})'-gemX(:,r{"+(i+1)+"})');");
				}
				LinkedHashSet<MacroCell> cellsAtDetectors = new LinkedHashSet<MacroCell>();
				for (NodeDetector n: macromodel.getDetectors()) {
					cellsAtDetectors.add(n.getClosestCell());
				}
				out.println("detectorsAt = "+Arrays.toString(config.getIndices(macromodel, cellsAtDetectors))+";");


				/*out.println("figure;hold on");

				for (int j=0; j< nrObservations; j++ ) {
					out.println("subplot("+nrObservations+",1,"+(j+1)+")");
					out.println("plot(time(1:nrTimes),y(:,"+(j+1)+"),time(1:nrTimes),obs(:,"+(j+1)+"),time(1:nrTimes),ya(:,"+(j+1)+"))");
				}
				out.println("hold off");*/
			}
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public LinkedHashSet<MacroCell> returnSurroundingCells(MacroCell center, int nrLevels) {

		LinkedHashSet<MacroCell> result = new LinkedHashSet<MacroCell>();
		HashSet<MacroCell> currentLevel = new HashSet<MacroCell>();
		HashSet<MacroCell> nextLevel = new HashSet<MacroCell>();


		currentLevel.add(center);
		result.add(center);
		for (int level = 0; level<nrLevels; level++) {
			for (MacroCell cell: currentLevel) {
				nextLevel.addAll(cell.downs);
			}
			result.addAll(nextLevel);
			currentLevel.clear();
			currentLevel.addAll(nextLevel);
			nextLevel.clear();
		}
		currentLevel.clear();
		currentLevel.add(center);

		for (int level = 0; level<nrLevels; level++) {
			for (MacroCell cell: currentLevel) {
				nextLevel.addAll(cell.ups);
			}
			result.addAll(nextLevel);
			currentLevel.clear();
			currentLevel.addAll(nextLevel);
			nextLevel.clear();
		}



		return result;
	}
	public LinkedHashSet<MacroCell> returnSurroundingCellsIncludingDiverges(MacroCell center, int nrLevels) {

		LinkedHashSet<MacroCell> result = new LinkedHashSet<MacroCell>();
		HashSet<MacroCell> currentLevel = new HashSet<MacroCell>();
		HashSet<MacroCell> nextLevel = new HashSet<MacroCell>();


		currentLevel.add(center);
		result.add(center);
		for (int level = 0; level<nrLevels; level++) {
			for (MacroCell cell: currentLevel) {
				for (MacroCell c: cell.downs) {
					if ((!currentLevel.contains(c)) &&  (!result.contains(c)))
						nextLevel.add(c);
				}
				for (MacroCell c: cell.ups) {
					if ((!currentLevel.contains(c)) &&  (!result.contains(c)))
						nextLevel.add(c);
				}
			}

			result.addAll(nextLevel);
			currentLevel.clear();
			currentLevel.addAll(nextLevel);
			nextLevel.clear();
		}

		return result;
	}
	static Matrix solveInversePStraightForward(Matrix R, Matrix HA, int N, Matrix solve) {
		Matrix P = R.plus((HA.times(HA.transpose())).times(1.0/(N-1)));
		CholeskyDecomposition L = P.chol();
		Matrix M = L.solve(solve);
		return M;
	}
	static Matrix[] solveInversePStraightForwardMult(Matrix R, Matrix HA, int N, Matrix solve1, Matrix solve2) {
		Matrix P = R.plus((HA.times(HA.transpose())).times(1.0/(N-1)));
		CholeskyDecomposition L = P.chol();
		return new Matrix[]{L.solve(solve1),L.solve(solve2)};
	}
	static Matrix solveInversePShermanMorrisonWoodbury(Matrix Rinv, Matrix HA, int N, Matrix solve) {
		long beginTQ  = System.nanoTime();
		//totalFormMatrices += System.nanoTime()-beginFormMatrices;
		Matrix T = JamaExtension.diagTimesRight((HA.transpose()),Rinv);
		Matrix Q = JamaExtension.plusIdentity((T.times(HA)).times(1.0/(N-1)));
		long totalTQ = System.nanoTime()-beginTQ;
		long beginQchol  = System.nanoTime();
		CholeskyDecomposition L = Q.chol();
		long totalQchol = System.nanoTime()-beginQchol;
		long totalQcholRedo;
		if (!L.isSPD()) {
			long beginQcholRedo  = System.nanoTime();
			//System.out.println("not SPD");
			Matrix q = Q.minus(Q.transpose());
			Q = (Q.minus(q.times(0.5)));
			L=Q.chol();
			totalQcholRedo = System.nanoTime()-beginQcholRedo;

		} else {
			totalQcholRedo = 0;
		}
		long beginSolve = System.nanoTime();
		Matrix We = (L.solve(T.times(solve)));
		Matrix M = JamaExtension.diagTimes(Rinv, JamaExtension.plusIdentity((HA.times(We)).times(-1.0/(N-1))));
		//Matrix M1 = JamaExtension.diagTimes(Rinv, (HA.times(We)).times(-1.0/(N-1)));
		//Matrix M2 = JamaExtension.diagPlus(M1, Rinv);
		long totalSolve = System.nanoTime()-beginSolve;
		/*	System.out.println("totalTQ: " +totalTQ);
		System.out.println("totalQchol: " +totalQchol);
		System.out.println("totalQcholRedo: " +totalQcholRedo);
		System.out.println("totalSolve: " +totalSolve);*/
		return M;
	}
	static Matrix solveInversePShermanMorrisonWoodbury2(Matrix Rinv, Matrix HA, int N, Matrix solve) {
		long beginTQ  = System.nanoTime();
		//totalFormMatrices += System.nanoTime()-beginFormMatrices;
		Matrix T = JamaExtension.diagTimesRight((HA.transpose()),Rinv);
		Matrix Q = JamaExtension.plusIdentity((T.times(HA)).times(1.0/(N-1)));
		long totalTQ = System.nanoTime()-beginTQ;
		long beginQchol  = System.nanoTime();
		CholeskyDecomposition L = Q.chol();
		long totalQchol = System.nanoTime()-beginQchol;
		long totalQcholRedo;
		if (!L.isSPD()) {
			long beginQcholRedo  = System.nanoTime();
			//System.out.println("not SPD");
			Matrix q = Q.minus(Q.transpose());
			Q = (Q.minus(q.times(0.5)));
			L=Q.chol();
			totalQcholRedo = System.nanoTime()-beginQcholRedo;

		} else {
			totalQcholRedo = 0;
		}
		long beginSolve = System.nanoTime();
		Matrix We = (L.solve(T.times(solve)));
		Matrix M = JamaExtension.diagTimes(Rinv, solve.plus((HA.times(We)).times(-1.0/(N-1))));
		//Matrix M1 = JamaExtension.diagTimes(Rinv, (HA.times(We)).times(-1.0/(N-1)));
		//Matrix M2 = JamaExtension.diagPlus(M1, Rinv);
		long totalSolve = System.nanoTime()-beginSolve;
		/*	System.out.println("totalTQ: " +totalTQ);
		System.out.println("totalQchol: " +totalQchol);
		System.out.println("totalQcholRedo: " +totalQcholRedo);
		System.out.println("totalSolve: " +totalSolve);*/
		return M;
	}
	static Matrix[] solveInversePShermanMorrisonWoodburyMult2(Matrix Rinv, Matrix HA, int N, Matrix solve1, Matrix solve2) {
		long beginTQ  = System.nanoTime();
		//totalFormMatrices += System.nanoTime()-beginFormMatrices;
		Matrix T = JamaExtension.diagTimesRight((HA.transpose()),Rinv);
		Matrix Q = JamaExtension.plusIdentity((T.times(HA)).times(1.0/(N-1)));
		long totalTQ = System.nanoTime()-beginTQ;
		long beginQchol  = System.nanoTime();
		CholeskyDecomposition L = Q.chol();
		long totalQchol = System.nanoTime()-beginQchol;
		long totalQcholRedo;
		if (!L.isSPD()) {
			long beginQcholRedo  = System.nanoTime();
			//System.out.println("not SPD");
			Matrix q = Q.minus(Q.transpose());
			Q = (Q.minus(q.times(0.5)));
			L=Q.chol();
			totalQcholRedo = System.nanoTime()-beginQcholRedo;

		} else {
			totalQcholRedo = 0;
		}
		long beginSolve = System.nanoTime();
		Matrix We1 = (L.solve(T.times(solve1)));
		Matrix M1 = JamaExtension.diagTimes(Rinv, solve1.plus((HA.times(We1)).times(-1.0/(N-1))));
		Matrix We2 = (L.solve(T.times(solve2)));
		Matrix M2 = JamaExtension.diagTimes(Rinv, solve2.plus((HA.times(We2)).times(-1.0/(N-1))));
		//Matrix M1 = JamaExtension.diagTimes(Rinv, (HA.times(We)).times(-1.0/(N-1)));
		//Matrix M2 = JamaExtension.diagPlus(M1, Rinv);
		long totalSolve = System.nanoTime()-beginSolve;
		/*	System.out.println("totalTQ: " +totalTQ);
		System.out.println("totalQchol: " +totalQchol);
		System.out.println("totalQcholRedo: " +totalQcholRedo);
		System.out.println("totalSolve: " +totalSolve);*/
		return new Matrix[]{M1,M2};
	}
	/*static Matrix[] solveInversePShermanMorrisonWoodburyExt(Matrix Rinv, Matrix HA, int N, Matrix solve) {
		long beginTQ  = System.nanoTime();
		//totalFormMatrices += System.nanoTime()-beginFormMatrices;
		Matrix T = JamaExtension.diagTimesRight((HA.transpose()),Rinv);
		Matrix Q = JamaExtension.plusIdentity((T.times(HA)).times(1.0/(N-1)));
		long totalTQ = System.nanoTime()-beginTQ;
		long beginQchol  = System.nanoTime();
		CholeskyDecomposition L = Q.chol();
		long totalQchol = System.nanoTime()-beginQchol;
		long totalQcholRedo;
		if (!L.isSPD()) {
			long beginQcholRedo  = System.nanoTime();
			//System.out.println("not SPD");
			Matrix q = Q.minus(Q.transpose());
			Q = (Q.minus(q.times(0.5)));
			L=Q.chol();
			totalQcholRedo = System.nanoTime()-beginQcholRedo;

		} else {
			totalQcholRedo = 0;
		}
		long beginSolve = System.nanoTime();
		Matrix We = (L.solve(T.times(solve)));

		Matrix M = JamaExtension.diagTimes(Rinv, JamaExtension.plusIdentity((HA.times(We)).times(-1.0/(N-1))));
		long totalSolve = System.nanoTime()-beginSolve;

		Matrix times = new Matrix(new double[]{(double) totalTQ,(double) totalQchol,(double) totalQcholRedo,(double) totalSolve},1);
		return new Matrix[]{M,times};
	}*/
	static private int[] buildIntArray(List<Integer> integers) {
		int[] ints = new int[integers.size()];
		int i = 0;
		for (Integer n : integers) {
			ints[i++] = n;
		}
		return ints;
	}
	static private Integer[] buildIntArray(int[] integers) {
		Integer[] ints = new Integer[integers.length];
		int i = 0;
		for (int n : integers) {
			ints[i++] = Integer.valueOf(n);
		}
		return ints;
	}
	static int[] buildIntArray(Integer[] integers) {
		int[] ints = new int[integers.length];
		int i = 0;
		for (Integer n : integers) {
			ints[i++] = n;
		}
		return ints;
	}
	

}
