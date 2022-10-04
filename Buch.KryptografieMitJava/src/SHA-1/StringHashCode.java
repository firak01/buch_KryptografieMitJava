import javax.swing.*;

public class StringHashCode {
  public static void main( String args[] ) {
    String s0 = "A", s1 = "hello",s2 = "Hello",
      s3 = "Dies ist ein Dokument, ein wichtiges Dokument was deswegen signiert wird",
      s4 = "Dis ist ein Dokument, ein wichtiges Dokument was deswegen signiert wird";
    String output =
      "Hashcode fuer \"" + s0 + "\": " +  s0.hashCode() + 
      "\nHashcode fuer \"" + s1 + "\": " +  s1.hashCode() + 
      "\nHashcode fuer \"" + s2 + "\": " + s2.hashCode() +
      "\nHashcode fuer \"Dies ist ein Dokument ...\": " + s3.hashCode() +
      "\nHashcode fuer \"Dis ist ...\" (EIN Zeichen verändert): " + s4.hashCode();
    JOptionPane.showMessageDialog(null, output, "Demon String Method hashCode",
       				  JOptionPane.INFORMATION_MESSAGE );
    System.exit(0);
  }
}
