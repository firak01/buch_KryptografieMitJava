import java.io.*; 
import java.math.*; 
import java.awt.*;

public class RSADecode {
  static Datei Chiffre, Text; static String d,n;
  int LaufVariable=0; boolean ende=false;
  public byte[] cText;
  BigInteger holeZeichen() {
    String szahl="";
    for (int i=0; (i<n.length()); i++) {
      szahl=szahl+(char)cText[LaufVariable];
      LaufVariable++;
      if (LaufVariable>=cText.length) ende=true;
    } 
    return new BigInteger(szahl);
  }
  public RSADecode () {
    String original, GesamtText="";
    int Lauf; char ASCII;
    BigInteger dBig=new BigInteger(d); BigInteger nBig=new BigInteger(n);    
    BigInteger BigOriginal, BigChiffriert;
    cText = Chiffre.lies() ;
    for (int i = 0; i < cText.length; i++ ) // Chiffre ausgeben
      System.out.print((char)cText[i]);
    System.out.println("\n");
    while (!ende) {      
      BigChiffriert=holeZeichen();
      System.out.print(BigChiffriert+":\t");
      original=BigChiffriert.modPow(dBig,nBig).toString();
      while ((original.length()%3)>0)    // Fuehrende Nullen erzeugen
        original="0"+original;
      System.out.print(original+":  ");
      Lauf = 0;       // erstes Zeichen aus "original" bestimmen
      while (Lauf<original.length()) {
        ASCII=(char)Integer.parseInt(original.substring(Lauf,Lauf+3));
        GesamtText=GesamtText+ASCII;
        System.out.print(ASCII);
        Lauf+=3;
      }
      System.out.println();
    }
    Text = new Datei();    // neue Ausgabedatei eroeffnen
    if (!Text.schreib(GesamtText.getBytes()))  // alles in eine Datei
      System.out.println("Konnte nicht schreiben!");
  }
  public static void main( String[] arg) {
    if (arg.length<2) { System.exit(0); } 
    d = arg[0];    n = arg[1]; 
    if (arg.length<3)  Chiffre = new Datei();
    else               Chiffre = new Datei(arg[2]);
    new RSADecode();
  }
}