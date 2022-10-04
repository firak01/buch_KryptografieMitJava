import java.awt.*;
class Kasiski {    			// bestimmt die Wieder-
  public static byte [] mtext;		// holungsperioden
  public static int mlaenge;
  
  public static void main( String[] arg) {
    int start, ende=1, TeilStringLaenge=3;
    boolean Ausgabe;
    Datei d;
    if (arg.length== 0)  d = new Datei();
    else                 d = new Datei(arg[0]);
    mtext = d.lies();         		// Text holen
    String mStr = new String(mtext);
    mlaenge = mtext.length;
    System.out.print("Länge der zu untersuchenden Teilstringlänge (>2): ");
    TeilStringLaenge = IO.ganzeZahl();
    if ((TeilStringLaenge<3)||(TeilStringLaenge>mtext.length))
      TeilStringLaenge=3;
    for (int i=0; i<mlaenge-TeilStringLaenge+1; i++) {
      String sub = mStr.substring(i,i+TeilStringLaenge);
      start=mStr.indexOf(sub);
      Ausgabe = false;
      while (start > 0) {
        ende = mStr.indexOf(sub,start+1);
        if (ende > 0) {
          if (!Ausgabe) {
            System.out.print(start+" ");
            Ausgabe = true;
          }
          System.out.print((ende-start)+" ");
        }
        start=ende;
      }
      if (Ausgabe) System.out.println("\n");
    }
    System.exit(0);  
  }
}