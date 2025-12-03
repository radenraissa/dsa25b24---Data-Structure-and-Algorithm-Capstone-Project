import java.util.*;

public class DijkstraAlgorithm {
    private int[][] adjMatrix;
    private int size;

    // HAPUS static final BOARD_SIZE yang lama

    public DijkstraAlgorithm(Board board) {
        // PERUBAHAN: Ambil ukuran dinamis dari Board
        this.size = board.getSize() + 1;
        this.adjMatrix = new int[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                adjMatrix[i][j] = 0;
            }
        }

        // 2. Edge Biasa (Jalan 1 langkah)
        for (int i = 1; i < size - 1; i++) {
            adjMatrix[i][i+1] = 1;
        }

        // 3. Edge Tangga (Jalan pintas, bobot 1)
        for (Ladder l : board.getLadders()) {
            if (l.getBottom() < size && l.getTop() < size) {
                adjMatrix[l.getBottom()][l.getTop()] = 1;
                // Hapus jalan biasa jika ada tangga (memaksa lewat tangga)
                if (l.getBottom() + 1 < size) {
                    adjMatrix[l.getBottom()][l.getBottom() + 1] = 0;
                }
            }
        }
    }

    // --- ALGORITMA MANUAL (Tidak Berubah) ---

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

    public ArrayList<Integer> getShortestPath(int orig, int dest) {
        int[] dist = new int[this.size];
        for(int i=0; i<this.size; i++) dist[i] = Integer.MAX_VALUE;
        dist[orig] = 0;

        boolean[] isVisited = new boolean[this.size];
        int[] prev = new int[this.size];
        Arrays.fill(prev, -1);

        for(int i = 0; i < this.size; i++){
            int nextNode = findTheNextNode(isVisited, dist);
            if (nextNode == -1 || nextNode == dest) break;
            isVisited[nextNode] = true;

            for (int j = 1; j < this.size; j++) {
                if (!isVisited[j] && this.adjMatrix[nextNode][j] > 0 &&
                        dist[nextNode] != Integer.MAX_VALUE &&
                        (dist[nextNode] + this.adjMatrix[nextNode][j] < dist[j])) {
                    dist[j] = dist[nextNode] + this.adjMatrix[nextNode][j];
                    prev[j] = nextNode;
                }
            }
        }

        ArrayList<Integer> fullPath = new ArrayList<>();
        if (dist[dest] != Integer.MAX_VALUE) {
            route(dest, orig, dest, prev, fullPath);
        }
        return fullPath;
    }
}