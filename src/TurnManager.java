import java.util.*;
import java.util.List;

class TurnManager {
    private Queue<Player> turnQueue;
    private Player currentPlayer;

    public TurnManager() {
        this.turnQueue = new LinkedList<>();
    }

    public void addPlayer(Player player) {
        turnQueue.add(player);
    }

    public Player getCurrentPlayer() {
        if (currentPlayer == null && !turnQueue.isEmpty()) {
            currentPlayer = turnQueue.poll();
        }
        return currentPlayer;
    }

    public void nextTurn() {
        if (currentPlayer != null) {
            turnQueue.add(currentPlayer);
        }
        currentPlayer = turnQueue.poll();
    }

    public List<Player> getAllPlayers() {
        List<Player> allPlayers = new ArrayList<>();
        if (currentPlayer != null) {
            allPlayers.add(currentPlayer);
        }
        allPlayers.addAll(turnQueue);
        return allPlayers;
    }
}