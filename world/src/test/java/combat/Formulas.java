package combat;

public class Formulas {

    static int health = 100000;

    public static void main(String[] args) {
        System.out.println(getPercent(25, health));
    }

    public static int getPercent(int percent, int variable) {
        return (int) ((0.01 * percent) * variable);
    }
}
