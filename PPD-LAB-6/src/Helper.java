import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class Helper {
    static Pair<Integer, Integer> readNrThreads() {
        BufferedReader reader = null;
        Integer nrThreadsO2 = null;
        Integer nrThreadsKaratsuba = null;
        try {
            reader = new BufferedReader(new FileReader("nrThreads"));
            nrThreadsO2 = Integer.parseInt(reader.readLine());
            nrThreadsKaratsuba = Integer.parseInt(reader.readLine());
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
        return new Pair<>(nrThreadsO2, nrThreadsKaratsuba);
    }

    static void readPolinomsFromFile(Polinom polinom1, Polinom polinom2) {
        BufferedReader reader1 = null;
        BufferedReader reader2 = null;
        try {
            reader1 = new BufferedReader(new FileReader("polinom1"));
            String line = reader1.readLine();
            while (line != null) {
                polinom1.getCoefficients().add(Integer.parseInt(line));
                line = reader1.readLine();
            }

            reader2 = new BufferedReader(new FileReader("polinom2"));
            line = reader2.readLine();
            while (line != null) {
                polinom2.getCoefficients().add(Integer.parseInt(line));
                line = reader2.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader1 != null) {
                try {
                    reader1.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (reader2 != null) {
                try {
                    reader2.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    static void printPolinom(Polinom polinom) {
        for (int i = 0; i < polinom.getDegree(); i++) {
            System.out.print(polinom.getCoeffAt(i));
            if (i != 0)
                System.out.print("x^" + i);
            if (i != polinom.getDegree() - 1)
                System.out.print(" + ");
        }
        System.out.print("\n");
    }
}
