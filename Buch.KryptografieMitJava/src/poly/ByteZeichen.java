class ByteZeichen {
  public static void main( String[] arg) {
    String zeichen="ÄÖÜäöüß";
    System.out.println();
    byte[] zByte = new byte[zeichen.length()];
    zByte = zeichen.getBytes();
    int[] zUni = new int[7];
    zUni=IO.Unicode(zByte);
    for (int i=0; i<zeichen.length(); i++)
      System.out.println(zeichen.charAt(i)+": "+IO.char16(zByte[i]));
    System.out.println("-150 mod 128="+(-150%128));
    System.out.println("-200 mod 128="+(-200%128));
    System.out.println("-200 mod (-128)="+(-200%(-128)));
    System.out.println("+150 mod 128="+(150%128));
    System.out.println("-150 mod (-128)="+(-150%(-128)));
  }
}
