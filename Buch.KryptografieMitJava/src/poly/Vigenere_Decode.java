class Vigenere_Decode2 {  	
  public static int [] c;		
  public static int cLaenge;
  double [][] sSpitze;				// absolute Haeufigkeit
  byte [][] cSpitze;				// zugehoerige Zeichen
  byte bezug_e=101;				// entspricht "e"
  byte bezug_a=0;			
// nur interessant, wenn verschlüsselter Text die Zaehlung der Zeichen
// bei a=0 bzw. A=0 begonnen hat, dann muss bezug_a=97 sein, bzw.
// muss korrigiert werden, falls im Text mehr Gross- als
// Klein-Buchstaben auftreten!  ->  bezug_E=69, bezug_A=65
  double pEsoll=0.15;				// Wahrscheinlichkeit für "e"
  
  public Vigenere_Decode2() {			
[ ... Textart festlegen ... ]
    int SL=bestimmeSL();			// Schluessellaenge
    System.out.println("Schluesselwortlaenge= "+SL);
    sSpitze = new double [SL][10];		// die Haeufigkeiten
    cSpitze = new byte[SL][10];			// die zugehoerigen Zeichen
    erstelleStatistik(SL);
    System.out.println("\n----- Die Statistik ----");
    for (int j=0; j<SL; j++) {			// gleiche Abstaende
      System.out.println(j+":");
      for (int i=0; i < 10; i++) {
//        IO.printChar(cSpitze[j][i]);		// Unicode-Ausgabe
        System.out.print("("+cSpitze[j][i]+")"+": "+IO.DM(100.0*sSpitze[j][i])+"% \t");
        if (((i+1)%5)==0) System.out.println();
      }
    }
//
// Entschluesselungsversuche bis ein sinnvoller Text erscheint
//
    System.out.println("\nStarte Entschluesselung ...");
    bildeKlartext(TextArt,SL);
    System.exit(0);
  }
  byte[] bildeGrundanordnung(int Schluessellaenge) {
    System.out.println("Bestimme wahrscheinlichste Anordnung ...");
    byte [] Anordnung = new byte[Schluessellaenge];
    double pEist;
    double diff;
    for (int i=0; i<Schluessellaenge; i++) {
      diff=pEsoll;				// Vorgabe
      for (int j=0; j<10; j++) {
        if ((Math.abs(sSpitze[i][j]-pEsoll)) < diff) {
          diff = sSpitze[i][j]-pEsoll;
          Anordnung[i]=(byte)j;
        }
      }  
    }
    return Anordnung;
  }
  void bildeKlartext(int TextArt, int SL) {
    byte[] s = new byte[SL];
    int p, k, l;
    String datStr;
//
// jetzt muessen alle moeglichen Zeichen fuer das Schluesselwort permutiert werden
//
    byte[] Anordnung = bildeGrundanordnung(SL);
    for (int i=0; i<SL; i++) System.out.print(Anordnung[i]+" ");
    System.out.println();
    boolean SchluesselWortOK=false;
    boolean SLWort=false;
    do {
      if (!SLWort)
        for (int i=0; i<SL; i++) 
          s[i] = (byte)(cSpitze[i][Anordnung[i]]-bezug_e+bezug_a);
      for (int i=0; i<SL; i++) System.out.print(s[i]+" ");
      System.out.print("  Schluesselwort: ");
      for (int i=0; i<SL; i++) IO.printChar(s[i]);
      System.out.println();
      for (int i = 0; i<80; i++) {
        p = c[i]-s[i%SL];
        if ((TextArt==4)&& (c[i]<97)) p-=26;	// GROSSbuchstabe???
        if (p<0) 
          if (TextArt>1) p+=26;
          else           p+=256;
        p+=bezug_a;
        IO.printChar(p);
      }
      System.out.print("\nSchluesselwort veraendern? (J/N): ");
      if (IO.JaNein()) {
        System.out.print("Aktuelle Anordnung: ");
        for (int i=0; i<SL; i++) System.out.print(Anordnung[i]+" ");
        System.out.println();
        System.out.print("Neue Anordnung:     ");
        datStr = IO.Satz();
        if (datStr.indexOf(" ")<0) {	// kein Space??
          SLWort=true;
          s = datStr.getBytes();
        }
        else {
          SLWort=false;
          datStr = datStr+" ";     	// Space wegen Umwandlung
          k=0; l=0;
          for (int i=0; i<SL; i++) {
            k = datStr.indexOf(" ",l);
            Anordnung[i] = Byte.parseByte(datStr.substring(l,k));
            l = k+1;
          }
        }
      }
      else SchluesselWortOK=true;
    } while (!SchluesselWortOK);
    System.out.print("Jetzt komplett entschluesseln? (J/N): ");
    if (!IO.JaNein()) System.exit(0);
    System.out.println("\n");    
    for (int i = 0; i<c.length; i++) {
      p = c[i]-s[i%SL];
      if ((TextArt==4)&& (c[i]<97)) p-=26;	// GROSSbuchstabe???
      if (p<0) 
        if (TextArt>1) p+=26;
        else           p+=256;
      p+=bezug_a;
      IO.printChar(p);
      c[i] = p; 			// wegen evtuellem Speichern
      if (((i+1)%80)==0) System.out.println();	// neue Zeile
    }
    System.out.println("\n---- Dateilaenge: "+c.length+" Bytes ----\n ");
  }
  void erstelleStatistik(int SL) {
[ ... wie angegeben ... ]
  }
  int bestimmeSL() {
[ ... wie angegeben ... ]
  }
  public static void main( String[] arg) {
[ ... wie angegeben ... ]
  }
}