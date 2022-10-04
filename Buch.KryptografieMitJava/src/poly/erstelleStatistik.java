  void erstelleStatistik(int SL) {
    int i,j,k,l;
    int [] h = new int[256];			// Zahlenstatistik
    for (j=0; j<SL; j++) {
      System.out.print(".");
      for (i=0; i<h.length; i++) h[i]=0;	// alles auf Null
      for (i=j; i<c.length; i+=SL ) h[c[i]]++; 	// erstmal alles zaehlen
      for (i=0; i<10; i++) {			// 10 haeufigsten 10 Buchstaben
        k=0;
        for (l=1; l<256; l++) 
          if (h[l] > h[k]) k=l;
        sSpitze[j][i]=(double)(h[k]*SL)/(double)(cLaenge);  // relativ
        cSpitze[j][i]=k;
        h[k]=0;
      }
    }
  }
