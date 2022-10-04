public final class MD5 {
    private static final int HashLaenge = 16;	// 16*8Bit = 128
    private static final int DatenLaenge = 64;	// 64*8Bit = 512
    private int[] digest;			// die ints des Hashwertes
    private int[] y;				// nichtlineare Funktion
    private final int[] z= {			// Bytenummer im Block
      0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,
      1,6,11,0,5,10,15,4,9,14,3,8,13,2,7,12,
      5,8,11,14,1,4,7,10,13,0,3,6,9,12,15,2,
      0,7,14,5,12,3,10,1,8,15,6,13,4,11,2,9 };
    private final int[] s= {			// Shiftgröße
      7,12,17,22,7,12,17,22,7,12,17,22,7,12,17,22,
      5,9,14,20,5,9,14,20,5,9,14,20,5,9,14,20,
      4,11,16,23,4,11,16,23,4,11,16,23,4,11,16,23,
      6,10,15,21,6,10,15,21,6,10,15,21,6,10,15,21 };
  public MD5() {				// Konstruktor
    init();
    reset();
  }
  private void init() {
    digest = new int[HashLaenge/4];		// [0..3]  128 Bit
    y = new int[64];				// sin(t)
  }
  private void reset() {
    digest[0] = 0x67452301;			// die vier Startwerte
    digest[1] = 0xEFCDAB89;
    digest[2] = 0x98BADCFE;
    digest[3] = 0x10325476;
    double ZweiHoch32=(double)65536*(double)65536;  // 2^32
    for (int j=1; j<65; j++)
      y[j-1]=(int)((long)(Math.abs(Math.sin((double)j)*ZweiHoch32)));
  }
  private static void byte2int(byte[] src, int srcOffset,
                               int[] dst, int dstOffset, int length) {
    while (length-- > 0) {            		// Little endian
      dst[dstOffset++] = (src[srcOffset++] & 0xFF)        |
                        ((src[srcOffset++] & 0xFF) <<  8) |
                        ((src[srcOffset++] & 0xFF) << 16) |
                        ((src[srcOffset++] & 0xFF) << 24);
      }
    }      
  private byte[] Haschcode(byte[] in) {
    int pos = in.length; 			// ab wann auffüllen?
    long bc = pos*8;				// DatenLänge in Bitform
    int n512 = (pos*8+65)/512+1;		// n 512-er Blöcke
    System.out.println("Anzahl Bytes="+pos);
    System.out.println("Anzahl Blöcke="+n512);
    byte[] tmp = new byte[n512*64];		// min 1 Block
    int[] data = new int[DatenLaenge/4];	// [0..15] 512 Bit
    int block=0;
    if (pos != 0) 				// Daten vorhanden?
      System.arraycopy(in, 0, tmp, 0, pos);	// in nach tmp kopieren
    for (block=0; block<n512-1; block++) {	// alle kompletten Blöcke
      byte2int(tmp,block*DatenLaenge,data,0,DatenLaenge/4);
      transform(data);
    }
//    
// auffüllen mit 1, gefolgt von Nullen + länges als 64 bit
// Nullen sind durch new byte[] bereits vordefiniert!!!
//
    tmp[pos++] = -128; 				// (byte)0x80=128 ("1")
    byte2int(tmp,block*DatenLaenge,data,0,(DatenLaenge/4)-2);
    data[14] = (int) bc;			// Low-Byte
    data[15] = (int) (bc>>>32);			// High-Byte
    transform(data);
    byte buf[] = new byte[HashLaenge];
    int off = 0;
    for (int i = 0; i < HashLaenge/4; ++i) {	// Little endian
      int d = digest[i];
      buf[off++] = (byte) d;
      buf[off++] = (byte) (d>>>8);
      buf[off++] = (byte) (d>>>16);
      buf[off++] = (byte) (d>>>24);
    }
    return buf;
  }
  protected int F(int x,int y,int z) { return (z^(x&(y^z))); }
  protected int G(int x,int y,int z) { return (y^(z&(x^y))); }
  protected int H(int x,int y,int z) { return (x^y^z); }
  protected int I(int x,int y,int z) { return (y^(x|~z)); }
  protected void transform (int M[]) {
    int a,b,c,d,t=0;
    a = digest[0]; b = digest[1];
    c = digest[2]; d = digest[3];
    for (int i=0; i<64; i++) {
      switch (i/16) {
        case 0: t = a + F(b,c,d)+M[z[i]]+y[i]; break;
        case 1: t = a + G(b,c,d)+M[z[i]]+y[i]; break;
        case 2: t = a + H(b,c,d)+M[z[i]]+y[i]; break;
        case 3: t = a + I(b,c,d)+M[z[i]]+y[i]; break;
      }     
      a = d; d = c; c = b;
      b = b + (t << s[i] | t >>> -s[i]);
    }
      digest[0] += a; digest[1] += b;
      digest[2] += c; digest[3] += d;
    }
  public static final void main(String arg[])  {
    byte[] text;
    Datei d;
    if (arg.length== 0)  d = new Datei();
    else                 d = new Datei(arg[0]);
    System.out.println("Lese Datei ... ");
    text = d.lies();         		// Text holen
    System.out.println("---- Datei: "+d.dateiname+" ("+text.length+" Bytes) ----");
    MD5 hash = new MD5();
    System.out.println(Hex.toString(hash.Haschcode(text)));
    System.exit(0);
  }
}