package nl.tudelft.otsim.Simulators.MacroSimulator.TestCases;


import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.TreeMap;
import java.util.TreeSet;

import nl.tudelft.otsim.Events.Scheduler;
import nl.tudelft.otsim.GUI.FakeGraphicsPanel;
import nl.tudelft.otsim.GUI.Main;
import nl.tudelft.otsim.GUI.StandAlone;
import nl.tudelft.otsim.GeoObjects.Vertex;
import nl.tudelft.otsim.Simulators.MacroSimulator.MacroCell;
import nl.tudelft.otsim.Simulators.MacroSimulator.Model;
import nl.tudelft.otsim.Simulators.MacroSimulator.MacroSimulator;
import nl.tudelft.otsim.Simulators.MacroSimulator.Nodes.NodeDetector;

public class DeleteLinksWithoutMerge {
	ArrayList<Integer> cellId = new ArrayList<Integer>();
	ArrayList<Integer> nodeIn = new ArrayList<Integer>();
	ArrayList<Integer> nodeOut = new ArrayList<Integer>();
	ArrayList<Integer> speedlimit = new ArrayList<Integer>();
	ArrayList<Integer> lanes = new ArrayList<Integer>();
	//ArrayList<Integer> lanes = new ArrayList<Integer>();
	ArrayList<ArrayList<Integer>> cellsIn = new ArrayList<ArrayList<Integer>>();
	ArrayList<ArrayList<Integer>> cellsOut = new ArrayList<ArrayList<Integer>>();
	ArrayList<ArrayList<String>> vertices = new ArrayList<ArrayList<String>>();
	ArrayList<ArrayList<Vertex>> vertices2 = new ArrayList<ArrayList<Vertex>>();
	static int nodeNr = 1;
	static int cellNr = 1;

	public static void main(String[] args) {
		DeleteLinksWithoutMerge del = new DeleteLinksWithoutMerge();
	}
	public DeleteLinksWithoutMerge() {
		// TODO Auto-generated method stub
		String folder = "C:\\Users\\Friso\\Documents\\Thesis";
		String input = "Netwerk_Rotterdam_metdetectors.txt";
		String output = "Netwerk_Rotterdam.txt";
		Path path = FileSystems.getDefault().getPath(folder, input);

		try {
			String fileContent = new String(Files.readAllBytes(path), "UTF-8");
			ArrayList<String> detectors = new ArrayList<String>();
			for (String line : fileContent.split("\n")) {
				line = line.trim();
				String[] fields = line.split("\t");
				if (fields[0].equals("")) 
					continue;
				if (fields[0].equals("Detector:")) {
					detectors.add(line);
					continue;
				}
				cellId.add(Integer.parseInt(fields[1]));
				nodeIn.add(Integer.parseInt(fields[3]));
				nodeOut.add(Integer.parseInt(fields[5]));
				speedlimit.add(Integer.parseInt(fields[7]));
				lanes.add(Integer.parseInt(fields[9]));

				ArrayList<String> cellVertices = new ArrayList<String>();
				ArrayList<Vertex> vVertices = new ArrayList<Vertex>();
				ArrayList<Integer> cIn = new ArrayList<Integer>();
				ArrayList<Integer> cOut = new ArrayList<Integer>();
				for (int i = 10; i < fields.length; i++) {
					if (fields[i].equals("vertices")) {
						// add all incoming links to MacroCell
						while (!fields[++i].startsWith("ins")) {
							if (!fields[i].equals("")) {
								cellVertices.add(fields[i]);
								vVertices.add(new Vertex(fields[i]));
							}
						}
						// decrease i to start check the right field in the following loop
						--i;
					}
					if (fields[i].equals("ins")) {
						// add all incoming links to MacroCell
						while (!fields[++i].startsWith("outs")) {
							cIn.add(Integer.valueOf(fields[i]));
						}
						// decrease i to start check the right field in the following loop
						--i;
					} else if (fields[i].equals("outs")) {
						// add all outgoing links to MacroCell
						while (++i <= fields.length-1) {
							cOut.add(Integer.valueOf(fields[i]));
						}
					}
				}
				cellsIn.add(cIn);
				cellsOut.add(cOut);
				vertices.add(cellVertices);
				vertices2.add(vVertices);
			}
			//int linkToBeDeleted = 69626;
			this.deleteLink(21462);
			this.deleteLink(35154);
			this.deleteLast(79527,0.1);
			this.deleteFirst(49091,0.1);
			this.deleteLink(81859);
			this.deleteLink(75017);
			this.deleteLink(74453);
			this.deleteFirst(1401,0.01);
			this.deleteLast(50646,0.72);
			this.deleteLink(12144);



			this.disconnect(55805, 73196, 0.05);

			this.deleteLink(78872);
			this.deleteLink(73597);
			this.deleteLink(69837);
			this.deleteLink(63845);
			this.deleteLink(61061);
			this.deleteLink(57376);
			this.deleteLink(16043);

			this.disconnect(66214,36945,0.05);
			this.disconnect(57620,17638,0.05);
			this.disconnect(35706,39834,0.05);


			this.deleteFirst(6038,0.1);
			this.addLink(85306, 6038,true);

			this.disconnect(30915, 66187, 0.1);
			this.disconnect(30915, 17189, 0.1);
			//this.deleteLast(30915,0.15);
			this.addLink(30915, 66187,false);
			this.addLink(30915, 17189,false);

			this.disconnect(41193, 22307, 0.01);
			this.disconnect(1633, 22307, 0.01);
			this.deleteLast(1633,0.1);
			//this.deleteLast(30915,0.15);
			this.addLink(41193, 22307,true);
			this.addLink(1633, 22307,true);


			this.disconnect(13788, 32030, 0.2);
			this.addLink(13788, 32030,true);
			ArrayList<Integer> links = new ArrayList<Integer>();

			ArrayList<Integer> onandofframps = new ArrayList<Integer>();
			onandofframps.add(11033);
			onandofframps.add(131494);
			onandofframps.add(131495);
			onandofframps.add(1367);
			onandofframps.add(13788);
			onandofframps.add(13868);
			onandofframps.add(14156);
			onandofframps.add(1441);
			onandofframps.add(16155);
			onandofframps.add(16233);
			onandofframps.add(1633);
			onandofframps.add(16679);
			onandofframps.add(17069);
			onandofframps.add(17638);
			onandofframps.add(18401);
			onandofframps.add(2057);
			onandofframps.add(21086);
			onandofframps.add(21211);
			onandofframps.add(21211);
			onandofframps.add(22121);
			onandofframps.add(24362);
			onandofframps.add(24453);
			onandofframps.add(24620);
			onandofframps.add(27840);
			onandofframps.add(29197);
			onandofframps.add(30112);
			onandofframps.add(30563);
			onandofframps.add(30563);
			onandofframps.add(3180);
			onandofframps.add(32030);
			onandofframps.add(32842);
			onandofframps.add(33119);
			onandofframps.add(3373);
			onandofframps.add(35706);
			onandofframps.add(35883);
			onandofframps.add(36369);
			onandofframps.add(36505);
			onandofframps.add(36630);
			onandofframps.add(36834);
			onandofframps.add(36945);
			onandofframps.add(37272);
			onandofframps.add(37328);
			onandofframps.add(3733);
			onandofframps.add(38034);
			onandofframps.add(39470);
			onandofframps.add(39834);
			onandofframps.add(40104);
			onandofframps.add(40502);
			onandofframps.add(42078);
			onandofframps.add(43090);
			onandofframps.add(43287);
			onandofframps.add(44421);
			onandofframps.add(45667);
			onandofframps.add(47092);
			onandofframps.add(47635);
			onandofframps.add(4854);
			onandofframps.add(48889);
			onandofframps.add(49091);
			onandofframps.add(50475);
			onandofframps.add(51882);
			onandofframps.add(51910);
			onandofframps.add(52291);
			onandofframps.add(52830);
			onandofframps.add(53583);
			onandofframps.add(53781);
			onandofframps.add(53916);
			onandofframps.add(54014);
			onandofframps.add(54180);
			onandofframps.add(55212);
			onandofframps.add(55805);
			onandofframps.add(56209);
			onandofframps.add(56248);
			onandofframps.add(56982);
			onandofframps.add(57327);
			onandofframps.add(57620);
			onandofframps.add(57784);
			onandofframps.add(58049);
			onandofframps.add(59292);
			onandofframps.add(59440);
			onandofframps.add(60150);
			onandofframps.add(61348);
			onandofframps.add(61737);
			onandofframps.add(61906);
			onandofframps.add(63327);
			onandofframps.add(64623);
			onandofframps.add(65180);
			onandofframps.add(66176);
			onandofframps.add(66214);
			onandofframps.add(66590);
			onandofframps.add(67701);
			onandofframps.add(6779);
			onandofframps.add(68358);
			onandofframps.add(69468);
			onandofframps.add(6997);
			onandofframps.add(70567);
			onandofframps.add(7074);
			onandofframps.add(71441);
			onandofframps.add(71517);
			onandofframps.add(72063);
			onandofframps.add(73196);
			onandofframps.add(73287);
			onandofframps.add(74173);
			onandofframps.add(74255);
			onandofframps.add(74808);
			onandofframps.add(75437);
			onandofframps.add(77923);
			onandofframps.add(79527);
			onandofframps.add(80533);
			onandofframps.add(81098);
			onandofframps.add(81104);
			onandofframps.add(8116);
			onandofframps.add(81968);
			onandofframps.add(82189);
			onandofframps.add(82261);
			onandofframps.add(838);
			onandofframps.add(84707);
			onandofframps.add(84770);
			onandofframps.add(85080);
			onandofframps.add(86094);
			onandofframps.add(87191);
			onandofframps.add(87495);
			onandofframps.add(88039);
			onandofframps.add(88107);
			onandofframps.add(89068);
			onandofframps.add(90087);
			onandofframps.add(90087);
			onandofframps.add(91870);
			onandofframps.add(91888);
			onandofframps.add(91930);
			onandofframps.add(9470);
			onandofframps.add(9839);


			for (ArrayList<Integer> c3: cellsOut) {
				if (c3.size() > 2) {
					System.out.println("cellsOut too large: " + cellId.get(cellsOut.indexOf(c3)) + " op " + vertices.get(cellsOut.indexOf(c3)).get(0));
				}
			}
			for (ArrayList<Integer> c3: cellsIn) {
				if (c3.size() > 2) {
					System.out.println("cellsIn too large: " + cellId.get(cellsIn.indexOf(c3)) + " op " + vertices.get(cellsIn.indexOf(c3)).get(0));
				}
			}
			for (Integer c3: nodeIn) {
				if (c3<=0) {
					System.out.println("NodeIn negative: " + c3 + " op cell "+cellId.get(nodeIn.indexOf(c3)) + vertices.get(nodeIn.indexOf(c3)).get(vertices.get(nodeIn.indexOf(c3)).size()-1));
				}
			}
			for (Integer c3: nodeOut) {
				if (c3<=0) {
					System.out.println("NodeOut negative: " + c3 + " op cell "+cellId.get(nodeIn.indexOf(c3)) + vertices.get(nodeOut.indexOf(c3)).get(0));
				}
			}


			TreeSet<Integer> list2 = new TreeSet<Integer>();
			list2.addAll(nodeIn);
			list2.addAll(nodeOut);

			for (Integer n: list2) {
				ArrayList<Integer> cIn2 = new ArrayList<Integer>();
				for (int i = 0; i<nodeOut.size(); i++) {
					if (nodeOut.get(i)!= null && nodeOut.get(i).equals(n)) {
						//System.out.println(i);
						//System.out.println(nodeOut.get(i));
						cIn2.add(cellId.get(i));
					}
				}
				ArrayList<Integer> cOut2 = new ArrayList<Integer>();
				for (int m = 0; m<nodeIn.size(); m++) {
					//System.out.println(nodeIn.get(m));
					if (nodeIn.get(m) != null) {
						if (nodeIn.get(m).equals(n)) {
							//System.out.println(m);
							//System.out.println(nodeIn.get(m));
							cOut2.add(cellId.get(m));
						}
					}
				}
				if (cIn2.size() == 0) {
					//newIncomingLink(n);
				}
				if (cOut2.size() == 0) {
					//newOutgoingLink(n);
				}
			}
			ArrayList<Integer> incomingLinks = new ArrayList<Integer>();
			int highwayIn = 0;
			for (int i = 0; i < cellId.size(); i++) {

				if (cellsIn.get(i).size()==0) {
					incomingLinks.add(i);
					System.out.println("id: " +cellId.get(i) + " & lanes: "+lanes.get(i));
					if (lanes.get(i) >1) 
						highwayIn++;
				}

			}
			System.out.println("highways in:" + highwayIn);
			TreeSet<Integer> list = new TreeSet<Integer>();
			for (Integer n: nodeIn) {
				if (n != null) 
					list.add(n);
			}
			for (Integer n: nodeOut) {
				if (n != null) 
					list.add(n);
			}
			//list.addAll(nodeIn);
			//list.addAll(nodeOut);
			ArrayList<String> tfs = new ArrayList<String>();
			TreeMap<Integer, Double> uitzondering = new TreeMap<Integer, Double>();
			uitzondering.put(67192, 0.2);
			uitzondering.put(49396, 0.55);
			Random r1 = new Random(1337);
			for (int i = 0; i < cellId.size(); i++) {
				if (cellsOut.get(i).size() == 2) {
					double tf;
					double tfofframp = 0.15;
					if (uitzondering.containsKey(cellId.get(i))) {
						tf = uitzondering.get(cellId.get(i));
					} else {
						boolean out0ramp = onandofframps.contains(cellsOut.get(i).get(0));
						boolean out1ramp = onandofframps.contains(cellsOut.get(i).get(1));

						if (out0ramp && !out1ramp)
							tf = tfofframp;
						else if (!out0ramp && out1ramp)
							tf = 1-tfofframp;
						else tf = ((double) lanes.get(cellId.indexOf(cellsOut.get(i).get(0))))/(lanes.get(cellId.indexOf(cellsOut.get(i).get(0))) + lanes.get(cellId.indexOf(cellsOut.get(i).get(1))));


					}
					tf += (r1.nextDouble()-0.5)*0.10;
					tfs.add("Turn:\t" + cellId.get(i) + "\t" + tf);



				}
			}
			ArrayList<String> fds = new ArrayList<String>();
			TreeMap<Integer, Double> uitzonderingkCri = new TreeMap<Integer, Double>();
			uitzonderingkCri.put(8838, 0.03);
			uitzonderingkCri.put(44327, 0.03);
			uitzonderingkCri.put(46105, 0.027);
			TreeMap<Integer, Double> uitzonderingkJam = new TreeMap<Integer, Double>();
			//uitzonderingkJam.put(8838, 0.2);
			TreeMap<Integer, Double> uitzonderingvCri = new TreeMap<Integer, Double>();
			//uitzonderingvCri.put(8838, 0.2);

			Random rfd = new Random(79);
			for (int i = 0; i < cellId.size(); i++) {
				double kCriPerLane = 0.025;
				if (uitzonderingkCri.containsKey(cellId.get(i)))
					kCriPerLane = uitzonderingkCri.get(cellId.get(i));
				double kJamPerLane =0.125;
				if (uitzonderingkJam.containsKey(cellId.get(i)))
					kJamPerLane = uitzonderingkJam.get(cellId.get(i));
				double vCri = Math.min(((double) speedlimit.get(i))/3.6-1, 22.222);
				if (uitzonderingvCri.containsKey(cellId.get(i)))
					vCri = uitzonderingvCri.get(cellId.get(i));


				kCriPerLane += (rfd.nextDouble()-0.5)*0.010;
				kJamPerLane+= (rfd.nextDouble()-0.5)*0.050; ;
				vCri+= (rfd.nextDouble()-0.5)*3;
				fds.add("FD:\t" + cellId.get(i) + "\t" + kCriPerLane + "\t"+ kJamPerLane + "\t"+ vCri + "\tSMULDERS");

			}

			System.out.println("klaar");

			PrintWriter out;
			out = new PrintWriter(folder+ "\\"+output);
			for (int i = 0; i < cellId.size(); i++) {
				//if (!links.contains(new Integer(cellId.get(i)))) {
				String ins = "ins\t";
				String outs = "outs\t";
				String v = "";
				for (Integer j: cellsIn.get(i)) {

					ins += j+"\t";
				}
				for (Integer j: cellsOut.get(i)) {
					outs += j+"\t";
				}
				for (String s: vertices.get(i)) {
					v += s+"\t";
				}
				/*if (cellsOut.get(i).size() == 2) {
					double tf;
					double tfofframp = 0.1;
					boolean out0ramp = onandofframps.contains(cellsOut.get(i).get(0));
					boolean out1ramp = onandofframps.contains(cellsOut.get(i).get(1));
					if (out0ramp && !out1ramp)
						tf = tfofframp;
					else if (!out0ramp && out1ramp)
						tf = 1-tfofframp;
					else tf = ((double) lanes.get(cellId.indexOf(cellsOut.get(i).get(0))))/(lanes.get(cellId.indexOf(cellsOut.get(i).get(0))) + lanes.get(cellId.indexOf(cellsOut.get(i).get(1))));
					outs += "tf\t"+tf;



				}*/
				//ins = ins.trim();
				//outs = outs.trim();
				//v = v.trim();
				String output2 = "Roadway:	"+cellId.get(i)+"	from	"+nodeIn.get(i)+"	to	"+nodeOut.get(i)+"	speedlimit	"+speedlimit.get(i)+"	lanes	"+lanes.get(i)+"	vertices	"+v+ins+outs;
				String[] o2 = output2.split("\t");
				out.println(output2);
				//}
			}
			for (String line: detectors) {
				out.println(line);
			}
			double[] inflowTruth = new double[]{1700};
			Random r = new Random(9);
			for (int i = 0; i < cellId.size(); i++) {
				if (cellsIn.get(i).size() == 0) {
					double inflow = inflowTruth[0]*lanes.get(i);
					inflow += (r.nextDouble()-0.5)*200;
					double[] times = new double[]{600,2100,2400,3900,5100};
					for (int j= 0; j< times.length; j++) 
						times[j] += Math.round((r.nextDouble()-0.5)*300);
					if (onandofframps.contains(cellId.get(i))) {
						inflow = inflow*0.5;
					}
					inflow = Math.round(inflow);
					//out.println("Inflow:\t"+nodeIn.get(i)+"\t[0.000/"+inflowTruth[0]/1.5+":1800/"+inflowTruth[0]+":2400.000/"+inflowTruth[0]+":3900/"+inflowTruth[0]/2+":4200/"+inflowTruth[0]/2+"]");
					out.println("Inflow:\t"+nodeIn.get(i)+"\t[0.000/"+inflow/1.5+":"+times[0]+"/"+inflow/1.5+":"+times[1]+"/"+inflow+":"+times[2]+"/"+inflow+":"+times[3]+"/"+inflow/2+":"+times[4]+"/"+inflow/2+"]");

					//out.println("Inflow:\t"+nodeIn.get(i)+"\t[0.000/"+inflow+":7200/"+inflow+"]");

				}

			}
			for (String line: tfs) {
				out.println(line);
			}
			for (String line: fds) {
				out.println(line);
			}
			out.close();





			String folder2 = "C:\\Users\\Friso\\Documents\\Thesis";
			String input2 = "Netwerk_Rotterdam.txt";
			Path path2 = FileSystems.getDefault().getPath(folder2, input2);
			String fileload2a = new String(Files.readAllBytes(path2), "UTF-8");
			String fileload2 = "EndTime:\t7200.00\nSeed:\t1\nRouteBased:\tfalse\n" + fileload2a;
			String[] files = fileload2.split("\n");

			Scheduler intSched = null;
			//int limit = Integer.MAX_VALUE;
			int limit = 1;
			for (int j = 0; j< limit; j++) {
				if (j== 0) {

					StandAlone.main(new String[]{"GenerateEvent=SelectTab 5", "GenerateEvent=zoomToRect 80000 425000 105000 445000"});
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				intSched = new Scheduler(MacroSimulator.simulatorType, Main.mainFrame.graphicsPanel, fileload2);



				//Scheduler intSched = new Scheduler(MacroSimulator.simulatorType, new FakeGraphicsPanel(), networkAfterSplit1+detectors);
				for (int i = 1; i < 900; i++) {
					//double time = 0;
					intSched.stepUpTo(i*2);
					intSched.getGraphicsPanel().repaint();;
					System.out.println(i*2);
				}
			}
			ArrayList<Integer> route = new ArrayList<Integer>();
			route.add(55904);
			route.add(37680);
			route.add(49396);
			route.add(89790);
			route.add(69227);
			route.add(71251);
			route.add(6292);
			route.add(43449);
			route.add(86969);

			Model macromodel = (Model) intSched.getSimulator().getModel();
			ArrayList<ArrayList<Integer>> possibleRoutes = new ArrayList<ArrayList<Integer>>();
			ArrayList<ArrayList<MacroCell>> possibleRoutesCells = new ArrayList<ArrayList<MacroCell>>();
			//find first incoming cell:
			ArrayList<MacroCell> cells = macromodel.getCells();
			MacroCell firstCell = null;
			for (MacroCell c: cells) {
				if ( route.get(0).intValue() == c.id) {
					firstCell = c;
					System.out.println("FirstCell found!");
				}
			}

			boolean incomingCellFound = false;
			while (!incomingCellFound) {
				if (firstCell.ups.size() >1)
					throw new Error("split");
				if (firstCell.ups.size() == 0) {
					System.out.println("incomingCell found!");
					incomingCellFound = true;
					continue;
				}
				firstCell = firstCell.ups.get(0);

			}

			ArrayList<MacroCell> firstroute = new ArrayList<MacroCell>();
			firstroute.add(firstCell);

			possibleRoutesCells.add(firstroute);
			
			boolean finished = false;
			int index = 0;
			while (!finished) {
				ArrayList<ArrayList<MacroCell>> possibleRoutesCellsCopy =  new ArrayList<ArrayList<MacroCell>>();
				for (ArrayList<MacroCell> rt: possibleRoutesCells) {
					ArrayList<MacroCell> rtclone1 = (ArrayList<MacroCell>) rt.clone();
					MacroCell c = rt.get(rt.size()-1);
					while (c.downs.size() ==1) {
						c = c.downs.get(0);
						rtclone1.add(c);
					}
					if (c.downs.size() >1)  {
						ArrayList<MacroCell> rtclone = (ArrayList<MacroCell>) rtclone1.clone();
						rtclone1.add(c.downs.get(0));
						rtclone.add(c.downs.get(1));
						possibleRoutesCellsCopy.add(rtclone);
						possibleRoutesCellsCopy.add(rtclone1);	

						continue;
					}
					if (c.downs.size() == 0)
						possibleRoutesCellsCopy.add(rtclone1);


				}
				int[] progress = new int[possibleRoutesCellsCopy.size()];
				int i = 0;
				for (ArrayList<MacroCell> rt: possibleRoutesCellsCopy) {
					for (MacroCell c: rt) {
						if (route.contains(Integer.valueOf(c.id))) {
							progress[i]++;
						}
							
					}
					i++;
				}
				int max = 0;
				ArrayList<Integer> indices = new ArrayList<Integer>();
				for (int j = 0; j<progress.length; j++) {
					if (progress[j]>=max)
						max = progress[j];
				}
				for (int j = 0; j<progress.length; j++) {
					if (progress[j]>=max)
						indices.add(j);
				}
				ArrayList<ArrayList<MacroCell>> possibleRoutesCellsCopy2 = new ArrayList<ArrayList<MacroCell>>();
				for (Integer ind: indices) {
					possibleRoutesCellsCopy2.add(possibleRoutesCellsCopy.get(ind.intValue()));
				}
				possibleRoutesCellsCopy = possibleRoutesCellsCopy2;
				finished = true;
				
				for (ArrayList<MacroCell> rt: possibleRoutesCellsCopy) {
					if (rt.get(rt.size()-1).downs.size() != 0) {
						finished = false;
						break;
					}
				}
				possibleRoutesCells = (ArrayList<ArrayList<MacroCell>>) possibleRoutesCellsCopy.clone();
			}
			ArrayList<MacroCell> finalRoute = possibleRoutesCells.get(0);
			LinkedHashSet<MacroCell> hashSet = new LinkedHashSet<MacroCell>(finalRoute);
			ArrayList<NodeDetector> dets = macromodel.getDetectors();
			ArrayList<NodeDetector> listDetectors = new ArrayList<NodeDetector>(dets.size());
			//ArrayList<NodeDetector> listDetectors2 = new ArrayList<NodeDetector>(dets.size());
			HashMap<MacroCell, NodeDetector> map = new HashMap<MacroCell,NodeDetector>();
			for (NodeDetector n: dets) {
				if (finalRoute.contains(n.getClosestCell())) {
					map.put(n.getClosestCell(), n);
				}
			}
			for (MacroCell c: finalRoute) {
				if (c.detector)
					listDetectors.add(map.get(c));
			}
			
			LinkedHashSet<NodeDetector> hashSetDet = new LinkedHashSet<NodeDetector>(listDetectors);
			
			int[] indices = macromodel.getIndices(hashSet);
			for (int i = 0; i< indices.length; i++)
				indices[i] = indices[i]+1;
			
			System.out.println("indices = "+Arrays.toString(indices));

			int[] indices2 = macromodel.getIndicesDetector(hashSetDet);
			for (int i = 0; i< indices2.length; i++)
				indices2[i] = indices2[i]+1;
			
			System.out.println("indicesDet = "+Arrays.toString(indices2));
			ArrayList<MacroCell> listOfCells = (ArrayList<MacroCell>) ((Model) intSched.getSimulator().getModel()).getCells().clone();
			Collections.sort(listOfCells,new Comparator<MacroCell>() {
				public int compare(MacroCell one, MacroCell other) {
					return Double.compare(one.l/one.getVLim(), other.l/other.getVLim());
				}
			}); 

			for (int i = 0; i<10; i++) {
				MacroCell c  =  listOfCells.get(i);

				System.out.println(c.id + ": " + c.l/c.getVLim() + " @ " + c.vertices.get(0).export());
			}
			ArrayList<NodeDetector> listOfDetectors = (ArrayList<NodeDetector>) ((Model) intSched.getSimulator().getModel()).getDetectors().clone();
			Collections.sort(listOfDetectors,new Comparator<NodeDetector>() {
				public int compare(NodeDetector one, NodeDetector other) {
					return Double.compare((one.getDistanceToCell()), (other.getDistanceToCell()));
				}
			}); 

			for (int i = listOfDetectors.size()-1; i>listOfDetectors.size()-11; i--) {
				NodeDetector c  =  listOfDetectors.get(i);

				System.out.println("det "+c.getId() + ": " + c.getDistanceToCell() + " @ " + c.location);
			}
			ArrayList<NodeDetector> listOfDetectors2 = (ArrayList<NodeDetector>) ((Model) intSched.getSimulator().getModel()).getDetectors().clone();
			for (NodeDetector n: listOfDetectors2) {
				double closestDistance = 99999;
				for (NodeDetector n1: listOfDetectors2) {

					if (!n.equals(n1) && n.getClosestCell().equals(n1.getClosestCell())) {
						double dist = n.location.distance(n1.location);
						if (dist<closestDistance)
							closestDistance = dist;
					}

				}
				n.setDistanceFromNode(closestDistance);
			}


			Collections.sort(listOfDetectors2,new Comparator<NodeDetector>() {
				public int compare(NodeDetector one, NodeDetector other) {

					//for (NodeDetector n: listOfDetector
					return Double.compare((one.getDistanceFromNode()), (other.getDistanceFromNode()));
				}
			}); 

			for (int i = 0; i<10; i++) {
				NodeDetector c  =  listOfDetectors2.get(i);

				System.out.println("det2 "+c.getId() + ": " + c.getDistanceFromNode() + " @ " + c.location);
			}
			//PrintWriter out3;
			/*out3 = new PrintWriter("C:\\Users\\Friso\\Documents\\PilotDatafusie\\Netwerk_Datafusie_Cells.txt");
			out3.println("LinkID, FromNode, ToNode, SpeedLimit, nrLanes, IncomingLink1, IncomingLink2, OutgoingLink1, OutgoingLink2, Length");
			for (int i = 0; i < cellId.size(); i++) {
				//if (!links.contains(new Integer(cellId.get(i)))) {
				String ins = "";
				String outs = "";
				String v = "";
				if (cellsIn.get(i).size() == 1) {
					ins += cellsIn.get(i).get(0)+",,";
				} else if (cellsIn.get(i).size() == 2) {
					ins += cellsIn.get(i).get(0)+","+cellsIn.get(i).get(1)+",";
				} else {
					ins += ","+",";
				}
				if (cellsOut.get(i).size() == 1) {
					outs += cellsOut.get(i).get(0)+",";
				} else if (cellsOut.get(i).size() == 2) {
					outs += cellsOut.get(i).get(0)+","+cellsOut.get(i).get(1)+"";
				} else {
					outs += "";
				}
				ArrayList<Vertex> v2 = new ArrayList<Vertex>();
				for (String s: vertices.get(i)) {
					v2.add(new Vertex(s));
				}

				double le = (double) Math.round(10*Vertex.calcLength(v2))/10d;

				//ins = ins.trim();
				//outs = outs.trim();
				//v = v.trim();
				String output2 = cellId.get(i)+","+nodeIn.get(i)+","+nodeOut.get(i)+","+speedlimit.get(i)+","+lanes.get(i)+","+ins+outs + "," + le;
				String[] o2 = output2.split("\t");
				out3.println(output);
				//}
			}
			out3.close();*/


			/*PrintWriter out2;
			out2 = new PrintWriter(folder+ "\\"+output);
			out2.println("NodeID, LinkIn1, LinkIn2, LinkOut1, LinkOut2, X, Y");
			String sep = ",";

			for (Integer n: list) {
				ArrayList<Integer> cIn2 = new ArrayList<Integer>();
				double x =0 ;
				double y = 0;
				for (int i = 0; i<nodeOut.size(); i++) {
					if (nodeOut.get(i)!= null && nodeOut.get(i).equals(n)) {
						System.out.println(i);
						System.out.println(nodeOut.get(i));
						cIn2.add(cellId.get(i));
						x = new Vertex(vertices.get(i).get(vertices.get(i).size()-1)).getX();
						y = new Vertex(vertices.get(i).get(vertices.get(i).size()-1)).getY();
					}
				}
				ArrayList<Integer> cOut2 = new ArrayList<Integer>();
				for (int m = 0; m<nodeIn.size(); m++) {
					if (nodeIn.get(m) != null && nodeIn.get(m).equals(n)) {
						System.out.println(m);
						System.out.println(nodeIn.get(m));
						cOut2.add(cellId.get(m));
						x = new Vertex(vertices.get(m).get(0)).getX();
						y = new Vertex(vertices.get(m).get(0)).getY();
					}
				}

				out2.print(n +",");
				if (cIn2.size() == 0) {
					out2.print(",,");
				} else if (cIn2.size() == 1) {
					out2.print(cIn2.get(0)+",,");

				} else if (cIn2.size() == 2) {
					out2.print(cIn2.get(0)+"," + cIn2.get(1)+",");
				} else {
					throw new Error("too many cells in");
				}
				if (cOut2.size() == 0) {
					out2.print(",");
				} else if (cOut2.size() == 1) {
					out2.print(cOut2.get(0)+",");

				} else if (cOut2.size() == 2) {
					out2.print(cOut2.get(0)+"," + cOut2.get(1)+"");
				} else {
					throw new Error("too many cells Out");
				}
				out2.print("," + x + "," + y);
				out2.println();


			}
			out2.close();*/










		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}
	public void deleteLink(int linkToBeDeleted) {

		//System.out.println(linkToBeDeleted);
		int index = cellId.indexOf(linkToBeDeleted);
		Integer nIn = nodeIn.get(index);
		Integer nOut = nodeOut.get(index);
		//Vertex a = new Vertex(vertices.get(index).get(0));
		//Vertex b = new Vertex(vertices.get(index).get(vertices.get(index).size()-1));
		//Vertex c = Vertex.weightedVertex(0.5, a, b);
		//String replaceVertex = c.export();

		ArrayList<Integer> cellsInAtNIn = new ArrayList<Integer>();
		ArrayList<Integer> cellsOutAtNOut = new ArrayList<Integer>();
		ArrayList<Integer> cellsInAtNOut = new ArrayList<Integer>();
		ArrayList<Integer> cellsOutAtNIn = new ArrayList<Integer>();
		for (int i=0; i<nodeIn.size(); i++) {
			if (!cellId.get(i).equals(linkToBeDeleted)) {
				if (nodeOut.get(i).equals(nIn))
					cellsInAtNIn.add(cellId.get(i));
				if (nodeIn.get(i).equals(nOut))
					cellsOutAtNOut.add(cellId.get(i));
				if (nodeOut.get(i).equals(nOut))
					cellsInAtNOut.add(cellId.get(i));
				if (nodeIn.get(i).equals(nIn))
					cellsOutAtNIn.add(cellId.get(i));
			}

		}

		for (Integer i: cellsInAtNIn) {
			int index2 = cellId.indexOf(i);
			cellsOut.get(index2).remove(new Integer(linkToBeDeleted));
			/*for (Integer j: cellsOutAtNOut) {
				if (!cellsOut.get(index2).contains(new Integer(j)))
					cellsOut.get(index2).add(new Integer(j));
			}*/
			//cellsOut.get(index2).addAll(cellsOutAtNOut);
			//cellsOut.

			//vertices.get(index2).set(vertices.get(index2).size()-1, replaceVertex);
			//System.out.println("In@In "+index2+": "+cellsOut.get(index2));
		}
		/*for (Integer i: cellsOutAtNIn) {
			int index2 = cellId.indexOf(i);
			//cellsIn.get(index2).remove(new Integer(linkToBeDeleted));
			for (Integer j: cellsInAtNOut) {
				if (!cellsIn.get(index2).contains(new Integer(j)))
					cellsIn.get(index2).add(new Integer(j));
			}
			//cellsIn.get(index2).addAll(cellsInAtNOut);
			vertices.get(index2).set(0, replaceVertex);
			System.out.println("Out@In "+index2+": "+cellsIn.get(index2));
		}*/
		/*for (Integer i: cellsInAtNOut) {
			int index2 = cellId.indexOf(i);
			//cellsOut.get(index2).remove(new Integer(linkToBeDeleted));
			for (Integer j: cellsOutAtNIn) {
				if (!cellsOut.get(index2).contains(new Integer(j)))
					cellsOut.get(index2).add(new Integer(j));
			}
			//cellsOut.get(index2).addAll(cellsOutAtNIn);
			nodeOut.set(index2, nIn);
			vertices.get(index2).set(vertices.get(index2).size()-1, replaceVertex);
			System.out.println("In@Out "+index2+": "+cellsOut.get(index2));
		}*/
		for (Integer i: cellsOutAtNOut) {
			int index2 = cellId.indexOf(i);
			cellsIn.get(index2).remove(new Integer(linkToBeDeleted));
			/*for (Integer j: cellsInAtNIn) {
				if (!cellsIn.get(index2).contains(new Integer(j)))
					cellsIn.get(index2).add(new Integer(j));
			}
			//cellsIn.get(index2).addAll(cellsInAtNIn);
			nodeIn.set(index2, nIn);
			vertices.get(index2).set(0, replaceVertex);
			System.out.println("Out@Out "+index2+": "+cellsIn.get(index2));*/
		}
		ArrayList<Integer> test = new ArrayList<Integer>();
		for (int i=0; i<nodeIn.size(); i++) {
			if (cellsIn.get(i).contains(linkToBeDeleted)) {
				test.add(i);
			}
			if (cellsOut.get(i).contains(linkToBeDeleted)) {
				test.add(i);
			}

		}

		for (int i = 0; i < cellId.size(); i++) {


		}

		cellId.remove(index);
		nodeIn.remove(index);
		nodeOut.remove(index);
		speedlimit.remove(index);
		lanes.remove(index);
		cellsIn.remove(index);
		cellsOut.remove(index);
		vertices.remove(index);

	}
	Random rnd = new Random();
	public void addLink(int from, int to, boolean characteristicsOfFirst) {

		System.out.println(from + " -> " + to);
		int iFrom = cellId.indexOf(from);
		int iTo = cellId.indexOf(to);
		Integer nIn = nodeOut.get(iFrom);
		Integer nOut = nodeIn.get(iTo);
		int sl = Integer.MAX_VALUE;
		int lns = Integer.MAX_VALUE;;

		if (characteristicsOfFirst) {
			sl = speedlimit.get(iFrom);
			lns = lanes.get(iFrom);
		} else {
			sl = speedlimit.get(iTo);
			lns = lanes.get(iTo);
		}


		//Vertex a = new Vertex(vertices.get(index).get(0));
		//Vertex b = new Vertex(vertices.get(index).get(vertices.get(index).size()-1));
		//Vertex c = Vertex.weightedVertex(0.5, a, b);
		//String replaceVertex = c.export();

		ArrayList<Integer> cellsInAtNIn = new ArrayList<Integer>();
		ArrayList<Integer> cellsOutAtNOut = new ArrayList<Integer>();
		ArrayList<Integer> cellsInAtNOut = new ArrayList<Integer>();
		ArrayList<Integer> cellsOutAtNIn = new ArrayList<Integer>();

		ArrayList<Integer> cIn = new ArrayList<Integer>();
		ArrayList<Integer> cOut = new ArrayList<Integer>();
		ArrayList<String> vert = new ArrayList<String>();
		vert.add(vertices.get(iFrom).get(vertices.get(iFrom).size()-1));
		vert.add(vertices.get(iTo).get(0));

		while (cellId.contains(new Integer(cellNr)) || (cellNr <=0) ) {
			cellNr++;
		}
		int newID = cellNr;

		for (int i=0; i<nodeIn.size(); i++) {
			//if (!cellId.get(i).equals(linkToBeDeleted)) {
			if (nodeOut.get(i).equals(nIn))
				cellsInAtNIn.add(cellId.get(i));
			if (nodeIn.get(i).equals(nOut))
				cellsOutAtNOut.add(cellId.get(i));
			if (nodeOut.get(i).equals(nOut))
				cellsInAtNOut.add(cellId.get(i));
			if (nodeIn.get(i).equals(nIn))
				cellsOutAtNIn.add(cellId.get(i));
			//}

		}

		for (Integer i: cellsInAtNIn) {
			int index2 = cellId.indexOf(i);
			cellsOut.get(index2).add(newID);
			cIn.add(i);
			/*for (Integer j: cellsOutAtNOut) {
			if (!cellsOut.get(index2).contains(new Integer(j)))
				cellsOut.get(index2).add(new Integer(j));
		}*/
			//cellsOut.get(index2).addAll(cellsOutAtNOut);
			//cellsOut.

			//vertices.get(index2).set(vertices.get(index2).size()-1, replaceVertex);
			//System.out.println("In@In "+index2+": "+cellsOut.get(index2));
		}
		/*for (Integer i: cellsOutAtNIn) {
		int index2 = cellId.indexOf(i);
		//cellsIn.get(index2).remove(new Integer(linkToBeDeleted));
		for (Integer j: cellsInAtNOut) {
			if (!cellsIn.get(index2).contains(new Integer(j)))
				cellsIn.get(index2).add(new Integer(j));
		}
		//cellsIn.get(index2).addAll(cellsInAtNOut);
		vertices.get(index2).set(0, replaceVertex);
		System.out.println("Out@In "+index2+": "+cellsIn.get(index2));
	}*/
		/*for (Integer i: cellsInAtNOut) {
		int index2 = cellId.indexOf(i);
		//cellsOut.get(index2).remove(new Integer(linkToBeDeleted));
		for (Integer j: cellsOutAtNIn) {
			if (!cellsOut.get(index2).contains(new Integer(j)))
				cellsOut.get(index2).add(new Integer(j));
		}
		//cellsOut.get(index2).addAll(cellsOutAtNIn);
		nodeOut.set(index2, nIn);
		vertices.get(index2).set(vertices.get(index2).size()-1, replaceVertex);
		System.out.println("In@Out "+index2+": "+cellsOut.get(index2));
	}*/
		for (Integer i: cellsOutAtNOut) {
			int index2 = cellId.indexOf(i);
			cellsIn.get(index2).add(newID);
			cOut.add(i);
			/*for (Integer j: cellsInAtNIn) {
			if (!cellsIn.get(index2).contains(new Integer(j)))
				cellsIn.get(index2).add(new Integer(j));
		}
		//cellsIn.get(index2).addAll(cellsInAtNIn);
		nodeIn.set(index2, nIn);
		vertices.get(index2).set(0, replaceVertex);
		System.out.println("Out@Out "+index2+": "+cellsIn.get(index2));*/
		}
		/*ArrayList<Integer> test = new ArrayList<Integer>();
		for (int i=0; i<nodeIn.size(); i++) {
			if (cellsIn.get(i).contains(linkToBeDeleted)) {
				test.add(i);
			}
			if (cellsOut.get(i).contains(linkToBeDeleted)) {
				test.add(i);
			}

		}

		for (int i = 0; i < cellId.size(); i++) {


		}*/

		cellId.add(newID);
		nodeIn.add(nIn);
		nodeOut.add(nOut);
		speedlimit.add(sl);
		lanes.add(lns);
		cellsIn.add(cIn);
		cellsOut.add(cOut);
		vertices.add(vert);

	}

	public void deleteFirst(Integer link, double fraction) {
		//System.out.println(link);
		int index = cellId.indexOf(link);

		Integer nIn = nodeIn.get(index);
		Integer nOut = nodeOut.get(index);


		if (cellsIn.get(index).size() != 0) {
			for (Integer i: cellsIn.get(index)) {
				cellsOut.get(cellId.indexOf(i)).remove(link);


			}
			cellsIn.get(index).clear();

		}
		ArrayList<String> vert = vertices.get(index); 
		ArrayList<Vertex> vert2 = new ArrayList<Vertex>();
		for (String v: vert) {
			vert2.add(new Vertex(v));
		}
		double l = Vertex.calcLength(vert2);

		double res[] = Vertex.calcPointAtDistance(l*fraction, vert2);

		ArrayList<String> vert3 = new ArrayList<String>(vert.subList((int) res[2], vert.size()));
		vert3.add(0, "("+res[0]+","+res[1]+",0.0)");
		vertices.set(index, vert3);
		//System.out.println(res);
		//int posN = nodeNr;
		while (nodeIn.contains(nodeNr) || nodeNr<=0) 
			nodeNr++;
		nodeIn.set(index, nodeNr);
		nodeNr++;


	}
	public void deleteLast(Integer link, double fraction) {
		//System.out.println(link);
		int index = cellId.indexOf(link);

		Integer nIn = nodeIn.get(index);
		Integer nOut = nodeOut.get(index);


		if (cellsOut.get(index).size() != 0) {
			for (Integer i: cellsOut.get(index)) {
				cellsIn.get(cellId.indexOf(i)).remove(link);


			}
			cellsOut.get(index).clear();

		}
		ArrayList<String> vert = vertices.get(index); 
		ArrayList<Vertex> vert2 = new ArrayList<Vertex>();
		for (String v: vert) {
			vert2.add(new Vertex(v));
		}
		double l = Vertex.calcLength(vert2);

		double res[] = Vertex.calcPointAtDistance(l*(1-fraction), vert2);

		ArrayList<String> vert3 = new ArrayList<String>(vert.subList(0,(int) res[2]));
		vert3.add(vert3.size(), "("+res[0]+","+res[1]+",0.0)");
		vertices.set(index, vert3);
		//System.out.println(res);
		//int posN =  rnd.nextInt();
		while (nodeOut.contains(nodeNr) || nodeNr<=0) 
			nodeNr++;
		nodeOut.set(index, nodeNr);
		nodeNr++;

	}
	public void decouple(Integer link, double fraction) {
		this.deleteLast(link,fraction);
		this.deleteFirst(link,fraction/(1-fraction));
	}
	public void disconnect(Integer link1, Integer link2, double fraction) {
		this.deleteLast(link1,fraction);
		this.deleteFirst(link2, fraction);
	}
	public void newIncomingLink(Integer node) {
		int index = nodeIn.indexOf(node);
		String vertex = vertices.get(index).get(0);
		ArrayList<String> vert = new ArrayList<String>();
		Vertex tmpVertex = new Vertex(vertex);
		Vertex newVertex = Vertex.plus(tmpVertex, new Vertex(0,10,0));

		vert.add(newVertex.export());
		vert.add(tmpVertex.export());



		Integer newID = 100;
		while (cellId.contains(newID))
			newID += 1;

		int sl = speedlimit.get(index);
		int lns = lanes.get(index);

		ArrayList<Integer> cOut = new ArrayList<Integer>();
		ArrayList<Integer> cellsInAtNIn = new ArrayList<Integer>();
		ArrayList<Integer> cellsOutAtNOut = new ArrayList<Integer>();
		ArrayList<Integer> cellsInAtNOut = new ArrayList<Integer>();
		ArrayList<Integer> cellsOutAtNIn = new ArrayList<Integer>();
		for (int i=0; i<nodeIn.size(); i++) {
			//if (!cellId.get(i).equals(linkToBeDeleted)) {

			if (nodeIn.get(i)!= null && nodeIn.get(i).equals(node))
				cellsOutAtNOut.add(cellId.get(i));

			//}

		}
		for (Integer i: cellsOutAtNOut) {
			int index2 = cellId.indexOf(i);
			cellsIn.get(index2).add(newID);
			cOut.add(i);

		}

		cellId.add(newID);
		nodeIn.add(null);
		nodeOut.add(node);
		speedlimit.add(sl);
		lanes.add(lns);
		cellsIn.add(new ArrayList<Integer>());
		cellsOut.add(cOut);
		vertices.add(vert);



	}
	public void newOutgoingLink(Integer node) {
		int index = nodeOut.indexOf(node);
		String vertex = vertices.get(index).get(vertices.get(index).size() -1);
		ArrayList<String> vert = new ArrayList<String>();
		Vertex tmpVertex = new Vertex(vertex);
		Vertex newVertex = Vertex.plus(tmpVertex, new Vertex(0,10,0));
		vert.add(tmpVertex.export());
		vert.add(newVertex.export());




		Integer newID = 200;
		while (cellId.contains(newID))
			newID += 1;

		int sl = speedlimit.get(index);
		int lns = lanes.get(index);

		ArrayList<Integer> cIn = new ArrayList<Integer>();
		ArrayList<Integer> cellsInAtNIn = new ArrayList<Integer>();
		ArrayList<Integer> cellsOutAtNOut = new ArrayList<Integer>();
		ArrayList<Integer> cellsInAtNOut = new ArrayList<Integer>();
		ArrayList<Integer> cellsOutAtNIn = new ArrayList<Integer>();
		for (int i=0; i<nodeOut.size(); i++) {
			//if (!cellId.get(i).equals(linkToBeDeleted)) {

			if (nodeOut.get(i)!= null && nodeOut.get(i).equals(node))
				cellsInAtNIn.add(cellId.get(i));

			//}

		}
		for (Integer i: cellsInAtNIn) {
			int index2 = cellId.indexOf(i);
			cellsOut.get(index2).add(newID);
			cIn.add(i);

		}

		cellId.add(newID);
		nodeIn.add(node);
		nodeOut.add(null);
		speedlimit.add(sl);
		lanes.add(lns);
		cellsIn.add(cIn);
		cellsOut.add(new ArrayList<Integer>());
		vertices.add(vert);



	}

}

