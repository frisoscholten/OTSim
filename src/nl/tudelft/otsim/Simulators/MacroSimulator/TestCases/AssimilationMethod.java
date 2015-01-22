package nl.tudelft.otsim.Simulators.MacroSimulator.TestCases;

public enum AssimilationMethod {
		ENKF (AssimilationMethodType.GLOBAL),
		DENKF (AssimilationMethodType.GLOBAL),
		ENKF_SCHUR (AssimilationMethodType.LOCAL),
		LENKF_GRID (AssimilationMethodType.LOCAL),
		LENKF_MEASUREMENT (AssimilationMethodType.LOCAL),
		DENKF_GRID (AssimilationMethodType.LOCAL),
		DENKF_MEASUREMENT (AssimilationMethodType.LOCAL),
		ENKF_SMW (AssimilationMethodType.GLOBAL),
		DENKF_SMW (AssimilationMethodType.GLOBAL),
		ENKF_SCHUR_SMW (AssimilationMethodType.LOCAL),
		LENKF_GRID_SMW (AssimilationMethodType.LOCAL),
		DENKF_GRID_SMW (AssimilationMethodType.LOCAL),
		LENKF_GRID_PARALLEL (AssimilationMethodType.LOCAL),
		LENKF_GRID_SMW_PARALLEL (AssimilationMethodType.LOCAL),
		DENKF_GRID_PARALLEL (AssimilationMethodType.LOCAL),
		DENKF_GRID_SMW_PARALLEL (AssimilationMethodType.LOCAL),
		NO_ASSIMILATION (AssimilationMethodType.GLOBAL);
		
		
		private AssimilationMethodType type;
		
		private AssimilationMethod(AssimilationMethodType type) {
			this.type = type;
		}
		public AssimilationMethodType getType() {
			return type;
		}
		
		enum AssimilationMethodType {
			GLOBAL,
			LOCAL;
		}
}
