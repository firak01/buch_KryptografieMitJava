public class Twofish {

  private final int
    BlockGroesse = 16, 					// 128 Bits im Datenblock
    Runden = 16, 					// Anzahl Runden
    MaxRunden = 16, 					// nur für die SubKeys
    PHI = 0x9E3779B9; 					// (sqrt(5)-1) * 2**31
    private boolean decrypt;
   private byte[][] Sbox = new byte[8][16];
   private final int INPUT_WHITEN = 0;
   private final int OUTPUT_WHITEN = INPUT_WHITEN +  BlockGroesse/4;
   private final int ROUND_SUBKEYS = OUTPUT_WHITEN + BlockGroesse/4; // 2*(# Runden)
   private final int TOTAL_SUBKEYS = ROUND_SUBKEYS + 2*MaxRunden;
   private int[] K = new int[TOTAL_SUBKEYS];
   private final int SK_STEP = 0x02020202;
   private final int SK_BUMP = 0x01010101;
   private final int SK_ROTL = 9;
   private final byte[][] P = new byte[2][256];
   private int[] sBox = new int[4 * 256];
   private final int P_00 = 1;
   private final int P_01 = 0;
   private final int P_02 = 0;
   private final int P_03 = P_01 ^ 1;
   private final int P_04 = 1;
   private final int P_10 = 0;
   private final int P_11 = 0;
   private final int P_12 = 1;
   private final int P_13 = P_11 ^ 1;
   private final int P_14 = 0;
   private final int P_20 = 1;
   private final int P_21 = 1;
   private final int P_22 = 0;
   private final int P_23 = P_21 ^ 1;
   private final int P_24 = 0;
   private final int P_30 = 0;
   private final int P_31 = 1;
   private final int P_32 = 1;
   private final int P_33 = P_31 ^ 1;
   private final int P_34 = 1;
   /** Primitive polynomial for GF(256) */
   private final int GF256_FDBK =   0x169;
   private final int GF256_FDBK_2 = 0x169 / 2;
   private final int GF256_FDBK_4 = 0x169 / 4;
   /** MDS matrix */
   private final int[][] MDS = new int[4][256]; // blank final
   private final int RS_GF_FDBK = 0x14D; // field generator

//-------------------------------------------------------------------------
  private int getNibble (int x,int i) {  return (x >>> (4 * i)) & 0x0F; }
//-------------------------------------------------------------------------
   private final int LFSR1( int x ) {
      return (x >> 1) ^
            ((x & 0x01) != 0 ? GF256_FDBK_2 : 0);
   }

   private final int LFSR2( int x ) {
      return (x >> 2) ^
            ((x & 0x02) != 0 ? GF256_FDBK_2 : 0) ^
            ((x & 0x01) != 0 ? GF256_FDBK_4 : 0);
   }

   private final int Mx_1( int x ) { return x; }
   private final int Mx_X( int x ) { return x ^ LFSR2(x); }            // 5B
   private final int Mx_Y( int x ) { return x ^ LFSR1(x) ^ LFSR2(x); } // EF

  protected void Init (byte[] key) {
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
    int zeile=0,spalte=0;
    for (int j=255; j>=0; j--) { 
      P[0][255-j]=(byte)getNibble(pI[0][(255-j)/8],j%8);
      P[1][255-j]=(byte)getNibble(pI[1][(255-j)/8],j%8);
    }
/*    // nur für eine Kontrollausgabe
    System.out.println("------------------ Die S-Boxen ----------------");
    for (int j=0; j<256; j++) {
      System.out.print(Hex.byteToString(P[0][j])+" ");
      if (j%16=00) System.out.println();
    }
*/
    System.out.println("fertig!");        
    System.out.print("Erstelle MDS-Matrix ... ");
    bestimmeMDSMatrix();
    if (!decrypt) {
      System.out.print("Schlüssellänge (128,192,256): ");
      switch (Integer.parseInt(IO.Satz())) {
        case 256: key=new byte[32]; break;
        case 192: key=new byte[24]; break;
        default: key=new byte[16];		// entspricht 128 Bit
      }
      for (int i=0; i<key.length; i++) key[i]=(byte)(Math.random()*127);
    }
    System.out.println("Schlüssel: "+Hex.toString(key));
    generateSubKeys(key);
  }
  void bestimmeMDSMatrix() {
      int[] m1 = new int[2];
      int[] mX = new int[2];
      int[] mY = new int[2];
      int i, j;
      for (i = 0; i < 256; i++) {
         j = P[0][i]       & 0xFF; // compute all the matrix elements
         m1[0] = j;
         mX[0] = Mx_X(j) & 0xFF;
         mY[0] = Mx_Y(j) & 0xFF;

         j = P[1][i]       & 0xFF;
         m1[1] = j;
         mX[1] = Mx_X(j) & 0xFF;
         mY[1] = Mx_Y(j) & 0xFF;

         MDS[0][i] = m1[P_00] <<  0 | // fill matrix w/ above elements
                     mX[P_00] <<  8 |
                     mY[P_00] << 16 |
                     mY[P_00] << 24;
         MDS[1][i] = mY[P_10] <<  0 |
                     mY[P_10] <<  8 |
                     mX[P_10] << 16 |
                     m1[P_10] << 24;
         MDS[2][i] = mX[P_20] <<  0 |
                     mY[P_20] <<  8 |
                     m1[P_20] << 16 |
                     mY[P_20] << 24;
         MDS[3][i] = mX[P_30] <<  0 |
                     m1[P_30] <<  8 |
                     mY[P_30] << 16 |
                     mX[P_30] << 24;
      }
  }
   public void generateSubKeys (byte[] k) {
      int length = k.length;
      int k64Cnt = length / 8;
      int subkeyCnt = ROUND_SUBKEYS + 2*Runden;
      int[] k32e = new int[4]; // even 32-bit entities
      int[] k32o = new int[4]; // odd 32-bit entities
      int[] sBoxKey = new int[4];
      int i, j, offset = 0;
      for (i = 0, j = k64Cnt-1; i < 4 && offset < length; i++, j--) {
         k32e[i] = (k[offset++] & 0xFF)       |
                   (k[offset++] & 0xFF) <<  8 |
                   (k[offset++] & 0xFF) << 16 |
                   (k[offset++] & 0xFF) << 24;
         k32o[i] = (k[offset++] & 0xFF)       |
                   (k[offset++] & 0xFF) <<  8 |
                   (k[offset++] & 0xFF) << 16 |
                   (k[offset++] & 0xFF) << 24;
         sBoxKey[j] = RS_MDS_Encode( k32e[i], k32o[i] ); // reverse order
      }
      int q, A, B;
      int[] subKeys = new int[subkeyCnt];
      for (i = q = 0; i < subkeyCnt/2; i++, q += SK_STEP) {
         A = F32( k64Cnt, q        , k32e ); // A uses even key entities
         B = F32( k64Cnt, q+SK_BUMP, k32o ); // B uses odd  key entities
         B = B << 8 | B >>> 24;
         A += B;
         subKeys[2*i    ] = A;               // combine with a PHT
         A += B;
         subKeys[2*i + 1] = A << SK_ROTL | A >>> (32-SK_ROTL);
      }
      int k0 = sBoxKey[0];
      int k1 = sBoxKey[1];
      int k2 = sBoxKey[2];
      int k3 = sBoxKey[3];
      int b0, b1, b2, b3;
      for (i = 0; i < 256; i++) {
         b0 = b1 = b2 = b3 = i;
         switch (k64Cnt & 3) {
         case 1:
            sBox[      2*i  ] = MDS[0][(P[P_01][b0] & 0xFF) ^ b0(k0)];
            sBox[      2*i+1] = MDS[1][(P[P_11][b1] & 0xFF) ^ b1(k0)];
            sBox[0x200+2*i  ] = MDS[2][(P[P_21][b2] & 0xFF) ^ b2(k0)];
            sBox[0x200+2*i+1] = MDS[3][(P[P_31][b3] & 0xFF) ^ b3(k0)];
            break;
         case 0: // same as 4
            b0 = (P[P_04][b0] & 0xFF) ^ b0(k3);
            b1 = (P[P_14][b1] & 0xFF) ^ b1(k3);
            b2 = (P[P_24][b2] & 0xFF) ^ b2(k3);
            b3 = (P[P_34][b3] & 0xFF) ^ b3(k3);
         case 3:
            b0 = (P[P_03][b0] & 0xFF) ^ b0(k2);
            b1 = (P[P_13][b1] & 0xFF) ^ b1(k2);
            b2 = (P[P_23][b2] & 0xFF) ^ b2(k2);
            b3 = (P[P_33][b3] & 0xFF) ^ b3(k2);
         case 2: // 128-bit keys
            sBox[      2*i  ] = MDS[0][(P[P_01][(P[P_02][b0] & 0xFF) ^ b0(k1)] & 0xFF) ^ b0(k0)];
            sBox[      2*i+1] = MDS[1][(P[P_11][(P[P_12][b1] & 0xFF) ^ b1(k1)] & 0xFF) ^ b1(k0)];
            sBox[0x200+2*i  ] = MDS[2][(P[P_21][(P[P_22][b2] & 0xFF) ^ b2(k1)] & 0xFF) ^ b2(k0)];
            sBox[0x200+2*i+1] = MDS[3][(P[P_31][(P[P_32][b3] & 0xFF) ^ b3(k1)] & 0xFF) ^ b3(k0)];
         }
      }
     K = subKeys;
   }

   public byte[] blockEncrypt (byte[] in, int inOffset, Object sessionKey) {
      Object[] sk = (Object[]) sessionKey; // extract S-box and session key
      int[] sBox = (int[]) sk[0];
      int[] sKey = (int[]) sk[1];


      int x0 = (in[inOffset++] & 0xFF)       |
               (in[inOffset++] & 0xFF) <<  8 |
               (in[inOffset++] & 0xFF) << 16 |
               (in[inOffset++] & 0xFF) << 24;
      int x1 = (in[inOffset++] & 0xFF)       |
               (in[inOffset++] & 0xFF) <<  8 |
               (in[inOffset++] & 0xFF) << 16 |
               (in[inOffset++] & 0xFF) << 24;
      int x2 = (in[inOffset++] & 0xFF)       |
               (in[inOffset++] & 0xFF) <<  8 |
               (in[inOffset++] & 0xFF) << 16 |
               (in[inOffset++] & 0xFF) << 24;
      int x3 = (in[inOffset++] & 0xFF)       |
               (in[inOffset++] & 0xFF) <<  8 |
               (in[inOffset++] & 0xFF) << 16 |
               (in[inOffset++] & 0xFF) << 24;

      x0 ^= sKey[INPUT_WHITEN    ];
      x1 ^= sKey[INPUT_WHITEN + 1];
      x2 ^= sKey[INPUT_WHITEN + 2];
      x3 ^= sKey[INPUT_WHITEN + 3];

      int t0, t1;
      int k = ROUND_SUBKEYS;
      for (int R = 0; R < Runden; R += 2) {
         t0 = Fe32( sBox, x0, 0 );
         t1 = Fe32( sBox, x1, 3 );
         x2 ^= t0 + t1 + sKey[k++];
         x2  = x2 >>> 1 | x2 << 31;
         x3  = x3 << 1 | x3 >>> 31;
         x3 ^= t0 + 2*t1 + sKey[k++];

         t0 = Fe32( sBox, x2, 0 );
         t1 = Fe32( sBox, x3, 3 );
         x0 ^= t0 + t1 + sKey[k++];
         x0  = x0 >>> 1 | x0 << 31;
         x1  = x1 << 1 | x1 >>> 31;
         x1 ^= t0 + 2*t1 + sKey[k++];
      }
      x2 ^= sKey[OUTPUT_WHITEN    ];
      x3 ^= sKey[OUTPUT_WHITEN + 1];
      x0 ^= sKey[OUTPUT_WHITEN + 2];
      x1 ^= sKey[OUTPUT_WHITEN + 3];

      byte[] result = new byte[] {
         (byte) x2, (byte)(x2 >>> 8), (byte)(x2 >>> 16), (byte)(x2 >>> 24),
         (byte) x3, (byte)(x3 >>> 8), (byte)(x3 >>> 16), (byte)(x3 >>> 24),
         (byte) x0, (byte)(x0 >>> 8), (byte)(x0 >>> 16), (byte)(x0 >>> 24),
         (byte) x1, (byte)(x1 >>> 8), (byte)(x1 >>> 16), (byte)(x1 >>> 24),
      };

      return result;
   }
   public byte[] blockDecrypt (byte[] in, int inOffset, Object sessionKey) {
      Object[] sk = (Object[]) sessionKey; // extract S-box and session key
      int[] sBox = (int[]) sk[0];
      int[] sKey = (int[]) sk[1];


      int x2 = (in[inOffset++] & 0xFF)       |
               (in[inOffset++] & 0xFF) <<  8 |
               (in[inOffset++] & 0xFF) << 16 |
               (in[inOffset++] & 0xFF) << 24;
      int x3 = (in[inOffset++] & 0xFF)       |
               (in[inOffset++] & 0xFF) <<  8 |
               (in[inOffset++] & 0xFF) << 16 |
               (in[inOffset++] & 0xFF) << 24;
      int x0 = (in[inOffset++] & 0xFF)       |
               (in[inOffset++] & 0xFF) <<  8 |
               (in[inOffset++] & 0xFF) << 16 |
               (in[inOffset++] & 0xFF) << 24;
      int x1 = (in[inOffset++] & 0xFF)       |
               (in[inOffset++] & 0xFF) <<  8 |
               (in[inOffset++] & 0xFF) << 16 |
               (in[inOffset++] & 0xFF) << 24;

      x2 ^= sKey[OUTPUT_WHITEN    ];
      x3 ^= sKey[OUTPUT_WHITEN + 1];
      x0 ^= sKey[OUTPUT_WHITEN + 2];
      x1 ^= sKey[OUTPUT_WHITEN + 3];

      int k = ROUND_SUBKEYS + 2*Runden - 1;
      int t0, t1;
      for (int R = 0; R < Runden; R += 2) {
         t0 = Fe32( sBox, x2, 0 );
         t1 = Fe32( sBox, x3, 3 );
         x1 ^= t0 + 2*t1 + sKey[k--];
         x1  = x1 >>> 1 | x1 << 31;
         x0  = x0 << 1 | x0 >>> 31;
         x0 ^= t0 + t1 + sKey[k--];

         t0 = Fe32( sBox, x0, 0 );
         t1 = Fe32( sBox, x1, 3 );
         x3 ^= t0 + 2*t1 + sKey[k--];
         x3  = x3 >>> 1 | x3 << 31;
         x2  = x2 << 1 | x2 >>> 31;
         x2 ^= t0 + t1 + sKey[k--];
      }
      x0 ^= sKey[INPUT_WHITEN    ];
      x1 ^= sKey[INPUT_WHITEN + 1];
      x2 ^= sKey[INPUT_WHITEN + 2];
      x3 ^= sKey[INPUT_WHITEN + 3];

      byte[] result = new byte[] {
         (byte) x0, (byte)(x0 >>> 8), (byte)(x0 >>> 16), (byte)(x0 >>> 24), 
         (byte) x1, (byte)(x1 >>> 8), (byte)(x1 >>> 16), (byte)(x1 >>> 24), 
         (byte) x2, (byte)(x2 >>> 8), (byte)(x2 >>> 16), (byte)(x2 >>> 24), 
         (byte) x3, (byte)(x3 >>> 8), (byte)(x3 >>> 16), (byte)(x3 >>> 24),
      };

      return result;
   }

   /** A basic symmetric encryption/decryption test. */ 
   public boolean self_test() { return self_test(BlockGroesse); }


// own methods
//...........................................................................

   private final int b0( int x ) { return  x         & 0xFF; }
   private final int b1( int x ) { return (x >>>  8) & 0xFF; }
   private final int b2( int x ) { return (x >>> 16) & 0xFF; }
   private final int b3( int x ) { return (x >>> 24) & 0xFF; }

   /**
    * Use (12, 8) Reed-Solomon code over GF(256) to produce a key S-box
    * 32-bit entity from two key material 32-bit entities.
    *
    * @param  k0  1st 32-bit entity.
    * @param  k1  2nd 32-bit entity.
    * @return  Remainder polynomial generated using RS code
    */
   private final int RS_MDS_Encode( int k0, int k1) {
      int r = k1;
      for (int i = 0; i < 4; i++) // shift 1 byte at a time
         r = RS_rem( r );
      r ^= k0;
      for (int i = 0; i < 4; i++)
         r = RS_rem( r );
      return r;
   }

   /*
    * Reed-Solomon code parameters: (12, 8) reversible code:<p>
    * <pre>
    *   g(x) = x**4 + (a + 1/a) x**3 + a x**2 + (a + 1/a) x + 1
    * </pre>
    * where a = primitive root of field generator 0x14D
    */
   private final int RS_rem( int x ) {
      int b  =  (x >>> 24) & 0xFF;
      int g2 = ((b  <<  1) ^ ( (b & 0x80) != 0 ? RS_GF_FDBK : 0 )) & 0xFF;
      int g3 =  (b >>>  1) ^ ( (b & 0x01) != 0 ? (RS_GF_FDBK >>> 1) : 0 ) ^ g2 ;
      int result = (x << 8) ^ (g3 << 24) ^ (g2 << 16) ^ (g3 << 8) ^ b;
      return result;
   }

   private final int F32( int k64Cnt, int x, int[] k32 ) {
      int b0 = b0(x);
      int b1 = b1(x);
      int b2 = b2(x);
      int b3 = b3(x);
      int k0 = k32[0];
      int k1 = k32[1];
      int k2 = k32[2];
      int k3 = k32[3];

      int result = 0;
      switch (k64Cnt & 3) {
      case 1:
         result =
            MDS[0][(P[P_01][b0] & 0xFF) ^ b0(k0)] ^
            MDS[1][(P[P_11][b1] & 0xFF) ^ b1(k0)] ^
            MDS[2][(P[P_21][b2] & 0xFF) ^ b2(k0)] ^
            MDS[3][(P[P_31][b3] & 0xFF) ^ b3(k0)];
         break;
      case 0:  // same as 4
         b0 = (P[P_04][b0] & 0xFF) ^ b0(k3);
         b1 = (P[P_14][b1] & 0xFF) ^ b1(k3);
         b2 = (P[P_24][b2] & 0xFF) ^ b2(k3);
         b3 = (P[P_34][b3] & 0xFF) ^ b3(k3);
      case 3:
         b0 = (P[P_03][b0] & 0xFF) ^ b0(k2);
         b1 = (P[P_13][b1] & 0xFF) ^ b1(k2);
         b2 = (P[P_23][b2] & 0xFF) ^ b2(k2);
         b3 = (P[P_33][b3] & 0xFF) ^ b3(k2);
      case 2:                             // 128-bit keys (optimize for this case)
         result =
            MDS[0][(P[P_01][(P[P_02][b0] & 0xFF) ^ b0(k1)] & 0xFF) ^ b0(k0)] ^
            MDS[1][(P[P_11][(P[P_12][b1] & 0xFF) ^ b1(k1)] & 0xFF) ^ b1(k0)] ^
            MDS[2][(P[P_21][(P[P_22][b2] & 0xFF) ^ b2(k1)] & 0xFF) ^ b2(k0)] ^
            MDS[3][(P[P_31][(P[P_32][b3] & 0xFF) ^ b3(k1)] & 0xFF) ^ b3(k0)];
         break;
      }
      return result;
   }

   private final int Fe32( int[] sBox, int x, int R ) {
      return sBox[        2*_b(x, R  )    ] ^
             sBox[        2*_b(x, R+1) + 1] ^
             sBox[0x200 + 2*_b(x, R+2)    ] ^
             sBox[0x200 + 2*_b(x, R+3) + 1];
   }

   private final int _b( int x, int N) {
      int result = 0;
      switch (N%4) {
      case 0: result = b0(x); break;
      case 1: result = b1(x); break;
      case 2: result = b2(x); break;
      case 3: result = b3(x); break;
      }
      return result;
   }
// utility methods (from cryptix.util.core ArrayUtil and Hex classes)
//...........................................................................
   
   /** @return True iff the arrays have identical contents. */
   private boolean areEqual (byte[] a, byte[] b) {
      int aLength = a.length;
      if (aLength != b.length)
         return false;
      for (int i = 0; i < aLength; i++)
         if (a[i] != b[i])
            return false;
      return true;
   }

   /**
    * Returns a string of 8 hexadecimal digits (most significant
    * digit first) corresponding to the integer <i>n</i>, which is
    * treated as unsigned.
    */
   private String intToString (int n) {
      char[] buf = new char[8];
      for (int i = 7; i >= 0; i--) {
         buf[i] = HEX_DIGITS[n & 0x0F];
         n >>>= 4;
      }
      return new String(buf);
   }

   /**
    * Returns a string of hexadecimal digits from a byte array. Each
    * byte is converted to 2 hex symbols.
    */
   private String toString (byte[] ba) {
      return toString(ba, 0, ba.length);
   }
   private String toString (byte[] ba, int offset, int length) {
      char[] buf = new char[length * 2];
      for (int i = offset, j = 0, k; i < offset+length; ) {
         k = ba[i++];
         buf[j++] = HEX_DIGITS[(k >>> 4) & 0x0F];
         buf[j++] = HEX_DIGITS[ k      & 0x0F];
      }
      return new String(buf);
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
