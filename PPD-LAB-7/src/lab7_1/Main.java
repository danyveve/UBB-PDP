package lab7_1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<Integer> sequence = readSequenceFromFile("sequence");
        Integer nrThreads = readNrThreadsFromFile("nrThreads");
        System.out.println("Sequence = " + sequence);
        System.out.println();

        Instant startTime = Instant.now();
            List<Integer> resultSequential = PrefixHelper.prefixSequential(sequence);
        Instant endTime = Instant.now();

        Duration timePassed = Duration.between(startTime, endTime);
        String timeUnit = timePassed.getSeconds() == 0 ? timePassed.toMillis() + " milliseconds." : timePassed.getSeconds() + " seconds.";
        System.out.println("<<<<<<<<<<    Sequential version    >>>>>>>>>>");
//        System.out.println("Result = " + resultSequential);
        System.out.println("Computed in : " + timeUnit);
        System.out.println();

        startTime = Instant.now();
        List<Integer> resultParallel = PrefixHelper.prefixParallel(sequence, nrThreads);
        endTime = Instant.now();

        timePassed = Duration.between(startTime, endTime);
        timeUnit = timePassed.getSeconds() == 0 ? timePassed.toMillis() + " milliseconds." : timePassed.getSeconds() + " seconds.";
        System.out.println("<<<<<<<<<<    Parallel version    >>>>>>>>>>");
        System.out.println("##########    " + nrThreads + " threads    ##########");
//        System.out.println("Result = " + resultParallel);
        System.out.println("Computed in : " + timeUnit);
    }

    public static Integer readNrThreadsFromFile(String fileName) {
        BufferedReader reader = null;
        int nrThreads = 1;
        try {
            reader = new BufferedReader(new FileReader(fileName));
            String nrThreadsString = reader.readLine();
            nrThreads = Integer.parseInt(nrThreadsString);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return nrThreads;
    }

    private static List<Integer> readSequenceFromFile(String fileName) {
        BufferedReader reader = null;
        List<Integer> sequence = new ArrayList<>();
        try {
            reader = new BufferedReader(new FileReader(fileName));
            String sequenceString = reader.readLine();
            String[] sequenceStringsArray = sequenceString.split(" ");
            for (String number : sequenceStringsArray){
                sequence.add(Integer.parseInt(number));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sequence;
    }
}
