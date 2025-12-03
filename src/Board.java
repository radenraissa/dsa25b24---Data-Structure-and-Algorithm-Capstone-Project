import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

class Board {
    private BoardNode[] nodes;
    private static final int SIZE = 100;
    private List<Ladder> ladders;
    private Map<Integer, Point> positionMap; // Peta posisi ke koordinat grid

    public Board() {
        nodes = new BoardNode[SIZE + 1];
        ladders = new ArrayList<>();
        positionMap = new HashMap<>();
        createGraph();
        createLadders();
        createPositionMap(); // Tambahkan peta posisi
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
        // Tambahkan posisi ladder sesuai gambar layout baru
        ladders.add(new Ladder(3, 22));
        ladders.add(new Ladder(5, 8));
        ladders.add(new Ladder(11, 26));
        ladders.add(new Ladder(20, 29));
        ladders.add(new Ladder(27, 56));
        // Tambahkan sesuai layout gambar Anda
    }

    private void createPositionMap() {
        // Sesuaikan posisi dan koordinatnya sesuai layout gambar
        // Format: posisi -> Point(x, y)
        positionMap.put(1, new Point(0, 9));
        positionMap.put(2, new Point(1, 9));
        positionMap.put(3, new Point(2, 9));
        positionMap.put(4, new Point(3, 9));
        positionMap.put(5, new Point(4, 9));
        positionMap.put(6, new Point(5, 9));
        positionMap.put(7, new Point(6, 9));
        positionMap.put(8, new Point(7, 9));
        positionMap.put(9, new Point(8, 9));
        positionMap.put(10, new Point(9, 9));
        // Baris atas
        positionMap.put(11, new Point(9, 8));
        positionMap.put(12, new Point(8, 8));
        positionMap.put(13, new Point(7, 8));
        positionMap.put(14, new Point(6, 8));
        positionMap.put(15, new Point(5, 8));
        positionMap.put(16, new Point(4, 8));
        positionMap.put(17, new Point(3, 8));
        positionMap.put(18, new Point(2, 8));
        positionMap.put(19, new Point(1, 8));
        positionMap.put(20, new Point(0, 8));
        // Baris kedua
        positionMap.put(21, new Point(0, 7));
        positionMap.put(22, new Point(1, 7));
        positionMap.put(23, new Point(2, 7));
        positionMap.put(24, new Point(3, 7));
        positionMap.put(25, new Point(4, 7));
        positionMap.put(26, new Point(5, 7));
        positionMap.put(27, new Point(6, 7));
        positionMap.put(28, new Point(7, 7));
        positionMap.put(29, new Point(8, 7));
        positionMap.put(30, new Point(9, 7));
        // dan seterusnya hingga posisi 100 sesuai layout gambar
        // Pastikan semua posisi dari 1 s/d 100 terisi sesuai layout
        // Anda bisa menambahkan semua posisi sesuai gambar
        positionMap.put(31, new Point(9, 6));
        positionMap.put(32, new Point(8, 6));
        positionMap.put(33, new Point(7, 6));
        positionMap.put(34, new Point(6, 6));
        positionMap.put(35, new Point(5, 6));
        positionMap.put(36, new Point(4, 6));
        positionMap.put(37, new Point(3, 6));
        positionMap.put(38, new Point(2, 6));
        positionMap.put(39, new Point(1, 6));
        positionMap.put(40, new Point(0, 6));
        // Tambahkan semua posisi hingga 100 sesuai layout
        // ...
        positionMap.put(100, new Point(9, 0));
    }

    public BoardNode getNode(int position) {
        if (position >= 1 && position <= SIZE) {
            return nodes[position];
        }
        return null;
    }

    public int getSize() {
        return SIZE;
    }

    public List<Ladder> getLadders() {
        return ladders;
    }

    public Ladder getLadderAt(int position) {
        for (Ladder ladder : ladders) {
            if (ladder.getBottom() == position) {
                return ladder;
            }
        }
        return null;
    }

    public Point getGridPosition(int position) {
        return positionMap.getOrDefault(position, new Point(0, 0));
    }
}