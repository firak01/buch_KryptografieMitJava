import java.awt.*;

class KappaBig extends Frame {    	// zaehlt die Buchstaben
  public static byte [] mtext, vtext;
  public static String m;
  public static int vlaenge, mlaenge;

  public KappaBig(String titel) {
    super(titel);
    setBackground(Color.white);
  }
  public void paint (Graphics g) {
    double kappaG=0.0, kappaM=0.0;
    int i,j,k,kappa;
    for (j=0; (j<vlaenge)&&(j<200); j++) {
      kappa=0;
      k=0;
      for (i=j; i<(j+mlaenge); i++) {
        if (vtext[i%vlaenge]==mtext[k]) kappa++;
        k++;
      }
//      System.out.print(j+": "+kappa+"  "+(double)kappa/(double)mlaenge);
      kappaG=kappaG+(double)kappa;
      kappaM=kappaG/(double)j/(double)mlaenge;
//      System.out.println(kappaG+"  "+kappaG/(double)j+"  "+kappaM);
      if (j<200) 
        g.drawLine(j+10,200,j+10,200-(int)(kappaM*1000)); 
    }
    System.out.println("Kappa gemittelt: "+(double)kappaM);
  }
  
  public static void main( String[] arg) {
    Datei d;
    if (arg.length== 0)  d = new Datei();
    else                 d = new Datei(arg[0]);
//    m="diese zeile soll mit einem anderen gleich langen text auf übereinstimmende";
//    m = "sometimes it is hard enough to realize all these complicated things for th";
    vtext = d.lies();         		// Text holen
    vlaenge = vtext.length;
    mtext = new byte[vlaenge/2];	// Text aufteilen
    mlaenge = mtext.length;
    for (int i=0; i<mlaenge; i++) 
      mtext[i] = vtext[i+mlaenge];
    KappaBig app = new KappaBig("Kappatest");
    app.setSize(250,200);
    app.setVisible(true);
  }
}
