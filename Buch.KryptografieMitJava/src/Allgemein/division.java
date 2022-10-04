public class division {
  
  public static void main (String[] args) {
    System.out.println("Divisionen in Java int a=17,b=3;");
    System.out.println("================================");
    int a = 17, b = 3;
    int c; 
    c = a/b;
    System.out.println("(a) int c;      c=a/b=17/3       = "+c);
    float d; 
    d = a/b;
    System.out.println("(b) float d;    d=a/b=17/3       = "+d);
    d = (float)a/b;
    System.out.println("(c) float d;    d=(float)a/b=17/3= "+d);
    c = a%b;
    System.out.println("(d) int c;      c=a%b=17/3       = "+c);

    System.out.println("\n\nModulo-Operationen in Java");
    System.out.println("==============================");
    System.out.println("(a) c = 17 % 3       = "+c);
    System.out.println("(a) c = 17 % -3      = "+(17 % -3));
    System.out.println("(a) c =-17 % 3       = "+(-17 % 3));
    System.out.println("(a) c =-17 % -3      = "+(-17 % -3));
  }
}