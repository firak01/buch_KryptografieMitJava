import java.math.BigInteger;
import java.util.*;
import java.awt.*;

class Zeige_Statistik2 extends Frame{
  static int MaxPIN=10000, MaxI;
  int[] PINFeld = new int[MaxPIN];
  int[] PINFeldS = new int[MaxPIN];		// die sortierte Liste	
     						// nur für die Grafik!
  public static void main (String[] args) {
    String DatName;
    if (args.length > 0) DatName = args[0];
    else                 DatName = "";
    Zeige_Statistik2 zeigeStatistik = new Zeige_Statistik2("PIN-Statistik", DatName);
    zeigeStatistik.setSize(800,250);
    zeigeStatistik.setVisible(true);
  }
  public void paint (Graphics g) {
    g.setColor(Color.gray);
    int max = PINFeldS[0], top = this.getHeight(), topD = top-50;
    double RelWert;
    for (int j=0; j<400; j++) {
      RelWert = (double)PINFeldS[j]/(double)max;
      g.drawLine(j+10,top,j+10,top-(int)(RelWert*topD)); 
    }
    int step=420;
    for (int j=8630; j<9000; j++) {
      RelWert = (double)PINFeldS[j]/(double)max;
      g.drawLine(step,top,step++,top-(int)(RelWert*topD)); 
    }
    g.setColor(Color.white);
    g.drawString("0-399",200,topD);
    g.setColor(Color.black);
    g.drawString("8599-9999",600,topD);
    g.drawString("1 Mio",this.getWidth()-50,50);
  }
  
  public Zeige_Statistik2(String titel, String DatName) {
    super(titel);
    setBackground(Color.white);
    String PINStr;
    Datei p;
    if (DatName.equals("")) p = new Datei();
    else                    p = new Datei(DatName);
    PINStr = p.liesString();
    String pin, anzahl;
    int index=0, endIndex=0, i=0;
    while (index<PINStr.length()) {
      endIndex = PINStr.indexOf(":",index);
      pin = PINStr.substring(index,endIndex);
      index = endIndex+2;
      endIndex = PINStr.indexOf(";",index);
      anzahl = PINStr.substring(index,endIndex);
      if (i<50) System.out.println(i+". "+pin+": "+anzahl);
      if ((i>150)&&(i<300)) System.out.println(i+". "+pin+": "+anzahl);
      if ((i>8850)&&(i<9000)) System.out.println(i+". "+pin+": "+anzahl);
      index = endIndex+2;
      PINFeldS[i++] = Integer.parseInt(anzahl);// für die Grafik
      PINFeld[Integer.parseInt(pin)] = Integer.parseInt(anzahl);
    }
    System.out.println("\nfertig!");      
  }  
}
