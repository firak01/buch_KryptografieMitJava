public final class MulTest {

  private static short mul(int a,int b) {
    a &= 0xFFFF;
    b &= 0xFFFF;
//    System.out.println("Multiplikation von: "+Hex.intToString(a)+
//                       " und "+Hex.intToString(b));
    int p;
    if (a != 0) {
      if (b != 0) {
        p = a * b;
        b = p & 0xFFFF;
        a = p >>> 16;
        return (short)(b - a + (b < a ? 1 : 0));
      }
      else return (short)(1 - a);
    }
    else return (short)(1 - b);
  }
//-------------------------------------------------------------
  private static short mul4(int a,int b) {
    a &= 0xF;
    b &= 0xF;
//    System.out.println("Multiplikation von: "+Hex.intToString(a)+
//                       " und "+Hex.intToString(b));
    int p;
    if (a != 0) {
      if (b != 0) {
        p = a * b;
        b = p & 0xF;
        a = p >>> 4;
        return (short)(b - a + (b < a ? 1 : 0));
      }
      else return (short)((1 - a)&0xF);
    }
    else return (short)((1 - b)&0xF);
  }
//-------------------------------------------------------------
  private static short mul(int a,int b) {
    a &= 0xF;
    b &= 0xF;
//    System.out.println("Multiplikation von: "+Hex.intToString(a)+
//                       " und "+Hex.intToString(b));
    int p;
    if (a == 0) a = 16;
    if (b == 0) b = 16;
    p = a * b;
    p = p%17;
    return (short)(p);
  }
//-------------------------------------------------------------
  public static void main (String[] arg) {
    int m;
    m = myMul4(Integer.parseInt(arg[0]),Integer.parseInt(arg[1]));
    System.out.println("Ergebnis: "+m);
    showInt.printInt(m);
  }
}