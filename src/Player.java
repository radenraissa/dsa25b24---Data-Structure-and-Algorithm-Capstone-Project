import java.awt.*;
import java.util.Stack;

class Player {
    private String name;
    private int position;
    private Color color;
    private int score;
    private int wins; // NEW: Track Wins
    private Stack<Integer> history;

    public Player(String name, Color color) {
        this.name = name;
        this.position = 1;
        this.color = color;
        this.score = 0;
        this.wins = 0; // Initialize wins
        this.history = new Stack<>();
        this.history.push(1);
    }

    public String getName() { return name; }
    public int getPosition() { return position; }
    public Color getColor() { return color; }

    public int getScore() { return score; }
    public void addScore(int points) { this.score += points; }

    // NEW: Win getters and setters
    public int getWins() { return wins; }
    public void addWin() { this.wins++; }

    public void setPosition(int position) { this.position = position; }

    public void recordStep(int pos) { history.push(pos); }

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

    public void reset() {
        this.position = 1;
        this.history.clear();
        this.history.push(1);
    }

    // toString for debugging/display
    @Override
    public String toString() {
        return name + " (Score: " + score + " | Wins: " + wins + ")";
    }
}