class CaesarDecode26 {                      // zaehlt die Buchstaben
  private int[]  h = new int[26];
  private int[] spitze = new int[10];
  private int[] cspitze = new int[10];
  static Datei d;
  int i,j,k,sl, bezug;
  String zKette;
  
  public CaesarDecode26 () {
    byte[] mtext = d.liesAsByte() ;
    for (int i = 0; i < mtext.length; i++ ) {
      h[mtext[i]-97]++;
      System.out.print((char)mtext[i]);
    }
    System.out.println();
    for (i=0; i < 26; i++) {
      System.out.print((char)(i+97)+":"+IO.intToString(h[i],3)+"   ");
      if (((i+1)%10)==0) System.out.println();
    }
    System.out.print("\n\nErmittele die haeufigsten 10 Buchstaben:");
    for (i=0; i<10; i++) {
      System.out.print(".");
      k=0;
      for (j=1; j<26; j++) 
        if (h[j] > h[k])  
          k=j;
      spitze[i]=h[k];
      cspitze[i]=k;
      h[k]=0;
    }    
    System.out.print("\nBezugsbuchstaben f�r Schluessell�nge: ");
    bezug=(byte)IO.Zeichen()-97;
    System.out.println("\nSchluessell�nge in bezug auf '"+(char)(bezug+97)+
                       "'(Zeichen-Ordinalzahl-Haeufigkeit)");
    for (i=0; i < 10; i++) {
      System.out.print((char)(cspitze[i]+97)+"("
                       +cspitze[i]+")"+": "+spitze[i]+" ");
      sl = cspitze[i]-bezug;
      spitze[i]=sl;
      System.out.println("---> Schluessell�nge= "+sl);
    }
    for (k=0; k<10; k++) {
      System.out.print("\nStarte Entschluesselung mit sl="+spitze[k]+":");
      for (i=0; i<mtext.length; i++) {
      if ((i%80) == 0) System.out.println();
        System.out.print((char)(mtext[i]-spitze[k]-97));
      }
      System.out.print("\n\nNeuer Versuch (J/N): ");
      zKette=IO.Satz();
      if (zKette.equals("n")||zKette.equals("N")) System.exit(0);
    }
  }
  public static void main( String[] arg) {
    if (arg.length== 0)  d = new Datei();
    else                 d = new Datei(arg[0]);
    new CaesarDecode26();
  }
}
