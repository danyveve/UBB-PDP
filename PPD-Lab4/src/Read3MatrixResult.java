import java.util.List;

public class Read3MatrixResult {
    private List<List<Element>> matrix1;
    private List<List<Element>> matrix2;
    private List<List<Element>> matrix3;
    private int n1;
    private int m1;
    private int n2;
    private int m2;
    private int n3;
    private int m3;

    public Read3MatrixResult(List<List<Element>> matrix1, List<List<Element>> matrix2, List<List<Element>> matrix3, int n1, int m1, int n2, int m2, int n3, int m3) {
        this.matrix1 = matrix1;
        this.matrix2 = matrix2;
        this.matrix3 = matrix3;
        this.n1 = n1;
        this.m1 = m1;
        this.n2 = n2;
        this.m2 = m2;
        this.n3 = n3;
        this.m3 = m3;
    }

    public List<List<Element>> getMatrix1() {
        return matrix1;
    }

    public void setMatrix1(List<List<Element>> matrix1) {
        this.matrix1 = matrix1;
    }

    public List<List<Element>> getMatrix2() {
        return matrix2;
    }

    public void setMatrix2(List<List<Element>> matrix2) {
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

    public List<List<Element>> getMatrix3() {
        return matrix3;
    }

    public void setMatrix3(List<List<Element>> matrix3) {
        this.matrix3 = matrix3;
    }

    public int getN3() {
        return n3;
    }

    public void setN3(int n3) {
        this.n3 = n3;
    }

    public int getM3() {
        return m3;
    }

    public void setM3(int m3) {
        this.m3 = m3;
    }
}
