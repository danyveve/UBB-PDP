public class SumExecutor implements Runnable{
    private int [][] matrix1;
    private int [][] matrix2;
    private int [][] result;
    private int lineStart;
    private int lineEnd;
    private int columnStart;
    private int columnEnd;
    private int nrCols;

    public SumExecutor(int[][] matrix1, int[][] matrix2, int[][] result, int nrCols, int elemStart, int elemEnd) {
        this.matrix1 = matrix1;
        this.matrix2 = matrix2;
        this.result = result;
        this.nrCols = nrCols;

        this.lineStart = elemStart / nrCols;
        this.lineEnd = elemEnd / nrCols;
        this.columnStart = elemStart % nrCols;
        this.columnEnd = elemEnd % nrCols;
    }

    @Override
    public void run() {
        for(int i = lineStart ; i <= lineEnd; i++){
            int currentJstart = 0;
            int currentJend = nrCols - 1;

            if(i == lineEnd && i == lineStart){
                currentJstart = columnStart;
                currentJend = columnEnd;
            }
            else if (i == lineStart){
                currentJstart = columnStart;
                currentJend = nrCols-1;
            }
            else if(i == lineEnd){
                currentJend = columnEnd;
            }

            for(int j = currentJstart; j <= currentJend; j++){
                result[i][j] = matrix1[i][j] + matrix2[i][j];
            }
        }
    }
}
