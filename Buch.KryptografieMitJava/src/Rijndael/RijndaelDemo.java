public final class RijndaelDemo {
  boolean decrypt;
  final int BlockGroesse = 16; // default block size in bytes
  final int[] alog = new int[256];
  final int[] log =  new int[256];
  final byte[] S =  new byte[256];
  final byte[] Si = new byte[256];
  final int[][] T = new int[8][256];
  final int[][] U = new int[4][256];
  final byte[] rcon = new byte[30];
  final int[][][] shifts = new int[][][] {
    { {0, 0}, {1, 3}, {2, 2}, {3, 1} },
    { {0, 0}, {1, 5}, {2, 4}, {3, 3} },
    { {0, 0}, {1, 7}, {3, 5}, {4, 4} }  };
  byte[][] A = new byte[][] {
    {1, 1, 1, 1, 1, 0, 0, 0},
    {0, 1, 1, 1, 1, 1, 0, 0},
    {0, 0, 1, 1, 1, 1, 1, 0},
    {0, 0, 0, 1, 1, 1, 1, 1},
    {1, 0, 0, 0, 1, 1, 1, 1},
    {1, 1, 0, 0, 0, 1, 1, 1},
    {1, 1, 1, 0, 0, 0, 1, 1},
    {1, 1, 1, 1, 0, 0, 0, 1}  };
  byte[] B = new byte[] { 0, 1, 1, 0, 0, 0, 1, 1};
  byte[][] cox = new byte[256][8];
  byte[][] G = new byte[][] {
    {2, 1, 1, 3},
    {3, 2, 1, 1},
    {1, 3, 2, 1},
    {1, 1, 3, 2}  };
  byte[][] AA = new byte[4][8];
  byte pivot, tmp;
  byte[][] iG = new byte[4][4];
  int[][] Ke, Kd; 			// Keys für encrypt und decrypt 

//------------------------------------------------------------------------
  public RijndaelDemo() {				// Constructor encrypt	
    byte[] p=new byte[16];
//    p[0]=(byte)0x80;
    System.out.println("Klartext ("+p.length+" Bytes):");
    for (int i=0; i<p.length; i++)
      System.out.print(Hex.byteToString(p[i]));
    decrypt = false;
    System.out.println("\nStarte Verschlüsselung ...");
    Init(null);						// Schlüssel generieren
System.out.println("Init fertig!");
    int AnzahlBloecke = (p.length+BlockGroesse-1)/BlockGroesse; // Anzahl 64 Bit-Blöcke
    byte[] tmp = new byte[AnzahlBloecke*BlockGroesse];
    byte[] out = new byte[AnzahlBloecke*BlockGroesse];
    System.out.println(AnzahlBloecke+" 128-Bit-Blöcke = "+
                 (AnzahlBloecke*BlockGroesse)+" Bytes");
    System.arraycopy(p,0,tmp,0,p.length);		// p nach tmp kopieren
    for (int i=0; i<AnzahlBloecke; i++) 
      blockEncrypt(tmp,i*BlockGroesse,out,i*BlockGroesse);
    for (int i=0; i<out.length; i++)
      System.out.print(Hex.byteToString(out[i]));
    System.out.println();
    p = out;
    decrypt=true;
    System.out.println("Starte Entschlüsselung ...");
    System.arraycopy(p,0,tmp,0,p.length);		// p nach tmp kopieren
    for (int i=0; i<AnzahlBloecke; i++)
      blockDecrypt(tmp,i*BlockGroesse,out,i*BlockGroesse);
    for (int i=0; i<out.length; i++)
      System.out.print(Hex.byteToString(out[i]));
    System.out.println();
    System.exit(0);
  }
//-------------------------------------------------------------------------
  void Init (byte[] key) {
    int ROOT = 0x11B;
    int i, j = 0;
    alog[0] = 1;
    for (i = 1; i < 256; i++) {
      j = (alog[i-1] << 1) ^ alog[i-1];
      if ((j & 0x100) != 0) j ^= ROOT;
      alog[i] = j;
    }
    for (i = 1; i < 255; i++) 
      log[alog[i]] = i;
    int t;
    byte[][] box = new byte[256][8];
    box[1][7] = 1;
    for (i = 2; i < 256; i++) {
      j = alog[255 - log[i]];
      for (t = 0; t < 8; t++)
        box[i][t] = (byte)((j >>> (7 - t)) & 0x01);
    }
    for (i = 0; i < 256; i++)
      for (t = 0; t < 8; t++) {
        cox[i][t] = B[t];
        for (j = 0; j < 8; j++)
          cox[i][t] ^= A[t][j] * box[i][j];
      }
    for (i = 0; i < 256; i++) {
      S[i] = (byte)(cox[i][0] << 7);
      for (t = 1; t < 8; t++)
        S[i] ^= cox[i][t] << (7-t);
      Si[S[i] & 0xFF] = (byte) i;
    }
    for (i = 0; i < 4; i++) {
      for (j = 0; j < 4; j++) AA[i][j] = G[i][j];
      AA[i][i+4] = 1;
    }
    for (i = 0; i < 4; i++) {
      pivot = AA[i][i];
      if (pivot == 0) {
        t = i + 1;
        while ((AA[t][i] == 0) && (t < 4))
          t++;
        if (t == 4)
          throw new RuntimeException("G matrix is not invertible");
        else {
          for (j = 0; j < 8; j++) {
            tmp = AA[i][j];
            AA[i][j] = AA[t][j];
            AA[t][j] = (byte) tmp;
          }
          pivot = AA[i][i];
        }
      }
      for (j = 0; j < 8; j++)
        if (AA[i][j] != 0)
      AA[i][j] = (byte)
      alog[(255 + log[AA[i][j] & 0xFF] - log[pivot & 0xFF]) % 255];
      for (t = 0; t < 4; t++)
        if (i != t) {
          for (j = i+1; j < 8; j++)
            AA[t][j] ^= mul(AA[i][j], AA[t][i]);
          AA[t][i] = 0;
        }
    }
    for (i = 0; i < 4; i++)
      for (j = 0; j < 4; j++) 
        iG[i][j] = AA[i][j + 4];
    int s;
    for (t = 0; t < 256; t++) {
      s = S[t];
      for (i=0; i<4; i++) {
        T[i][t] = mul4(s, G[i]);
        U[i][t] = mul4(t,iG[i]);
      }
      s = Si[t];
      for (i=4; i<8; i++) 
        T[i][t] = mul4(s,iG[i-4]);
    }
    rcon[0] = 1;
    int r = 1;
    for (t = 1; t < 30; ) 
      rcon[t++] = (byte)(r = mul(2, r));
    if (!decrypt) {
      System.out.print("Schlüssellänge (128,192,256): ");
      switch (Integer.parseInt(IO.Satz())) {
        case 256: key=new byte[32]; break;
        case 192: key=new byte[24]; break;
        default: key=new byte[16];		// entspricht 128 Bit
      }
    }
    key[0]=(byte)0x80;
    System.out.println("Schlüssel: "+Hex.toString(key));
    generateSubKeys(key);
  }
//-------------------------------------------------------------------------
  private final void generateSubKeys(byte[] k) {
    int ROUNDS = getRounds(k.length, BlockGroesse);
    int BC = BlockGroesse / 4;
    Ke = new int[ROUNDS + 1][BC]; 	// encryption round keys
    Kd = new int[ROUNDS + 1][BC]; 	// decryption round keys
    int ROUND_KEY_COUNT = (ROUNDS + 1) * BC;
    int KC = k.length / 4;
    int[] tk = new int[KC];
// copy user material bytes into temporary ints
    for (int i = 0, j = 0; i < KC; )
      tk[i++] = (k[j++] & 0xFF) << 24 | (k[j++] & 0xFF) << 16 |
                (k[j++] & 0xFF) <<  8 | (k[j++] & 0xFF);
// copy values into round key arrays
    int t = 0;
    for (int j = 0; (j < KC) && (t < ROUND_KEY_COUNT); j++, t++) {
      Ke[t / BC][t % BC] = tk[j];
      Kd[ROUNDS - (t / BC)][t % BC] = tk[j];
    }
    int tt, rconpointer = 0;
    while (t < ROUND_KEY_COUNT) {
        // extrapolate using phi (the round key evolution function)
      tt = tk[KC - 1];
      tk[0] ^= (S[(tt >>> 16) & 0xFF] & 0xFF) << 24 ^
               (S[(tt >>>  8) & 0xFF] & 0xFF) << 16 ^
               (S[ tt         & 0xFF] & 0xFF) <<  8 ^
               (S[(tt >>> 24) & 0xFF] & 0xFF)       ^
               (rcon[rconpointer++]   & 0xFF) << 24;
      if (KC != 8)
        for (int i = 1, j = 0; i < KC; ) tk[i++] ^= tk[j++];
      else {
        for (int i = 1, j = 0; i < KC / 2; ) tk[i++] ^= tk[j++];
          tt = tk[KC / 2 - 1];
        tk[KC / 2] ^= (S[ tt         & 0xFF] & 0xFF)       ^
                      (S[(tt >>>  8) & 0xFF] & 0xFF) <<  8 ^
                      (S[(tt >>> 16) & 0xFF] & 0xFF) << 16 ^
                      (S[(tt >>> 24) & 0xFF] & 0xFF) << 24;
        for (int j=KC/2, i=j+1; i<KC;) 
          tk[i++] ^= tk[j++];
      }
// copy values into round key arrays
      for (int j=0; (j<KC) && (t<ROUND_KEY_COUNT); j++, t++) {
        Ke[t / BC][t % BC] = tk[j];
        Kd[ROUNDS - (t / BC)][t % BC] = tk[j];
      }
    }
    for (int r = 1; r < ROUNDS; r++)    // inverse MixColumn where needed
      for (int j = 0; j < BC; j++) {
        tt = Kd[r][j];
        Kd[r][j] = U[0][(tt >>> 24) & 0xFF];
        for (int i=1; i<4; i++)
          Kd[r][j] ^= U[i][(tt >>> ((3-i)*8)) & 0xFF];
        }
  }
//-------------------------------------------------------------------------
  final int mul (int a, int b) {
    return (a != 0 && b != 0) ? alog[(log[a & 0xFF] + log[b & 0xFF]) % 255] : 0;
  }
//-------------------------------------------------------------------------  
  final int mul4 (int a, byte[] b) {
    if (a == 0) 
      return 0;
    a = log[a & 0xFF];
    int a0 = (b[0] != 0) ? alog[(a + log[b[0] & 0xFF]) % 255] & 0xFF : 0;
    int a1 = (b[1] != 0) ? alog[(a + log[b[1] & 0xFF]) % 255] & 0xFF : 0;
    int a2 = (b[2] != 0) ? alog[(a + log[b[2] & 0xFF]) % 255] & 0xFF : 0;
    int a3 = (b[3] != 0) ? alog[(a + log[b[3] & 0xFF]) % 255] & 0xFF : 0;
    return a0 << 24 | a1 << 16 | a2 << 8 | a3;
  }
//-------------------------------------------------------------------------
  public void blockEncrypt (byte[] in, int inOffset, byte[] out, int outOffset) {
    int ROUNDS = Ke.length - 1;
    int[] Ker = Ke[0];
    int[] X = new int[4];
    for (int i=0; i<4; i++)
      X[i] = ((in[inOffset++] & 0xFF) << 24 |
              (in[inOffset++] & 0xFF) << 16 |
              (in[inOffset++] & 0xFF) <<  8 |
              (in[inOffset++] & 0xFF)        ) ^ Ker[i];
    int[] A = new int[4];
    for (int r = 1; r < ROUNDS; r++) {          // apply round transforms
      Ker = Ke[r];
      for (int i=0; i<4; i++) {
        A[i] = Ker[i];
        for (int k=0; k<4; k++)
          A[i] ^= T[k][(X[(i+k)%4] >>> ((3-k)*8)) & 0xFF];
      }
      for (int i=0; i<4; i++)
        X[i] = A[i];
    }
    Ker = Ke[ROUNDS];
    for (int i=0; i<4; i++) {
      int tt = Ker[i];
      out[outOffset++] = (byte)(S[(X[i]       >>> 24) & 0xFF] ^ (tt >>> 24));
      out[outOffset++] = (byte)(S[(X[(i+1)%4] >>> 16) & 0xFF] ^ (tt >>> 16));
      out[outOffset++] = (byte)(S[(X[(i+2)%4] >>>  8) & 0xFF] ^ (tt >>>  8));
      out[outOffset++] = (byte)(S[ X[(i+3)%4]         & 0xFF] ^  tt        );
    }
  }
//-------------------------------------------------------------------------
  public void blockDecrypt (byte[] in, int inOffset,byte[]out,int outOffset) {
    int ROUNDS = Kd.length - 1;
    int[] Kdr = Kd[0];
    int[] X = new int[4];
    for (int i=0; i<4; i++)
      X[i] = ((in[inOffset++] & 0xFF) << 24 |
              (in[inOffset++] & 0xFF) << 16 |
              (in[inOffset++] & 0xFF) <<  8 |
              (in[inOffset++] & 0xFF)        ) ^ Kdr[i];
    int[] A = new int[4];
    for (int r = 1; r < ROUNDS; r++) {          // apply round transforms
      Kdr = Kd[r];
      for (int i=4; i<8; i++) {
        A[i-4] = Kdr[i-4];
        for (int k=0; k<4; k++)
          A[i-4] ^= T[k+4][(X[(i-k)%4] >>> ((3-k)*8)) & 0xFF];
      }
      for (int i=0; i<4; i++)
        X[i] = A[i];
    }
    Kdr = Kd[ROUNDS];
    for (int i=4; i<8; i++) {
      int tt = Kdr[i-4];
      out[outOffset++] = (byte)(Si[(X[i%4]     >>> 24) & 0xFF] ^ (tt >>> 24));
      out[outOffset++] = (byte)(Si[(X[(i-1)%4] >>> 16) & 0xFF] ^ (tt >>> 16));
      out[outOffset++] = (byte)(Si[(X[(i-2)%4] >>>  8) & 0xFF] ^ (tt >>>  8));
      out[outOffset++] = (byte)(Si[ X[(i-3)%4]         & 0xFF] ^  tt        );
    }
  }
//-------------------------------------------------------------------------
  public int getRounds (int keySize, int blockSize) {
    switch (keySize) {
      case 16:
        return blockSize == 16 ? 10 : (blockSize == 24 ? 12 : 14);
      case 24:
        return blockSize != 32 ? 12 : 14;
      default: // 32 bytes = 256 bits
        return 14;
    }
  }
//-------------------------------------------------------------------------
  private boolean areEqual (byte[] a, byte[] b) {
    int aLength = a.length;
    if (aLength != b.length)
      return false;
    for (int i = 0; i < aLength; i++)
      if (a[i] != b[i])
        return false;
    return true;
  }
//-------------------------------------------------------------------------
  public static void main (String[] arg) {
    RijndaelDemo makeRijndaelDemo = new RijndaelDemo();
  }
}
