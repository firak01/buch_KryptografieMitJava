class Caesar { // Caesarverschluesselung ohne Rotation

  public static void main( String[] arg) {
    Datei Original;
    int zeichen;
    int schluessel = 3;		// Vorgabe
    if (arg.length > 0) { schluessel = Integer.parseInt(arg[0]); }
    if (arg.length > 1) { Original = new Datei(arg[1]); }
    else { Original = new Datei(); }
    byte[] geheim = Original.liesAsByte() ;
    System.out.println(
      "---- Verschluesseln von: "+Datei.dateiname+" ----");
    for (int i = 0; i < geheim.length; i++) {
      if ((geheim[i] > 31) && (geheim[i] < 127)) {
        zeichen = geheim[i]-32;     // auf space beziehen
        geheim[i] = (byte)(((zeichen+schluessel)%95)+32);
      }    
      System.out.print((char)geheim[i]);
    }	
    System.out.println(
      "\n---- Dateilaenge: "+geheim.length+" Bytes ----\n ");
    Datei Kodiert = new Datei();
    Kodiert.schreib(geheim);
  }
}
