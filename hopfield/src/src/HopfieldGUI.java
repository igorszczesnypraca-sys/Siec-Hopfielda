package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class HopfieldGUI extends JFrame {

    private final int GRID_SIZE = 8;
    private final int NUM_NEURONS = GRID_SIZE * GRID_SIZE;

    private HopfieldNetwork network;
    private JPanel[] cells;
    private int[] currentInput;

    private volatile boolean isRunning = false;

    // Litera H
    private final int[] H = {
            1,-1,-1,-1,-1,-1,-1,1,
            1,-1,-1,-1,-1,-1,-1,1,
            1,-1,-1,-1,-1,-1,-1,1,
            1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,1,
            1,-1,-1,-1,-1,-1,-1,1,
            1,-1,-1,-1,-1,-1,-1,1,
            1,-1,-1,-1,-1,-1,-1,1
    };

    // Litera A
    private final int[] A = {
            -1,-1,-1,1,1,-1,-1,-1,
            -1,-1,1,-1,-1,1,-1,-1,
            -1,1,-1,-1,-1,-1,1,-1,
            1,-1,-1,-1,-1,-1,-1,1,
            1,1,1,1,1,1,1,1,
            1,-1,-1,-1,-1,-1,-1,1,
            1,-1,-1,-1,-1,-1,-1,1,
            1,-1,-1,-1,-1,-1,-1,1
    };

    // Litera K
    private final int[] K = {
            1,-1,-1,-1,-1,1,1,-1,
            1,-1,-1,-1,1,1,-1,-1,
            1,-1,-1,1,1,-1,-1,-1,
            1,1,1,1,-1,-1,-1,-1,
            1,1,1,1,-1,-1,-1,-1,
            1,-1,-1,1,1,-1,-1,-1,
            1,-1,-1,-1,1,1,-1,-1,
            1,-1,-1,-1,-1,1,1,-1
    };

    public HopfieldGUI() {

        network = new HopfieldNetwork(NUM_NEURONS);
        network.train(new int[][]{H, A, K});

        currentInput = new int[NUM_NEURONS];

        // Start od białego tła
        java.util.Arrays.fill(currentInput, -1);

        setTitle("Sieć Hopfielda 8x8");
        setLayout(new BorderLayout());

        JPanel gridPanel = new JPanel(
                new GridLayout(GRID_SIZE, GRID_SIZE, 2, 2)
        );

        gridPanel.setBackground(Color.DARK_GRAY);

        cells = new JPanel[NUM_NEURONS];

        for (int i = 0; i < NUM_NEURONS; i++) {

            final int index = i;

            cells[i] = new JPanel();
            cells[i].setPreferredSize(new Dimension(50,50));
            cells[i].setBorder(
                    BorderFactory.createLineBorder(Color.GRAY)
            );

            // Rysowanie tylko 2 stanami
            cells[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {

                    // tylko biały <-> czarny
                    if (currentInput[index] == 1) {
                        currentInput[index] = -1;
                    } else {
                        currentInput[index] = 1;
                    }

                    updateGrid();
                }
            });

            gridPanel.add(cells[i]);
        }

        JButton recallBtn = new JButton("Odtwórz obraz");
        JButton clearBtn = new JButton("Wyczyść");

        recallBtn.addActionListener(e -> {

            if (isRunning) return;

            isRunning = true;
            recallBtn.setEnabled(false);

            new Thread(() -> {

                for (int step = 0;
                     step < 2000 && isRunning;
                     step++) {

                    network.updateSingleNeuronAsync(currentInput);

                    SwingUtilities.invokeLater(
                            this::updateGrid
                    );

                    try {
                        Thread.sleep(2);
                    } catch (InterruptedException ignored) {}
                }

                isRunning = false;

                SwingUtilities.invokeLater(
                        () -> recallBtn.setEnabled(true)
                );

            }).start();
        });

        clearBtn.addActionListener(e -> {

            isRunning = false;

            java.util.Arrays.fill(currentInput, -1);

            updateGrid();
            recallBtn.setEnabled(true);
        });

        add(gridPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        controlPanel.add(recallBtn);
        controlPanel.add(clearBtn);

        add(controlPanel, BorderLayout.SOUTH);

        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        updateGrid();
    }

    // Tylko 2 kolory
    private void updateGrid() {

        for (int i = 0; i < NUM_NEURONS; i++) {

            if (currentInput[i] == 1) {
                cells[i].setBackground(Color.BLACK);
            } else {
                // -1 oraz 0 pokazujemy jako biały
                cells[i].setBackground(Color.WHITE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(HopfieldGUI::new);
    }
}