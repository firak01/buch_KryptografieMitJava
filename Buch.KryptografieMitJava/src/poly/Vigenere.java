class Vigenere { 		// Vigenereverschluesselung

  public static void main( String[] arg) {
    String SchluesselWort=" ";
    Datei Original;
    int c, i, laengeSW;
    if (arg.length > 0) SchluesselWort = (arg[0]); 
    else 		System.exit(0);
    laengeSW = SchluesselWort.length();
    int[] s = IO.Unicode(SchluesselWort.getBytes());
    if (arg.length > 1) { Original = new Datei(arg[1]); }
    else { Original = new Datei(); }
    int[] p = Original.liesUnicode();
    System.out.print("Originaltext ausgeben? (J/N): ");
    if (IO.JaNein()) {
      System.out.println("---- Originaltext von: "+Datei.dateiname+" ----");
      for (i=0; i < p.length; i++) {
        IO.printChar(p[i]);	// druckbares Zeichen?
        if (((i+1)%80)==0) System.out.println();
      }
    }
    System.out.println("\n-- Verschluessele Text von: "+Datei.dateiname+" --");
    for (i = 0; i < p.length; i++) {
      c = (s[i%laengeSW]+p[i])%256;
      p[i] = c;				// nur wegen abspeichern
    }	
    System.out.print("Verschluesselten Text ausgeben? (J/N): ");
    if (IO.JaNein()) {
      System.out.println("\n\n-- Verschluesselter Text von: "+Datei.dateiname+" --");
      for (i = 0; i < p.length; i++) {
        IO.printChar(p[i]);
        if (((i+1)%80)==0) System.out.println();	// neue Zeile
      }
    }
    System.out.println(
                 "\n---- Dateilaenge: "+p.length+" Bytes ----\n ");
    Datei Kodiert = new Datei();
    Kodiert.schreib(p);
    System.exit(0);
  }
}
