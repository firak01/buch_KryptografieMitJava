import java.util.*;					// nur fuer Zeitmessung

class showInt {
  public static void printInt(int I) {
    for (int i=31; i>=0; i--)
      System.out.print((I >>> i) & 1);
    System.out.println();
  }
  public static void printByteArray(byte[] B) {
    for (int h=0; h<B.length; h++) {
      for (int i=7; i>=0; i--)
        System.out.print((B[h] >>> i) & 1);
      System.out.println();
    }
  }
  public static void main (String[] args) {
    Date dat1 = new Date();
    printInt(Integer.parseInt(args[0]));
    long ms = dat1.getTime();				// millisekunden
    for (int i=0; i<100000; i++)
      printInt((int)(Math.random()*Integer.MAX_VALUE));	// Schluessel bilden
    dat1 = new Date();
    ms = dat1.getTime()-ms;
    System.out.println("100.000 Werte in "+ms+" msek bestimmt!");

  }
}
