class CipherDecode {                      // zaehlt die Buchstaben
  private int[] h = new int[256];	// alle ASCII
  private int[] H = new int[26];	// nur die Buchstaben;
  private String Deutsch = "enirsatdhulgocmbfwkzpvjyxq".toUpperCase();
  private String English = "etaoinsrhldcumfpgwybvkxjqz".toUpperCase();
  private String Francais = "etainroshdlcfumgpwbyvkqxjz".toUpperCase();
  private int[] nHaufen = new int[26];		// H�ufigkeit
  private char[] cHaufen = new char[26];	// welche Zeichen
  static Datei d;
  int i,j,k,sl, bezug;
  String zKette;
  
  public CipherDecode () {
    char[] alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    char[] dummy = new char[26];
    byte[] mtext = d.liesAsByte() ;
    for (int i = 0; i < mtext.length; i++ ) {
      h[mtext[i]]++;
      System.out.print((char)mtext[i]);
    }
    for (i=0; i<26; i++)		// klein+GROSS zusammenfassen
      H[i] = h[i+65]+h[i+97];
    System.out.print("\n\nErmittele die H�ufigkeitsverteilung:");
    System.out.println();
    for (i=0; i<26; i++) {
      System.out.print((char)(i+65)+":"+IO.intToString(H[i],3)+"   ");
      if (((i+1)%10)==0) System.out.println();
    }
    System.out.print("\n\nSortierte Ausgabe:");
    for (i=0; i<26; i++) {
      k=0;
      for (j=1; j<26; j++) 
        if (H[j] > H[k])  
          k=j;
      nHaufen[i]=H[k];
      cHaufen[i]=(char)(k+65);
      H[k]=0;
    }    
    System.out.println();
    for (i=0; i<26; i++) {
      System.out.print((char)(cHaufen[i])+":"+IO.intToString(nHaufen[i],3)+"   ");
      if (((i+1)%10)==0) System.out.println();
    }
    System.out.print("\nAls Zeichenkette: ");
    String DataStr = new String(cHaufen);
    System.out.println(DataStr);
    System.out.print("Welche Sprache: (D)eutsch, (E)nglish, (F)rancais? ");
    char Sprache = IO.ZeichenUpCase();
    String SpracheStr="";
    System.out.print("Entsprechende H�ufigkeitsverteilung: ");
    switch (Sprache) {
      case 'D' : System.out.println(Deutsch); SpracheStr=Deutsch; break;
      case 'E' : System.out.println(English); SpracheStr=English; break;
      case 'F' : System.out.println(Francais); SpracheStr=Francais; break;
    }
    char[] SprData = SpracheStr.toCharArray();
    do {
      System.out.println("\nStarte Entschluesselung");
      for (i=0; i<mtext.length; i++) {
        if ((i%80) == 0) System.out.println();
          if ((mtext[i]>64) && (mtext[i]<91))
            System.out.print(SprData[DataStr.indexOf(mtext[i])]); 
          else
            if ((mtext[i]>96) && (mtext[i]<123)) {
              mtext[i]-=32;
              System.out.print(SprData[DataStr.indexOf(mtext[i])]); 
            } 
            else System.out.print((char)mtext[i]);
      }
      System.out.print("\n\nNeuer Versuch (J/N): ");
      if (!IO.JaNein()) {
        System.out.print("Fertig!\n\nDie Entschl�sselungstabelle\nChiffre : ");
        System.out.println(alpha);
        System.out.print("Klartext: ");
        for (int i=0; i<26; i++) {
          dummy[i] = SprData[DataStr.indexOf(i+65)];
          System.out.print(dummy[i]); 
        }
        DataStr = new String(dummy);
        System.out.print("\n\nbzw. die zugeh�rige Verschl�sselungstabelle\nKlartext: ");
        System.out.println(alpha);
        System.out.print("Chiffre : ");
        for (int i=65; i<91; i++) 
          System.out.print(alpha[DataStr.indexOf(i)]); 
        System.out.println();
        System.exit(0);					// Abbruch
      }
      System.out.println("Gib neue H�ufigkeitsverteilung ein!");
      System.out.println("(gleiche Buchstaben m�ssen nicht wiederholt werden)");
      System.out.println(SprData);
      dummy=IO.Satz().toUpperCase().toCharArray();
      for (int i=0; i<dummy.length; i++)
        if (!(dummy[i]==(' ')))
          SprData[i]=dummy[i];
      System.out.println(SprData);
    } while (true);
  }
  public static void main( String[] arg) {
    if (arg.length== 0)  d = new Datei();
    else                 d = new Datei(arg[0]);
    new CipherDecode();
  }
}
