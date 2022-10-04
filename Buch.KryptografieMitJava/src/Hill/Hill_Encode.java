class Hill_Encode {
  static int[] p;
  static int pLaenge;

  static long ggT(long a, long b) { 
    long d,r; 
    do {
      if (a<b) { r=b; b=a; a=r; }
      r = a; d = a-b;
      a = b; b = d;
    } while (d != 0);
    return r;
  }
  public Hill_Encode() {
    System.out.print("Blocklänge: ");
    int BlockL = IO.ganzeZahl();
    int[][] A;
    System.out.print("Matrix A aus Datei einlesen? (J/N): ");
    if (IO.JaNein()) {
      A = IO.liesIntsAusDatei(BlockL,BlockL);
      IO.printMatrix(A);
    }
    else {
      A = Matrix.regulaereMatrix(BlockL);	// Schluesselmatrix A
      IO.printMatrix(A);
      System.out.print("Matrix abspeichern? (J/N)");
      if (IO.JaNein()) IO.schreibeIntsInDatei(A);
    }
    double detA = Matrix.Determinante(A);
    System.out.print("det(A)="+IO.DM(detA));
    long cgd = ggT((int)Math.abs(detA),256);
    System.out.println("  ggT(det(A),256)="+cgd);
    if ( cgd > 1) {
      System.out.println("ggT="+cgd);
      System.exit(0);
    }
    int[] b;
    System.out.print("Vektor b aus Datei einlesen? (J/N): ");
    if (IO.JaNein()) {
      b = IO.liesIntsAusDatei(BlockL);
    }
    else {
      System.out.println("Eingabe des Störvektors b("+BlockL+"):");
      System.out.println("(einzelne Werte durch SPACE getrennt!");
      b = IO.liesVektorB(BlockL);			// Störvektor B
    }
    IO.printVektor(b);
    Chiffriere(A,b);
    Datei C=new Datei();
    C.schreib(p);
    System.out.print("\nChiffrierte Datei ausgeben? (J/N): ");
    if (IO.JaNein()) { 
      for (int i=0; i<p.length; i++) IO.printChar(p[i]);
      System.out.println();
      for (int i=0; i<p.length; i++) System.out.print(p[i]+"\t");
    }
    System.out.println("\nFertig!\n");
    System.exit(0);
  }
//----------------------------------------------------------------
  void Chiffriere(int[][] A,int[] b) {
    int BlockL = b.length;
    int[] c = new int[BlockL];
    for (int i=0; i< pLaenge; i+=BlockL) { 	// gesamten Text durchgehen
      for (int j=0; j<BlockL; j++) 
        c[j]=p[i+j];  				// p-Block zuweisen
      c = Matrix.add(Matrix.mult(A,c),b);	// AxC+b
      for (int j=0; j<BlockL; j++) 
        p[i+j]=c[j]%256;			// Modulo 256
    }
  }
//-----------------------------------------------------------------
  public static void main( String[] arg) {
    Datei P;
    if (arg.length== 0)  P = new Datei();
    else                 P = new Datei(arg[0]);
    System.out.println("Lese Datei ... ");
    p = P.liesUnicode();         		// Text holen
    System.out.println("---- Datei: "+P.dateiname+" ("+p.length+" Bytes) ----");
    pLaenge = p.length;
    Hill_Encode app = new Hill_Encode();
  }
}
