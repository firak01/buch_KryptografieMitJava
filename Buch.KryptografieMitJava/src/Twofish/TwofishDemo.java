public final class TwofishDemo {
  private final int
    BlockGroesse = 16, 				// 128 Bits im Datenblock
    Runden     	 = 16,
    SK_BUMP 	 = 0x01010101,
    SK_ROTL 	 = 9,
    GF256_FDBK   = 0x169,
    GF256_FDBK_2 = 0x169/2,
    GF256_FDBK_4 = 0x169/4;
  private final int TOTAL_SUBKEYS = 4+4+2*Runden;
  private final byte[][] P = new byte[2][256];
  private final int[][] P_ = 
    {{1,0,0,1,1},{0,0,1,1,0},{1,1,0,0,0},{0,1,1,0,1}};
  private final int[][] MDS = new int[4][256];
  private final int RS_GF_FDBK = 0x14D;
  private boolean decrypt;
  private final int[] sBox = new int[4 * 256];
  private final int[] subKeys = new int[TOTAL_SUBKEYS];
//------------------------------------------------------------------------
  public  TwofishDemo() {
    byte[]p = new byte[16];
    System.out.println("Klartext:");
    for (int i=0; i<p.length; i++)
      System.out.print(Hex.byteToString(p[i]));
    System.out.println(" ("+p.length+" Bytes)");
    decrypt = false;
    System.out.println("Starte Verschlüsselung ... ");
    Init(null);						// Schlüssel generieren
    int AnzahlBloecke = (p.length+BlockGroesse-1)/BlockGroesse;
    byte[] tmp = new byte[AnzahlBloecke*BlockGroesse];
    byte[] out = new byte[AnzahlBloecke*BlockGroesse];
    System.out.println(AnzahlBloecke+" 128-Bit-Blöcke = "+
                      (AnzahlBloecke*BlockGroesse)+" Bytes");
    System.arraycopy(p,0,tmp,0,p.length);		// p nach tmp kopieren
    long ms = -System.currentTimeMillis();
    for (int i=0; i<AnzahlBloecke; i++) 
      blockCrypt(tmp,i*BlockGroesse,out,i*BlockGroesse);
    ms += System.currentTimeMillis();;
    System.out.println("Verschlüsselung beendet. \nBenötigte Zeit: "+
                 (float)ms/1000.0+" sek\nVerschlüsselter Text: ");
    for (int i=0; i<out.length; i++)
      System.out.print(Hex.byteToString(out[i]));
    System.out.println();
    p = out; 	
    decrypt=true;
    Init(new byte[16]);
    System.out.println("Starte Entschlüsselung ...");
    System.arraycopy(p,0,tmp,0,p.length);		// p nach tmp kopieren
    ms = -System.currentTimeMillis();
    for (int i=0; i<AnzahlBloecke; i++)
      blockCrypt(tmp,i*BlockGroesse,out,i*BlockGroesse);
    ms += System.currentTimeMillis();
    System.out.println("Entschlüsselung beendet. \nBenötigte Zeit: "+
                       (float)ms/1000.0+" sek\nEntschlüselter Text:");
    for (int i=0; i<out.length; i++)
      System.out.print(Hex.byteToString(out[i]));
    System.out.println();
    System.exit(0);
  }
//-------------------------------------------------------------------------
  protected void Init(byte[] key) {
    int[][] pI = new int[][] { // die SBoxen
      {  // p0
        0xA967B3E8,0x04FDA376,0x9A928078,0xE4DDD138,
        0x0DC63598,0x18F7EC6C,0x43753726,0xFA139448,
        0xF2D08B30,0x8454DF23,0x195B3D59,0xF3AEA282,
        0x6301832E,0xD9519B7C,0xA6EBA5BE,0x160CE361,
        0xC08C3AF5,0x732C250B,0xBB4E896B,0x536AB4F1,
        0xE1E6BD45,0xE2F4B666,0xCC950356,0xD41C1ED7,
        0xFBC38EB5,0xE9CFBFBA,0xEA7739AF,0x33C96271,
        0x817909AD,0x24CDF9D8,0xE5C5B94D,0x440886E7,
        0xA11DAAED,0x0670B2D2,0x417BA011,0x31C22790,
        0x20F660FF,0x965CB1AB,0x9E9C521B,0x5F930AEF,
        0x918549EE,0x2D4F8F3B,0x47876D46,0xD63E6964,
        0x2ACECB2F,0xFC97057A,0xAC7FD51A,0x4B0EA75A,
        0x28143F29,0x883C4C02,0xB8DAB017,0x551F8A7D,
        0x57C78D74,0xB7C49F72,0x7E152212,0x58079934,
        0x6E50DE68,0x65BCDBF8,0xC8A82B40,0xDCFE32A4,
        0xCA1021F0,0xD35D0F00,0x6F9D3642,0x4A5EC1E0 },
      {  // p1
        0x75F3C6F4,0xDB7BFBC8,0x4AD3E66B,0x457DE84B,
        0xD632D8FD,0x3771F1E1,0x300FF81B,0x87FA063F,
        0x5EBAAE5B,0x8A00BC9D,0x6DC1B10E,0x805DD2D5,
        0xA0840714,0xB5902CA3,0xB2734C54,0x92743651,
        0x38B0BD5A,0xFC606296,0x6C42F710,0x7C28278C,
        0x13959CC7,0x24463B70,0xCAE385CB,0x11D093B8,
        0xA68320FF,0x9F77C3CC,0x036F08BF,0x40E72BE2,
        0x790CAA82,0x413AEAB9,0xE49AA497,0x7EDA7A17,
        0x6694A11D,0x3DF0DEB3,0x0B72A71C,0xEFD1533E,
        0x8F33265F,0xEC762A49,0x8188EE21,0xC41AEBD9,
        0xC53999CD,0xAD318B01,0x1823DD1F,0x4E2DF948,
        0x4FF2658E,0x785C5819,0x8DE59857,0x677F0564,
        0xAF63B6FE,0xF5B73CA5,0xCEE96844,0xE04D4369,
        0x292EAC15,0x59A80A9E,0x6E47DF34,0x356ACFDC,
        0x22C9C09B,0x89D4EDAB,0x12A20D52,0xBB022FA9,
        0xD7611EB4,0x5004F6C2,0x16258656,0x5509BE91 }};
    System.out.print("Lese SBoxen ... ");
    int i1=0, i2=0;
    for (int i=0; i<64; i++)
      for (int j=24; j>=0; j-=8) { 			// Beginn ist 6.nibble
        P[0][i1++] = (byte)((pI[0][i]>>>j)&0xFF);	// Bytes aus ints holen
        P[1][i2++] = (byte)((pI[1][i]>>>j)&0xFF);
      }
/*/ nur für eine Kontrollausgabe
    System.out.print("------------------ Die S-Boxen ----------------");
    for (int j=0; j<256; j++) {
      if ((j%16)==0) System.out.println();
      System.out.print(Hex.byteToString(P[0][j])+" ");
    }
*/
    System.out.println("fertig!");        
    System.out.print("Erstelle MDS-Matrix ... ");
    bestimmeMDSMatrix();
    System.out.println("fertig!");        
    if (!decrypt) {
      System.out.print("Schlüssellänge (128,192,256): ");
      switch (Integer.parseInt(IO.Satz())) {
        case 256: key=new byte[32]; break;
        case 192: key=new byte[24]; break;
        default: key=new byte[16];		// entspricht 128 Bit
      }
//      for (int i=0; i<key.length; i++) key[i]=(byte)(Math.random()*127);
    }
    key[0]=(byte)0x80;
    System.out.println("Schlüssel: "+Hex.toString(key));
    makeSubKeys(key);
  }
//-------------------------------------------------------------------------
  private void bestimmeMDSMatrix() {
    int[] m1 = new int[2];
    int[] mX = new int[2];
    int[] mY = new int[2];
    int i, j;
    for (i = 0; i < 256; i++) {
      j = P[0][i] & 0xFF;
      m1[0] = j;
      mX[0] = Mx_X(j) & 0xFF;
      mY[0] = Mx_Y(j) & 0xFF;
      j = P[1][i] & 0xFF;
      m1[1] = j;
      mX[1] = Mx_X(j) & 0xFF;
      mY[1] = Mx_Y(j) & 0xFF;
      MDS[0][i] = m1[P_[0][0]] <<  0 | mX[P_[0][0]] <<  8 |
                  mY[P_[0][0]] << 16 | mY[P_[0][0]] << 24;
      MDS[1][i] = mY[P_[1][0]] <<  0 | mY[P_[1][0]] <<  8 |
                  mX[P_[1][0]] << 16 | m1[P_[1][0]] << 24;
      MDS[2][i] = mX[P_[2][0]] <<  0 | mY[P_[2][0]] <<  8 |
                  m1[P_[2][0]] << 16 | mY[P_[2][0]] << 24;
      MDS[3][i] = mX[P_[3][0]] <<  0 | m1[P_[3][0]] <<  8 |
                  mY[P_[3][0]] << 16 | mX[P_[3][0]] << 24;
      }
  }
//-------------------------------------------------------------------------
  private final void makeSubKeys(byte[] k) {
    int length    = k.length;
    int k64Cnt    = length / 8;
    int[] k32e    = new int[4]; // even 32-bit entities
    int[] k32o    = new int[4]; // odd 32-bit entities
    int[] sBoxKey = new int[4];
    int i, j, offset = 0;
    for (i=0, j=k64Cnt-1; i<4 && offset<length; i++, j--) {
      k32e[i] = (k[offset++] & 0xFF)       |(k[offset++] & 0xFF) <<  8 |
                (k[offset++] & 0xFF) << 16 |(k[offset++] & 0xFF) << 24;
      k32o[i] = (k[offset++] & 0xFF)       |(k[offset++] & 0xFF) <<  8 |
                (k[offset++] & 0xFF) << 16 |(k[offset++] & 0xFF) << 24;
      sBoxKey[j] = RS_MDS_Encode( k32e[i], k32o[i] ); 		// reverse order
    }
    int A, B, q=0;
    i=0;
    while(i<TOTAL_SUBKEYS) {
      A = F32( k64Cnt, q, k32e );
      q += SK_BUMP;
      B = F32( k64Cnt, q, k32o );
      q += SK_BUMP;
      B = B << 8 | B >>> 24;
      A += B;
      subKeys[i++] = A;
      A += B;
      subKeys[i++] = A << SK_ROTL | A >>> (32-SK_ROTL);
    }
    int k0 = sBoxKey[0];
    int k1 = sBoxKey[1];
    int k2 = sBoxKey[2];
    int k3 = sBoxKey[3];
    int[] bb = new int[4];
    for (i = 0; i < 256; i++) {
      bb[0]=bb[1]=bb[2]=bb[3]=i;
      switch (k64Cnt & 3) {
        case 1:
          for (int m=0; m<2; m++) {
            sBox[      2*i+m] = MDS[m][(P[P_[m][1]][bb[m]] & 0xFF) ^ b(m,k0)];
            sBox[0x200+2*i+m] = MDS[m+2][(P[P_[m+2][1]][bb[m+2]] & 0xFF) ^ b(m+2,k0)];
          }
          break;
        case 0: // same as 4
          for (int m=0; m<3; m++)
          bb[m] = (P[P_[m][4]][bb[m]] & 0xFF) ^ b(m,k3);
        case 3:
          for (int m=0; m<3; m++)
            bb[m] = (P[P_[m][3]][bb[m]] & 0xFF) ^ b(m,k2);
        case 2: // 128-bit keys
          for (int m=0; m<2; m++) {
            sBox[      2*i+m] = MDS[m][(P[P_[m][1]][(P[P_[m][2]][bb[m]] & 0xFF) ^ b(m,k1)] & 0xFF) ^ b(m,k0)];
            sBox[0x200+2*i+m] = MDS[m+2][(P[P_[m+2][1]][(P[P_[m+2][2]][bb[m+2]] & 0xFF) ^ 
                                b(m+2,k1)] & 0xFF) ^ b(m+2,k0)];     
          }
      }
    }
    if(decrypt)
      for(i=0; i<4; i++) {
        int t        = subKeys[i];
        subKeys[i]   = subKeys[i+4];
        subKeys[i+4] = t;
      }
  }
//-------------------------------------------------------------------------
  private final void blockCrypt(byte[] in, int inOffset,
                                byte[] out, int outOffset) {
    int[]X = new int[4]; 
    for (int i=0; i<4; i++) {
      X[i] = (in[inOffset++] & 0xFF)       | (in[inOffset++] & 0xFF) <<  8 |
             (in[inOffset++] & 0xFF) << 16 | (in[inOffset++] & 0xFF) << 24;
      X[i] ^= subKeys[i];
    }
    int k, t0, t1;
    if(decrypt) {
      k = 39;					// decrypt
      for (int R=0; R<Runden; R+=2) {
        t0 = Fe32( sBox, X[0], 0 );
        t1 = Fe32( sBox, X[1], 3 );
        X[3] ^= t0 + 2*t1 + subKeys[k--];
        X[3]  = X[3] >>> 1 | X[3] << 31;
        X[2]  = X[2] << 1 | X[2] >>> 31;
        X[2] ^= t0 + t1 + subKeys[k--];
        t0 = Fe32(sBox, X[2], 0);
        t1 = Fe32(sBox, X[3], 3);
        X[1] ^= t0 + 2*t1 + subKeys[k--];
        X[1]  = X[1] >>> 1 | X[1] << 31;
        X[0]  = X[0] << 1 | X[0] >>> 31;
        X[0] ^= t0 + t1 + subKeys[k--];
      }
    }
    else {					// encrypt
      k = 8;
      for (int R=0; R<Runden; R+=2) {
        t0 = Fe32( sBox, X[0], 0 );
        t1 = Fe32( sBox, X[1], 3 );
        X[2] ^= t0 + t1 + subKeys[k++];
        X[2]  = X[2] >>> 1 | X[2] << 31;
        X[3]  = X[3] << 1 | X[3] >>> 31;
        X[3] ^= t0 + 2*t1 + subKeys[k++];
        t0 = Fe32( sBox, X[2], 0 );
        t1 = Fe32( sBox, X[3], 3 );
        X[0] ^= t0 + t1 + subKeys[k++];
        X[0]  = X[0] >>> 1 | X[0] << 31;
        X[1]  = X[1] << 1 | X[1] >>> 31;
        X[1] ^= t0 + 2*t1 + subKeys[k++];
      }
    }
    for (int i=2; i<6; i++)
      X[i%4] ^= subKeys[i+2];
    for (int i=2; i<6; i++)
      for (int j=0; j<4; j++)
        out[outOffset++] = (byte)(X[i%4] >>> (j*8));
  }
//-------------------------------------------------------------------------
  private final int LFSR1( int x ) {
    return (x >> 1)^((x & 0x01) != 0 ? GF256_FDBK_2 : 0);
  }
  private final int LFSR2( int x ){
    return (x >> 2) ^ ((x & 0x02) != 0 ? GF256_FDBK_2 : 0) ^
                      ((x & 0x01) != 0 ? GF256_FDBK_4 : 0);
  }
  private final int Mx_1( int x ) { return x;                       }
  private final int Mx_X( int x ) { return x ^ LFSR2(x);            }
  private final int Mx_Y( int x ) { return x ^ LFSR1(x) ^ LFSR2(x); }
  private final int b(int i, int x ) { return (x >>> (i*8))  & 0xFF; }
  private final int RS_MDS_Encode(int k0, int k1) {
    int r = k1;
    for (int i=0; i<4; i++) 		// shift 1 byte at a time
    r = RS_rem(r);
    r ^= k0;
    for (int i = 0; i < 4; i++)
      r = RS_rem( r );
    return r;
  }
//-------------------------------------------------------------------------
  private final int RS_rem(int x) {
    int b  = (x >>> 24) & 0xFF;
    int g2 = ((b  <<  1) ^ ((b & 0x80)!=0 ? RS_GF_FDBK : 0 )) & 0xFF;
    int g3 = (b >>>  1) ^ ((b & 0x01)!=0 ? (RS_GF_FDBK >>> 1) : 0)^g2;
    int result = (x << 8)^(g3 << 24)^(g2 << 16)^(g3 << 8)^b;
    return result;
  }
//-------------------------------------------------------------------------
  private final int F32( int k64Cnt, int x, int[] k32 ){
    int[] bb=new int[4];
    for (int i=0; i<4; i++)
      bb[i] = b(i,x);
    int result = 0;
    switch (k64Cnt & 3) {
      case 1: 
        result = 0;
        for (int i=0; i<4; i++)
          result ^= MDS[i][(P[P_[i][1]][bb[i]] & 0xFF)^b(i,k32[0])];
        break;
      case 0:  // same as 4
        for (int i=0; i<4; i++)
          bb[i] = (P[P_[i][4]][bb[i]] & 0xFF) ^ b(i,k32[3]);
      case 3:
        for (int i=0; i<4; i++)
          bb[i] = (P[P_[i][3]][bb[i]] & 0xFF) ^ b(i,k32[2]);
      case 2:
        result = 0;
        for (int i=0; i<4; i++)
          result ^= MDS[i][(P[P_[i][1]][(P[P_[i][2]][bb[i]]&0xFF)^
                    b(i,k32[1])]&0xFF)^b(i,k32[0])];
        break;
    }
    return result;
  }
//-------------------------------------------------------------------------
  private final int Fe32( int[] sBox, int x, int R ) {
    return sBox[      2*_b(x, R  )] ^ sBox[      2*_b(x,R+1)+1] ^
           sBox[0x200+2*_b(x, R+2)] ^ sBox[0x200+2*_b(x,R+3)+1];
  }
//-------------------------------------------------------------------------
  private final int _b( int x, int N ) {
    return b(N%4,x);
  }
//-------------------------------------------------------------------------
  public static void main (String[] arg) {
    TwofishDemo makeTwofishDemo = new TwofishDemo();
  }
}
