package nl.tudelft.otsim.Simulators.MacroSimulator.TestCases;

import java.util.ArrayList;
import java.util.Arrays;




public class ExperimentConfiguration {


	ArrayList<ArrayList<ErrorConfiguration>> errorConfigurations;
	ArrayList<AssimilationMethod> assimilationMethods;
	ArrayList<String> networkConfigurations;
	ArrayList<EnKFRunConfiguration> runConfigurations;
	ArrayList<Integer> localizationWidths;
	ArrayList<Integer> inflowTFFactors;
	ArrayList<Integer> ensembleSizes;
	boolean forecasts;
	int[] exportRoute = new int[]{0};
	boolean extendedOutput = false;

	public ExperimentConfiguration(ArrayList<ArrayList<ErrorConfiguration>> errorConfigurations, ArrayList<AssimilationMethod> assimilationMethods,	ArrayList<String> networkConfigurations, ArrayList<Integer> localizationWidths, ArrayList<Integer> inflowTFFactors, ArrayList<Integer> ensembleSizes, boolean forecasts, boolean extendedOutput) {
		this.errorConfigurations=errorConfigurations;

		this.networkConfigurations=networkConfigurations;
		this.assimilationMethods=assimilationMethods;
		this.localizationWidths = localizationWidths;
		this.ensembleSizes = ensembleSizes;
		this.inflowTFFactors = inflowTFFactors;
		this.runConfigurations = generateEnKFRunConfigurations();
		this.forecasts = forecasts;
		this.extendedOutput = extendedOutput;
		
	}
	public ExperimentConfiguration(ArrayList<ArrayList<ErrorConfiguration>> errorConfigurations, ArrayList<AssimilationMethod> assimilationMethods,	ArrayList<String> networkConfigurations, ArrayList<Integer> localizationWidths, ArrayList<Integer> ensembleSizes, boolean forecasts, boolean extendedOutput) {
		this(errorConfigurations, assimilationMethods, networkConfigurations, localizationWidths, (new ArrayList<Integer>(Arrays.asList(Integer.valueOf(1)))),ensembleSizes,forecasts, extendedOutput);
	}
	public ExperimentConfiguration(ArrayList<EnKFRunConfiguration> runConfigurations, ArrayList<String> networkConfigurations, boolean forecasts, boolean extendedOutput) {
		this.runConfigurations = runConfigurations;
		this.networkConfigurations = networkConfigurations;
		this.forecasts = forecasts;
		this.extendedOutput = extendedOutput;
	}

	private ArrayList<EnKFRunConfiguration> generateEnKFRunConfigurations() {
		ArrayList<EnKFRunConfiguration> configs = new ArrayList<EnKFRunConfiguration>();

		//for (String networkConfig: networkConfigurations) {
		for (AssimilationMethod method: assimilationMethods) {
			switch (method.getType()) {
			case LOCAL: 
				for (Integer localizationWidth: localizationWidths) {
					for (Integer inflowTFFactor: inflowTFFactors) {
					
					for (ArrayList<ErrorConfiguration> errors: errorConfigurations) {
						for (Integer i: ensembleSizes) {
							configs.add(new EnKFRunConfiguration(method,localizationWidth, errors,i, inflowTFFactor*localizationWidth));
						}
					}
					}
				}
				break;
			case GLOBAL:
				for (ArrayList<ErrorConfiguration> errors: errorConfigurations) {
					for (Integer i: ensembleSizes) {
						configs.add(new EnKFRunConfiguration(method,1, errors,i,1));
					}
				}
			}

		}
		//}
		return configs;


	}

	public ArrayList<EnKFRunConfiguration> getRunConfigurations() {
		return runConfigurations;
	}
	public void addRunConfiguration(EnKFRunConfiguration config) {
		this.runConfigurations.add(config);
	}
	public ArrayList<String> getNetworkConfigurations() {
		return networkConfigurations;
	}
	public boolean forecastsNeeded() {
		return forecasts;
	}
	public int[] getExportRoute() {
		return exportRoute;
	}
	public void setExportRoute(int[] exportRoute) {
		this.exportRoute = exportRoute;
	}
	public boolean getExtendedOutput() {
		return extendedOutput;
	}
	public void setExtendedOutput(boolean extendedOutput) {
		this.extendedOutput = extendedOutput;
	}

}
