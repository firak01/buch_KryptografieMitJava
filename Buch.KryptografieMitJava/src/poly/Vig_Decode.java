class Vig_Decode { 	// Vigenereentschluesselung mit bekanntem Schluesselwort!
  public static void main( String[] arg) {
    String SchluesselWort=" ";
    Datei Chiffre;
    int p, i, laengeSW;
    if (arg.length > 0) SchluesselWort = (arg[0]);
    else                System.exit(0);
    laengeSW = SchluesselWort.length();
    int[] s = IO.Unicode(SchluesselWort.getBytes());
    if (arg.length > 1) Chiffre = new Datei(arg[1]); 
    else                Chiffre = new Datei(); 
    System.out.println("Datei einlesen ...");
    int[] c = Chiffre.liesUnicode();	// Datei einlesen
    System.out.print("\nChiffrierte Datei ausgeben? (J/N): ");
    if (IO.JaNein()) {
      System.out.println("---- Chiffretext von: "+Datei.dateiname+" ----");
      for (i=0; i < c.length; i++) {
        IO.printChar(c[i]);
        if (((i+1)%80)==0) System.out.println(); 	// neue Zeile
      }
    }
    System.out.println("\nBeginne Entschluesselung ... ");
    for (i=0; i<c.length; i++) {
      p = c[i]-s[i%laengeSW];			// c-s
      if (p < 0) p+=256;
      c[i] = p; 				// wegen Abspeichern von P
    }
    System.out.print("\nOriginal-Datei ausgeben? (J/N): ");
    if (IO.JaNein()) {
      System.out.println("\n\n-- Originaltext von: "+Datei.dateiname+" --");
      for (i=0; i<c.length; i++) {
        IO.printChar(c[i]);
        if (((i+1)%80)==0) System.out.println();	// neue Zeile
      }
      System.out.println("\n---- Dateilaenge: "+c.length+" Bytes ----\n ");
    }
    Datei Original = new Datei();
    Original.schreib(c);
    System.exit(0);
  }
}