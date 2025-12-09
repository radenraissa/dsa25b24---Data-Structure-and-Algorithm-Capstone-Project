import java.awt.*;
import java.util.Stack;

class Player {
    private String name;
    private int position;
    private Color color;
    private int score; // Score persists
    private Stack<Integer> history;

    public Player(String name, Color color) {
        this.name = name;
        this.position = 1;
        this.color = color;
        this.score = 0;
        this.history = new Stack<>();
        this.history.push(1);
    }

    public String getName() { return name; }
    public int getPosition() { return position; }
    public Color getColor() { return color; }

    public int getScore() { return score; }

    public void addScore(int points) {
        this.score += points;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void recordStep(int pos) {
        history.push(pos);
    }

    public int undoStep() {
        if (history.size() > 1) {
            history.pop();
            return history.peek();
        }
        return 1;
    }

    public int peekHistory() {
        return history.isEmpty() ? 1 : history.peek();
    }

    // --- NEW RESET METHOD ---
    public void reset() {
        this.position = 1;       // Back to Start
        this.history.clear();    // Wipe movement history
        this.history.push(1);    // Initialize history at Start
    }
}