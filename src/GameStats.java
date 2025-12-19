import java.util.*;

public class GameStats {

    // Helper method to get formatted Top 3 String
    public static String getTop3Stats(List<Player> allPlayers) {
        if (allPlayers == null || allPlayers.isEmpty()) return "No data available.";

        PriorityQueue<Player> scoreQueue = new PriorityQueue<>((p1, p2) -> Integer.compare(p2.getScore(), p1.getScore()));
        PriorityQueue<Player> winQueue = new PriorityQueue<>((p1, p2) -> Integer.compare(p2.getWins(), p1.getWins()));

        // Add all players to both queues
        scoreQueue.addAll(allPlayers);
        winQueue.addAll(allPlayers);

        StringBuilder sb = new StringBuilder();
        sb.append("📊 GAME STATISTICS\n\n");

        sb.append("🏆 TOP 3 SCORES:\n");
        int count = 1;
        while (!scoreQueue.isEmpty() && count <= 3) {
            Player p = scoreQueue.poll();
            sb.append(String.format(" %d. %s : %d pts\n", count++, p.getName(), p.getScore()));
        }

        sb.append("\n");

        sb.append("👑 TOP 3 WINS:\n");
        count = 1;
        while (!winQueue.isEmpty() && count <= 3) {
            Player p = winQueue.poll();
            sb.append(String.format(" %d. %s : %d wins\n", count++, p.getName(), p.getWins()));
        }

        return sb.toString();
    }
}