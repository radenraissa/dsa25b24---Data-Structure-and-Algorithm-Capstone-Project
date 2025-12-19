import java.util.*;

public class DijkstraAlgorithm {
    private int[][] adjMatrix;
    private int size;

    public DijkstraAlgorithm(Board board) {
        this.size = board.getSize() + 1; // +1 untuk index 0
        this.adjMatrix = new int[size][size];

        // Initialize matrix dengan 0
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                adjMatrix[i][j] = 0;
            }
        }

        // Edge biasa (jalan 1 langkah normal)
        for (int i = 1; i < size - 1; i++) {
            adjMatrix[i][i+1] = 1;
        }

        // Edge tangga (shortcut dengan bobot 1)
        for (Ladder ladder : board.getLadders()) {
            int bottom = ladder.getBottom();
            int top = ladder.getTop();

            if (bottom < size && top < size) {
                // Tambah edge tangga
                adjMatrix[bottom][top] = 1;

                // Hapus jalan normal dari bottom (memaksa lewat tangga)
                if (bottom + 1 < size) {
                    adjMatrix[bottom][bottom + 1] = 0;
                }
            }
        }

        System.out.println("✓ Dijkstra graph initialized for " + (size - 1) + " nodes");
    }

    // Rekursif untuk trace path
    private void route(int n, int from, int to, int[] prev, ArrayList<Integer> pathResult){
        if (n == from) {
            pathResult.add(n);
        } else {
            if (prev[n] == -1) return;
            route(prev[n], from, to, prev, pathResult);
            pathResult.add(n);
        }
    }

    private int findTheNextNode(boolean[] isVisited, int[] dist){
        int min = Integer.MAX_VALUE;
        int minVertex = -1;

        for (int i = 1; i < this.size; i++) {
            if (!isVisited[i] && dist[i] < min) {
                min = dist[i];
                minVertex = i;
            }
        }

        return minVertex;
    }

    // Algoritma Dijkstra untuk mencari shortest path
    public ArrayList<Integer> getShortestPath(int orig, int dest) {
        int[] dist = new int[this.size];
        for(int i = 0; i < this.size; i++) {
            dist[i] = Integer.MAX_VALUE;
        }
        dist[orig] = 0;

        //Tracking
        boolean[] isVisited = new boolean[this.size];
        int[] prev = new int[this.size];
        Arrays.fill(prev, -1);

        //Dijkstra loop
        for(int i = 0; i < this.size; i++){
            int nextNode = findTheNextNode(isVisited, dist);

            if (nextNode == -1 || nextNode == dest) {
                break;
            }

            isVisited[nextNode] = true;

            for (int j = 1; j < this.size; j++) {
                if (!isVisited[j] &&
                        this.adjMatrix[nextNode][j] > 0 &&
                        dist[nextNode] != Integer.MAX_VALUE &&
                        (dist[nextNode] + this.adjMatrix[nextNode][j] < dist[j])) {

                    dist[j] = dist[nextNode] + this.adjMatrix[nextNode][j];
                    prev[j] = nextNode;
                }
            }
        }

        // Reconstruct path
        ArrayList<Integer> fullPath = new ArrayList<>();
        if (dist[dest] != Integer.MAX_VALUE) {
            route(dest, orig, dest, prev, fullPath);
        }

        return fullPath;
    }
}