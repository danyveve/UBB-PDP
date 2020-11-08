public class ProductExecutor implements Runnable{
    private int [][] matrix1;
    private int [][] matrix2;
    private int [][] result;
    private int lineStart;
    private int lineEnd;
    private int columnStart;
    private int columnEnd;
    private int nrColsResult;
    private int nrColsMatrix1;

    public ProductExecutor(int[][] matrix1, int[][] matrix2, int[][] result, int nrColsResult, int nrColsMatrix1, int elemStart, int elemEnd) {
        this.matrix1 = matrix1;
        this.matrix2 = matrix2;
        this.result = result;
        this.nrColsResult = nrColsResult;
        this.nrColsMatrix1 = nrColsMatrix1;

        this.lineStart = elemStart / nrColsResult;
        this.lineEnd = elemEnd / nrColsResult;
        this.columnStart = elemStart % nrColsResult;
        this.columnEnd = elemEnd % nrColsResult;
    }

    @Override
    public void run() {
        for(int i = lineStart ; i <= lineEnd; i++){
            int currentJstart = 0;
            int currentJend = nrColsResult - 1;

            if(i == lineEnd && i == lineStart){
                currentJstart = columnStart;
                currentJend = columnEnd;
            }
            else if (i == lineStart){
                currentJstart = columnStart;
                currentJend = nrColsResult -1;
            }
            else if(i == lineEnd){
                currentJend = columnEnd;
            }

            for(int j = currentJstart; j <= currentJend; j++){
                int sum = 0;
                for(int k = 0; k < nrColsMatrix1; k++){
                    sum += matrix1[i][k] * matrix2[k][j];
                }
                result[i][j] = sum;
            }
        }
    }
}
