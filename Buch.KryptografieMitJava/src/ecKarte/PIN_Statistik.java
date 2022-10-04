import java.math.BigInteger;
import java.util.Random;
import java.awt.*;

class PIN_Statistik extends Frame{
  int[] Ziffern = new int[10];		// Ziffern 0..9
  static int MaxI;
  
  public static void main (String[] args) {
    if (args.length > 0) MaxI = Integer.parseInt(args[0]);
    else                 System.exit(0);
    PIN_Statistik zeigeStatistik = new PIN_Statistik("PIN-Statistik");
    zeigeStatistik.setSize(520,250);
    zeigeStatistik.setVisible(true);
  }
  public void paint (Graphics g) {
    int max = 0, top = this.getHeight(), topD = top-50;
    double RelWert;
    for (int j=0; j<10; j++)
      if (Ziffern[j] > max) max = Ziffern[j]; 
    for (int j=0; j<10; j++) {
      RelWert = (double)Ziffern[j]/(double)max;
      g.setColor(Color.yellow);
      g.fillRect(10+j*50,top-(int)(RelWert*topD),50,(int)(RelWert*topD)); 
      g.setColor(Color.black);
      g.drawRect(10+j*50,top-(int)(RelWert*topD),50,(int)(RelWert*topD)); 
    }
    for (int j=0; j<10; j++) {
      g.drawString(Integer.toString(j),35+j*50,top-20);
      g.drawString(Integer.toString(Ziffern[j]),20+j*50,topD);
      g.drawString(Double.toString(IO.DM((double)Ziffern[j]/(double)MaxI)),25+j*50,topD-20);
    }
    g.drawString(Integer.toString(MaxI),this.getWidth()-50,50);
  }
  
  public PIN_Statistik(String titel) {
    super(titel);
    setBackground(Color.white);
    byte [][][] SBoxen = new byte [8][4][16];   // die SBoxen 1..8
    int index;
    SBoxen = DES.LiesSBoxen("../SBoxen.dat");
    BigInteger DESSchluessel = new BigInteger("01FE07A454C7E3F2",16);
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
      int vi;
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
      String PIN=Wort.toString(16).substring(2,6);
      if (PIN.charAt(0) == '0') PIN = "1"+PIN.substring(1,4);
      for (int i=0; i< PIN.length(); i++) {
        if (PIN.charAt(i) > '9') index = (byte)PIN.charAt(i)- 97;
        else                     index = (byte)PIN.charAt(i)- 48;
        Ziffern[index]++;
      }
    } // end of for ii
    System.out.println("\nStatistik");
    for (int i=0; i<10; i++)
      System.out.println(Ziffern[i]);
  }  
}
