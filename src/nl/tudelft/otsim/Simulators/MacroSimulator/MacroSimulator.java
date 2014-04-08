package nl.tudelft.otsim.Simulators.MacroSimulator;

import java.awt.event.MouseEvent;
import java.util.ArrayList;

import nl.tudelft.otsim.Events.Scheduler;
import nl.tudelft.otsim.Events.Step;
import nl.tudelft.otsim.GUI.GraphicsPanel;
import nl.tudelft.otsim.Simulators.ShutDownAble;
import nl.tudelft.otsim.Simulators.SimulatedObject;
import nl.tudelft.otsim.Simulators.Simulator;

/**
 * Macro Simulator for OpenTraffic
 * 
 * @author Peter Knoppers
 */
public class MacroSimulator extends Simulator implements ShutDownAble, Step {
	/** Type of this Simulator */
	public static final String simulatorType = "Macro simulator";
	
	private final Scheduler scheduler;
	private final GraphicsPanel graphicsPanel;
	private double endTime = 1000;	// should be overridden in the configuration
	private double randomSeed = 0;	// idem

	/**
	 * Create a MacroSimulator.
	 * @param configuration String; textual description of the network,
	 * traffic demand and measurement plans
	 * @param graphicsPanel {@link GraphicsPanel} to draw on
	 * @param scheduler {@link Scheduler} for this simulation
	 * @throws Exception 
	 */
	public MacroSimulator(String configuration, GraphicsPanel graphicsPanel, Scheduler scheduler) throws Exception {
		System.out.println("Creating a new MacroSimulator based on description:\n" + configuration);
		this.scheduler = scheduler;
		this.graphicsPanel = graphicsPanel;
		scheduler.enqueueEvent(0, this);	// Set up my first evaluation
		/*
		 * It does make sense to first join successive roadway sections that
		 * have the same capacity and speed limit. Then split up longer roadways 
		 * into multiple cells and create a class that macro-simulates one cell.
		 */
		for (String line : configuration.split("\n")) {
			String[] fields = line.split("\t");
			if (fields.length == 0)
				continue;	// Ignore empty lines in configuration
			else if (fields[0].equals("EndTime:"))
				this.endTime = Double.parseDouble(fields[1]);
			else if (fields[0].equals("Seed:"))
				this.randomSeed = Double.parseDouble(fields[1]);
			else if (fields[0].equals("Roadway:")) {
				// TODO: store the info so we can simulate it and draw it 
			} else
				throw new Exception("Don't know how to parse " + line);
			// TODO: write code to handle the not-yet-handled lines in the configuration
		}
	}

	@Override
	public void setModified() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void repaintGraph(GraphicsPanel graphicsPanel) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(GraphicsPanel graphicsPanel, MouseEvent evt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(GraphicsPanel graphicsPanel, MouseEvent evt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(GraphicsPanel graphicsPanel, MouseEvent evt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(GraphicsPanel graphicsPanel, MouseEvent evt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ShutDown() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preStep() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postStep() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Scheduler getScheduler() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<SimulatedObject> SampleMovables() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean step(double now) {
		// TODO Auto-generated method stub
		return false;
	}

}
