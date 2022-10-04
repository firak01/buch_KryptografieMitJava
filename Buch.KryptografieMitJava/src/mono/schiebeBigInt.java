  public BigInteger schiebeBigInt(BigInteger BigI,int Bits,int anzahl,String Richtung) {
//
// das Schieben erfolgt GRUNDSAETZLICH mit Rotation!!! Ohne Rotation kann
// direkt die Methode der Klasse BigInteger verwendet werden!
//  
    boolean Bit;
    if (Richtung.equals("<-"))
      for (int i=0; i<anzahl; i++) {
        Bit = false;
        if (BigI.testBit(Bits-1))    // Bit zwischenspeichern
          Bit = true;
        BigI = BigI.shiftLeft(1);
        if (Bit) 
          BigI=BigI.setBit(0);
      }
    if (Richtung.equals("->")) 
      for (int i=0; i<anzahl; i++) {
        Bit = false;
        if (BigI.testBit(0))         // Bit zwischenspeichern
          Bit = true;
        BigI = BigI.shiftRight(1);
        if (Bit) 
          BigI=BigI.setBit(Bits-1);
      }
    return BigI;
  }  
