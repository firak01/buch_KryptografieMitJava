// Herbert Voss 001101

class MD2 {
/* Permutation of 0..255 constructed from the digits of pi. It gives a
   "random" nonlinear byte substitution operation.                        */
  static final int[] S = {
     41,  46,  67, 201, 162, 216, 124,   1,  61,  54,  84, 161, 236, 240,   6,  19,  
     98, 167,   5, 243, 192, 199, 115, 140, 152, 147,  43, 217, 188,  76, 130, 202,
     30, 155,  87,  60, 253, 212, 224,  22, 103,  66, 111,  24, 138,  23, 229,  18, 
    190,  78, 196, 214, 218, 158, 222,  73, 160, 251, 245, 142, 187,  47, 238, 122, 
    169, 104, 121, 145,  21, 178,   7,  63, 148, 194,  16, 137,  11,  34,  95,  33, 
    128, 127,  93, 154,  90, 144,  50,  39,  53,  62, 204, 231, 191, 247, 151,   3, 
    255,  25,  48, 179,  72, 165, 181, 209, 215,  94, 146,  42, 172,  86, 170, 198,
     79, 184,  56, 210,  150, 164, 125, 182, 118, 252, 107, 226, 156, 116,  4, 241,
     69, 157, 112,  89, 100, 113, 135,  32, 134,  91, 207, 101, 230,  45, 168,   2,  
     27,  96,  37, 173, 174, 176, 185, 246,  28,  70,  97, 105,  52,  64, 126,  15,
     85,  71, 163,  35, 221,  81, 175,  58, 195,  92, 249, 206, 186, 197, 234,  38,
     44,  83,  13, 110, 133,  40, 132,   9, 211, 223, 205, 244,  65, 129,  77,  82, 
    106, 220,  55, 200, 108, 193, 171, 250,  36, 225, 123,   8,  12, 189, 177,  74,
    120, 136, 149, 139, 227,  99, 232, 109, 233, 203, 213, 254,  59,   0,  29,  57,
    242, 239, 183,  14, 102,  88, 208, 228, 166, 119, 114, 248, 235, 117,  75,  10,
     49,  68,  80, 180, 143, 237,  31,  26, 219, 153, 141,  51, 159,  17, 131,  20 };

  static final String[] Padding = {
  "",
  "\001",		// alles OKTAL!!!
  "\002\002",
  "\003\003\003",
  "\004\004\004\004",
  "\005\005\005\005\005",
  "\006\006\006\006\006\006",
  "\007\007\007\007\007\007\007",
  "\010\010\010\010\010\010\010\010",
  "\011\011\011\011\011\011\011\011\011",
  "\012\012\012\012\012\012\012\012\012\012",
  "\013\013\013\013\013\013\013\013\013\013\013",
  "\014\014\014\014\014\014\014\014\014\014\014\014",
  "\015\015\015\015\015\015\015\015\015\015\015\015\015",
  "\016\016\016\016\016\016\016\016\016\016\016\016\016\016",
  "\017\017\017\017\017\017\017\017\017\017\017\017\017\017\017",
  "",							// NICHTS anhängen
  "\020\020\020\020\020\020\020\020\020\020\020\020\020\020\020\020" }; 
// der letzte Wert nur für leeren Eingabestring in main!

//--------------------------------------
  public MD2 (int[] M) {
    int[] HashWert = new int[16];
printByteFeldAlsChar(M);
printByteFeld(M);
    int[] T = new int[M.length+16];
System.out.println("Length(T)="+T.length);
    System.arraycopy(M, 0, T, 0, M.length);		// M[0]->T[0]
    System.arraycopy(CheckSum(M), 0, T, M.length, 16);	// C[0]->T[n*16]
    int AnzahlBloecke = T.length/16;			// Anzahl 16Byte Blöcke - 1
    System.out.println(AnzahlBloecke+" Blöcke zu je 16Byte!");    
    int[] X = new int[48];
    int t;
    for (int block=0; block<AnzahlBloecke; block++) {
      for (int i=16; i<32; i++) {				// X[] initialisieren
        X[i] = T[(block-1)*16+i];
        X[i+16] = X[i-16]^X[i];				// entspricht =X[i-16]^X[i]
      }
      t = 0;
      for (int i=0; i<18; i++) {			// Encrypt block (18 rounds)
        for (int j=0; j<48; j++) {
          t = X[j]^S[t];
          X[j] = t;
        }  
        t = (t + i) & 0xff;				// entspricht mod 256
      }
    }
    System.out.println("Hashwert:");
    for (int i=0; i<16; i++) {
      HashWert[i]=X[i];
      System.out.print((HashWert[i]&0xFF)+" ");
    }
    System.out.println();
    for (int i=0; i<16; i++)
      System.out.print(Hex.byteToString((byte)HashWert[i]));
    System.out.println();
  }
//--------------------------------------------------------
  int[] CheckSum (int[] M) {
    int[] C = new int[16];
    int c, L=0;
    for (int i=0; i<(M.length/16); i++) {        	// jeden 16-er block
      for (int j=0; j<16; j++) {  
        c = M[i*16+j];
        L = S[c^L];
        C[j] = L;
      }
    }
System.out.println("Prüfsumme:");
printByteFeld(C);
    return C;
  }
//--------------------------------------------------------
  void printByteFeldAlsChar (int[] B) {
    for (int i=0; i<B.length; i++)
      System.out.print((char)B[i]);
    System.out.println();
  }
//--------------------------------------------------------
  void printByteFeld (int[] B) {
    for (int i=0; i<B.length; i++)
      System.out.print((B[i]&0xFF)+" ");
    System.out.println();
  }
//--------------------------------------------------------

  public static void main (String[] M) {
    String MStrich="";					// Normlänge
    if (M.length>0)
      MStrich=M[0]+Padding[16-M[0].length()%16];  	// auf Vielfaches von 16 bringen
    else						// kein Parameter
      MStrich=Padding[17];				// 16*0x0F
    int[] MStr = new int[MStrich.length()];
    for (int i=0; i<MStr.length; i++)
      MStr[i] = (int)MStrich.charAt(i);
    MD2 Test = new MD2(MStr);
  }
}
