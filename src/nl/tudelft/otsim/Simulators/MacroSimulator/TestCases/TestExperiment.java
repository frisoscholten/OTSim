package nl.tudelft.otsim.Simulators.MacroSimulator.TestCases;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import Jama.Matrix;

public class TestExperiment {
	static boolean extendedOutput = true;
	public static void main(String[] args) {

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
				+ "Detector:	3	(2000.000,-250.000,0.000)\n";



		String[] networksplit = network.split("\n");
		String networkAfterSplit1 = networksplit[0]+"\n"+networksplit[1]+"\n";
		String networkAfterSplit2 = networksplit[0]+"\n"+networksplit[1]+"\n";
		for (int i = 2; i<networksplit.length; i++) {
			networkAfterSplit1 += networksplit[i].concat("	fd	0.025	0.125	22.222	SMULDERS\n");
			networkAfterSplit2 += networksplit[i].concat("	fd	0.025	0.125	22.222	SMULDERS\n");
		}
		int seed = 619;
		Random r = new Random(seed);
		int nrExperiments =25;






		double[] inflowTruth = new double[]{
				(1800.0),
				1600.0

		};
		double [] stdInflow = new double[] {
				200,
				150
				//0,0
		};
		double[][] inflow3 = new double[nrExperiments][inflowTruth.length];
		for (int n =0; n<nrExperiments; n++) {
			for (int i = 0; i<inflowTruth.length; i++) {
				inflow3[n][i] = Math.max(0, inflowTruth[i] + r.nextGaussian()*stdInflow[i]);
			}
		}

		/*
		double[] inflow2 = new double[]{
				(1900.0),
				1600.0
		};*/
		//String pattern1 = "[0.000/"+inflowTruth+":1500.000/"+inflowTruth+":2100/"+inflowTruth/2+":3600/"+inflowTruth/2+"]";
		String[] pattern1 = new String[] {
				"[0.000/"+inflowTruth[0]/1.5+":1800/"+inflowTruth[0]+":2400.000/"+inflowTruth[0]+":3900/"+inflowTruth[0]/2+":4200/"+inflowTruth[0]/2+"]",
				"[0.000/"+inflowTruth[1]/1.5+":2100/"+inflowTruth[1]+":3000.000/"+inflowTruth[1]+":4800/"+inflowTruth[1]/2+":5100/"+inflowTruth[1]/2+"]"
		};
		/*String[] pattern1 = new String[] {
				"[0.000/"+inflowTruth[0]/1.5+":1800/"+inflowTruth[0]/1.5+":3600/"+inflowTruth[0]+":4200.000/"+inflowTruth[0]+":5700/"+inflowTruth[0]/2+":6000/"+inflowTruth[0]/2+"]",
				"[0.000/"+inflowTruth[1]/1.5+":1800/"+inflowTruth[1]/1.5+":3900/"+inflowTruth[1]+":4800.000/"+inflowTruth[1]+":6400/"+inflowTruth[1]/2+":6900/"+inflowTruth[1]/2+"]"
		};*/

		//String pattern2 = "[0.000/"+inflow2+":1500.000/"+inflow2+":2100/"+inflow2/2+":3600/"+inflow2/2+"]";

		//String pattern2 = "[0.000/"+inflow2+":1200.000/"+inflow2+":2300/"+inflow2/2.1+":3600/"+inflow2/2+"]";
		/*String[] pattern2 = new String[] {
				"[0.000/"+inflow2[0]/1.5+":1800/"+inflow2[0]+":2400.000/"+inflow2[0]+":3900/"+inflow2[0]/2+":4200/"+inflow2[0]/2+"]",
				"[0.000/"+inflow2[1]/1.5+":1800/"+inflow2[1]+":2400.000/"+inflow2[1]+":3900/"+inflow2[1]/2+":4200/"+inflow2[1]/2+"]"
		};*/
		Random rTF = new Random(12345);
		double[] turnfractionTruth = new double[]{
				0.6
		};
		double[] stdTurnFraction = new double[]{
				0.15
				//0
		};
		double[][] turnfractionExp2 = new double[nrExperiments][turnfractionTruth.length];
		for (int n =0; n<nrExperiments; n++) {
			for (int i = 0; i<turnfractionTruth.length; i++) {
				turnfractionExp2[n][i] = Math.min(1,Math.max(0, turnfractionTruth[i] + rTF.nextGaussian()*stdTurnFraction[i]));
			}
		}
		/*double[] turnfractionExp = new double[]{
				0.8
		};*/
		//String pattern2 = "[0.000/"+inflow2/1.5+"]";

		String otsimConfigurationTruth = networkAfterSplit1
				+ "TripPatternPath	numberOfTrips:	"+pattern1[0]+"	NodePattern:	[origin ID=1 (0.00m, 0.00m, 0.00m), destination ID=2 (2500.00m, 0.00m, 0.00m)]\n"
				+ "Path:	"+turnfractionTruth[0]+"	nodes:	0	1	2	3\n"
				+ "Path:	"+(1-turnfractionTruth[0])+"	nodes:	0	1	5	6	7	2	3\n"
				+ "TripPatternPath	numberOfTrips:	"+pattern1[1]+"	NodePattern:	[origin ID=2 (0.00m, -500.00m, 0.00m), destination ID=2 (2500.00m, 0.00m, 0.00m)]\n"
				+ "Path:	1.00000	nodes:	4	5	6	7	2	3\n"
				+ detectors;

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
			String[] pattern3 = new String[] {
					"[0.000/"+inflow3[i][0]/1.5+":1800/"+inflow3[i][0]+":2400.000/"+inflow3[i][0]+":3900/"+inflow3[i][0]/2+":4200/"+inflow3[i][0]/2+"]",
					"[0.000/"+inflow3[i][1]/1.5+":2100/"+inflow3[i][1]+":3000.000/"+inflow3[i][1]+":4800/"+inflow3[i][1]/2+":5100/"+inflow3[i][1]/2+"]"
			};
			/*String[] pattern3 = new String[] {
					"[0.000/"+inflow3[i][0]/1.5+":1800/"+inflow3[i][0]/1.5+":3600/"+inflow3[i][0]+":4200.000/"+inflow3[i][0]+":5700/"+inflow3[i][0]/2+":6000/"+inflow3[i][0]/2+"]",
					"[0.000/"+inflow3[i][1]/1.5+":1800/"+inflow3[i][1]/1.5+":3900/"+inflow3[i][1]+":4800.000/"+inflow3[i][1]+":6400/"+inflow3[i][1]/2+":6900/"+inflow3[i][1]/2+"]"
			};*/
			String networkConfig = networkAfterSplit2
					+ "TripPatternPath	numberOfTrips:	"+pattern3[0]+"	NodePattern:	[origin ID=1 (0.00m, 0.00m, 0.00m), destination ID=2 (2500.00m, 0.00m, 0.00m)]\n"
					+ "Path:	"+turnfractionExp2[i][0]+"	nodes:	0	1	2	3\n"
					+ "Path:	"+(1-turnfractionExp2[i][0])+"	nodes:	0	1	5	6	7	2	3\n"
					+ "TripPatternPath	numberOfTrips:	"+pattern3[1]+"	NodePattern:	[origin ID=2 (0.00m, -500.00m, 0.00m), destination ID=2 (2500.00m, 0.00m, 0.00m)]\n"
					+ "Path:	1.00000	nodes:	4	5	6	7	2	3\n"
					+ detectors;
			networkConfigs.add(networkConfig);
		}
		int nrSteps = (int) (7200.0/2.0 +1);

		//Matrix[] obsTest = TestEKF2.generateTruthData(otsimConfigurationTruth, nrSteps, 2.0);
		Matrix[] obsTest = TestEnKF.generateTruthValues(otsimConfigurationTruth, nrSteps, 2.0);

		ArrayList<ArrayList<ErrorConfiguration>> errorConfigs = new ArrayList<ArrayList<ErrorConfiguration>>();
		/*
		// first experiment: 
		double[] errorsK = new double[]{0.001,0.003,0.01};
		double[] errorsI = new double[]{0.03,0.1,0.3};
		double[] errorsT = new double[]{0.01,0.2,0.3};
		double[] errorsK = new double[]{0.003};
		double[] errorsI = new double[]{0.1};
		double[] errorsT = new double[]{0.9,1.5,2.5};

		for (double k: errorsK) {
			for (double i: errorsI) { 
				for (double t: errorsT) { 
				ArrayList<ErrorConfiguration> errorList1a = new ArrayList<ErrorConfiguration>();
				errorList1a.add(new ErrorConfiguration(StateDefinition.K_CELL,k,1.00));
				errorList1a.add(new ErrorConfiguration(StateDefinition.INFLOW_NODE,i,1.00));
				errorList1a.add(new ErrorConfiguration(StateDefinition.TF_NODE,t,1.00));
				errorConfigs.add(errorList1a);
				}
			}
		}

		// first MC experiment: 
		Random rt = new Random(65);
		double[][] bounds = new double[6][2];
		bounds[0] = new double[]{0.001,0.01};
		bounds[1] = new double[]{0.01,0.3};
		bounds[2] = new double[]{0.05,0.45};
		bounds[3] = new double[]{1.000,1.00};
		bounds[4] = new double[]{1.000,1.00};
		bounds[5] = new double[]{1.000,1.00};

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
		double[] errorsK = new double[]{0.001,0.003,0.01};
		double[] errorsI = new double[]{0.01,0.03,0.1,0.3};
		double[] errorsT = new double[]{0.01,0.015,0.2};
		for (double[] p: par) {
			ArrayList<ErrorConfiguration> errorList1a = new ArrayList<ErrorConfiguration>();
			errorList1a.add(new ErrorConfiguration(StateDefinition.K_CELL,p[0],p[3]));
			errorList1a.add(new ErrorConfiguration(StateDefinition.INFLOW_NODE,p[1],p[4]));
			errorList1a.add(new ErrorConfiguration(StateDefinition.TF_NODE,p[2],p[5]));
			errorConfigs.add(errorList1a);
			errorList1a.add(new ErrorConfiguration(StateDefinition.K_CELL,0,1));
			errorList1a.add(new ErrorConfiguration(StateDefinition.INFLOW_NODE,0,1));
			errorList1a.add(new ErrorConfiguration(StateDefinition.TF_NODE,0,1));
			errorConfigs.add(errorList1a);
		}




		// second experiment
		// select three best MC-values:
		Random rt = new Random(65);
		double[][] bounds = new double[6][2];
		bounds[0] = new double[]{0.001,0.01};
		bounds[1] = new double[]{0.01,0.3};
		bounds[2] = new double[]{0.05,0.45};
		bounds[3] = new double[]{1.000,1.00};
		bounds[4] = new double[]{1.000,1.00};
		bounds[5] = new double[]{1.000,1.00};

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
		//double[][][] pars = new double[6][nrMC][6];
		//pars[0] = 
		double[][] par2 = new double[][]{par[4], par[9]};
		double[] inflationbounds = new double[]{1.0, 1.05};
		double[][] par3 = new double[10][6];
		double[][] bounds2 = new double[][]{inflationbounds, inflationbounds, inflationbounds};
		par3[0] = par2[0].clone();
		par3[5] = par2[1].clone();
		for (int i=1; i<5;i++) {
			int j = 0;
			par3[i] = par2[0].clone();
			par3[i+5] = par2[1].clone();
			for (double[] b: bounds2) {

				//par[i][j] = Math.pow(b[1]/b[0],rt.nextDouble())*b[0];
				par3[i][3+j] = b[0] + rt.nextDouble()*(b[1]-b[0]);
				par3[i+5][3+j] = par3[i][3+j];
				j++;	



			}
		}






		for (double[] p: par3) {


			ArrayList<ErrorConfiguration> errorList1a = new ArrayList<ErrorConfiguration>();

			errorList1a.add(new ErrorConfiguration(StateDefinition.K_CELL,p[0],p[3]));
			errorList1a.add(new ErrorConfiguration(StateDefinition.INFLOW_NODE,p[1],p[4]));
			errorList1a.add(new ErrorConfiguration(StateDefinition.TF_NODE,p[2],p[5]));
			errorConfigs.add(errorList1a);
		}

		// third experiment
		double[][] par4 = new double[][]{par3[2],par3[1],par3[9],par3[5],par3[0]};
		for (double[] p: par4) {


			ArrayList<ErrorConfiguration> errorList1a = new ArrayList<ErrorConfiguration>();

			errorList1a.add(new ErrorConfiguration(StateDefinition.K_CELL,p[0],p[3]));
			errorList1a.add(new ErrorConfiguration(StateDefinition.INFLOW_NODE,p[1],p[4]));
			errorList1a.add(new ErrorConfiguration(StateDefinition.TF_NODE,p[2],p[5]));
			errorConfigs.add(errorList1a);
		}
		//int[] ensemblesizes = new int[]{40,28,20,14,10,7,5};
		int[] ensemblesizes = new int[]{20};
		ArrayList<EnKFRunConfiguration> runConfigurations = new ArrayList<EnKFRunConfiguration>();
		for (int e: ensemblesizes) {
			runConfigurations.add(new EnKFRunConfiguration(AssimilationMethod.ENKF, 20, errorConfigs.get(0), e,60));
			runConfigurations.add(new EnKFRunConfiguration(AssimilationMethod.ENKF_SMW, 20, errorConfigs.get(0), e,60));

			runConfigurations.add(new EnKFRunConfiguration(AssimilationMethod.DENKF, 20, errorConfigs.get(3), e,60));
			runConfigurations.add(new EnKFRunConfiguration(AssimilationMethod.DENKF_SMW, 20, errorConfigs.get(3), e,60));

			runConfigurations.add(new EnKFRunConfiguration(AssimilationMethod.LENKF_MEASUREMENT, 20, errorConfigs.get(2), e,60));
			runConfigurations.add(new EnKFRunConfiguration(AssimilationMethod.LENKF_GRID, 20, errorConfigs.get(1), e,60));
			runConfigurations.add(new EnKFRunConfiguration(AssimilationMethod.LENKF_GRID_SMW, 20, errorConfigs.get(1), e,60));

			runConfigurations.add(new EnKFRunConfiguration(AssimilationMethod.DENKF_MEASUREMENT, 20, errorConfigs.get(4), e,60));
			runConfigurations.add(new EnKFRunConfiguration(AssimilationMethod.DENKF_GRID, 20, errorConfigs.get(3), e,60));
			runConfigurations.add(new EnKFRunConfiguration(AssimilationMethod.DENKF_GRID_SMW, 20, errorConfigs.get(3), e,60));

		}
		for (int e: ensemblesizes) {
			runConfigurations.add(new EnKFRunConfiguration(AssimilationMethod.DENKF_MEASUREMENT, 20, errorConfigs.get(3), e));
			runConfigurations.add(new EnKFRunConfiguration(AssimilationMethod.DENKF_MEASUREMENT, 20, errorConfigs.get(2), e));
		}
		int[] ensemblesizes2 = new int[]{20};
		int[] localizationradii = new int[]{20,14,10,7,5};
		int[] localizationFactor = new int[]{5,4,3,2,1};
		ArrayList<EnKFRunConfiguration> runConfigurations = new ArrayList<EnKFRunConfiguration>();
		for (int e: ensemblesizes2) {
			runConfigurations.add(new EnKFRunConfiguration(AssimilationMethod.DENKF, 20, errorConfigs.get(0), e));
			for (int l: localizationradii) {
				for (int f: localizationFactor) {
					//runConfigurations.add(new EnKFRunConfiguration(AssimilationMethod.ENKF, 20, errorConfigs.get(2), e));

					//runConfigurations.add(new EnKFRunConfiguration(AssimilationMethod.LENKF_MEASUREMENT, 20, errorConfigs.get(1), e));
					//runConfigurations.add(new EnKFRunConfiguration(AssimilationMethod.LENKF_GRID, 20, errorConfigs.get(3), e));
					runConfigurations.add(new EnKFRunConfiguration(AssimilationMethod.DENKF_MEASUREMENT, l, errorConfigs.get(2), e, l*f));
					runConfigurations.add(new EnKFRunConfiguration(AssimilationMethod.DENKF_GRID, l, errorConfigs.get(2), e, l*f));
				}
			}
		}




		// second experiment
		double[] errorsK = new double[]{0.001,0.005};
		double[] errorsI = new double[]{0.1,0,15,0.2,0.25};
		//double[] errorsT = new double[]{1.0,2.0};
		double[] errorsIn = new double[]{1.00,1.01};

		for (double k: errorsK) {
			for (double i: errorsI) {
				//for (double t: errorsT) {
				for (double in: errorsIn) {
				ArrayList<ErrorConfiguration> errorList1a = new ArrayList<ErrorConfiguration>();
				errorList1a.add(new ErrorConfiguration(StateDefinition.K_CELL,k,in));
				errorList1a.add(new ErrorConfiguration(StateDefinition.INFLOW_NODE,i,in));
				errorList1a.add(new ErrorConfiguration(StateDefinition.TF_NODE,i/2.0,in));
				errorConfigs.add(errorList1a);
				}
				//}
			}
		}

		// third experiment
				double[] errorsK = new double[]{0.003};
				double[] errorsI = new double[]{0.1,0.15,0.2};
				double[] errorsT = new double[]{1.0,1.5,2.0};
				double[] errorsIn1 = new double[]{1.00,1.01};
				//double[] errorsIn2 = new double[]{1.00,1.01};

				for (double k: errorsK) {
					for (double i: errorsI) {
						for (double t: errorsT) {
						for (double in: errorsIn1) {
						ArrayList<ErrorConfiguration> errorList1a = new ArrayList<ErrorConfiguration>();
						errorList1a.add(new ErrorConfiguration(StateDefinition.K_CELL,k,in));
						errorList1a.add(new ErrorConfiguration(StateDefinition.INFLOW_NODE,i,in+0.04));
						errorList1a.add(new ErrorConfiguration(StateDefinition.TF_NODE,i/t,in));
						errorConfigs.add(errorList1a);
						}
						}
					}
				}

		// fourth experiment
		double[] errorsK = new double[]{0.005};
		double[] errorsI = new double[]{0.05,0.1,0.15,0.2};
		double[] errorsT = new double[]{0.05,0.075,0.1,0.125,0.15};
		double[] errorsIn1 = new double[]{1.00};
		//double[] errorsIn2 = new double[]{1.00,1.01};

		for (double k: errorsK) {
			for (double i: errorsI) {
				for (double t: errorsT) {
				for (double in: errorsIn1) {
				ArrayList<ErrorConfiguration> errorList1a = new ArrayList<ErrorConfiguration>();
				errorList1a.add(new ErrorConfiguration(StateDefinition.K_CELL,k,1.01));
				errorList1a.add(new ErrorConfiguration(StateDefinition.INFLOW_NODE,i,1.05));
				errorList1a.add(new ErrorConfiguration(StateDefinition.TF_NODE,t,1.01));
				errorConfigs.add(errorList1a);
				}
				}
			}
		}
		// fifth experiment
				double[] errorsK = new double[]{0.005};
				double[] errorsI = new double[]{0.1,0.15};
				double[] errorsT = new double[]{0.075,0.1,0.125};
				double[] errorsIn1 = new double[]{1.00};
				//double[] errorsIn2 = new double[]{1.00,1.01};

				for (double k: errorsK) {
					for (double i: errorsI) {
						for (double t: errorsT) {
						for (double in: errorsIn1) {
						ArrayList<ErrorConfiguration> errorList1a = new ArrayList<ErrorConfiguration>();
						errorList1a.add(new ErrorConfiguration(StateDefinition.K_CELL,k,1.01));
						errorList1a.add(new ErrorConfiguration(StateDefinition.INFLOW_NODE,i,1.05));
						errorList1a.add(new ErrorConfiguration(StateDefinition.TF_NODE,t,1.01));
						errorConfigs.add(errorList1a);
						}
						}
					}
				}
		//errorConfigs.add(errorList);
		ArrayList<ErrorConfiguration> errorList1 = new ArrayList<ErrorConfiguration>();
			errorList1.add(new ErrorConfiguration(StateDefinition.K_CELL,0.005,1.01));
			errorList1.add(new ErrorConfiguration(StateDefinition.INFLOW_NODE,0.15,1.05));
			errorList1.add(new ErrorConfiguration(StateDefinition.TF_NODE,0.05,1.01));
			errorConfigs.add(errorList1);

		ArrayList<ErrorConfiguration> errorList1a = new ArrayList<ErrorConfiguration>();
		errorList1a.add(new ErrorConfiguration(StateDefinition.K_CELL,0.005,1.00));
		errorList1a.add(new ErrorConfiguration(StateDefinition.INFLOW_NODE,0.1,1.00));
		errorList1a.add(new ErrorConfiguration(StateDefinition.TF_NODE,0.05,1.00));
		errorConfigs.add(errorList1a);
		ArrayList<ErrorConfiguration> errorList2 = new ArrayList<ErrorConfiguration>();
			errorList2.add(new ErrorConfiguration(StateDefinition.K_CELL,0.001,1.01));
			errorList2.add(new ErrorConfiguration(StateDefinition.INFLOW_NODE,0.02,1.05));
			errorList2.add(new ErrorConfiguration(StateDefinition.TF_NODE,0.01,1.01));
			errorConfigs.add(errorList2);
			ArrayList<ErrorConfiguration> errorList2a = new ArrayList<ErrorConfiguration>();
			errorList2a.add(new ErrorConfiguration(StateDefinition.K_CELL,0.001,1.00));
			errorList2a.add(new ErrorConfiguration(StateDefinition.INFLOW_NODE,0.02,1.00));
			errorList2a.add(new ErrorConfiguration(StateDefinition.TF_NODE,0.01,1.00));
			errorConfigs.add(errorList2a);
			ArrayList<ErrorConfiguration> errorList3 = new ArrayList<ErrorConfiguration>();
			errorList3.add(new ErrorConfiguration(StateDefinition.K_CELL,0.01,1.02));
			errorList3.add(new ErrorConfiguration(StateDefinition.INFLOW_NODE,0.15,1.05));
			errorList3.add(new ErrorConfiguration(StateDefinition.TF_NODE,0.1,1.01));
			errorConfigs.add(errorList3);
			ArrayList<ErrorConfiguration> errorList3a = new ArrayList<ErrorConfiguration>();
			errorList3a.add(new ErrorConfiguration(StateDefinition.K_CELL,0.01,1.00));
			errorList3a.add(new ErrorConfiguration(StateDefinition.INFLOW_NODE,0.15,1.00));
			errorList3a.add(new ErrorConfiguration(StateDefinition.TF_NODE,0.1,1.00));
			errorConfigs.add(errorList3a);


		ArrayList<AssimilationMethod> assimilationMethods = new ArrayList<AssimilationMethod>();
		assimilationMethods.add(AssimilationMethod.ENKF);
		//assimilationMethods.add(AssimilationMethod.ENKF_SMW);
		assimilationMethods.add(AssimilationMethod.DENKF);
		//assimilationMethods.add(AssimilationMethod.DENKF_SMW);


		assimilationMethods.add(AssimilationMethod.LENKF_MEASUREMENT);
		assimilationMethods.add(AssimilationMethod.LENKF_GRID);
		assimilationMethods.add(AssimilationMethod.DENKF_MEASUREMENT);
		assimilationMethods.add(AssimilationMethod.DENKF_GRID);

		ArrayList<Integer> localizationWidths = new ArrayList<Integer>();
		//localizationWidths.add(1);
		//localizationWidths.add(5);
		//localizationWidths.add(10);
		//localizationWidths.add(15);
		localizationWidths.add(20);
		//localizationWidths.add(25);
		//localizationWidths.add(35);
		//localizationWidths.add(45);
		//localizationWidths.add(60);
		//localizationWidths.add(9999);

		ArrayList<Integer> inflowTFFactors = new ArrayList<Integer>();

		inflowTFFactors.add(3);



		ArrayList<Integer> ensembleSizes = new ArrayList<Integer>();
		ensembleSizes.add(20);
		//ensembleSizes.add(14);
		//ensembleSizes.add(10);
		//ensembleSizes.add(7);
		//ensembleSizes.add(5);
		//ensembleSizes.add(40);

		boolean forecastsNeeded = false;

		//ExperimentConfiguration expConfig = new ExperimentConfiguration(errorConfigs,assimilationMethods,networkConfigs,localizationWidths, inflowTFFactors, ensembleSizes, forecastsNeeded);
		ExperimentConfiguration expConfig = new ExperimentConfiguration(runConfigurations,networkConfigs, forecastsNeeded);








		ArrayList<ErrorConfiguration> errorList = new ArrayList<ErrorConfiguration>();
		errorList.add(new ErrorConfiguration(StateDefinition.K_CELL,0.000,1.00));
		errorList.add(new ErrorConfiguration(StateDefinition.INFLOW_NODE,0.00,1.00));
		errorList.add(new ErrorConfiguration(StateDefinition.TF_NODE,0.00,1.00));
		expConfig.addRunConfiguration(new EnKFRunConfiguration(AssimilationMethod.ENKF, 5, errorList, 2));
		 */
		int[] route = new int[obsTest[1].getColumnDimension()];
		for (int i=0; i< obsTest[1].getColumnDimension(); i++)
			route[i] = i;
		
		for (int i = 6; i<=6; i++) {
			String prefix = Long.toString(System.currentTimeMillis());
			ExperimentConfiguration expConfig = null;
			String method = "experiment"+i;
			try {
				Method m = TestExperiment.class.getMethod(method, ArrayList.class);
				expConfig = (ExperimentConfiguration) m.invoke(TestExperiment.class, networkConfigs);
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
			exportToMatlab(expConfig, inflow3, turnfractionExp2, prefix, computationTimes);
		}

	}
	public static ExperimentConfiguration experiment1(ArrayList<String> networkConfigs) {

		ArrayList<ArrayList<ErrorConfiguration>> errorConfigs = new ArrayList<ArrayList<ErrorConfiguration>>();
		// first MC experiment: 
		Random rt = new Random(65);
		double[][] bounds = new double[6][2];
		bounds[0] = new double[]{0.001,0.01};
		bounds[1] = new double[]{0.01,0.3};
		bounds[2] = new double[]{0.05,0.45};
		bounds[3] = new double[]{1.000,1.00};
		bounds[4] = new double[]{1.000,1.00};
		bounds[5] = new double[]{1.000,1.00};

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

		for (double[] p: par) {
			ArrayList<ErrorConfiguration> errorList1a = new ArrayList<ErrorConfiguration>();
			errorList1a.add(new ErrorConfiguration(StateDefinition.K_CELL,p[0],p[3]));
			errorList1a.add(new ErrorConfiguration(StateDefinition.INFLOW_NODE,p[1],p[4]));
			errorList1a.add(new ErrorConfiguration(StateDefinition.TF_NODE,p[2],p[5]));
			errorConfigs.add(errorList1a);

		}


		ArrayList<AssimilationMethod> assimilationMethods = new ArrayList<AssimilationMethod>();
		assimilationMethods.add(AssimilationMethod.ENKF);
		//assimilationMethods.add(AssimilationMethod.ENKF_SMW);
		assimilationMethods.add(AssimilationMethod.DENKF);
		//assimilationMethods.add(AssimilationMethod.DENKF_SMW);


		assimilationMethods.add(AssimilationMethod.LENKF_MEASUREMENT);
		assimilationMethods.add(AssimilationMethod.LENKF_GRID);
		assimilationMethods.add(AssimilationMethod.DENKF_MEASUREMENT);
		assimilationMethods.add(AssimilationMethod.DENKF_GRID);

		ArrayList<Integer> localizationWidths = new ArrayList<Integer>();
		//localizationWidths.add(1);
		//localizationWidths.add(5);
		//localizationWidths.add(10);
		//localizationWidths.add(15);
		localizationWidths.add(20);
		//localizationWidths.add(25);
		//localizationWidths.add(35);
		//localizationWidths.add(45);
		//localizationWidths.add(60);
		//localizationWidths.add(9999);

		ArrayList<Integer> inflowTFFactors = new ArrayList<Integer>();

		inflowTFFactors.add(3);



		ArrayList<Integer> ensembleSizes = new ArrayList<Integer>();
		ensembleSizes.add(20);
		//ensembleSizes.add(14);
		//ensembleSizes.add(10);
		//ensembleSizes.add(7);
		//ensembleSizes.add(5);
		//ensembleSizes.add(40);

		boolean forecastsNeeded = true;

		ExperimentConfiguration expConfig = new ExperimentConfiguration(errorConfigs,assimilationMethods,networkConfigs,localizationWidths, inflowTFFactors, ensembleSizes, forecastsNeeded, extendedOutput);
		//ExperimentConfiguration expConfig = new ExperimentConfiguration(runConfigurations,networkConfigs, forecastsNeeded);

		ArrayList<ErrorConfiguration> errorList = new ArrayList<ErrorConfiguration>();
		errorList.add(new ErrorConfiguration(StateDefinition.K_CELL,0.000,1.00));
		errorList.add(new ErrorConfiguration(StateDefinition.INFLOW_NODE,0.00,1.00));
		errorList.add(new ErrorConfiguration(StateDefinition.TF_NODE,0.00,1.00));
		expConfig.addRunConfiguration(new EnKFRunConfiguration(AssimilationMethod.ENKF, 5, errorList, 2));

		return expConfig;
	}
	public static ExperimentConfiguration experiment2(ArrayList<String> networkConfigs) {

		ArrayList<ArrayList<ErrorConfiguration>> errorConfigs = new ArrayList<ArrayList<ErrorConfiguration>>();
		// first MC experiment: 
		Random rt = new Random(65);
		double[][] bounds = new double[6][2];
		bounds[0] = new double[]{0.001,0.01};
		bounds[1] = new double[]{0.01,0.3};
		bounds[2] = new double[]{0.05,0.45};
		bounds[3] = new double[]{1.000,1.00};
		bounds[4] = new double[]{1.000,1.00};
		bounds[5] = new double[]{1.000,1.00};

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

		double[][] par2 = new double[][]{par[4], par[9]};
		double[] inflationbounds = new double[]{1.0, 1.05};
		double[][] par3 = new double[10][6];
		double[][] bounds2 = new double[][]{inflationbounds, inflationbounds, inflationbounds};
		par3[0] = par2[0].clone();
		par3[5] = par2[1].clone();
		for (int i=1; i<5;i++) {
			int j = 0;
			par3[i] = par2[0].clone();
			par3[i+5] = par2[1].clone();
			for (double[] b: bounds2) {

				//par[i][j] = Math.pow(b[1]/b[0],rt.nextDouble())*b[0];
				par3[i][3+j] = b[0] + rt.nextDouble()*(b[1]-b[0]);
				par3[i+5][3+j] = par3[i][3+j];
				j++;	



			}
		}




		for (double[] p: par3) {


			ArrayList<ErrorConfiguration> errorList1a = new ArrayList<ErrorConfiguration>();

			errorList1a.add(new ErrorConfiguration(StateDefinition.K_CELL,p[0],p[3]));
			errorList1a.add(new ErrorConfiguration(StateDefinition.INFLOW_NODE,p[1],p[4]));
			errorList1a.add(new ErrorConfiguration(StateDefinition.TF_NODE,p[2],p[5]));
			errorConfigs.add(errorList1a);
		}

		ArrayList<AssimilationMethod> assimilationMethods = new ArrayList<AssimilationMethod>();
		assimilationMethods.add(AssimilationMethod.ENKF);

		assimilationMethods.add(AssimilationMethod.DENKF);



		assimilationMethods.add(AssimilationMethod.LENKF_MEASUREMENT);
		assimilationMethods.add(AssimilationMethod.LENKF_GRID);
		assimilationMethods.add(AssimilationMethod.DENKF_MEASUREMENT);
		assimilationMethods.add(AssimilationMethod.DENKF_GRID);

		ArrayList<Integer> localizationWidths = new ArrayList<Integer>();
		//localizationWidths.add(1);
		//localizationWidths.add(5);
		//localizationWidths.add(10);
		//localizationWidths.add(15);
		localizationWidths.add(20);
		//localizationWidths.add(25);
		//localizationWidths.add(35);
		//localizationWidths.add(45);
		//localizationWidths.add(60);
		//localizationWidths.add(9999);

		ArrayList<Integer> inflowTFFactors = new ArrayList<Integer>();

		inflowTFFactors.add(3);



		ArrayList<Integer> ensembleSizes = new ArrayList<Integer>();
		ensembleSizes.add(20);
		//ensembleSizes.add(14);
		//ensembleSizes.add(10);
		//ensembleSizes.add(7);
		//ensembleSizes.add(5);
		//ensembleSizes.add(40);

		boolean forecastsNeeded = true;

		ExperimentConfiguration expConfig = new ExperimentConfiguration(errorConfigs,assimilationMethods,networkConfigs,localizationWidths, inflowTFFactors, ensembleSizes, forecastsNeeded, extendedOutput);
		//ExperimentConfiguration expConfig = new ExperimentConfiguration(runConfigurations,networkConfigs, forecastsNeeded);

		ArrayList<ErrorConfiguration> errorList = new ArrayList<ErrorConfiguration>();
		errorList.add(new ErrorConfiguration(StateDefinition.K_CELL,0.000,1.00));
		errorList.add(new ErrorConfiguration(StateDefinition.INFLOW_NODE,0.00,1.00));
		errorList.add(new ErrorConfiguration(StateDefinition.TF_NODE,0.00,1.00));
		expConfig.addRunConfiguration(new EnKFRunConfiguration(AssimilationMethod.ENKF, 5, errorList, 2));

		return expConfig;
	}
	public static ExperimentConfiguration experiment3(ArrayList<String> networkConfigs) {

		ArrayList<ArrayList<ErrorConfiguration>> errorConfigs = new ArrayList<ArrayList<ErrorConfiguration>>();
		// first MC experiment: 
		Random rt = new Random(65);
		double[][] bounds = new double[6][2];
		bounds[0] = new double[]{0.001,0.01};
		bounds[1] = new double[]{0.01,0.3};
		bounds[2] = new double[]{0.05,0.45};
		bounds[3] = new double[]{1.000,1.00};
		bounds[4] = new double[]{1.000,1.00};
		bounds[5] = new double[]{1.000,1.00};

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

		double[][] par2 = new double[][]{par[4], par[9]};
		double[] inflationbounds = new double[]{1.0, 1.05};
		double[][] par3 = new double[10][6];
		double[][] bounds2 = new double[][]{inflationbounds, inflationbounds, inflationbounds};
		par3[0] = par2[0].clone();
		par3[5] = par2[1].clone();
		for (int i=1; i<5;i++) {
			int j = 0;
			par3[i] = par2[0].clone();
			par3[i+5] = par2[1].clone();
			for (double[] b: bounds2) {

				//par[i][j] = Math.pow(b[1]/b[0],rt.nextDouble())*b[0];
				par3[i][3+j] = b[0] + rt.nextDouble()*(b[1]-b[0]);
				par3[i+5][3+j] = par3[i][3+j];
				j++;	



			}
		}
		double[][] par4 = new double[][]{par3[2],par3[1],par3[9],par3[5],par3[0]};
		for (double[] p: par4) {


			ArrayList<ErrorConfiguration> errorList1a = new ArrayList<ErrorConfiguration>();

			errorList1a.add(new ErrorConfiguration(StateDefinition.K_CELL,p[0],p[3]));
			errorList1a.add(new ErrorConfiguration(StateDefinition.INFLOW_NODE,p[1],p[4]));
			errorList1a.add(new ErrorConfiguration(StateDefinition.TF_NODE,p[2],p[5]));
			errorConfigs.add(errorList1a);
		}


		int[] ensemblesizes = new int[]{40,28,20,14,10,7,5};

		ArrayList<EnKFRunConfiguration> runConfigurations = new ArrayList<EnKFRunConfiguration>();
		for (int e: ensemblesizes) {
			runConfigurations.add(new EnKFRunConfiguration(AssimilationMethod.ENKF, 20, errorConfigs.get(0), e,60));
			//runConfigurations.add(new EnKFRunConfiguration(AssimilationMethod.ENKF_SMW, 20, errorConfigs.get(0), e,60));

			runConfigurations.add(new EnKFRunConfiguration(AssimilationMethod.DENKF, 20, errorConfigs.get(3), e,60));
			//runConfigurations.add(new EnKFRunConfiguration(AssimilationMethod.DENKF_SMW, 20, errorConfigs.get(3), e,60));

			runConfigurations.add(new EnKFRunConfiguration(AssimilationMethod.LENKF_MEASUREMENT, 20, errorConfigs.get(2), e,60));
			runConfigurations.add(new EnKFRunConfiguration(AssimilationMethod.LENKF_GRID, 20, errorConfigs.get(1), e,60));
			//runConfigurations.add(new EnKFRunConfiguration(AssimilationMethod.LENKF_GRID_SMW, 20, errorConfigs.get(1), e,60));

			runConfigurations.add(new EnKFRunConfiguration(AssimilationMethod.DENKF_MEASUREMENT, 20, errorConfigs.get(4), e,60));
			runConfigurations.add(new EnKFRunConfiguration(AssimilationMethod.DENKF_GRID, 20, errorConfigs.get(3), e,60));
			//runConfigurations.add(new EnKFRunConfiguration(AssimilationMethod.DENKF_GRID_SMW, 20, errorConfigs.get(3), e,60));

		}


		boolean forecastsNeeded = true;

		ExperimentConfiguration expConfig = new ExperimentConfiguration(runConfigurations,networkConfigs, forecastsNeeded, extendedOutput);

		ArrayList<ErrorConfiguration> errorList = new ArrayList<ErrorConfiguration>();
		errorList.add(new ErrorConfiguration(StateDefinition.K_CELL,0.000,1.00));
		errorList.add(new ErrorConfiguration(StateDefinition.INFLOW_NODE,0.00,1.00));
		errorList.add(new ErrorConfiguration(StateDefinition.TF_NODE,0.00,1.00));
		expConfig.addRunConfiguration(new EnKFRunConfiguration(AssimilationMethod.ENKF, 5, errorList, 2));

		return expConfig;
	}
	public static ExperimentConfiguration experiment4(ArrayList<String> networkConfigs) {

		ArrayList<ArrayList<ErrorConfiguration>> errorConfigs = new ArrayList<ArrayList<ErrorConfiguration>>();
		// first MC experiment: 
		Random rt = new Random(65);
		double[][] bounds = new double[6][2];
		bounds[0] = new double[]{0.001,0.01};
		bounds[1] = new double[]{0.01,0.3};
		bounds[2] = new double[]{0.05,0.45};
		bounds[3] = new double[]{1.000,1.00};
		bounds[4] = new double[]{1.000,1.00};
		bounds[5] = new double[]{1.000,1.00};

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

		double[][] par2 = new double[][]{par[4], par[9]};
		double[] inflationbounds = new double[]{1.0, 1.05};
		double[][] par3 = new double[10][6];
		double[][] bounds2 = new double[][]{inflationbounds, inflationbounds, inflationbounds};
		par3[0] = par2[0].clone();
		par3[5] = par2[1].clone();
		for (int i=1; i<5;i++) {
			int j = 0;
			par3[i] = par2[0].clone();
			par3[i+5] = par2[1].clone();
			for (double[] b: bounds2) {

				//par[i][j] = Math.pow(b[1]/b[0],rt.nextDouble())*b[0];
				par3[i][3+j] = b[0] + rt.nextDouble()*(b[1]-b[0]);
				par3[i+5][3+j] = par3[i][3+j];
				j++;	



			}
		}



		double[][] par4 = new double[][]{par3[2],par3[1],par3[9],par3[5],par3[0]};
		for (double[] p: par4) {


			ArrayList<ErrorConfiguration> errorList1a = new ArrayList<ErrorConfiguration>();

			errorList1a.add(new ErrorConfiguration(StateDefinition.K_CELL,p[0],p[3]));
			errorList1a.add(new ErrorConfiguration(StateDefinition.INFLOW_NODE,p[1],p[4]));
			errorList1a.add(new ErrorConfiguration(StateDefinition.TF_NODE,p[2],p[5]));
			errorConfigs.add(errorList1a);
		}
		int[] ensemblesizes2 = new int[]{20};
		int[] localizationradii = new int[]{20,14,10,7,5};
		int[] localizationFactor = new int[]{5,4,3,2,1};
		ArrayList<EnKFRunConfiguration> runConfigurations = new ArrayList<EnKFRunConfiguration>();
		for (int e: ensemblesizes2) {
			runConfigurations.add(new EnKFRunConfiguration(AssimilationMethod.DENKF, 20, errorConfigs.get(3), e));
			for (int l: localizationradii) {
				for (int f: localizationFactor) {
					//runConfigurations.add(new EnKFRunConfiguration(AssimilationMethod.ENKF, 20, errorConfigs.get(2), e));

					//runConfigurations.add(new EnKFRunConfiguration(AssimilationMethod.LENKF_MEASUREMENT, 20, errorConfigs.get(1), e));
					//runConfigurations.add(new EnKFRunConfiguration(AssimilationMethod.LENKF_GRID, 20, errorConfigs.get(3), e));
					runConfigurations.add(new EnKFRunConfiguration(AssimilationMethod.DENKF_MEASUREMENT, l, errorConfigs.get(4), e, l*f));
					runConfigurations.add(new EnKFRunConfiguration(AssimilationMethod.DENKF_GRID, l, errorConfigs.get(3), e, l*f));
				}
			}
		}


		boolean forecastsNeeded = true;

		ExperimentConfiguration expConfig = new ExperimentConfiguration(runConfigurations,networkConfigs, forecastsNeeded, extendedOutput);

		ArrayList<ErrorConfiguration> errorList = new ArrayList<ErrorConfiguration>();
		errorList.add(new ErrorConfiguration(StateDefinition.K_CELL,0.000,1.00));
		errorList.add(new ErrorConfiguration(StateDefinition.INFLOW_NODE,0.00,1.00));
		errorList.add(new ErrorConfiguration(StateDefinition.TF_NODE,0.00,1.00));
		expConfig.addRunConfiguration(new EnKFRunConfiguration(AssimilationMethod.ENKF, 5, errorList, 2));

		return expConfig;
	}
	public static ExperimentConfiguration experiment5(ArrayList<String> networkConfigs) {

		ArrayList<ArrayList<ErrorConfiguration>> errorConfigs = new ArrayList<ArrayList<ErrorConfiguration>>();
		// first MC experiment: 
		Random rt = new Random(65);
		double[][] bounds = new double[6][2];
		bounds[0] = new double[]{0.001,0.01};
		bounds[1] = new double[]{0.01,0.3};
		bounds[2] = new double[]{0.05,0.45};
		bounds[3] = new double[]{1.000,1.00};
		bounds[4] = new double[]{1.000,1.00};
		bounds[5] = new double[]{1.000,1.00};

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

		double[][] par2 = new double[][]{par[4], par[9]};
		double[] inflationbounds = new double[]{1.0, 1.05};
		double[][] par3 = new double[10][6];
		double[][] bounds2 = new double[][]{inflationbounds, inflationbounds, inflationbounds};
		par3[0] = par2[0].clone();
		par3[5] = par2[1].clone();
		for (int i=1; i<5;i++) {
			int j = 0;
			par3[i] = par2[0].clone();
			par3[i+5] = par2[1].clone();
			for (double[] b: bounds2) {

				//par[i][j] = Math.pow(b[1]/b[0],rt.nextDouble())*b[0];
				par3[i][3+j] = b[0] + rt.nextDouble()*(b[1]-b[0]);
				par3[i+5][3+j] = par3[i][3+j];
				j++;	



			}
		}



		double[][] par4 = new double[][]{par3[2],par3[1],par3[9],par3[5],par3[0]};
		for (double[] p: par4) {


			ArrayList<ErrorConfiguration> errorList1a = new ArrayList<ErrorConfiguration>();

			errorList1a.add(new ErrorConfiguration(StateDefinition.K_CELL,p[0],p[3]));
			errorList1a.add(new ErrorConfiguration(StateDefinition.INFLOW_NODE,p[1],p[4]));
			errorList1a.add(new ErrorConfiguration(StateDefinition.TF_NODE,p[2],p[5]));
			errorConfigs.add(errorList1a);
		}
		int[] ensemblesizes2 = new int[]{20};
		int[] localizationradii = new int[]{20,14,10,7,5};
		int[] localizationFactor = new int[]{40,28,20,14,10};
		ArrayList<EnKFRunConfiguration> runConfigurations = new ArrayList<EnKFRunConfiguration>();
		for (int e: ensemblesizes2) {
			runConfigurations.add(new EnKFRunConfiguration(AssimilationMethod.DENKF, 20, errorConfigs.get(3), e));
			for (int l: localizationradii) {
				for (int f: localizationFactor) {
					//runConfigurations.add(new EnKFRunConfiguration(AssimilationMethod.ENKF, 20, errorConfigs.get(2), e));

					//runConfigurations.add(new EnKFRunConfiguration(AssimilationMethod.LENKF_MEASUREMENT, 20, errorConfigs.get(1), e));
					//runConfigurations.add(new EnKFRunConfiguration(AssimilationMethod.LENKF_GRID, 20, errorConfigs.get(3), e));
					runConfigurations.add(new EnKFRunConfiguration(AssimilationMethod.DENKF_MEASUREMENT, l, errorConfigs.get(4), e, f));
					runConfigurations.add(new EnKFRunConfiguration(AssimilationMethod.DENKF_GRID, l, errorConfigs.get(3), e, f));
				}
			}
		}


		boolean forecastsNeeded = true;

		ExperimentConfiguration expConfig = new ExperimentConfiguration(runConfigurations,networkConfigs, forecastsNeeded, extendedOutput);

		ArrayList<ErrorConfiguration> errorList = new ArrayList<ErrorConfiguration>();
		errorList.add(new ErrorConfiguration(StateDefinition.K_CELL,0.000,1.00));
		errorList.add(new ErrorConfiguration(StateDefinition.INFLOW_NODE,0.00,1.00));
		errorList.add(new ErrorConfiguration(StateDefinition.TF_NODE,0.00,1.00));
		expConfig.addRunConfiguration(new EnKFRunConfiguration(AssimilationMethod.ENKF, 5, errorList, 2));

		return expConfig;
	}
	public static ExperimentConfiguration experiment6(ArrayList<String> networkConfigs) {

		ArrayList<ArrayList<ErrorConfiguration>> errorConfigs = new ArrayList<ArrayList<ErrorConfiguration>>();
		// first MC experiment: 
		Random rt = new Random(65);
		double[][] bounds = new double[6][2];
		bounds[0] = new double[]{0.001,0.01};
		bounds[1] = new double[]{0.01,0.3};
		bounds[2] = new double[]{0.05,0.45};
		bounds[3] = new double[]{1.000,1.00};
		bounds[4] = new double[]{1.000,1.00};
		bounds[5] = new double[]{1.000,1.00};

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

		for (double[] p: par) {
			ArrayList<ErrorConfiguration> errorList1a = new ArrayList<ErrorConfiguration>();
			errorList1a.add(new ErrorConfiguration(StateDefinition.K_CELL,p[0],p[3]));
			errorList1a.add(new ErrorConfiguration(StateDefinition.INFLOW_NODE,p[1],p[4]));
			errorList1a.add(new ErrorConfiguration(StateDefinition.TF_NODE,p[2],p[5]));
			errorConfigs.add(errorList1a);

		}


		ArrayList<AssimilationMethod> assimilationMethods = new ArrayList<AssimilationMethod>();
		//assimilationMethods.add(AssimilationMethod.ENKF);
		//assimilationMethods.add(AssimilationMethod.ENKF_SMW);
		//assimilationMethods.add(AssimilationMethod.DENKF);
		//assimilationMethods.add(AssimilationMethod.DENKF_SMW);
		assimilationMethods.add(AssimilationMethod.ENKF_SCHUR);
		assimilationMethods.add(AssimilationMethod.ENKF_SCHUR_SMW);

		assimilationMethods.add(AssimilationMethod.LENKF_MEASUREMENT);
		assimilationMethods.add(AssimilationMethod.LENKF_GRID);
		assimilationMethods.add(AssimilationMethod.DENKF_MEASUREMENT);
		assimilationMethods.add(AssimilationMethod.DENKF_GRID);

		ArrayList<Integer> localizationWidths = new ArrayList<Integer>();
		//localizationWidths.add(1);
		//localizationWidths.add(5);
		//localizationWidths.add(10);
		//localizationWidths.add(15);
		localizationWidths.add(20);
		//localizationWidths.add(25);
		//localizationWidths.add(35);
		//localizationWidths.add(45);
		//localizationWidths.add(60);
		//localizationWidths.add(9999);

		ArrayList<Integer> inflowTFFactors = new ArrayList<Integer>();

		inflowTFFactors.add(3);



		ArrayList<Integer> ensembleSizes = new ArrayList<Integer>();
		ensembleSizes.add(20);
		//ensembleSizes.add(14);
		//ensembleSizes.add(10);
		//ensembleSizes.add(7);
		//ensembleSizes.add(5);
		//ensembleSizes.add(40);

		boolean forecastsNeeded = true;

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
		/*PrintWriter out;
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
		}*/
		PrintWriter out1;
		try {
			//out1 = new PrintWriter("sumConfigEnKF.m");
			out1 = new PrintWriter(new BufferedWriter(new FileWriter("sumConfigEnKF"+prefix+".m")));
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("experiment is finalized");
	}
}
