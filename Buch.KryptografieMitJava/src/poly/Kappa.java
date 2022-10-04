import java.awt.*;

class Kappa {    			// zaehlt die Buchstaben
  public static byte [] mtext;
  public static int mlaenge;

  public Kappa() {
    int[] Buchstaben = new int[26];
    System.out.print("Schlüsselwortlänge: ");
    int SWL = IO.ganzeZahl();
    for (int i=0; i<SWL; i++) { 		// für jeden Buchstaben ds SW
      for (int j=0; (j<26); j++) 		// das Alphabet
        for (int k=i; k<mlaenge;k+=SWL) 	// jeden SWL-ten Buchstaben
          if (mtext[k]==(j+65))
            Buchstaben[j]++;
    
      System.out.println("Verteilung für "+(i+1)+". Stelle");
      for (int j=65; j<91; j++)
        System.out.print(" "+(char)j+" ");
      System.out.println();
      for (int j=0; j<26; j++)
        if (Buchstaben[j]<10) System.out.print(" "+Buchstaben[j]+" ");
        else                  System.out.print(Buchstaben[j]+" ");
      System.out.println();
      Buchstaben = new int[26];
    }
  }
  
  public static void main( String[] arg) {
    Datei d;
    if (arg.length== 0)  d = new Datei();
    else                 d = new Datei(arg[0]);
    mtext = d.lies();         		// Text holen
    mlaenge = mtext.length;
    Kappa app = new Kappa();
  }
}
