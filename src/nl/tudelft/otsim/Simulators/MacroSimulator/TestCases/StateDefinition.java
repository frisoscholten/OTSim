package nl.tudelft.otsim.Simulators.MacroSimulator.TestCases;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

public enum StateDefinition {
	// In and Out
	K_CELL ("density", StateType.CELL, StateFunction.IN, StateFunction.OUT),
	KCRI_CELL ("criticalDensity", StateType.CELL, StateFunction.IN, StateFunction.OUT),
	VLIM_CELL ("speedLimit", StateType.CELL, StateFunction.IN, StateFunction.OUT),
	KJAM_CELL ("jamDensity", StateType.CELL, StateFunction.IN, StateFunction.OUT),
	VCRI_CELL ("criticalSpeed", StateType.CELL, StateFunction.IN, StateFunction.OUT),
	KCRI_LINK ("criticalDensityLink", StateType.LINK, StateFunction.IN, StateFunction.OUT),
	VLIM_LINK ("speedLimitLink", StateType.LINK, StateFunction.IN, StateFunction.OUT),
	KJAM_LINK ("jamDensityLink", StateType.LINK, StateFunction.IN, StateFunction.OUT),
	VCRI_LINK ("criticalSpeedLink", StateType.LINK, StateFunction.IN, StateFunction.OUT),
	INFLOW_NODE ("inflow", StateType.NODEIN, StateFunction.IN, StateFunction.OUT),
	TF_NODE ("turnfraction", StateType.NODEJUNCTION, StateFunction.IN, StateFunction.OUT),
	//Only out
	V_CELL ("speed", StateType.CELL, StateFunction.OUT),
	Q_CELL ("flow", StateType.CELL, StateFunction.OUT),
	MIN1_DETECTOR ("1min", StateType.DETECTOR, StateFunction.OUT),
	TRAFFICREGIME_CELL ("trafficregime", StateType.CELL, StateFunction.OUT);

	
	private String name;
	private StateType type;
	//private StateFunction[] functions;
	
	private StateDefinition(String name, StateType type, StateFunction ... functions) {
		this.name = name;
		this.type = type;
		for (StateFunction sf: functions) {
			sf.addMember(this);
		}
	}
	public String getName() {
		return name;
	}
	public StateType getType() {
		return type;
	}
	public boolean is(StateFunction with){
	    for (StateDefinition sf : with.getMembers()){
	        if( sf .equals(this))   return true;
	    }
	    return false;
	}
	public enum StateFunction {
		IN,
		OUT;
		
		private List<StateDefinition> members = new LinkedList<StateDefinition>();

	    
	    public EnumSet<StateDefinition> getMembers() {
	        return EnumSet.copyOf(members);
	    }

	    
	    public void addMember(StateDefinition sd) {
	        members.add(sd);
	    }
	    static { // forcing initiation of dependent enum
	        try {
	            Class.forName(StateDefinition.class.getName()); 
	        } catch (ClassNotFoundException ex) { 
	            throw new RuntimeException("Class StateDefinition not found", ex); 
	        }
	    }
	}
	
}
