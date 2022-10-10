import java.math.*;
import java.util.*;

public class DES { 		// Zusammenstellung der DES-Tools
  public static byte [][][] SBoxen = new byte [8][4][16];   		// die SBoxen 1..8

  public static void BigIntAusgeben (BigInteger b, int Zeilen, int Spalten) {
    for (int zeile=Zeilen-1; zeile>=0; zeile--) {
      for (int spalte=Spalten-1; spalte>=0; spalte--)
        if (b.testBit(zeile*Spalten+spalte)) 
          System.out.print(1+" ");
        else System.out.print(0+" ");
      System.out.println();
    }
  }
  public static void BigIntAusgeben (BigInteger b,int bits) {
    for (int i=bits-1; i>=0; i--) {
      if (b.testBit(i)) 
        System.out.print(1);
      else System.out.print(0);
      if (i%8 == 0) System.out.print(" ");
    }
    System.out.println();
  }
  public static void BigIntAusgeben (BigInteger b) {
    for (int i=b.bitLength()-1; i>=0; i--)
      if (b.testBit(i)) 
        System.out.print(1);
      else System.out.print(0);
    System.out.println();
  }
  public static int[] BigInt2IntFeld(BigInteger I) {
    int[] B = new int[8];
    long longL=DES.holeHaelfte(I,"<-",64).longValue();	// long nur 64 Bit
    long longR=DES.holeHaelfte(I,"->",64).longValue();	// deswegen aufspalten
    for (int i=0; i<4; i++) {
      B[i] = (int)(longR%256);
      B[i+4] = (int)(longL%256);
      longL = longL/256;
      longR = longR/256;
    }
    return B;
  }
  public static BigInteger BigSchluessel() {       // Schluessel mit BigInteger
    byte zeile, spalte, quersumme;
    BigInteger schluessel = new BigInteger(64,new Random());
    for (zeile=0; zeile<=7; zeile++) {
      quersumme=0;
      for (spalte=0; spalte<=6; spalte++) 
        if (schluessel.testBit(zeile*8+spalte)) 
          quersumme++;
      if ((quersumme%2)==0) 
        schluessel = schluessel.setBit(zeile*8+7);
      else 
        schluessel = schluessel.clearBit(zeile*8+7);
    }
    return schluessel;
  } 
  public static BigInteger initialePermutation (BigInteger P) {
    int bP, bIP;
    BigInteger PIP = new BigInteger("00000000");  // 64 Bit
    for (int zeile=0; zeile<=7; zeile++) 
      for (int spalte=0; spalte<=7; spalte++) {
        bP = zeile*8+spalte;
        bIP = (7-spalte)*8+(zeile*2+1)%9;
        if (P.testBit(bIP))
          PIP = PIP.setBit(bP);
      }
    return PIP;
  }
  public static BigInteger inverseInitialePermutation (BigInteger IP) {
    int bP, bIP;
    BigInteger P = new BigInteger("00000000");  // 64 Bit
    for (int zeile=0; zeile<=7; zeile++) 
      for (int spalte=0; spalte<=7; spalte++) {
        bP = zeile*8+spalte;
        bIP =  ((spalte*5+4)%9)*8+7-zeile;
        if (IP.testBit(bIP))
          P = P.setBit(bP);
      }
    return P;
  }
  public static BigInteger Expand (BigInteger P) {  // 32->48 Bit
    int iP, j;
    BigInteger ExpP = new BigInteger("000000");  // 48 Bit
    for (iP=0; iP<48; iP++) { 
      j = (iP+31-(iP/6)*2)%32;
      if (P.testBit(j))
        ExpP = ExpP.setBit(iP);     
    }
    return ExpP;
  }

  public static BigInteger PC1 (BigInteger Key, String Typ) {  // 64->28 Bit
    int i, j, k;
    BigInteger C = new BigInteger("0000");  // 28 Bit
    if (Typ.equals("C")) 
      k = 28;        // Matrix C - oben
    else 
      k = 60;        // Matrix D - unten
    for (j=0; j<4; j++) { 
      if (Key.testBit(k)) C = C.setBit(j);     
      k-=8;
    }
    if (Typ.equals("C")) i = 61;        // Matrix C - oben
    else                 i = 59;        // Matrix D - unten
    k = i;
    for (j=4; j<28; j++) { 
      if (Key.testBit(k)) C = C.setBit(j);     
      k-=8;
      if (k<0) {
        if (Typ.equals("C")) i++;
        else                 i--; 
        k = i;
      } 
    }
    return C;
  }                   
  public static BigInteger PC2 (BigInteger C, BigInteger D) { // 28,28->48 Bit
//
// D: 0..27  C: 28..47
//
    BigInteger P = new BigInteger("000000");  // 48 Bit
    if (D.testBit(24)) P = P.setBit(0);  if (C.testBit(26)) P = P.setBit(24);     
    if (D.testBit(27)) P = P.setBit(1);  if (C.testBit(15)) P = P.setBit(25);     
    if (D.testBit(20)) P = P.setBit(2);  if (C.testBit(8))  P = P.setBit(26);     
    if (D.testBit(6))  P = P.setBit(3);  if (C.testBit(1))  P = P.setBit(27);
    if (D.testBit(14)) P = P.setBit(4);  if (C.testBit(21)) P = P.setBit(28);     
    if (D.testBit(10)) P = P.setBit(5);  if (C.testBit(12)) P = P.setBit(29);     
    if (D.testBit(3))  P = P.setBit(6);  if (C.testBit(20)) P = P.setBit(30);     
    if (D.testBit(22)) P = P.setBit(7);  if (C.testBit(2))  P = P.setBit(31);     
    if (D.testBit(0))  P = P.setBit(8);  if (C.testBit(24)) P = P.setBit(32);     
    if (D.testBit(17)) P = P.setBit(9);  if (C.testBit(16)) P = P.setBit(33);     
    if (D.testBit(7))  P = P.setBit(10); if (C.testBit(9))  P = P.setBit(34);     
    if (D.testBit(12)) P = P.setBit(11); if (C.testBit(5))  P = P.setBit(35);     
    if (D.testBit(8))  P = P.setBit(12); if (C.testBit(18)) P = P.setBit(36);     
    if (D.testBit(23)) P = P.setBit(13); if (C.testBit(7))  P = P.setBit(37);     
    if (D.testBit(11)) P = P.setBit(14); if (C.testBit(22)) P = P.setBit(38);     
    if (D.testBit(5))  P = P.setBit(15); if (C.testBit(13)) P = P.setBit(39);     
    if (D.testBit(16)) P = P.setBit(16); if (C.testBit(0))  P = P.setBit(40);     
    if (D.testBit(26)) P = P.setBit(17); if (C.testBit(25)) P = P.setBit(41);     
    if (D.testBit(1))  P = P.setBit(18); if (C.testBit(23)) P = P.setBit(42);     
    if (D.testBit(9))  P = P.setBit(19); if (C.testBit(27)) P = P.setBit(43);     
    if (D.testBit(19)) P = P.setBit(20); if (C.testBit(4))  P = P.setBit(44);     
    if (D.testBit(25)) P = P.setBit(21); if (C.testBit(17)) P = P.setBit(45);     
    if (D.testBit(4))  P = P.setBit(22); if (C.testBit(11)) P = P.setBit(46);     
    if (D.testBit(15)) P = P.setBit(23); if (C.testBit(14)) P = P.setBit(47);     
    return P;
  }
  public static BigInteger schiebeBigInt(BigInteger BigI, int Bits, int anzahl, String Richtung) {
//
// das Schieben erfolgt GRUNDS�TZLICH mit Rotation!!! Ohne Rotation kann
// direkt die MEthode der Klasse BigInteger verwendet werden!
//  
    boolean Bit;
    if (Richtung.equals("<-"))
      for (int i=0; i<anzahl; i++) {
        Bit = false;
        if (BigI.testBit(Bits-1))
          Bit = true;
        BigI = BigI.shiftLeft(1);
        if (Bit) BigI=BigI.setBit(0);
      }
    if (Richtung.equals("->")) 
      for (int i=0; i<anzahl; i++) {
        Bit = false;
        if (BigI.testBit(0))
          Bit = true;
        BigI = BigI.shiftRight(1);
        if (Bit) BigI=BigI.setBit(Bits-1);
      }
    return BigI;
  }
  public static BigInteger SBox(BigInteger Arg, byte[][][] SBoxen) {  // 48 Bit->32 Bit
    byte zeile, spalte;
    BigInteger S = new BigInteger("0");   // 32 Bit
    BigInteger dummy = new BigInteger("0");
    for (int i=42; i>=0; i-=6) {
      dummy=BigInteger.valueOf(0);
      spalte=0; zeile=0;
      if (Arg.testBit(i)) zeile+=1;    // Zeilenindex
      if (Arg.testBit(i+5)) zeile+=2;
      if (Arg.testBit(i+1)) spalte+=1;
      if (Arg.testBit(i+2)) spalte+=2;
      if (Arg.testBit(i+3)) spalte+=4;
      if (Arg.testBit(i+4)) spalte+=8;
      dummy = BigInteger.valueOf(SBoxen[7-i/6][zeile][spalte]);
      S = S.add(dummy.shiftLeft((28-(i+i)/3)));
    }
    return S;
  }
  public static BigInteger PFunc (BigInteger C) { // 32-32 Bit
    BigInteger P = new BigInteger("0000");  // 32 Bit
    if (C.testBit(7))  P = P.setBit(0);  if (C.testBit(12)) P = P.setBit(16);    
    if (C.testBit(28)) P = P.setBit(1);  if (C.testBit(1))  P = P.setBit(17);    
    if (C.testBit(21)) P = P.setBit(2);  if (C.testBit(14)) P = P.setBit(18);    
    if (C.testBit(10)) P = P.setBit(3);  if (C.testBit(27)) P = P.setBit(19);
    if (C.testBit(26)) P = P.setBit(4);  if (C.testBit(6))  P = P.setBit(20);    
    if (C.testBit(2))  P = P.setBit(5);  if (C.testBit(9))  P = P.setBit(21);      
    if (C.testBit(19)) P = P.setBit(6);  if (C.testBit(17)) P = P.setBit(22);  
    if (C.testBit(13)) P = P.setBit(7);  if (C.testBit(31)) P = P.setBit(23);   
    if (C.testBit(23)) P = P.setBit(8);  if (C.testBit(15)) P = P.setBit(24);  
    if (C.testBit(29)) P = P.setBit(9);  if (C.testBit(4))  P = P.setBit(25);  
    if (C.testBit(5))  P = P.setBit(10); if (C.testBit(20)) P = P.setBit(26); 
    if (C.testBit(0))  P = P.setBit(11); if (C.testBit(3))  P = P.setBit(27);  
    if (C.testBit(18)) P = P.setBit(12); if (C.testBit(11)) P = P.setBit(28);   
    if (C.testBit(8))  P = P.setBit(13); if (C.testBit(22)) P = P.setBit(29);    
    if (C.testBit(24)) P = P.setBit(14); if (C.testBit(25)) P = P.setBit(30);    
    if (C.testBit(30)) P = P.setBit(15); if (C.testBit(16)) P = P.setBit(31);     
    return P;
  }
  public static BigInteger holeHaelfte(BigInteger BigI, String LR, int Bits) {
    BigInteger haelfte = new BigInteger("0");
    int anfang, ende;
    if (LR.equals("<-")) {
      anfang = Bits/2;
      ende = Bits;
    }
    else {
      anfang = 0;
      ende = Bits/2;
    }
    int j = 0;
    for (int i=anfang; i<ende; i++) {
      if (BigI.testBit(i))
        haelfte = haelfte.setBit(j);
      j++;
    }  
    return haelfte;
  }
//
// S-Boxen einlesen
//
  public static byte[][][] LiesSBoxen(String DateiName) {
    byte [][][] SBoxen = new byte [8][4][16];   		// die SBoxen 1..8
    int spalte=0, zeile=0;
    Datei SBoxDat = new Datei(DateiName);
    byte[] datByte = SBoxDat.liesAsByte();
    String datStr = new String(datByte);
    for (int i=0; i<8; i++)         // SBox-Nr
      for (int j=0; j<4; j++)       // SBox-Zeile
        for (int k=0; k<16; k++) {  // SBox-Spalte
          zeile = datStr.indexOf(" ",spalte);
          SBoxen[i][j][k] = Byte.parseByte(datStr.substring(spalte,zeile));
          spalte = zeile+1;
        }
    return SBoxen;
  }
  public static BigInteger[] BestimmeAlleSchluessel(BigInteger DESSchluessel) {
    BigInteger [] SchluesselFeld = new BigInteger[16];	// alle Schluessel
    BigInteger C = new BigInteger("0000");
    C = PC1(DESSchluessel,"C");    
    BigInteger D = new BigInteger("0000");
    D = PC1(DESSchluessel,"D");    
    BigInteger C1 = new BigInteger("0");
    BigInteger D1 = new BigInteger("0");
    int vi;
    for (int Runde=1; Runde<=16; Runde++) {
      vi = 2;
      if ((Runde<3)||(Runde==9)||(Runde==16))
        vi = 1; 
      C1 = schiebeBigInt(C,28,vi,"<-");
      D1 = schiebeBigInt(D,28,vi,"<-");
      SchluesselFeld[Runde-1] = PC2(C1,D1);     // der Rundenschluessel
      C = C1; D = D1;        			// auf ein Neues!
    } // for (Runde ...
    return SchluesselFeld;
  }  
}
