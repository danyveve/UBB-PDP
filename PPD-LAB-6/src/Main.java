import javafx.util.Pair;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        try {
            Polinom polinom1 = new Polinom(new ArrayList<>());
            Polinom polinom2 = new Polinom(new ArrayList<>());
            Helper.readPolinomsFromFile(polinom1, polinom2);
            //System.out.print("P#1 = ");
            //Helper.printPolinom(polinom1);
            //System.out.print("P#2 = ");
            //Helper.printPolinom(polinom2);
            //System.out.println();
            Pair<Integer, Integer> nrThreads = Helper.readNrThreads();

            Integer nrThreadsO2 = nrThreads.getKey();
            Integer nrThreadsKaratsuba = nrThreads.getValue();

            assert (nrThreadsO2 <= polinom1.getDegree() * polinom2.getDegree()) : "The given number of threads for O2 alg is too big!";

            Instant startTime = Instant.now();
            Polinom resultO2sequential = ProductO2.multiplyO2sequential(polinom1, polinom2);
            Instant endTime = Instant.now();
            Duration timePassed = Duration.between(startTime, endTime);
            String timeUnit = timePassed.getSeconds() == 0 ? timePassed.toMillis() + " milliseconds." : timePassed.getSeconds() + " seconds.";
            System.out.println("<<<<<<<<<<<<<<<<<<<<    O2 sequential    >>>>>>>>>>>>>>>>>>>>");
            System.out.println("Result: ");
            Helper.printPolinom(resultO2sequential);
            System.out.println("Computed in : " + timeUnit);
            System.out.println();


            startTime = Instant.now();
            Polinom resultO2parallel = ProductO2.multiplyO2parallel(polinom1, polinom2, nrThreadsO2);
            endTime = Instant.now();
            timePassed = Duration.between(startTime, endTime);
            timeUnit = timePassed.getSeconds() == 0 ? timePassed.toMillis() + " milliseconds." : timePassed.getSeconds() + " seconds.";
            System.out.println("<<<<<<<<<<<<<<<<<<<<    O2 parallel    >>>>>>>>>>>>>>>>>>>>");
            System.out.println("@@@@@@@@@@@@@@@@@@@@    " + nrThreadsO2 + " threads    @@@@@@@@@@@@@@@@@@@@");
            System.out.println("Result: ");
           // Helper.printPolinom(resultO2parallel);
            System.out.println("Computed in : " + timeUnit);
            System.out.println();


            startTime = Instant.now();
            Polinom resultKaratsSequential = ProductKaratsuba.multiplyKaratsubaSequantial(polinom1, polinom2);
            endTime = Instant.now();
            timePassed = Duration.between(startTime, endTime);
            timeUnit = timePassed.getSeconds() == 0 ? timePassed.toMillis() + " milliseconds." : timePassed.getSeconds() + " seconds.";
            System.out.println("<<<<<<<<<<<<<<<<<<<<    Karatsuba sequential    >>>>>>>>>>>>>>>>>>>>");
            System.out.println("Result: ");
            //Helper.printPolinom(resultKaratsSequential);
            System.out.println("Computed in : " + timeUnit);
            System.out.println();


            startTime = Instant.now();
            Polinom resultKaratsubaParallel = new Polinom();
            Thread karatsubaMainThread = new Thread(() -> {
                Polinom resultKaratsubaParallelComputed = ProductKaratsuba.multiplyKaratsubaSequantial(polinom1, polinom2);
                resultKaratsubaParallel.setCoefficients(resultKaratsubaParallelComputed.getCoefficients());
            });
            karatsubaMainThread.start();
            karatsubaMainThread.join();
            endTime = Instant.now();
            timePassed = Duration.between(startTime, endTime);
            timeUnit = timePassed.getSeconds() == 0 ? timePassed.toMillis() + " milliseconds." : timePassed.getSeconds() + " seconds.";
            System.out.println("<<<<<<<<<<<<<<<<<<<<    Karatsuba parallel    >>>>>>>>>>>>>>>>>>>>");
            System.out.println("@@@@@@@@@@@@@@@@@@@@    " + nrThreadsKaratsuba + " threads    @@@@@@@@@@@@@@@@@@@@");
            System.out.println("Result: ");
            //Helper.printPolinom(resultKaratsubaParallel);
            System.out.println("Computed in : " + timeUnit);


        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
