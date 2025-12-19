import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Board {
    private BoardNode[] nodes;
    private static final int SIZE = 74;
    private List<Ladder> ladders;

    // NEW: Map to store Special Nodes (Position -> Score)
    private Map<Integer, Integer> specialNodes;

    public Board() {
        nodes = new BoardNode[SIZE + 1];
        ladders = new ArrayList<>();
        specialNodes = new HashMap<>(); // Initialize

        createGraph();
        createLadders();
        initSpecialNodes(); // Call setup
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
        // Ladders from previous setup
        ladders.add(new Ladder(15, 18));
        ladders.add(new Ladder(27, 31));
        ladders.add(new Ladder(40, 47));
        ladders.add(new Ladder(51, 54));
        ladders.add(new Ladder(57, 60));
        ladders.add(new Ladder(64, 66));
    }

    // NEW: Setup scores based on your request
    private void initSpecialNodes() {
        // +10 Score
        specialNodes.put(11, 10);
        specialNodes.put(22, 10);
        specialNodes.put(33, 10);
        specialNodes.put(59, 10);

        // +5 Score
        specialNodes.put(30, 5);
        specialNodes.put(42, 5);
        specialNodes.put(50, 5);
        specialNodes.put(63, 5);

        // -5 Score
        specialNodes.put(17, -5);

        // -10 Score
        specialNodes.put(38, -10);
    }

    // NEW: Helper to get score at specific position
    public int getScoreEffect(int position) {
        return specialNodes.getOrDefault(position, 0);
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

    public Ladder getLadderWithTopAt(int position) {
        for (Ladder ladder : ladders) {
            if (ladder.getTop() == position) {
                return ladder;
            }
        }
        return null;
    }
}