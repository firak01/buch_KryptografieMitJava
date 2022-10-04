  int bestimmeSL() {   // gibt 0 zurück, falls nicht erfolgreich
    double kDeutsch=0.05, kappaM=0.0; 
    int i,j,k,kappa,SL=1,iD=0,MaxDurchlauf=10;
    boolean ende=false;
    j=0;
    while ((j<mlaenge) && (ende==false)) {
      kappa=0;	
      k=0;
      for (i=j; i<(j+mlaenge); i++) {
        if (mtext[i%mlaenge]==mtext[k]) kappa++;
        k++;
      }
      kappaM=(double)kappa/(double)mlaenge;  // Mittelwert
      if (kappaM>kDeutsch) {
        SL=j-SL;
        System.out.println("j="+j+"\tSL="+SL+"\tKappa="+kappaM);
        iD++;
        if((iD%MaxDurchlauf)==0) {
          System.out.print("Weitermachen (J/N)? >");
          if (!Eingabe.JaNein()) return SL;
        }
        SL=j;
      }
      j++;
    }
    return 0;   			// falls ende erreicht
  }
