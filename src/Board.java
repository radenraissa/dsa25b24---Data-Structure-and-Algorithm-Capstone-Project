import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

class Board {
    private BoardNode[] nodes;
    private static final int SIZE = 60; // Dikurangi jadi 60 kotak
    private List<Ladder> ladders;
    private List<Point> snakePath; // Path ular meliuk

    public Board() {
        nodes = new BoardNode[SIZE + 1];
        ladders = new ArrayList<>();
        snakePath = new ArrayList<>();
        createGraph();
        createSnakePath(); // Buat jalur ular
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

    // Membuat jalur ular yang meliuk-liuk
    private void createSnakePath() {
        // Jalur ular berbentuk S meliuk dari kiri atas ke kanan bawah
        // Menggunakan kurva Bezier untuk efek smooth

        int canvasWidth = 600;
        int canvasHeight = 600;

        // Control points untuk membuat kurva ular yang meliuk
        Point[] controlPoints = {
                new Point(50, 50),    // Start (kiri atas)
                new Point(150, 100),
                new Point(250, 80),
                new Point(350, 150),
                new Point(450, 120),
                new Point(520, 180),
                new Point(480, 250),
                new Point(380, 280),
                new Point(280, 320),
                new Point(200, 380),
                new Point(120, 420),
                new Point(180, 480),
                new Point(280, 510),
                new Point(380, 500),
                new Point(480, 520),
                new Point(550, 550)   // Finish (kanan bawah)
        };

        // Generate smooth path menggunakan interpolasi
        for (int i = 0; i < controlPoints.length - 1; i++) {
            Point p1 = controlPoints[i];
            Point p2 = controlPoints[i + 1];

            int steps = SIZE / (controlPoints.length - 1);
            for (int j = 0; j < steps; j++) {
                float t = (float) j / steps;
                int x = (int) (p1.x + t * (p2.x - p1.x));
                int y = (int) (p1.y + t * (p2.y - p1.y));

                // Tambahkan sedikit variasi untuk efek meliuk
                double wave = Math.sin(snakePath.size() * 0.3) * 15;
                x += wave;

                snakePath.add(new Point(x, y));
            }
        }

        // Pastikan ada tepat SIZE posisi
        while (snakePath.size() < SIZE) {
            Point last = snakePath.get(snakePath.size() - 1);
            snakePath.add(new Point(last.x + 5, last.y + 5));
        }
        while (snakePath.size() > SIZE) {
            snakePath.remove(snakePath.size() - 1);
        }
    }

    private void createLadders() {
        // Arrow hijau shortcuts di jalur ular
        ladders.add(new Ladder(8, 15));
        ladders.add(new Ladder(18, 28));
        ladders.add(new Ladder(25, 38));
        ladders.add(new Ladder(35, 48));
        ladders.add(new Ladder(42, 55));
    }

    public BoardNode getNode(int position) {
        if (position >= 1 && position <= SIZE) {
            return nodes[position];
        }
        return null;
    }

    public int getSize() { return SIZE; }
    public List<Ladder> getLadders() { return ladders; }

    public Ladder getLadderAt(int position) {
        for (Ladder ladder : ladders) {
            if (ladder.getBottom() == position) {
                return ladder;
            }
        }
        return null;
    }

    // Get posisi dalam path ular
    public Point getSnakePosition(int position) {
        if (position < 1 || position > SIZE) return new Point(0, 0);
        return snakePath.get(position - 1);
    }

    public List<Point> getSnakePath() {
        return snakePath;
    }
}