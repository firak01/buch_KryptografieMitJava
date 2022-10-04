  static public String bigIntToHex(BigInteger I) {
    int[] Wertigkeit = {1,2,4,8};
    int Stellen = I.bitLength()/4;
    if (I.bitLength()%4 > 0) Stellen++;
    String s="";
    int wert;
    for (int j=Stellen; j>0; j--) {
      wert = 0;
      for (int k=0; k<4; k++) 
        if (I.testBit((j-1)*4+k))
          wert = wert+Wertigkeit[k];
      if (wert<10) s = s + (char)(wert+48); 	// 0..9
      else	   s = s + (char)(wert+87);	// a..f
    }  
    return s;
  }
