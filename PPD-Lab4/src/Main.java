import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    public static void main(String[] args) {
            productProgram();
    }

    private static void productProgram() {
        try {
            int nrThreadsAB = 100;
            int nrThreadsBC = 50;
            Lock lock = new ReentrantLock();
            Condition isSomethingReady = lock.newCondition();

            Read3MatrixResult prodMatrixes = readThreeMatrixesFromFile("prod_matrix");
            int prodResultSize1 = prodMatrixes.getN1() * prodMatrixes.getM2();
            List<List<Element>> resultAB = new ArrayList<>();
            for (int i = 0; i < prodMatrixes.getN1(); i++) {
                List<Element> oneLine = new ArrayList<>();
                for (int j = 0; j < prodMatrixes.getM2(); j++) {
                    oneLine.add(new Element());
                }
                resultAB.add(oneLine);
            }
            List<List<Element>> resultBC = new ArrayList<>();
            for (int i = 0; i < prodMatrixes.getN1(); i++) {
                List<Element> oneLine = new ArrayList<>();
                for (int j = 0; j < prodMatrixes.getM3(); j++) {
                    oneLine.add(new Element());
                }
                resultBC.add(oneLine);
            }


            //start threads for B*C
            List<Thread> threadsBC = new ArrayList<>();
            Thread threadBC = new Thread(() -> {
                lock.lock();
                try {
                    isSomethingReady.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < nrThreadsBC; i++) {
                    Thread t = new Thread(new ProductExecutorBC(resultAB, prodMatrixes.getMatrix3(), resultBC, prodMatrixes.getN1(), prodMatrixes.getM3(), i + 1));
                    t.start();
                    threadsBC.add(t);
                }
                lock.unlock();

            });
            threadBC.start();




            //start threads A*B
            int nrThreadsCopyAB = nrThreadsAB;
            int prodResultSizeCopy = prodResultSize1;
            int startElement = 0;

            //register the start time
            Instant startTime = Instant.now();

            for (int j = 1; j <= nrThreadsAB; j++) {
                int threadShare;

                if (prodResultSizeCopy % nrThreadsCopyAB == 0) {
                    threadShare = Double.valueOf((double) prodResultSizeCopy / nrThreadsCopyAB).intValue();
                } else {
                    threadShare = Double.valueOf(Math.ceil((double) prodResultSizeCopy / nrThreadsCopyAB)).intValue();
                }

                Thread t = new Thread(new ProductExecutorAB(prodMatrixes.getMatrix1(), prodMatrixes.getMatrix2(), resultAB, prodMatrixes.getM2(),
                        prodMatrixes.getM1(), startElement, startElement + threadShare - 1, isSomethingReady, lock, threadsBC));
                t.start();

                startElement += threadShare;
                nrThreadsCopyAB -= 1;
                prodResultSizeCopy -= threadShare;
            }

            boolean allDone = false;
            while (!allDone) {
                allDone = true;
                if(threadsBC.size() == 0){
                    allDone = false;
                }else{
                    lock.lock();
                    for (Thread t : threadsBC) {
                        if (t.isAlive()) {
                            allDone = false;
                        }
                    }
                    lock.unlock();
                }
            }

            //calculate elapsed time
            Instant endTime = Instant.now();
            Duration timePassed = Duration.between(startTime, endTime);
            String timeUnit = timePassed.getSeconds() == 0 ? timePassed.toMillis() + " milliseconds." : timePassed.getSeconds() + " seconds.";
            System.out.println("Nr. Threads A*B: " + nrThreadsAB +
                    "\nNr. Threads B*C " + nrThreadsBC +
                    "\n---> " + timeUnit);
            for (List<Element> el : resultBC) {
                for (Element e : el) {
                    System.out.print(e + " ");
                }
                System.out.print("\n");
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void readMatrix(BufferedReader reader, int n, int m, List<List<Element>> matrix) throws IOException {
        String line;
        for (int i = 0; i < n; i++) {
            line = reader.readLine();
            List<Element> oneLine = new ArrayList<>();
            String[] splittedLine = line.split(" ");
            for (int j = 0; j < m; j++) {
                oneLine.add(new Element(Integer.parseInt(splittedLine[j])));
            }
            matrix.add(oneLine);
        }
    }

    public static Read3MatrixResult readThreeMatrixesFromFile(String fileName) throws IOException {
        BufferedReader reader;

        reader = new BufferedReader(new FileReader(fileName));

        String line = reader.readLine();
        String[] matrixDimensions = line.split(" ");
        int n1 = Integer.parseInt(matrixDimensions[0]);
        int m1 = Integer.parseInt(matrixDimensions[1]);
        List<List<Element>> matrix1 = new ArrayList<>();


        readMatrix(reader, n1, m1, matrix1);

        line = reader.readLine();
        matrixDimensions = line.split(" ");
        int n2 = Integer.parseInt(matrixDimensions[0]);
        int m2 = Integer.parseInt(matrixDimensions[1]);
        List<List<Element>> matrix2 = new ArrayList<>();

        readMatrix(reader, n2, m2, matrix2);

        line = reader.readLine();
        matrixDimensions = line.split(" ");
        int n3 = Integer.parseInt(matrixDimensions[0]);
        int m3 = Integer.parseInt(matrixDimensions[1]);
        List<List<Element>> matrix3 = new ArrayList<>();

        readMatrix(reader, n3, m3, matrix3);

        reader.close();
        return new Read3MatrixResult(matrix1, matrix2, matrix3, n1, m1, n2, m2, n3, m3);
    }
}
