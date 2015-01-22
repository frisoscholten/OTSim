package nl.tudelft.otsim.Simulators.MacroSimulator.TestCases;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

import Jama.Matrix;
import nl.tudelft.otsim.Events.Scheduler;
import nl.tudelft.otsim.GUI.FakeGraphicsPanel;
import nl.tudelft.otsim.Simulators.MacroSimulator.MacroSimulator;

public class MultiThreadedUpdate extends RecursiveTask<Matrix> {
	/*static class Globals {
	    static ForkJoinPool fjPool = new ForkJoinPool();
	}*/
	private static final long serialVersionUID = 1L;
	private Matrix HX;
	private Matrix D;
	private Matrix R;
	private Matrix HA;
	private Matrix X;
	private Matrix A;
	private int n;
	private int N;
	private int[] stateIndices;
	private AssimilationMethod method;
	//private int m;
	public MultiThreadedUpdate(int[] stateIndices, Matrix HX, Matrix D, Matrix R, Matrix HA, Matrix X, Matrix A, AssimilationMethod method) {
		this.stateIndices = stateIndices;
		this.HX = HX;
		this.D = D;
		this.R = R;
		this.HA = HA;
		this.X = X;
		this.A = A;
		this.n = X.getRowDimension();
		this.N = X.getColumnDimension();
		this.method = method;
	}
	public static void main(String[] args) {

	}
	static Matrix computeTotal(int[] stateIndices, Matrix HX, Matrix D, Matrix R, Matrix HA, Matrix X, Matrix A, AssimilationMethod method) {
		
        return FJPool.fjPool.invoke(new MultiThreadedUpdate(stateIndices, HX, D, R, HA, X, A, method));
    }
	protected Matrix computeDirectly() {
		Matrix diff = new Matrix(stateIndices.length,N);
		int i = 0;
		for (int index: stateIndices) {
			int[] indices = TestEnKF.buildIntArray(TestEnKF.correspondingIndicesOfStateObjects.get(index));
			Matrix HX2 = HX.getMatrix(indices, 0, N-1);
			Matrix D21 = D.getMatrix(indices, 0, N-1);
			Matrix R2 = R.getMatrix(indices, indices);
			Matrix HA2 = HA.getMatrix(indices,0, N-1);

			Matrix Y = D21.minus(HX2);
			//int[] indices2 = config.getIndices(macromodel, surroundingCellsMap.get(detectors.get(i).getClosestCell()));
			Matrix Xtest = X.getMatrix(index,index,0,N-1);
			Matrix Atest = A.getMatrix(index,index,0,N-1);

			//P = (HA2.times(HA2.transpose())).times(1.0/(N-1)).plus(R2);
			//L = P.chol();
			//M = L.solve(Y);
			Matrix M;
			Matrix Z;
			switch (method) {
			case LENKF_GRID_PARALLEL:
				M = TestEnKF.solveInversePStraightForward(R2,HA2,N,Y);
				Z = (HA2.transpose()).times(M);
				diff.setMatrix(i, i, 0,N-1, (Atest.times(Z)).times(1.0/(N-1)));
				break;
			case LENKF_GRID_SMW_PARALLEL:
				// R2 = Rinv!
				M = TestEnKF.solveInversePShermanMorrisonWoodbury2(R2,HA2,N,Y);
				Z = (HA2.transpose()).times(M);
				diff.setMatrix(i, i, 0,N-1, (Atest.times(Z)).times(1.0/(N-1)));
				break;
			case DENKF_GRID_PARALLEL:
				Matrix gemHX = HX2.times(new Matrix(N,1,1.0/N));
				Y =  (D.getMatrix(indices, 0, 0)).minus(gemHX);
				Matrix[] M21 = TestEnKF.solveInversePStraightForwardMult(R2,HA2,N,Y,(D21).minus(HX2));
				Matrix diff21 = (Atest.times((HA2.transpose()).times(M21[0]))).times(1.0/(N-1));
				Matrix gemXtest = Xtest.times(new Matrix(N,1,1.0/N));
				Matrix gemXa2 = gemXtest.plus(diff21);
				Matrix diff3 = (Atest.times((HA2.transpose()).times(M21[1]))).times(1.0/(N-1)).times(0.5);
				Matrix Xa3= Xtest.plus(diff3);
				Matrix gemXa3 = Xa3.times(new Matrix(N,1,1.0/N));
				Matrix Xa21 = Xa3.plus((gemXa2.minus(gemXa3).times(new Matrix(1,N,1.0))));
				diff.setMatrix(i, i, 0, N-1,Xa21.minus(Xtest));
				break;
			case DENKF_GRID_SMW_PARALLEL:
				Matrix gemH = HX2.times(new Matrix(N,1,1.0/N));
				Y =  (D.getMatrix(indices, 0, 0)).minus(gemH);
				Matrix[] M21b = TestEnKF.solveInversePShermanMorrisonWoodburyMult2(R2,HA2,N,Y,(D21).minus(HX2));
				Matrix diff21b = (Atest.times((HA2.transpose()).times(M21b[0]))).times(1.0/(N-1));
				Matrix gemXtestb = Xtest.times(new Matrix(N,1,1.0/N));
				Matrix gemXa2b = gemXtestb.plus(diff21b);
				Matrix diff3b = (Atest.times((HA2.transpose()).times(M21b[1]))).times(1.0/(N-1)).times(0.5);
				Matrix Xa3b= Xtest.plus(diff3b);
				Matrix gemXa3b = Xa3b.times(new Matrix(N,1,1.0/N));
				Matrix Xa22b = Xa3b.plus((gemXa2b.minus(gemXa3b).times(new Matrix(1,N,1.0))));
				diff.setMatrix(i, i, 0, N-1,Xa22b.minus(Xtest));
				break;
			default:
				throw new Error("unimplemented method");
			
				
			}
			
			i++;
		}
		return diff;
	}
	protected static int nTreshold = 50;
	@Override
	protected Matrix compute() {
		int nrn = stateIndices.length;
		if (nrn <= nTreshold) {
			return computeDirectly();
		}
		int split = (int) Math.round(((double) nrn)/2);
		int[] indices1 = Arrays.copyOfRange(stateIndices, 0, split);
		int[] indices2 = Arrays.copyOfRange(stateIndices, split, stateIndices.length);

		MultiThreadedUpdate left = new MultiThreadedUpdate(indices1, HX, D, R, HA, X, A, method);
		MultiThreadedUpdate right = new MultiThreadedUpdate(indices2, HX, D, R, HA, X, A, method);
		left.fork();
		Matrix diffR = right.compute();
		Matrix diffL = left.join();
		
		Matrix totalDiff = new Matrix(diffL.getRowDimension()+diffR.getRowDimension(), N);
		totalDiff.setMatrix(0, diffL.getRowDimension()-1, 0, N-1,diffL);
		totalDiff.setMatrix(diffL.getRowDimension(), diffL.getRowDimension()+diffR.getRowDimension()-1, 0, N-1,diffR);
		return totalDiff;
	}
}
