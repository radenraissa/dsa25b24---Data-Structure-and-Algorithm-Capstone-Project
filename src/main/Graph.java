package main;
import java.awt.*;
import java.util.*;

class Graph {
    private ArrayList<Node> nodes;
    private ArrayList<Edge> edges;
    private boolean directed;
    private int[][] adjMatrix;
    private int size;

    private static final double CENTER_X = 400;
    private static final double CENTER_Y = 300;
    private static final double LAYOUT_RADIUS = 220;

    private static final String[] INDONESIAN_CITIES = {
            "Jakarta", "Surabaya", "Bandung", "Medan", "Semarang",
            "Makassar", "Palembang", "Tangerang", "Depok", "Bekasi",
            "Yogyakarta", "Malang", "Bogor", "Batam", "Pekanbaru",
            "Bandar Lampung", "Padang", "Denpasar", "Samarinda", "Manado"
    };

    public Graph(int[][] adjacencyMatrix, boolean directed) {
        this.directed = directed;
        this.nodes = new ArrayList<>();
        this.edges = new ArrayList<>();

        this.adjMatrix = adjacencyMatrix;
        this.size = adjacencyMatrix.length;

        buildGraph(adjacencyMatrix);
    }

    private void buildGraph(int[][] matrix) {
        int n = matrix.length;
        for (int i = 0; i < n; i++) {
            double angle = 2 * Math.PI * i / n - Math.PI / 2;
            double x = CENTER_X + LAYOUT_RADIUS * Math.cos(angle);
            double y = CENTER_Y + LAYOUT_RADIUS * Math.sin(angle);
            String cityName = INDONESIAN_CITIES[i % INDONESIAN_CITIES.length];
            nodes.add(new Node(i, cityName, x, y));
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (matrix[i][j] > 0) {
                    if (!directed && i > j) continue;
                    edges.add(new Edge(nodes.get(i), nodes.get(j), matrix[i][j], directed));
                }
            }
        }
    }

    public Node findNodeAt(int x, int y) {
        for (Node node : nodes) {
            if (node.contains(x, y)) return node;
        }
        return null;
    }

    public void draw(Graphics2D g2d) {
        for (Edge edge : edges) edge.draw(g2d);
        for (Node node : nodes) node.draw(g2d);
    }

    public int getNodeCount() {
        return nodes.size();
    }
    public String getNodeName(int index) {
        return nodes.get(index).getCityName();
    }

    public void resetHighlight() {
        for (Node n : nodes) n.setHighlighted(false);
        for (Edge e : edges) e.setHighlighted(false);
    }

    // Helper: Rekursif route (Dimodifikasi untuk menyimpan ke List)
    private void route(int n, int from, int to, int[] prev, ArrayList<Integer> pathResult){
        if (n == from) {
            // Base case: sudah sampai di node awal
            pathResult.add(n);
        } else {
            if (prev[n] == -1) return; //check jika tidak ada jalur
            route(prev[n], from, to, prev, pathResult);
            pathResult.add(n);
        }
    }

    private int findTheNextNode(boolean[] isVisited, int[] dist){
        int min = Integer.MAX_VALUE;
        int minVertex = -1;
        for (int i = 0; i < this.size; i++) {
            if (!isVisited[i] && dist[i] < min) {
                min = dist[i];
                minVertex = i;
            }
        }
        return minVertex;
    }

    // Main Dijkstra Algorithm
    public void runDijkstra(int orig, int dest) {
        resetHighlight(); // Bersihkan visualisasi lama

        int[] dist = new int[this.size];

        for(int i = 0; i < this.size; i++){
            dist[i] = Integer.MAX_VALUE;
        }

        dist[orig] = 0;

        boolean[] isVisited = new boolean[this.size];
        int[] prev = new int[this.size];
        // Inisialisasi prev dengan -1 untuk safety
        Arrays.fill(prev, -1);

        for(int i = 0; i < this.size; i++){
            int nextNode = findTheNextNode(isVisited, dist);

            // Safety break jika graph terputus
            if (nextNode == -1) break;

            isVisited[nextNode] = true;

            for (int j = 0; j < this.size; j++) {
                // Adaptasi: adjMatrix adalah 'this.g'
                if (!isVisited[j] && this.adjMatrix[nextNode][j] > 0 && dist[nextNode] != Integer.MAX_VALUE && (dist[nextNode] + this.adjMatrix[nextNode][j] < dist[j])) {
                    dist[j] = dist[nextNode] + this.adjMatrix[nextNode][j];
                    prev[j] = nextNode;
                }
            }
        }

        // Output ke Console
        if (dist[dest] == Integer.MAX_VALUE) {
            System.out.println("No path found form " + nodes.get(orig).getCityName() + " to " + nodes.get(dest).getCityName());
        } else {
            System.out.println("The minimum distance from " + nodes.get(orig).getCityName() + " to " + nodes.get(dest).getCityName() + " is " + dist[dest]);
            System.out.println("Routing:");

            // jalur untuk visualize
            ArrayList<Integer> pathIds = new ArrayList<>();
            route(dest, orig, dest, prev, pathIds);

            //print ke console
            for(int i=0; i<pathIds.size(); i++) {
                System.out.print(nodes.get(pathIds.get(i)).getCityName());
                if(i < pathIds.size()-1) System.out.print(" --> ");
            }
            System.out.println();

            highlightPath(pathIds);
        }
    }

    // Helper untuk menyalakan warna hijau pada jalur
    private void highlightPath(ArrayList<Integer> pathIds) {

        for (int id : pathIds) {
            nodes.get(id).setHighlighted(true);
        }


        for (int i = 0; i < pathIds.size() - 1; i++) {
            int u = pathIds.get(i);
            int v = pathIds.get(i + 1);

            // Cari edge yang menghubungkan u -> v
            for (Edge e : edges) {
                if (e.getSource().getId() == u && e.getTarget().getId() == v) {
                    e.setHighlighted(true);
                } else if (!directed && e.getSource().getId() == v && e.getTarget().getId() == u) {
                    e.setHighlighted(true);
                }
            }
        }
    }
}