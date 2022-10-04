import java.math.*;
import java.io.*;

class IO {                      // zaehlt die Buchstaben
  static BufferedReader Eingabe = new BufferedReader(new InputStreamReader(System.in));
  static String EingabeZeile;
  
  static public int ganzeZahl() { 
    try { EingabeZeile = Eingabe.readLine(); }
    catch(IOException e) { System.out.println("Fehler bei der Eingabe!"); }
    return Integer.valueOf(EingabeZeile).intValue();
  }
  static public double reelleZahl() { 
    try { EingabeZeile = Eingabe.readLine(); }
    catch(IOException e) { System.out.println("Fehler bei der Eingabe!"); }
    return (double)Float.valueOf(EingabeZeile).floatValue();
  }
  static public char Zeichen() { 
    try { EingabeZeile = Eingabe.readLine(); }
    catch(IOException e) { System.out.println("Fehler bei der Eingabe!"); }
    return EingabeZeile.charAt(0);
  }
  static public char ZeichenUpCase() { 
    try { EingabeZeile = Eingabe.readLine(); }
    catch(IOException e) { System.out.println("Fehler bei der Eingabe!"); }
    return EingabeZeile.toUpperCase().charAt(0);
  }
  static public String Satz() { 
    try { EingabeZeile = Eingabe.readLine(); }
    catch(IOException e) { System.out.println("Fehler bei der Eingabe!"); }
    return EingabeZeile;
  }  
  static public boolean JaNein() { 
    try { EingabeZeile = Eingabe.readLine(); }
    catch(IOException e) { System.out.println("Fehler bei der Eingabe!"); }
    if (EingabeZeile.equals("J")||EingabeZeile.equals("j")) 
      return true;
    else
      return false;
  }    
  static public void printChar(byte zeichen) { 
     int z=(int)zeichen;
     if (z<0) z+=256;
     if (z==10) System.out.println();
     else
       if (((z>31)&&(z<127)) || ((z>160)&&(z<256) ))       
         System.out.print((char)(z));
       else 
         System.out.print(".");
  }  
  static public void printChar(int zeichen) {  // Unicodezeichen
    printChar((byte)zeichen);
  }  
  static int[] Unicode(byte[] ByteFeld) {
    int[] dummy=new int[ByteFeld.length];
    for (int i=0; i<dummy.length; i++) 
      if (ByteFeld[i]>=0)  dummy[i]=ByteFeld[i];
      else                 dummy[i]=256+ByteFeld[i];
    return dummy;
  }
  static int char16 (byte b) {
      if (b>=0) return b;
      else      return (256+b);
  }
  static byte char8 (int i) {
    return (byte)i;
  }
  static public double DM(double d) {
    d = d*100;
    int i = (int)d;
    return (double)i / 100;
  }
  static public String intToString(int i, int laenge, String c) {
    String s=" ";
    s=s.valueOf(i);
    if (laenge > 0)
      while (s.length()<laenge) 
        s=c+s;
    return s;
  }
  static public String intToString(int i, int laenge) {
    String s=" ";
    s=s.valueOf(i);
    while (s.length()<laenge) s=" "+s;
    return s;
  }
  static public String intToString(int i) {
    String s=" ";
    s=s.valueOf(i);
    return s;
  }
  static public String intToHex(int i) {
    String s=" ";
    s=Integer.toHexString(i);
    return s;
  }
  static public String byteToBits(byte i) {
    String s = "";
    for (int j=0; j<8; j++)
      if (((i >>> j) & 1) > 0) s = "1"+s;
      else                    s = "0"+s;
    return s;
  }
  static public String longToString(long i, int laenge, String c) {
    String s=" ";
    s=s.valueOf(i);
    while (s.length()<laenge) 
      s=c+s;
    return s;
  }
  static public String longToString(long i, int laenge) {
    String s=" ";
    s=s.valueOf(i);
    if (laenge>0)
      while (s.length()<laenge) s=" "+s;
    return s;
  }
  static public String longToString(long i) {
    String s=" ";
    s=s.valueOf(i);
    return s;
  }
  static public String bigIntToString(BigInteger i, int laenge, String c) {
    String s=" ";
    s=s.valueOf(i);
    while (s.length()<laenge) 
      s=c+s;
    return s;
  }
  static public String bigIntToString(BigInteger i, int laenge) {
    String s=" ";
    s=s.valueOf(i);
    if (laenge>0)
      while (s.length()<laenge) s=" "+s;
    return s;
  }
  static public String bigIntToString(BigInteger i) {
    String s=" ";
    s=s.valueOf(i);
    return s;
  }
  static public String bigIntToHex(BigInteger I) {
    int[] Wertigkeit = {1,2,4,8};
    int Stellen = I.bitLength()/4;
    if (I.bitLength()%4 > 0) Stellen++;
    String s="";
    int wert;
    for (int j=Stellen; j>0; j--) {
      wert = 0;
      for (int k=0; k<4; k++) 
        if (I.testBit((j-1)*4+k))
          wert = wert+Wertigkeit[k];
      if (wert<10) s = s + (char)(wert+48); 	// 0..9
      else	   s = s + (char)(wert+87);	// a..f
    }  
    return s;
  }
  static public boolean liesInts(int[] A) {;
    String datStr;
    datStr = IO.Satz()+" ";     // Space wegen Umwandlung
    int k=0, l=0;
    for (int i=0; i<A.length; i++) {
      k = datStr.indexOf(" ",l);
      if (k<0) return false;                        // Fehlerhafte Eingabe
      A[i] = Byte.parseByte(datStr.substring(l,k));
      l = k+1;
    }
    return true;
  }
  static public boolean liesDoubles(double[] A) {;
    String datStr;
    datStr = IO.Satz()+" ";     // Space wegen Umwandlung
    int k=0, l=0;
    for (int i=0; i<A.length; i++) {
      k = datStr.indexOf(" ",l);
      if (k<0) return false;                        // Fehlerhafte Eingabe
      A[i] = Double.parseDouble(datStr.substring(l,k));
      l = k+1;
    }
    return true;
  }
  static int[] liesIntsAusDatei(int Anzahl) {
    int[] A = new int[Anzahl];
    Datei D = new Datei();
    String datStr = D.liesString()+" ";
    int k=0, l=0;
    for (int i=0; i<A.length; i++) {
      k = datStr.indexOf(" ",l);
      if (k<0) return A;                        // Fehlerhafte Eingabe
      A[i] = Integer.parseInt(datStr.substring(l,k));
      l = k+1;
    }
    return A;
  }
  static int[][] liesIntsAusDatei(int Zeilen, int Spalten) {
    int[][] A = new int[Zeilen][Spalten];
    int[] a = new int[Zeilen*Spalten];
    a = liesIntsAusDatei(a.length);
    for (int i=0; i<Zeilen; i++) 
      for (int j=0; j<Spalten; j++) 
        A[i][j] = a[i*Zeilen+j];
    return A;
  }
  static double[] liesDoublesAusDatei(int Anzahl) {
    double[] A = new double[Anzahl];
    Datei D = new Datei();
    String datStr = D.liesString();
    int k=0, l=0;
    for (int i=0; i<A.length; i++) {
      k = datStr.indexOf(" ",l);
      if (k<0) return A;                        // Fehlerhafte Eingabe
      A[i] = Double.parseDouble(datStr.substring(l,k));
      l = k+1;
    }
    return A;
  }
  static double[][] liesDoublesAusDatei(int Zeilen, int Spalten) {
    double[][] A = new double[Zeilen][Spalten];
    Datei D = new Datei();
    String datStr = D.liesString();
    int k=0, l=0;
    for (int i=0; i<Zeilen; i++) 
      for (int j=0; j<Spalten; j++) {
        k = datStr.indexOf(" ",l);
        if (k<0) return A;                        // Fehlerhafte Eingabe
        A[i][j] = Double.parseDouble(datStr.substring(l,k));
        l = k+1;
      }
    return A;
  }
  static void schreibeIntsInDatei(int[] A) {
    Datei D = new Datei();
    String datStr="";
    for (int i=0; i<A.length; i++) datStr.concat(String.valueOf(A[i]).concat(" "));
    D.schreib(datStr);
  }
  static void schreibeIntsInDatei(int[][] A) {
    String datStr="";
    for (int i=0; i<A.length; i++) 
      for (int j=0; j<A[0].length; j++) datStr = datStr+String.valueOf(A[i][j])+" ";
    Datei D = new Datei();
    D.schreib(datStr);
  }
  static public void printVektor(int[] A) {
   for (int i=0; i<A.length; i++) System.out.print(A[i]+"\t");
  }
  static public void printVektor(double[] A) {
   for (int i=0; i<A.length; i++) System.out.print(A[i]+"\t");
  }
  static public void printMatrix(int[] A) {
   int Zeilen = (int)Math.sqrt(A.length);
   for (int i=0; i<A.length; i++) {
     System.out.print(DM(A[i])+"\t");
     if ((i+1)%Zeilen==0) System.out.println();
   }
  }
  static public void printMatrix(int[][] A) {
   int Zeilen = A.length;
   for (int i=0; i<Zeilen; i++) {
     for (int j=0; j<Zeilen; j++)
       System.out.print(A[i][j]+"\t");
     System.out.println();
   }
  }
  static public void printMatrix(double[][] A) {
   int Zeilen = A.length;
   for (int i=0; i<Zeilen; i++) {
     for (int j=0; j<Zeilen; j++)
       System.out.print(DM(A[i][j])+"\t");
     System.out.println();
   }
  }
  static public int[] liesVektorB(int Zeilen) {
    int[] B = new int[Zeilen];
    do {} while (!liesInts(B));
    return B;
  }
  static public double[] liesDoubleVektorB(int Zeilen) {
    double[] B = new double[Zeilen];
    do {} while (!liesDoubles(B));
    return B;
  }
  static public int[] liesMatrixA(int Zeilen) {
    int[] A = new int[Zeilen*Zeilen];
    int[] a = new int[Zeilen];
    for (int i=0; i<Zeilen; i++) {
      System.out.print((i+1)+" Zeile: ");
      do {
       } while (!liesInts(a));
      for (int j=i*Zeilen; j<(Zeilen*(i+1)); j++) A[j]=a[j-i*Zeilen];
    }
    return A;
  }
  static public int[][] liesIntMatrix(int Zeilen, int Spalten) {
    int[][] A = new int[Zeilen][Spalten];
    int[] a = new int[Zeilen*Spalten];
    a = liesMatrixA(Zeilen);
    for (int i=0; i<Zeilen; i++)
      for (int j=0; j<Spalten; j++) A[i][j]=a[i*Zeilen+j];
    return A;
  }
  static public int[][] liesMatrixA(int Zeilen, int Spalten) {
    int[][] A = new int[Zeilen][Zeilen];
    int[] a = new int[Zeilen];
    for (int i=0; i<Zeilen; i++) {
      System.out.print((i+1)+" Zeile: ");
      do {
       } while (!liesInts(a));
      for (int j=i*Zeilen; j<(Zeilen*(i+1)); j++) A[i][j]=a[j-i*Zeilen];
    }
    return A;
  }
}
