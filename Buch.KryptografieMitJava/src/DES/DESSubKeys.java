
public final class DESSubKeys {
  private static final int
    ROUNDS     = 16,  				// number of encryption/decryption rounds
    BLOCK_SIZE =  8,  				// DES block size in bytes
    KEY_LENGTH =  8,  				// DES key length in bytes
    INTERNAL_KEY_LENGTH = 2 * ROUNDS; 		// number of elements in key schedule
    private int[] sKey = new int[INTERNAL_KEY_LENGTH];

    /** Table for PC2 permutations in key schedule computation. */
    private static final int[] SKB = new int[8 * 64];

    /** Table for S-boxes and permutations, used in encrypt_base. */
    private static final int SP_TRANS[] = new int[8 * 64];

    /** Build the SKB and SP_TRANS tables */
    static
{
// build the SKB table
// represent the bit number that each permutated bit is derived from
// according to FIPS-46
      String cd =
 "D]PKESYM`UBJ\\@RXA`I[T`HC`LZQ"+"\\PB]TL`[C`JQ@Y`HSXDUIZRAM`EK";
      int j, s, bit;
      int count = 0;
      int offset = 0;
      for (int i = 0; i < cd.length(); i++)
{
        s = cd.charAt(i) - '@';
        if (s != 32)
{
          bit = 1 << count++;
          for (j = 0; j < 64; j++)
            if ((bit & j) != 0) SKB[offset + j] |= 1 << s;
          if (count == 6)
{
            offset += 64;
            count = 0;
          }
        }
      }
      String spt =
				// build the SP_TRANS table
        "g3H821:80:H03BA0@N1290BAA88::3112aIH8:8282@0@AH0:1W3A8P810@22;22"+
        "A18^@9H9@129:<8@822`?:@0@8PH2H81A19:G1@03403A0B1;:0@1g192:@919AA"+
        "0A109:W21492H@0051919811:215011139883942N8::3112A2:31981jM118::A"+
        "101@I88:1aN0<@030128:X;811`920:;H0310D1033@W980:8A4@804A3803o1A2"+
        "021B2:@1AH023GA:8:@81@@12092B:098042P@:0:A0HA9>1;289:@1804:40Ph="+
        "1:H0I0HP0408024bC9P8@I808A;@0@0PnH0::8:19J@818:@iF0398:8A9H0<13@"+
        "001@11<8;@82B01P0a2989B:0AY0912889bD0A1@B1A0A0AB033O91182440A9P8"+
        "@I80n@1I03@1J828212A`A8:12B1@19A9@9@8^B:0@H00<82AB030bB840821Q:8"+
        "310A302102::A1::20A1;8";
// [526 chars, 3156 bits]
// The theory is that each bit position in each int of SP_TRANS is
// set in exactly 32 entries. We keep track of set bits.
      offset = 0;
      int k, c, param;
      for (int i = 0; i < 32; i++) {		// each bit position

        k = -1; 				// pretend the -1th bit was set
        bit = 1 << i;
        for (j = 0; j < 32; j++) { 		// each set bit
// Each character consists of two three-bit values:
          c = spt.charAt(offset >> 1) - '0' >> (offset & 1) * 3 & 7;
          offset++;
          if (c < 5)
{
// values 0...4 indicate a set bit 1...5 positions
// from the previous set bit
            k += c + 1;
            SP_TRANS[k] |= bit;
            continue;
          }
// other values take at least an additional parameter:
// the next value in the sequence.
          param = spt.charAt(offset >> 1) - '0' >> (offset & 1) * 3 & 7;
          offset++;
          if (c == 5)
{
// indicates a bit set param+6 positions from
// the previous set bit
            k += param + 6;
            SP_TRANS[k] |= bit;
          }
          else if (c == 6)
{
// indicates a bit set (param * 64) + 1 positions
// from the previous set bit
            k += (param << 6) + 1;
            SP_TRANS[k] |= bit;
          }
          else
{
// indicates that we should skip (param * 64) positions,
// then process the next value which will be in the range 0...4.
            k += param << 6;
            j--;
          }
        }
      }
    }

// Instance variables
// ...................................................................

  protected void coreInit(byte[] userkey, boolean decrypt) {
    showInt.printByteArray(userkey);
    int i = 0;
    int c = (userkey[i++] & 0xFF)       |
 (userkey[i++] & 0xFF) <<  8 |
            (userkey[i++] & 0xFF) << 16 |
 (userkey[i++] & 0xFF) << 24;
    int d = (userkey[i++] & 0xFF)       |
 (userkey[i++] & 0xFF) <<  8 |
            (userkey[i++] & 0xFF) << 16 |
 (userkey[i++] & 0xFF) << 24;
//
System.out.print("c: "); showInt.printInt(c);
System.out.print("d: "); showInt.printInt(d);
    int t = ((d >>> 4) ^ c) & 0x0F0F0F0F;
System.out.print("t: "); showInt.printInt(t);
    c ^= t;
System.out.print("c: "); showInt.printInt(c);
    d ^= t << 4;
System.out.print("d: "); showInt.printInt(d);
    t = ((c << 18) ^ c) & 0xCCCC0000;
System.out.print("t: "); showInt.printInt(t);
    c ^= t ^ t >>> 18;
System.out.print("c: "); showInt.printInt(c);
    t = ((d << 18) ^ d) & 0xCCCC0000;
System.out.print("t: "); showInt.printInt(t);
    d ^= t ^ t >>> 18;
System.out.print("d: "); showInt.printInt(d);
    t = ((d >>> 1) ^ c) & 0x55555555;
System.out.print("t: "); showInt.printInt(t);
    c ^= t;
System.out.print("c: "); showInt.printInt(c);
    d ^= t << 1;
System.out.print("d: "); showInt.printInt(d);
    t = ((c >>> 8) ^ d) & 0x00FF00FF;
System.out.print("t: "); showInt.printInt(t);
    d ^= t;
System.out.print("d: "); showInt.printInt(d);
    c ^= t << 8;
System.out.print("c: "); showInt.printInt(c);
    t = ((d >>> 1) ^ c) & 0x55555555;
System.out.print("t: "); showInt.printInt(t);
    c ^= t;
System.out.print("c: "); showInt.printInt(c);
    d ^= t << 1;
System.out.print("d: "); showInt.printInt(d);
System.out.println();
//
    d = (d & 0x000000FF) <<  16 | (d & 0x0000FF00)        |
        (d & 0x00FF0000) >>> 16 | (c & 0xF0000000) >>>  4;
System.out.print("d: "); showInt.printInt(d);
    c &= 0x0FFFFFFF;
System.out.print("c: "); showInt.printInt(c);
    int s;
    int j = 0;
    for (i = 0; i < ROUNDS; i++) {
      System.out.println("i="+i);
      if ((0x7EFC >> i & 1) == 1) {
        c = (c >>> 2 | c << 26) & 0x0FFFFFFF;
System.out.print("c: "); showInt.printInt(c);
        d = (d >>> 2 | d << 26) & 0x0FFFFFFF;
System.out.print("d: "); showInt.printInt(d);
      }
      else {
        c = (c >>> 1 | c << 27) & 0x0FFFFFFF;
System.out.print("c: "); showInt.printInt(c);
        d = (d >>> 1 | d << 27) & 0x0FFFFFFF;
System.out.print("d: "); showInt.printInt(d);
      }
      s = SKB[           c         & 0x3F                        ] |
          SKB[0x040 | (((c >>>  6) & 0x03) | ((c >>>  7) & 0x3C))] |
          SKB[0x080 | (((c >>> 13) & 0x0F) | ((c >>> 14) & 0x30))] |
          SKB[0x0C0 | (((c >>> 20) & 0x01) | ((c >>> 21) & 0x06)
   | ((c >>> 22) & 0x38))];
      t = SKB[0x100 | ( d         & 0x3F                      )] |
          SKB[0x140 | (((d >>>  7) & 0x03) | ((d >>>  8) & 0x3c))] |
          SKB[0x180 | ((d >>> 15) & 0x3F                      )] |
          SKB[0x1C0 | (((d >>> 21) & 0x0F) | ((d >>> 22) & 0x30))];
System.out.print("t: "); showInt.printInt(t);
System.out.print("s: "); showInt.printInt(s);
          sKey[j++] = t <<  16 | (s & 0x0000FFFF);
          s         = s >>> 16 | (t & 0xFFFF0000);
System.out.print("s: "); showInt.printInt(s <<   4 |  s >>> 28);
          sKey[j++] = s <<   4 |  s >>> 28;
    }
// FIXME: needs a clean-up ------------------------------------
    if(decrypt) {		// Reihenfolge der subkeys umkehren
      for(int jjj=0; jjj<16; jjj++) {
        int tmp = sKey[jjj];
        sKey[jjj] = sKey[31-jjj];
        sKey[31-jjj] = tmp;
      }
      for(int kkk=0; kkk<32; kkk+=2) {
        int tmp1 = sKey[kkk];
        sKey[kkk] = sKey[kkk+1];
        sKey[kkk+1] = tmp1;
      }
    }
  }
  
  public DESSubKeys() {
    byte[] Key = new byte[KEY_LENGTH];
    for (int i=0; i<KEY_LENGTH;i++)
      Key[i]=(byte)(Math.random()*256.0f);
    coreInit(Key,false);
  }
  public DESSubKeys(byte[] Key) {
    coreInit(Key,false);
  }
  public static void main (String[] args) {
    DESSubKeys demo;
    if (args.length==8) {
      byte[] key = new byte[8];
      for (int i=0; i<8; i++)
        key[i] = (byte)Integer.parseInt(args[i]);
      demo = new DESSubKeys(key);
    }
    else
      demo = new DESSubKeys();
  }

}
