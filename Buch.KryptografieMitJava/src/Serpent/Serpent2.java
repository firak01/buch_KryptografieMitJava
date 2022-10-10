public final class Serpent2 {
  private static final int
    BlockGroesse = 16, 					// bytes in a data-block
    Runden = 32, 					// Anzahl Runden
    PHI = 0x9E3779B9; 					// (sqrt(5)-1) * 2**31
  private boolean decrypt;
  final int[][] K = new int[Runden + 1][4];
  final byte[][] SBox = new byte[8][16];
  final byte[][] SBoxInverse = new byte[8][16];
  final byte[] IPtable = new byte[128];
  final byte[] FPtable = new byte[128];
  final byte[][] LTtable = new byte[128][8];
  final byte[][] LTtableInverse = new byte[128][8]; 


//------------------------------------------------------------------------
  public Serpent2(String DatName) {			// Constructor encrypt	
    long ms;						// Millisekunden
    Datei P;
    byte[] p;
    if (DatName.length()>0) P = new Datei(DatName);
    else		    P = new Datei();
    System.out.print("Lese Klartext: ");
    ms = -System.currentTimeMillis();
    p = P.liesAsByte(); 	
    ms += System.currentTimeMillis();
    System.out.println(P.dateiname+" ...");
    System.out.println(" ben�tigte Zeit: "+(float)ms/1000.0+" sek");
    System.out.println(p.length+" Bytes)");
    decrypt = false;
    System.out.println("Starte Verschl�sselung ...");
    Init(null);						// Schl�ssel generieren
    int AnzahlBloecke = (p.length+BlockGroesse-1)/BlockGroesse; // Anzahl 128 Bit-Bl�cke
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
                 (float)ms/1000.0+" sek\nIn Datei speichern ...");
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
  public Serpent2(byte[] key, String DatName) {		// Constructor decrypt
    long ms;						// Millisekunden
    Datei P;
    byte[] p;
    P = new Datei(DatName);
    System.out.print("Lese Datei: "+P.dateiname+" ...");
    ms = -System.currentTimeMillis();
    p = P.liesAsByte(); 	
    ms += System.currentTimeMillis();
    System.out.println("("+p.length+" Bytes)");
    System.out.println(" ben�tigte Zeit: "+(float)ms/1000.0+" sek");
    decrypt=true;
    System.out.println("Starte Entschl�sselung ...");
    Init(key);						// Teilschl�ssel 
    int AnzahlBloecke = (p.length+BlockGroesse-1)/BlockGroesse;	// Anzahl 64 Bit-Bl�cke
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
  protected void Init(byte[] key) { 
    bestimmeSBoxen();
    bestimmeLTtables();
    System.out.print("Bestimme Permutationen ... ");
    int iFP = 0,dFP = 1,iIP = 0,dIP = 1;
    for (int i=0; i<128; i++) { 
      IPtable[i] = (byte)iIP;
      iIP += 32;
        if (iIP > 127)
        iIP = dIP++;
        FPtable[i] = (byte)iFP;
        iFP += 4;
        if (iFP > 127)
          iFP = dFP++;
    }
    System.out.println("fertig!");
    if (!decrypt) {				// Key bestimmen?
      System.out.print("Schl�ssell�nge (128,192,256): ");
      switch (Integer.parseInt(IO.Satz())) {
        case 256: key=new byte[32]; break;
        case 192: key=new byte[24]; break;
        default: key=new byte[16];		// entspricht 128 Bit
      }
      for (int i=0; i<key.length; i++) key[i]=(byte)(Math.random()*128);
    }
    System.out.println("Schl�ssel:"+Hex.toString(key));
    System.out.print("Bestimme Rundenschl�ssel ... ");
    makeKey(key);
    System.out.println("fertig!");
  }
//-------------------------------------------------------------------------
  private void bestimmeSBoxen() { 
    System.out.print("Lese SBoxen ... ");
    final int[] SInt = new int[] { 
//  jeweils 8 Nibble in einer Int
      0x38f1a65b,0xed42709c,0xfc27905a,0x1be86d34,
      0x86793Caf,0xd1e40b52,0x0fb8c963,0xd124a75e,
      0x1f83c0b6,0x254a9e7d,0xf52b4a9c,0x03e8d671,
      0x72c5846b,0xe91fd3a0,0x1df0e82b,0x74ca9356,
//  es folgen die inversen Boxen
      0xd3b0a65c,0x1e47f982,0x582ef6c3,0xb4791da0,
      0xc9f4be12,0x036d58a7,0x09a7be6d,0x35c248f1,
      0x5083a97e,0x2cb64fd1,0x8f2941de,0xb6537ca0,
      0xfa1d5360,0x49e72c8b,0x306d9ef8,0x5cb7a142
    };
    int zeile=0,spalte=0;
    for (int i=0; i<16; i+=2)
      for (int j=15; j>=0; j--) { 
        SBox[i/2][15-j]=(byte)getNibble(SInt[i+(15-j)/8],j%8);
        SBoxInverse[i/2][15-j]=(byte)getNibble(SInt[i+16+(15-j)/8],j%8);
      }
/*    // nur eine Kontrollausgabe
    System.out.println("------------------ Die S-Boxen ----------------");
    for (int i=0; i<8; i++) { 
      for (int j=0; j<16; j++)
        System.out.print(Hex.byteToString(SBox[i][j])+" ");
      System.out.println();
    }
    System.out.println("\n------------------ Die Inversen ---------------");
    for (int i=0; i<8; i++) { 
      for (int j=0; j<16; j++)
        System.out.print(Hex.byteToString(SBoxInverse[i][j])+" ");
      System.out.println();
    }
*/
    System.out.println("fertig!");        
  }
//-------------------------------------------------------------------------
  public void bestimmeLTtables() {
    System.out.print("Lese LTtables ... ");
    Datei P = new Datei("LTtable.dat");
    byte[] p = P.liesAsByte();
    int z=0;
    for (int i=0; i<128; i++)
      for (int j=0; j<8;j++)
        LTtable[i][j] = p[z++];
    P = new Datei("LTtableInverse.dat");
    p = P.liesAsByte();
    z=0;
    for (int i=0; i<128; i++)
      for (int j=0; j<8;j++)
        LTtableInverse[i][j] = p[z++];
    System.out.println("fertig!");        
  }
//-------------------------------------------------------------------------
  public void makeKey (byte[] key) { 
    int[] w = new int[4 * (Runden + 1)];
    int offset = 0;
    int limit = key.length / 4;
    int i,j,m,t,box,a,b,c,d,in,out;
    for (i = 0; i < limit; i++)
      w[i] = (key[offset++] & 0xFF)       | (key[offset++] & 0xFF) <<  8 |
             (key[offset++] & 0xFF) << 16 |(key[offset++] & 0xFF) << 24;
    if (i < 8)
    w[i++] = 1;
    for (i = 8,j = 0; i < 16; i++) { 
      t = w[j] ^ w[i-5] ^ w[i-3] ^ w[i-1] ^ PHI ^ j++;
      w[i] = t << 11 | t >>> 21;
    }
    for (i = 0,j = 8; i < 8; ) 
      w[i++] = w[j++];
    limit = 4 * (Runden + 1); 			// 132 for a 32-round Serpent
    for ( ; i < limit; i++) { 
      t = w[i-8] ^ w[i-5] ^ w[i-3] ^ w[i-1] ^ PHI ^ i;
      w[i] = t << 11 | t >>> 21;
    }
    int[] k = new int[limit];
    for (i = 0; i < Runden + 1; i++) { 
      box = (Runden + 3 - i) % Runden;
      a = w[4*i    ];
      b = w[4*i + 1];
      c = w[4*i + 2];
      d = w[4*i + 3];
      for (j = 0; j < 32; j++) { 
        in = getBit(a,j) | getBit(b,j) << 1 |getBit(c,j) << 2 | getBit(d,j) << 3;
        out = S(box,in);
        for (m=0; m<4; m++)
          k[4*i+m] |= getBit(out,m) << j;
      }
    }
    for (i=0,offset=0; i < Runden + 1; i++)
      for (j=0; j<4; j++) 
        K[i][j] = k[offset++];
      for (i = 0; i < Runden + 1; i++)
    K[i] = IP(K[i]);				// entspricht KDach=IP(K)!
  }
//----------------------------------------------------------------------------
  public void blockEncrypt (byte[] in,int inOffset,byte[] out,int outOffset) { 
    int[] X = new int[4];   
    for (int i=0; i<4; i++) 
      X[i] = (in[inOffset++] & 0xFF)       |(in[inOffset++] & 0xFF) <<  8 |
             (in[inOffset++] & 0xFF) << 16 |(in[inOffset++] & 0xFF) << 24;
    int[] BDach = IP(X);
    for (int i = 0; i < Runden; i++)
      BDach = R(i,BDach,K);			// eigentlich KDach, aber K=KDach!!
    X = FP(BDach);
    for (int i=0; i<16; i++)				// die Chiffre-der Text
      out[outOffset++] = (byte)(X[i/4] >>> (i%4)*8);
  }
//----------------------------------------------------------------------------
  public void blockDecrypt (byte[] in,int inOffset,byte[] out,int outOffset) { 
    int[] X = new int[4];   
    for (int i=0; i<4; i++) 
      X[i] = (in[inOffset++] & 0xFF)       |(in[inOffset++] & 0xFF) <<  8 |
             (in[inOffset++] & 0xFF) << 16 |(in[inOffset++] & 0xFF) << 24;
    int[] BDach = FPinverse(X);
    for (int i = Runden - 1; i >= 0; i--)
      BDach = Rinverse(i,BDach,K);		// eigentlich KDach, aber K=KDach!!
    X = IPinverse(BDach);
    for (int i=0; i<16; i++)				// die Chiffre-der Text
      out[outOffset++] = (byte)(X[i/4] >>> (i%4)*8);
  }
//----------------------------------------------------------------------------
  private int getBit (int x,int i) {  return (x >>> i) & 0x01; }
  private int getBit (int[] x,int i) { return (x[i / 32] >>> (i % 32)) & 0x01; }
  private void setBit (int[] x,int i,int v) { 
    if ((v & 0x01) == 1) x[i / 32] |= 1 << (i % 32); // set it
    else                 x[i / 32] &= ~(1 << (i % 32)); // clear it
  }
  private int getNibble (int x,int i) {  return (x >>> (4 * i)) & 0x0F; }
  private int[] IP (int[] x) {  return permutate(IPtable,x); }
  private int[] IPinverse (int[] x) {  return permutate(FPtable,x); }
  private int[] FP (int[] x) {  return permutate(FPtable,x); }
  private int[] FPinverse (int[] x) {  return permutate(IPtable,x); }
  private int[] permutate (byte[] T,int[] x) { 
    int[] result = new int[4];
    for (int i = 0;  i < 128; i++)
      setBit(result,i,getBit(x,T[i] & 0x7F));
    return result;
  }
  private int[] xor128 (int[] x,int[] y) { 
    return new int[] { x[0] ^ y[0],x[1] ^ y[1],x[2] ^ y[2],x[3] ^ y[3]};
  }
  private int S (int box,int x) {  return SBox[box%8][x] & 0x0F; }
  private int Sinverse (int box,int x) {  return SBoxInverse[box%8][x] & 0x0F; }
  private int[] SDach (int box,int[] x) { 
    int[] result = new int[4];
    for (int i = 0; i < 4; i++)
      for (int nibble = 0; nibble < 8; nibble++)
        result[i] |= S(box,getNibble(x[i],nibble)) << (nibble * 4);
    return result;
  }
  private int[] SDachInverse (int box,int[] x) { 
    int[] result = new int[4];
    for (int i = 0; i < 4; i++)
      for (int nibble = 0; nibble < 8; nibble++)
        result[i] |= Sinverse(box,getNibble(x[i],nibble)) << (nibble * 4);
    return result;
  }
  private int[] LT (int[] x) {  return transform(LTtable,x); }
  private int[] LTinverse (int[] x) { return transform(LTtableInverse,x); }
  private int[] transform (byte[][] T,int[] x) { 
    int i,j,b;
    int[] result = new int[4];
    for (i=0; i<128; i++) { 
      b = 0;
      j = 0;
      while (T[i][j] != (byte)0xFF) { 
        b ^= getBit(x,T[i][j] & 0x7F);
        j++;
      }
      setBit(result,i,b);
    }
    return result;
  }
  private int[] R (int i,int[] BDachi,int[][] KDach) { 
    int[] xored = xor128(BDachi,KDach[i%8]);
    int[] SDachi = SDach(i,xored);
    int[] BDachiPlus1;
    if ((0 <= i) && (i <= Runden - 2))
      BDachiPlus1 = LT(SDachi);
    else 
      if (i == Runden - 1)
        BDachiPlus1 = xor128(SDachi,KDach[Runden%8]);
      else BDachiPlus1=null;
    return BDachiPlus1;
  }
  private int[] Rinverse (int i,int[] BDachiPlus1,int[][] KDach) { 
    int[] SDachi = new int[4];
    if ((0 <= i) && (i <= Runden - 2))
      SDachi = LTinverse(BDachiPlus1);
    else 
      if (i==(Runden-1))
        SDachi = xor128(BDachiPlus1,KDach[Runden%8]);
    int[] xored = SDachInverse(i,SDachi);
    int[] BDachi = xor128(xored,KDach[i%8]);
    return BDachi;
  }
  private int[] Rinverse (int i,int[] BDachiPlus1,int[][] KDach,int in,int val) { 
    int[] SDachi = new int[4];
    if ((0 <= i) && (i <= Runden - 2))
      SDachi = LTinverse(BDachiPlus1);
    else 
      if (i == Runden - 1)
        SDachi = xor128(BDachiPlus1,KDach[Runden%8]);
    int[] xored = SDachInverse(i,SDachi);
    if(i==in) { 
      xored[0] = val | (val<<4);
      xored[0] |= (xored[0]<<8);
      xored[0] |= (xored[0]<<16);
      xored[1] = xored[2] = xored[3] = xored[0];
    }
    int[] BDachi = xor128(xored,KDach[i%8]);
    return BDachi;
  }
//-------------------------------------------------------------------------
  public static void main (String[] arg) {
    Serpent2 makeSerpent;
    switch(arg.length) {
      case 0: makeSerpent = new Serpent2("");
        break;
      case 1: makeSerpent = new Serpent2(arg[0]);
        break;
      case 2: makeSerpent = new Serpent2(Hex.fromString(arg[0]),arg[1]); // decrypt
        break;
    }
  }
}

