package basic.zBasic.util.crypt;

import base.files.DateiUtil;

/** Caesarverschlüsselung ohne Rotation
 *  Buch "Kryptografie mit Java" Seite 22
 *  	
 * Abgetippt und erweitert/angepasst von mir
 * @author Fritz Lindhauer, 04.10.2022, 14:54:20
 * 
 */
public class Caesar {

	public static void main(String[] args) {
		DateiUtil original;
		int zeichen;
		int schluessel = 3; // Vorgabe
		
		if(args.length > 0) { schluessel = Integer.parseInt(args[0]); }
		if(args.length > 1) { 
			original = new DateiUtil(args[1]);
		}else {
			original = new DateiUtil("tryout\\basic\\zBasic\\util\\crypt\\Beispieltext_ohne_sonderzeichen_klein_ein_wort.txt");
		}
		byte[] geheim = original.lies();
		System.out.println("---- Verschluesseln von: "+DateiUtil.dateiname+"----");
		for(int i = 0 ; i < geheim.length; i++) {
			if((geheim[i] > 31) && (geheim[i] < 127)){
				zeichen = geheim[i] - 32; // auf Space beziehen
				geheim[i] = (byte)(((zeichen+schluessel)%95)+32);
			}
			System.out.println("\n---- Dateilaenge: "+geheim.length+" Bytes ----\n ");
		}
		
		DateiUtil kodiert = new DateiUtil();
		kodiert.schreib(geheim);				
		
	}
}
