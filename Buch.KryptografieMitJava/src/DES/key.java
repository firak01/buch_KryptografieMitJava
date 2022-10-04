import java.math.BigInteger;

public class key {

  static boolean isWeak(byte[] key) {
    int a = (key[0] & 0xFE) << 8 | (key[1] & 0xFE);
    int b = (key[2] & 0xFE) << 8 | (key[3] & 0xFE);
    int c = (key[4] & 0xFE) << 8 | (key[5] & 0xFE);
    int d = (key[6] & 0xFE) << 8 | (key[7] & 0xFE);
    return (a==0x0000 || a==0xFEFE)&&(b==0x0000 || b==0xFEFE)&&
           (c==0x0000 || c==0xFEFE)&&(d==0x0000 || d==0xFEFE);
  }
  static boolean isWeak(BigInteger key) { 
    int[] ikey = new int[8];
    ikey = DES.BigInt2IntFeld(key); 
    byte[] bkey = new byte[8];
    for (int i=0; i<8;i++) {
      bkey[i]=(byte)ikey[i];
    }
    return isWeak(bkey);
  }
  public static void main (String[] args) {
    System.out.print("Schlüssel: "+args[0]+" ist ");
    if (isWeak(new BigInteger(args[0],16),0))
      System.out.println("ein weicher Schlüssel!");
    else
      System.out.println("kein weicher Schlüssel!");
  }
}
