/* Anwendung der DESKey-Klasse
   Herbert Voss 000919              */

import java.util.*;					// nur fuer Zeitmessung

class DESKey {						// DES-Key Klasse
  protected boolean isWeak(byte[] key) {		// Test for weak keys
    int a = (key[0] & 0xFE) << 8 | (key[1] & 0xFE);
    int b = (key[2] & 0xFE) << 8 | (key[3] & 0xFE);
    int c = (key[4] & 0xFE) << 8 | (key[5] & 0xFE);
    int d = (key[6] & 0xFE) << 8 | (key[7] & 0xFE);
    return (a == 0x0000 || a == 0xFEFE) &&
           (b == 0x0000 || b == 0xFEFE) &&
           (c == 0x0000 || c == 0xFEFE) &&
           (d == 0x0000 || d == 0xFEFE);
  }    
  protected byte[] fixParity(byte[] key) {		// parity-bits setzen
    int b;
    for (int i = 0; i < key.length; i++) {
      b = key[i];					// key zwischenspeichern
      key[i] = (byte)((b&0xFE) | ( 
                      ~((b>>1)^(b>>2)^(b>>3)^(b>>4)^(b>>5)^(b>>6)^(b>>7)) & 0x01));
    }
    return key;
  }
  protected byte[] makeKey() {
    byte[] b = new byte[8];				// 64 Bit
    do {
      for (int i=0; i<8; i++) b[i] = (byte)(Math.random()*256.0f);
      b = fixParity(b);
    } while (isWeak(b));				// solange wie "weich"
    return (b);	
  }
  byte[] Key = makeKey();				// bestimme Schluessel
}

public class DESKeyGenerator {
  public static void main (String[] MyArgs) {		// main-Methode
    Date dat1 = new Date();
    long ms = dat1.getTime();				// millisekunden
    DESKey myKey = new DESKey();			// Konstruktor aufrufen
    if (MyArgs.length>0) {				// Zeittest
      for (int i=0; i<100000; i++) 
        myKey = new DESKey();				// Schluessel bilden
      dat1 = new Date();
      ms = dat1.getTime()-ms;
      System.out.println("100.000 Schluessel in "+ms+" msek bestimmt!");
    }
    else {
      for (int i=0; i<8;i++) {
        for (int j=7; j>=0; j--)
          if ( ((myKey.Key[i]>>>j)&0x01)>0) System.out.print("1 ");
      	  else   		            System.out.print("0 ");
        System.out.println();				// CLRF
      }
    }
  }
}
