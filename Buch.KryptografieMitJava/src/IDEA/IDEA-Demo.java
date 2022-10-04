public final class IDEA {
  private static final int
    ROUNDS              = 8,
    BLOCK_SIZE          = 8,
    KEY_LENGTH          = 16,
    INTERNAL_KEY_LENGTH = 52,
    MaxCharProZeile	= 100;

  private static byte[]key = new byte[KEY_LENGTH];
  private short[] ks = new short[INTERNAL_KEY_LENGTH];	// Instance variables

//-------------------------------------------------------------------------
  public IDEA(String DatName) {				// Constructor encrypt	
    System.out.println("Starte Verschlüsselung ...");
    Datei P;
    byte[] p;
    if (DatName.length()>0) P = new Datei(DatName);
    else		    P = new Datei();
    System.out.print("Lese Datei: "+P.dateiname+" ...");
    p = P.lies(); 	
    System.out.println();
    System.out.println(p.length+" Bytes)");
    Init(false);					// Schlüssel generieren
    System.out.println("Schlüssel: "+Hex.toString(key));
    int AnzahlBloecke = (p.length+7)/8;			// Anzahl 64 Bit-Blöcke
    byte[] tmp = new byte[AnzahlBloecke*8];
    byte[] out = new byte[AnzahlBloecke*8];
    System.out.println(AnzahlBloecke+" 64-Bit-Blöcke = "+(AnzahlBloecke*8)+" Bytes");
    System.arraycopy(p,0,tmp,0,p.length);		// p nach tmp kopieren
    for (int i=0; i<AnzahlBloecke; i++) {
      coreCrypt(tmp,i*8,out,i*8);
    }
    P = new Datei();
    P.schreib(out);
    System.out.print("\nVerschlüsselte Datei ausgeben? (J/N):");
    if (IO.JaNein()) 
      for (int i=0; i<out.length; i++) {
        if ((i%MaxCharProZeile)==0) System.out.println();
        IO.printChar(out[i]);
      } 
    System.exit(0);
  }
//-------------------------------------------------------------------------
  public IDEA(byte[] skey, String DatName) {		// Constructor decrypt
    System.out.println("Starte Entschlüsselung ...");
    key = skey;
    System.out.println("Schlüssel: "+Hex.toString(key,0,16));
    Datei P;
    byte[] p;
    P = new Datei(DatName);
    System.out.print("Lese Datei: "+P.dateiname+" ...");
    p = P.lies(); 	
    System.out.println(p.length+" Bytes)");
    Init(true);						// Teilschlüssel 
    int AnzahlBloecke = (p.length+7)/8;			// Anzahl 64 Bit-Blöcke
    byte[] tmp = new byte[AnzahlBloecke*8];
    byte[] out = new byte[AnzahlBloecke*8];
    System.out.println(AnzahlBloecke+" 64-Bit-Blöcke"+(AnzahlBloecke*8)+" Bytes");
    System.arraycopy(p,0,tmp,0,p.length);		// p nach tmp kopieren
    for (int i=0; i<AnzahlBloecke; i++) {
      coreCrypt(tmp,i*8,out,i*8);
    }
    P = new Datei();
    P.schreib(out);
    System.out.print("\nEntschlüsselte Datei ausgeben? (J/N):");
    if (IO.JaNein()) 
      for (int i=0; i<out.length; i++) {
        if ((i%MaxCharProZeile)==0) System.out.println();
        IO.printChar(out[i]);
      } 
    System.exit(0);
  }
//-------------------------------------------------------------------------
  protected void Init(boolean decrypt) {
    if (!decrypt) {
      for (int i=0; i<16; i++)
        key[i]=(byte)(Math.random()*127.0);
      makeKey();					// internen key generieren
    }
    else {
      makeKey();
      invertKey();
    }
  }
//-------------------------------------------------------------------------
  protected void coreCrypt(byte[] in,int inOffset,byte[] out,int outOffset) {
    blockEncrypt(in, inOffset, out, outOffset);
  }
/**
* IDEA encryption/decryption algorithm using the current key schedule.
*
* @param  in       an array containing the plaintext block
* @param  inOffset the starting offset of the plaintext block
* @param  out      an array containing the ciphertext block
* @param  inOffset the starting offset of the ciphertext block
*/
  private void blockEncrypt(byte[] in,int inOffset,
                            byte[] out,int outOffset) {
    short
      x1 = (short)(((in[inOffset++]&0xFF) << 8) | (in[inOffset++]&0xFF)),
      x2 = (short)(((in[inOffset++]&0xFF) << 8) | (in[inOffset++]&0xFF)),
      x3 = (short)(((in[inOffset++]&0xFF) << 8) | (in[inOffset++]&0xFF)),
      x4 = (short)(((in[inOffset++]&0xFF) << 8) | (in[inOffset  ]&0xFF));
    short s2, s3;
    int i     = 0;
    int round = ROUNDS;
    while (round-- > 0) {
      x1 = mul(x1, ks[i++]);
      x2 += ks[i++];
      x3 += ks[i++];
      x4 = mul(x4, ks[i++]);
      s3 = x3;
      x3 = mul(x1 ^ x3, ks[i++]);
      s2 = x2;
      x2 = mul(x3 + (x2 ^ x4), ks[i++]);
      x3 += x2;
      x1 ^= x2;
      x4 ^= x3;
      x2 ^= s3;
      x3 ^= s2;
    }
    s2 = mul(x1, ks[i++]);
    out[outOffset++] = (byte)(s2 >>> 8);
    out[outOffset++] = (byte) s2;
    s2 = (short)(x3 + ks[i++]);
    out[outOffset++] = (byte)(s2 >>> 8);
    out[outOffset++] = (byte) s2;
    s2 = (short)(x2 + ks[i++]);
    out[outOffset++] = (byte)(s2 >>> 8);
    out[outOffset++] = (byte) s2;
    s2 = mul(x4, ks[i]);
    out[outOffset++] = (byte)(s2 >>> 8);
    out[outOffset  ] = (byte) s2;
  }
//-------------------------------------------------------------------------
  private void makeKey() {
// Expand user key of 128 bits to full 832 bits of encryption key.
    ks[0] = (short)((key[ 0] & 0xFF) << 8 | (key[ 1] & 0xFF));
    ks[1] = (short)((key[ 2] & 0xFF) << 8 | (key[ 3] & 0xFF));
    ks[2] = (short)((key[ 4] & 0xFF) << 8 | (key[ 5] & 0xFF));
    ks[3] = (short)((key[ 6] & 0xFF) << 8 | (key[ 7] & 0xFF));
    ks[4] = (short)((key[ 8] & 0xFF) << 8 | (key[ 9] & 0xFF));
    ks[5] = (short)((key[10] & 0xFF) << 8 | (key[11] & 0xFF));
    ks[6] = (short)((key[12] & 0xFF) << 8 | (key[13] & 0xFF));
    ks[7] = (short)((key[14] & 0xFF) << 8 | (key[15] & 0xFF));
    for (int i=0, zoff=0, j=8; j < INTERNAL_KEY_LENGTH; i&=7, j++) {
      i++;
      ks[i+7+zoff]=(short)((ks[(i&7)+zoff] << 9) |((ks[((i+1)&7)+zoff] >>> 7) & 0x1FF));
      zoff += i&8;
    }
  }
//-------------------------------------------------------------------------
  private void invertKey() {
    int i, j = 4, k = INTERNAL_KEY_LENGTH - 1;
    short[] temp = new short[INTERNAL_KEY_LENGTH];
    temp[k--] = inv(ks[3]);
    temp[k--] = (short) -ks[2];
    temp[k--] = (short) -ks[1];
    temp[k--] = inv(ks[0]);
    for (i = 1; i < ROUNDS; i++, j += 6) {
      temp[k--] = ks[j + 1];
      temp[k--] = ks[j];
      temp[k--] = inv(ks[j + 5]);
      temp[k--] = (short) -ks[j + 3];
      temp[k--] = (short) -ks[j + 4];
      temp[k--] = inv(ks[j + 2]);
    }
    temp[k--] = ks[j + 1];
    temp[k--] = ks[j];
    temp[k--] = inv(ks[j + 5]);
    temp[k--] = (short) -ks[j + 4];
    temp[k--] = (short) -ks[j + 3];
    temp[k--] = inv(ks[j + 2]);
    System.arraycopy(temp, 0, ks, 0, INTERNAL_KEY_LENGTH);
  }
//-------------------------------------------------------------------------
  private static short inv( short xx ) {
    int x = xx & 0xFFFF;         		// only lower 16 bits
    if (x <= 1)
      return (short)x;         			// 0 and 1 are self-inverse
    int t1 = 0x10001/x;        			// Since x >= 2, this fits into 16 bits
    int y = 0x10001%x;
    if (y == 1)
      return (short)(1 - t1);
    int t0 = 1;
    int q;
    do {
      q = x / y;
      x = x % y;
      t0 += q * t1;
      if (x == 1)
        return (short)t0;
      q = y / x;
      y %= x;
      t1 += q * t0;
    }
    while (y != 1);
    return (short)(1 - t1);
  }
//-------------------------------------------------------------------------
  private static short mul(int a,int b) {
    a &= 0xFFFF;
    b &= 0xFFFF;
    int p;
    if (a != 0) {
      if (b != 0) {
        p = a * b;
        b = p & 0xFFFF;
        a = p >>> 16;
        return (short)(b - a + (b < a ? 1 : 0));
      }
      else return (short)(1 - a);
    }
    else return (short)(1 - b);
  }
//-------------------------------------------------------------------------
  public static void main (String[] arg) {
    IDEA-Demo makeIDEA;
    switch(arg.length) {
      case 0: makeIDEA = new IDEA("");
              break;
      case 1: makeIDEA = new IDEA(arg[0]);
              break;
      case 2: makeIDEA = new IDEA(Hex.fromString(arg[0]),arg[1]); // decrypt
              break;
    }
  }
}
