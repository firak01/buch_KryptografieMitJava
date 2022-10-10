package basic.zBasic.util.crypt.encode;

import base.files.DateiUtil;
import base.io.IoUtil;

/** Aus "Kryptographie mit Java", Seite 33f
 *  Die Klasse stammt aus der Buch CD, aus dem Verzeichnis poly
 *  Die Verschlusselungsmethode gehoert zu den "polyalphabetischen Ansaetzen"
 *  
 * Erweitert und angepasst
 * @author Fritz Lindhauer, 08.10.2022, 08:29:08
 * 
 */
class Vigenere { 		// Vigenereverschluesselung

  public static void main( String[] arg) {
    String SchluesselWort="HALLO"; //FGL: passend zum Beispiel im Buch
	//String SchluesselWort="SchluesselWort"; //FGL: passend zur Datei Vigenere.txt im poly - Verzeichnis der Begleit CD
	                                        //     ABER: DAS ERGEBNIS WEICHT AB!!!
    DateiUtil Original;
    int c, i, laengeSW;   
   
    int[] s = IoUtil.Unicode(SchluesselWort.getBytes()); //fGL: Die Schlüsselwortbuchstaben
    if (arg.length > 0) {
    	Original = new DateiUtil(arg[0]);
    } else {
    	Original = new DateiUtil("tryout\\basic\\zBasic\\util\\crypt\\encode\\file\\Beispieltext2_ohne_Sonderzeichen.txt");
    	//Original = new DateiUtil("tryout\\basic\\zBasic\\util\\crypt\\encode\\file\\Langer_Beispieltext2_zur_Vigenere_Verschluesselung.txt");
    	//Original = new DateiUtil("tryout\\basic\\zBasic\\util\\crypt\\encode\\file\\Langer_Beispieltext1_ohne_Sonderzeichen.txt");
    }
    
    if (arg.length > 1) {
    	SchluesselWort = (arg[1]); 
    }else {
    	//Nur weil das Schlüsselwort nicht angegeben wurde... ?    System.exit(0);
    }
    laengeSW = SchluesselWort.length();
    
    int[] p = Original.liesUnicode();//FGL: Der Klartextbuchstabe
    System.out.print("Originaltext ausgeben? (J/N): ");
    if (IoUtil.JaNein()) {
      System.out.println("---- Originaltext von: "+DateiUtil.dateiname+" ----");
      for (i=0; i < p.length; i++) {
        IoUtil.printChar(p[i]);	// druckbares Zeichen?
        if (((i+1)%80)==0) System.out.println();
      }
    }
    System.out.println("\n-- Verschluessele Text von: "+DateiUtil.dateiname+" --");
    for (i = 0; i < p.length; i++) {
    	if(i>=1) System.out.print("|");
      //Das steht in der Codedatei
    	//Merke: c = Chiffrebuchstabe
    	int iIndexS = i%laengeSW;
    	int iSum = s[iIndexS]+p[i];
    	int iFormula = (iSum)%256;
      c = iFormula; //FGL: 	Das ist der Mathematische Ansatz: 
      								//		Die Buchstaben wurden durch natuerliche Zahlen ersetzt.
                                    //		Dann fiel eine Gesetzmaessigkeit auf (s. Seite 32 im Buch), die so ausgenutzt wurde.
      System.out.print("i="+c);
      p[i] = c;				// nur wegen abspeichern
      
    }	
    System.out.print("Verschluesselten Text ausgeben? (J/N): ");
    if (IoUtil.JaNein()) {
      System.out.println("\n\n-- Verschluesselter Text von: "+DateiUtil.dateiname+" --");
      for (i = 0; i < p.length; i++) {
    	IoUtil.printCharWithPosition((p[i]),"|");
        if (((i+1)%80)==0) System.out.println();	// neue Zeile
      }
    }
    System.out.println(
                 "\n---- Dateilaenge: "+p.length+" Bytes ----\n ");
    DateiUtil Kodiert = new DateiUtil();
    Kodiert.schreib(p);
    System.exit(0);
  }
}
