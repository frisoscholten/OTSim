package nl.tudelft.otsim.Simulators.MacroSimulator.ExternalEvents;

import java.util.ArrayList;
import java.util.List;

import nl.tudelft.otsim.GeoObjects.Vertex;
import nl.tudelft.otsim.Simulators.MacroSimulator.MacroCell;
import nl.tudelft.otsim.Simulators.MacroSimulator.Model;


public class ExternalEventLaneDrop implements ExternalEvent {
	double beginTime;
	double endTime;
	boolean eventStarted = false;
	boolean eventEnded= false;
	int oldLanes;
	int newLanes;
	Vertex location;
	MacroCell associatedCell;
	
	public ExternalEventLaneDrop(double beginTime, double endTime, double oldLanes,
			double newLanes, String location) {
		this(beginTime, endTime, (int) oldLanes, (int) newLanes, location);
		
	}
	public ExternalEventLaneDrop(double beginTime, double endTime, int oldLanes,
			int newLanes, String location) {
		this(beginTime, endTime, oldLanes, newLanes, new Vertex(location));
		
	}
	
	public ExternalEventLaneDrop(double beginTime, double endTime, int oldLanes,
			int newLanes, Vertex location) {
		this.beginTime = beginTime;
		this.endTime = endTime;
		this.oldLanes = oldLanes;
		this.newLanes = newLanes;
		this.location = location;
		
	}
	@Override
	public void startEvent() {
		associatedCell.lanes = newLanes;
		associatedCell.recalculateFDparameters();
		this.eventStarted = true;
		
	}
	@Override
	public void endEvent() {
		associatedCell.lanes = oldLanes;
		associatedCell.recalculateFDparameters();
		this.eventEnded = true;
	}
	public MacroCell getAssociatedCell() {
		return associatedCell;
	}
	public void setAssociatedCell(MacroCell associatedCell) {
		this.associatedCell = associatedCell;
	}
	public void init(Model macromodel) {
		List<MacroCell> macroCells = macromodel.getCells(); 
		double bestDistance = Double.MAX_VALUE;
		
		MacroCell selectedCell = null;
		for (MacroCell m: macroCells) {
			double[] distance = new double[]{0,0};
			if (location.distance(m.vertices.get(0)) > 100000)
				continue;
			distance = m.getSquaredDistanceToVertices(location);
			//double distance = p.distance(new Vertex(translated,0));
			if ((distance[0] < bestDistance)) {
				selectedCell = m;
				bestDistance = distance[0];

			}

		}
		if (selectedCell == null)
			System.out.println("selectCell = null");
		setAssociatedCell(selectedCell);
	
	}
	@Override
	public double getBeginTime() {
		return beginTime;

		
	}
	@Override
	public double getEndTime() {
		return endTime;

		
	}
	@Override
	public boolean eventStarted() {
		// TODO Auto-generated method stub
		return eventStarted;
	}
	@Override
	public boolean eventEnded() {
		// TODO Auto-generated method stub
		return eventEnded;
	}
	
	
	
}
