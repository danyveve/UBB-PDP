import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class Polinom {
    private List<Integer> coefficients;

    public Polinom() {

    }

    public int getDegree() {
        return coefficients.size();
    }


    public Polinom(List<Integer> coefficients) {
        this.coefficients = coefficients;
    }

    public List<Integer> getCoefficients() {
        return coefficients;
    }

    public void setCoefficients(List<Integer> coefficients) {
        this.coefficients = coefficients;
    }

    public Integer getCoeffAt(int i) {
        return this.coefficients.get(i);
    }

    public List<Integer> getSubListCoeff(int startPos, int endPos) {
        return this.coefficients.subList(startPos, endPos);
    }

    public Polinom add(Polinom polinom2) {
        int i = 0;
        List<Integer> result = new ArrayList<>();
        int smallestSize = Math.min(this.getDegree(), polinom2.getDegree());
        while (i < smallestSize) {
            result.add(this.getCoeffAt(i) + polinom2.getCoeffAt(i));
            i++;
        }

        if (this.getDegree() == smallestSize) {
            result.addAll(polinom2.getSubListCoeff(smallestSize, polinom2.getDegree()));
        } else {
            result.addAll(this.getSubListCoeff(smallestSize, this.getDegree()));
        }

        return new Polinom(result);
    }

    public Polinom substract(Polinom polinom2) {
        int i = 0;
        List<Integer> result = new ArrayList<>();
        int smallestSize = Math.min(this.getDegree(), polinom2.getDegree());
        while (i < smallestSize) {
            result.add(this.getCoeffAt(i) - polinom2.getCoeffAt(i));
            i++;
        }

        if (this.getDegree() == smallestSize) {
            result.addAll(polinom2.getSubListCoeff(smallestSize, polinom2.getDegree()).stream().map(coeff -> -coeff).collect(Collectors.toList()));
        } else {
            result.addAll(this.getSubListCoeff(smallestSize, this.getDegree()));
        }

        return new Polinom(result);
    }
}
