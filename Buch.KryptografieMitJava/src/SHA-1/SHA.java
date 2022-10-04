public final class SHA {	
    private static final int HashLaenge = 20;	// 20*8Bit = 160
    private static final int DatenLaenge = 64;	// 64*8Bit = 512
    private int[] digest;			// die ints des Hashwertes
    private int[] y;				// additive Konstanten
  public SHA() {				// Konstruktor
    init();
    reset();
  }
  private void init() {
    digest = new int[HashLaenge/4];		// [0..3]  128 Bit
    y = new int[4];
  }
  private void reset() {
    digest[0] = 0x67452301; digest[1] = 0xEFCDAB89;  // die fünf Startwerte
    digest[2] = 0x98BADCFE; digest[3] = 0x10325476;
    digest[4] = 0xC3D2E1F0;
    y[0] = 0x5A827999; y[1] = 0x6ED9EBA1;	// die vier Konstanten
    y[2] = 0x8F1BBCDC; y[3] = 0xCA62C1D6;
  }
  private void byte2int(byte[] src, int srcOffset,
                               int[] dst, int dstOffset, int length) {
    while (length-- > 0) {            		// Little endian
      dst[dstOffset++] = (src[srcOffset++] & 0xFF)        |
                        ((src[srcOffset++] & 0xFF) <<  8) |
                        ((src[srcOffset++] & 0xFF) << 16) |
                        ((src[srcOffset++] & 0xFF) << 24);
    }
  }     
  private byte[] bildeHashcode(byte[] in) {
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
    for (int i = 0; i < 5; i++) {		// Little endian
      int d = digest[i];
      buf[off++] = (byte) d;
      buf[off++] = (byte) (d>>>8);
      buf[off++] = (byte) (d>>>16);
      buf[off++] = (byte) (d>>>24);
    }
    return buf;
  }  
  protected int F(int x,int y,int z) { return ((x&y)|(~x&z)); }
  protected int H(int x,int y,int z) { return (x^y^z); }
  protected int G(int x,int y,int z) { return ((x&y)|(x&z)|(y&z)); }
  protected void transform (int M[]) {
    int[] X = new int[80];
    int n=M.length;
    System.arraycopy(M,0,X,0,n);		// Kopie erstellen
    for (int i=16; i<80; i++) {
      n = X[i-16] ^ X[i-14] ^ X[i-8] ^ X[i-3];
      X[i] = (n << 1 | n >>> -1);
    }
    int a,b,c,d,e,t=0;
    a = digest[0]; b = digest[1];
    c = digest[2]; d = digest[3];
    e = digest[4];
    for (int i=0; i<80; i++) {
      switch (i/HashLaenge) {
        case 0: t = (a<<5 | a >>>-5) + F(b,c,d)+e+X[i]+y[0]; break;
        case 1: t = (a<<5 | a >>>-5) + H(b,c,d)+e+X[i]+y[1]; break;
        case 2: t = (a<<5 | a >>>-5) + G(b,c,d)+e+X[i]+y[2]; break;
        case 3: t = (a<<5 | a >>>-5) + H(b,c,d)+e+X[i]+y[3]; break;
      }     
      e = d; d = c;
      c = (b<<30 | b >>>-30);
      b = a; a = t;
    }
      digest[0] += a; digest[1] += b;
      digest[2] += c; digest[3] += d;
      digest[4] += e;
    }
//---------------------------------------------------------------------------
  public static final void main(String arg[])  {
    byte[] text;
    if (arg.length > 0)
    text=new byte[arg[0].length()];
    else text = new byte[0];
    Datei d;
    if (arg.length== 0)  d = new Datei();
    else                 d = new Datei(arg[0]);
    System.out.println("Lese Datei ... ");
    text = d.lies();         		// Text holen
    System.out.println("---- Datei: "+d.dateiname+" ("+text.length+" Bytes) ----");
    SHA hash = new SHA();
    System.out.println(Hex.toString(hash.bildeHashcode(text)));
    System.exit(0);
  }
}