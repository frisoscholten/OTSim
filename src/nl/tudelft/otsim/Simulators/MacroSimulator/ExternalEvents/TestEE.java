package nl.tudelft.otsim.Simulators.MacroSimulator.ExternalEvents;

import nl.tudelft.otsim.GeoObjects.Vertex;

public class TestEE {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ExternalEvent ex = new ExternalEventLaneDrop(300, 6000, 4, 2, new Vertex(2,2,0));
		ExternalEvent ext = ExternalEvents.fromString("LANEDROP").create(300, 6000, 4, 2, "(2,2,0)");
		System.out.println(ext.getBeginTime());
	}

}
