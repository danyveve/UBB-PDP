import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class ProductExecutorAB implements Runnable {
    private List<List<Element>> matrix1;
    private List<List<Element>> matrix2;
    private List<List<Element>> result;
    private int lineStart;
    private int lineEnd;
    private int columnStart;
    private int columnEnd;
    private int nrColsResult;
    private int nrColsMatrix1;
    private Condition isSomethingReady;
    private static Boolean hasBeenSignaled = false;
    private Lock lock;
    private List<Thread> threadsBC;

    public ProductExecutorAB(List<List<Element>> matrix1, List<List<Element>> matrix2, List<List<Element>> result, int nrColsResult, int nrColsMatrix1, int elemStart, int elemEnd, Condition isSomethingReady, Lock lock, List<Thread> threadsBC) {
        this.matrix1 = matrix1;
        this.matrix2 = matrix2;
        this.result = result;
        this.nrColsResult = nrColsResult;
        this.nrColsMatrix1 = nrColsMatrix1;

        this.lineStart = elemStart / nrColsResult;
        this.lineEnd = elemEnd / nrColsResult;
        this.columnStart = elemStart % nrColsResult;
        this.columnEnd = elemEnd % nrColsResult;
        this.isSomethingReady = isSomethingReady;

        this.lock = lock;
        this.threadsBC = threadsBC;
    }

    @Override
    public void run() {
        for (int i = lineStart; i <= lineEnd; i++) {
            int currentJstart = 0;
            int currentJend = nrColsResult - 1;

            if (i == lineEnd && i == lineStart) {
                currentJstart = columnStart;
                currentJend = columnEnd;
            } else if (i == lineStart) {
                currentJstart = columnStart;
                currentJend = nrColsResult - 1;
            } else if (i == lineEnd) {
                currentJend = columnEnd;
            }

            for (int j = currentJstart; j <= currentJend; j++) {
                int sum = 0;
                for (int k = 0; k < nrColsMatrix1; k++) {
                    sum += matrix1.get(i).get(k).getInfo() * matrix2.get(k).get(j).getInfo();
                }
                result.get(i).get(j).setInfo(sum);
            }
        }
        lock.lock();
        if (!ProductExecutorAB.hasBeenSignaled) {
            if(checkIfWeHaveAtLeastOneLine()){
                ProductExecutorAB.hasBeenSignaled = true;
                this.isSomethingReady.signalAll();
            }
        }
        lock.unlock();
    }

    private boolean checkIfWeHaveAtLeastOneLine(){
        for(List<Element> line: result){
            boolean enaughAvailable = true;
            for(Element el : line){
                if(el.getInfo() == null){
                    enaughAvailable = false;
                }
            }
            if (enaughAvailable){
                return true;
            }
        }
        return false;
    }
}
