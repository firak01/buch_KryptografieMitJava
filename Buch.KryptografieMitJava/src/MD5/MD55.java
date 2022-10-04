//import java.math.BigInteger;

class MD55 {
  public static void main (String[] args) {
    long[]y = new long[64];
    double ZweiHoch32=(double)65536*(double)65536;  // 2^32
    for (int j=1; j<65; j++) {
      y[j-1]=(long)Math.abs(Math.sin((double)j)*ZweiHoch32);
      System.out.println(Long.toHexString(y[j-1]));
      if (j%16 ==0) System.out.println();
    }
  }
}
