package nl.tudelft.otsim.Simulators.MacroSimulator.FundamentalDiagrams;

import nl.tudelft.otsim.Simulators.MacroSimulator.MacroCell;

public class FDSmulders implements IFD {
	final int nrParameters = 5; 
	public FDSmulders() {
		// TODO Auto-generated constructor stub
	}
	public double calcQ(double[] param) {
		if (param.length != nrParameters) {
			throw new Error("Wrong number of parameters");
		}
		double k = param[0];
		double vLim = param[1];
		double kCri = param[2];
		double kJam = param[3];
		double vCri = param[4];
		
		
		
		
		double w = kCri*vCri/(kJam - kCri);
		double e = 0.0001;
		if (k<0-e || k > kJam+e) {
			System.out.println("density is not correct" + Double.toString(k));
			k = Math.max(Math.min(k, kJam),0);

		} 
		
		double q;
		
		if (k<=kCri) {

			q = k*(vLim - k*(vLim - vCri)/kCri);
		} else {

			q = w*(kJam-k);

		}
		
		//if (q<0)
			//throw new Error("q<0");
		return q;
		
	}
	public double calcV(double[] param) {
		if (param.length != nrParameters) {
			throw new Error("Wrong number of parameters");
		}
		double k = param[0];
		double vLim = param[1];
		double kCri = param[2];
		double kJam = param[3];
		double vCri = param[4];
		
		
		double w = kCri*vCri/(kJam - kCri);
		double e = 0.0001;
		if (k<0-e || k > kJam+e) {
			System.out.println("density is not correct" + Double.toString(k));
			k = Math.max(Math.min(k, kJam),0);

		} 
		double v;
		if (k<=kCri) {

			v = (vLim - k*(vLim - vCri)/kCri);
		} else {

			v =  w*(kJam/k-1);

		}
		if (v<0)
			throw new Error("v<0");
		return v;
	}
	public double calcQ(MacroCell mc, double[] addedParam) {
		if (addedParam.length != nrParameters) {
			throw new Error("Wrong number of parameters");
		}
		double[] param = new double[]{mc.KCell + addedParam[0], mc.vLim + addedParam[1], mc.kCri + addedParam[2], mc.kJam+ addedParam[3], mc.vCri + addedParam[4]};
		return calcQ(param);


	}
	public double calcQ(MacroCell mc) {
		double[] param = new double[]{mc.KCell, mc.vLim, mc.kCri, mc.kJam, mc.vCri};
		return calcQ(param);
	}

	
	public double calcV(MacroCell mc) {
		double[] param = new double[]{mc.KCell, mc.vLim, mc.kCri, mc.kJam, mc.vCri};

		return calcV(param);
	}
	public double calcV(MacroCell mc, double[] addedParam) {
		if (addedParam.length != nrParameters) {
			throw new Error("Wrong number of parameters");
		}
		double[] param = new double[]{mc.KCell + addedParam[0], mc.vLim + addedParam[1], mc.kCri + addedParam[2], mc.kJam+ addedParam[3], mc.vCri+ addedParam[4]};

		return calcV(param);
	}



/*public double calcQcap(MacroCell mc) {
	double kJam = mc.kJam;
	double kCri = mc.kCri;
	double vLim = mc.vLim;
	return kCri*vLim*(1-(kCri/kJam));
}*/
public double calcQCap(MacroCell mc) {
	double[] param = new double[]{mc.kCri, mc.vLim, mc.kCri, mc.kJam, mc.vCri};
	return calcQ(param);
}
public double calcQCap(double[] parameters) {
	double[] param = new double[]{parameters[2], parameters[1], parameters[2], parameters[3], parameters[4]};
	return calcQ(param);
	
}
public double calcQCap(MacroCell mc, double[] addedParameters) {
	double[] param = new double[]{mc.kCri + addedParameters[2], mc.vLim+addedParameters[1], mc.kCri+addedParameters[2], mc.kJam+addedParameters[3], mc.vCri+addedParameters[4]};
	return calcQ(param);
}



}
