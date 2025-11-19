package main;
import java.awt.*;
import java.util.*;

class Graph {
    private ArrayList<Node> nodes;
    private ArrayList<Edge> edges;
    private boolean directed;

    private static final double CENTER_X = 400;
    private static final double CENTER_Y = 300;
    private static final double LAYOUT_RADIUS = 200;

    public Graph(int[][] adjacencyMatrix, boolean directed) {
        this.directed = directed;
        this.nodes = new ArrayList<>();
        this.edges = new ArrayList<>();
        buildGraph(adjacencyMatrix);
    }

    private void buildGraph(int[][] matrix) {
        int n = matrix.length;

        // Create nodes in circular layout
        for (int i = 0; i < n; i++) {
            double angle = 2 * Math.PI * i / n - Math.PI / 2;
            double x = CENTER_X + LAYOUT_RADIUS * Math.cos(angle);
            double y = CENTER_Y + LAYOUT_RADIUS * Math.sin(angle);
            nodes.add(new Node(i, x, y));
        }

        // Create edges from adjacency matrix
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (matrix[i][j] > 0) {
                    // Skip if undirected and already added the reverse edge
                    if (!directed && i > j) continue;
                    edges.add(new Edge(nodes.get(i), nodes.get(j),
                            matrix[i][j], directed));
                }
            }
        }
    }

    public Node findNodeAt(int x, int y) {
        for (Node node : nodes) {
            if (node.contains(x, y)) {
                return node;
            }
        }
        return null;
    }

    public void draw(Graphics2D g2d) {
        // Draw edges first (so they appear behind nodes)
        for (Edge edge : edges) {
            edge.draw(g2d);
        }

        // Draw nodes on top
        for (Node node : nodes) {
            node.draw(g2d);
        }
    }
}
