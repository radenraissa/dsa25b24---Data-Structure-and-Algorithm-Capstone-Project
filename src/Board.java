import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

class Board {
    private BoardNode[] nodes;
    private static final int SIZE = 100;
    private List<Ladder> ladders;

    public Board() {
        nodes = new BoardNode[SIZE + 1];
        ladders = new ArrayList<>();
        createGraph();
        createLadders();
    }

    private void createGraph() {
        for (int i = 1; i <= SIZE; i++) {
            nodes[i] = new BoardNode(i);
        }

        for (int i = 1; i < SIZE; i++) {
            nodes[i].setNext(nodes[i + 1]);
            nodes[i + 1].setPrev(nodes[i]);
        }
    }

    private void createLadders() {
        // 5 tangga sesuai klarifikasi - CORRECTED
        ladders.add(new Ladder(16, 25));   // Tangga 1: 16 → 25
        ladders.add(new Ladder(34, 67));   // Tangga 2: 34 → 67
        ladders.add(new Ladder(39, 79));   // Tangga 3: 39 → 79
        ladders.add(new Ladder(57, 86));   // Tangga 4: 57 → 86
        ladders.add(new Ladder(72, 93));   // Tangga 5: 72 → 93
    }

    public BoardNode getNode(int position) {
        if (position >= 1 && position <= SIZE) {
            return nodes[position];
        }
        return null;
    }

    public int getSize() { return SIZE; }

    public List<Ladder> getLadders() { return ladders; }

    // Check if position has ladder
    public Ladder getLadderAt(int position) {
        for (Ladder ladder : ladders) {
            if (ladder.getBottom() == position) {
                return ladder;
            }
        }
        return null;
    }

    // Convert position to grid coordinates (row, col)
    public Point getGridPosition(int position) {
        if (position < 1 || position > 100) return new Point(0, 0);

        int adjustedPos = position - 1;
        int row = 9 - (adjustedPos / 10);
        int col = adjustedPos % 10;

        if ((9 - row) % 2 == 1) {
            col = 9 - col;
        }

        return new Point(col, row);
    }
}