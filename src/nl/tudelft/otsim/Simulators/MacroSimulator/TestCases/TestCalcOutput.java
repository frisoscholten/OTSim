package nl.tudelft.otsim.Simulators.MacroSimulator.TestCases;

import java.util.Arrays;

import nl.tudelft.otsim.Utilities.JamaExtension;
import Jama.Matrix;

public class TestCalcOutput {
	
	public static void main(String[] args) {
		testRMSE();

	}
	public static void testRMSE() {
		
		Matrix t = new Matrix(new double[]{1,2,3,4,1,2,3,4,1,2,3,4,5,1,2,3,4,1,2,3,4,1,2,3,4,5},13);
		//Matrix e = new Matrix(new double[]{1.5,1,2,5},13);
		double[][] arr = t.getArray();
		double[][] arr2 = Arrays.copyOfRange(arr, 0, 6);
		double[] rmse = CalcOutput.calcSumIndicators(t.getArray());
		
		System.out.println(rmse);
		
		double[] a=new double[]{1.45, 1.578234978,0.002435,557824};
		System.out.println(Arrays.toString(CalcOutput.roundToSignificantFigures(1, a)));
		System.out.println(Arrays.toString(CalcOutput.roundToSignificantFigures(2, a)));
		System.out.println(Arrays.toString(CalcOutput.roundToSignificantFigures(3, a)));
		System.out.println(Arrays.toString(CalcOutput.roundToSignificantFigures(4, a)));
		Matrix res;
		Matrix A = new Matrix(new double[][]{
				{1.0, 0.7, 0.3},
				{2.0, 2.0, 5.0},
				{6.0, 3.0, 3.0}}
			 );
		Matrix d = new Matrix(new double[][]{
			{1.0, 0.0, 0.0},
			{0.0, 2.0, 0.0},
			{0.0, 0.0, 3.0}}
		 );
		res = JamaExtension.diagTimes(d,A);
		System.out.println(Arrays.deepToString(res.getArray()));
		res = JamaExtension.diagTimesRight(A,d);
		System.out.println(Arrays.deepToString(res.getArray()));
		
		res = JamaExtension.plusIdentity(res);
		System.out.println(Arrays.deepToString(res.getArray()));
		
		
	}

}
