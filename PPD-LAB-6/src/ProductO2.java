import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

class ProductO2 {
    static Polinom multiplyO2sequential(Polinom polinom1, Polinom polinom2) {
        List<Integer> result = new ArrayList<>(Collections.nCopies(polinom1.getDegree() + polinom2.getDegree() - 1, 0));
        for (int i = 0; i < polinom1.getDegree(); i++) {
            for (int j = 0; j < polinom2.getDegree(); j++) {
                result.set(i + j, result.get(i + j) + polinom1.getCoeffAt(i) * polinom2.getCoeffAt(j));
            }
        }

        return new Polinom(result);
    }

    static Polinom multiplyO2parallel(Polinom polinom1, Polinom polinom2, Integer nrThreadsO2) {
        List<Integer> result = new ArrayList<>(Collections.nCopies(polinom1.getDegree() + polinom2.getDegree() - 1, 0));
        List<ReentrantLock> locks = new ArrayList<>(Collections.nCopies(polinom1.getDegree() + polinom2.getDegree(), new ReentrantLock()));

        try {
            ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(nrThreadsO2);

            int startProduction = 0;
            int nrThreadsRemaining = nrThreadsO2;
            int nrProductionsRemaining = polinom1.getDegree() * polinom2.getDegree();
            for (int i = 1; i <= nrThreadsO2; i++) {
                int threadShare = Double.valueOf(Math.ceil((double) nrProductionsRemaining / nrThreadsRemaining)).intValue();
                int finalStartProduction = startProduction;
                threadPool.execute(() -> {
                    multiplyO2parallelTask(finalStartProduction, finalStartProduction + threadShare - 1, polinom1, polinom2, result, locks);
                });

                startProduction += threadShare;
                nrThreadsRemaining -= 1;
                nrProductionsRemaining -= threadShare;
            }
            threadPool.shutdown();
            threadPool.awaitTermination(5, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new Polinom(result);
    }

    static void multiplyO2parallelTask(int productionStart, int productionEnd, Polinom polinom1, Polinom polinom2, List<Integer> result, List<ReentrantLock> locks) {
        int startCoeff1 = productionStart / polinom2.getDegree();
        int startCoeff2 = productionStart % polinom2.getDegree();
        int endCoeff1 = productionEnd / polinom2.getDegree();
        int endCoeff2 = productionEnd % polinom2.getDegree();

        for (int i = startCoeff1; i <= endCoeff1; i++) {
            int currentJstart = 0;
            int currentJend = polinom2.getDegree() - 1;

            if(i == startCoeff1 && i == endCoeff1){
                currentJstart = startCoeff2;
                currentJend = endCoeff2;
            } else if (i == startCoeff1){
                currentJstart = startCoeff2;
            } else if (i == endCoeff1){
                currentJend = endCoeff2;
            }

            for(int j = currentJstart ; j <= currentJend; j++){
                locks.get(i+j).lock();
                result.set(i + j, result.get(i + j) + polinom1.getCoeffAt(i) * polinom2.getCoeffAt(j));
                locks.get(i+j).unlock();
            }
        }
    }
}
