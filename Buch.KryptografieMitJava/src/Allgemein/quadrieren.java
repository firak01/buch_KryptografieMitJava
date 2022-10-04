public class quadrieren {
  
  public static void main (String[] args) {
    long a = Long.parseLong(args[0]);
    long b = Long.parseLong(args[1]);
    long c = Long.parseLong(args[2]);
    System.out.println("Zerlegung einer Potenz in einfache Multiplikation");
    System.out.print("zur Berechnung von "+a+"^"+b+" mod "+c+" = ");
    long wert = 1;

    while (b > 0) {
	if ((b & 1) > 0) { wert = (wert*a) % c; }
        b >>= 1;
        a = (a*a) % c; 
    }
    System.out.println(wert);
  }
}