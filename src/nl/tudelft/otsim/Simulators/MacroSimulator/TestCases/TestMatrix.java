package nl.tudelft.otsim.Simulators.MacroSimulator.TestCases;

import nl.tudelft.otsim.Utilities.JamaExtension;

import org.ojalgo.matrix.BasicMatrix;
import org.ojalgo.matrix.BasicMatrix.Factory;
import org.ojalgo.matrix.PrimitiveMatrix;
import org.ojalgo.matrix.decomposition.CholeskyDecomposition;
import org.ojalgo.random.Uniform;
import org.ojalgo.random.Weibull;

import Jama.Matrix;


//import Jama.CholeskyDecomposition;
//import Jama.Matrix;

public class TestMatrix {
	static BasicMatrix<?> solveInversePShermanMorrisonWoodbury(BasicMatrix<?> Rinv, BasicMatrix<?> HA, int N, BasicMatrix<?> solve) {
		long beginTQ  = System.nanoTime();
		long m = HA.countRows();
		//totalFormMatrices += System.currentTimeMillis()-beginFormMatrices;
		final BasicMatrix.Factory<?> tmpFactory = PrimitiveMatrix.FACTORY;
		final BasicMatrix<?> I = tmpFactory.makeEye(N, N);
		BasicMatrix<?> T = Rinv.multiplyLeft(HA.transpose());
		BasicMatrix<?> Q = ((T.multiplyRight(HA)).multiply(1.0/(N-1))).add(I);
		BasicMatrix<?> We = Q.solve(T.multiplyLeft(solve));
		BasicMatrix<?> M = Rinv.multiplyLeft(tmpFactory.makeEye(m, m).add(HA.multiplyRight(We).multiply(-1.0/(N-1))));
		Object[] hallo = M.toPrimitiveStore().asList().toArray();
		//Matrix M = new Matrix(hallo.,3)
		return M;
		//Matrix Q = JamaExtension.plusIdentity((T.times(HA)).times(1.0/(N-1)));
		/*long totalTQ = System.nanoTime()-beginTQ;
		long beginQchol  = System.nanoTime();
		Q.
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
		long totalSolve = System.nanoTime()-beginSolve;
		System.out.println("totalTQ: " +totalTQ);
		System.out.println("totalQchol: " +totalQchol);
		System.out.println("totalQcholRedo: " +totalQcholRedo);
		System.out.println("totalSolve: " +totalSolve);
		return M;*/
	}
	public static void main(String[] args) {
		final BasicMatrix.Factory<?> tmpFactory = PrimitiveMatrix.FACTORY;
		int m = 6;
		int N = 10;
		double r1 = 1.5;
		double r2 = 6.0;
		
	
		
		BasicMatrix<?> Rinv = tmpFactory.makeEye(m, m).multiply(r1).add(m/2, m/2, r2-r1);
		BasicMatrix.Builder<?> Rtemp = tmpFactory.getBuilder(m, m); 
		//for (int i = 0; i<m; i++)
			//Rtemp.set(i, i, 7.0);
		
		
		//BasicMatrix<?> Rinv2 = (BasicMatrix<?>) Rtemp.build();
		BasicMatrix<?> HA = tmpFactory.makeRandom(m,N,new Uniform(1.0,2.0));
		BasicMatrix<?> solve = tmpFactory.makeEye(N, N);
		solveInversePShermanMorrisonWoodbury(Rinv,HA,N,solve);
		
		//tmpA.
		//tmpA.
		
		// TODO Auto-generated method stub
		 	/*final BasicMatrix.Factory<?> tmpFactory = PrimitiveMatrix.FACTORY;
	        // A MatrixFactory has 13 different methods that return BasicMatrix instances.

	        final BasicMatrix<?> tmpA = tmpFactory.makeEye(5000, 300);
	        // Internally this creates an "eye-structure" - not a large array...
	        final BasicMatrix<?> tmpB = tmpFactory.makeRandom(300, 2, new Weibull(5.0, 2.0));
	        // When you create a matrix with random elements you can specify their distribution.

	        final BasicMatrix<?> tmpC = tmpB.multiplyLeft(tmpA);
	        final BasicMatrix<?> tmpD = tmpA.multiplyRight(tmpB);
	        // ojAlgo differentiates between multiplying from the left and from the right.
	        // The matrices C and D will be equal, but the code executed to calculate them are different.
	        // The second alternative, resulting in D, will be much faster!
	        // How you created the matrices determines the speed difference.

	        final BasicMatrix<?> tmpE = tmpA.add(1000, 19, 3.14);
	        final BasicMatrix<?> tmpF = tmpE.add(10, 270, 2.18);
	        // The BasicMatrix interface does not specify a set-method for matrix elements.
	        // BasicMatrix instances are immutable.
	        // The add(...) method should only be used to modify a small number of elements.

	        // Don't do this!!!
	        BasicMatrix tmpG = tmpFactory.makeZero(500, 500);
	        for (int j = 0; j < tmpG.countColumns(); j++) {
	            for (int i = 0; i < tmpG.countRows(); i++) {
	                tmpG = tmpG.add(i, j, 100.0 * Math.min(i, j));
	                // Note that add(..) actually adds the specified value to whatever is already there.
	                // In this case that, kind of, works since the base matrix is all zeros.
	                // Completely populating a matrix this way is a really bad idea!
	            }
	        }
	        // Don't do this!!!

	        final double[][] tmpData = new double[][] { { 1.0, 2.0, 3.0 }, { 4.0, 5.0, 6.0 }, { 7.0, 8.0, 9.0 } };
	        final BasicMatrix<?> tmpH = tmpFactory.rows(tmpData);
	        // A, perhaps, natural way to create a small matrix, but the arrays are copied.
	        // Doing it this way is clumsy for larger matrices.

	        final BasicMatrix.Builder<?> tmpBuilder = tmpFactory.getBuilder(500, 500);
	        // If you want to individually set many/all elements of a larger matrix you should use the builder.
	        for (int j = 0; j < tmpBuilder.countColumns(); j++) {
	            for (int i = 0; i < tmpBuilder.countRows(); i++) {
	                tmpBuilder.set(i, j, 100.0 * Math.min(i, j));
	            }
	        }
	        final BasicMatrix<?> tmpI = (BasicMatrix<?>) tmpBuilder.build();
	        // Now you've seen 4 of the 13 MatrixFactory methods...

	        final BasicMatrix<?> tmpJ = tmpA.mergeRows(tmpD);
	        final BasicMatrix<?> tmpK = tmpJ.selectRows(1, 10, 100, 1000);
	        // Sometimes it's practical to only use the factory/builder to create parts of the final matrix.
*/	}

}
