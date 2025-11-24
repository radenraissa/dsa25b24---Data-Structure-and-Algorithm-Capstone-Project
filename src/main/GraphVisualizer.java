package main;

import javax.swing.*;
import java.awt.*;

public class GraphVisualizer extends JFrame {
    private GraphPanel graphPanel;
    private Graph graph;
    private JComboBox<String> startCombo;
    private JComboBox<String> endCombo;

    public GraphVisualizer(int[][] adjacencyMatrix, boolean directed) {
        setTitle("Graph Visualizer with Dijkstra | " + (directed ? "Directed" : "Undirected"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        graph = new Graph(adjacencyMatrix, directed);
        graphPanel = new GraphPanel(graph);

        add(graphPanel, BorderLayout.CENTER);

        // panel kontrol di bawah
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());
        controlPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
        controlPanel.setBackground(new Color(240, 240, 240));

        //  dropdown nama kota
        String[] cityNames = new String[graph.getNodeCount()];
        for (int i = 0; i < graph.getNodeCount(); i++) {
            cityNames[i] = graph.getNodeName(i);
        }

        startCombo = new JComboBox<>(cityNames);
        endCombo = new JComboBox<>(cityNames);

        if (cityNames.length > 1) {
            endCombo.setSelectedIndex(1);
        }

        JButton runBtn = new JButton("Find Shortest Path");
        runBtn.setBackground(new Color(70, 130, 180));
        runBtn.setForeground(Color.WHITE);
        runBtn.setFocusPainted(false);

        // action Listener utk tombol
        runBtn.addActionListener(e -> {
            int srcIndex = startCombo.getSelectedIndex();
            int destIndex = endCombo.getSelectedIndex();

            if (srcIndex == destIndex) {
                JOptionPane.showMessageDialog(this, "Start and End node must be different!");
                return;
            }

            graph.runDijkstra(srcIndex, destIndex);

            graphPanel.repaint();
        });

        controlPanel.add(new JLabel("From:"));
        controlPanel.add(startCombo);
        controlPanel.add(new JLabel("To:"));
        controlPanel.add(endCombo);
        controlPanel.add(runBtn);

        add(controlPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        int[][] adjacencyMatrix1 = {
                {0, 4, 2, 0, 0},
                {4, 0, 1, 5, 3},
                {2, 1, 0, 8, 0},
                {0, 5, 8, 0, 6},
                {0, 3, 0, 6, 0}
        };
        // graph kompleks utk dijkstra
        int[][] adjacencyMatrix2 = {
                {0, 10, 0, 30, 100},
                {10, 0, 50, 0, 0},
                {0, 50, 0, 20, 10},
                {30, 0, 20, 0, 60},
                {100, 0, 10, 60, 0}
        };

        SwingUtilities.invokeLater(() -> {
            // (true = directed, false = undirected)
            new GraphVisualizer(adjacencyMatrix2, false).setVisible(true);
        });
    }
}