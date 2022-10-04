public final class RIPEMD160 {
  private static final int HashLaenge = 20;	// 20*8Bit = 160
  private static final int DatenLaenge = 64;	// 64*8Bit = 512
  private static final int[]			// Konstanten und Shiftwerte
    R  = { 0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,
           7,4,13,1,10,6,15,3,12,0,9,5,2,14,11,8,
           3,10,14,4,9,15,8,1,2,7,0,6,13,11,5,12,
           1,9,11,10,0,8,12,4,13,3,7,15,14,5,6,2,
           4,0,5,9,7,12,2,10,14,1,3,8,11,6,15,13 },
    Rp = { 5,14,7,0,9,2,11,4,13,6,15,8,1,10,3,12,
           6,11,3,7,0,13,5,10,14,15,8,12,4,9,1,2,
           15,5,1,3,7,14,6,9,11,8,12,2,10,0,4,13,
           8,6,4,1,3,11,15,0,5,12,2,13,9,7,10,14,
           12,15,10,4,1,5,8,7,6,2,13,14,0,3,9,11 },
    S  = { 11,14,15,12,5,8,7,9,11,13,14,15,6,7,9,8,
           7,6,8,13,11,9,7,15,7,12,15,9,11,7,13,12,
           11,13,6,7,14,9,13,15,14,8,13,6,5,12,7,5,
           11,12,14,15,14,15,9,8,9,14,5,6,8,6,5,12,
           9,15,5,11,6,8,13,12,5,12,13,14,11,8,5,6 },
    Sp = { 8,9,9,11,13,15,15,5,7,7,8,11,14,14,12,6,
           9,13,15,7,12,8,9,11,7,7,12,7,6,15,13,11,
           9,7,15,11,8,6,6,14,12,13,5,14,13,13,7,5,
           15,5,8,11,14,14,6,14,6,9,12,9,12,5,15,8,
           8,5,12,9,12,5,14,6,8,13,6,5,15,13,11,11 };
    private static int[] digest = new int[HashLaenge/4];// die Startwerte
    private static int[] y = new int[HashLaenge/2];	// die Konstanten
//---------------------------------------------------------------------------
  public RIPEMD160() {			// Konstruktor
    init();
    Reset();
  }
//---------------------------------------------------------------------------
  protected void init() {
    y[0] = 0;					// Konstanten zuweisen
    y[1] = 0x50A28BE6;    y[2] = 0x5A827999;    y[3] = 0x5C4DD124;
    y[4] = 0x6ED9EBA1;    y[5] = 0x6D703EF3;    y[6] = 0x8F1BBCDC;  
    y[7] = 0x7A6D76E9;    y[8] = 0xA953FD4E;    y[9] = 0;
  }
//---------------------------------------------------------------------------
  protected void Reset() {			// Startwerte zuweisen
    digest[0] = 0x67452301; digest[1] = 0xEFCDAB89; digest[2] = 0x98BADCFE;
    digest[3] = 0x10325476; digest[4] = 0xC3D2E1F0;
  }
//---------------------------------------------------------------------------
  private static void byte2int(byte[] src, int srcOffset,
                               int[] dst, int dstOffset, int length) {
    while (length-- > 0) {            		// Little endian
      dst[dstOffset++] = (src[srcOffset++] & 0xFF)        |
                        ((src[srcOffset++] & 0xFF) <<  8) |
                        ((src[srcOffset++] & 0xFF) << 16) |
                        ((src[srcOffset++] & 0xFF) << 24);
    }
  }
//---------------------------------------------------------------------------
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
//---------------------------------------------------------------------------
  protected int F(int typ,int x,int y,int z) {
    int i=0;
    switch (typ) {
      case 0: i= (x^y^z); 		break;
      case 1: i= ((x&y)|(~x&z)); 	break;
      case 2: i= ((x|~y)^z); 		break;
      case 3: i= ((x&z)|(y&~z)); 	break;
      case 4: i= (x^(y|~z));		break;
    }
    return i;
  }
//---------------------------------------------------------------------------
  protected void transform(int[] X) {
    int A, B, C, D, E, Ap, Bp, Cp, Dp, Ep, T, s, i;
    A = Ap = digest[0];
    B = Bp = digest[1];
    C = Cp = digest[2];
    D = Dp = digest[3];
    E = Ep = digest[4];
    for (i=0; i<80; i++) {
      s = S[i];
      T = A + F(i/16,B,C,D)+X[R[i]]+y[(i/16)*2];
      A = E; 
      E = D;
      D = C << 10 | C >>> 22; 
      C = B; 
      B = (T << s | T >>> (32 - s)) + A;		// eigentlich "+E"!
//---- Parallele Schiene -----
      s = Sp[i];
      T = Ap + F(4-i/16,Bp,Cp,Dp)+X[Rp[i]]+y[(i/16)*2+1];
      Ap = Ep; 
      Ep = Dp;
      Dp = Cp << 10 | Cp >>> 22; 
      Cp = Bp; 
      Bp = (T << s | T >>> (32 - s)) + Ap;		// eigentlich "+Ep"!
    }
    T = digest[1] + C + Dp;
    digest[1] = digest[2] + D + Ep;
    digest[2] = digest[3] + E + Ap;
    digest[3] = digest[4] + A + Bp;
    digest[4] = digest[0] + B + Cp;
    digest[0] = T;
  }
//---------------------------------------------------------------------------
  public static void main(String[] arg) {
    byte[] text;
    Datei d;
    if (arg.length== 0)  d = new Datei();
    else                 d = new Datei(arg[0]);
    System.out.println("Lese Datei ... ");
    text = d.lies();         		// Text holen
    System.out.println("---- Datei: "+d.dateiname+" ("+text.length+" Bytes) ----");
    RIPEMD160 hash = new RIPEMD160();
    System.out.println(Hex.toString(hash.Haschcode(text)));
    System.exit(0);
  }
}
