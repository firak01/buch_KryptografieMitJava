class OneTimePad { 		// Vigenereverschluesselung

  public static void main( String[] arg) {
    String SchluesselWort=" ";
    Datei Original, Schluessel;
    int c, i, laengeSW;
//
// Zwei Argumente werden erwartet: KlartextDatei und SchluesselDatei
//
    if (arg.length < 2) System.exit(0);
    Original = new Datei(arg[0]);
    System.out.print("Lese Klartextdatei ... ");
    int[] p = Original.liesUnicode();
    System.out.println("fertig!");
    Schluessel = new Datei(arg[1]);
    System.out.print("Lese Schluesseldatei ... ");
    int[] k = Schluessel.liesUnicode();
    System.out.println("fertig!");
    laengeSW = k.length;
    if (laengeSW < p.length) {
      System.out.println("Schluesselwort MUSS laenger als der Text sein!");
      System.exit(0);
    }
    System.out.print("Originaltext ausgeben? (J/N): ");
    if (IO.JaNein()) {
      System.out.println("---- Originaltext von: "+Original.dateiname+" ----");
      for (i=0; i < p.length; i++) 
        System.out.print((char)p[i]);	// druckbares Zeichen?
    }
    System.out.print("Schluesselltext ausgeben? (J/N): ");
    if (IO.JaNein()) {
      System.out.println("---- Schluesseltext von: "+Schluessel.dateiname+" ----");
      for (i=0; i < p.length; i++) 
        System.out.print((char)k[i]);	// druckbares Zeichen?
    }
    System.out.println("\n-- Verschluessele Text von: "+Original.dateiname+" --");
    for (i = 0; i < p.length; i++) 
      p[i] = (k[i]+p[i])%256;
    System.out.print("Verschluesselten Text ausgeben? (J/N): ");
    if (IO.JaNein()) {
      System.out.println("\n\n-- Verschluesselter Text von: "+Original.dateiname+" --");
      for (i = 0; i < p.length; i++) {
        IO.printChar(p[i]);
        if (((i+1)%80)==0) System.out.println();	// neue Zeile
      }
    }
    System.out.println("\n---- Dateilaenge: "+p.length+" Bytes ----\n ");
    Datei Kodiert = new Datei();
    Kodiert.schreib(p);
    System.exit(0);
  }
}