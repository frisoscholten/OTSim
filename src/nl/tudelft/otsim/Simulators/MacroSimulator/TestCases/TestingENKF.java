package nl.tudelft.otsim.Simulators.MacroSimulator.TestCases;

import java.util.Arrays;
import java.util.Random;

import nl.tudelft.otsim.Simulators.MacroSimulator.Nodes.NodeBoundaryIn;
import nl.tudelft.otsim.Utilities.JamaExtension;
import Jama.CholeskyDecomposition;
import Jama.Matrix;

public class TestingENKF {

	public static void main(String[] args) {
		Random r = new Random(1);
		int n = 4000;
		int m =1000;
		int N=20;
		
		int nrExperiments = 100;
		long[] times = new long[nrExperiments];
		for (int t = 0; t<nrExperiments; t++) {
		Matrix X = new Matrix(n, N, 1.0);
		for (int i = 0; i<X.getRowDimension(); i++) {
			for (int j = 0; j<X.getColumnDimension(); j++) {
				X.set(i,j, r.nextDouble());
			}

		}
		Matrix HX = new Matrix(m,N,1.0);
		for (int i = 0; i<HX.getRowDimension(); i++) {
			for (int j = 0; j<HX.getColumnDimension(); j++) {
				HX.set(i,j, 1.0 + 2*r.nextDouble());
			}

		}
		Matrix D = new Matrix(m,N,1.0);
		for (int i = 0; i<HX.getRowDimension(); i++) {
			for (int j = 0; j<HX.getColumnDimension(); j++) {
				D.set(i,j, HX.get(i, j) + r.nextDouble());
			}

		}
		Matrix gemD = D.times(new Matrix(N,1,1.0/N));
		Matrix R = Matrix.identity(m, m).times(2.0);
		Matrix Rinv = Matrix.identity(m, m).times(1/2.0);
		//int N = 10;
		//int nrObservations = 2;
		Matrix gemH = new Matrix(m, 1);
		Matrix gemX = new Matrix(n,1);
		for (int j = 0; j<N; j++) {
			Matrix tmp = HX.getMatrix(0,m-1, j,j);
			Matrix tmp2 = X.getMatrix(0,n-1, j,j);
			gemH = gemH.plus(tmp);
			gemX = gemX.plus(tmp2);
		}
		gemH = gemH.times(1.0/N);
		gemX = gemX.times(1.0/N);
		Matrix HA = new Matrix(m, N);
		for (int i = 0; i<N; i++) {


			HA.setMatrix(0,m-1, i, i, HX.getMatrix(0,m-1,i,i).minus(gemH));
		}
		Matrix A1 = X.times(new Matrix(N,1,1.0));
		Matrix A2 = A1.times(new Matrix(1,N,1.0));
		Matrix A3 = A2.times(1.0/N);
		Matrix A = X.minus(A3);
		long t1 = System.nanoTime();
		Matrix Y = D.minus(HX);
		Matrix gemY = gemD.minus(gemH);
		int a = 0;
		int nr = 20;
		long[] ts1 = new long[nr];
		long[] ts2 = new long[nr];
		Matrix M;
		Matrix[] M2;
		Matrix Z;
		Matrix Z1;
		Matrix Z2;
		Matrix[] Ms = new Matrix[nr];
		Matrix[] Zs = new Matrix[nr];
		
		
		
		
		
		
		ts1[a] = System.nanoTime();
		M = solveInversePStraightForward(R, HA, N, Y);
		Z = (HA.transpose()).times(M);
		ts2[a] = (System.nanoTime()-ts1[a])/1000000;
		Ms[a] = M;
		Zs[a] = Z;
		a++;
		
		
		
		ts1[a] = System.nanoTime();
		M = solveInversePStraightForward2(R, HA, N, Y);
		Z = (HA.transpose()).times(M);
		ts2[a] = (System.nanoTime()-ts1[a])/1000000;
		Ms[a] = M;
		Zs[a] = Z;
		a++;
		
		ts1[a] = System.nanoTime();
		M2 = solveInversePStraightForwardMult(R, HA, N, Y,gemY);
		Z1 = (HA.transpose()).times(M2[0]);
		Z2 = (HA.transpose()).times(M2[1]);
		ts2[a] = (System.nanoTime()-ts1[a])/1000000;
		Z = Z1;
		Ms[a] = M2[0];
		Zs[a] = Z;
		a++;
		
	
		
		
		
		ts1[a] = System.nanoTime();
		M2 = solveInversePStraightForwardMult2(R, HA, N, Y,gemY);
		Z1 = (HA.transpose()).times(M2[0]);
		Z2 = (HA.transpose()).times(M2[1]);
		ts2[a] = (System.nanoTime()-ts1[a])/1000000;
		Z = Z1;
		Ms[a] = M2[0];
		Zs[a] = Z;
		a++;
		
		ts1[a] = System.nanoTime();
		M = solveInversePStraightForward(R, HA, N, gemY);
		Z = (HA.transpose()).times(M);
		ts2[a] = (System.nanoTime()-ts1[a])/1000000;
		a++;
		
		ts1[a] = System.nanoTime();
		M = solveInversePStraightForward2(R, HA, N, gemY);
		Z = (HA.transpose()).times(M);
		ts2[a] = (System.nanoTime()-ts1[a])/1000000;
		a++;
		// too slow:
		/*ts1[a] = System.nanoTime();
		M = solveInversePStraightForward(R, HA, N, Matrix.identity(m, m));
		Z = (HA.transpose()).times(M.times(Y));
		ts2[a] = (System.nanoTime()-ts1[a])/1000000;
		a++;*/
		
		ts1[a] = System.nanoTime();
		M = solveInversePStraightForward2(R, HA, N, Matrix.identity(m, m));
		Z = (HA.transpose()).times(M.times(Y));
		Z2 = (HA.transpose()).times(M.times(gemY));
		ts2[a] = (System.nanoTime()-ts1[a])/1000000;
		a++;
		
		/*ts1[a] = System.nanoTime();
		M = solveInversePStraightForward(R, HA, N, Matrix.identity(m, m));
		Z = (HA.transpose()).times(M.times(gemY));
		ts2[a] = (System.nanoTime()-ts1[a])/1000000;
		a++;*/
		
		/*ts1[a] = System.nanoTime();
		M = solveInversePStraightForward2(R, HA, N, Matrix.identity(m, m));
		Z = (HA.transpose()).times(M.times(gemY));
		ts2[a] = (System.nanoTime()-ts1[a])/1000000;
		a++;*/
		
		ts1[a] = System.nanoTime();
		M = solveInversePShermanMorrisonWoodbury2(Rinv,HA,N,Y);
		Z = (HA.transpose()).times(M);
		ts2[a] = (System.nanoTime()-ts1[a])/1000000;
		Ms[a] = M;
		Zs[a] = Z;
	
		a++;
		
		/*ts1[a] = System.nanoTime();
		M = solveInversePShermanMorrisonWoodbury2(Rinv,HA,N,gemY);
		Z = (HA.transpose()).times(M);
		ts2[a] = (System.nanoTime()-ts1[a])/1000000;
		a++;*/
		
		ts1[a] = System.nanoTime();
		M2 = solveInversePShermanMorrisonWoodburyMult2(Rinv,HA,N,Y,gemY);
		Z1 = (HA.transpose()).times(M2[0]);
		Z2 = (HA.transpose()).times(M2[1]);
		ts2[a] = (System.nanoTime()-ts1[a])/1000000;
		Z = Z1;
		Ms[a] = M2[0];
		Zs[a] = Z;
		a++;
		/*ts1[a] = System.nanoTime();
		M = solveInversePShermanMorrisonWoodbury(Rinv,HA,N,Matrix.identity(m, m));
		Z = (HA.transpose()).times(M.times(Y));
		ts2[a] = (System.nanoTime()-ts1[a])/1000000;
		a++;*/
		
		/*ts1[a] = System.nanoTime();
		M = solveInversePShermanMorrisonWoodbury2(Rinv,HA,N,Matrix.identity(m, m));
		Z = (HA.transpose()).times(M.times(Y));
		ts2[a] = (System.nanoTime()-ts1[a])/1000000;
		a++;*/
		
		System.out.println(Arrays.toString(ts2));
		
		//Matrix P = R.plus((HA.times(HA.transpose())).times(1.0/(N-1)));
		//CholeskyDecomposition L = P.chol();
		//Matrix M = L.solve(Y);
		
		Matrix diff = (A.times(Z)).times(1.0/(N-1));
		/*for (int i = 1; i<6; i++) {
		System.out.println("col: " + (Ms[i-1].minus(Ms[i])).norm1()+" row: " + (Ms[i-1].minus(Ms[i])).normInf());
		System.out.println("err: " + (Ms[i-1].minus(Ms[i])).normF()+" sing: " + (Ms[i-1].minus(Ms[i])).norm2());
		}*/
		Matrix  Xa = X.plus(diff);
		long t2 = System.nanoTime();
		times[t] = (t2-t1);
		System.out.println("time: "+(t2-t1)/1000000);
		
		}
		
		/*
		
		Matrix diff2 = new Matrix(n,N,0.0);
		//	ArrayList<Object> stateVariables = config.getStateVariables(macromodel);
		//int i1=0;
		for (int i1=0; i1<n; i1++) {
			int[] indices = new int[]{0,1};
			
			Matrix HX2 = HX.getMatrix(indices, 0, N-1);
			Matrix D21 = D.getMatrix(indices, 0, N-1);
			Matrix R2 = R.getMatrix(indices, indices);
			Matrix HA2 = HA.getMatrix(indices,0, N-1);

			Y = D21.minus(HX2);
			//int[] indices2 = config.getIndices(macromodel, surroundingCellsMap.get(detectors.get(i).getClosestCell()));
			Matrix Xtest = X.getMatrix(i1,i1,0,N-1);
			Matrix Atest = A.getMatrix(i1,i1,0,N-1);
			P = (HA2.times(HA2.transpose())).times(1.0/(N-1)).plus(R2);
			L = P.chol();
			M = L.solve(Y);
			Z = (HA2.transpose()).times(M);
			diff2.setMatrix(i1, i1, 0, N-1,(Atest.times(Z)).times(1.0/(N-1)));


			//Matrix M2 = L.solve(Matrix.identity(indices.length, indices.length));

			//Matrix K = (Atest.times((HA2.transpose()).times(M2))).times(1.0/(N-1));
			//Matrix KHA = K.times(HA2);
			//Matrix Aa = Atest.minus(KHA.times(0.5));

			//Matrix diff2 = K.times(Y);

			i1++;
		}
		Matrix Xa2 = X.plus(diff2);
		
		

		Matrix backupHX = HX.copy();
		Matrix backupX = X.copy();
		Matrix backupA = A.copy();
		Matrix backupHA = HA.copy();
		for (int i1 = 0; i1 < m; i1++) {
			Matrix HX2 = HX.getMatrix(i1, i1, 0, N-1);
			Matrix D21 = D.getMatrix(i1, i1, 0, N-1);
			double R2 = R.get(i1, i1);
			Matrix HA2 = HA.getMatrix(i1, i1, 0, N-1);

			Y = D21.minus(HX2);
			int[] indices = {0,1,2};
			
			if (i1 < nrSpeedObservations) {
				indices = config.getIndices(macromodel, surroundingCellsMap.get(detectors.get(i1).getClosestCell()));
			} else {
				indices = config.getIndices(macromodel, surroundingCellsMap.get(detectors.get(i1-nrSpeedObservations).getClosestCell()));

			}


			Matrix Xtest = X.getMatrix(indices,0,N-1);
			Matrix Atest = A.getMatrix(indices,0,N-1);
			Matrix bX = backupX.getMatrix(indices,0,N-1);
			Matrix bA = backupA.getMatrix(indices,0,N-1);
			Matrix bHX = backupHX.getMatrix(i1, i1,0,N-1);
			Matrix bHA = backupHA.getMatrix(i1, i1,0,N-1);

			double bp1 = (bHA.times(bHA.transpose()).get(0, 0))*(1.0/(N-1));
			Matrix Kster = bA.times(bHA.transpose()).times(bp1);

			Matrix difference = bX.minus(Xtest);


			double p1 = (HA2.times(HA2.transpose()).get(0, 0))*(1.0/(N-1));




			double p = p1+R2;
			//L = P.chol();
			M = Y.times(1.0/p);
			Z = (HA2.transpose()).times(M);
			diff = (Atest.times(Z)).times(1.0/(N-1));


			double HK = p1/(p);




			Matrix K1 = (Atest.times((HA2.transpose()).times(1.0/p))).times(1.0/(N-1));
			Matrix diff21 = K1.times(Y);

			Xa = Xtest.plus(diff);
			Matrix HXa = HX2.plus(Y.times(HK));
			//System.out.println("X="+Xtest.getArray());
			X.setMatrix(indices, 0, N-1, Xa);
			HX.setMatrix(i1, i1, 0, N-1,HXa);
			Matrix meanXa = HXa.times(new Matrix(N,1,1.0/N));
			Matrix XAa = HXa.minus(new Matrix(1,N,1.0).times(meanXa.get(0, 0)));
			HA.setMatrix(i1, i1, 0, N-1,XAa);

			System.out.println(i1);
		}
		Xa = X.copy();
		X = backupX.copy();
		//HX = HXa;
		System.out.println("stop");*/
		
		System.out.println("klaar");

	}
	static Matrix solveInversePStraightForward(Matrix R, Matrix HA, int N, Matrix solve) {
		Matrix P = R.plus((HA.times(HA.transpose())).times(1.0/(N-1)));
		CholeskyDecomposition L = P.chol();
		Matrix M = L.solve(solve);
		return M;
	}
	static Matrix solveInversePStraightForward2(Matrix R, Matrix HA, int N, Matrix solve) {
		Matrix P = R.plus((HA.times(HA.transpose())).times(1.0/(N-1)));
		//CholeskyDecomposition L = P.chol();
		Matrix M = P.solve(solve);
		return M;
	}
	static Matrix[] solveInversePStraightForwardMult(Matrix R, Matrix HA, int N, Matrix solve1, Matrix solve2) {
		Matrix P = R.plus((HA.times(HA.transpose())).times(1.0/(N-1)));
		CholeskyDecomposition L = P.chol();
		return new Matrix[]{L.solve(solve1),L.solve(solve2)};
	}
	static Matrix[] solveInversePStraightForwardMult2(Matrix R, Matrix HA, int N, Matrix solve1, Matrix solve2) {
		Matrix P = R.plus((HA.times(HA.transpose())).times(1.0/(N-1)));
		//CholeskyDecomposition L = P.chol();
		//Matrix M = P.solve(solve);
		return new Matrix[]{P.solve(solve1),P.solve(solve2)};
	}
	static Matrix solveInversePShermanMorrisonWoodbury(Matrix Rinv, Matrix HA, int N, Matrix solve) {
		long beginTQ  = System.nanoTime();
		//totalFormMatrices += System.nanoTime()-beginFormMatrices;
		Matrix T = JamaExtension.diagTimesRight((HA.transpose()),Rinv);
		Matrix Q = JamaExtension.plusIdentity((T.times(HA)).times(1.0/(N-1)));
		long totalTQ = System.nanoTime()-beginTQ;
		long beginQchol  = System.nanoTime();
		CholeskyDecomposition L = Q.chol();
		long totalQchol = System.nanoTime()-beginQchol;
		long totalQcholRedo;
		if (!L.isSPD()) {
			long beginQcholRedo  = System.nanoTime();
			//System.out.println("not SPD");
			Matrix q = Q.minus(Q.transpose());
			Q = (Q.minus(q.times(0.5)));
			L=Q.chol();
			totalQcholRedo = System.nanoTime()-beginQcholRedo;

		} else {
			totalQcholRedo = 0;
		}
		long beginSolve = System.nanoTime();
		Matrix We = (L.solve(T.times(solve)));
		Matrix M = JamaExtension.diagTimes(Rinv, JamaExtension.plusIdentity((HA.times(We)).times(-1.0/(N-1))));
		//Matrix M1 = JamaExtension.diagTimes(Rinv, (HA.times(We)).times(-1.0/(N-1)));
		//Matrix M2 = JamaExtension.diagPlus(M1, Rinv);
		long totalSolve = System.nanoTime()-beginSolve;
		/*	System.out.println("totalTQ: " +totalTQ);
		System.out.println("totalQchol: " +totalQchol);
		System.out.println("totalQcholRedo: " +totalQcholRedo);
		System.out.println("totalSolve: " +totalSolve);*/
		return M;
	}
	static Matrix solveInversePShermanMorrisonWoodburyb(Matrix Rinv, Matrix HA, int N, Matrix solve) {
		long beginTQ  = System.nanoTime();
		//totalFormMatrices += System.nanoTime()-beginFormMatrices;
		Matrix T = JamaExtension.diagTimesRight((HA.transpose()),Rinv);
		Matrix Q = JamaExtension.plusIdentity((T.times(HA)).times(1.0/(N-1)));
		long totalTQ = System.nanoTime()-beginTQ;
		long beginQchol  = System.nanoTime();
		//CholeskyDecomposition L = Q.chol();
		long totalQchol = System.nanoTime()-beginQchol;
		long totalQcholRedo;
		
		long beginSolve = System.nanoTime();
		Matrix We = (Q.solve(T.times(solve)));
		Matrix M = JamaExtension.diagTimes(Rinv, JamaExtension.plusIdentity((HA.times(We)).times(-1.0/(N-1))));
		//Matrix M1 = JamaExtension.diagTimes(Rinv, (HA.times(We)).times(-1.0/(N-1)));
		//Matrix M2 = JamaExtension.diagPlus(M1, Rinv);
		long totalSolve = System.nanoTime()-beginSolve;
		/*	System.out.println("totalTQ: " +totalTQ);
		System.out.println("totalQchol: " +totalQchol);
		System.out.println("totalQcholRedo: " +totalQcholRedo);
		System.out.println("totalSolve: " +totalSolve);*/
		return M;
	}
	static Matrix solveInversePShermanMorrisonWoodbury2(Matrix Rinv, Matrix HA, int N, Matrix solve) {
		long beginTQ  = System.nanoTime();
		//totalFormMatrices += System.nanoTime()-beginFormMatrices;
		Matrix T = JamaExtension.diagTimesRight((HA.transpose()),Rinv);
		Matrix Q = JamaExtension.plusIdentity((T.times(HA)).times(1.0/(N-1)));
		long totalTQ = System.nanoTime()-beginTQ;
		long beginQchol  = System.nanoTime();
		CholeskyDecomposition L = Q.chol();
		long totalQchol = System.nanoTime()-beginQchol;
		long totalQcholRedo;
		if (!L.isSPD()) {
			long beginQcholRedo  = System.nanoTime();
			//System.out.println("not SPD");
			Matrix q = Q.minus(Q.transpose());
			Q = (Q.minus(q.times(0.5)));
			L=Q.chol();
			totalQcholRedo = System.nanoTime()-beginQcholRedo;

		} else {
			totalQcholRedo = 0;
		}
		long beginSolve = System.nanoTime();
		Matrix We = (L.solve(T.times(solve)));
		Matrix M = JamaExtension.diagTimes(Rinv, solve.plus((HA.times(We)).times(-1.0/(N-1))));
		//Matrix M1 = JamaExtension.diagTimes(Rinv, (HA.times(We)).times(-1.0/(N-1)));
		//Matrix M2 = JamaExtension.diagPlus(M1, Rinv);
		long totalSolve = System.nanoTime()-beginSolve;
		/*	System.out.println("totalTQ: " +totalTQ);
		System.out.println("totalQchol: " +totalQchol);
		System.out.println("totalQcholRedo: " +totalQcholRedo);
		System.out.println("totalSolve: " +totalSolve);*/
		return M;
	}
	static Matrix solveInversePShermanMorrisonWoodbury3(Matrix Rinv, Matrix HA, int N, Matrix solve) {
		long beginTQ  = System.nanoTime();
		//totalFormMatrices += System.nanoTime()-beginFormMatrices;
		Matrix T = JamaExtension.diagTimesRight((HA.transpose()),Rinv);
		Matrix Q = JamaExtension.plusIdentity((T.times(HA)).times(1.0/(N-1)));
		long totalTQ = System.nanoTime()-beginTQ;
		long beginQchol  = System.nanoTime();
		//CholeskyDecomposition L = Q.chol();
		long totalQchol = System.nanoTime()-beginQchol;
		long totalQcholRedo;
		/*if (!L.isSPD()) {
			long beginQcholRedo  = System.nanoTime();
			//System.out.println("not SPD");
			Matrix q = Q.minus(Q.transpose());
			Q = (Q.minus(q.times(0.5)));
			L=Q.chol();
			totalQcholRedo = System.nanoTime()-beginQcholRedo;

		} else {
			totalQcholRedo = 0;
		}*/
		long beginSolve = System.nanoTime();
		Matrix We = (Q.solve(T.times(solve)));
		Matrix M = JamaExtension.diagTimes(Rinv, solve.plus((HA.times(We)).times(-1.0/(N-1))));
		//Matrix M1 = JamaExtension.diagTimes(Rinv, (HA.times(We)).times(-1.0/(N-1)));
		//Matrix M2 = JamaExtension.diagPlus(M1, Rinv);
		long totalSolve = System.nanoTime()-beginSolve;
		/*	System.out.println("totalTQ: " +totalTQ);
		System.out.println("totalQchol: " +totalQchol);
		System.out.println("totalQcholRedo: " +totalQcholRedo);
		System.out.println("totalSolve: " +totalSolve);*/
		return M;
	}
	static Matrix[] solveInversePShermanMorrisonWoodburyMult2(Matrix Rinv, Matrix HA, int N, Matrix solve1, Matrix solve2) {
		long beginTQ  = System.nanoTime();
		//totalFormMatrices += System.nanoTime()-beginFormMatrices;
		Matrix T = JamaExtension.diagTimesRight((HA.transpose()),Rinv);
		Matrix Q = JamaExtension.plusIdentity((T.times(HA)).times(1.0/(N-1)));
		long totalTQ = System.nanoTime()-beginTQ;
		long beginQchol  = System.nanoTime();
		CholeskyDecomposition L = Q.chol();
		long totalQchol = System.nanoTime()-beginQchol;
		long totalQcholRedo;
		if (!L.isSPD()) {
			long beginQcholRedo  = System.nanoTime();
			//System.out.println("not SPD");
			Matrix q = Q.minus(Q.transpose());
			Q = (Q.minus(q.times(0.5)));
			L=Q.chol();
			totalQcholRedo = System.nanoTime()-beginQcholRedo;

		} else {
			totalQcholRedo = 0;
		}
		long beginSolve = System.nanoTime();
		Matrix We1 = (L.solve(T.times(solve1)));
		Matrix M1 = JamaExtension.diagTimes(Rinv, solve1.plus((HA.times(We1)).times(-1.0/(N-1))));
		Matrix We2 = (L.solve(T.times(solve2)));
		Matrix M2 = JamaExtension.diagTimes(Rinv, solve2.plus((HA.times(We2)).times(-1.0/(N-1))));
		//Matrix M1 = JamaExtension.diagTimes(Rinv, (HA.times(We)).times(-1.0/(N-1)));
		//Matrix M2 = JamaExtension.diagPlus(M1, Rinv);
		long totalSolve = System.nanoTime()-beginSolve;
		/*	System.out.println("totalTQ: " +totalTQ);
		System.out.println("totalQchol: " +totalQchol);
		System.out.println("totalQcholRedo: " +totalQcholRedo);
		System.out.println("totalSolve: " +totalSolve);*/
		return new Matrix[]{M1,M2};
	}

}
