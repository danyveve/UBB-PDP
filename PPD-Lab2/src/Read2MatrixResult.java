public class Read2MatrixResult {
    private int[][] matrix1;
    private int[][] matrix2;
    private int n1;
    private int m1;
    private int n2;
    private int m2;

    public Read2MatrixResult(int[][] matrix1, int[][] matrix2, int n1, int m1, int n2, int m2) {
        this.matrix1 = matrix1;
        this.matrix2 = matrix2;
        this.n1 = n1;
        this.m1 = m1;
        this.n2 = n2;
        this.m2 = m2;
    }

    public int[][] getMatrix1() {
        return matrix1;
    }

    public void setMatrix1(int[][] matrix1) {
        this.matrix1 = matrix1;
    }

    public int[][] getMatrix2() {
        return matrix2;
    }

    public void setMatrix2(int[][] matrix2) {
        this.matrix2 = matrix2;
    }

    public int getN1() {
        return n1;
    }

    public void setN1(int n1) {
        this.n1 = n1;
    }

    public int getM1() {
        return m1;
    }

    public void setM1(int m1) {
        this.m1 = m1;
    }

    public int getN2() {
        return n2;
    }

    public void setN2(int n2) {
        this.n2 = n2;
    }

    public int getM2() {
        return m2;
    }

    public void setM2(int m2) {
        this.m2 = m2;
    }
}
