public class dBestimmen { 
  public static void main (String args[]) { 
  long d, e, dRest, phi, x;
    if (args.length == 0) { System.exit(0); } 
    e = Long.parseLong(args[0]); 
    phi = Long.parseLong(args[1]); 
    dRest = 1;  // Prophylaktisch!
    for (x=1; (dRest>0); x++) {
      d = (x*phi+1)/e;
      dRest = (x*phi+1)%e;
      System.out.println("x:"+x+" e:"+e+" d:"+d+" dRest:"+dRest);
    }
  }
}
