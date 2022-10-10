class Skytale { // Entschluesselung einer Skytale
  public static void main( String[] arg) {
    Datei Original;
    String zKette;
    int i, j, zeilen=2, MaxZeilen;
    if (arg.length > 0) { Original = new Datei(arg[1]); }
    else { Original = new Datei(); }
    byte[] geheim = Original.liesAsByte();
    MaxZeilen = geheim.length/2;
    System.out.println("\n-- Verschluesselter Text von: "+Datei.dateiname+" --");
    for (i=0; i < geheim.length; i++) {
      System.out.print((char)geheim[i]);
      if (((i+1)%80)==0) System.out.println();
    }
    System.out.println("\n\n---- Originaltext von: "+Datei.dateiname+" ----");
    boolean ok = false;
    while (!ok && (zeilen<=MaxZeilen)) {
      for (j=0; j<zeilen; j++) {
        for (i=j; i<geheim.length; i+=zeilen) {
          System.out.print((char)geheim[i]);
        }
        System.out.println();
      }
      System.out.print("\nNeuer Versuch (J/N): ");
      zKette=Eingabe.Satz();
      if (zKette.equals("n")||zKette.equals("N")) System.exit(0);
      zeilen++;    
    }	
    System.out.println("\n---- Dateilaenge: "+geheim.length+" Bytes ----\n ");
    Datei Kodiert = new Datei();
    Kodiert.schreib(geheim);
  }
}
