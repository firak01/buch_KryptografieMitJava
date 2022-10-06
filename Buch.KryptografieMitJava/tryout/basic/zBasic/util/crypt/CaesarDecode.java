package basic.zBasic.util.crypt;

import base.files.DateiUtil;
import base.io.IoUtil;

/**Monoalphabetische Decodierung
 * s. im Buch "Kryptografie mit Java"
 * Seite 27ff
 *  
 * Ist wohl eine Weiterentwicklung von CaesarDecode26.java
 * und geht über den ganzen ASCII Zeichensatz
 * 
 * @author Fritz Lindhauer, 04.10.2022, 14:45:19
 * 
 */
class CaesarDecode {                      // zaehlt die Buchstaben
  private int[]  h = new int[256];
  private int[] spitze = new int[10];
  private int[] cspitze = new int[10];
  static DateiUtil d;
  int i,j,k,sl, bezug;
  String zKette;
  
  public CaesarDecode () {	 	 
    byte[] mtext = d.lies() ;
    for (int i = 0; i < mtext.length; i++ ) {
      byte b = mtext[i];
      System.out.print(b);
      
      //Da der ganze ASCII Bereich verwendet wird, ist hier eine Fallunterscheidung nicht notwendig
//      if(b-97>=26 || b-97 < 0) {
//    	  //FGL: Das Zeichen ist dann nicht zu zählen
//      }else {
//    	  h[b-97]++;
//      }
      
      h[b]++;
      System.out.print((char)mtext[i]);
    }
    System.out.println();
    for (i=32; i < 127; i++) {      
      if (((i+1)%10)==0) System.out.println();
      System.out.print((char)(i)+":"+IoUtil.intToString(h[i],3)+"   ");
    }
    System.out.print("\n\nErmittele die haeufigsten 10 Buchstaben:");
    for (i=0; i<10; i++) {
      System.out.print(".");
      k=0;
      for (j=1; j<256; j++) 
        if (h[j] > h[k])  
          k=j;
      spitze[i]=h[k];
      cspitze[i]=k;
      h[k]=0;
    }    
    System.out.print("\nEingabe des Bezugsbuchstaben fuer Schluessellaenge: ");
    bezug=(byte)IoUtil.Zeichen();
    System.out.println("\nSchluessellaenge in bezug auf '"+(char)(bezug)+
                       "'(Zeichen-Ordinalzahl-Haeufigkeit)");
    for (i=0; i < 10; i++) {
      System.out.print((char)(cspitze[i])+"("
                       +cspitze[i]+")"+": "+spitze[i]+" ");
      sl = cspitze[i]-bezug;
      spitze[i]=sl;
      System.out.println("---> Schluessellaenge= "+sl);
    }
    for (k=0; k<10; k++) {
      System.out.print("\nStarte Entschluesselung mit sl="+spitze[k]+":");
      for (i=0; i<mtext.length; i++) {
      if ((i%80) == 0) System.out.println();
      //FGL 20221005: Mache mal Fehlerbehebung..
      
        //char c = (char)(mtext[i]-spitze[k]-97);
        //System.out.print(c);
      
      	//byte b = (byte)erg;
    	//System.out.print(b);  System.out.println("|");
      
      	//########
      	//int erg = mtext[i]-spitze[k]-97;    //FGL: hier 97 (s. ASCII Tabelle) abzuziehen ist falsch!!!  	      
    	int erg = mtext[i]-spitze[k];
      	//System.out.print(erg); System.out.println("|");           
        IoUtil.printChar(erg);
      	
      	
      }
      System.out.print("\n\nNeuer Versuch (J/N): ");
      zKette=IoUtil.Satz();
      if (zKette.equals("n")||zKette.equals("N")) System.exit(0);
    }
  }
  public static void main( String[] arg) {
    if (arg.length== 0)  d = new DateiUtil("tryout\\basic\\zBasic\\util\\crypt\\CaesarCrypted.txt");	 
    else                 d = new DateiUtil(arg[0]);
    new CaesarDecode();
  }
}