import java.math.BigInteger;
import java.util.*;
import java.awt.*;

class PIN_Statistik2 extends Frame{
  static int MaxPIN=10000, MaxI;
  int[] PINFeld = new int[MaxPIN];
  int[] PINFeldS = new int[MaxPIN];		// die sortierte Liste	
     						// nur für die Grafik!
  public static void main (String[] args) {
    if (args.length > 0) MaxI = Integer.parseInt(args[0]);
    else                 System.exit(0);
    PIN_Statistik2 zeigeStatistik = new PIN_Statistik2("PIN-Statistik");
    zeigeStatistik.setSize(1000,250);
    zeigeStatistik.setVisible(true);
  }
  public void paint (Graphics g) {
    int max = PINFeldS[0], top = this.getHeight(), topD = top-50;
    double RelWert;
    int step=0;
    for (int j=1000; j<MaxPIN; j+=100) {
      RelWert = (double)PINFeldS[j]/(double)max;
      g.drawLine(step,top,step,top-(int)(RelWert*topD)); 
      if (j%100==0) step++;
    }
    g.drawString(Integer.toString(MaxI),this.getWidth()-50,50);
  }
  
  public PIN_Statistik2(String titel) {
    super(titel);
    setBackground(Color.white);
    byte [][][] SBoxen = new byte [8][4][16];   // die SBoxen 1..8
    int index;
    SBoxen = DES.LiesSBoxen("../SBoxen.dat");
    BigInteger DESSchluessel = new BigInteger("01FE07A454C7E3F2",16); // der Grunddatensatz
    for (int ii=0; ii<MaxI; ii++) {
      if (ii%100 == 0) System.out.print(".");	// optische Kontrolle
      BigInteger C = new BigInteger("0000");
      C = DES.PC1(DESSchluessel,"C");    
      BigInteger D = new BigInteger("0000");
      D = DES.PC1(DESSchluessel,"D");    
      BigInteger Wort = new BigInteger(64, new Random()); // zufällig festlegen
      BigInteger Links = new BigInteger("0");
      BigInteger Rechts = new BigInteger("0");
      Wort=DES.initialePermutation(Wort);  
      Links = DES.holeHaelfte(Wort,"<-",64);
      Rechts = DES.holeHaelfte(Wort,"->",64);
      BigInteger C1 = new BigInteger("0");
      BigInteger D1 = new BigInteger("0");
      BigInteger Ki = new BigInteger("0");
      int vi, PIN, diff;
      for (int Runde=1; Runde<=16; Runde++) {
        vi = 2;
        if ((Runde<3)||(Runde==9)||(Runde==16))
          vi = 1; 
        C1 = DES.schiebeBigInt(C,28,vi,"<-");
        D1 = DES.schiebeBigInt(D,28,vi,"<-");
        Ki = DES.PC2(C1,D1);                   	// der Rundenschluessel
        D = Ki.xor(DES.Expand(Rechts));       	// Exclusiv-Oder
        D = DES.PFunc(DES.SBox(D,SBoxen));  	// Zwischenergebnis
        D = Links.xor(D);            		// Links+f(Rechts,K)
        Links = Rechts;
        Rechts = D;                       	// Runde i erledigt
        C = C1; D = D1;        			// auf ein Neues!
      }
      Rechts = Rechts.shiftLeft(32);
      Wort = DES.inverseInitialePermutation(Rechts.add(Links));
      String PINStr = Wort.toString(16).substring(2,6);
      if (PINStr.charAt(0) == '0') PIN=1000;
      else {
        if (PINStr.charAt(0) > '9') diff=97;	// damits schneller geht
        else                        diff=48;	// alles einzeln
        PIN+=((byte)PINStr.charAt(0)-diff)*1000;
      }
      if (PINStr.charAt(1) > '9') diff=97;
      else                        diff=48;
      PIN+=((byte)PINStr.charAt(1)-diff)*100;
      if (PINStr.charAt(2) > '9') diff=97;
      else                        diff=48;
      PIN+=((byte)PINStr.charAt(2)-diff)*10;
      if (PINStr.charAt(3) > '9') diff=97;
      else                        diff=48;
      PIN+=(byte)PINStr.charAt(3)-diff;
//      System.out.print(PIN+" ");		// reine Kontrolle
      PINFeld[PIN]++;				// PIN hochzählen
    } // end of for ii
    System.out.println("\nErstelle sortierte Liste: ");
    String PINStr = "";
    int PIN=0, vi=0;				// alles sortieren
    for (int m=0; m<MaxPIN; m++) {		// index muss gespeichert bleiben
      index=0;					// darum keine Anwendung von
      if (m%100 == 0) System.out.print(".");	// Array.sort()
      for (int i=1000; i<MaxPIN; i++) {
        if (PINFeld[i]>index) {
          index = PINFeld[i];
          PIN = i;
        }
      }
      PINStr = PINStr + (Integer.toString(PIN)+": "+Integer.toString(index)+"; ");
      PINFeldS[vi++] = PINFeld[index];		// für die Grafik
      PINFeld[PIN] = 0;				// PIN erledigt!
    }
    System.out.println("\nfertig!");      
    Datei p = new Datei();			// sortierte Liste speichern
    p.schreib(PINStr);
  }  
}
