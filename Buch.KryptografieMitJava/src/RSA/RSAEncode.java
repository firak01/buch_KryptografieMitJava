import java.io.*;
import java.math.*;
import java.awt.*;

public class RSAEncode {
  static int LaufVariable=0, ByteLaenge=12; // Blockgroesse (Zeichen pro Zeile)
  static Datei Text, Chiffre; 
  static String e, n;
  boolean ende=false;
  byte[] oText;

  public BigInteger holeZeichen() {
    String szahl=""; 
    int zeichen;
    for (long i=0; ((i<ByteLaenge)&&(!ende));i++) {
      System.out.print((char)oText[LaufVariable]);  // Original ASCII-Zeichen
      szahl=szahl+Eingabe.intToString(oText[LaufVariable],3,"0");
      LaufVariable++;
      if (LaufVariable==oText.length) ende=true;
    }  
    while (szahl.length()<(ByteLaenge*3))
      szahl="0"+szahl;     // mit Nullen auff�llen (reine Optik)
    System.out.print(": --> "+szahl);    // zur Kontrolle ausgeben
    return new BigInteger(szahl);
  }
  public RSAEncode () { 
    String GesamtStr="", neuStr="";
    BigInteger BigOriginal, BigChiffriert=BigInteger.ZERO;
    BigInteger eBig=new BigInteger(e);
    BigInteger nBig=new BigInteger(n);
    oText = Text.liesAsByte() ;
    for (int i=0; i < oText.length; i++ ) // Original ausgeben
      System.out.print((char)oText[i]);     // reine Kontrolle!
    System.out.println("\n");
    while (!ende) {      
      BigOriginal=holeZeichen();
      BigChiffriert=BigOriginal.modPow(eBig,nBig);
      neuStr=Eingabe.bigIntToString(BigChiffriert,n.length(),"0");
      System.out.println("    --> kodiert: "+neuStr);
      GesamtStr=GesamtStr+neuStr;
    }
    System.out.println();
    Chiffre=new Datei();
    if (!Chiffre.schreib(GesamtStr.getBytes()))
      System.out.println("Konnte Datei nicht eroeffnen!");
  }
  public static void main( String[] arg) {
    if (arg.length<2) { System.exit(0); } 
    e = arg[0];
    n = arg[1]; 
    if (arg.length<3)  Text = new Datei();
    else               Text = new Datei(arg[2]);
    new RSAEncode();
  }
}
