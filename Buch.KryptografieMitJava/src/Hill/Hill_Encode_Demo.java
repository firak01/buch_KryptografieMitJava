import VisualNumerics.math.*;

class Hill_Encode_Demo {
  static int[] p = {11,18,25,15,20};
  static int pLaenge=5;

  public Hill_Encode_Demo() {
    int BlockL = 5;
    int[][] A = {{1,0,0,0,1}, 
                 {0,1,1,0,0},
		 {1,0,1,1,0},
		 {1,1,0,1,1},
		 {0,1,0,0,1}};
    System.out.println("Klartext:");
    for (int i=0; i<p.length; i++) IO.printChar(p[i]+65);
    System.out.println();
    IO.printVektor(p);
    System.out.println();
    System.out.println("Matrix A:");
    IO.printMatrix(A);
    int[] b={0,1,3,2,1};
    System.out.println("Vektor b:");
    IO.printVektor(b);
    Chiffriere(A,b);
    System.out.println();
    for (int i=0; i<p.length; i++) IO.printChar(p[i]+65);
    System.out.println();
    for (int i=0; i<p.length; i++) System.out.print(p[i]+"\t");
    System.out.println("\nFertig!\n");
    System.exit(0);
  }
//------------------------------------------------------------------------
  void Chiffriere(int[][] A,int[] b) {
    int BlockL = b.length;
    int[] c = new int[BlockL];
    for (int i=0; i< pLaenge; i+=BlockL) { 		// den gesamten Text durchgehen
      for (int j=0; j<BlockL; j++) 
        c[j]=p[i+j];  					// p-Block zuweisen
      c = Matrix.add(Matrix.mult(A,c),b);		// AxC+b
      for (int j=0; j<BlockL; j++) 
        p[i+j]=c[j]%26;					// Modulo 26
    }
  }
//------------------------------------------------------------------------
  public static void main( String[] arg) {
    Hill_Encode_Demo app = new Hill_Encode_Demo();
  }
}
