package nl.tudelft.otsim.Simulators.MacroSimulator.ExternalEvents;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


public enum ExternalEvents {
	LANEDROP("lanedrop","nl.tudelft.otsim.Simulators.MacroSimulator.ExternalEvents.ExternalEventLaneDrop");
		
	//private class c;
	private final String name;
	private final String classname;
	private Class<?> c;
	private ExternalEvents(String s, String cn) {
		this.name = s;
		this.classname = cn;
		try {
			this.c = Class.forName(cn);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static ExternalEvents fromString(String s) {
		ExternalEvents ret = null;
		for (ExternalEvents f: values()) {
			if (s.equals(f.toString())) {	
				ret = f;
			}
			
		}
		return ret;
	}
	public ExternalEvent create(double beginTime, double endTime, double oldPar,
			double newPar, String location) {
		try {
			Constructor ct = c.getConstructor(new Class[]{double.class, double.class, double.class, double.class, String.class});
			return (ExternalEvent) ct.newInstance(beginTime, endTime, oldPar,
					newPar, location);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
