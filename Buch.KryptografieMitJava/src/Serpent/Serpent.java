public final class Serpent {
  private static final int
    BlockGroesse = 16, 					// 128 Bits im Datenblock
    Runden = 32, 					// Anzahl Runden
    PHI = 0x9E3779B9; 					// (sqrt(5)-1) * 2**31
    private boolean decrypt;
    private byte []key;
    private int[] K=new int[4*(Runden+1)];
    private byte[][] Sbox = new byte[8][16];
//------------------------------------------------------------------------
  public Serpent(String DatName) {			// Constructor encrypt	
    Datei P;
    if (DatName.length()>0) P = new Datei(DatName);
    else		    P = new Datei();
    System.out.print("Lese Klartext ... ");
    long ms = -System.currentTimeMillis();
    byte[]p = P.liesAsByte(); 	
    System.out.println(P.dateiname);
    ms += System.currentTimeMillis();
    System.out.println(" ben�tigte Zeit: "+(float)ms/1000.0+" sek");
    System.out.println(p.length+" Bytes)");
    decrypt = false;
    System.out.println("Starte Verschl�sselung ... ");
    Init(null );					// Schl�ssel generieren
    int AnzahlBloecke = (p.length+BlockGroesse-1)/BlockGroesse;
    byte[] tmp = new byte[AnzahlBloecke*BlockGroesse];
    byte[] out = new byte[AnzahlBloecke*BlockGroesse];
    System.out.println(AnzahlBloecke+" 128-Bit-Bl�cke = "+
                      (AnzahlBloecke*BlockGroesse)+" Bytes");
    System.arraycopy(p,0,tmp,0,p.length);		// p nach tmp kopieren
    ms = -System.currentTimeMillis();
    for (int i=0; i<AnzahlBloecke; i++) 
      blockEncrypt(tmp,i*BlockGroesse,out,i*BlockGroesse);
    ms += System.currentTimeMillis();;
    System.out.println("Verschl�sselung beendet. \nBen�tigte Zeit: "+
                 (float)ms/1000.0+" sek\nIn Datei speichern ... ");
    P = new Datei();
    P.schreib(out);
    System.out.print("\nVerschl�sselte Datei ausgeben? (J/N):");
    if (IO.JaNein()) 
      for (int i=0; i<out.length; i++)
        System.out.print(Hex.byteToString(out[i]));
    System.out.println();
    System.exit(0);
  }
//-------------------------------------------------------------------------
  public Serpent(byte[] key, String DatName) {		// Constructor decrypt
    Datei P;
    P = new Datei(DatName);
    System.out.print("Lese Datei: "+P.dateiname+" ...");
    long ms = -System.currentTimeMillis();
    byte[] p = P.liesAsByte(); 	
    ms += System.currentTimeMillis();
    System.out.println("("+p.length+" Bytes)");
    System.out.println(" ben�tigte Zeit: "+(float)ms/1000.0+" sek");
    decrypt=true;
    System.out.println("Starte Entschl�sselung ...");
    Init(key);						// Teilschl�ssel 
    int AnzahlBloecke = (p.length+BlockGroesse-1)/BlockGroesse;	// 128 Bit-Bl�cke
    byte[] tmp = new byte[AnzahlBloecke*BlockGroesse];
    byte[] out = new byte[AnzahlBloecke*BlockGroesse];
    System.out.println(AnzahlBloecke+" 128-Bit-Bl�cke = "+
                 (AnzahlBloecke*BlockGroesse)+" Bytes");
    System.arraycopy(p,0,tmp,0,p.length);		// p nach tmp kopieren
    ms = -System.currentTimeMillis();
    for (int i=0; i<AnzahlBloecke; i++)
      blockDecrypt(tmp,i*BlockGroesse,out,i*BlockGroesse);
    ms += System.currentTimeMillis();
    System.out.println("Entschl�sselung beendet. \nBen�tigte Zeit: "+
                       (float)ms/1000.0+" sek\nIn Datei speichern ...");
    P = new Datei();
    P.schreib(out);
    System.out.print("\nEntschl�sselte Datei ausgeben? (J/N):");
    if (IO.JaNein()) 
      for (int i=0; i<out.length; i++)
        IO.printChar(out[i]);
    System.out.println();
    System.exit(0);
  }
//-------------------------------------------------------------------------
  private int getNibble (int x,int i) {  return (x >>> (4 * i)) & 0x0F; }
//-------------------------------------------------------------------------
  protected void Init(byte[] key) {
    System.out.print("Lese SBoxen ... ");
    final int[] SInt = new int[] { 
//  jeweils 8 Nibble in einer Int
      0x38f1a65b,0xed42709c,0xfc27905a,0x1be86d34,
      0x86793Caf,0xd1e40b52,0x0fb8c963,0xd124a75e,
      0x1f83c0b6,0x254a9e7d,0xf52b4a9c,0x03e8d671,
      0x72c5846b,0xe91fd3a0,0x1df0e82b,0x74ca9356,
    };
    int zeile=0,spalte=0;
    for (int i=0; i<16; i+=2)
      for (int j=15; j>=0; j--) { 
        Sbox[i/2][15-j]=(byte)getNibble(SInt[i+(15-j)/8],j%8);
      }
/*    // nur f�r eine Kontrollausgabe
    System.out.println("------------------ Die S-Boxen ----------------");
    for (int i=0; i<8; i++) { 
      for (int j=0; j<16; j++)
        System.out.print(Hex.byteToString(Sbox[i][j])+" ");
      System.out.println();
    }
*/
    System.out.println("fertig!");        
    if (!decrypt) {
      System.out.print("Schl�ssell�nge (128,192,256): ");
      switch (Integer.parseInt(IO.Satz())) {
        case 256: key=new byte[32]; break;
        case 192: key=new byte[24]; break;
        default: key=new byte[16];		// entspricht 128 Bit
      }
      for (int i=0; i<key.length; i++) key[i]=(byte)(Math.random()*127);
    }
    System.out.println("Schl�ssel: "+Hex.toString(key));
    generateSubKeys(key);
  }
//-------------------------------------------------------------------------
  private final void generateSubKeys(byte[] key) {
    int[] w = new int[4*(Runden+1)];
    int limit  = key.length / 4;
    int offset = key.length - 1;
    int i, j, t, z;
    for (i = 0; i < limit; i++)
      w[i]= (key[offset--]&(byte)0xFF)     |(key[offset--]&(byte)0xFF)<< 8 |
             (key[offset--]&(byte)0xFF)<<16 |(key[offset--]&(byte)0xFF)<<24;
    if (i < 8)
      w[i++]= 1;				// eine "1" anhaengen
    for (i=8, j=0; i<16; i++) {
      t = w[j] ^ w[i-5] ^ w[i-3] ^ w[i-1] ^ PHI ^ j++;
      w[i]= t << 11 | t >>> 21;
    }
    for (i = 0, j = 8; i < 8; )  		// translate the buffer by -8
      w[i++]= w[j++];
    limit = 4 * (Runden + 1); 			// 132 for a 32-round Serpent
    for ( ; i < limit; i++) {
      t = w[i-8] ^ w[i-5] ^ w[i-3] ^ w[i-1] ^ PHI ^ i;
      w[i]= t << 11 | t >>> 21;
    }
    int[] x=new int[4], y=new int[4];
    byte[] sb;
    for (i=0; i<Runden+1; i++) {
      for (int k=0; k<4; k++) 
        x[k]= w[4*i+k];
      y[0]=y[1]=y[2]=y[3]=0;
      sb = Sbox[(Runden+3-i) % 8];		// modulo 8
      for (j = 0; j < 32; j++) {
        z = sb[((x[0]>>>j)&0x01)    |((x[1]>>>j)&0x01)<< 1 |
               ((x[2]>>>j)&0x01)<<2 |((x[3]>>>j)&0x01)<<3]  ;
        for (int k=0; k<4; k++) 
          y[k] |= ((z >>> k) & 0x01) << j;
      }
      for (int k=0; k<4; k++) 
        w[4*i+k]= y[k];
    }
    K=w;
  }
//-------------------------------------------------------------------------
  protected void LinTransE(int[]X,int[]Y) {
    X[0]= (Y[0] << 13) | (Y[0] >>> 19);
    X[2]= (Y[2] << 3) | (Y[2] >>> 29);
    X[1]= Y[1] ^ X[0] ^ X[2];
    X[3]= Y[3] ^ X[2] ^ (X[0] << 3);
    X[1]= (X[1] << 1) | (X[1] >>> 31);
    X[3]= (X[3] << 7) | (X[3] >>> 25);
    X[0]= X[0] ^ X[1] ^ X[3];
    X[2]= X[2] ^ X[3] ^ (X[1] << 7);
    X[0]= (X[0] << 5) | (X[0] >>> 27);
    X[2]= (X[2] << 22) | (X[2] >>> 10);
  }
//-------------------------------------------------------------------------
  private final void blockEncrypt(byte[] in, int inOffset,
                                  byte[] out, int outOffset) {
    int[]X=new int[4], Y=new int[4];
    for (int k=3; k>=0; k--) 
      X[k]= (in[inOffset++]&0xFF)<<24 | (in[inOffset++]&0xFF)<<16 |
             (in[inOffset++]&0xFF)<< 8 | (in[inOffset++]&0xFF)      ;
    int t00, t01, t02, t03, t04, t05, t06, t07, t08, t09, t10;
    int t11, t12, t13, t14, t15, t16, t17, t18, t19;
    int z, idxK = 0;
    for(int i=0; i<4; i++){
      for (int k=0; k<4; k++) 
        X[k] ^=  K[idxK++];
// S0:  
      t01 = X[1] ^ X[2]; t02 = X[0] | X[3]; t03 = X[0] ^ X[1]; Y[3]= t02 ^ t01;
      t05 = X[2] | Y[3]; t06 = X[0] ^ X[3]; t07 = X[1] | X[2]; t08 = X[3] & t05;
      t09 = t03 & t07; Y[2]= t09 ^ t08; t11 = t09 & Y[2]; t12 = X[2] ^ X[3];
      t13 = t07 ^ t11; t14 = X[1]  & t06; t15 = t06 ^ t13; Y[0]= ~t15;
      t17 = Y[0] ^ t14; Y[1]= t12 ^ t17;
      LinTransE(X,Y);
      for (int k=0; k<4; k++) 
        X[k] ^=  K[idxK++];
// S1:
      t01 = X[0] | X[3]; t02 = X[2] ^ X[3]; t03 = ~ X[1]; t04 = X[0] ^ X[2];
      t05 = X[0] | t03; t06 = X[3] & t04; t07 = t01 & t02; t08 = X[1] | t06;
      Y[2]  = t02 ^ t05; t10 = t07 ^ t08; t11 = t01 ^ t10; t12 = Y[2] ^ t11;
      t13 = X[1]  & X[3]; Y[3]= ~ t10; Y[1]= t13 ^ t12; t16 = t10 | Y[1];
      t17 = t05 & t16; Y[0]= X[2]  ^ t17;
      LinTransE(X,Y);
      for (int k=0; k<4; k++) 
        X[k] ^=  K[idxK++];
// S2:
      t01 = X[0] | X[2]; t02 = X[0] ^ X[1]; t03 = X[3] ^ t01; Y[0]= t02 ^ t03;
      t05 = X[2] ^ Y[0]; t06 = X[1] ^ t05; t07 = X[1] | t05; t08 = t01 & t06;
      t09 = t03 ^ t07; t10 = t02 | t09; Y[1]= t10 ^ t08; t12 = X[0]  | X[3];
      t13 = t09 ^ Y[1]; t14 = X[1] ^ t13; Y[3]= ~t09; Y[2]= t12 ^ t14;
      LinTransE(X,Y);
      for (int k=0; k<4; k++) 
        X[k] ^=  K[idxK++];
// S3:
      t01 = X[0] ^ X[2]; t02 = X[0] | X[3]; t03 = X[0] & X[3]; t04 = t01 & t02;
      t05 = X[1] | t03; t06 = X[0] & X[1]; t07 = X[3]  ^ t04;  t08 = X[2] | t06;
      t09 = X[1] ^ t07; t10 = X[3] & t05; t11 = t02 ^ t10; Y[3]= t08 ^ t09;
      t13 = X[3] | Y[3]; t14 = X[0]  | t07; t15 = X[1]  & t13; Y[2]= t08 ^ t11;
      Y[0]= t14 ^ t15; Y[1]= t05 ^ t04;
      LinTransE(X,Y);
      for (int k=0; k<4; k++) 
        X[k] ^=  K[idxK++];
// S4:
      t01 = X[0] | X[1]; t02 = X[1] | X[2]; t03 = X[0] ^ t02; t04 = X[1] ^ X[3];
      t05 = X[3] | t03; t06 = X[3] & t01; Y[3]= t03 ^ t06; t08 = Y[3] & t04;
      t09 = t04 & t05; t10 = X[2] ^ t06; t11 = X[1] & X[2]; t12 = t04 ^ t08;
      t13 = t11 | t03; t14 = t10 ^ t09; t15 = X[0] & t05; t16 = t11 | t12;
      Y[2]= t13 ^ t08; Y[1]= t15 ^ t16; Y[0]= ~t14;         
       LinTransE(X,Y);
      for (int k=0; k<4; k++) 
        X[k] ^=  K[idxK++];
// S5:
      t01 = X[1] ^ X[3]; t02 = X[1] | X[3]; t03 = X[0] & t01; t04 = X[2] ^ t02;
      t05 = t03 ^ t04; Y[0]= ~t05; t07 = X[0]  ^ t01; t08 = X[3] | Y[0];
      t09 = X[1]  | t05; t10 = X[3] ^ t08; t11 = X[1] | t07; t12 = t03 | Y[0];
      t13 = t07 | t10; t14 = t01 ^ t11; Y[2]= t09 ^ t13; Y[1]= t07 ^ t08;
      Y[3]= t12 ^ t14;   
      LinTransE(X,Y);
      for (int k=0; k<4; k++) 
        X[k] ^=  K[idxK++];
// S6:
      t01 = X[0] & X[3];  t02 = X[1] ^ X[2]; t03 = X[0] ^ X[3]; t04 = t01 ^ t02;
      t05 = X[1] | X[2];  Y[1]= ~t04; t07 = t03 & t05; t08 = X[1] & Y[1];
      t09 = X[0] | X[2];  t10 = t07 ^ t08;  t11 = X[1] | X[3];  t12 = X[2] ^ t11;
      t13 = t09 ^ t10;  Y[2]= ~t13; t15 = Y[1] & t03;  Y[3]= t12 ^ t07;
      t17 = X[0]  ^ X[1]; t18 = Y[2] ^ t15; Y[0]= t17 ^ t18;
      LinTransE(X,Y);
      for (int k=0; k<4; k++) 
        X[k] ^=  K[idxK++];
// S7:
      t01 = X[0] & X[2]; t02 = ~X[3]; t03 = X[0] & t02; t04 = X[1] | t01;
      t05 = X[0] & X[1]; t06 = X[2] ^ t04; Y[3]= t03 ^ t06; t08 = X[2] | Y[3];
      t09 = X[3] | t05; t10 = X[0] ^ t08; t11 = t04 & Y[3]; Y[1]= t09 ^ t10;
      t13 = X[1] ^ Y[1]; t14 = t01 ^ Y[1]; t15 = X[2] ^ t05; t16 = t11 | t13;
      t17 = t02 | t14; Y[0]= t15 ^ t17; Y[2]= X[0] ^ t16;
      if(i==3) break;
      LinTransE(X,Y);
    }
    for (int k=0; k<4; k++) 
      Y[k] ^=  K[idxK++];
    for (int k=15; k>=0; k--) 
      out[outOffset++]= (byte)(Y[k/4] >>> ((k%4)*8));
  }
//----------------------------------------------------------------------------
  protected void LinTransD(int[]X, int[]Y) {
    X[2]= (Y[2] << 10) | (Y[2] >>> 22);
    X[0]= (Y[0] << 27) | (Y[0] >>> 5);
    X[2]= X[2] ^ Y[3] ^ (Y[1]<< 7);
    X[0]= X[0] ^ Y[1] ^ Y[3];
    X[3]= (Y[3] << 25) | (Y[3] >>> 7);
    X[1]= (Y[1] << 31) | (Y[1] >>> 1);
    X[3]= X[3] ^ X[2] ^ (X[0] << 3);
    X[1]= X[1] ^ X[0] ^ X[2];
    X[2]= (X[2] << 29) | (X[2] >>> 3);
    X[0]= (X[0] << 19) | (X[0] >>> 13);
  }
//----------------------------------------------------------------------------
  private final void blockDecrypt(byte[] in, int inOffset,
                                  byte[] out, int outOffset) {
    int[]X=new int[4], Y=new int[4];
    for (int k=3; k>=0; k--) 
      X[k]= (in[inOffset++] & 0xFF)<<24 | (in[inOffset++]&0xFF)<<16 |
             (in[inOffset++] & 0xFF)<< 8 | (in[inOffset++]&0xFF)      ;
    int t00, t01, t02, t03, t04, t05, t06, t07, t08, t09, t10;
    int t11, t12, t13, t14, t15, t16, t17, t18, t19;
    int z, idxK=32*4+3;
    for (int k=3; k>=0; k--) 
      X[k] ^=  K[idxK--];
    for(int i=0; i<4; i++){
// InvS7:
      t01 = X[0] & X[1]; t02 = X[0] | X[1]; t03 = X[2] | t01; t04 = X[3] & t02;
      Y[3]= t03 ^ t04; t06 = X[1] ^ t04; t07 = X[3] ^ Y[3]; t08 = ~t07;
      t09 = t06 | t08; t10 = X[1] ^ X[3]; t11 = X[0] | X[3];  Y[1]= X[0] ^ t09;
      t13 = X[2]  ^ t06; t14 = X[2]  & t11; t15 = X[3] | Y[1]; t16 = t01 | t10;
      Y[0]= t13 ^ t15; Y[2]= t14 ^ t16;
      for (int k=3; k>=0; k--) 
        Y[k] ^=  K[idxK--];
      LinTransD(X,Y);
// InvS6:
      t01 = X[0] ^ X[2]; t02 = ~X[2]; t03 = X[1]  & t01; t04 = X[1] | t02;
      t05 = X[3] | t03; t06 = X[1] ^ X[3]; t07 = X[0] & t04; t08 = X[0] | t02;
      t09 = t07 ^ t05; Y[1]= t06 ^ t08; Y[0]= ~t09; t12 = X[1] & Y[0];
      t13 = t01 & t05; t14 = t01 ^ t12; t15 = t07 ^ t13; t16 = X[3] | t02;
      t17 = X[0] ^ Y[1]; Y[3]= t17 ^ t15; Y[2]= t16 ^ t14;
      for (int k=3; k>=0; k--) 
        Y[k] ^=  K[idxK--];
      LinTransD(X,Y);
// InvS5:
      t01 = X[0] & X[3]; t02 = X[2]  ^ t01; t03 = X[0] ^ X[3]; t04 = X[1] & t02;
      t05 = X[0] & X[2]; Y[0]= t03 ^ t04; t07 = X[0] & Y[0]; t08 = t01 ^ Y[0];
      t09 = X[1] | t05; t10 = ~X[1]; Y[1]= t08 ^ t09; t12 = t10 | t07;
      t13 = Y[0]  | Y[1]; Y[3]= t02 ^ t12; t15 = t02 ^ t13; t16 = X[1] ^ X[3];
      Y[2]= t16 ^ t15;
      for (int k=3; k>=0; k--) 
        Y[k] ^=  K[idxK--];
      LinTransD(X,Y);
// InvS4: 
        t01 = X[1] | X[3]; t02 = X[2] | X[3]; t03 = X[0] & t01; t04 = X[1] ^ t02;
        t05 = X[2] ^ X[3]; t06 = ~t03; t07 = X[0] & t04;  Y[1]= t05 ^ t07;
        t09 = Y[1] | t06; t10 = X[0] ^ t07; t11 = t01 ^ t09; t12 = X[3] ^ t04;
        t13 = X[2]  | t10; Y[3]= t03 ^ t12; t15 = X[0] ^ t04; Y[2]= t11 ^ t13;
        Y[0]= t15 ^ t09;
      for (int k=3; k>=0; k--) 
        Y[k] ^=  K[idxK--];
      LinTransD(X,Y);
// InvS3:
      t01 = X[2] | X[3]; t02 = X[0] | X[3]; t03 = X[2] ^ t02; t04 = X[1] ^ t02;
      t05 = X[0] ^ X[3]; t06 = t04 & t03; t07 = X[1] & t01; Y[2]= t05 ^ t06;
      t09 = X[0] ^ t03; Y[0]= t07 ^ t03; t11 = Y[0] | t05; t12 = t09 & t11;
      t13 = X[0] & Y[2]; t14 = t01 ^ t05; Y[1]= X[1]  ^ t12; t16 = X[1]  | t13;
      Y[3]= t14 ^ t16;
      for (int k=3; k>=0; k--) 
        Y[k] ^=  K[idxK--];
      LinTransD(X,Y);
// InvS2:
        t01 = X[0] ^ X[3]; t02 = X[2] ^ X[3]; t03 = X[0] & X[2]; t04 = X[1] | t02;
        Y[0]= t01 ^ t04; t06 = X[0] | X[2]; t07 = X[3] | Y[0]; t08 = ~X[3];
        t09 = X[1] & t06; t10 = t08 | t03; t11 = X[1] & t07; t12 = t06 & t02;
        Y[3]= t09 ^ t10; Y[1]= t12 ^ t11; t15 = X[2] & Y[3]; t16 = Y[0] ^ Y[1];
        t17 = t10 ^ t15; Y[2]= t16 ^ t17;
      for (int k=3; k>=0; k--) 
        Y[k] ^=  K[idxK--];
      LinTransD(X,Y);
// InvS1: 
        t01 = X[0] ^ X[1]; t02 = X[1] | X[3]; t03 = X[0] & X[2];t04 = X[2] ^ t02;
        t05 = X[0] | t04; t06 = t01 & t05; t07 = X[3] | t03; t08 = X[1] ^ t06;
        t09 = t07 ^ t06; t10 = t04 | t03; t11 = X[3]  & t08; Y[2]=  ~t09;
        Y[1]= t10 ^ t11; t14 = X[0] | Y[2]; t15 = t06 ^ Y[1];  Y[3]= t01 ^ t04;
        t17 = X[2]  ^ t15; Y[0]= t14 ^ t17;
      for (int k=3; k>=0; k--) 
        Y[k] ^=  K[idxK--];
      LinTransD(X,Y);
// InvS0: 
        t01 = X[2] ^ X[3]; t02 = X[0] | X[1]; t03 = X[1] | X[2];t04 = X[2] & t01;
        t05 = t02 ^ t01;  t06 = X[0] | t04; Y[2]= ~t05; t08 = X[1] ^ X[3];
        t09 = t03 & t08; t10 = X[3] | Y[2]; Y[1]= t09 ^ t06; t12 = X[0] | t05;
        t13 = Y[1] ^ t12; t14 = t03 ^ t10; t15 = X[0] ^ X[2]; Y[3]= t14 ^ t13;
        t17 = t05 & t13; t18 = t14 | t17; Y[0]= t15 ^ t18;
        for (int k=3; k>=0; k--) 
        Y[k] ^=  K[idxK--];
      if(i==3) break;
      LinTransD(X,Y);
    }
    for (int k=15; k>=0; k--) 
      out[outOffset++]= (byte)(Y[k/4] >>> ((k%4)*8));
  }
//-------------------------------------------------------------------------
  public static void main (String[] arg) {
    Serpent makeSerpent;
    switch(arg.length) {
      case 0: makeSerpent = new Serpent("");			// encrypt
        break;
      case 1: makeSerpent = new Serpent(arg[0]);		// encrypt
        break;
      case 2: makeSerpent = new Serpent(Hex.fromString(arg[0]),arg[1]); // decrypt
        break;
    }
  }
}