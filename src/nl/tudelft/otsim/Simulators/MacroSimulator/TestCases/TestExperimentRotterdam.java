package nl.tudelft.otsim.Simulators.MacroSimulator.TestCases;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import nl.tudelft.otsim.Events.Scheduler;
import nl.tudelft.otsim.GUI.FakeGraphicsPanel;
import nl.tudelft.otsim.GUI.Main;
import nl.tudelft.otsim.GUI.StandAlone;
import nl.tudelft.otsim.Simulators.MacroSimulator.MacroSimulator;
import nl.tudelft.otsim.Simulators.MacroSimulator.Model;
import nl.tudelft.otsim.Simulators.MacroSimulator.Nodes.NodeBoundaryIn;
import nl.tudelft.otsim.Simulators.MacroSimulator.Nodes.NodeInterior;
import nl.tudelft.otsim.Utilities.TimeScaleFunction;
import Jama.Matrix;

public class TestExperimentRotterdam {
	static boolean extendedOutput = false;
	public static void main(String[] args) {
		String folder = "C:\\Users\\Friso\\Documents\\Thesis";
		String input = "Netwerk_Rotterdam.txt";
		Path path = FileSystems.getDefault().getPath(folder, input);

		try {
			String fileload = new String(Files.readAllBytes(path), "UTF-8");
			String[] network = fileload.split("Detector",2);
			String networkDescription = network[0];
			String tmpsplits1 = "Detector" + network[1];
			String[] tmpsplits2 = tmpsplits1.split("Inflow",2);
			String detectors = tmpsplits2[0];
			String[] tmpsplits3 = ("Inflow"+tmpsplits2[1]).split("Turn",2);
			String inflows = tmpsplits3[0];
			String turns = ("Turn" + tmpsplits3[1]).split("FD",2)[0];
			String fd =  "FD"+(tmpsplits3[1]).split("FD",2)[1];
			String[] cells = networkDescription.split("\t");





			String param = "EndTime:\t7200.00\nSeed:\t1\nRouteBased:\tfalse\n";
			/*
		String network = "EndTime:\t7200.00\nSeed:\t1\n"
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
				+ "Detector:	3	(2000.000,-250.000,0.000)\n";*/



			String[] networksplit = networkDescription.split("\r\n");
			String networkAfterSplit1 = param;
			String networkAfterSplit2 = param;
			for (int i = 0; i<networksplit.length; i++) {
				String[] linesplit = networksplit[i].split("\t");
				/*networkAfterSplit1 += networksplit[i].concat("	fd	0.025	0.125	"+Math.min(Double.parseDouble(linesplit[7])/3.6-1, 22.222)+"	SMULDERS\n");
				networkAfterSplit2 += networksplit[i].concat("	fd	0.025	0.125	"+Math.min(Double.parseDouble(linesplit[7])/3.6-1, 22.222)+"	SMULDERS\n");
				 */		networkAfterSplit1 += networksplit[i].concat("\n");
				 networkAfterSplit2 += networksplit[i].concat("\n");

			}
			String[] networksplit2 = networkAfterSplit1.split("\n");
			int seed = 619;
			Random r = new Random(seed);
			int nrExperiments =1;
			boolean openGUI = true;
			int nrShows;
			//nrShows = 0;
			nrShows = Integer.MAX_VALUE;
			for (int j = 0; j< nrShows; j++) {

				Scheduler intSched;
				if (j== 0) {

					if (openGUI) {

						StandAlone.main(new String[]{"GenerateEvent=SelectTab 5", "GenerateEvent=zoomToRect 80000 425000 105000 445000"});
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						intSched = new Scheduler(MacroSimulator.simulatorType, Main.mainFrame.graphicsPanel, networkAfterSplit1+detectors+inflows + turns+fd);
					} else {
						intSched = new Scheduler(MacroSimulator.simulatorType, new FakeGraphicsPanel(), networkAfterSplit1+detectors+inflows + turns+fd);

					}}
				else {
					if (openGUI) {

						intSched = new Scheduler(MacroSimulator.simulatorType, Main.mainFrame.graphicsPanel, networkAfterSplit1+detectors+inflows + turns+fd);
					} else {
						intSched = new Scheduler(MacroSimulator.simulatorType, new FakeGraphicsPanel(), networkAfterSplit1+detectors+inflows + turns+fd);

					}				}

				//Scheduler intSched = new Scheduler(MacroSimulator.simulatorType, new FakeGraphicsPanel(), networkAfterSplit1+detectors);
				for (int i = 1; i < 360; i++) {
					//double time = 0;
					intSched.stepUpTo(i*20);
					intSched.getGraphicsPanel().repaint();;
					System.out.println(i*20);
				}
			}
			//scheduler.getGraphicsPanel().repaint();


			/*Model macromodel = (Model) intSched.getSimulator().getModel();
		ArrayList<Object> inf = AssimilationConfiguration.getStateVariables(macromodel, StateType.NODEIN);
		ArrayList<Object> turnf = AssimilationConfiguration.getStateVariables(macromodel, StateType.NODEJUNCTION);

		int nrInflows = inf.size();
		int nrTFs = turnf.size();*/
			String[] tfs = turns.split("\r\n");
			String[] inf = inflows.split("\r\n");
			int nrInflows = inf.length;
			int nrTFs = tfs.length;

			/*double[] cells2 = new double[nrTFs]; 
			for (int i = 1; i<cells.length; i++)
				cells2[i-1] = Double.parseDouble((cells[i].split("\t")[1]).split("\r\n")[0]);*/
			double[] inflowTruth = new double[nrInflows];
			double[] stdInflow = new double[nrInflows];
			double[] inflowTL = new double[nrInflows];
			double[] turnfractionTruth = new double[nrTFs];
			double[] stdTurnFraction = new double[nrTFs];
			int t = 0;

			for (String in: inf) {
				//String[] timescale = (in.split("\t")[2]).split(":");


				inflowTruth[t] = Double.parseDouble(((in.split("\t")[2]).split(":",2)[0]).split("/")[1]);
				stdInflow[t] = inflowTruth[t]/10;
				//inflowTL[t] = ((NodeBoundaryIn) n).getInflowPerLane();
				t++;
			}
			t = 0;

			for (String tf: tfs) {
				turnfractionTruth[t] = Double.parseDouble(tf.split("\t")[2]);
				stdTurnFraction[t] = turnfractionTruth[t]/20;
				t++;
			}


			/*
		double[] inflowTruth = new double[]{
				(1800.0),
				1600.0

		};*/
			/*double [] stdInflow = new double[] {
				200,
				150
		};*/
			double[][] inflow3 = new double[nrExperiments][inflowTruth.length];
			String[][] infExp = new String[nrExperiments][inflowTruth.length];
			for (int n =0; n<nrExperiments; n++) {

				for (int i = 0; i<inflowTruth.length; i++) {
					inflow3[n][i] = (Math.max(0, inflowTruth[i] + r.nextGaussian()*stdInflow[i]));
					infExp[n][i] = new TimeScaleFunction(new TimeScaleFunction(inf[i].split("\t")[2]),inflow3[n][i]/inflowTruth[i]).export();
				}

			}
			String[] inflowExperiments = new String[nrExperiments];
			for (int n =0; n<nrExperiments; n++) {
				String tmpString = "";
				for (int i = 0; i<inf.length; i++) {
					String[] tmp = inf[i].split("\t");
					tmpString = tmpString + tmp[0] + "\t" + tmp[1] + "\t" + infExp[n][i] + "\r\n";

				}
				inflowExperiments[n] = tmpString;
			}
			//String[nrExperiments] inflowExperiments = 
			/*String[] pattern1 = new String[nrInflows];
		for (int i = 0; i< nrInflows; i++) {
			pattern1[i] = "[0.000/"+inflowTruth[i]/1.5+":1500/"+inflowTruth[i]/1.5+":2400.000/"+inflowTruth[i]+":3000.000/"+inflowTruth[i]+":3900/"+inflowTruth[i]/2+":4200/"+inflowTruth[i]/2+"]";

		}*/
			String[] pattern1 = inf;

			double[][] turnfractionExp2 = new double[nrExperiments][turnfractionTruth.length];
			for (int n =0; n<nrExperiments; n++) {
				for (int i = 0; i<turnfractionTruth.length; i++) {
					turnfractionExp2[n][i] = Math.min(1,Math.max(0, turnfractionTruth[i] + r.nextGaussian()*stdTurnFraction[i]));
				}
			}
			String[] tfExperiments = new String[nrExperiments];
			for (int n =0; n<nrExperiments; n++) {
				String tmpString = "";
				for (int i = 0; i<tfs.length; i++) {
					String[] tmp = tfs[i].split("\t");
					tmpString = tmpString + tmp[0] + "\t" + tmp[1] + "\t" + turnfractionExp2[n][i] + "\r\n";

				}
				tfExperiments[n] = tmpString;
			}
			String otsimConfigurationTruth = networkAfterSplit1
					+ detectors
					+ inflows
					+ turns
					+ fd;
			/*String otsimConfigurationTruth = networkAfterSplit1
				+ "TripPatternPath	numberOfTrips:	"+pattern1[0]+"	NodePattern:	[origin ID=1 (0.00m, 0.00m, 0.00m), destination ID=2 (2500.00m, 0.00m, 0.00m)]\n"
				+ "Path:	"+turnfractionTruth[0]+"	nodes:	0	1	2	3\n"
				+ "Path:	"+(1-turnfractionTruth[0])+"	nodes:	0	1	5	6	7	2	3\n"
				+ "TripPatternPath	numberOfTrips:	"+pattern1[1]+"	NodePattern:	[origin ID=2 (0.00m, -500.00m, 0.00m), destination ID=2 (2500.00m, 0.00m, 0.00m)]\n"
				+ "Path:	1.00000	nodes:	4	5	6	7	2	3\n"
				+ detectors;*/

			/*String otsimConfiguration2 = networkAfterSplit2
				+ "TripPatternPath	numberOfTrips:	"+pattern2[0]+"	NodePattern:	[origin ID=1 (0.00m, 0.00m, 0.00m), destination ID=2 (2500.00m, 0.00m, 0.00m)]\n"
				+ "Path:	"+turnfractionExp[0]+"	nodes:	0	1	2	3\n"
				+ "Path:	"+(1-turnfractionExp[0])+"	nodes:	0	1	5	6	7	2	3\n"
		+ "TripPatternPath	numberOfTrips:	"+pattern2[1]+"	NodePattern:	[origin ID=2 (0.00m, -500.00m, 0.00m), destination ID=2 (2500.00m, 0.00m, 0.00m)]\n"
				+ "Path:	1.00000	nodes:	4	5	6	7	2	3\n"
				+ detectors;*/
			//nrExperiments = 1;

			ArrayList<String> networkConfigs = new ArrayList<String>();
			for (int i=0; i<nrExperiments; i++) {
				String networkConfig = networkAfterSplit2
						+ detectors
						+ inflowExperiments[i]
								+ tfExperiments[i]
										+ fd;
				/*String[] pattern3 = new String[] {
					"[0.000/"+inflow3[i][0]/1.5+":1800/"+inflow3[i][0]+":2400.000/"+inflow3[i][0]+":3900/"+inflow3[i][0]/2+":4200/"+inflow3[i][0]/2+"]",
					"[0.000/"+inflow3[i][1]/1.5+":2100/"+inflow3[i][1]+":3000.000/"+inflow3[i][1]+":4800/"+inflow3[i][1]/2+":5100/"+inflow3[i][1]/2+"]"
			};*/
				/*String[] pattern3 = new String[] {
					"[0.000/"+inflow3[i][0]/1.5+":1800/"+inflow3[i][0]/1.5+":3600/"+inflow3[i][0]+":4200.000/"+inflow3[i][0]+":5700/"+inflow3[i][0]/2+":6000/"+inflow3[i][0]/2+"]",
					"[0.000/"+inflow3[i][1]/1.5+":1800/"+inflow3[i][1]/1.5+":3900/"+inflow3[i][1]+":4800.000/"+inflow3[i][1]+":6400/"+inflow3[i][1]/2+":6900/"+inflow3[i][1]/2+"]"
			};*/
				/*String networkConfig = networkAfterSplit2
					+ "TripPatternPath	numberOfTrips:	"+pattern3[0]+"	NodePattern:	[origin ID=1 (0.00m, 0.00m, 0.00m), destination ID=2 (2500.00m, 0.00m, 0.00m)]\n"
					+ "Path:	"+turnfractionExp2[i][0]+"	nodes:	0	1	2	3\n"
					+ "Path:	"+(1-turnfractionExp2[i][0])+"	nodes:	0	1	5	6	7	2	3\n"
					+ "TripPatternPath	numberOfTrips:	"+pattern3[1]+"	NodePattern:	[origin ID=2 (0.00m, -500.00m, 0.00m), destination ID=2 (2500.00m, 0.00m, 0.00m)]\n"
					+ "Path:	1.00000	nodes:	4	5	6	7	2	3\n"
					+ detectors;*/
				networkConfigs.add(networkConfig);
			}
			int nrSteps = (int) (7200.0/2.0 +1);

			//int nrSteps = (int) (600.0/2.0 +1);
			//Matrix[] obsTest = TestEKF2.generateTruthData(otsimConfigurationTruth, nrSteps, 2.0);
			Matrix[] obsTest = TestEnKF.generateTruthValues(otsimConfigurationTruth, nrSteps, 2.0);



			ArrayList<ArrayList<ErrorConfiguration>> errorConfigs = new ArrayList<ArrayList<ErrorConfiguration>>();

			// first experiment: 
			/*double[] errorsK = new double[]{0.001,0.003,0.01};
		double[] errorsI = new double[]{0.03,0.1,0.3};
		double[] errorsT = new double[]{0.01,0.2,0.3};
		double[] errorsK = new double[]{0.003};
		double[] errorsI = new double[]{0.1};
		double[] errorsT = new double[]{0.9,1.5,2.5};

		for (double k: errorsK) {
			for (double i: errorsI) { 
				for (double tf: errorsT) { 
				ArrayList<ErrorConfiguration> errorList1a = new ArrayList<ErrorConfiguration>();
				errorList1a.add(new ErrorConfiguration(StateDefinition.K_CELL,k,1.00));
				errorList1a.add(new ErrorConfiguration(StateDefinition.INFLOW_NODE,i,1.00));
				errorList1a.add(new ErrorConfiguration(StateDefinition.TF_NODE,tf,1.00));
				errorConfigs.add(errorList1a);
				}
			}
		}*/

			//String prefix = Long.toString(System.currentTimeMillis());

			//long[][][] computationTimes = TestEnKF.performExperiment(obsTest, expConfig, prefix);
			int[] route = new int[]{2958, 2959, 2960, 2961, 2962, 2963, 2964, 2965, 2966, 2967, 2968, 2969, 2970, 2971, 2972, 2973, 2974, 2975, 1875, 1876, 1877, 1878, 1879, 1880, 1881, 1882, 1883, 1884, 1885, 1886, 1887, 1888, 1889, 1890, 1891, 1892, 1893, 1894, 1895, 1896, 1897, 1898, 1899, 1900, 1901, 1902, 1903, 1904, 1437, 1438, 1439, 1440, 2646, 2647, 2648, 2649, 2650, 2651, 2652, 2653, 2654, 2655, 2656, 2657, 2658, 2659, 2660, 2661, 2662, 2663, 2664, 2665, 2666, 2667, 2668, 2669, 2670, 2671, 2672, 2673, 2674, 2675, 2676, 2677, 2678, 2679, 4473, 4474, 4475, 4476, 4477, 4478, 4479, 4480, 4481, 4482, 4483, 4484, 4485, 4486, 4487, 4488, 4489, 4490, 3566, 3567, 3568, 3569, 3570, 3571, 3572, 3573, 3574, 3575, 3576, 3577, 3578, 3579, 3580, 3581, 3582, 3583, 3584, 3585, 3586, 3587, 3588, 3589, 3590, 3591, 3592, 3593, 3594, 3595, 3596, 3597, 3598, 3599, 3600, 3601, 3602, 3603, 3604, 3605, 3606, 3607, 3608, 3609, 3610, 3611, 3612, 3613, 3614, 3615, 3616, 3617, 3618, 3619, 3620, 3621, 3622, 3623, 3624, 3625, 3626, 3627, 3628, 3629, 3630, 3631, 3632, 3633, 3634, 3635, 3636, 3637, 3638, 3639, 3640, 3641, 3642, 3643, 3644, 3645, 3646, 3647, 3648, 3649, 3650, 3651, 3652, 3653, 3654, 3655, 3656, 3657, 3658, 3659, 3660, 3661, 3662, 3663, 3664, 3665, 3666, 3667, 3668, 3669, 3670, 3671, 3672, 3673, 2596, 2597, 2598, 2599, 2600, 2601, 2602, 2603, 2604, 2605, 2606, 2607, 2608, 2609, 2610, 2611, 2612, 2613, 3778, 3779, 3780, 3781, 3782, 3783, 3784, 3785, 3786, 3787, 3788, 3789, 3790, 3791, 3792, 3793, 3794, 3795, 3796, 3797, 3798, 3799, 3800, 3801, 3802, 3803, 3804, 3805, 3806, 3807, 3808, 3809, 3810, 3811, 3812, 3813, 3814, 3815, 3816, 3817, 3818, 3819, 3217, 3218, 3219, 3220, 3221, 3222, 3223, 3224, 3225, 3226, 3227, 3228, 3229, 3230, 3231, 2181, 2182, 2183, 2184, 2185, 2186, 2187, 2188, 2189, 2190, 2191, 2192, 2193, 2194, 2195, 2196, 2197, 2198, 2199, 2200, 2201, 2202, 2203, 2204, 553, 554, 555, 556, 557, 558, 559, 560, 561, 562, 563, 564, 565, 566, 567, 568, 569, 570, 571, 572, 573, 574, 575, 576, 2552, 2553, 2554, 2555, 2556, 2557, 2558, 2559, 2560, 2561, 2562, 2563, 2564, 2565, 2566, 1313, 1314, 1315, 1316, 1317, 1318, 1319, 1320, 1321, 1322, 1323, 442, 443, 444, 445, 446, 447, 448, 449, 2097, 2098, 2099, 2100, 2101, 2102, 2103, 2104, 2105, 2106, 2107, 2108, 2109, 2110, 2111, 3401, 3402, 3403, 3404, 3405, 3406, 3407, 3408, 3409, 4491, 4492, 4493, 4494, 4495, 4496, 4497, 4498, 4499, 4500, 4501, 4502, 4503, 4504, 4505, 4506, 4507, 4508, 4509, 4510, 4511, 4512, 4513, 4514, 4515, 4177, 4178, 4179, 4180, 4181, 4182, 4183, 4184, 2841, 2842, 2843, 2844, 2845, 2846, 2847, 2848, 2849, 2850, 2851, 2852, 4277, 4278, 4279, 4280, 4281, 4282, 4283, 4284, 4285, 4286, 4287, 4288, 4289, 4290, 4291, 4292, 4293, 4294, 4295, 4296, 4297, 4298, 4299, 4300, 4301, 4302, 4303, 4304, 4305, 4306, 4307, 4308, 4309, 4310, 4311, 4312, 4313, 4314, 4315, 4316, 4317, 4318, 4319, 4320, 4321, 4322, 4323, 4324, 4325, 4326, 4327, 4328, 4329, 4330, 4331, 4332, 4333, 4334, 4335, 4336, 4337, 4338, 4339, 4340, 4341, 4342, 4343, 4344, 4345, 4346, 4347, 4348, 4349, 4350, 4351, 4352, 4353, 4354, 4355, 4356, 4357, 4358, 4359};
			int[] experiments = new int[]{1};
			for (int i: experiments) {
				String prefix = Long.toString(System.currentTimeMillis());
				ExperimentConfiguration expConfig = null;
				String method = "experiment"+i;
				try {
					Method m = TestExperimentRotterdam.class.getMethod(method, ArrayList.class);
					expConfig = (ExperimentConfiguration) m.invoke(TestExperimentRotterdam.class, networkConfigs);
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				expConfig.setExportRoute(route);
				//ExperimentConfiguration expConfig = experiment1(networkConfigs);
				//ExperimentConfiguration expConfig = experiment2(networkConfigs);
				//ExperimentConfiguration expConfig = experiment3(networkConfigs);
				//expConfig = experiment4(networkConfigs);
				//ExperimentConfiguration expConfig = experiment5(networkConfigs);

				long[][][] computationTimes = TestEnKF.performExperiment(obsTest, expConfig, prefix);
				TestExperiment.exportToMatlab(expConfig, inflow3, turnfractionExp2, prefix, computationTimes);
			}
			
			
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
	public static ExperimentConfiguration experiment1(ArrayList<String> networkConfigs) {
		ArrayList<ArrayList<ErrorConfiguration>> errorConfigs = new ArrayList<ArrayList<ErrorConfiguration>>();

	// first MC experiment: 
				Random rt = new Random(65);
				double[][] bounds = new double[6][2];
				bounds[0] = new double[]{0.001,0.015};
				bounds[1] = new double[]{0.01,0.25};
				bounds[2] = new double[]{0.001,0.10};
				bounds[3] = new double[]{1.000,1.05};
				bounds[4] = new double[]{1.000,1.05};
				bounds[5] = new double[]{1.000,1.05};

				int nrMC = 10;
				double[][] par = new double[nrMC][6]; 
				for (int i=0; i<nrMC;i++) {
					int j = 0;
					for (double[] b: bounds) {

						//par[i][j] = Math.pow(b[1]/b[0],rt.nextDouble())*b[0];
						par[i][j] = b[0] + rt.nextDouble()*(b[1]-b[0]);
						j++;	



					}
				}
				double[][] fixPar = new double[1][6];
				fixPar[0] = par[1];

				double[] errorsK = new double[]{0.001,0.003,0.01};
				double[] errorsI = new double[]{0.01,0.03,0.1,0.3};
				double[] errorsT = new double[]{0.01,0.015,0.2};
				//for (double[] p: par) {
				for (double[] p: fixPar) {
					ArrayList<ErrorConfiguration> errorList1a = new ArrayList<ErrorConfiguration>();
					errorList1a.add(new ErrorConfiguration(StateDefinition.K_CELL,p[0],p[3]));
					errorList1a.add(new ErrorConfiguration(StateDefinition.INFLOW_NODE,p[1],p[4]));
					errorList1a.add(new ErrorConfiguration(StateDefinition.TF_NODE,p[2],p[5]));
					errorConfigs.add(errorList1a);
				}

				System.out.println(Arrays.deepToString(par));
				
				ArrayList<AssimilationMethod> assimilationMethods = new ArrayList<AssimilationMethod>();
				assimilationMethods.add(AssimilationMethod.ENKF);
				assimilationMethods.add(AssimilationMethod.ENKF_SMW);
				assimilationMethods.add(AssimilationMethod.DENKF);
				assimilationMethods.add(AssimilationMethod.DENKF_SMW);
				//assimilationMethods.add(AssimilationMethod.ENKF_SCHUR);
				//assimilationMethods.add(AssimilationMethod.ENKF_SCHUR_SMW);
				assimilationMethods.add(AssimilationMethod.LENKF_MEASUREMENT);
				assimilationMethods.add(AssimilationMethod.LENKF_GRID);
				assimilationMethods.add(AssimilationMethod.LENKF_GRID_PARALLEL);
				assimilationMethods.add(AssimilationMethod.LENKF_GRID_SMW);
				assimilationMethods.add(AssimilationMethod.LENKF_GRID_SMW_PARALLEL);
				assimilationMethods.add(AssimilationMethod.DENKF_MEASUREMENT);
				assimilationMethods.add(AssimilationMethod.DENKF_GRID);
				assimilationMethods.add(AssimilationMethod.DENKF_GRID_PARALLEL);
				assimilationMethods.add(AssimilationMethod.DENKF_GRID_SMW);
				assimilationMethods.add(AssimilationMethod.DENKF_GRID_SMW_PARALLEL);
				//assimilationMethods.add(AssimilationMethod.NO_ASSIMILATION);

				ArrayList<Integer> localizationWidths = new ArrayList<Integer>();
				
				localizationWidths.add(20);
				

				ArrayList<Integer> ensembleSizes = new ArrayList<Integer>();
				//ensembleSizes.add(2);
				ensembleSizes.add(50);
				
				
				ArrayList<Integer> inflowTFFactors = new ArrayList<Integer>();

				inflowTFFactors.add(3);

				boolean forecastsNeeded = false;

				ExperimentConfiguration expConfig = new ExperimentConfiguration(errorConfigs,assimilationMethods,networkConfigs,localizationWidths, inflowTFFactors, ensembleSizes, forecastsNeeded, extendedOutput);
				//ExperimentConfiguration expConfig = new ExperimentConfiguration(runConfigurations,networkConfigs, forecastsNeeded);







				ArrayList<ErrorConfiguration> errorList = new ArrayList<ErrorConfiguration>();
				errorList.add(new ErrorConfiguration(StateDefinition.K_CELL,0.000,1.00));
				errorList.add(new ErrorConfiguration(StateDefinition.INFLOW_NODE,0.00,1.00));
				errorList.add(new ErrorConfiguration(StateDefinition.TF_NODE,0.00,1.00));
				expConfig.addRunConfiguration(new EnKFRunConfiguration(AssimilationMethod.NO_ASSIMILATION, 5, errorList, 2));
				
			/*	errorList = new ArrayList<ErrorConfiguration>();
				errorList.add(new ErrorConfiguration(StateDefinition.K_CELL,0.000,1.00));
				errorList.add(new ErrorConfiguration(StateDefinition.INFLOW_NODE,0.00,1.00));
				errorList.add(new ErrorConfiguration(StateDefinition.TF_NODE,0.00,1.00));
				expConfig.addRunConfiguration(new EnKFRunConfiguration(AssimilationMethod.ENKF, 5, errorList, 2));
				*/return expConfig;
	}
	public static ExperimentConfiguration experiment2(ArrayList<String> networkConfigs) {
		ArrayList<ArrayList<ErrorConfiguration>> errorConfigs = new ArrayList<ArrayList<ErrorConfiguration>>();

	// first MC experiment: 
				Random rt = new Random(65);
				double[][] bounds = new double[6][2];
				bounds[0] = new double[]{0.001,0.015};
				bounds[1] = new double[]{0.01,0.25};
				bounds[2] = new double[]{0.001,0.10};
				bounds[3] = new double[]{1.000,1.05};
				bounds[4] = new double[]{1.000,1.05};
				bounds[5] = new double[]{1.000,1.05};

				int nrMC = 10;
				double[][] par = new double[nrMC][6]; 
				for (int i=0; i<nrMC;i++) {
					int j = 0;
					for (double[] b: bounds) {

						//par[i][j] = Math.pow(b[1]/b[0],rt.nextDouble())*b[0];
						par[i][j] = b[0] + rt.nextDouble()*(b[1]-b[0]);
						j++;	



					}
				}
				double[][] fixPar = new double[1][6];
				fixPar[0] = par[1];

				double[] errorsK = new double[]{0.001,0.003,0.01};
				double[] errorsI = new double[]{0.01,0.03,0.1,0.3};
				double[] errorsT = new double[]{0.01,0.015,0.2};
				//for (double[] p: par) {
				for (double[] p: fixPar) {
					ArrayList<ErrorConfiguration> errorList1a = new ArrayList<ErrorConfiguration>();
					errorList1a.add(new ErrorConfiguration(StateDefinition.K_CELL,p[0],p[3]));
					errorList1a.add(new ErrorConfiguration(StateDefinition.INFLOW_NODE,p[1],p[4]));
					errorList1a.add(new ErrorConfiguration(StateDefinition.TF_NODE,p[2],p[5]));
					errorConfigs.add(errorList1a);
				}

				System.out.println(Arrays.deepToString(par));
				
				ArrayList<AssimilationMethod> assimilationMethods = new ArrayList<AssimilationMethod>();
				//assimilationMethods.add(AssimilationMethod.ENKF);
				//assimilationMethods.add(AssimilationMethod.ENKF_SMW);
				//assimilationMethods.add(AssimilationMethod.DENKF);
				//assimilationMethods.add(AssimilationMethod.DENKF_SMW);

				//assimilationMethods.add(AssimilationMethod.LENKF_MEASUREMENT);
				assimilationMethods.add(AssimilationMethod.LENKF_GRID);
				
				//assimilationMethods.add(AssimilationMethod.LENKF_GRID_SMW);
				//assimilationMethods.add(AssimilationMethod.DENKF_MEASUREMENT);
				//assimilationMethods.add(AssimilationMethod.DENKF_GRID);
				//assimilationMethods.add(AssimilationMethod.DENKF_GRID_SMW);

				ArrayList<Integer> localizationWidths = new ArrayList<Integer>();
				
				localizationWidths.add(20);
				

				ArrayList<Integer> ensembleSizes = new ArrayList<Integer>();
				ensembleSizes.add(20);
				
				ArrayList<Integer> inflowTFFactors = new ArrayList<Integer>();

				inflowTFFactors.add(3);

				boolean forecastsNeeded = false;

				ExperimentConfiguration expConfig = new ExperimentConfiguration(errorConfigs,assimilationMethods,networkConfigs,localizationWidths, inflowTFFactors, ensembleSizes, forecastsNeeded, extendedOutput);
				//ExperimentConfiguration expConfig = new ExperimentConfiguration(runConfigurations,networkConfigs, forecastsNeeded);








				ArrayList<ErrorConfiguration> errorList = new ArrayList<ErrorConfiguration>();
				errorList.add(new ErrorConfiguration(StateDefinition.K_CELL,0.000,1.00));
				errorList.add(new ErrorConfiguration(StateDefinition.INFLOW_NODE,0.00,1.00));
				errorList.add(new ErrorConfiguration(StateDefinition.TF_NODE,0.00,1.00));
				expConfig.addRunConfiguration(new EnKFRunConfiguration(AssimilationMethod.ENKF, 5, errorList, 2));
				return expConfig;
	}
	public static void exportToMatlab(ExperimentConfiguration expConfig, double[][] inflow3, double[][] turnfractionExp2, String prefix, long[][][] computationTimes) {
		PrintWriter out;
		try {
			out = new PrintWriter("configEnKF.m");
			out.println("clear all;");
			out.println("set(0,'DefaultFigureVisible','off');");
			int j=1;
			for (String nw: expConfig.getNetworkConfigurations()) {
				out.println("%{");
				out.println("Network{"+j+"}='"+nw+"';");
				out.println("%}");
				out.println("inflow{"+j+"} = "+Arrays.toString(inflow3[j-1])+";");
				out.println("tf{"+j+"} = "+Arrays.toString(turnfractionExp2[j-1])+";");

				ArrayList<EnKFRunConfiguration> runs = expConfig.getRunConfigurations();
				int i=1;
				for (EnKFRunConfiguration run: runs) {
					out.println("RUN{"+j+","+i+"}="+run.toString());
					out.println("ENKF"+prefix+"_"+(runs.size()*(j-1)+i-1)+";");
					if (expConfig.forecastsNeeded()) {
						out.println("FC"+prefix+"_"+(runs.size()*(j-1)+i-1)+";");
						out.println("[R{"+j+","+i+",1}]=testForecast(f_time, f_x, time, truth, nrCells, len, 1);");
						out.println("[R{"+j+","+i+",2}]=testForecast(f_time, f_v, time, truthV, nrCells, len, 1);");
						out.println("[R{"+j+","+i+",3}]=testForecast(f_time, f_tr, time, truthTR, nrCells, len, 0);");
					}
					out.println("O{"+j+","+i+",1} = calcRMSE(truth(2:end,1:nrCells),gemXa(:,1:nrCells));");
					out.println("O{"+j+","+i+",2} = calcRMSE(truthV(2:end,1:nrCells),gemV(:,1:nrCells));");
					out.println("O{"+j+","+i+",3} = sum(sum((ones(nrTimes,1)*len).*(abs(gemTR(:,:)-truthTR(2:end,1:nrCells))))*60);");
					out.println("ComputationTime{"+j+","+i+"} = "+Arrays.toString(computationTimes[j-1][i-1]) +";");
					out.println("clearvars -except Network inflow tf RUN ENKF O ComputationTime R;");
					out.println("close all; clear functions;");
					i++;

				}
				//out.println("close all");
				j++;
			}
			out.println("set(0,'DefaultFigureVisible','on');");
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PrintWriter out1;
		try {
			out1 = new PrintWriter("sumConfigEnKF.m");
			out1.println("clear all;");
			out1.println("set(0,'DefaultFigureVisible','off');");
			int j=1;
			for (String nw: expConfig.getNetworkConfigurations()) {
				out1.println("%{");
				out1.println("Network{"+j+"}='"+nw+"';");
				out1.println("%}");
				out1.println("inflow{"+j+"} = "+Arrays.toString(inflow3[j-1])+";");
				out1.println("tf{"+j+"} = "+Arrays.toString(turnfractionExp2[j-1])+";");

				ArrayList<EnKFRunConfiguration> runs = expConfig.getRunConfigurations();
				int i=1;
				for (EnKFRunConfiguration run: runs) {
					out1.println("RUN{"+j+","+i+"}="+run.toString());
					out1.println("SumENKF"+prefix+"_"+(runs.size()*(j-1)+i-1)+";");
					out1.println("O{"+j+","+i+"} = result;");
					/*out1.println("O{"+j+","+i+",1} = RMSEK;");
			out1.println("O{"+j+","+i+",2} = RMSEV;");
			out1.println("O{"+j+","+i+",3} = TRE;");

			if (expConfig.forecastsNeeded()) {
			out1.println("[R{"+j+","+i+",1}]=fk;");
			out1.println("[R{"+j+","+i+",2}]=fv;");
			out1.println("[R{"+j+","+i+",3}]=ftr;");
			}*/

					out1.println("ComputationTime{"+j+","+i+"} = "+Arrays.toString(computationTimes[j-1][i-1]) +";");
					out1.println("clearvars -except Network inflow tf RUN ENKF O ComputationTime R;");
					out1.println("close all; clear functions;");
					i++;

				}
				//out.println("close all");
				j++;
			}
			out1.println("set(0,'DefaultFigureVisible','on');");
			out1.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("experiment is finalized");
	}
}
