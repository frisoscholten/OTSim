package nl.tudelft.otsim.Simulators.MacroSimulator.TestCases;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

import Jama.Matrix;
import nl.tudelft.otsim.Events.Scheduler;
import nl.tudelft.otsim.GUI.FakeGraphicsPanel;
import nl.tudelft.otsim.GUI.Main;
import nl.tudelft.otsim.Simulators.MacroSimulator.MacroCell;
import nl.tudelft.otsim.Simulators.MacroSimulator.MacroSimulator;
import nl.tudelft.otsim.Simulators.MacroSimulator.Model;
import nl.tudelft.otsim.Simulators.MacroSimulator.Nodes.Node;
import nl.tudelft.otsim.Simulators.MacroSimulator.Nodes.NodeDetector;
import nl.tudelft.otsim.Simulators.MacroSimulator.Nodes.NodeInteriorTampere;

public class TestEKF2 {

	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		double inflowTruth = (2500.0);
		String network = "EndTime:\t7200.00\nSeed:\t1\n"
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
				+ "Detector:	8	(3750.000,-0.250,0.000)\n";
		
		String[] networksplit = network.split("\n");
		String networkAfterSplit1 = networksplit[0]+"\n"+networksplit[1]+"\n";
		String networkAfterSplit2 = networksplit[0]+"\n"+networksplit[1]+"\n";
		for (int i = 2; i<networksplit.length; i++) {
			networkAfterSplit1 += networksplit[i].concat("	fd	0.02	0.125	SMULDERS\n");
			networkAfterSplit2 += networksplit[i].concat("	fd	0.020	0.125	SMULDERS\n");
		}
		double inflow2 = (2700.0);
		//String pattern1 = "[0.000/"+inflowTruth+":1500.000/"+inflowTruth+":2100/"+inflowTruth/2+":3600/"+inflowTruth/2+"]";
		String pattern1 = "[0.000/"+inflowTruth/1.5+":1800/"+inflowTruth+":2400.000/"+inflowTruth+":3900/"+inflowTruth/2+":4200/"+inflowTruth/2+"]";

		
		//String pattern2 = "[0.000/"+inflow2+":1500.000/"+inflow2+":2100/"+inflow2/2+":3600/"+inflow2/2+"]";
		
		//String pattern2 = "[0.000/"+inflow2+":1200.000/"+inflow2+":2300/"+inflow2/2.1+":3600/"+inflow2/2+"]";
		//String pattern2 = "[0.000/"+inflow2/1.5+":1800/"+inflow2+":2400.000/"+inflow2+":3900/"+inflow2/2+":4200/"+inflow2/2+"]";

		String pattern2 = "[0.000/"+inflow2+"]";

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
		
		Matrix obs1 = obsTest[0];
		
		Scheduler scheduler = new Scheduler(MacroSimulator.simulatorType, new FakeGraphicsPanel(), otsimConfiguration2);
		//Scheduler scheduler2 = new Scheduler(MacroSimulator.simulatorType, new FakeGraphicsPanel(), otsimConfiguration);
		// Do something with the scheduler
		System.out.println(scheduler.nextDue());
		Model macromodel = (Model) scheduler.getSimulator().getModel();
		
		//Model macromodel2 = (Model) scheduler2.getSimulator().getModel();
		macromodel.init();
		System.out.println(scheduler.nextDue());
		//scheduler.deQueueEvent();
		//System.out.println(scheduler.nextDue());
		
		
		
		
		TestEKF test = new TestEKF();
		
		test.macromodel = macromodel;
		test.init(1);
	
		Matrix[] speeds = new Matrix[nrSteps];
		Matrix[] flows = new Matrix[nrSteps];
		//Matrix obs = new Matrix(nrSteps,test.locDet.size(),1);
		Matrix obs2 = new Matrix(new double[][]{{33.333333333333336,33.333333333333336,33.333333333333336,33.333333333333336,33.333333333333336},{30.275585131726157,30.354849715098805,31.00954386321669,32.18974973383772,33.02706095829538},{30.27494301594429,30.274944705634564,30.275308301864317,30.288370739948956,30.412408985018793},{30.274943015462124,30.27494301546511,30.274943020262622,30.274944317632407,30.27503812783224},{30.274943015462124,30.274943015462135,30.274943015462163,30.274943015477092,30.274943020026402},{30.274943015462124,30.274943015462135,30.274943015462163,30.27494301546218,30.274943015462224},{30.274943015462124,30.274943015462135,30.274943015462163,30.27494301546218,30.274943015462195},{30.274943015462124,30.274943015462135,30.274943015462163,30.27494301546218,30.274943015462195},{30.274943015462124,30.274943015462135,30.274943015462163,30.27494301546218,30.274943015462195},{30.274943015462124,30.274943015462135,30.274943015462163,30.27494301546218,30.274943015462195},{30.274943015462124,30.274943015462135,30.274943015462163,30.27494301546218,23.632584828561647},{30.274943015462124,30.274943015462135,30.274943015462163,30.27494301546218,5.567776557809155},{30.274943015462124,30.274943015462135,30.274943015462163,30.27494301546218,3.921611830635727},{30.274943015462124,30.274943015462135,30.274943015462163,30.27494301546218,3.864502085419644},{30.274943015462124,30.274943015462135,30.274943015462163,30.27494301546218,3.862168982859144},{30.274943015462124,30.274943015462135,30.274943015462163,30.27494301546218,3.862073077892338},{30.274943015462124,30.274943015462135,30.274943015462163,30.27494301546218,3.8620691346059934},{30.274943015462124,30.274943015462135,30.274943015462163,30.27494301546218,3.8620689724698454},{30.274943015462124,30.274943015462135,30.274943015462163,11.765756327011967,3.862068965800335},{30.274943015462124,30.274943015462135,30.274943015462163,4.232572416905524,3.8620689655291924},{30.274943015462124,30.274943015462135,30.274943015462163,3.8767362950566433,3.862068965518112},{30.274943015462124,30.274943015462135,30.274943015462163,3.8626775621091216,3.8620689655177287},{30.274943015462124,30.274943015462135,30.274943015462163,3.8620939876277496,3.8620689655177287},{30.274943015462124,30.274943015462135,30.274943015462163,3.862069994350461,3.8620689655177287},{30.274943015462124,30.274943015462135,30.274943015462163,3.862069007820355,3.8620689655177287},{30.274943015462124,30.274943015462135,28.30230674506453,3.86206896725722,3.8620689655177287},{30.274943015462124,30.274943015462135,6.581158374228063,3.8620689655894482,3.8620689655177287},{30.274943015462124,30.274943015462135,3.9530152701822767,3.862068965520939,3.8620689655177287},{30.274943015462124,30.274943015462135,3.86577327888747,3.8620689655181724,3.8620689655177287},{30.274943015462124,30.274943015462135,3.8622212171597194,3.862068965518135,3.8620689655177287}});
		//double[][] arr2 = {{33.333333333333336,33.333333333333336,33.333333333333336,33.333333333333336,33.333333333333336},{30.275585131726157,30.354849715098805,31.00954386321669,32.18974973383772,33.02706095829538},{30.27494301594429,30.274944705634564,30.275308301864317,30.288370739948956,30.412408985018793},{30.274943015462124,30.27494301546511,30.274943020262622,30.274944317632407,30.27503812783224},{30.274943015462124,30.274943015462135,30.274943015462163,30.274943015477092,30.274943020026402},{30.274943015462124,30.274943015462135,30.274943015462163,30.27494301546218,30.274943015462224},{30.274943015462124,30.274943015462135,30.274943015462163,30.27494301546218,30.274943015462195},{30.274943015462124,30.274943015462135,30.274943015462163,30.27494301546218,30.274943015462195},{30.274943015462124,30.274943015462135,30.274943015462163,30.27494301546218,30.274943015462195},{30.274943015462124,30.274943015462135,30.274943015462163,30.27494301546218,30.274943015462195},{30.274943015462124,30.274943015462135,30.274943015462163,30.27494301546218,23.632584828561647},{30.274943015462124,30.274943015462135,30.274943015462163,30.27494301546218,5.567776557809155},{30.274943015462124,30.274943015462135,30.274943015462163,30.27494301546218,3.921611830635727},{30.274943015462124,30.274943015462135,30.274943015462163,30.27494301546218,3.864502085419644},{30.274943015462124,30.274943015462135,30.274943015462163,30.27494301546218,3.862168982859144},{30.274943015462124,30.274943015462135,30.274943015462163,30.27494301546218,3.862073077892338},{30.274943015462124,30.274943015462135,30.274943015462163,30.27494301546218,3.8620691346059934},{30.274943015462124,30.274943015462135,30.274943015462163,30.27494301546218,3.8620689724698454},{30.274943015462124,30.274943015462135,30.274943015462163,11.765756327011967,3.862068965800335},{30.274943015462124,30.274943015462135,30.274943015462163,4.232572416905524,3.8620689655291924},{30.274943015462124,30.274943015462135,30.274943015462163,3.8767362950566433,3.862068965518112},{30.274943015462124,30.274943015462135,30.274943015462163,3.8626775621091216,3.8620689655177287},{30.274943015462124,30.274943015462135,30.274943015462163,3.8620939876277496,3.8620689655177287},{30.274943015462124,30.274943015462135,30.274943015462163,3.862069994350461,3.8620689655177287},{30.274943015462124,30.274943015462135,30.274943015462163,3.862069007820355,3.8620689655177287},{30.274943015462124,30.274943015462135,28.30230674506453,3.86206896725722,3.8620689655177287},{30.274943015462124,30.274943015462135,6.581158374228063,3.8620689655894482,3.8620689655177287},{30.274943015462124,30.274943015462135,3.9530152701822767,3.862068965520939,3.8620689655177287},{30.274943015462124,30.274943015462135,3.86577327888747,3.8620689655181724,3.8620689655177287},{30.274943015462124,30.274943015462135,3.8622212171597194,3.862068965518135,3.8620689655177287}};
		
		/*
		[33.333333333333336, 33.333333333333336, 33.333333333333336, 33.333333333333336, 33.333333333333336]
				[30.275585131726157, 30.354849715098805, 31.00954386321669, 32.18974973383772, 33.02706095829538]
				[30.27494301594429, 30.274944705634564, 30.275308301864317, 30.288370739948956, 30.412408985018793]
				[30.274943015462124, 30.27494301546511, 30.274943020262622, 30.274944317632407, 30.27503812783224]
				[30.274943015462124, 30.274943015462135, 30.274943015462163, 30.274943015477092, 30.274943020026402]
				[30.274943015462124, 30.274943015462135, 30.274943015462163, 30.27494301546218, 30.274943015462224]
				[30.274943015462124, 30.274943015462135, 30.274943015462163, 30.27494301546218, 30.274943015462195]
				[30.274943015462124, 30.274943015462135, 30.274943015462163, 30.27494301546218, 30.274943015462195]
				[30.274943015462124, 30.274943015462135, 30.274943015462163, 30.27494301546218, 30.274943015462195]
				[30.274943015462124, 30.274943015462135, 30.274943015462163, 30.27494301546218, 30.274943015462195]
				[30.274943015462124, 30.274943015462135, 30.274943015462163, 30.27494301546218, 23.632584828561647]
				[30.274943015462124, 30.274943015462135, 30.274943015462163, 30.27494301546218, 5.567776557809155]
				[30.274943015462124, 30.274943015462135, 30.274943015462163, 30.27494301546218, 3.921611830635727]
				[30.274943015462124, 30.274943015462135, 30.274943015462163, 30.27494301546218, 3.864502085419644]
				[30.274943015462124, 30.274943015462135, 30.274943015462163, 30.27494301546218, 3.862168982859144]
				[30.274943015462124, 30.274943015462135, 30.274943015462163, 30.27494301546218, 3.862073077892338]
				[30.274943015462124, 30.274943015462135, 30.274943015462163, 30.27494301546218, 3.8620691346059934]
				[30.274943015462124, 30.274943015462135, 30.274943015462163, 30.27494301546218, 3.8620689724698454]
				[30.274943015462124, 30.274943015462135, 30.274943015462163, 11.765756327011967, 3.862068965800335]
				[30.274943015462124, 30.274943015462135, 30.274943015462163, 4.232572416905524, 3.8620689655291924]
				[30.274943015462124, 30.274943015462135, 30.274943015462163, 3.8767362950566433, 3.862068965518112]
				[30.274943015462124, 30.274943015462135, 30.274943015462163, 3.8626775621091216, 3.8620689655177287]
				[30.274943015462124, 30.274943015462135, 30.274943015462163, 3.8620939876277496, 3.8620689655177287]
				[30.274943015462124, 30.274943015462135, 30.274943015462163, 3.862069994350461, 3.8620689655177287]
				[30.274943015462124, 30.274943015462135, 30.274943015462163, 3.862069007820355, 3.8620689655177287]
				[30.274943015462124, 30.274943015462135, 28.30230674506453, 3.86206896725722, 3.8620689655177287]
				[30.274943015462124, 30.274943015462135, 6.581158374228063, 3.8620689655894482, 3.8620689655177287]
				[30.274943015462124, 30.274943015462135, 3.9530152701822767, 3.862068965520939, 3.8620689655177287]
				[30.274943015462124, 30.274943015462135, 3.86577327888747, 3.8620689655181724, 3.8620689655177287]
				[30.274943015462124, 30.274943015462135, 3.8622212171597194, 3.862068965518135, 3.8620689655177287]
*/		
		double[] stdArray = new double[test.nrObservations];
		Arrays.fill(stdArray, 0, test.nrSpeedObservations, 2.5); //2.5
		Arrays.fill(stdArray, test.nrSpeedObservations, test.nrObservations, 0.06); //0.06
		Matrix obsNoised = test.generateWhiteNoise(obs1, stdArray);
		ArrayList<Matrix[]> results = new ArrayList<Matrix[]>();
		ArrayList<ArrayList<Matrix[]>> forecasts = new ArrayList<ArrayList<Matrix[]>>();
		for (NodeDetector nd: macromodel.getDetectors()) {
			nd.addMeasurements(0);
		}
		for (int i = 0; i < nrSteps; i++) {
			scheduler.stepUpTo(i*60.0);
			double time = scheduler.getSimulatedTime();
			
			if ((i % 5) == 0) {
				ArrayList<Matrix[]> forecast = TestEKF2.generateForecastValues(scheduler, 5, 60*5.0);
				forecasts.add(forecast);
			}
			
			//Matrix y = new Matrix(test.locDet.size(),1);
			Matrix[] output = test.update((obsNoised.getMatrix(i,i,0,test.nrObservations-1)).transpose());
			output[output.length-1].set(0, 0, time); 
			results.add(output);
			speeds[i] = output[1].copy();
			//System.out.println(output);
			//System.out.println(output[0].getArray());
		}
		ArrayList<ArrayList<ArrayList<Double>>> tt = test.macromodel.getRoutes().getTravelTimes();
		Matrix tts = new Matrix(tt.size()*2,tt.get(0).get(0).size());
		for (int i = 0; i<tt.size(); i++) {
			ArrayList<ArrayList<Double>> route = tt.get(i);
			
			for (int j = 0; j<route.get(0).size(); j++) {
				tts.set(2*i, j, route.get(0).get(j));
				tts.set(2*i+1, j, route.get(1).get(j));
				
			}
		}
		results.add(new Matrix[]{tts});
		
		for (int i = 0; i < nrSteps; i++) {
			
			double[][] arr = speeds[i].transpose().getArrayCopy();
			System.out.println(Arrays.toString(arr[0]));
		}
		test.exportToMatlab(results, "", false);
		test.exportTTToMatlab(obsTest[1], "", false);
		for (ArrayList<Matrix[]> fc: forecasts){
			test.exportToMatlab(fc, "forecast" + forecasts.indexOf(fc), false);
		}
		test.exportForecastsToMatlab(forecasts, "forecasts",true);
		System.out.println("einde");
		
		
		

		
		
	}
	public TestEKF2() {
		
	}
	public static Matrix[] generateTruthData(String configuration, int nrSteps, double timestep) {
		Scheduler tmpScheduler = new Scheduler(MacroSimulator.simulatorType, new FakeGraphicsPanel(), configuration);
		Model tmpMacromodel = (Model) tmpScheduler.getSimulator().getModel();
		tmpMacromodel.init();
		
		return generateTruthObservations(tmpScheduler, tmpMacromodel, nrSteps, timestep, 0);
			
	}
	public static Matrix[] generateTruthObservations(Scheduler tmpScheduler, Model tmpMacromodel, int nrSteps, double timestep, double timeoffset) {
		TestEKF ekf = new TestEKF();
		
		ekf.macromodel = tmpMacromodel;
		ekf.init(0);
		ArrayList<Matrix[]> results = new ArrayList<Matrix[]>();
		Matrix[] out = new Matrix[nrSteps];
		Matrix[] out2 = new Matrix[nrSteps];
		Matrix[] out3 = new Matrix[nrSteps];
		Matrix[] out4 = new Matrix[nrSteps];
		for (NodeDetector nd: ekf.macromodel.getDetectors()) {
			nd.addMeasurements(0);
		}
		for (int i = 0; i < nrSteps; i++) {
			tmpScheduler.stepUpTo(i*timestep);
			//Matrix y = new Matrix(test.locDet.size(),1);
			Matrix[] output = ekf.update(null);
			results.add(output);
			out2[i]=output[0].copy(); // state values
			out[i] = output[1].copy(); // detectorvalues
			out3[i] = output[8].copy(); // speed values at cells
			out4[i] = output[9].copy(); // traffic regime
			//System.out.println(output);
			//System.out.println(output[0].getArray());
		}
		ArrayList<ArrayList<ArrayList<Double>>> tt = ekf.macromodel.getRoutes().getTravelTimes();
		Matrix tts = new Matrix(tt.size()*2,tt.get(0).get(0).size());
		for (int i = 0; i<tt.size(); i++) {
			ArrayList<ArrayList<Double>> route = tt.get(i);
			
			for (int j = 0; j<route.get(0).size(); j++) {
				tts.set(2*i, j, route.get(0).get(j));
				tts.set(2*i+1, j, route.get(1).get(j));
				
			}
		}
		
		
		
		int nrDet = out[0].getRowDimension();
		int nrDet2 = out2[0].getRowDimension();
		int nrDet3 = out3[0].getRowDimension();
		int nrDet4 = out4[0].getRowDimension();
		Matrix o = new Matrix(nrSteps, nrDet);
		Matrix o2 = new Matrix(nrSteps, nrDet2);
		Matrix o3 = new Matrix(nrSteps, nrDet3);
		Matrix o4 = new Matrix(nrSteps, nrDet4);
		
		for (int j = 0; j<nrSteps; j++) {
			Matrix c = out[j].transpose();
			Matrix c2 = out2[j].transpose();
			Matrix c3 = out3[j].transpose();
			Matrix c4 = out4[j].transpose();
			o.setMatrix(j, j, 0,  nrDet-1, c);
			o2.setMatrix(j, j, 0,  nrDet2-1, c2);
			o3.setMatrix(j, j, 0,  nrDet3-1, c3);
			o4.setMatrix(j, j, 0,  nrDet4-1, c4);
		}
		
		
		return new Matrix[]{o,tts,o3,o2,o4};
	}
	public static ArrayList<Matrix[]> generateTruthData(Scheduler tmpScheduler, Model tmpMacromodel, int nrSteps, double timestep, double timeoffset) {
			
		TestEKF ekf = new TestEKF();
		
		ekf.macromodel = tmpMacromodel;
		ekf.macromodel.getRoutes().setTimeOffset(timeoffset);
		ekf.init(0);
		ArrayList<Matrix[]> results = new ArrayList<Matrix[]>();
		Matrix[] out = new Matrix[nrSteps];
		for (NodeDetector nd: ekf.macromodel.getDetectors()) {
			nd.addMeasurements(0);
		}
		for (int i = 0; i < nrSteps; i++) {
			tmpScheduler.stepUpTo(i*timestep);
			
			//Matrix y = new Matrix(test.locDet.size(),1);
			Matrix[] output = ekf.update(null);
			output[output.length-1].set(0, 0, i*timestep+timeoffset); 
			results.add(output);
			
			
			//System.out.println(output);
			//System.out.println(output[0].getArray());
		}
		//ArrayList<ArrayList<ArrayList<Double>>> tt = ekf.macromodel.getRoutes().getTravelTimes();
		//ArrayList<ArrayList<ArrayList<Double>>> tt = new ArrayList<ArrayList<ArrayList<Double>>>();
		//Matrix tts = new Matrix(tt.size()*2,tt.get(0).get(0).size());
		/*for (int i = 0; i<tt.size(); i++) {
			ArrayList<ArrayList<Double>> route = tt.get(i);
			
			for (int j = 0; j<route.get(0).size(); j++) {
				tts.set(2*i, j, route.get(0).get(j));
				tts.set(2*i+1, j, route.get(1).get(j));
				
			}
		}*/
		//results.add(new Matrix[]{tts});
		results.add(new Matrix[1]);
		return results;
	
	}
	public static ArrayList<Matrix[]>  generateForecastValues(Scheduler scheduler, int nrSteps, double timestep) {
		String config = "Offset:	-"+scheduler.getSimulatedTime()+"\n"+scheduler.getConfiguration();
		Scheduler sch = new Scheduler(MacroSimulator.simulatorType, new FakeGraphicsPanel(),  config);
		Model modelScheduler = (Model) scheduler.getSimulator().getModel();
		Model modelSch = (Model) sch.getSimulator().getModel();
		modelSch.init();
		double[] density = modelScheduler.saveStateToArray("density");
		double[] inflow = modelScheduler.saveStateToArray("inflow");
		double[] speedLimit = modelScheduler.saveStateToArray("speedLimit");
		double[] criticalDensity = modelScheduler.saveStateToArray("criticalDensity");
		double[] jamDensity = modelScheduler.saveStateToArray("jamDensity");
		//System.out.println("densities: "+Arrays.toString(density));
		//System.out.println("inflows: "+Arrays.toString(inflow));
		modelSch.restoreState(density, "density");
		modelSch.restoreState(inflow, "inflow");
		modelSch.restoreState(speedLimit, "speedLimit");
		modelSch.restoreState(criticalDensity, "criticalDensity");
		modelSch.restoreState(jamDensity, "jamDensity");
		for (NodeDetector nd: modelSch.getDetectors()) {
			nd.addMeasurements(0);
		}
				 
		
		
		
		return generateTruthData(sch, modelSch, nrSteps, timestep, scheduler.getSimulatedTime());
		
	}
}