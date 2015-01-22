package nl.tudelft.otsim.Simulators.MacroSimulator.TestCases;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import Jama.Matrix;
import nl.tudelft.otsim.Events.Scheduler;
import nl.tudelft.otsim.GUI.FakeGraphicsPanel;
import nl.tudelft.otsim.GUI.Main;
import nl.tudelft.otsim.Simulators.MacroSimulator.Link;
import nl.tudelft.otsim.Simulators.MacroSimulator.MacroCell;
import nl.tudelft.otsim.Simulators.MacroSimulator.MacroSimulator;
import nl.tudelft.otsim.Simulators.MacroSimulator.Model;
import nl.tudelft.otsim.Simulators.MacroSimulator.Nodes.Node;
import nl.tudelft.otsim.Simulators.MacroSimulator.Nodes.NodeBoundaryIn;
import nl.tudelft.otsim.Simulators.MacroSimulator.Nodes.NodeDetector;
import nl.tudelft.otsim.Simulators.MacroSimulator.Nodes.NodeInteriorTampere;

public class TestEKF {
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
	int mode = 1;
	//protected int[] state = new int[]{0,2,3}; // 0 = density, 1 = vLim, 2 = kCri, 3 = kJam;



	public static void main(String[] args) {
		// TODO Auto-generated method stub
		double inflow = (2500.0);
		String otsimConfiguration = "EndTime:\t1800.00\nSeed:\t1\n"
				+ "Roadway:	0	from	1	to	2	speedlimit	120	lanes	2	vertices	(0.000,-0.250,0.000)	(3000.000,-0.250,0.000)	ins	outs	1\n"
				+ "Roadway:	1	from	2	to	3	speedlimit	120	lanes	1	vertices	(3000.000,-0.250,0.000)	(3500.000,-2.000,0.000)	ins	0	outs\n"
				+ "TrafficClass	passengerCar_act	4.000	140.000	-6.000	0.900000	600.000\nTripPattern	numberOfTrips:	[0.000/"+inflow+"][0.000/1.000000]	LocationPattern:	[z1, z2]	Fractions	passengerCar_act:1.000000\nTripPatternPath	numberOfTrips:	[0.000/"+inflow+"][0.000/1.000000]	NodePattern:	[origin ID=1 (0.00m, 0.00m, 0.00m), destination ID=2 (3500.00m, 0.00m, 0.00m)]\nPath:	1.00000	nodes:	1	2	3\n"
				+ "Detector:	0	(500.000,-0.250,0.000)	(1000.000,-0.250,0.000)	(1500.000,-0.250,0.000)	(2000.000,-0.250,0.000)	(2500.000,-0.250,0.000)";
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
		Scheduler scheduler = new Scheduler(MacroSimulator.simulatorType, new FakeGraphicsPanel(), otsimConfiguration);
		// Do something with the scheduler
		Model macromodel = (Model) scheduler.getSimulator().getModel();
		macromodel.init();

		TestEKF test = new TestEKF();

		test.macromodel = macromodel;
		test.init(0);
		int nrSteps = 30;
		Matrix[] speeds = new Matrix[nrSteps];


		for (int i = 0; i < nrSteps; i++) {
			scheduler.stepUpTo(i*60.0);
			Matrix y = new Matrix(test.nrObservations,1);
			Matrix[] output = test.update(y);

			speeds[i] = output[1].copy();
			//System.out.println(output);
			//System.out.println(output[0].getArray());
		}

		System.out.print("{");
		/*for (int i = 0; i < nrSteps; i++) {

			double[][] arr = speeds[i].transpose().getArrayCopy();
			System.out.print(Arrays.toString(arr[0])+",");
		}*/
		for (int i = 0; i < nrSteps; i++) {
			System.out.print("{");
			double[][] arr = speeds[i].transpose().getArrayCopy();
			for (int j=0; j< arr[0].length; j++) {
				System.out.print(arr[0][j]);
				if (j<arr[0].length-1)
					System.out.print(",");
			}
			System.out.print("}");
			if (i < nrSteps-1)
				System.out.print(",");
		}
		System.out.print("}");
		System.out.println("einde");






	}
	public TestEKF() {

	}
	public void init(int mode) {
		this.mode = mode;
		nrCells = macromodel.getCells().size();
		nrLinks = macromodel.getLinks().size();
		locDetSpeed = new ArrayList<MacroCell>();
		locDetFlow = new ArrayList<MacroCell>();
		detectors = macromodel.getDetectors();
		/*for (MacroCell m: macromodel.getCells()) {
			if (m.detector) {
				locDetSpeed.add(m);
				locDetFlow.add(m);
			}
		}*/
		for (NodeDetector nd: macromodel.getDetectors()) {
			locDetSpeed.add(nd.getClosestCell());
			locDetFlow.add(nd.getClosestCell());
			
		}
		nrSpeedObservations = locDetSpeed.size();
		nrFlowObservations = locDetFlow.size();
		nrObservations = nrSpeedObservations + nrFlowObservations;


		if (linkStates) {
			nrStateVariablesPerCell = 1;
			nrStateVariablesPerLink = 4;
		} else {
			nrStateVariablesPerCell = 5;
			nrStateVariablesPerLink = 0;
		}
		nrInflowNodes = macromodel.getInflowNodes().size();

		indicesStateCells = new int[]{0,nrCells*nrStateVariablesPerCell};
		indicesStateLinks = new int[]{indicesStateCells[1],indicesStateCells[1]+nrLinks*nrStateVariablesPerLink};
		indicesStateInflowNodes = new int[]{indicesStateLinks[1],indicesStateLinks[1] + nrInflowNodes};

		nrStateVariables = indicesStateInflowNodes[1];
		if (mode==0) {

			F = new Matrix(nrStateVariables, nrStateVariables);
			P = Matrix.identity(nrStateVariables, nrStateVariables).times(0);
			R = Matrix.identity(nrObservations, nrObservations).times(0.1);
			Q = Matrix.identity(nrStateVariables, nrStateVariables).times(0);
		} else {

			if (!adaptable) {
				F = new Matrix(nrStateVariables, nrStateVariables);
				P = Matrix.identity(nrStateVariables, nrStateVariables).times(0.0000001);
				R = Matrix.identity(nrObservations, nrObservations).times(1);

				double q = 0.001;
				Q = Matrix.identity(nrStateVariables, nrStateVariables).times(q);



				P.set(indicesStateInflowNodes[0], indicesStateInflowNodes[0], 13.3);


				double initErrorSpeedObs = 7.7;
				double initErrorFlowObs= 0.0038;
				R.setMatrix(0,nrSpeedObservations-1,0,nrSpeedObservations-1,Matrix.identity(nrSpeedObservations, nrSpeedObservations).times(initErrorSpeedObs));
				R.setMatrix(nrSpeedObservations,nrSpeedObservations+nrFlowObservations-1,nrSpeedObservations,nrSpeedObservations+nrFlowObservations-1,Matrix.identity(nrFlowObservations, nrFlowObservations).times(initErrorFlowObs));

				double errorDensity=0.005;
				double errorVLim = 0.0;
				double errorKCri = 0.000000;
				double errorKJam = 0.000000;
				double errorVCri = 0.000000;
				if (linkStates) {
					Q.setMatrix(0,indicesStateCells[1]-1,0,indicesStateCells[1]-1,Matrix.identity(nrCells, nrCells).times(errorDensity));

					/*double[][] block = new double[nrCells][nrCells];
					for (double[] row: block)
						Arrays.fill(row, 1.0);
					Matrix ones = new Matrix(block);
					
					ones = new Matrix(new double[nrCells][nrCells]);
					ones.set(0, 0, 1);

					Q.setMatrix(nrCells,2*nrCells-1,nrCells,2*nrCells-1,ones.times(errorVLim));
					Q.setMatrix(2*nrCells,3*nrCells-1,2*nrCells,3*nrCells-1,ones.times(errorKCri));
					Q.setMatrix(3*nrCells,4*nrCells-1,3*nrCells,4*nrCells-1,ones.times(errorKJam));
*/
					Q.setMatrix(indicesStateLinks[0],indicesStateLinks[0]+nrLinks-1,indicesStateLinks[0],indicesStateLinks[0]+nrLinks-1,Matrix.identity(nrLinks, nrLinks).times(errorVLim));
					Q.setMatrix(indicesStateLinks[0]+nrLinks,indicesStateLinks[0]+2*nrLinks-1,indicesStateLinks[0]+nrLinks,indicesStateLinks[0]+2*nrLinks-1,Matrix.identity(nrLinks, nrLinks).times(errorKCri));
					Q.setMatrix(indicesStateLinks[0]+2*nrLinks,indicesStateLinks[0]+3*nrLinks-1,indicesStateLinks[0]+2*nrLinks,indicesStateLinks[0]+3*nrLinks-1,Matrix.identity(nrLinks, nrLinks).times(errorKJam));
					Q.setMatrix(indicesStateLinks[0]+3*nrLinks,indicesStateLinks[0]+4*nrLinks-1,indicesStateLinks[0]+3*nrLinks,indicesStateLinks[0]+4*nrLinks-1,Matrix.identity(nrLinks, nrLinks).times(errorVCri));

				} else {

					Q.setMatrix(0,nrCells-1,0,nrCells-1,Matrix.identity(nrCells, nrCells).times(errorDensity));
					Q.setMatrix(nrCells,2*nrCells-1,nrCells,2*nrCells-1,Matrix.identity(nrCells, nrCells).times(errorVLim));
					Q.setMatrix(2*nrCells,3*nrCells-1,2*nrCells,3*nrCells-1,Matrix.identity(nrCells, nrCells).times(errorKCri));
					Q.setMatrix(3*nrCells,4*nrCells-1,3*nrCells,4*nrCells-1,Matrix.identity(nrCells, nrCells).times(errorKJam));
					Q.setMatrix(4*nrCells,5*nrCells-1,4*nrCells,5*nrCells-1,Matrix.identity(nrCells, nrCells).times(errorVCri));

				}


				double inflowError = 0.3;
				Q.setMatrix(indicesStateInflowNodes[0], indicesStateInflowNodes[1]-1,indicesStateInflowNodes[0], indicesStateInflowNodes[1]-1, Matrix.identity(nrInflowNodes, nrInflowNodes).times(inflowError));
				P = Q.times(2);
			} else {
				F = new Matrix(nrStateVariables, nrStateVariables);
				P = Matrix.identity(nrStateVariables, nrStateVariables).times(0.0000001);
				P.set(indicesStateInflowNodes[0], indicesStateInflowNodes[0], 13.3);
				double alfa = 0.000001;
				beta = 1;
				P = Matrix.identity(nrStateVariables, nrStateVariables).times(alfa);
				R = Matrix.identity(nrObservations, nrObservations).times(beta);

				double q = 0.0001;
				Q = Matrix.identity(nrStateVariables, nrStateVariables).times(alfa);
				double errorDensity=0.0001;
				double errorVLim = 0.00;
				double errorKCri = 0.00000;
				double errorKJam = 0.00000;
				double errorVCri = 0.00000;
				Q.setMatrix(0,nrCells-1,0,nrCells-1,Matrix.identity(nrCells, nrCells).times(errorDensity));
				Q.setMatrix(nrCells,2*nrCells-1,nrCells,2*nrCells-1,Matrix.identity(nrCells, nrCells).times(errorVLim));
				Q.setMatrix(2*nrCells,3*nrCells-1,2*nrCells,3*nrCells-1,Matrix.identity(nrCells, nrCells).times(errorKCri));
				Q.setMatrix(3*nrCells,4*nrCells-1,3*nrCells,4*nrCells-1,Matrix.identity(nrCells, nrCells).times(errorKJam));
				Q.setMatrix(4*nrCells,5*nrCells-1,4*nrCells,5*nrCells-1,Matrix.identity(nrCells, nrCells).times(errorVCri));

				double inflowError = 0.3;
				Q.setMatrix(indicesStateInflowNodes[0], indicesStateInflowNodes[1]-1,indicesStateInflowNodes[0], indicesStateInflowNodes[1]-1, Matrix.identity(nrInflowNodes, nrInflowNodes).times(inflowError));

				double initErrorSpeedObs = beta/1;
				double initErrorFlowObs= beta/50;
				R.setMatrix(0,nrSpeedObservations-1,0,nrSpeedObservations-1,Matrix.identity(nrSpeedObservations, nrSpeedObservations).times(initErrorSpeedObs));
				R.setMatrix(nrSpeedObservations,nrSpeedObservations+nrFlowObservations-1,nrSpeedObservations,nrSpeedObservations+nrFlowObservations-1,Matrix.identity(nrFlowObservations, nrFlowObservations).times(initErrorFlowObs));
				baseR = R.copy();
				P=Q.times(10);

			}

		}

	}
	public Matrix[] update(Matrix observations) {
		if (observations == null) 
			observations  = new Matrix(nrObservations,1);
		
		
		if (mode != 0) {

		double[] diffQIn = new double[5];
		double[] diffQOut = new double[5];
		for (MacroCell c: macromodel.getCells()) {
			int index = macromodel.getCells().indexOf(c);
			Link link = c.link;
			int indexLink = macromodel.getLinks().indexOf(link);
			int nrCellsIn = c.ups.size();
			int nrCellsOut = c.downs.size();
			int[] indexIn = new int[nrCellsIn];
			int[] indexOut = new int[nrCellsOut];
			double[] fDist = new double[]{0.000001,0,0,0,0};
			double[] fDistNeg = new double[]{-0.000001,0,0,0,0};
			double totFDist = fDist[0]-fDistNeg[0];
			if (nrCellsIn > 0) {
				for (int i = 0; i<nrCellsIn; i++) {
					indexIn[i] = macromodel.getCells().indexOf(c.ups.get(i));
				}
			}

			if (nrCellsOut > 0) {
				for (int j = 0; j<nrCellsOut; j++) {
					indexOut[j] = macromodel.getCells().indexOf(c.downs.get(j));
				}
			}
			if (c.nodeIn instanceof NodeInteriorTampere) {
				double[] d1 = new double[]{0,0,0,0,0};
				double[] d2 = new double[]{0,0,0,0,0};
				double d3;
				for (int s = 0; s<nrStateVariablesPerCell ; s++) {
					d1 = new double[]{0,0,0,0,0};
					d2 = new double[]{0,0,0,0,0};
					d1[s] = 0.00001;
					d2[s] = -0.00001;
					d3 = d1[s]-d2[s];
					NodeInteriorTampere n = (NodeInteriorTampere) c.nodeIn;
					double diffQInplus = n.calcFluxValue(c, d1);
					double diffQInmin = n.calcFluxValue(c,d2);
					diffQIn[s] = (diffQInplus - diffQInmin)/(d3);

					//System.out.println(diffQIn);

					if (diffQIn[s] != 0) 
						System.out.println("groterdan0");
				}
			}
			if (c.nodeIn instanceof NodeBoundaryIn) {
				NodeBoundaryIn n2 = (NodeBoundaryIn) c.nodeIn;
				double[] d1 = new double[]{0,0,0,0,0};
				double[] d2 = new double[]{0,0,0,0,0};
				double d3;
				for (int s = 0; s<nrStateVariablesPerCell ; s++) {
					d1 = new double[]{0,0,0,0,0};
					d2 = new double[]{0,0,0,0,0};
					d1[s] = 0.00001;
					d2[s] = -0.00001;
					d3 = d1[s]-d2[s];
					double diffQInplus = n2.calcFluxValue(c, d1, 0);
					double diffQInmin = n2.calcFluxValue(c,d2, 0);
					diffQIn[s] = (diffQInplus - diffQInmin)/(d3);
				}

			}

			if (c.nodeOut instanceof NodeInteriorTampere) {
				double[] d1 = new double[]{0,0,0,0,0};
				double[] d2 = new double[]{0,0,0,0,0};
				diffQOut = new double[]{0,0,0,0,0};
				double d3;
				for (int s = 0; s<nrStateVariablesPerCell ; s++) {
					d1 = new double[]{0,0,0,0,0};
					d2 = new double[]{0,0,0,0,0};
					d1[s] = 0.000001;
					d2[s] = -0.000001;
					d3 = d1[s]-d2[s];
					NodeInteriorTampere n = (NodeInteriorTampere) c.nodeOut;
					//double diffQnormal = n.calcFluxValue(c, new double[]{0,0,0,0});
					double diffQOutplus = n.calcFluxValue(c, d1);
					double diffQOutmin = n.calcFluxValue(c, d2);
					diffQOut[s] = (diffQOutplus - diffQOutmin)/(d3);

					//System.out.println(diffQOut);
					if (diffQOut[s] != 0) {
						System.out.println("groterdan1");
					}
					if (diffQOut[s] > 12 && diffQOut[s]<13) 
						System.out.println("groterdan12");
				}
			}
			for (int s = 0; s<nrStateVariablesPerCell ; s++) {
				if (s==0)
					F.set(index, index, 1+macromodel.dt/c.l*(diffQIn[s] - diffQOut[s]));
				if (s>0) {
					if (linkStates) {
						F.set(index, s*nrCells, macromodel.dt/c.l*(diffQIn[s] - diffQOut[s]));
						F.set(s*nrCells, s*nrCells, 1);
							
					} else {
						F.set(index, index+s*nrCells, macromodel.dt/c.l*(diffQIn[s] - diffQOut[s]));
						F.set(index + s*nrCells, index + s*nrCells, 1);
					}
				}
			}
			for (int s=0; s<nrStateVariablesPerLink; s++){
				F.set(index, indicesStateLinks[0]+s*nrLinks + indexLink, macromodel.dt/c.l*(diffQIn[s] - diffQOut[s]));
				F.set(indicesStateLinks[0]+s*nrLinks + indexLink, indicesStateLinks[0]+s*nrLinks + indexLink, 1);
			}
			for (int i: indexIn) {
				for (int s = 0; s<nrStateVariablesPerCell ; s++) {
					if (s==0)
						F.set(i, index + s*nrCells, -macromodel.dt/macromodel.getCells().get(i).l*(diffQIn[s]));
					if (s>0) {
						if (linkStates) {
							F.set(i, s*nrCells, -macromodel.dt/macromodel.getCells().get(i).l*(diffQIn[s]));
								
						} else {
							F.set(i, index + s*nrCells, -macromodel.dt/macromodel.getCells().get(i).l*(diffQIn[s]));
						}
					}
				}
				for (int s=0; s<nrStateVariablesPerLink; s++){
					F.set(i, indicesStateLinks[0]+s*nrLinks + indexLink, -macromodel.dt/macromodel.getCells().get(i).l*(diffQIn[s]));
					//F.set(indicesStateLinks[0]+s*nrLinks + indexLink, indicesStateLinks[0]+s*nrLinks + indexLink, 1);
				}
				//F.set(i*nrStateVariablesPerCell, index*nrStateVariablesPerCell, -macromodel.dt/macromodel.getCells().get(i).l*(diffQIn));
			}
			for (int j: indexOut) {
				//F.set(j*nrStateVariablesPerCell, index*nrStateVariablesPerCell, macromodel.dt/macromodel.getCells().get(j).l*(diffQOut));
				for (int s = 0; s<nrStateVariablesPerCell ; s++) {
		//			F.set(j, index + s*nrCells, macromodel.dt/macromodel.getCells().get(j).l*(diffQOut[s]));
					if (s==0)
						F.set(j, index + s*nrCells, macromodel.dt/macromodel.getCells().get(j).l*(diffQOut[s]));
					if (s>0) {
						if (linkStates) {
							F.set(j, s*nrCells, macromodel.dt/macromodel.getCells().get(j).l*(diffQOut[s]));
			
						} else {
							F.set(j, index + s*nrCells, macromodel.dt/macromodel.getCells().get(j).l*(diffQOut[s]));
						}
					}
				}
				for (int s=0; s<nrStateVariablesPerLink; s++){
					F.set(j, indicesStateLinks[0]+s*nrLinks + indexLink,  macromodel.dt/macromodel.getCells().get(j).l*(diffQOut[s]));
					//F.set(indicesStateLinks[0]+s*nrLinks + indexLink, indicesStateLinks[0]+s*nrLinks + indexLink, 1);
				}
			}
			/*if (c.nodeIn instanceof NodeBoundaryIn) {
				F.set(indicesStateInflowNodes[0] + macromodel.getInflowNodes().indexOf(c.nodeIn), index, );
			}*/
		}  
		for (NodeBoundaryIn n: macromodel.getInflowNodes()) {
			int index = indicesStateInflowNodes[0] + macromodel.getInflowNodes().indexOf(n);
			F.set(index, index, 1);
			double fDist = 0.0001;
			double diffQInplus = n.calcFluxValue(n.cellsOut.get(0), new double[]{0,0,0,0,0}, fDist);
			double diffQInmin = n.calcFluxValue(n.cellsOut.get(0), new double[]{0,0,0,0,0}, -fDist);
			double diffQIn2 =  (diffQInplus - diffQInmin)/(2*fDist);
			F.set(macromodel.getCells().indexOf(n.cellsOut.get(0)),index,diffQIn2*(macromodel.dt/macromodel.getCells().get(0).l));


		}
		Matrix F1 = F;
		Matrix F2 = F.times(F).times(F).times(F);
		//System.out.println(Arrays.deepToString(F.getArray()));
		}

		//System.out.println(Pnew);
		Matrix y = new Matrix(nrObservations,1);

		for (NodeDetector nd: detectors ) {
			double[] obs= nd.getMeasurements(macromodel.t()-60, macromodel.t(), macromodel.dt);
			//double[] obs= nd.getInstantMeasurements();
			y.set(detectors.indexOf(nd),0, obs[1]);
			y.set(nrSpeedObservations + detectors.indexOf(nd),0, obs[0]);
		}

		/*for (MacroCell mc: locDetSpeed) {
			y.set(locDetSpeed.indexOf(mc), 0, mc.VCell);
		}
		for (MacroCell mc: locDetFlow) {
			y.set(nrSpeedObservations + locDetSpeed.indexOf(mc), 0, mc.QCell);
		}*/
		//double[] h = new double[nrObservations];
		Matrix H = new Matrix(nrObservations,nrStateVariables);
		double[] d1 = new double[]{0,0,0,0,0};
		double[] d2 = new double[]{0,0,0,0,0};
		double d3;
		if (mode != 0) {
		if (linkStates) {
			for (MacroCell mc: locDetSpeed) {
				double[] h = new double[nrStateVariablesPerCell + nrStateVariablesPerLink];
				int i = locDetSpeed.indexOf(mc);
				for (int s = 0; s<nrStateVariablesPerCell ; s++) {
					d1 = new double[]{0,0,0,0,0};
					d2 = new double[]{0,0,0,0,0};
					d1[s] = 0.00001;
					d2[s] = -0.00001;
					d3 = d1[s]-d2[s];
					double[] fDist2 = new double[]{0.00001,0,0,0,0};
					double[] fDistNeg2 = new double[]{-0.00001,0,0,0,0};
					double totFDist2 = fDist2[0]-fDistNeg2[0];

					double h1 = mc.fd.calcV(mc, d1);
					double h2 =  mc.fd.calcV(mc, d2);
					h[s] = (h1 - h2)/(d3);
					double[] arr = new double[nrCells];
					Arrays.fill(arr, 1.0);
					double[][] arr1 = new double[1][nrCells];
					//arr1[0] = arr;
					arr1[0][0]=1;
					//H.setMatrix(i, i, s*nrCells, (s+1)*nrCells-1, (new Matrix(arr1)).times(h[s]));
					H.set(i, macromodel.getCells().indexOf(mc), h[s]);
				}
				for (int s = 1; s<nrStateVariablesPerLink+1 ; s++) {
					d1 = new double[]{0,0,0,0,0};
					d2 = new double[]{0,0,0,0,0};
					d1[s] = 0.00001;
					d2[s] = -0.00001;
					d3 = d1[s]-d2[s];
					double[] fDist2 = new double[]{0.00001,0,0,0,0};
					double[] fDistNeg2 = new double[]{-0.00001,0,0,0,0};
					double totFDist2 = fDist2[0]-fDistNeg2[0];

					double h1 = mc.fd.calcV(mc, d1);
					double h2 =  mc.fd.calcV(mc, d2);
					h[s] = (h1 - h2)/(d3);
					double[] arr = new double[nrCells];
					Arrays.fill(arr, 1.0);
					double[][] arr1 = new double[1][nrCells];
					//arr1[0] = arr;
					arr1[0][0]=1;
					//H.setMatrix(i, i, s*nrCells, (s+1)*nrCells-1, (new Matrix(arr1)).times(h[s]));
					H.set(i, indicesStateLinks[0]+macromodel.getLinks().indexOf(mc.link)+ (s-1)*nrLinks, h[s]);
				}
			}
			for (MacroCell mc: locDetFlow) {
				double[] h = new double[nrStateVariablesPerCell+ nrStateVariablesPerLink];
				int i = locDetFlow.indexOf(mc);
				for (int s = 0; s<nrStateVariablesPerCell ; s++) {
					d1 = new double[]{0,0,0,0,0};
					d2 = new double[]{0,0,0,0,0};
					d1[s] = 0.00001;
					d2[s] = -0.00001;
					d3 = d1[s]-d2[s];
					double[] fDist2 = new double[]{0.00001,0,0,0,0};
					double[] fDistNeg2 = new double[]{-0.00001,0,0,0,0};
					double totFDist2 = fDist2[0]-fDistNeg2[0];

					double h1 = mc.fd.calcQ(mc, d1);
					double h2 =  mc.fd.calcQ(mc, d2);
					h[s] = (h1 - h2)/(d3);
					double[] arr = new double[nrCells];
					Arrays.fill(arr, 1.0);
					double[][] arr1 = new double[1][nrCells];
					//arr1[0] = arr;
					arr1[0][0]=1;
					//H.setMatrix(i, i, s*nrCells, (s+1)*nrCells-1, (new Matrix(arr1)).times(h[s]));
					H.set(nrSpeedObservations+i, macromodel.getCells().indexOf(mc), h[s]);
				}
				for (int s = 1; s<nrStateVariablesPerLink+1 ; s++) {
					d1 = new double[]{0,0,0,0,0};
					d2 = new double[]{0,0,0,0,0};
					d1[s] = 0.00001;
					d2[s] = -0.00001;
					d3 = d1[s]-d2[s];
					double[] fDist2 = new double[]{0.00001,0,0,0,0};
					double[] fDistNeg2 = new double[]{-0.00001,0,0,0,0};
					double totFDist2 = fDist2[0]-fDistNeg2[0];

					double h1 = mc.fd.calcQ(mc, d1);
					double h2 =  mc.fd.calcQ(mc, d2);
					h[s] = (h1 - h2)/(d3);
					double[] arr = new double[nrCells];
					Arrays.fill(arr, 1.0);
					double[][] arr1 = new double[1][nrCells];
					//arr1[0] = arr;
					arr1[0][0]=1;
					//H.setMatrix(i, i, s*nrCells, (s+1)*nrCells-1, (new Matrix(arr1)).times(h[s]));
					H.set(nrSpeedObservations+i, indicesStateLinks[0]+macromodel.getLinks().indexOf(mc.link)+ (s-1)*nrLinks, h[s]);
				}
			}
		} else {
			for (MacroCell mc: locDetSpeed) {
				double[] h = new double[nrStateVariablesPerCell];
				int i = locDetSpeed.indexOf(mc);
				for (int s = 0; s<nrStateVariablesPerCell ; s++) {
					d1 = new double[]{0,0,0,0,0};
					d2 = new double[]{0,0,0,0,0};
					d1[s] = 0.00001;
					d2[s] = -0.00001;
					d3 = d1[s]-d2[s];
					double[] fDist2 = new double[]{0.00001,0,0,0,0};
					double[] fDistNeg2 = new double[]{-0.00001,0,0,0,0};
					double totFDist2 = fDist2[0]-fDistNeg2[0];

					double h1 = mc.fd.calcV(mc, d1);
					double h2 =  mc.fd.calcV(mc, d2);
					h[s] = (h1 - h2)/(d3);

					H.set(i, macromodel.getCells().indexOf(mc)+ s*nrCells, h[s]);
				}
			}
			for (MacroCell mc: locDetFlow) {
				double[] h = new double[nrStateVariablesPerCell];
				int i = locDetFlow.indexOf(mc);
				for (int s = 0; s<nrStateVariablesPerCell ; s++) {
					d1 = new double[]{0,0,0,0,0};
					d2 = new double[]{0,0,0,0,0};
					d1[s] = 0.00001;
					d2[s] = -0.00001;
					d3 = d1[s]-d2[s];
					double[] fDist2 = new double[]{0.00001,0,0,0,0};
					double[] fDistNeg2 = new double[]{-0.00001,0,0,0,0};
					double totFDist2 = fDist2[0]-fDistNeg2[0];

					double h1 = mc.fd.calcQ(mc, d1);
					double h2 =  mc.fd.calcQ(mc, d2);
					h[s] = (h1 - h2)/(d3);

					H.set(nrSpeedObservations+i, macromodel.getCells().indexOf(mc)+ s*nrCells, h[s]);
				}
			}
		}
		}
		Matrix Pnew;
		if (mode != 0)
			Pnew = ((F.times(P)).times(F.transpose())).plus(Q);
		else 
			Pnew = new Matrix(nrStateVariables,nrStateVariables);

		/*double[] h = new double[nrCells];
		Matrix H = new Matrix(nrCells,nrCells);
		for (int i = 0; i<nrCells; i++) {
			MacroCell mc = macromodel.getCells().get(i);
			if (locDet.contains(mc)) {
			double[] fDist2 = new double[]{0.00001,0,0,0};
			double[] fDistNeg2 = new double[]{-0.00001,0,0,0};
			double totFDist2 = fDist2[0]-fDistNeg2[0];

			double h1 = mc.fd.calcV(mc, fDist2);
			double h2 =  mc.fd.calcV(mc, fDistNeg2);
			h[i] = (h1 - h2)/(totFDist2);
			} else {
				h[i]=0;
			}
			H.set(i, i, h[i]);
		}*/

		Matrix xmin = new Matrix(nrStateVariables,1);
		
		if (linkStates) {
		for (int i = 0; i<nrCells; i++) {
			MacroCell mc = macromodel.getCells().get(i);
			if (mc.KCell <0)
				System.out.println("negativeKCell");
			xmin.set(i, 0, mc.KCell);
			
		}
		for (int i = 0; i<nrLinks; i++) {
			Link l = macromodel.getLinks().get(i);
			xmin.set(nrCells+i, 0, l.vLim);
			xmin.set(nrCells+nrLinks + i, 0, l.kCri);
			xmin.set(nrCells+2*nrLinks + i, 0, l.kJam);
			xmin.set(nrCells+3*nrLinks + i, 0, l.vCri);
		}
			
		for (int j = 0; j <macromodel.getInflowNodes().size(); j++) {
			xmin.set(indicesStateInflowNodes[0]+j, 0, macromodel.getInflowNodes().get(j).getInflow());
		}
		} else {
			for (int i = 0; i<nrCells; i++) {
				MacroCell mc = macromodel.getCells().get(i);
				if (mc.KCell <0)
					System.out.println("negativeKCell");
				
				xmin.set(i, 0, mc.KCell);
				
				xmin.set(i+ nrCells, 0, mc.vLim);
				
				xmin.set(i+ 2*nrCells, 0, mc.kCri);
			
				xmin.set(i+ 3*nrCells, 0, mc.kJam);
				xmin.set(i+ 4*nrCells, 0, mc.vCri);
			}
			for (int j = 0; j <macromodel.getInflowNodes().size(); j++) {
				xmin.set(indicesStateInflowNodes[0]+j, 0, macromodel.getInflowNodes().get(j).getInflow());
			}
		}
		
		Matrix Rster = R.copy();
		for (MacroCell mc: locDetFlow) {
			if (mc.VCell < 20)
				Rster.set(nrSpeedObservations+locDetFlow.indexOf(mc), nrSpeedObservations+locDetFlow.indexOf(mc), Rster.get(nrSpeedObservations+locDetFlow.indexOf(mc),nrSpeedObservations+locDetFlow.indexOf(mc))*2);
		}
	
		
		Matrix up = Pnew.times(H.transpose());
		Matrix down = (H.times(Pnew).times(H.transpose())).plus(R);
		Matrix G = up.times(down.inverse());

		//Matrix yster = y.times(0.95);
		Matrix e = observations.minus(y);
		Matrix update = G.times(e);
		Matrix x = xmin.plus(update);
		P = (Matrix.identity(nrStateVariables, nrStateVariables).minus((G.times(H)))).times(Pnew);
		//System.out.println(Arrays.toString(x.getArray()));
		Matrix yafter = new Matrix(nrStateVariables,1);
		if (mode != 0) {
		if (linkStates) {
			for (int i = 0; i<nrCells; i++) {
				MacroCell mc = macromodel.getCells().get(i);
				int indexLink = macromodel.getLinks().indexOf(mc.link);
				yafter.set(i,0,mc.fd.calcV(new double[]{x.get(i, 0),x.get(indicesStateLinks[0]+ indexLink, 0), x.get(indicesStateLinks[0]+ 1*nrLinks+ indexLink, 0), x.get(indicesStateLinks[0]+ + 2*nrLinks+indexLink, 0), x.get(indicesStateLinks[0]+ 3*nrLinks+indexLink, 0)}));

			}
			for (int j = 0; j <macromodel.getInflowNodes().size(); j++) {
				yafter.set(indicesStateInflowNodes[0]+j, 0, macromodel.getInflowNodes().get(j).getInflow());
			}
		} else {
		for (int i = 0; i<nrCells; i++) {
			MacroCell mc = macromodel.getCells().get(i);
			yafter.set(i,0,mc.fd.calcV(new double[]{x.get(i, 0),x.get(i+ nrCells, 0), x.get(i+ 2*nrCells, 0), x.get(i+ 3*nrCells, 0),  x.get(i+ 4*nrCells, 0)}));


		}
		for (int j = 0; j <macromodel.getInflowNodes().size(); j++) {
			yafter.set(indicesStateInflowNodes[0]+j, 0, macromodel.getInflowNodes().get(j).getInflow());
		}
		}
		if (linkStates) {
			double[][] array = (x.transpose()).getArrayCopy();
			double[] arr = Arrays.copyOfRange(array[0], 0, nrStateVariables);
			
			double[] densities = Arrays.copyOfRange(arr, 0, nrCells);
			
			double[] vlimsLinks = Arrays.copyOfRange(arr, nrCells, nrCells+nrLinks);
			double[] kcrisLinks = Arrays.copyOfRange(arr, nrCells+nrLinks, nrCells+2*nrLinks);
			double[] kjamsLinks = Arrays.copyOfRange(arr, nrCells+2*nrLinks, nrCells+3*nrLinks);
			double[] vcrisLinks = Arrays.copyOfRange(arr, nrCells+3*nrLinks, nrCells+4*nrLinks);
			
			
			
			macromodel.restoreState(densities, "density");
			macromodel.restoreStateLinks(vlimsLinks, "speedLimit");
			macromodel.restoreStateLinks(kcrisLinks, "criticalDensity");
			macromodel.restoreStateLinks(kjamsLinks, "jamDensity");
			macromodel.restoreStateLinks(vcrisLinks, "criticalSpeed");
			macromodel.restoreState(Arrays.copyOfRange(array[0], indicesStateInflowNodes[0], indicesStateInflowNodes[1]), "inflow");

		} else {
		double[][] array = (x.transpose()).getArrayCopy();
		double[] arr = Arrays.copyOfRange(array[0], indicesStateCells[0], indicesStateCells[1]);
		
		double[] densities = Arrays.copyOfRange(arr, 0, nrCells);
		double[] vlims = Arrays.copyOfRange(arr, nrCells, 2*nrCells);
		double[] kcris = Arrays.copyOfRange(arr, 2*nrCells, 3*nrCells);
		double[] kjams = Arrays.copyOfRange(arr, 3*nrCells, 4*nrCells);
		double[] vcris = Arrays.copyOfRange(arr, 4*nrCells, 5*nrCells);
		/*for (int i = 0; i<arr.length/4; i++) {
			densities[i] = arr[i*4];
			vlims[i] = arr[i*4+1];
			kcris[i] = arr[i*4+2];
			kjams[i] =arr[i*4+3];
		}*/
		macromodel.restoreState(densities, "density");
		macromodel.restoreState(vlims, "speedLimit");
		macromodel.restoreState(kcris, "criticalDensity");
		macromodel.restoreState(kjams, "jamDensity");
		macromodel.restoreState(vcris, "criticalSpeed");
		macromodel.restoreState(Arrays.copyOfRange(array[0], indicesStateInflowNodes[0], indicesStateInflowNodes[1]), "inflow");
		}
		}
		Matrix ya = new Matrix(nrObservations,1);
		for (NodeDetector nd: detectors ) {
			//double[] obs= nd.getMeasurements(macromodel.t()-60, macromodel.t());
			double[] obs = nd.getInstantMeasurements();
			ya.set(detectors.indexOf(nd),0, obs[1]);
			ya.set(nrSpeedObservations + detectors.indexOf(nd),0, obs[0]);
		}
		/*for (MacroCell mc: locDetSpeed) {
			ya.set(locDetSpeed.indexOf(mc), 0, mc.fd.calcV(new double[]{mc.KCell,mc.vLim, mc.kCri, mc.kJam}));
		}
		for (MacroCell mc: locDetFlow) {
			ya.set(nrSpeedObservations + locDetFlow.indexOf(mc), 0, mc.fd.calcQ(new double[]{mc.KCell,mc.vLim, mc.kCri, mc.kJam}));
		}*/
		if (mode != 0) {
		if (Pnew.rank() == nrStateVariables) {
			System.out.println(H);

			Matrix A1 = Pnew.inverse();
			Matrix A2 = (H.transpose()).times(H).times(beta);
			Matrix A = A1.plus(A2);

			//Matrix A = Pnew.inverse().plus((H.transpose()).times(H).times(beta));
			Matrix D1 = ((observations.minus(ya)).transpose());
			Matrix D2 = observations.minus(ya);
			Matrix D3 = D1.times(D2);
			Matrix D4 = A.inverse();
			Matrix D5 = ((D4.times(H.transpose())).times(H));
			double D6 = ((A.inverse().times(H.transpose())).times(H)).trace();

			double beta2 = nrObservations/(D3.get(0, 0) + D6);
			if (adaptable) {
				if (Double.isNaN(beta2) || beta2 == 0)
					System.out.println("beta: "+beta2);
				R = baseR.times(beta2);
				beta = beta2;
			}
			//double errorSpeedObs = beta/1;
			//double errorFlowObs= beta/100;
			//R.setMatrix(0,nrSpeedObservations-1,0,nrSpeedObservations-1,Matrix.identity(nrSpeedObservations, nrSpeedObservations).times(errorSpeedObs));
			//R.setMatrix(nrSpeedObservations,nrSpeedObservations+nrFlowObservations-1,nrSpeedObservations,nrSpeedObservations+nrFlowObservations-1,Matrix.identity(nrFlowObservations, nrFlowObservations).times(errorFlowObs));

		}
		}
		Matrix speeds = new Matrix(macromodel.saveStateToArray("speed"),1).transpose();
		Matrix[] output = AssimilationConfiguration.getOutput(macromodel, StateDefinition.K_CELL, StateDefinition.V_CELL, StateDefinition.TRAFFICREGIME_CELL); 
		
		return new Matrix[]{xmin,y,F,Pnew,observations,x,P,ya, output[1], output[2], new Matrix(1,1)};

	}

	public Matrix generateWhiteNoise(Matrix source, double[] std) {
		Random rnd = new Random();
		Matrix noise = new Matrix(source.getRowDimension(), source.getColumnDimension());
		for (int i=0; i<noise.getRowDimension(); i++) {
			for (int j=0; j<noise.getColumnDimension(); j++) {
				noise.set(i, j, rnd.nextGaussian()*std[j]);
			}
		}
		Matrix result = noise.plus(source);
		return result;
	}

	public Matrix generateWhiteNoise(Matrix source, double std) {
		int i = source.getRowDimension();
		double[] stdArray = new double[i];

		Arrays.fill(stdArray,std);
		return generateWhiteNoise(source, stdArray);
	}
	public static void exportForecastsToMatlab(ArrayList<ArrayList<Matrix[]>> forecasts, String filename, boolean plot) {
		PrintWriter out;
		if (filename.isEmpty())
			filename = "testEKF2";
		try {
			out = new PrintWriter(filename+".m");
			out.println("close all;");
			int nrObservations = 8;
			for (int i = 0; i< forecasts.size(); i++){
				ArrayList<Matrix[]> results = forecasts.get(i);
				for (int k = 0; k< results.size()-1; k++) {
					Matrix[] m = results.get(k);

					out.println("%k=" + k);
					out.println("f_time{"+(i+1)+"}("+(k+1)+",:)=" + Arrays.toString(m[10].getArray()[0]) + ";");
					//return new Matrix[]{xmin,y,F,Pnew,observations,x,P};
					//out.println("f_xmin{"+(i+1)+"}("+(k+1)+",:)="+Arrays.toString(m[0].transpose().getArray()[0])+";");
					//out.println("f_ymin{"+(i+1)+"}("+(k+1)+",:)="+Arrays.toString(m[1].transpose().getArray()[0])+";");
					out.println("f_x{"+(i+1)+"}("+(k+1)+",:)="+Arrays.toString(m[5].transpose().getArray()[0])+";");
					//out.println("f_obs{"+(i+1)+"}("+(k+1)+",:)="+Arrays.toString(m[4].transpose().getArray()[0])+";");
					out.println("f_y{"+(i+1)+"}("+(k+1)+",:)="+Arrays.toString(m[7].transpose().getArray()[0])+";");
					out.println("f_v{"+(i+1)+"}("+(k+1)+",:)="+Arrays.toString(m[8].transpose().getArray()[0])+";");
					out.println("f_tr{"+(i+1)+"}("+(k+1)+",:)="+Arrays.toString(m[9].transpose().getArray()[0])+";");

					/*for (int i = 0; i<m[0].getRowDimension(); i++) {
					out.println("xmin["+k+1+"]("+(i+1)+",:)="+Arrays.toString(m[0].getArray()[i]));
				}*/
					//m[0].pr;
				}
				ArrayList<Matrix[]> results2 = forecasts.get(i);

				/*Matrix m1 = results2.get(results2.size()-1)[0];
				int nrRows = m1.getRowDimension();
				out.println("%TTs" + i);
				for (int k = 0; k<nrRows/2; k++) {
					out.println("f_ttTime{"+(i+1)+"}("+(k+1)+",:)=" + Arrays.toString(m1.getArray()[k*2]) + ";");
					out.println("f_ttValues{"+(i+1)+"}("+(k+1)+",:)=" + Arrays.toString(m1.getArray()[k*2+1]) + ";");

				}*/
			}


			/*for (int i = 0; i<m[0].getRowDimension(); i++) {
				out.println("xmin["+k+1+"]("+(i+1)+",:)="+Arrays.toString(m[0].getArray()[i]));
			}*/
			//m[0].pr;

			if (plot) {
				//out.println("nrTimes = "+forecasts.get(0).size() + ";");
				//out.println("figure;hold all");

				for (int j=0; j< nrObservations; j++ ) {
					//out.println("subplot("+nrObservations+",1,"+(j+1)+")");
					out.println("figure;hold on");
					for (int k = 0; k<5; k++) {

						out.println("subplot(5,1,"+(k+1)+");hold all;");

						//out.println("plot(time(1:nrTimes),y(:,"+(j+1)+"),time(1:nrTimes),obs(:,"+(j+1)+"),time(1:nrTimes),ya(:,"+(j+1)+"))");
						out.println("plot(time(1:nrTimes),y(:,"+(j+1)+"),time(1:nrTimes),obs(:,"+(j+1)+"))");

						/*for (int i = 0; i< forecasts.size(); i++){
						out.println("plot(f_time{"+(i+1)+"}(:), f_y{"+(i+1)+"}(:,"+(j+1)+"),':o');");
					}*/
						for (int i = 0; i< forecasts.size(); i++){
							//out.println("plot(f_time{"+(i+1)+"}(:), f_y{"+(i+1)+"}(:,"+(j+1)+"),':o');");
							out.println("plot(f_time{"+(i+1)+"}("+(k+1)+"), f_y{"+(i+1)+"}("+(k+1)+","+(j+1)+"),':o');");
						}
						out.println("hold off");
					}

				}
				out.println("figure;hold all");
				out.println("plot(ttTimeTruth(ttValuesTruth>-1),ttValuesTruth(ttValuesTruth>-1));");
				out.println("plot(ttTime(ttValues>-1),ttValues(ttValues>-1));");


				for (int i = 0; i< forecasts.size(); i++){
					//out.println("subplot("+nrObservations+",1,"+(j+1)+")");
					out.println("plot(f_ttTime{"+(i+1)+"}(f_ttValues{"+(i+1)+"}>-1),f_ttValues{"+(i+1)+"}(f_ttValues{"+(i+1)+"}>-1));");


				}
				out.println("hold off");
			}
			/*out.println("nrTimes = "+results.size() + ";");
			out.println("figure;hold on");

			for (int j=0; j< nrObservations; j++ ) {
				out.println("subplot("+nrObservations+",1,"+(j+1)+")");
				out.println("plot(time(1:nrTimes),y(:,"+(j+1)+"),time(1:nrTimes),obs(:,"+(j+1)+"),time(1:nrTimes),ya(:,"+(j+1)+"))");
			}
			out.println("hold off");*/
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void  exportToMatlab(ArrayList<Matrix[]> results, String filename, boolean plot) {
		PrintWriter out;
		if (filename.isEmpty())
			filename = "testEKF2";
		try {
			out = new PrintWriter(filename+".m");
			out.println("clear;");
			for (int k = 0; k< results.size()-1; k++) {
				Matrix[] m = results.get(k);

				out.println("%k=" + k);
				out.println("time("+(k+1)+",:)=" + Arrays.toString(m[8].getArray()[0]) + ";");
				//return new Matrix[]{xmin,y,F,Pnew,observations,x,P};
				out.println("xmin("+(k+1)+",:)="+Arrays.toString(m[0].transpose().getArray()[0])+";");
				out.println("y("+(k+1)+",:)="+Arrays.toString(m[1].transpose().getArray()[0])+";");
				out.println("x("+(k+1)+",:)="+Arrays.toString(m[5].transpose().getArray()[0])+";");
				out.println("obs("+(k+1)+",:)="+Arrays.toString(m[4].transpose().getArray()[0])+";");
				out.println("ya("+(k+1)+",:)="+Arrays.toString(m[7].transpose().getArray()[0])+";");

				/*for (int i = 0; i<m[0].getRowDimension(); i++) {
					out.println("xmin["+k+1+"]("+(i+1)+",:)="+Arrays.toString(m[0].getArray()[i]));
				}*/
				//m[0].pr;
			}
			Matrix m = results.get(results.size()-1)[0];
			int nrRows = m.getRowDimension();
			out.println("%TTs");
			for (int k = 0; k<nrRows/2; k++) {
				out.println("ttTime("+(k+1)+",:)=" + Arrays.toString(m.getArray()[k*2]) + ";");
				out.println("ttValues("+(k+1)+",:)=" + Arrays.toString(m.getArray()[k*2+1]) + ";");

			}
			out.println("nrTimes = "+(results.size()-1) + ";");
			if (plot) {

				out.println("figure;hold on");

				for (int j=0; j< nrObservations; j++ ) {
					out.println("subplot("+nrObservations+",1,"+(j+1)+")");
					out.println("plot(time(1:nrTimes),y(:,"+(j+1)+"),time(1:nrTimes),obs(:,"+(j+1)+"),time(1:nrTimes),ya(:,"+(j+1)+"))");
				}
				out.println("hold off");
			}
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void exportTTToMatlab(Matrix m, String filename, boolean plot) {
		PrintWriter out;
		if (filename.isEmpty())
			filename = "TT";
		try {
			out = new PrintWriter(filename+".m");
			int nrRows = m.getRowDimension();
			out.println("%TTs");
			for (int k = 0; k<nrRows/2; k++) {
				out.println("ttTimeTruth("+(k+1)+",:)=" + Arrays.toString(m.getArray()[k*2]) + ";");
				out.println("ttValuesTruth("+(k+1)+",:)=" + Arrays.toString(m.getArray()[k*2+1]) + ";");

			}
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
