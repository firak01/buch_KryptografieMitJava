class Vigenere_Decode2 {  	
  public static int [] c;		
  public static int cLaenge;
  double [][] sSpitze;				// absolute Haeufigkeit
  byte [][] cSpitze;				// zugehoerige Zeichen
  byte bezug_e=0;				// entspricht "e"
  byte bezug_a=0;			
// nur interessant, wenn verschlüsselter Text die Zaehlung der Zeichen
// bei a=0 bzw. A=0 begonnen hat, dann muss bezug_a=97 sein, bzw.
// muss korrigiert werden, falls im Text mehr Gross- als
// Klein-Buchstaben auftreten!  ->  bezug_E=69, bezug_A=65
  double pEsoll=0.15;			// Wahrscheinlichkeit für "e"
  
  public Vigenere_Decode2() {			
    System.out.println("Bitte Textart angeben:");
    System.out.println("alle 256 Zeichen -----------------------> 0");
    System.out.println("alle 256 Zeichen, aber GROSSbuchstaben -> 1");
    System.out.println("nur GROSSbuchstaben --------------------> 2");
    System.out.println("nur Kleinbuchstaben --------------------> 3");
    System.out.println("nur Buchstaben, aber GROSS/klein -------> 4");
    System.out.print("Eingabe: ");
    int TextArt = IO.ganzeZahl();
    switch (TextArt) {
      case 0 : break;
      case 1 :
        bezug_e = 69;
        break;
      case 2: 
        bezug_e = 69;
        bezug_a = 65;
        break;
      case 4 :
      case 3 :
//        bezug_e = 101;
        bezug_a = 97; 
        break;
      default:
        System.out.println("\nFehlerhafte Eingabe!");
        System.exit(0);
        break;
    }
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
// jetzt alle moeglichen Zeichen fuer das Schluesselwort permutierten
//
    byte[] Anordnung = bildeGrundanordnung(SL);
    for (int i=0; i<SL; i++) System.out.print(Anordnung[i]+" ");
    System.out.println();
    boolean SchluesselWortOK=false;
    boolean SLWort=false;
    do {
      if (!SLWort)
        for (int i=0; i<SL; i++) 
//          s[i] = (byte)(cSpitze[i][Anordnung[i]]);
          s[i] = ASCII(cSpitze[i][Anordnung[i]]-bezug_e+bezug_a);
      for (int i=0; i<SL; i++) System.out.print(s[i]+" ");
      System.out.print("  Schluesselwort: ");
      for (int i=0; i<SL; i++) IO.printChar(s[i]);
      System.out.println();
      for (int i = 0; i<80; i++) {
//System.out.println(c[i]+"  "+s[i%SL]+"  "+(c[i]-s[i%SL])); //
        p = c[i]-s[i%SL];
        if ((TextArt==4) && (c[i]<97)) p-=26;	// GROSSbuchstabe???
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
  byte ASCII(int ch) {
    if (ch < 0) return (byte)(ch+=256);
    else 	return (byte)(ch%256);
  }
  void erstelleStatistik(int SL) {
    int i,j,k,l;
    int []h = new int[256];			// Zahlenstatistik
    for (j=0; j<SL; j++) {
      System.out.print(".");
      for (i=0; i<h.length; i++) h[i]=0;	// alles auf Null
      for (i=j; i<c.length; i+=SL ) h[c[i]]++; 	// erstmal alles zaehlen
      for (i=0; i<10; i++) {			// 10 haeufigsten 10 Buchstaben
        k=0;
        for (l=1; l<256; l++) 
          if (h[l] > h[k]) k=l;
        sSpitze[j][i]=(double)(h[k]*SL)/(double)(cLaenge);  // relativ
        cSpitze[j][i]=(byte)k;
        h[k]=0;
      }
    }
  }
  
  int bestimmeSL() {
    double kDeutsch=0.05, kappaM=0.0; 
    int i,j,k,kappa,SL=1,iD=0,MaxDurchlauf=10;
    boolean ende=false;
    j=0;
    while ((j<cLaenge) && (ende==false)) {
      kappa=0;	
      k=0;
      for (i=j; i<(j+cLaenge); i++) {
        if (c[i%cLaenge]==c[k]) kappa++;
        k++;
      }
      kappaM=(double)kappa/(double)cLaenge;  // Mittelwert
      if (kappaM>kDeutsch) {
        SL=j-SL;
        System.out.println("j="+j+"\tSL="+SL+"\tKappa="+kappaM);
        iD++;
        if((iD%MaxDurchlauf)==0) {
          System.out.print("Weitermachen (J/N)? >");
          if (!IO.JaNein()) return SL;
        }
        SL=j;
      }
      j++;
    }
    return 0;   			// falls ende erreicht
  }
  public static void main( String[] arg) {
    Datei d;
    if (arg.length== 0)  d = new Datei();
    else                 d = new Datei(arg[0]);
    System.out.println("Lese Datei ... ");
    c = d.liesUnicode();         		// Text holen
    System.out.println("---- Verschluesselte Datei: "+d.dateiname+
         	" ("+c.length+" Bytes) ----");
    cLaenge = c.length;
    Vigenere_Decode2 app = new Vigenere_Decode2();
  }
}
