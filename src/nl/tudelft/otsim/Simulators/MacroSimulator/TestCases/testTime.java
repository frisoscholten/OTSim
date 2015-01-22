package nl.tudelft.otsim.Simulators.MacroSimulator.TestCases;

import java.util.ArrayList;
import java.util.Arrays;

import nl.tudelft.otsim.Utilities.TimeScaleFunction;

public class testTime {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*double inflowTruth = 2500;
		String ts = "[0.000/"+inflowTruth+":1500.000/"+inflowTruth+":2100/"+inflowTruth/2+":3600/"+inflowTruth/2+"]";
		TimeScaleFunction t = new TimeScaleFunction(ts);
		for (double i = 0.0; i< 3600; i=i+100.0){
		System.out.println(i +": " + t.getFactor(i));
		}
		int nrCells = 5;
		double[][] test = new double[nrCells][nrCells];
		for (double[] row: test)
			Arrays.fill(row,1.0);
		
		System.out.println(test);*/
		ArrayList<ErrorConfiguration> list = new ArrayList<ErrorConfiguration>();
		list.add(new ErrorConfiguration(StateDefinition.K_CELL,0.001));
		list.add(new ErrorConfiguration(StateDefinition.KCRI_CELL,0.001));
		
		ErrorConfiguration[] list2 = new ErrorConfiguration[list.size()];
		list2 = (ErrorConfiguration[]) list.toArray(list2);
		AssimilationConfiguration config = new AssimilationConfiguration(list2,null, AssimilationMethod.DENKF);
		//System.out.println(e.getName());
		
		
	}

}
