package nl.tudelft.otsim.Simulators.MacroSimulator.TestCases;

import java.util.Arrays;

import nl.tudelft.otsim.GeoObjects.Vertex;
import nl.tudelft.otsim.Simulators.MacroSimulator.MacroCell;

public class testVertex {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Vertex mouse = new Vertex(84355.189,445014.749,0);
		MacroCell cell = new MacroCell(null);
		cell.addVertex(new Vertex(84342.0,445034.0,0));
		cell.addVertex(new Vertex(84351.0,445014.0,0));
		cell.addVertex(new Vertex(84390.0,445034.0,0));
		
		
		double[] distance = cell.getSquaredDistanceToVertices(mouse);
		System.out.println(Arrays.toString(distance));
		
		System.out.println(cell.calcLength());
		System.out.println(distance[1]*cell.calcLength());
		System.out.println(Arrays.toString(cell.calcPointAtDistance(distance[1]*cell.calcLength())));
		System.out.println(distance[2]);
		System.out.println(Arrays.toString(cell.calcPointAtDistance(distance[2])));
	}

}
