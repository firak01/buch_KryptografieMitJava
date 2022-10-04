class Hill_Decode_D {
  static int[] c;
  static int cLaenge;

  public Hill_Decode_D() {
    System.out.print("Blocklänge: ");
    int BlockL = IO.ganzeZahl();
    int[][] A=new int[BlockL][BlockL];
    System.out.print("Matrix A aus Datei einlesen? (J/N): ");
    if (IO.JaNein()) {
      A = IO.liesIntsAusDatei(BlockL,BlockL);
      IO.printMatrix(A);
    }
    else {
      System.out.println("Eingabe der Schluesselmatrix A("+BlockL+","+BlockL+"):");
      System.out.println("(Eingabe zeilenweise und einzelne Werte durch SPACE getrennt!");
      A = IO.liesMatrixA(BlockL,BlockL);			// Schluesselmatrix A
    }
    System.out.println("Bestimme inverse Matrix ... ");
    double[][] AInv = new double[BlockL][BlockL];
    AInv=Matrix.Inverse(Matrix.int2doubleMatrix(A));
    if (!Matrix.Fehler) {
      System.out.println("Inverse Matrix:");
      IO.printMatrix(AInv); 
    }
    else {
      System.out.println(Matrix.Fehlermeldung);
      System.exit(0);
    }
    double[] b;
    System.out.print("Vektor b aus Datei einlesen? (J/N): ");
    if (IO.JaNein()) {
      b = IO.liesDoublesAusDatei(BlockL);
    }
    else {
      System.out.println("Eingabe des Störvektors b("+BlockL+"):");
      System.out.println("(einzelne Werte durch SPACE getrennt!");
      b = IO.liesDoubleVektorB(BlockL);			// Störvektor B
    }
    IO.printVektor(b);
    Dechiffriere(AInv,b);
    int[]P = new int[cLaenge];
    Datei PD=new Datei();
    PD.schreib(c);
    System.out.print("\nDechiffrierte Datei ausgeben? (J/N): ");
    if (IO.JaNein()) {
      for (int i=0; i<c.length; i++) IO.printChar(c[i]);
      System.out.println();
      for (int i=0; i<c.length; i++) System.out.print(c[i]+"\t");
    }
    System.out.println("\nFertig!\n");
    System.exit(0);
  }
//------------------------------------------------------------------------
  void Dechiffriere(double[][] a,double[] b) {
    int BlockL = b.length;
    int[] p = new int[BlockL];
    int[] cBlock = new int[BlockL];
    for (int i=0; i< cLaenge; i+=BlockL) {
      for (int j=0; j<BlockL; j++) cBlock[j] = c[i+j]; 
  System.out.println("\ncBlock="); IO.printVektor(cBlock);

      p = Matrix.double2intVektor(Matrix.mult(Matrix.sub(Matrix.int2doubleVektor(cBlock),b),a));
//      p = Matrix.mult(cBlock,a);
  System.out.println("\np="); IO.printVektor(p);
      
      for (int j=0; j<BlockL; j++) c[i+j] = p[j]%256;
  System.out.println("\nc="); IO.printVektor(c);
    }
  }
//------------------------------------------------------------------------
  public static void main( String[] arg) {
    Datei C;
    if (arg.length== 0)  C = new Datei();
    else                 C = new Datei(arg[0]);
    System.out.println("Lese Datei ... ");
    c = C.liesUnicode();         		// Text holen
    cLaenge = c.length;
    System.out.println("---- Datei: "+C.dateiname+
         	" ("+cLaenge+" Bytes) ----");
    Hill_Decode_D app = new Hill_Decode_D();
  }
}
