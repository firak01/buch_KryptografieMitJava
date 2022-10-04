class detBestimmen {  // Determinante bestimmen   java detBestimmen <m>
  public static void main (String[] args) {
    int m=Integer.parseInt(args[0]);
    int[][] A = new int[m][m];
    A=Matrix.regulaereMatrix(m);
    IO.printMatrix(A);
    System.out.println("det(A)="+Matrix.Determinante(A));
    System.out.print("Matrix abspeichern? (J/N)");
    if (IO.JaNein()) IO.schreibeIntsInDatei(A);
    System.out.println("\n\nInverse Matrix");
    IO.printMatrix(Matrix.Inverse(A));
    System.out.println("\n\nA*A");
    IO.printMatrix(Matrix.mult(A,A));
    System.out.println("\n\nIvolutorische Matrix");
    int[][]B = new int[m][m];
    for (int i=0; i<B.length; i++)  
      if ((i%2)==0) B[i][i]=1;
      else          B[i][i]=-1;
    IO.printMatrix(B);
    System.out.println("A*(1)*AInv");
    A = Matrix.mult(Matrix.mult(A,B),Matrix.Inverse(A));
    IO.printMatrix(A);
    B = Matrix.Modulo(A,256);
    System.out.println("A mod 256");
    IO.printMatrix(B);
    System.out.println("det(A)="+Matrix.Determinante(B));
    System.out.println("det(AInv)");
    IO.printMatrix(Matrix.int2doubleMatrix(Matrix.Inverse(A)));
    int[][]C = new int[3][3];
    C[0][0]=31;
    C[0][1]=-30;
    C[0][2]=12;
    C[1][0]=100;
    C[1][1]=-99;
    C[1][2]=40;
    C[2][0]=170;
    C[2][1]=-190;
    C[2][2]=69;
    System.out.println("\nMatrix C");
    IO.printMatrix(C);
    System.out.println("det(C)="+Matrix.Determinante(C));
    System.out.println("\n\nInverse Matrix");
    IO.printMatrix(Matrix.Inverse(C));
    System.out.println("det(C)="+Matrix.Determinante(Matrix.Inverse(C)));
    System.exit(0);
  }
}
