public final class MARS {
  static final int
    BlockSize = 16, 					// Byte eines Datenblocks
    Runden    = 32, 					// Anzahl Runden
    MaxCharProZeile = 30;
  private static final int[] S = new int[128*4];	// Die S-Boxen
  private boolean decrypt;				// Instanzvariablen
  private byte[] key;					// Grundschl�ssel
  private int KeyLength;
  private final int[] K = new int[40];			// Teilschl�ssel
//-------------------------------------------------------------------------

  public MARS(String DatName) {				// Constructor encrypt	
    System.out.println("Lese Klartext ...");
    Datei P;
    byte[] p;
    long ms;						// Millisekunden
    if (DatName.length()>0) {
      P = new Datei(DatName);
      System.out.print("Lese Datei: "+P.dateiname+" ...");
    }
    else P = new Datei();
    ms = -System.currentTimeMillis();
    p = P.liesAsByte(); 	
    ms += System.currentTimeMillis();
    System.out.println(" ben�tigte Zeit: "+(float)ms/1000.0+" sek");
    System.out.println("("+p.length+" Bytes)");
    decrypt = false;
    Init();						// Schl�ssel generieren
    ms = -System.currentTimeMillis();
    System.out.println("Schl�ssel: "+Hex.toString(key));
    int nBlock = (p.length+BlockSize-1)/BlockSize; 	// 128 Bit-Bl�cke
    byte[] tmp = new byte[nBlock*BlockSize];
    byte[] out = new byte[nBlock*BlockSize];
    System.out.println(nBlock+" 128-Bit-Bl�cke = "+(nBlock*BlockSize)+" Bytes");
    System.arraycopy(p,0,tmp,0,p.length);		// p nach tmp kopieren
    System.out.println("Starte Verschl�sselung ...");
    for (int i=0; i<nBlock; i++) 
      coreCrypt(tmp,i*BlockSize,out,i*BlockSize);
    ms += System.currentTimeMillis();;
    System.out.println("Verschl�sselung beendet. \nBen�tigte Zeit: "+
                       (float)ms/1000.0+" sek\nIn Datei speichern ...\u0007");
    P = new Datei();
    P.schreib(out);
    System.out.print("\nVerschl�sselte Datei ausgeben? (J/N):");
    if (IO.JaNein()) 
      for (int i=0; i<out.length; i++) {
        if ((i%MaxCharProZeile)==0) System.out.println();
        System.out.print(Hex.byteToString(out[i]));
      } 
    System.out.println();
    System.exit(0);
  }
//-------------------------------------------------------------------------
  public MARS(byte[] skey, String DatName) {		// Constructor decrypt
    System.out.println("Starte Entschl�sselung ...");
    key = skey;
    Datei P;
    byte[] p;
    long ms;						// Millisekunden
    P = new Datei(DatName);
    System.out.print("Lese Datei: "+P.dateiname+" ...");
    ms = -System.currentTimeMillis();
    p = P.liesAsByte();
    ms += System.currentTimeMillis();
    System.out.println(" ben�tigte Zeit: "+(float)ms/1000.0+" sek");
    System.out.println("("+p.length+" Bytes)");
    decrypt=true;
    ms = -System.currentTimeMillis();
    Init();						// Teilschl�ssel 
    int nBlock = (p.length+BlockSize-1)/BlockSize;	// 28 Bit-Bl�cke
    byte[] tmp = new byte[nBlock*BlockSize];
    byte[] out = new byte[nBlock*BlockSize];
    System.out.println(nBlock+" 128-Bit-Bl�cke = "+
                       (nBlock*BlockSize)+" Bytes");
    System.arraycopy(p,0,tmp,0,p.length);		// p nach tmp kopieren
    for (int i=0; i<nBlock; i++)
      coreCrypt(tmp,i*BlockSize,out,i*BlockSize);
    ms += System.currentTimeMillis();
    System.out.println("Entschl�sselung beendet. \nBen�tigte Zeit: "+
                       (float)ms/1000.0+" sek\nIn Datei speichern ...\u0007");
    P = new Datei();
    P.schreib(out);
    System.out.print("\nEntschl�sselte Datei ausgeben? (J/N):");
    if (IO.JaNein()) 
      for (int i=0; i<out.length; i++)
        System.out.print((char)out[i]); 
    System.out.println();
    System.exit(0);
  }
//-------------------------------------------------------------------------
  protected void Init() {
    liesSBoxen();
    if (!decrypt) { System.out.print("Schl�ssell�nge (128,192,256): ");
      switch (Integer.parseInt(IO.Satz())) {
        case 256: key=new byte[32]; break;
        case 192: key=new byte[24]; break;
        default: key=new byte[16];			// entspricht 128 Bit
      }
      for (int i=0; i<key.length; i++)
        key[i]=(byte)(Math.random()*127.0);
    }
    KeyLength = key.length;
    generateSubKeys();
  }
//-------------------------------------------------------------------------
  private final void liesSBoxen() {
    Datei SBox;
    String Dummy;
    int i,j=0;
    SBox = new Datei("SBoxen.dat");
    System.out.print("Lese SBoxen: "+SBox.dateiname+" ...");
    Dummy=SBox.liesString();				// alles in einen String
    for (i=0; i<128*4; i++) {
      j = Dummy.indexOf("x",j);
      S[i]=(int)Long.parseLong(Dummy.substring(j+1,j+9),16);
      j++;
    }
    System.out.println(" erledigt!");
  }
//-------------------------------------------------------------------------
  private final void generateSubKeys() {
    int n = KeyLength/4;			// int hat vier Byte bei Java
    int[] K = this.K;
    int[] T = new int[15];
    int[] B = { 0xa4a8d57b, 0x5b5d193b, 0xc8a8309b, 0x73f9a978 };
    int i;
    for(i = 0; i < KeyLength; i++)
      T[i/4] |= (key[i] & 0xFF) << (i*8);
    T[i/4] = i/4;
    int j, ii;
    for(j=0; j<4; j++) {
      for(i=0; i<15; i++)
        T[i]^=rotIntLinks(T[(i+8)%15]^T[(i+13)%15],3)^(4*i+j);// lineare Transformation
      for(ii=0; ii<4; ii++)					// 4 Runden "Mixer ..."
        for(i=0; i<15; i++)
          T[i]=rotIntLinks(T[i]+S[T[(i+14)%15]&0x1FF],9);
        for(i=0; i<10; i++)					// Teilschl�ssel speichern
          K[10*j+i]=T[(4*i)%15];
    }
    int m, p, r, w;
    for(i=5; i<=35; i+=2) {
      j = K[i] & 0x3;
      w = K[i] | 0x3;
      m = maskFrom(w);
      r = K[i-1] & 0x1F;
      p = rotIntLinks(B[j], r);
      K[i] = w ^ (p & m);
    }
  }
//-------------------------------------------------------------------------
  private static int maskFrom(int x) {
    int m;
    m = (~x ^ (x >>> 1)) & 0x7fffffff;
    m &= (m >>> 1) & (m >>> 2);
    m &= (m >>> 3) & (m >>> 6);
    m <<= 1;
    m |= (m << 1);
    m |= (m << 2);
    m |= (m << 4);
    return m & 0xfffffffc;
  }
//-------------------------------------------------------------------------
  private final void coreCrypt(byte[] in, int inOffset,
                               byte[] out, int outOffset) {
    int i, t;
    int[] ia;
    int[] X = new int[4];
    for (i=0; i<4; i++) 
      X[i] = (in[inOffset++] & 0xFF)       |
             (in[inOffset++] & 0xFF) <<  8 |
             (in[inOffset++] & 0xFF) << 16 |
             (in[inOffset++] & 0xFF) << 24;
    if (decrypt) {					// Entschl�sseln?
      for (i=0; i<4; i++)				// Ja
        X[i] += K[36+i];				// 1. Ebene: Schl�sseladdition
      for (i = 7; i >= 0; i--) {
        t = X[3]; X[3] = X[2]; X[2] = X[1]; X[1] = X[0]; X[0] = t;
        X[3] ^= S[ X[0] & 0xFF     ]; X[0] = rotIntRechts(X[0],8);
        X[3] += S[(X[0] & 0xFF)+256]; X[0] = rotIntRechts(X[0],8);
        X[2] += S[ X[0] & 0xFF     ]; X[0] = rotIntRechts(X[0],8);
        X[1] ^= S[(X[0] & 0xFF)+256];
        if ((i==2)||(i==6)) X[0] += X[3];
        else
          if ((i==3)||(i==7)) X[0] += X[1];
      } 
      for (i = 15; i >= 0; i--) {
        t = X[3]; X[3] = X[2]; X[2] = X[1]; X[1] = X[0]; X[0] = t;
        X[0] = rotIntRechts(X[0],13); 
        ia = E(X[0],K[2*i+4],K[2*i+5]);			// e-Funktion
        X[2] -= ia[1];
        if (i < 8) {
          X[1] -= ia[0];
          X[3] ^= ia[2]; 
        }
        else {
          X[3] -= ia[0];
          X[1] ^= ia[2];
        }
      }
      for (i = 7; i >= 0; i--) {
        t=X[3]; X[3]=X[2]; X[2]=X[1]; X[1]=X[0]; X[0]=t;
        if ((i==0)||(i==4)) X[0] -= X[3];
        else
          if ((i==1)||(i==5)) X[0] -= X[1];
        
        X[3] ^= S[(X[0] & 0xFF)+256]; X[0] = rotIntLinks(X[0],8);
        X[2] -= S[ X[0] & 0xFF     ]; X[0] = rotIntLinks(X[0],8);
        X[1] -= S[(X[0] & 0xFF)+256]; X[0] = rotIntLinks(X[0],8);
        X[1] ^= S[ X[0] & 0xFF     ];
      } 
      for (i=0; i<4; i++)
        X[i] -= K[i];
    }
    else {						// Verschl�sseln
      for (i=0; i<4; i++)
        X[i] += K[i];					// 1. Ebene: Schl�sseladdition
      for (i = 0; i < 8; i++) {				// 2. Ebene: Vorw�rtsmischen
        X[1] ^= S[ X[0] & 0xFF];        X[0] = rotIntRechts(X[0],8);
        X[1] += S[(X[0] & 0xFF)+256]; X[0] = rotIntRechts(X[0],8);
        X[2] += S[ X[0] & 0xFF];        X[0] = rotIntRechts(X[0],8);
        X[3] ^= S[(X[0] & 0xFF)+256];
        if ((i==0)||(i==4)) X[0] += X[3];
        else
          if ((i==1)||(i==5)) X[0] += X[1];
        t = X[0]; X[0] = X[1]; X[1] = X[2]; X[2] = X[3]; X[3] = t;
      }
      for (i = 0; i < 16; i++) {			// 3. Ebene: Transformation
        ia = E(X[0],K[2*i+4],K[2*i+5]);			// e-Funktion
        X[0] = rotIntLinks(X[0],13);
        X[2] += ia[1];
        if (i < 8) {
          X[1] += ia[0];
          X[3] ^= ia[2];
        }
        else {
          X[3] += ia[0];
          X[1] ^= ia[2];
        }
        t=X[0]; X[0]=X[1]; X[1]=X[2]; X[2]=X[3]; X[3]=t;
      }
      for (i = 0; i < 8; i++) {				// 5. Ebene: R�ckw�rtsmischen
        if ((i==2)||(i==6)) X[0] -= X[3];
        else
          if ((i==3)||(i==7)) X[0] -= X[1];
        X[1] ^= S[256 + (X[0] & 0xFF)]; X[0] = rotIntLinks(X[0],8);
        X[2] -= S[       X[0] & 0xFF ]; X[0] = rotIntLinks(X[0],8);
        X[3] -= S[256 + (X[0] & 0xFF)]; X[0] = rotIntLinks(X[0],8);
        X[3] ^= S[       X[0] & 0xFF ];
        t = X[0]; X[0] = X[1]; X[1] = X[2]; X[2] = X[3]; X[3] = t;
      }
      for (i=0; i<4; i++)
        X[i] -= K[36+i];				// 6. Ebene: Schl�sselsubtraktion
    }
    for (i=0; i<16; i++)				// die Chiffre-der Text
      out[outOffset++] = (byte)(X[i/4] >>> (i%4)*8);
  }
//-------------------------------------------------------------------------
  private static int[] E(int in,int key1,int key2) {	// Expansionsfunktion
    int M = in + key1;
    int R = rotIntLinks(in,13) * key2;
    int L = S[M&0x1FF];					// 8 Bit -> 32 Bit
    R = rotIntLinks(R,5);
    M = rotIntLinks(M,(R&0x1F));			// 5 Bit geben zahl an
    L ^= R;
    R = rotIntLinks(R,5);
    L ^= R;
    L = rotIntLinks(L,(R&0x1F));			// 5 Bit geben zahl an
    return new int[] { L, M, R }; 
  }
//-------------------------------------------------------------------------
    private static int rotIntLinks(int val, int amount) {	// rot links
      return (val << amount) | (val >>> (32-amount));
    }
//-------------------------------------------------------------------------
    private static int rotIntRechts(int val, int amount) {	// rot rechts
      return (val >>> amount) | (val << (32-amount));
    }
//-------------------------------------------------------------------------
  public static void main (String[] arg) {
    MARS makeMARS;
    switch(arg.length) {
      case 0: makeMARS = new MARS("");
              break;
      case 1: makeMARS = new MARS(arg[0]);
              break;
      case 2: makeMARS = new MARS(Hex.fromString(arg[0]),arg[1]); // decrypt
    }
  }
}
