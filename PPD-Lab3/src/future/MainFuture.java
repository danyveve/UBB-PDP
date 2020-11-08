package future;

import threadpool.MainThreadPool;
import threadpool.Read2MatrixResult;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainFuture {

    public static void main(String[] args) {
//        sumProgram();
//        productProgram();
    }

    private static void sumProgram() {
        try {
            Read2MatrixResult sumMatrixes = MainThreadPool.readTwoMatrixesFromFile("sum_matrix");
            int sumResultSize = sumMatrixes.getN1() * sumMatrixes.getM1();

            for (int nrThreads = 1; nrThreads <= sumResultSize; nrThreads++) {
                int nrThreadsCopy = nrThreads;
                int sumResultSizeCopy = sumResultSize;
                int startElement = 0;
                int[][] result = new int[sumMatrixes.getN1()][sumMatrixes.getM1()];

                //start threads
                Instant startTime = Instant.now();
                ExecutorService executor = Executors.newFixedThreadPool(nrThreads);
                List<Future<?>> futures = new ArrayList<>();


                for (int j = 1; j <= nrThreads; j++) {
                    int threadShare;

                    if (sumResultSizeCopy % nrThreadsCopy == 0) {
                        threadShare = Double.valueOf((double) sumResultSizeCopy / nrThreadsCopy).intValue();
                    } else {
                        threadShare = Double.valueOf(Math.ceil((double) sumResultSizeCopy / nrThreadsCopy)).intValue();
                    }

                    futures.add(new FutureSumExecutor(sumMatrixes.getMatrix1(), sumMatrixes.getMatrix2(), result, sumMatrixes.getM1(), startElement, startElement + threadShare - 1, executor).run());

                    startElement += threadShare;
                    nrThreadsCopy -= 1;
                    sumResultSizeCopy -= threadShare;
                }

                checkIfDone(futures);
                executor.shutdown();
                //calculate elapsed time
                Instant endTime = Instant.now();
                Duration timePassed = Duration.between(startTime, endTime);
                String timeUnit = timePassed.getSeconds() == 0 ? timePassed.toMillis() + " milliseconds." : timePassed.getSeconds() + " seconds.";
                System.out.println("Nr. Threads: " + nrThreads + " ---> " + timeUnit);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void checkIfDone(List<Future<?>> futures) {
        boolean isDone = false;
        while(!isDone){
            isDone = true;
            for(Future<?> future : futures){
                if (!future.isDone()){
                    isDone = false;
                    break;
                }
            }
        }
    }

    private static void productProgram() {
        try {
            Read2MatrixResult prodMatrixes = MainThreadPool.readTwoMatrixesFromFile("prod_matrix");
            int prodResultSize = prodMatrixes.getN1() * prodMatrixes.getM2();

            for (int nrThreads = 1; nrThreads <= prodResultSize; nrThreads++) {
                int nrThreadsCopy = nrThreads;
                int prodResultSizeCopy = prodResultSize;
                int startElement = 0;
                int[][] result = new int[prodMatrixes.getN1()][prodMatrixes.getM2()];

                //start threads
                Instant startTime = Instant.now();
                ExecutorService executor = Executors.newFixedThreadPool(nrThreads);
                List<Future<?>> futures = new ArrayList<>();

                for (int j = 1; j <= nrThreads; j++) {
                    int threadShare;

                    if (prodResultSizeCopy % nrThreadsCopy == 0) {
                        threadShare = Double.valueOf((double) prodResultSizeCopy / nrThreadsCopy).intValue();
                    } else {
                        threadShare = Double.valueOf(Math.ceil((double) prodResultSizeCopy / nrThreadsCopy)).intValue();
                    }

                    futures.add(new FutureProductExecutor(prodMatrixes.getMatrix1(), prodMatrixes.getMatrix2(), result, prodMatrixes.getM2(), prodMatrixes.getM1(), startElement, startElement + threadShare - 1, executor).run());

                    startElement += threadShare;
                    nrThreadsCopy -= 1;
                    prodResultSizeCopy -= threadShare;
                }

                checkIfDone(futures);
                executor.shutdown();
                //calculate elapsed time
                Instant endTime = Instant.now();
                Duration timePassed = Duration.between(startTime, endTime);
                String timeUnit = timePassed.getSeconds() == 0 ? timePassed.toMillis() + " milliseconds." : timePassed.getSeconds() + " seconds.";
                System.out.println("Nr. Threads: " + nrThreads + " ---> " + timeUnit);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}