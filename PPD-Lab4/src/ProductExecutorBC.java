import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class ProductExecutorBC implements Runnable {
    private List<List<Element>> matrix1;
    private List<List<Element>> matrix2;
    private List<List<Element>> result;
    private int nrLinesResult;
    private int nrColsResult;
    private int counter;

    public ProductExecutorBC(List<List<Element>> matrix1, List<List<Element>> matrix2, List<List<Element>> result, int nrLinesResult, int nrColsResult, int counter) {
        this.matrix1 = matrix1;
        this.matrix2 = matrix2;
        this.result = result;
        this.nrColsResult = nrColsResult;
        this.nrLinesResult = nrLinesResult;
        this.counter = counter;
    }

    @Override
    public void run() {
        //now at least a line is ready, so try to find an element in the result matrix which is not computed,
        //then check if we have the available info so we can compute it, and if so, compute it
        boolean resultIsDone = false;
        while (!resultIsDone) {
            resultIsDone = true;
            for (int i = 0; i < nrLinesResult; i++) {
                for (int j = 0; j < nrColsResult; j++) {
                    Element oneResultElement = result.get(i).get(j);
                    oneResultElement.getLock().lock();
                    if (oneResultElement.getInfo() == null) {
                        resultIsDone = false;
                        if (checkIfDataIsAvailableToComputeThisLine(i)) {
                            int sum = 0;
                            for (int index = 0; index < this.matrix1.get(0).size(); index++) {
                                sum += this.matrix1.get(i).get(index).getInfo() * this.matrix2.get(index).get(j).getInfo();
                            }
                            oneResultElement.setInfo(sum);
                            resultIsDone = true;
                        }
                    }
                    oneResultElement.getLock().unlock();
                }
            }
        }
        System.out.println(counter + "@");
    }

    private boolean checkIfDataIsAvailableToComputeThisLine(int i) {
        return this.matrix1.get(i).stream().map(el -> !(el.getInfo() == null)).reduce(true, Boolean::logicalAnd);
    }
}
