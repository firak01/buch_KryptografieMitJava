public final class RC6Demo {
//
// die Anwendung dieses Algorithmus unterliegt den Lizenzbedingungen
// der RSA: http://www.rsa.com
//
  private static final int
    Runden     	    = 20,
    BlockGroesse    = 16,
    KeyLength       = 16;				// 16, 24 oder 32 Byte

  private static final int
    P = 0xB7E15163, Q = 0x9E3779B9;			// allgemeine Konstanten
  private int[] S = new int[2*Runden + 4];		// Teilschluessel 
  private byte[]key = new byte[KeyLength];		// Grundschluessel
  private boolean decrypt;
//-------------------------------------------------------------------------

  public RC6Demo() {					// Constructor	
    byte[] p = new byte[16];
    System.out.println("Klartext (Hex):");
    for (int i=0; i<p.length; i++)
      System.out.print(Hex.byteToString(p[i]));
    System.out.println("\n("+p.length+" Bytes)");
    decrypt = false;
    Init();						// Schlüssel generieren
    System.out.println("Schlüssel: "+Hex.toString(key));
    int AnzahlBloecke = (p.length+BlockGroesse-1)/BlockGroesse; // Anzahl 64 Bit-Blöcke
    byte[] tmp = new byte[AnzahlBloecke*BlockGroesse];
    byte[] out = new byte[AnzahlBloecke*BlockGroesse];
    System.out.println(AnzahlBloecke+" 128-Bit-Blöcke = "+
                       (AnzahlBloecke*BlockGroesse)+" Bytes");
    System.arraycopy(p,0,tmp,0,p.length);		// p nach tmp kopieren
    System.out.println("Starte Verschlüsselung ...");
    for (int i=0; i<AnzahlBloecke; i++) 
      coreCrypt(tmp,i*BlockGroesse,out,i*BlockGroesse);
    System.out.print("Verschlüsselter Text (Hex):");
    for (int i=0; i<out.length; i++)
      System.out.print(Hex.byteToString(out[i]));
    System.out.println();
    System.out.println("Starte Entschlüsselung ...");
    p = out;
    System.out.println("("+p.length+" Bytes)");
    decrypt=true;
    System.arraycopy(p,0,tmp,0,p.length);		// p nach tmp kopieren
    for (int i=0; i<AnzahlBloecke; i++)
      coreCrypt(tmp,i*BlockGroesse,out,i*BlockGroesse);
    System.out.print("Entschlüsselter Text (Hex):\n");
    for (int i=0; i<out.length; i++)
      System.out.print(Hex.byteToString(out[i]));
    System.out.println();
    System.exit(0);
  }
//-------------------------------------------------------------------------
  protected void Init() {
    generateSubKeys();			// Grundschlüssel = 0Hex
  }
//-------------------------------------------------------------------------
  protected final void coreCrypt(byte[] in, int inOffset,
                                   byte[] out, int outOffset) {
    int t, u;
    int[]X = new int[4]; 
    for (int i=0; i<4; i++) 
      X[i] = (in[inOffset++] & 0xFF)       |
             (in[inOffset++] & 0xFF) <<  8 |
             (in[inOffset++] & 0xFF) << 16 |
             (in[inOffset++] & 0xFF) << 24;
    if(decrypt) {
      X[2] -= S[2*Runden+3];
      X[0] -= S[2*Runden+2];
      for(int i=2*Runden+2; i>2; ) {
        t = X[3]; X[3] = X[2]; X[2] = X[1]; X[1] = X[0]; X[0] = t;
        u = rotLinks(X[3]*(2*X[3]+1),5);
        t = rotLinks(X[1]*(2*X[1]+1),5);
        X[2] = rotRechts(X[2]-S[--i],t)^u;
        X[0] = rotRechts(X[0]-S[--i],u)^t;
      }
      X[3] -= S[1]; 
      X[1] -= S[0];
    }
    else {
      X[1] += S[0];
      X[3] += S[1];
      for(int i=1; i<=2*Runden; ) {
        t = rotLinks(X[1]*(2*X[1]+1),5);
        u = rotLinks(X[3]*(2*X[3]+1),5);
        X[0] = rotLinks((X[0]^t),u)+S[++i];
        X[2] = rotLinks((X[2]^u),t)+S[++i];
        t = X[0]; X[0]=X[1]; X[1]=X[2]; X[2]=X[3]; X[3]=t;
      }
      X[0] += S[2*Runden+2];
      X[2] += S[2*Runden+3];
    }
    for (int i=0; i<16; i++)			// Speichern
      out[outOffset++] = (byte)(X[i/4] >>> ((i%4)*8));
  }
//-------------------------------------------------------------------------
  private final void generateSubKeys() {
    int c = KeyLength/4;
    int[] L = new int[c];
    for(int off=0, i=0; i<c; i++)
      L[i] = ((key[off++]&0xFF)) |((key[off++]&0xFF) <<  8) 
         			 |((key[off++]&0xFF) << 16) 
                                 |((key[off++]&0xFF) << 24);
    S[0] = P;
    for(int i=1; i<=(2*Runden+3); i++)
      S[i] = S[i-1] + Q;
    int A=0, B=0, i=0, j=0, v=3*(2*Runden+4);
    for(int s=1; s<=v; s++) {
      A = S[i] = rotLinks( S[i]+A+B, 3 );
      B = L[j] = rotLinks( L[j]+A+B, A+B );
      i = (i+1)%(2*Runden+4);
      j = (j+1)%c;
    }
  }
//-------------------------------------------------------------------------
    private static int rotLinks(int val, int amount) {	// rot links
        return (val << amount) | (val >>> (32-amount));
    }
//-------------------------------------------------------------------------
    private static int rotRechts(int val, int amount) {	// rot rechts
        return (val >>> amount) | (val << (32-amount));
    }
//-------------------------------------------------------------------------
  public static void main (String[] arg) {
    RC6Demo makeRC6Demo = new RC6Demo();
  }
}
