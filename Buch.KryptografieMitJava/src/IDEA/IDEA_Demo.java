public final class IDEA_Demo {
  private static final int
    ROUNDS              = 8,
    BLOCK_SIZE          = 8,
    INTERNAL_KEY_LENGTH = 52,
    MaxCharProZeile	= 100;

  static byte[]key = new byte[16];
  short[] ks = new short[INTERNAL_KEY_LENGTH];			// Instanzvariable

//-----------------------------------------------------------
  public IDEA_Demo() {						// Constructor encrypt	
    System.out.println("Grundschlüssel: "+Hex.toString(key));
    System.out.print("Verschlüsseln? (J/N):");
    if (IO.JaNein()) {
      byte[] p= { 67,111,114,110,101,108,105,97 };		// Originaltext
      System.out.println(p.length+" Bytes)");
      System.out.println("Starte Verschlüsselung ...");
      Init(false);						// Schlüssel generieren
      int AnzahlBloecke = (p.length+7)/8;			// Anzahl 64 Bit-Blöcke
      byte[] tmp = new byte[AnzahlBloecke*8];
      byte[] out = new byte[AnzahlBloecke*8];
      System.out.println(AnzahlBloecke+" 64-Bit-Blöcke = "+(AnzahlBloecke*8)+" Bytes");
      System.arraycopy(p,0,tmp,0,p.length);			// p nach tmp kopieren
      for (int i=0; i<AnzahlBloecke; i++) {
        blockCrypt(tmp,i*8,out,i*8);
      }
      System.out.println(Hex.toString(out));
      System.out.println();
      System.exit(0);
    }
    else {
      byte[]p = Hex.fromString("ef95bb42ee478d8d");		// Chiffre vorgeben
      System.out.println("Starte Entschlüsselung ...");
      System.out.println(p.length+" Bytes)");
      Init(true);						// Teilschlüssel 
      int AnzahlBloecke = (p.length+7)/8;			// Anzahl 64 Bit-Blöcke
      byte[] tmp = new byte[AnzahlBloecke*8];
      byte[] out = new byte[AnzahlBloecke*8];
      System.out.println(AnzahlBloecke+" 64-Bit-Blöcke"+(AnzahlBloecke*8)+" Bytes");
      System.arraycopy(p,0,tmp,0,p.length);			// p nach tmp kopieren
      for (int i=0; i<AnzahlBloecke; i++) {
        blockCrypt(tmp,i*8,out,i*8);
      }
      System.out.println(Hex.toString(out));
      for (int i=0; i<out.length; i++) System.out.print((char)out[i]);
      System.out.println();
      System.exit(0);
    }
  }
//-----------------------------------------------------------
  protected void Init(boolean decrypt) {
    makeKey();
    if (decrypt) invertKey();
  }
//-----------------------------------------------------------
  private void blockCrypt(byte[] in,int inOffset,
                          byte[] out,int outOffset) {
    short
      x1 = (short)(((in[inOffset++]&0xFF) << 8) | (in[inOffset++]&0xFF)),
      x2 = (short)(((in[inOffset++]&0xFF) << 8) | (in[inOffset++]&0xFF)),
      x3 = (short)(((in[inOffset++]&0xFF) << 8) | (in[inOffset++]&0xFF)),
      x4 = (short)(((in[inOffset++]&0xFF) << 8) | (in[inOffset  ]&0xFF));
    System.out.println("4 16-Bit-Datenblöcke:\nX0="+Hex.shortToString(x1)+
                       " X1="+Hex.shortToString(x2)+
		       " X2="+Hex.shortToString(x3)+
                       " X3="+Hex.shortToString(x4));
    short s2, s3;
    int i = 0;
    int round = ROUNDS;
    while (round-- > 0) {
      System.out.println((ROUNDS-round)+". Runde:");
      x1 = mul(x1, ks[i++]);
      x2 += ks[i++];
      x3 += ks[i++];
      x4 = mul(x4, ks[i++]);
      System.out.println("Ebene 1: Links="+Hex.shortToString(x1)+
                         " Rechts="+Hex.shortToString(x4));
      System.out.println("Ebene 2: Links="+Hex.shortToString(x2)+
                         " Rechts="+Hex.shortToString(x3));
      s3 = x3;
      x3 = (short)(x1^x3);
      s2 = x2;
      x2 = (short)(x2^x4);
      System.out.println("Ebene 3: Links="+Hex.shortToString(x3)+
                         " Rechts="+Hex.shortToString(x2));
      x3 = mul(x3, ks[i++]);
      x2 = (short)(x3+x2);
      System.out.println("Ebene 4: Links="+Hex.shortToString(x3)+
                         " Rechts="+Hex.shortToString(x2));
      x2 = mul(x2, ks[i++]);
      x3 += x2;
      System.out.println("Ebene 5: Links="+Hex.shortToString(x3)+
                         " Rechts="+Hex.shortToString(x2));
      x1 ^= x2;
      x4 ^= x3;
      x2 ^= s3;
      x3 ^= s2;
      System.out.println("Ebene 6: Links="+Hex.shortToString(x2)+
                         " Rechts="+Hex.shortToString(x3));
      System.out.println("Ebene 7: Links="+Hex.shortToString(x1)+
                         " Rechts="+Hex.shortToString(x4));
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
//-------------------------------------------------------------
  private void makeKey() {
    System.out.print("Die Teilschlüssel:");
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
    for (int i=0; i<INTERNAL_KEY_LENGTH;i++) {
      if (i%6==0) System.out.println();
      System.out.print("K("+i+")="+Hex.shortToString(ks[i])+" ");
    }
    System.out.println();
  }
//--------------------------------------------------------------
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
//------------------------------------------------------------
  private static short inv( short xx ) {
    int x = xx & 0xFFFF;         			// only lower 16 bits
    if (x <= 1)
      return (short)x;         				// 0 and 1 are self-inverse
    int t1 = 0x10001/x;        				// Since x >= 2, this fits into 16 bits
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
//------------------------------------------------------------
  private static short mul(int a,int b) {
    a &= 0xFFFF;
    b &= 0xFFFF;
//    System.out.println("Multiplikation von: "+Hex.intToString(a)+
//                       " und "+Hex.intToString(b));
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
//-------------------------------------------------------------
  public static void main (String[] arg) {
   key = Hex.fromString("0123456789abcdeffedcba9876543210");
   IDEA_Demo makeIDEA=new IDEA_Demo();
  }
}