import VisualNumerics.math.DoubleMatrix;
import VisualNumerics.math.MathException;

class InverseMatrix {

  public InverseMatrix() {
    System.out.print("Zeilenzahl: ");
    int BlockL = IO.ganzeZahl();
    int[]a = new int[BlockL*BlockL];
    System.out.print("Matrix A aus Datei einlesen? (J/N): ");
    if (IO.JaNein()) a = IO.liesIntsAusDatei(BlockL*BlockL);
    else {
      System.out.println("Eingabe der Schluesselmatrix A("+BlockL+","+BlockL+"):");
      System.out.println("(Eingabe zeilenweise und einzelne Werte durch SPACE getrennt!");
      a = IO.liesMatrixA(BlockL);			// Schluesselmatrix A
    }
    IO.printMatrix(a);
    double[][]A = new double[BlockL][BlockL];
    double[][]B = new double[BlockL][BlockL];
    for (int i=0; i<BlockL; i++)
      for (int j=0; j<BlockL; j++)
        A[i][j] = (double)a[i*BlockL+j];    
    IO.printMatrix(A);
    try { B =  DoubleMatrix.inverse(A); }
    catch (IllegalArgumentException e)
{
      System.out.println(e);
    }
    catch(MathException e)
{
      System.out.println(e);
      System.out.println("Inverse Matrix kann nicht bestimmt werden!");
      System.exit(0);
    }
    catch(Exception e)
{
      System.out.println(e);
    }
    System.out.println("Inverse Matrix:");
    IO.printMatrix(B);
    System.out.println("\nFertig!\n");
    System.exit(0);
  }
//------------------------------------------------------------------------
  public static void main( String[] arg) {
    InverseMatrix app = new InverseMatrix();
  }
}
