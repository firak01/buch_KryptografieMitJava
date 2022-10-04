class Convert  {    	// beseitigt CRLF und Space
  static Datei d;
  
  public static void main( String[] arg) {
    if (arg.length== 0)  d = new Datei();
    else                 d = new Datei(arg[0]);
    byte[] mtext = d.lies();
    int j = 0;
    for (int i=0; i<mtext.length; i++) 
      if ((mtext[i]!=13)&&(mtext[i]!=10)&&(mtext[i]!=32)) {
        mtext[j]=mtext[i];
        j++;
      }
    byte[] neu = new byte[j+1];
    for (int i=0;i<(j+1);i++)
      neu[i]=mtext[i];
    d.schreib(neu);
    System.exit(0);
  }
}
