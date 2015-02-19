package nl.tudelft.otsim.Simulators.MacroSimulator.Nodes;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import nl.tudelft.otsim.GUI.GraphicsPanel;
import nl.tudelft.otsim.GeoObjects.Vertex;
import nl.tudelft.otsim.Simulators.MacroSimulator.MacroCell;

public class NodeDetector extends Node {
	private String name;
	private MacroCell closestCell;
	private double distanceToCell;
	private double distanceFromNode;
	private int fromNode;
	private int toNode;
	private ArrayList<Double> times = new ArrayList<Double>();
	private TreeMap<Double, Double> flows= new TreeMap<Double, Double>();
	private TreeMap<Double, Double> speeds= new TreeMap<Double, Double>();
	public NodeDetector(Vertex loc) {
		super(loc);
		
	}
	@Override
	public void calcFlux() {
		// TODO Auto-generated method stub
//
	}
	
	public void addMeasurements(double time) {
		closestCell.updateVariables();
		flows.put(time, closestCell.QCell);
		speeds.put(time, closestCell.VCell);
		flows.headMap(time-100).clear();
		speeds.headMap(time-100).clear();
		/*if (time > 1000) 
			System.out.println("test");*/
	}
	
	/*public double[] getMeasurements(double fromTime, double untilTime) {
		double avgFlow = 0;
		double avgSpeed = 0;
		//Math.
		ArrayList<Double> selectedFlows = new ArrayList<Double>();
		ArrayList<Double> selectedSpeeds = new ArrayList<Double>();
		
		for (double t: flows.keySet()) {
			if (t>=fromTime && t <= untilTime) {
				selectedFlows.add(flows.get(t));
			}
		}
		for (double t: speeds.keySet()) {
			if (t>=fromTime && t <= untilTime) {
				selectedSpeeds.add(speeds.get(t));
			}
		}
		return new double[]{calcArithmeticMean(selectedFlows),calcArithmeticMean(selectedSpeeds)};
		
	}*/
	public double[] getMeasurements(double fromTime, double untilTime, double timestep) {
		double avgFlow = 0;
		double avgSpeed = 0;
		//Math.
		ArrayList<Double> selectedFlows = new ArrayList<Double>();
		ArrayList<Double> selectedSpeeds = new ArrayList<Double>();
		
		//for (double t = fromTime; t < untilTime; t+=timestep) {
			//if (t>=0) {
			//selectedFlows.add(flows.get(t));
			//selectedSpeeds.add(speeds.get(t));
			//}
		//}
			selectedFlows.addAll(flows.subMap(Math.max(fromTime,0), Math.max(untilTime,0)).values());
			selectedSpeeds.addAll(speeds.subMap(Math.max(fromTime,0), Math.max(untilTime,0)).values());
		return new double[]{calcArithmeticMean(selectedFlows),calcArithmeticMean(selectedSpeeds)};
		
	}
	public double[] getInstantMeasurements() {
		closestCell.updateVariables();
		return new double[]{closestCell.QCell,closestCell.VCell};
	}
	public String getInstantMeasurements_r() {
		return Arrays.toString(getInstantMeasurements());
	}
	
	public static double calcArithmeticMean(ArrayList<Double> values) {
		double res = 0;
		for (double i: values) {
			res += i;
		}
		return res/values.size();
	}
	
	
	public void draw(GraphicsPanel graphicsPanel) {
		//final int nonSelectedNodeDiameter = (int) (130.2 *  graphicsPanel.getZoom());
		final int nonSelectedNodeDiameter = (int) (50.2 *  graphicsPanel.getZoom());
    	Point2D.Double point = location.getPoint();            
        final Color color = Color.BLUE;
        graphicsPanel.setColor(color);
        graphicsPanel.setStroke(1f);
        //graphicsPanel.setStroke(50f);
        graphicsPanel.drawCircle(point, color, nonSelectedNodeDiameter);
        graphicsPanel.setStroke(3f);
   	
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	public String getName_r() {
		return name;
	}
	public MacroCell getClosestCell_r() {
		return closestCell;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the closestCell
	 */
	public MacroCell getClosestCell() {
		return closestCell;
	}
	/**
	 * @param closestCell the closestCell to set
	 */
	public void setClosestCell(MacroCell closestCell) {
		this.closestCell = closestCell;
	}
	/**
	 * @return the distanceToCell
	 */
	public double getDistanceToCell() {
		return distanceToCell;
	}
	public double getDistanceToCell_r() {
		return distanceToCell;
	}
	/**
	 * @param distanceToCell the distanceToCell to set
	 */
	public void setDistanceToCell(double distanceToCell) {
		this.distanceToCell = distanceToCell;
	}
	/**
	 * @return the distanceFromNode
	 */
	public double getDistanceFromNode() {
		return distanceFromNode;
	}
	public double getDistanceFromNode_r() {
		return distanceFromNode;
	}
	/**
	 * @param distanceFromNode the distanceFromNode to set
	 */
	public void setDistanceFromNode(double distanceFromNode) {
		this.distanceFromNode = distanceFromNode;
	}
	/**
	 * @return the fromNode
	 */
	public int getFromNode() {
		return fromNode;
	}
	public int getFromNode_r() {
		return fromNode;
	}
	/**
	 * @param fromNode the fromNode to set
	 */
	public void setFromNode(int fromNode) {
		this.fromNode = fromNode;
	}
	/**
	 * @return the toNode
	 */
	public int getToNode() {
		return toNode;
	}
	public int getToNode_r() {
		return toNode;
	}
	/**
	 * @param toNode the toNode to set
	 */
	public void setToNode(int toNode) {
		this.toNode = toNode;
	}
	public String toString() {
		return "Node("+name+")";
	}

}
