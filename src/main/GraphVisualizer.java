package main;

import javax.swing.*;
import java.awt.*;

public class GraphVisualizer extends JFrame {
    private GraphPanel graphPanel;

    public GraphVisualizer(int[][] adjacencyMatrix, boolean directed) {
        setTitle("Graph Visualizer - " + (directed ? "Directed" : "Undirected") + " | Drag nodes to move them!");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Graph graph = new Graph(adjacencyMatrix, directed);
        graphPanel = new GraphPanel(graph);

        add(graphPanel, BorderLayout.CENTER);

        JLabel info = new JLabel("  Graph with " + adjacencyMatrix.length +
                " nodes | " + (directed ? "Directed" : "Undirected") + " | Click and drag nodes to move them");
        info.setFont(new Font("Arial", Font.PLAIN, 12));
        add(info, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        // Example 1: Undirected weighted graph
        int[][] adjacencyMatrix1 = {
                {0, 4, 2, 0, 0},
                {4, 0, 1, 5, 3},
                {2, 1, 0, 8, 0},
                {0, 5, 8, 0, 6},
                {0, 3, 0, 6, 0}
        };

        // Example 2: Directed weighted graph
        int[][] adjacencyMatrix2 = {
                {0, 2, 0, 3},
                {0, 0, 4, 0},
                {1, 0, 0, 2},
                {0, 5, 0, 0}
        };

        SwingUtilities.invokeLater(() -> {
//            new GraphVisualizer(adjacencyMatrix1, false).setVisible(true);
            new GraphVisualizer(adjacencyMatrix2, true).setVisible(true);
        });
    }
}