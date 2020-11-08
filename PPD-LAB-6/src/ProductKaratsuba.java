import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

class ProductKaratsuba {

    static Polinom multiplyKaratsubaSequantial(Polinom polinom1, Polinom polinom2) {
        if (polinom1.getDegree() == 1) {
            Integer scalar = polinom1.getCoeffAt(0);
            return new Polinom(polinom2.getCoefficients().stream().map(coeff -> coeff * scalar).collect(Collectors.toList()));
        } else if (polinom2.getDegree() == 1) {
            Integer scalar = polinom2.getCoeffAt(0);
            return new Polinom(polinom1.getCoefficients().stream().map(coeff -> coeff * scalar).collect(Collectors.toList()));
        }

        Integer smallerDegree = Math.min(polinom1.getDegree(), polinom2.getDegree());
        Integer middle = Math.floorDiv(smallerDegree, 2);

        Polinom low1 = new Polinom(polinom1.getSubListCoeff(0, middle));
        Polinom high1 = new Polinom(polinom1.getSubListCoeff(middle, polinom1.getDegree()));
        Polinom low2 = new Polinom(polinom2.getSubListCoeff(0, middle));
        Polinom high2 = new Polinom(polinom2.getSubListCoeff(middle, polinom2.getDegree()));

        Polinom product0 = multiplyKaratsubaSequantial(low1, low2);
        Polinom product1 = multiplyKaratsubaSequantial(low1.add(high1), low2.add(high2));
        Polinom product2 = multiplyKaratsubaSequantial(high1, high2);

        Polinom highPolinom = new Polinom(new ArrayList<>(Collections.nCopies(middle * 2, 0)));
        highPolinom.getCoefficients().addAll(product2.getCoefficients());


        Polinom intermediaryPolinom = new Polinom(new ArrayList<>(Collections.nCopies(middle, 0)));
        intermediaryPolinom.getCoefficients().addAll(product1.substract(product2).substract(product0).getCoefficients());

        return highPolinom.add(intermediaryPolinom.add(product0));
    }


    Polinom multiplyKaratsubaParallel(Polinom polinom1, Polinom polinom2) {
        if (polinom1.getDegree() == 1) {
            Integer scalar = polinom1.getCoeffAt(0);
            return new Polinom(polinom2.getCoefficients().stream().map(coeff -> coeff * scalar).collect(Collectors.toList()));
        } else if (polinom2.getDegree() == 1) {
            Integer scalar = polinom2.getCoeffAt(0);
            return new Polinom(polinom1.getCoefficients().stream().map(coeff -> coeff * scalar).collect(Collectors.toList()));
        }

        Integer smallerDegree = Math.min(polinom1.getDegree(), polinom2.getDegree());
        Integer middle = Math.floorDiv(smallerDegree, 2);

        Polinom low1 = new Polinom(polinom1.getSubListCoeff(0, middle));
        Polinom high1 = new Polinom(polinom1.getSubListCoeff(middle, polinom1.getDegree()));
        Polinom low2 = new Polinom(polinom2.getSubListCoeff(0, middle));
        Polinom high2 = new Polinom(polinom2.getSubListCoeff(middle, polinom2.getDegree()));

        Polinom product0 = new Polinom();
        Polinom product1 = new Polinom();
        Polinom product2 = new Polinom();


        ///threaded stuff
        Thread product0Thread = new Thread(() -> {
            Polinom product0Computed = multiplyKaratsubaParallel(low1, low2);
            product0.setCoefficients(product0Computed.getCoefficients());
        });
        product0Thread.start();

        Thread product1Thread = new Thread(() -> {
            Polinom product1Computed = multiplyKaratsubaParallel(low1.add(high1), low2.add(high2));
            product1.setCoefficients(product1Computed.getCoefficients());
        });
        product1Thread.start();

        Thread product2Thread = new Thread(() -> {
            Polinom product2Computed = multiplyKaratsubaParallel(high1, high2);
            product2.setCoefficients(product2Computed.getCoefficients());
        });
        product2Thread.start();

        try {
            product0Thread.join();
            product1Thread.join();
            product2Thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Polinom highPolinom = new Polinom(new ArrayList<>(Collections.nCopies(middle * 2, 0)));
        highPolinom.getCoefficients().addAll(product2.getCoefficients());


        Polinom intermediaryPolinom = new Polinom(new ArrayList<>(Collections.nCopies(middle, 0)));
        intermediaryPolinom.getCoefficients().addAll(product1.substract(product2).substract(product0).getCoefficients());

        return highPolinom.add(intermediaryPolinom.add(product0));
    }
}

