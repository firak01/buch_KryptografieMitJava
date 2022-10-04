import java.awt.*;

class Kappa_Test extends Frame {    	// zaehlt die Buchstaben
//  static Datei d, e;
  public byte [] mtext, vtext;
  String m,v;
  public int laenge;
  
  public Kappa_Test(String titel) {
    super(titel);
    setBackground(Color.white);
    m="diese zeile soll mit einem anderen gleich langen text auf übereinstimmende";
    v="dagegen werden die nationalen sonderzeichen beibehalten und auch die leerz";
//    m = "sometimes it is hard enough to realize all these complicated things for th";
//    v = "it is useless to expect rational behavior from the people you work with or";
    mtext = m.getBytes();
    vtext = v.getBytes();
    laenge = mtext.length;
  }
  public void paint (Graphics g) {
    double kappaG=0.0, kappaM=0.0;
    int i,j,k,kappa;
    for (j=0; j<laenge; j++) {
      kappa=0;
      k=0;
      for (i=j; i<(j+laenge); i++) {
        if (vtext[i%laenge]==mtext[k]) kappa++;
//        System.out.print((char)vtext[i%laenge]);
        k++;
      }
      System.out.print(j+": "+kappa+"  ");
      kappaG=kappaG+(double)kappa;
      kappaM=kappaG/(double)j/(double)laenge;
      System.out.println(kappaG+"  "+kappaG/(double)j+"  "+kappaM);
      g.drawLine(j,200,j,200-(int)(kappaM*1000));
    }
    System.out.println("Kappa gemittelt: "+(double)kappaG/(double)laenge);
  }
  public static void main( String[] arg) {
/*    if (arg.length== 0)  d = new Datei();
    else                 d = new Datei(arg[0]);
    if (arg.length < 2)  e = new Datei();
    else                 e = new Datei(arg[1]);    */
    Kappa_Test app = new Kappa_Test("Kappatest");
    app.setSize(100,200);
    app.setVisible(true);
  }
}

