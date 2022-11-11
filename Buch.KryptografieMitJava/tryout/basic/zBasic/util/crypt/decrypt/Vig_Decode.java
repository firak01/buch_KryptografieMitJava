package basic.zBasic.util.crypt.decode;

import base.files.DateiUtil;
import base.io.IoUtil;

class Vig_Decode { 	// Vigenereentschluesselung mit bekanntem Schluesselwort!
  public static void main( String[] arg) {
    String SchluesselWort="HALLO";
	//String SchluesselWort="SchluesselWort";
    DateiUtil Chiffre;
    int p, i, laengeSW;
    
    int[] s = IoUtil.Unicode(SchluesselWort.getBytes());
    if (arg.length > 0) {
    	Chiffre = new DateiUtil(arg[0]); 
    } else {
    	//Chiffre = new Datei();
    	Chiffre = new DateiUtil("tryout\\basic\\zBasic\\util\\crypt\\decode\\file\\VigenereCrypted0_Beispieltext2_schluesselwort_HALLO.txt");
    	//Chiffre = new DateiUtil("tryout\\basic\\zBasic\\util\\crypt\\decode\\file\\VigenereCrypted1_LangerBeispieltext1_schluesselwort_HALLO.txt"); 
    	//Chiffre = new DateiUtil("tryout\\basic\\zBasic\\util\\crypt\\decode\\file\\VigenereCrypted3_LangerBeispieltext2_schluesselwort_SchluesselWort");
    }
    
    if (arg.length > 1) {
    	SchluesselWort = (arg[1]);
    }else{
    	//System.exit(0);//FGL: Nicht beenden, Defaultwert nehmen...
    }
    laengeSW = SchluesselWort.length();
    
    System.out.println("Datei einlesen ...");
    int[] c = Chiffre.liesAsInt(); //FGL: Fehlerkorrektur... das ist ja nicht als Unicode in die Datei geschrieben worden...  Chiffre.liesUnicode();	// Datei einlesen
    for(i=0; i < c.length; i++) {
    	int i2 = c[i];
    	IoUtil.printChar(i2);
    }
    
    
    System.out.print("\nChiffrierte Datei ausgeben? (J/N): ");
    if (IoUtil.JaNein()) {
      System.out.println("---- Chiffretext von: "+DateiUtil.dateiname+" ----");
      for (i=0; i < c.length; i++) {
        IoUtil.printCharWithPosition(c[i],"|");
        if (((i+1)%80)==0) System.out.println(); 	// neue Zeile
      }
    }
    System.out.println("\nBeginne Entschluesselung ... ");
    for (i=0; i<c.length; i++) {
      p = c[i]-s[i%laengeSW];			// c-s
      if (p < 0) p+=256;
      c[i] = (byte) p; 				// wegen Abspeichern von P
    }
    System.out.print("\nOriginal-Datei ausgeben? (J/N): ");
    if (IoUtil.JaNein()) {
      System.out.println("\n\n-- Originaltext von: "+DateiUtil.dateiname+" --");
      for (i=0; i<c.length; i++) {
        IoUtil.printCharWithPosition(c[i],"|");
        if (((i+1)%80)==0) System.out.println();	// neue Zeile
      }
      System.out.println("\n---- Dateilaenge: "+c.length+" Bytes ----\n ");
    }
    DateiUtil Original = new DateiUtil();
    Original.schreib(c);
    System.exit(0);
  }
}