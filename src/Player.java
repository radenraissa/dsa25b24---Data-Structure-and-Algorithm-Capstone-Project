import java.awt.*;
import java.util.Stack;

class Player {

    private String name;
    private int position;
    private Color color;
    private int score;
    private int wins;

    //  history langkah (dipakai undo & animasi)
    private Stack<Integer> history;


    public Player(String name, Color color) {
        this(name, color, 1);
    }

    public Player(String name, Color color, int startPosition) {
        this.name = name;
        this.color = color;
        this.position = clamp(startPosition);
        this.score = 0;
        this.wins = 0;

        history = new Stack<>();
        history.push(this.position);
    }

    public String getName() { return name; }
    public int getPosition() { return position; }
    public Color getColor() { return color; }
    public int getScore() { return score; }
    public int getWins() { return wins; }


    public void setPosition(int pos) {
        this.position = clamp(pos);
    }

    public void recordStep(int pos) {
        this.position = clamp(pos);
        history.push(this.position);
    }

    public int undoStep() {
        if (history.size() > 1) {
            history.pop();
            position = history.peek();
            return position;
        }
        return position;
    }

    public void reset() {
        position = 1;
        history.clear();
        history.push(1);
    }

    public void addScore(int s) { score += s; }
    public void addWin() { wins++; }


    private int clamp(int pos) {
        if (pos < 1) return 1;
        if (pos > 74) return 74;
        return pos;
    }

    @Override
    public String toString() {
        return name + " | Pos: " + position + " | Wins: " + wins;
    }
}