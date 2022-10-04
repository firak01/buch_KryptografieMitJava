import java.math.BigInteger;
import java.util.Random;

class PIN_Demo {
  
  public static void main (String[] args) {
    PIN_Demo zeigeDemo = new PIN_Demo();
  }
  public PIN_Demo() {
    byte [][][] SBoxen = new byte [8][4][16];   // die SBoxen 1..8
    int zeile=0, spalte=0;
    SBoxen = DES.LiesSBoxen("../SBoxen.dat");
//    BigInteger DESSchluessel = new BigInteger("01FE07A454C7E3F2",16);
    BigInteger DESSchluessel = new BigInteger("0101010101010101",16);   // Pool-Key
    String datStr = "1001003829101001";     // 64 Bit;
    System.out.println("\nDES-Schluessel als BigInteger (dezimal): "+DESSchluessel);
    System.out.print(  "(binär) : ");
    DES.BigIntAusgeben(DESSchluessel,64);
    System.out.println("\nIn Matrixform (Bytefolgen)  : ");
    DES.BigIntAusgeben(DESSchluessel,8,8);
    BigInteger C = new BigInteger("0000");
    C = DES.PC1(DESSchluessel,"C");    
    BigInteger D = new BigInteger("0000");
    D = DES.PC1(DESSchluessel,"D");    
    System.out.print("\nEine PIN berechnen\n");
    System.out.print("------------\n");
    BigInteger Wort = new BigInteger(datStr,16);
    BigInteger Links = new BigInteger("0");
    BigInteger Rechts = new BigInteger("0");
    System.out.println("Originalwort als BitFeld:"); 
    DES.BigIntAusgeben(Wort,8,8);
    System.out.println("Originalwort als BitKette:"); 
    DES.BigIntAusgeben(Wort,64);
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
    System.out.println("\nDas Endergebis:");
    System.out.println("In Matrixform:");
    DES.BigIntAusgeben(Wort,8,8);
    System.out.println("Als Bitkette:");
    DES.BigIntAusgeben(Wort,64);
    System.out.println("Als Hexkette:");
    System.out.println(Wort.toString(16));
    System.out.println("3-6. Stelle für PIN");
    String PIN=Wort.toString(16).substring(2,6);
    System.out.println(PIN);
    System.out.println("PIN in korrigierter dezimalisierter Form");
    if (PIN.charAt(0) == '0') PIN = "1"+PIN.substring(1,4);
    for (int i=0; i< PIN.length(); i++)
      if (PIN.charAt(i) > '9')
        System.out.print((char)((byte)(PIN.charAt(i))-49));
      else                 
        System.out.print(PIN.charAt(i));
    System.out.println();
  }  
}
