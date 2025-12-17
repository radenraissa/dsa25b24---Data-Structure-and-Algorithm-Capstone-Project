//import java.awt.*;
//import java.util.Stack;
//
//class Player {
//    private String name;
//    private int position;
//    private Color color;
//    private int score;
//    private int wins;
//    private Stack<Integer> history;
//
//    public Player(String name, Color color) {
//        this.name = name;
//        this.position = 1;
//        this.color = color;
//        this.score = 0;
//        this.wins = 0;
//        this.history = new Stack<>();
//        this.history.push(1);
//    }
//
//    public String getName() { return name; }
//    public int getPosition() { return position; }
//    public Color getColor() { return color; }
//
//    public int getScore() { return score; }
//    public void addScore(int points) { this.score += points; }
//
//    public int getWins() { return wins; }
//    public void addWin() { this.wins++; }
//
//    public void setPosition(int position) { this.position = position; }
//
//    public void recordStep(int pos) { history.push(pos); }
//
//    public int undoStep() {
//        if (history.size() > 1) {
//            history.pop();
//            return history.peek();
//        }
//        return 1;
//    }
//
//    public int peekHistory() {
//        return history.isEmpty() ? 1 : history.peek();
//    }
//
//    public void reset() {
//        this.position = 1;
//        this.history.clear();
//        this.history.push(1);
//    }
//
//    @Override
//    public String toString() {
//        return name + " (Score: " + score + " | Wins: " + wins + ")";
//    }
//}

import java.awt.*;
import java.util.Stack;

class Player {

    private String name;
    private int position;
    private Color color;
    private int score;
    private int wins;

    // 🔹 history langkah (dipakai undo & animasi)
    private Stack<Integer> history;

    // ================= CONSTRUCTOR =================

    // Dipakai jika hanya name & color
    public Player(String name, Color color) {
        this(name, color, 1);
    }

    // Dipakai GameGUI (name, color, startPosition)
    public Player(String name, Color color, int startPosition) {
        this.name = name;
        this.color = color;
        this.position = clamp(startPosition);
        this.score = 0;
        this.wins = 0;

        history = new Stack<>();
        history.push(this.position);
    }

    // ================= GETTER =================

    public String getName() { return name; }
    public int getPosition() { return position; }
    public Color getColor() { return color; }
    public int getScore() { return score; }
    public int getWins() { return wins; }

    // ================= GAME LOGIC =================

    public void setPosition(int pos) {
        this.position = clamp(pos);
        history.push(this.position);
    }

    // ✅ INI YANG HILANG & MENYEBABKAN ERROR
    public void recordStep(int pos) {
        history.push(clamp(pos));
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

    // ================= UTIL =================

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
