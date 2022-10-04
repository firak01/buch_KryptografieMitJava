class Feistel {        // FeistelChiffre für beliebige Runden
  public Feistel (byte p, byte[][] fk) {
    System.out.println("Feistel-Netzwerk mit "+fk.length+" Runden");
    System.out.println("Klartext: "+IO.byteToBits(p));
    byte pLinks = (byte)((p & 0xF0) >>> 4);
    byte pRechts = (byte)(p & 0xF);
    byte pRneu;
    for (int i=0; i<fk.length; i++) {		// die n Schlüssel
      pRneu = (byte)(pLinks^fKey(pRechts,fk[i]));
      pLinks = pRechts;
      pRechts = pRneu;
    }
    p = (byte)((pLinks << 4) + pRechts);
    System.out.println("Chiffre : "+IO.byteToBits(p));
  }
  byte fKey(byte p, byte[] fk) {
    byte pNeu = 0;
    for (int i=0; i<4; i++) {		// die 4 Bits 
//      byte willi = (byte)(p << (fk[i]-1));	// nur für DEMO
//      willi = (byte)(willi & 0x8);
//      willi = (byte)(willi >> i);
//      pNeu = (byte)(pNeu+willi);
      pNeu+=(byte)(((p << (fk[i]-1)) & 0x8) >> i);
    }
    return pNeu;
  }
  public static void main( String[] arg) {
// arg1: 8 Bit Klartext
// arg2: 4 Bit-Schlüssel fk1
// arg3:       "	 fk2
// arg4: ... usw.
    byte p = (byte)Integer.parseInt(arg[0],2); // über int gehen
    byte[][] fk = new byte [arg.length-1][4];
    for (int i=0; i<arg.length-1; i++)
      fk[i] = arg[i+1].getBytes();
    for (int i=0; i<arg.length-1; i++)
      for (int j=0; j<4; j++)
        fk[i][j]-=48;			// ASCII -> Zahl
    new Feistel(p,fk);
  }
}
