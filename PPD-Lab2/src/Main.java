import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {
//        sumProgram();
        productProgram();
    }

    private static void sumProgram(){
        try {
            Read2MatrixResult sumMatrixes = readTwoMatrixesFromFile("sum_matrix");
            int sumResultSize = sumMatrixes.getN1() * sumMatrixes.getM1();

            for (int nrThreads = 1; nrThreads <= sumResultSize; nrThreads++) {
                List<Thread> myThreads = new ArrayList<>();
                int nrThreadsCopy = nrThreads;
                int sumResultSizeCopy = sumResultSize;
                int startElement = 0;
                int[][] result = new int[sumMatrixes.getN1()][sumMatrixes.getM1()];

                //start threads
                Instant startTime = Instant.now();

                for (int j = 1; j <= nrThreads; j++) {
                    Thread t;

                    int threadShare;

                    if (sumResultSizeCopy % nrThreadsCopy == 0) {
                        threadShare = Double.valueOf((double) sumResultSizeCopy / nrThreadsCopy).intValue();
                    } else {
                        threadShare = Double.valueOf(Math.ceil((double) sumResultSizeCopy / nrThreadsCopy)).intValue();
                    }

                    t = new Thread(new SumExecutor(sumMatrixes.getMatrix1(), sumMatrixes.getMatrix2(), result, sumMatrixes.getM1(), startElement, startElement + threadShare - 1));

                    startElement += threadShare;
                    nrThreadsCopy -= 1;
                    sumResultSizeCopy -= threadShare;

                    myThreads.add(t);
                    t.start();
                }

                for (Thread t : myThreads) {
                    t.join();
                }

                //calculate elapsed time
                Instant endTime = Instant.now();
                Duration timePassed = Duration.between(startTime, endTime);
                String timeUnit = timePassed.getSeconds() == 0 ? timePassed.toMillis() + " milliseconds." : timePassed.getSeconds() + " seconds.";
                System.out.println("Nr. Threads: " + nrThreads + " ---> " + timeUnit);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void productProgram(){
        try {
            Read2MatrixResult prodMatrixes = readTwoMatrixesFromFile("prod_matrix");
            int prodResultSize = prodMatrixes.getN1() * prodMatrixes.getM2();

            for (int nrThreads = 1; nrThreads <= prodResultSize; nrThreads++) {
                List<Thread> myThreads = new ArrayList<>();
                int nrThreadsCopy = nrThreads;
                int prodResultSizeCopy = prodResultSize;
                int startElement = 0;
                int[][] result = new int[prodMatrixes.getN1()][prodMatrixes.getM2()];

                //start threads
                Instant startTime = Instant.now();

                for (int j = 1; j <= nrThreads; j++) {
                    Thread t;

                    int threadShare;

                    if (prodResultSizeCopy % nrThreadsCopy == 0) {
                        threadShare = Double.valueOf((double) prodResultSizeCopy / nrThreadsCopy).intValue();
                    } else {
                        threadShare = Double.valueOf(Math.ceil((double) prodResultSizeCopy / nrThreadsCopy)).intValue();
                    }

                    t = new Thread(new ProductExecutor(prodMatrixes.getMatrix1(), prodMatrixes.getMatrix2(), result, prodMatrixes.getM2(), prodMatrixes.getM1(), startElement, startElement + threadShare - 1));

                    startElement += threadShare;
                    nrThreadsCopy -= 1;
                    prodResultSizeCopy -= threadShare;

                    myThreads.add(t);
                    t.start();
                }

                for (Thread t : myThreads) {
                    t.join();
                }

                //calculate elapsed time
                Instant endTime = Instant.now();
                Duration timePassed = Duration.between(startTime, endTime);
                String timeUnit = timePassed.getSeconds() == 0 ? timePassed.toMillis() + " milliseconds." : timePassed.getSeconds() + " seconds.";
                System.out.println("Nr. Threads: " + nrThreads + " ---> " + timeUnit);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }




    private static void readMatrix(BufferedReader reader, int n, int m, int[][] matrix) throws IOException {
        String line;
        for (int i = 0; i < n; i++) {
            line = reader.readLine();
            String[] splittedLine = line.split(" ");
            for (int j = 0; j < m; j++) {
                matrix[i][j] = Integer.parseInt(splittedLine[j]);
            }
        }
    }

    private static Read2MatrixResult readTwoMatrixesFromFile(String fileName) throws IOException {
        BufferedReader reader;

        reader = new BufferedReader(new FileReader(fileName));

        String line = reader.readLine();
        String[] matrixDimensions = line.split(" ");
        int n1 = Integer.parseInt(matrixDimensions[0]);
        int m1 = Integer.parseInt(matrixDimensions[1]);
        int matrix1[][] = new int[n1][m1];


        readMatrix(reader, n1, m1, matrix1);

        line = reader.readLine();
        matrixDimensions = line.split(" ");
        int n2 = Integer.parseInt(matrixDimensions[0]);
        int m2 = Integer.parseInt(matrixDimensions[1]);
        int matrix2[][] = new int[n2][m2];

        readMatrix(reader, n2, m2, matrix2);

        reader.close();
        return new Read2MatrixResult(matrix1, matrix2, n1, m1, n2, m2);
    }
}
