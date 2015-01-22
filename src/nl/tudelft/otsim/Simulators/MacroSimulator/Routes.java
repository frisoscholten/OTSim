package nl.tudelft.otsim.Simulators.MacroSimulator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import nl.tudelft.otsim.Simulators.MacroSimulator.Nodes.Node;
import nl.tudelft.otsim.Simulators.MacroSimulator.Nodes.NodeBoundaryIn;
import nl.tudelft.otsim.Simulators.MacroSimulator.Nodes.NodeInterior;
import nl.tudelft.otsim.Utilities.TimeScaleFunction;

public class Routes {
	protected ArrayList<ArrayList<Integer>> routes = new ArrayList<ArrayList<Integer>>();
	protected ArrayList<ArrayList<MacroCell>> extendedRoutes = new ArrayList<ArrayList<MacroCell>>();
	protected ArrayList<Double> flows = new ArrayList<Double>();
	protected ArrayList<TimeScaleFunction> tsfs = new ArrayList<TimeScaleFunction>();
	protected ArrayList<RouteTravelTime> rtt = new ArrayList<RouteTravelTime>();
	
	public Routes() {
		
	}
	
	public void addRoute(ArrayList<Integer> route, Double flow, TimeScaleFunction tsf) {
		routes.add(route);
		int index = routes.indexOf(route);
		flows.add(index, flow);
		tsfs.add(index,tsf);
	}
	public void deleteNode(Integer node) {
		
		for (ArrayList<Integer> route: routes) {
			if (route.contains(node)) {
				int index = route.indexOf(node);
				route.remove(index);
				//flows.remove(index);
			}
			
		}


	}
	public void cleanRoutes(HashSet<Integer> usedNodes) {
		ArrayList<ArrayList<Integer>> tmproutes = new ArrayList<ArrayList<Integer>>();
		for (ArrayList<Integer> route: routes) {
			ArrayList<Integer> tmproute = new ArrayList<Integer>();
			for (Integer i: route) {
				if (usedNodes.contains(i)) {
					tmproute.add(i);
					//flows.remove(index);
				}
					
			}
			tmproutes.add(tmproute);
		}
		routes = tmproutes;
	}
	public void setTurnFractions(ArrayList<NodeInterior> junctionNodes) {
		for (Node n : junctionNodes) {
			double[] flowIn = new double[n.cellsIn.size()];
			double[] flowOut = new double[n.cellsOut.size()];
			Arrays.fill(flowIn, 0);
			Arrays.fill(flowOut, 0);
			
			double[][] assignedFlows = new double[n.cellsIn.size()][n.cellsOut.size()];
			
			for (ArrayList<Integer> route: routes) {
				int indexInRoute = route.indexOf(n.getId());
				if (indexInRoute != -1) {
				int idOfUpstreamNode = route.get(indexInRoute-1);
				int idOfDownstreamNode = route.get(indexInRoute+1);
				double flow = flows.get(routes.indexOf(route));
				
				for (int i = 0; i < n.cellsIn.size(); i++) {
					MacroCell mcIn = n.cellsIn.get(i);
				
					if (mcIn.getConfigNodeIn() == idOfUpstreamNode) {
						for (int j = 0; j < n.cellsOut.size(); j++) {
							MacroCell mcOut = n.cellsOut.get(j);
							if (mcOut.getConfigNodeOut() == idOfDownstreamNode) {
								assignedFlows[i][j] = flow;
							}
							
							
						}
					}
				}
				
				}
				
			}
			
		
			n.setTurningRatio(assignedFlows);
		}
	}
	public void setInflowBoundaries(ArrayList<NodeBoundaryIn> inflowNodes) {
		for (NodeBoundaryIn n: inflowNodes) {
			
			for (ArrayList<Integer> route: routes) {
				
				if (route.get(0) == n.getId()) {
					
					n.setInflow(n.getInflow() + flows.get(routes.indexOf(route))/3600.0);
					n.addTimeScaleFunction(tsfs.get(routes.indexOf(route)));
				}
			
			}
			n.initTSF();
		}
	}
	public void setExtendedRoutes(ArrayList<NodeBoundaryIn> inflowNodes) {
		for (ArrayList<Integer> route: routes) {
			ArrayList<MacroCell> extendedRoute = new ArrayList<MacroCell>();
			for (NodeBoundaryIn n: inflowNodes) {
				if (route.get(0) == n.getId()) {
					MacroCell next = n.cellsOut.get(0);
					extendedRoute.add(next);
					
					
					while (!(next.downs.size() == 0) ) {
						if (next.downs.size() == 1) {
							MacroCell nc = next.downs.get(0);
							extendedRoute.add(nc);
							next = nc;
							
						} else {
							for (MacroCell mc: next.downs) {
								Integer node = route.indexOf(next.getConfigNodeOut());
								if ((mc.getConfigNodeIn() == route.get(node)) && (mc.getConfigNodeOut()==route.get(node+1)) ) {
									
									extendedRoute.add(mc);
									next = mc;
									break;
								}
							}
						}
						
					}
					
				}
			
			
			}
			extendedRoutes.add(extendedRoute);
			rtt.add(new RouteTravelTime(extendedRoute));
		}
	}
	public ArrayList<ArrayList<ArrayList<Double>>> getTravelTimes() {
		ArrayList<ArrayList<ArrayList<Double>>> ret = new ArrayList<ArrayList<ArrayList<Double>>>();
		for (RouteTravelTime rt: rtt) {
			ret.add(rt.getTravelTime());
		}
		return ret;
		
	}
	
	public void setTimeOffset(double offset) {
		for (RouteTravelTime rt: rtt) {
			rt.setTimeOffset(offset);
		}
	}
	/*public void calcTravelTimeUpdate(double timenow) {
		for (ArrayList<MacroCell> extendedRoute : extendedRoutes) {
			
			MacroCell cell = extendedRoute.get(0);
			
		}
	}
	*/

	public ArrayList<ArrayList<MacroCell>> getExtendedRoutes() {
		return extendedRoutes;
	}

}
