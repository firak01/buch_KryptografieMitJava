package basic.zBasic.util.crypt;

import base.files.DateiUtil;
import base.io.IoUtil;

/**Monoalphabetische Decodierung
 * s. Verzeichnis src/mono als Ausgangslage
 * im Buch "Kryptografie mit Java"
 *  
 * 
 * @author Fritz Lindhauer, 04.10.2022, 14:45:19
 * 
 */
class CaesarDecode26 {                      // zaehlt die Buchstaben
  private int[]  h = new int[26];
  private int[] spitze = new int[10];
  private int[] cspitze = new int[10];
  static DateiUtil d;
  int i,j,k,sl, bezug;
  String zKette;
  
  public CaesarDecode26 () {	 	 
    byte[] mtext = d.lies() ;
    for (int i = 0; i < mtext.length; i++ ) {
      byte b = mtext[i];
      System.out.print(b);
      if(b-97>=26 || b-97 < 0) {
    	  //FGL: Das Zeichen ist dann nicht zu zählen
      }else {
    	  h[b-97]++;
      }
      System.out.print((char)mtext[i]);
    }
    System.out.println();
    for (i=0; i < 26; i++) {
      System.out.print((char)(i+97)+":"+IoUtil.intToString(h[i],3)+"   ");
      if (((i+1)%10)==0) System.out.println();
    }
    System.out.print("\n\nErmittele die haeufigsten 10 Buchstaben:");
    for (i=0; i<10; i++) {
      System.out.print(".");
      k=0;
      for (j=1; j<26; j++) 
        if (h[j] > h[k])  
          k=j;
      spitze[i]=h[k];
      cspitze[i]=k;
      h[k]=0;
    }    
    System.out.print("\nEingabe des Bezugsbuchstaben fuer Schluessellaenge: ");
    bezug=(byte)IoUtil.Zeichen()-97;
    System.out.println("\nSchluessellaenge in bezug auf '"+(char)(bezug+97)+
                       "'(Zeichen-Ordinalzahl-Haeufigkeit)");
    for (i=0; i < 10; i++) {
      System.out.print((char)(cspitze[i]+97)+"("
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
    if (arg.length== 0)  d = new DateiUtil("tryout\\basic\\zBasic\\util\\crypt\\Rot13ZZZCrypted.txt");	 
    else                 d = new DateiUtil(arg[0]);
    new CaesarDecode26();
  }
}
