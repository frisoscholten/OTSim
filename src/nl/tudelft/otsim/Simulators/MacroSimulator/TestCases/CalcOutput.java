package nl.tudelft.otsim.Simulators.MacroSimulator.TestCases;

import Jama.Matrix;

public class CalcOutput {
	private CalcOutput() {
		
	}
	public static double[] calcOutput(Matrix truthK, Matrix truthV, Matrix truthTR, Matrix estimateK, Matrix estimateV, Matrix estimateTR, double[] cellLengths, double timestep) {
		double RMSEK = CalcOutput.calcRMSEGlobal(truthK, estimateK)[0];
		double RMSEV = CalcOutput.calcRMSEGlobal(truthV, estimateV)[0];
		double TRE = CalcOutput.calcTRE(truthTR, estimateTR, cellLengths, timestep)[0];
		double MAPEK = CalcOutput.calcMAPEGlobal(truthK, estimateK)[0];
		double MAPEV = CalcOutput.calcMAPEGlobal(truthV, estimateV)[0];
		double[] RMSEKState = CalcOutput.calcRMSEStateDependent(truthK, estimateK, truthTR);
		double[] RMSEVState = CalcOutput.calcRMSEStateDependent(truthV, estimateV, truthTR);
		double[] MAPEKState = CalcOutput.calcMAPEStateDependent(truthK, estimateK, truthTR);
		double[] MAPEVState = CalcOutput.calcMAPEStateDependent(truthV, estimateV, truthTR);

		return new double[]{RMSEK,MAPEK,RMSEV,MAPEV,TRE,RMSEKState[0],MAPEKState[0],RMSEVState[0],MAPEVState[0],RMSEKState[1],MAPEKState[1],RMSEVState[1],MAPEVState[1]};
		
	}
	public static double[] calcOutputExtended(Matrix truthK, Matrix truthV, Matrix truthTR, Matrix estimateK, Matrix estimateV, Matrix estimateTR, double[] cellLengths, double timestep) {
		double[] RMSEK = CalcOutput.calcRMSEGlobal(truthK, estimateK);
		double[] RMSEV = CalcOutput.calcRMSEGlobal(truthV, estimateV);
		double[] TRE = CalcOutput.calcTRE(truthTR, estimateTR, cellLengths, timestep);
		double[] MAPEK = CalcOutput.calcMAPEGlobal(truthK, estimateK);
		double[] MAPEV = CalcOutput.calcMAPEGlobal(truthV, estimateV);
		double[] RMSEKState = CalcOutput.calcRMSEStateDependent(truthK, estimateK, truthTR);
		double[] RMSEVState = CalcOutput.calcRMSEStateDependent(truthV, estimateV, truthTR);
		double[] MAPEKState = CalcOutput.calcMAPEStateDependent(truthK, estimateK, truthTR);
		double[] MAPEVState = CalcOutput.calcMAPEStateDependent(truthV, estimateV, truthTR);

		return new double[]{RMSEK[0],MAPEK[0],RMSEV[0],MAPEV[0],TRE[0],RMSEKState[0],MAPEKState[0],RMSEVState[0],MAPEVState[0],RMSEKState[1],MAPEKState[1],RMSEVState[1],MAPEVState[1],RMSEK[1],MAPEK[1],RMSEV[1],MAPEV[1],TRE[1],RMSEKState[2],MAPEKState[2],RMSEVState[2],MAPEVState[2],RMSEKState[3],MAPEKState[3],RMSEVState[3],MAPEVState[3]};
		
	}
	public static double[] calcSumIndicators(double[][] indicators) {
		int i1= 0;
		int i2=0;
		double[][] nrElements = new double[indicators.length][indicators[0].length];
		for (double[] i: indicators) {
			i2=0;
			for (double j: i){
				nrElements[i1][i2] = 1;
				i2++;
			}
			i1++;
		}
		return calcSumIndicators(indicators, nrElements);
	}
	public static double[] calcSumIndicators(double[][] indicators, double[][] nrElements) {
		double[] result = new double[13];
		int i=0;
		result[i] = calcSumRMSE(indicators[i],nrElements[i++]);
		result[i] = calcSumMAPE(indicators[i],nrElements[i++]);
		result[i] = calcSumRMSE(indicators[i],nrElements[i++]);
		result[i] = calcSumMAPE(indicators[i],nrElements[i++]);
		result[i] = calcSumTRE(indicators[i],nrElements[i++]);
		result[i] = calcSumRMSE(indicators[i],nrElements[i++]);
		result[i] = calcSumMAPE(indicators[i],nrElements[i++]);
		result[i] = calcSumRMSE(indicators[i],nrElements[i++]);
		result[i] = calcSumMAPE(indicators[i],nrElements[i++]);
		result[i] = calcSumRMSE(indicators[i],nrElements[i++]);
		result[i] = calcSumMAPE(indicators[i],nrElements[i++]);
		result[i] = calcSumRMSE(indicators[i],nrElements[i++]);
		result[i] = calcSumMAPE(indicators[i],nrElements[i++]);
		return result;
	}
	public static double calcSumRMSE(double[] RMSE, double[] nrElements) {
		double res = 0;
		int i = 0;
		int totNr = 0;
		for (double r: RMSE) {
			totNr+=nrElements[i];
			res += r*r * nrElements[i++];
		}
		
		return Math.sqrt(res/totNr);
	}
	public static double calcSumMAPE(double[] MAPE, double[] nrElements) {
		double res = 0;
		int totNr = 0;
		int i = 0;
		for (double r: MAPE){
			totNr+=nrElements[i];
			res += r * nrElements[i++];
		}
		return res/totNr;
	}
	public static double calcSumTRE(double[] TRE, double[] nrElements) {
		double res = 0;
		//int totNr = 0;
		int i = 0;
		for (double r: TRE) {
			//totNr+=nrElements[i];
			//res += r * nrElements[i++];
			res += r;
		}
		
		return res;
	}
	public static double[] calcRMSEGlobal(Matrix truth, Matrix estimate) {
		Matrix difference = truth.minus(estimate);
		
		double result = 0;
		for (double[] x1: difference.getArray()) {
			for (double x2: x1) {
				result += x2*x2;
			}
		}
		
		return new double[]{Math.sqrt(result/(difference.getColumnDimension()*difference.getRowDimension())),(difference.getColumnDimension()*difference.getRowDimension())};
		
		
	}
	public static double[] calcRMSEStateDependent(Matrix truth, Matrix estimate, Matrix truthCongested) {
		Matrix difference = truth.minus(estimate);
		double resultCongested = 0;
		double resultFreeFlow = 0;
		int nrCon = 0;
		int nrFF = 0;
		for (int i = 0; i<truth.getRowDimension(); i++) {
			
			
			for (int j = 0; j<truth.getColumnDimension(); j++) {
				if (truthCongested.get(i, j) == 1) {
					resultCongested += difference.get(i, j)*difference.get(i, j);
					nrCon++;
				} else {
					resultFreeFlow += difference.get(i, j)*difference.get(i, j);
					nrFF++;
				}
			}
		}
		double RMSEFF=0;
		double RMSECon=0;
		if (nrFF!= 0)
			RMSEFF = Math.sqrt(resultFreeFlow/nrFF);
		if (nrCon!= 0)
			RMSECon= Math.sqrt(resultCongested/nrCon);
		return new double[]{RMSEFF,RMSECon,nrFF,nrCon};
		
		
	}
	public static double[] calcTRE(Matrix truth, Matrix estimate, double[] cellLengths, double deltaT) {
		Matrix difference = truth.minus(estimate);
		
		double result = 0;
		//double result2 = 0;
		for (double[] x1: difference.getArray()) {
			int i=0;
			
			
			for (double x2: x1) {
				//result2 += Math.abs(x2);
				result += Math.abs(x2)*cellLengths[i];
				
				i++;
			}
			//System.out.println(result);
		}
		
		return new double[]{result*deltaT,(difference.getColumnDimension()*difference.getRowDimension())};
		
		
	}
	public static double[] calcMAPEGlobal(Matrix truth, Matrix estimate) {
		//Matrix difference = (truth.minus(estimate)).arrayRightDivide(truth);
		
		double result = 0;
		int nrEl = 0;
		for (int i = 0; i<truth.getRowDimension(); i++) {
		
		
			for (int j = 0; j<truth.getColumnDimension(); j++) {
				if (truth.get(i, j)!=0) {
				result += Math.min(999999, Math.abs((truth.get(i, j) - estimate.get(i, j))/truth.get(i, j)));
				nrEl++;
				}
			}
			
		}
		double MAPE=0;
		
		if (nrEl!= 0)
			MAPE = result/nrEl;
		
		return new double[]{(MAPE),nrEl};
		
		
	}
	public static double[] calcMAPEStateDependent(Matrix truth, Matrix estimate, Matrix truthCongested) {
		//Matrix difference = (truth.minus(estimate)).arrayRightDivide(truth);
		
		double resultCongested = 0;
		double resultFreeFlow = 0;
		int nrCon = 0;
		int nrFF = 0;
		//int nrIgnoredCon = 0;
		//int nrIgnoredFF = 0;
		for (int i = 0; i<truth.getRowDimension(); i++) {
		
		
			for (int j = 0; j<truth.getColumnDimension(); j++) {
				if (truth.get(i, j)!=0) {
					
					if (truthCongested.get(i, j) == 1) {
						resultCongested += Math.abs((truth.get(i, j) - estimate.get(i, j))/truth.get(i, j));
						nrCon++;
					} else {
						resultFreeFlow += Math.abs((truth.get(i, j) - estimate.get(i, j))/truth.get(i, j));
						nrFF++;
					}
			
				} else {
					/*if (truthCongested.get(i, j) == 1) {
						nrIgnoredCon++;
					} else {
						nrIgnoredFF++;
					}*/
				}
			}
			
		}
		double MAPEFF=0;
		double MAPECon=0;
		if (nrFF!= 0)
			MAPEFF = resultFreeFlow/nrFF;
		if (nrCon!= 0)
			MAPECon= resultCongested/nrCon;
		return new double[]{MAPEFF,MAPECon,nrFF,nrCon};
		
		
		
	}
	public static double[] roundToSignificantFigures(int n, double...numbers ) {
		double[] result = new double[numbers.length];
		int i=0;
		for (double num: numbers) {
		    if(num == 0) {
		        result[i]=0;
		    } else {
	
		    final double d = Math.ceil(Math.log10(num < 0 ? -num: num));
		    final int power = n - (int) d;
	
		    final double magnitude = Math.pow(10, power);
		    final long shifted = Math.round(num*magnitude);
		    
		    result[i]= shifted/magnitude;
		    }
		    i++;
		}
		return result;
	}
	

}
