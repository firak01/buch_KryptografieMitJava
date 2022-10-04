import java.awt.*;
class KappaBig4 extends Frame {    	// bestimmt Kappa
  public static byte [] mtext;		// indem der Text gegen
  public static int mlaenge;		// sich selbst verschoben wird.
  public KappaBig4(String titel) {
    super(titel);
    setBackground(Color.white);
  }
  public void paint (Graphics g) {
    double kappaG=0.0, kappaM=0.0;
    int i,j,k,kappa;
    for (j=0; (j<mlaenge)&&(j<100); j++) {
      kappa=0;				// Beginn ohne Verschiebung
      k=0;
      for (i=j; i<(j+mlaenge); i++) {
        if (mtext[i%mlaenge]==mtext[k]) 
          kappa++;
        k++;
      }
      kappaM=(double)kappa/(double)mlaenge;  // Mittelwert
      g.drawRect(10+j*10,200-(int)(kappaM*1000),10,(int)(kappaM*1000)); 
    }
    System.out.println("Kappa gemittelt: "+kappaM);
  }
  public static void main( String[] arg) {
    Datei d;
    if (arg.length== 0)  d = new Datei();
    else                 d = new Datei(arg[0]);
    mtext = d.lies();         		// Text holen
    mlaenge = mtext.length;
    KappaBig4 app = new KappaBig4("Kappatest");
    app.setSize(500,200);
    app.setVisible(true);
  }
}