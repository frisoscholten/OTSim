package nl.tudelft.otsim.Simulators.MacroSimulator.TestCases;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeSet;

import nl.tudelft.otsim.GeoObjects.Vertex;

public class DeleteLinks {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Path path = FileSystems.getDefault().getPath("C:\\Users\\Friso\\Documents\\PilotDatafusie", "Netwerk_Datafusie_fixedNodesTest2.txt");
		
		try {
			String fileContent = new String(Files.readAllBytes(path), "UTF-8");
			ArrayList<Integer> cellId = new ArrayList<Integer>();
			ArrayList<Integer> nodeIn = new ArrayList<Integer>();
			ArrayList<Integer> nodeOut = new ArrayList<Integer>();
			ArrayList<Integer> speedlimit = new ArrayList<Integer>();
			ArrayList<Integer> lanes = new ArrayList<Integer>();
			ArrayList<ArrayList<Integer>> cellsIn = new ArrayList<ArrayList<Integer>>();
			ArrayList<ArrayList<Integer>> cellsOut = new ArrayList<ArrayList<Integer>>();
			ArrayList<ArrayList<String>> vertices = new ArrayList<ArrayList<String>>();
			for (String line : fileContent.split("\n")) {
				line = line.trim();
				String[] fields = line.split("\t");
				cellId.add(Integer.parseInt(fields[1]));
				nodeIn.add(Integer.parseInt(fields[3]));
				nodeOut.add(Integer.parseInt(fields[5]));
				speedlimit.add(Integer.parseInt(fields[7]));
				lanes.add(Integer.parseInt(fields[9]));
				
				ArrayList<String> cellVertices = new ArrayList<String>();
				ArrayList<Integer> cIn = new ArrayList<Integer>();
				ArrayList<Integer> cOut = new ArrayList<Integer>();
				for (int i = 10; i < fields.length; i++) {
					if (fields[i].equals("vertices")) {
						// add all incoming links to MacroCell
						while (!fields[++i].startsWith("ins")) {
							cellVertices.add(fields[i]);
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
			}
			//int linkToBeDeleted = 69626;
			ArrayList<Integer> links = new ArrayList<Integer>();
			links.add(69626);
			links.add(65717);
			links.add(90363);
			links.add(44750);
			
			links.add(70180);
			links.add(90870);
			links.add(58108);
			links.add(32256);
			
			links.add(16421);
			links.add(64349);
			links.add(8945);
			links.add(75332);
			
			links.add(27262);
			links.add(85577);
			
			links.add(49578);
			links.add(49579);
			
			links.add(28850);
			links.add(74277);
			
			links.add(36589);
			links.add(36590);
			
			links.add(6124);
			links.add(40627);
			links.add(45044);
			links.add(13243);
			
			links.add(90072);
			links.add(18256);
			links.add(60246);
			links.add(58230);
			
			links.add(1090);
			links.add(91922);
			
			links.add(67321);
			links.add(18914);
			
			links.add(43847);
			links.add(78076);
			links.add(50951);
			links.add(63023);
			links.add(57708);
			links.add(69222);
			links.add(1837);
			links.add(81354);
			
			for (int linkToBeDeleted: links) {
			
			int index = cellId.indexOf(linkToBeDeleted);
			Integer nIn = nodeIn.get(index);
			Integer nOut = nodeOut.get(index);
			Vertex a = new Vertex(vertices.get(index).get(0));
			Vertex b = new Vertex(vertices.get(index).get(vertices.get(index).size()-1));
			Vertex c = Vertex.weightedVertex(0.5, a, b);
			String replaceVertex = c.export();
			
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
				for (Integer j: cellsOutAtNOut) {
					if (!cellsOut.get(index2).contains(new Integer(j)))
						cellsOut.get(index2).add(new Integer(j));
				}
				//cellsOut.get(index2).addAll(cellsOutAtNOut);
				//cellsOut.
				
				vertices.get(index2).set(vertices.get(index2).size()-1, replaceVertex);
				System.out.println("In@In "+index2+": "+cellsOut.get(index2));
			}
			for (Integer i: cellsOutAtNIn) {
				int index2 = cellId.indexOf(i);
				//cellsIn.get(index2).remove(new Integer(linkToBeDeleted));
				for (Integer j: cellsInAtNOut) {
					if (!cellsIn.get(index2).contains(new Integer(j)))
						cellsIn.get(index2).add(new Integer(j));
				}
				//cellsIn.get(index2).addAll(cellsInAtNOut);
				vertices.get(index2).set(0, replaceVertex);
				System.out.println("Out@In "+index2+": "+cellsIn.get(index2));
			}
			for (Integer i: cellsInAtNOut) {
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
			}
			for (Integer i: cellsOutAtNOut) {
				int index2 = cellId.indexOf(i);
				cellsIn.get(index2).remove(new Integer(linkToBeDeleted));
				for (Integer j: cellsInAtNIn) {
					if (!cellsIn.get(index2).contains(new Integer(j)))
						cellsIn.get(index2).add(new Integer(j));
				}
				//cellsIn.get(index2).addAll(cellsInAtNIn);
				nodeIn.set(index2, nIn);
				vertices.get(index2).set(0, replaceVertex);
				System.out.println("Out@Out "+index2+": "+cellsIn.get(index2));
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
			
		
			
			System.out.println("klaar");
			PrintWriter out;
			out = new PrintWriter("C:\\Users\\Friso\\Documents\\PilotDatafusie\\Netwerk_Datafusie_fixedNodesTest3.txt");
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
				//ins = ins.trim();
				//outs = outs.trim();
				//v = v.trim();
				String output = "Roadway:	"+cellId.get(i)+"	from	"+nodeIn.get(i)+"	to	"+nodeOut.get(i)+"	speedlimit	"+speedlimit.get(i)+"	lanes	"+lanes.get(i)+"	vertices	"+v+ins+outs;
				String[] o2 = output.split("\t");
				out.println(output);
				//}
			}
			out.close();
		
		
		
		
		
		
		
		
		
		
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

}
