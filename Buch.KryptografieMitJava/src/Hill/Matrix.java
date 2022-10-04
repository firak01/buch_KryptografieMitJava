import VisualNumerics.math.DoubleMatrix;
import VisualNumerics.math.MathException;

class Matrix extends VisualNumerics.math.DoubleMatrix {
  public static String Fehlermeldung;
  public static boolean Fehler;
  static final double epsilon = 1.0E-4;

  static void setzeFehler (String str) {
    Fehlermeldung = str;
    Fehler = true;
  } 
  public static double[] int2doubleVektor(int[]A) {
    double[] a=new double[A.length];
    for (int i=0; i<A.length; i++)
      a[i]=(double)A[i];
    return a;
  }
  public static double[][] int2doubleMatrix(int[][]A) {
    double[][] a=new double[A.length][A[0].length];
    for (int i=0; i<A.length; i++)
      for (int j=0; j<A[i].length; j++) 
        a[i][j]=(double)A[i][j]+epsilon;
    return a;
  }
  public static int [][] double2intMatrix(double[][]A) {
    int[][] a=new int[A.length][A[1].length];
    for (int i=0; i<A.length; i++)
      for (int j=0; j<A[i].length; j++) 
        a[i][j]=(int)Math.round(A[i][j]);
     return a;
  }
  public static int [] double2intVektor(double[]A) {
    int[] a=new int[A.length];
    for (int i=0; i<A.length; i++)
        a[i]=(int)Math.round(A[i]);
    return a;
  }
  public static double[][] Inverse(double[][] A) {
    Fehler = false;
    try { return DoubleMatrix.inverse(A); }
    catch (MathException e) { setzeFehler("Singuläre Matrix!"); }
    catch(IllegalArgumentException e) { setzeFehler("Matrix nicht quadratisch!"); }
    return A;
  }
  public static int[][] Inverse(int[][] A) {
    Fehler = false;
    try { 
      return double2intMatrix(DoubleMatrix.inverse(int2doubleMatrix(A))); 
    }
    catch (MathException e) { setzeFehler("Singuläre Matrix!"); }
    catch(IllegalArgumentException e) { setzeFehler("Matrix nicht quadratisch!"); }
    return A;
  }
  public static double Determinate(double[][] A) {
    Fehler = false;
    try { return DoubleMatrix.determinant(A); }
    catch (IllegalArgumentException e) { setzeFehler("Matrix nicht quadratisch!"); }
    catch(MathException e) { setzeFehler("Singuläre Matrix!"); }
    return 0.0;
  }
  public static int Determinante(int[][] A) {
    Fehler = false;
    double[][]a = new double[A.length][A[0].length];
    a = int2doubleMatrix(A);
    try { return (int)Math.round(DoubleMatrix.determinant(a)); }
    catch (IllegalArgumentException e) { setzeFehler("Matrix nicht quadratisch!"); }
    catch(MathException e) { setzeFehler("Singuläre Matrix!"); }
    return 0;
  }
  public static double[][] add(double[][] A, double[][] B) {    
    Fehler = false;
    try { return DoubleMatrix.add(A,B); }
    catch (IllegalArgumentException e) { 
      setzeFehler("Matritzen nicht gleich (m,n)!");
      return (new double[A.length][A[0].length]);
    }
  }
  public static int[][] add (int[][] A, int[][]B) {
    int[][]a = new int[A.length][A[0].length];
    for (int i=0; i<A.length; i++)
      for (int j=0; j<A[i].length; j++) 
        a[i][j]=A[i][j]+B[i][j];
    return a;
  }
  public static int[] add (int[] A, int[]B) {
    int[]a = new int[A.length];
    for (int i=0; i<A.length; i++)
      a[i]=A[i]+B[i];
    return a;
  }
  public static double[][] sub(double[][] A, double[][] B) {    
    Fehler = false;
    try { return DoubleMatrix.subtract(A,B); }
    catch (IllegalArgumentException e) {
      setzeFehler("Matritzen nicht gleich (m,n)!"); 
      return (new double[A.length][A[0].length]);
    }
  }
  public static double[] sub (double[] A, double[]B) {
    double[]a = new double[A.length];
    for (int i=0; i<A.length; i++)
      a[i]=A[i]-B[i];
    return a;
  }
  public static int[][] sub (int[][] A, int[][]B) {
    int[][]a = new int[A.length][A[0].length];
    for (int i=0; i<A.length; i++)
      for (int j=0; j<A[i].length; j++) 
        a[i][j]=A[i][j]-B[i][j];
    return a;
  }
  public static int[] sub (int[] A, int[]B) {
    int[]a = new int[A.length];
    for (int i=0; i<A.length; i++)
      a[i]=A[i]-B[i];
    return a;
  }
  public static double[] mult (double[] A, double[][]B) {
    Fehler = false;
    try {
      int k;
      double[]a = new double[A.length];
      a = DoubleMatrix.multiply(A,B);
      return a;
    }
    catch (IllegalArgumentException e) {
      setzeFehler("Matritzen nicht gleich (m,n)!");
      return (new double[A.length]);
    }   
  }
  public static double[] mult (double[][] A, double[]B) {
    Fehler = false;
    try {
      int k;
      double[]a = new double[A.length];
      a = DoubleMatrix.multiply(A,B);
      return a;
    }
    catch (IllegalArgumentException e) {
      setzeFehler("Matritzen nicht gleich (m,n)!");
      return (new double[A.length]);
    }   
  }
  public static int[][] mult (int[][] A, int[][]B) {
    Fehler = false;
    try {
      int k;
      int[][]a = new int[A.length][A[0].length];
      a = double2intMatrix(DoubleMatrix.multiply(int2doubleMatrix(A),int2doubleMatrix(B)));
      return a;
    }
    catch (IllegalArgumentException e) {
      setzeFehler("Matritzen nicht gleich (m,n)!"); 
      return (new int[A.length][A[0].length]);
    }   
  }
/*
  public static int[] mult (int[] A, int[][]B) {
    int[]a = new int[A.length];
    for (int i=0; i<A.length; i++)
      for (int k=0; k<A.length; k++)
        a[i]=a[i]+A[i]*B[k][i];
    return a;
  }
*/
  public static int[] mult (int[] A, int[][]B) {
    Fehler = false;
    try {
      int k;
      int[]a = new int[A.length];
      a = double2intVektor(DoubleMatrix.multiply(int2doubleVektor(A),int2doubleMatrix(B)));
      return a;
    }
    catch (IllegalArgumentException e) {
      setzeFehler("Matritzen nicht gleich (m,n)!");
      return (new int[A.length]);
    }   
  }


  public static int[] mult (int[][] A, int[]B) {
    Fehler = false;
    try {
      int k;
      int[]a = new int[A.length];
      a = double2intVektor(DoubleMatrix.multiply(int2doubleMatrix(A),int2doubleVektor(B)));
      return a;
    }
    catch (IllegalArgumentException e) {
      setzeFehler("Matritzen nicht gleich (m,n)!");
      return (new int[A.length]);
    }   
  }
  public static int[][] regulaereMatrix(int m) { // reguläre Matrix bestimmen
    int [][]A = new int[m][m];		// sind mit 0 vorbesetzt!
    int [][]B = new int[m][m];
    int i,j;
    for (i=0; i<m; i++) {    		// Diagonalen besetzen
      A[i][i]=1;
      B[i][i]=1;
    }
    for (j=0; j<(m-1); j++)		// spaltenweise durchgehen
      for (i=j+1; i<m; i++) {
        A[i][j]=A[i-1][j]+m-j-1;
        B[j][i]=A[i][j];		// DreiecksMaterix
      }
    return mult(A,B);			// Fehler kann nicht auftreten!
  }
  public static int[][] Modulo(int[][]A, int m) { // alle Elemente modulo m
    for (int j=0; j<A.length; j++)
      for (int i=0; i<A[0].length; i++) 
        if (A[j][i]<0) A[j][i]+=m;
        else 	       A[j][i]=A[j][i]%m;
    return A;				// Fehler kann nicht auftreten!
  }
}
