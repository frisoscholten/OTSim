package nl.tudelft.otsim.Simulators.MacroSimulator;

import java.util.ArrayList;

public class RouteTravelTime {
	ArrayList<MacroCell> route;
	ArrayList<MacroCell> currentCells = new ArrayList<MacroCell>();
	ArrayList<Double> beginTimes = new ArrayList<Double>();
	ArrayList<Double> endTimes = new ArrayList<Double>();
	ArrayList<Double> travelTimes = new ArrayList<Double>();
	ArrayList<Double> progressInCurrentCell = new ArrayList<Double>();
	double lastTime;
	double timeOffset = 0;
	
	public RouteTravelTime(ArrayList<MacroCell> route) {
		this.route = route;
	}
	private void updateStep(double timenow) {
		double dt = timenow-lastTime;
		if (dt>0) {
			for (int i = 0; i<currentCells.size(); i++) {
				
				double restT = dt;
				
				while (restT>0 && endTimes.get(i) == -1) {
					if ((progressInCurrentCell.get(i) + restT*currentCells.get(i).VCell) < currentCells.get(i).l ) {
						progressInCurrentCell.set(i, progressInCurrentCell.get(i) + restT*currentCells.get(i).VCell);
						restT = 0;
						//travelTimes.set(i, element)
						
						
					} else {
						restT = restT-(currentCells.get(i).l-progressInCurrentCell.get(i))/currentCells.get(i).VCell;
						if (!(route.indexOf(currentCells.get(i)) == route.size() -1)) {
							currentCells.set(i, route.get(route.indexOf(currentCells.get(i))+1));
							progressInCurrentCell.set(i,0.0);
						}
						else {
							endTimes.set(i, timenow-restT+timeOffset);
							travelTimes.set(i, endTimes.get(i) - beginTimes.get(i));
						}
					}
					
					
					
					
				}
				
				
				
				
				
				
			}
			
			
		
		
		}
		lastTime = timenow;
		
	}
	private void newTrajectory(double timenow) {
		currentCells.add(route.get(0));
		beginTimes.add(timenow+timeOffset);
		endTimes.add(-1.0);
		travelTimes.add(-1.0);
		progressInCurrentCell.add(0.0);
	}
	public void modelUpdate(double timenow) {
		if (currentCells.size() !=0)
			updateStep(timenow);
		newTrajectory(timenow);
		
	}
	public ArrayList<ArrayList<Double>> getTravelTime() {
		ArrayList<ArrayList<Double>> ret = new ArrayList<ArrayList<Double>>();
		ret.add(beginTimes);
		ret.add(travelTimes);
		return ret;
	}
	public void setTimeOffset(double offset) {
		this.timeOffset = offset;
	}
	
	
}
