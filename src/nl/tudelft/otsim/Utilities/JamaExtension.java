package nl.tudelft.otsim.Utilities;

import Jama.Matrix;
import JamaSparseMatrix.SparseMatrix;
import JamaSparseMatrix.SparseMatrixCompressedRS;
import JamaSparseMatrix.SparseMatrixDirectRS;

public class JamaExtension {

	private JamaExtension() {
		
	}
	public static Matrix diagTimes(Matrix diag, Matrix other) {
		int n = diag.getColumnDimension();
		//int m = diag.getColumnDimension();
		if (diag.getRowDimension() != n)
			throw new Error("Matrix is not diagonal.");
		Matrix X = new Matrix(other.getRowDimension(),other.getColumnDimension());
		for (int i=0; i<other.getRowDimension(); i++) {
			X.setMatrix(i, i, 0, other.getColumnDimension()-1,other.getMatrix(i, i, 0, other.getColumnDimension()-1).times(diag.get(i, i)));
		}
			
		return X;
	}
	public static Matrix diagPlus(Matrix A, Matrix other) {
		int n = A.getColumnDimension();
		//if (A.getRowDimension() != n)
			//throw new Error("Matrix is not diagonal.");
		if (A.getColumnDimension() != other.getColumnDimension() && A.getRowDimension() != other.getRowDimension())
			throw new Error("A and other not of same size");
		Matrix X = A.copy();
	
		for (int i=0; i<n; i++) {
			X.set(i,i,A.get(i, i)+other.get(i, i));
		}
			
		return X;
	}
	public static Matrix diagTimesRight(Matrix other, Matrix diag) {
		int n = diag.getRowDimension();
		if (diag.getColumnDimension() != n)
			throw new Error("Matrix is not diagonal.");
		Matrix X = new Matrix(other.getRowDimension(),other.getColumnDimension());
		for (int i=0; i<other.getColumnDimension(); i++) {
			X.setMatrix(0, other.getRowDimension()-1, i, i,other.getMatrix(0, other.getRowDimension()-1, i, i).times(diag.get(i, i)));
		}
			
		return X;
	}
	public static Matrix plusIdentity(Matrix A) {
		return plusIdentity(A,1.0);
	}
	public static Matrix plusIdentity(Matrix A, double value) {
		int n = A.getColumnDimension();
		if (A.getRowDimension() != n)
			throw new Error("Matrix is not diagonal.");
		Matrix X = A.copy();
		for (int i=0; i<n; i++) {
			X.set(i,i,A.get(i, i)+value);
		}
			
		return X;
	}
	public static Matrix sparseMatrixSchurMultiplication(Matrix A, Matrix B) {
		SparseMatrix As = new SparseMatrixDirectRS(A.getArray()); 
		SparseMatrix Bs = new SparseMatrixDirectRS(B.getArray()); 
		SparseMatrix res = (As.arrayTimesEquals(Bs));
		return res.toMatrix();
		
		
	}
	
	public static SparseMatrix arrayTimes (SparseMatrix A, Matrix B) {
		int m = A.getRowDimension();
		if (B.getRowDimension() != A.getRowDimension() || B.getColumnDimension() != A.getColumnDimension())
			throw new IllegalArgumentException("SparseMatrix dimensions must agree.");
		SparseMatrix res = A.copy();
		for (int i=0; i<m; i++) {
			int[] indexes = A.getColumnIndexes(i);
			double[] val = A.getValues(i);
			for (int j=0; j<A.nnzRow(i); j++) {
				res.set(i,indexes[j],B.get(i,indexes[j])*val[j]);
			}
		}
		return res;
	}
}
