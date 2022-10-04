public class Hex2Byte {
  public static void main(String arg[])  {
    if (arg.length== 0)  System.exit(0);
    byte[] text;
    Datei d;
    d = new Datei();
    System.out.println("Schreibe Datei ... ");
    d.schreib(Hex.fromString(arg[0]));
    System.out.println("---- Datei: "+d.dateiname+ "----");
    System.exit(0);
  }
}