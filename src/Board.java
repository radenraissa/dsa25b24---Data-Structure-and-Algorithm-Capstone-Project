import java.util.ArrayList;
import java.util.List;
import java.awt.Point;

class Board {
    private BoardNode[] nodes;

    // PERUBAHAN UTAMA: UBAH SIZE JADI 81
    private static final int SIZE = 74;

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
        // Daftar Tangga (Sesuaikan dengan gambar board Anda jika perlu)
        // Pastikan angka 'Ke' (Top) tidak lebih dari 81
        ladders.add(new Ladder(15, 18));
        ladders.add(new Ladder(27, 31));
        ladders.add(new Ladder(40, 47));
        ladders.add(new Ladder(51, 54));
        ladders.add(new Ladder(57, 60));   // Adjusted agar tidak lewat 81
        ladders.add(new Ladder(64, 66));   // Adjusted agar tidak lewat 81
    }

    public BoardNode getNode(int position) {
        if (position >= 1 && position <= SIZE) {
            return nodes[position];
        }
        return null;
    }

    public int getSize() { return SIZE; }

    public List<Ladder> getLadders() { return ladders; }

    // Check if position has ladder (Bottom logic)
    public Ladder getLadderAt(int position) {
        for (Ladder ladder : ladders) {
            if (ladder.getBottom() == position) {
                return ladder;
            }
        }
        return null;
    }

    // Check if position is top of ladder (Reverse logic)
    public Ladder getLadderWithTopAt(int position) {
        for (Ladder ladder : ladders) {
            if (ladder.getTop() == position) {
                return ladder;
            }
        }
        return null;
    }

    // Helper untuk grid logic (jika masih dipakai di tempat lain)
    public Point getGridPosition(int position) {
        if (position < 1 || position > SIZE) return new Point(0, 0);
        return new Point(0, 0); // Dummy, karena sekarang pakai Mapping Manual di BoardPanel
    }
}