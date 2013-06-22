package com.amlogic.a3d.math;

//is line cross with a plane 

import java.util.Arrays;

public final class Matrix {
	private float[][] matrix = null;

	private int length = 0;

	private int width = 0;

	/*
	 * construct method
	 */
	public Matrix(int i, int j) {
		if ((i <= 0) || (j <= 0)) {
		//	System.out.println("\n The parameter is not legal");
			return;
		}
		length = i;
		width = j;
		matrix = new float[length][width];
	}
	
	
	public Matrix(float[][] arg) {
		
		length = arg.length;
		width = arg[0].length;
		matrix = new float[length][width];
		for (int m = 0; m < length; m++)
			for (int n = 0; n < width; n++)
				matrix[m][n] = arg[m][n];
	}

	//set element line i ,colume j
	public void setElement(float data, int i, int j) {
		if ((i < 1) || (j < 1) || (i > length) || (j > width)) {
			//System.out.println("\nThe parameter is unnormal");
			return;
		}
		matrix[i - 1][j - 1] = data;

	}

	//
	public float getElement(int i, int j) {
		return matrix[i - 1][j - 1];
	}

	public int length() {
		return length;
	}

	public int width() {
		return width;
	}

	public void setLength(int len) {
		if (len <= 0) {
		//	System.out.println("\n The length is legal");
			return;
		}
		length = len;
		
	}

	public void setWide(int wid) {
		if (wid <= 0) {
		//	System.out.println("\n The width is not legal");
			return;
		}
		width = wid;
		/*
		if (wid<=width) {
			width = wid;
			return;
		}
		float[][] tmp = new float[length][wid]; //extend memory.
		for (int i=0; i<length; i++) {//Copy
			for (int j=0; j<width; j++) {
				tmp[i][j] = matrix[i][j];
			}
		}
		
		matrix = tmp;
		width = wid;
		*/
	}

	public float[] getRow(int row) {
		if ((row > length) || (row < 1)) {
		//	System.out.println("\n parameter error");
			return null;
		}
		float[] temp = matrix[row - 1];
		return temp;
	}

	public float[] getCol(int col) {
		if ((col > width) || (col < 1)) {
			// System.out.println("\n parameter error");
			return null;
		}
		float[] temp = new float[length];
		for (int i = 0; i < length; i++)
			temp[i] = matrix[i][col - 1];
		return temp;
	}

	public void setRow(float[] data, int row) {
		if ((row < 1) || (row > length)) {
			//System.out.println("\n row parameter error");
			return;
		}
		int len = data.length;
		if (len != width) {
		//	System.out.println("\n The data is not normal");
			return;
		}
		for (int i = 0; i < width; i++)
			matrix[row - 1][i] = data[i];
	}

	public void setCol(float[] data, int col) {
		if ((col < 1) || (col > width)) {
		//	System.out.println("\ncol parameter error");
			return;
		}
		int len = data.length;
		if (len != length) {
		//	System.out.println("\ncol The data is not normal");
			return;
		}
		for (int i = 0; i < length; i++)
			matrix[i][col - 1] = data[i];
	}

	//change two lines
	public void chgRow(int chgfrom, int chgto) {
		//float[] temp = null;
		if ((chgfrom > length) || (chgto > length) || (chgfrom < 1)
				|| (chgto < 1)) {
			return;
		}
		float[] temp = new float[width];
		for (int i = 0; i < width; i++) {
			temp[i] = matrix[chgfrom - 1][i];
			matrix[chgfrom - 1][i] = matrix[chgto - 1][i];
			matrix[chgto - 1][i] = temp[i];
		}
	}

	//change two columes
	public void chgCol(int chgfrom, int chgto) {
		if ((chgfrom > width) || (chgto > width) || (chgfrom < 1)
				|| (chgto < 1)) {
		//	System.out.println("\n parameter error");
			return;
		}
		float[] temp = new float[length];
		for (int i = 0; i < length; i++) {
			temp[i] = matrix[i][chgfrom - 1];
			matrix[i][chgfrom - 1] = matrix[i][chgto - 1];
			matrix[i][chgto - 1] = temp[i];
		}
	}

	//
	public int add(Matrix matrixAdd) {
		int lengthAdd = matrixAdd.length;
		int widthAdd = matrixAdd.width;
		if ((lengthAdd != this.length) || (widthAdd != this.width)) {
			//System.out.println("THe parameter is wrong,two Matrix can not do add operation");
			return -1;
		}
		float[][] matrixData = new float[lengthAdd][widthAdd];
		matrixData = matrixAdd.matrix;
		for (int i = 0; i < this.length; i++)
			for (int j = 0; j < this.width; j++) {
				matrix[i][j] = matrix[i][j] + matrixData[i][j];
			}
		return 1;
	}

	public int add(float[][] matrixAdd) {
		float[] sap = matrixAdd[0];
		if(matrixAdd.length<this.length||sap.length<this.width){
			return -1;		
		}
		for (int i = 0; i < this.length; i++)
			for (int j = 0; j < this.width; j++) {
				matrix[i][j] = matrix[i][j] + matrixAdd[i][j];
			}
		return 1;
	}

	
	public int subtract(Matrix matrixAdd) {
		int lengthAdd = matrixAdd.length;
		int widthAdd = matrixAdd.width;
		if ((lengthAdd != this.length) || (widthAdd != this.width)) {
			//System.out.println("THe parameter is wrong,two Matrix can not do add operation");
			return -1;
		}
		float[][] matrixData = new float[lengthAdd][widthAdd];
		matrixData = matrixAdd.matrix;
		for (int i = 0; i < this.length; i++)
			for (int j = 0; j < this.width; j++)
			{
				matrix[i][j] = matrix[i][j] - matrixData[i][j];
			}
		return 1;
	}

	public int subtract(float[][] matrixAdd) {
		float[] sap = matrixAdd[0];
		if(matrixAdd.length<this.length||sap.length<this.width){
			return -1;		
		}

		for (int i = 0; i < this.length; i++)
			for (int j = 0; j < this.width; j++) {
				matrix[i][j] = matrix[i][j] - matrixAdd[i][j];
			}
		return 1;
	}
	
	public void multiConstants(float k) {
		
		for (int i = 0; i < this.length; i++)
			for (int j = 0; j < this.width; j++) {
				matrix[i][j] = k* matrix[i][j];
			}
		
	}

	/*
	 * 
	 * 
	 * */
	public static Matrix multiply(Matrix matrix1, Matrix matrix2) {
		Matrix ResultMul = null;
		int length1 = matrix1.length();
		int width1 = matrix1.width();
		int length2 = matrix2.length();
		int width2 = matrix2.width();
		if ((length1 <= 0)
			|| (length2 <= 0)
			|| (width1 <= 0)
			|| (width2 <= 0)
			|| (width1 != length2)) {
			// System.out.println("\n The compute is not legal");
			return null;
		}
		ResultMul = new Matrix(length1,width2);
		for (int i = 1; i <= length1; i++) {
			for (int j = 1; j <= width2; j++) {
				float temp = 0;
				for (int k = 1; k <= width1; k++) {
					temp = temp + matrix1.getElement(i, k) * matrix2.getElement(k, j);
				}
				ResultMul.setElement(temp, i, j);
			}
		}
		return ResultMul;
	}
	
	
	/*
	 * 
	 * 
	 * */
	public static Matrix turn(Matrix matrix1) {
		Matrix resultTurn = null;
		int len = matrix1.length();
		int wid = matrix1.width();
		if ((len <= 0) || (wid <= 0)) {
			//System.out.println("\n The matrix is not legal");
			return null;
		}
		resultTurn = new Matrix(wid,len);
		resultTurn.setLength(wid);
		resultTurn.setWide(len);
		for (int i = 1; i <= wid; i++) {
			for (int j = 1; j <= len; j++) {
				float temp = matrix1.getElement(j, i);
				resultTurn.setElement(temp, i, j);
			}
		}
		return resultTurn;
	}

	/*
	 * 
	 * 
	 * */
	 public static Matrix rev(Matrix matrix){
		 int len = matrix.length();
		 int wid = matrix.width();
		 if((len <= 0) || (wid <= 0) || (len != wid)){
			// System.out.println("\n The matrix is not legal");
			 return null;
		 }
		 //
		 Matrix matrixA = new Matrix(len,wid*2);
		 for (int i=1; i<=len; i++)
			 for (int j=1; j<=wid; j++){
			 matrixA.setElement(matrix.getElement(i,j),i,j);
		 }
		 
		 for (int i=1; i<=len; i++)
			 for (int j=wid+1; j<=wid*2; j++){
			 if (i==j-wid){
				 matrixA.setElement(1,i,j);
			 }
			 else 
				 matrixA.setElement(0,i,j);
		 }
		 //
		 for (int i=1; i<=len; i++){
			 float max = matrixA.getElement(i,i);
			 float absmax = Math.abs(max);
			 
			 int maxrow = i;
			 for (int j=i; j<=len; j++){
				 float max2 = matrixA.getElement(j,i);
				 float absmax2 = Math.abs(max2);
				 if (absmax2>absmax){
					 maxrow = j;
					 max = max2;
					 absmax = absmax2;
				 }
			 }
			 //System.out.println(max);
			 if (absmax==0){
				// System.out.println("\n");
				 return null;
			 }
			 matrixA.chgRow(i,maxrow);
			 //return matrixA;
			 //
			 for (int k=1; k<=wid*2; k++){
				 float temp = matrixA.getElement(i,k) / max;
				 matrixA.setElement(temp,i,k);
			 }

			 for (int m=1; m<i; m++){
				 float para = matrixA.getElement(m,i);
				 if (para!=0){
					 for (int n=1; n<=wid*2; n++){
						 float num = matrixA.getElement(m,n);
						 num = num - matrixA.getElement(i,n) * para;
						 matrixA.setElement(num,m,n);
					 }
				 }
			 }

			 for (int m=i+1; m<=len; m++){
				 float para = matrixA.getElement(m,i);
				 if (para!=0){
					 for (int n=1; n<=wid*2; n++){
						 float num = matrixA.getElement(m,n);
						 num = num - matrixA.getElement(i,n) * para;
						 matrixA.setElement(num,m,n);
					 }
				 }
			 }
			 //return matrixA;
		 }

		 Matrix matrixB = new Matrix(len, wid);
		 for (int i = 1; i <= len; i++)
			 for (int j = wid+1; j <= wid*2; j++) {
				 float temp = matrixA.getElement(i,j);
				 matrixB.setElement(temp,i,j-wid);
			 }
		 return matrixB;
	 }	
}
