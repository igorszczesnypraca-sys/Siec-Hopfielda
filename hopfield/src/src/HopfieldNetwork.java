package src;

public class HopfieldNetwork {

    private final int size;
    private final double[][] weights;

    public HopfieldNetwork(int size) {
        this.size = size;
        this.weights = new double[size][size];
    }

    // Uczenie Hebba
    public void train(int[][] patterns) {

        for (int[] pattern : patterns) {

            for (int i = 0; i < size; i++) {

                for (int j = 0; j < size; j++) {

                    if (i != j) {
                        weights[i][j] += pattern[i] * pattern[j];
                    }
                }
            }
        }
    }

    // FUNKCJA AKTYWACJI 3-STANOWA
    private int activationFunction(int previousState,double value) {

        // większy od 0
        if (value > 0) {
            return 1;
        }

        // mniejszy od 0
        if (value < 0) {
            return -1;
        }

        // równy 0
        return previousState;
    }

    // Asynchroniczna aktualizacja neuronu
    public void updateSingleNeuronAsync(int[] currentState) {

        int i = (int) (Math.random() * size);

        double sum = 0;

        for (int j = 0; j < size; j++) {
            sum += weights[i][j] * currentState[j];
        }

        // użycie funkcji aktywacji
        currentState[i] = activationFunction(currentState[i],sum);
    }
}