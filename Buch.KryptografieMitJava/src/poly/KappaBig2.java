import java.awt.*;

class KappaBig2 extends Frame {    	// bestimmt Kappa
  public static byte [] mtext, vtext;
  public static int vlaenge, mlaenge;

  public KappaBig2(String titel) {
    super(titel);
    setBackground(Color.white);
  }
  public void paint (Graphics g) {
    double kappaG=0.0, kappaM=0.0;
    int i,j,k,kappa;
    for (j=0; (j<vlaenge)&&(j<100); j++) {
      kappa=0;
      k=0;
      for (i=j; i<(j+mlaenge); i++) {
        if (vtext[i%vlaenge]==mtext[k]) kappa++;
        k++;
      }
      kappaM=(double)kappa/(double)mlaenge;
      g.drawRect(10+j*10,200-(int)(kappaM*2000),10,(int)(kappaM*2000)); 
    }
    System.out.println("Kappa gemittelt: "+kappaM);
  }
  
  public static void main( String[] arg) {
    Datei d;
    if (arg.length== 0)  d = new Datei();
    else                 d = new Datei(arg[0]);
    vtext = d.liesAsByte();         		// Text holen
    vlaenge = vtext.length;
    mtext = new byte[vlaenge/2];	// Text aufteilen
    mlaenge = mtext.length;
    for (int i=0; i<mlaenge; i++) 
      mtext[i] = vtext[i+mlaenge];
    KappaBig2 app = new KappaBig2("Kappatest");
    app.setSize(500,200);
    app.setVisible(true);
  }
}
