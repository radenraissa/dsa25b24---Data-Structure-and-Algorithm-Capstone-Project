import java.awt.*;
import java.util.Stack;

class Player {
    private String name;
    private int position;
    private Color color;

    // NEW: Score variable
    private int score;

    private Stack<Integer> history;

    public Player(String name, Color color) {
        this.name = name;
        this.position = 1;
        this.color = color;
        this.score = 0; // Start with 0

        this.history = new Stack<>();
        this.history.push(1);
    }

    public String getName() { return name; }
    public int getPosition() { return position; }
    public Color getColor() { return color; }

    // NEW: Getter and Adder for Score
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
}