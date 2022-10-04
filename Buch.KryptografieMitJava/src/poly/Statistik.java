import java.awt.*;

class Statistik extends Frame {    	// zaehlt die Buchstaben
  private int[]  h = new int[256];
  static Datei d;
  private byte [] mtext;
  
  public Statistik(String titel) {
    super(titel);
    setBackground(Color.white);
    System.out.println("Lese Datei");
    mtext = d.lies();
  }
  public void paint (Graphics g) {
    for (int j=1; j<17; j++) {
      for (int i=0; i<256; i++) h[i]=0;
      for (int i=0; i<mtext.length; i+=j) 
        h[Math.abs((int)mtext[i])]++;
      int Max=0; byte zeichen=0;
      for (int i=0; i<256; i++)
        if (Max < h[i]) {
          Max = h[i];
          zeichen = mtext[i];
        }
      System.out.println(
      "Maximale Haeufigkeit= "+Max+" mal Nr. "+zeichen);
      for (int i=0; i<127; i++) 
        if (j<9)
          g.drawLine((i+10)+(j-1)*125,400,(i+10)+(j-1)*125,400-(h[i]*400)/Max);
        else  
          g.drawLine((i+10)+(j-9)*125,800,(i+10)+(j-9)*125,800-(h[i]*400)/Max);
    }
  }
  public static void main( String[] arg) {
    if (arg.length== 0)  d = new Datei();
    else                 d = new Datei(arg[0]);
    Statistik app = new Statistik("Haeufigkeitsverteilung");
    app.setSize(1100,800);
    app.setVisible(true);
  }
}
