import java.util.*;

class MovementManager {
    private Stack<Integer> movementStack;

    public MovementManager() {
        this.movementStack = new Stack<>();
    }

    // --- BARU: Dipakai untuk Dijkstra ---
    public void setPath(ArrayList<Integer> path) {
        movementStack.clear();
        // Masukkan ke stack secara terbalik (kecuali posisi awal index 0)
        for (int i = path.size() - 1; i > 0; i--) {
            movementStack.push(path.get(i));
        }
    }

    public Integer popNextPosition() {
        if (!movementStack.isEmpty()) return movementStack.pop();
        return null;
    }

    public boolean hasMovement() {
        return !movementStack.isEmpty();
    }
}