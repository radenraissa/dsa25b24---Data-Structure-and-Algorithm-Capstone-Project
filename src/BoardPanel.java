import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

class BoardPanel extends JPanel {
    private Board board;
    private List<Player> players;
    private static final int CELL_SIZE = 60;
    private static final int BOARD_SIZE = 10;

    public BoardPanel(Board board) {
        this.board = board;
        this.players = new ArrayList<>();
        setPreferredSize(new Dimension(CELL_SIZE * BOARD_SIZE, CELL_SIZE * BOARD_SIZE));
        setBackground(new Color(88, 141, 60));
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawBoard(g2d);
        drawLadders(g2d);
        drawPlayers(g2d);
    }

    private void drawBoard(Graphics2D g2d) {
        // Gambar grid sesuai posisi dari peta layout
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                int x = col * CELL_SIZE;
                int y = row * CELL_SIZE;

                // Warna checkerboard
                if ((row + col) % 2 == 0) {
                    g2d.setColor(new Color(170, 215, 81));
                } else {
                    g2d.setColor(new Color(120, 170, 50));
                }

                g2d.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                g2d.setColor(new Color(88, 141, 60));
                g2d.drawRect(x, y, CELL_SIZE, CELL_SIZE);

                int position = getPositionFromGrid(row, col);

                // Tampilkan posisi
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 14));
                String posStr = String.valueOf(position);
                g2d.drawString(posStr, x + 5, y + 15);

                // Highlight start dan finish
                if (position == 1) {
                    g2d.setColor(new Color(46, 204, 113, 100));
                    g2d.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                    g2d.setColor(Color.WHITE);
                    g2d.setFont(new Font("Arial", Font.BOLD, 12));
                    g2d.drawString("START", x + 8, y + 40);
                } else if (position == 100) {
                    g2d.setColor(new Color(241, 196, 15, 100));
                    g2d.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                    g2d.setColor(Color.WHITE);
                    g2d.setFont(new Font("Arial", Font.BOLD, 12));
                    g2d.drawString("FINISH", x + 8, y + 40);
                }
            }
        }
    }

    private int getPositionFromGrid(int row, int col) {
        // Hitung posisi berdasarkan layout khusus
        int rowFromBottom = 9 - row;
        int position;

        // Jika baris genap dari bawah (0,2,4,...), posisi berjalan dari kiri ke kanan
        if (rowFromBottom % 2 == 0) {
            position = rowFromBottom * 10 + col + 1;
        } else {
            // Jika ganjil dari bawah, posisi berjalan dari kanan ke kiri
            position = rowFromBottom * 10 + (10 - col);
        }
        return position;
    }

    private void drawLadders(Graphics2D g2d) {
        for (Ladder ladder : board.getLadders()) {
            Point bottomPos = board.getGridPosition(ladder.getBottom());
            Point topPos = board.getGridPosition(ladder.getTop());

            int x1 = bottomPos.x * CELL_SIZE + CELL_SIZE / 2;
            int y1 = bottomPos.y * CELL_SIZE + CELL_SIZE / 2;
            int x2 = topPos.x * CELL_SIZE + CELL_SIZE / 2;
            int y2 = topPos.y * CELL_SIZE + CELL_SIZE / 2;

            g2d.setColor(new Color(205, 133, 63));
            g2d.setStroke(new BasicStroke(6));
            g2d.drawLine(x1, y1, x2, y2);

            // Rungs
            double distance = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
            int numRungs = (int)(distance / 15);
            g2d.setStroke(new BasicStroke(4));
            for (int i = 0; i <= numRungs; i++) {
                double t = (double) i / numRungs;
                int rx = (int)(x1 + t * (x2 - x1));
                int ry = (int)(y1 + t * (y2 - y1));
                g2d.drawLine(rx - 8, ry, rx + 8, ry);
            }
        }
    }

    private void drawPlayers(Graphics2D g2d) {
        Map<Integer, List<Player>> playersByPos = new HashMap<>();
        for (Player p : players) {
            playersByPos.putIfAbsent(p.getPosition(), new ArrayList<>());
            playersByPos.get(p.getPosition()).add(p);
        }

        for (Map.Entry<Integer, List<Player>> entry : playersByPos.entrySet()) {
            int pos = entry.getKey();
            List<Player> plist = entry.getValue();
            Point gridPos = board.getGridPosition(pos);
            int baseX = gridPos.x * CELL_SIZE + CELL_SIZE / 2;
            int baseY = gridPos.y * CELL_SIZE + CELL_SIZE / 2;

            int count = plist.size();
            for (int i = 0; i < count; i++) {
                Player p = plist.get(i);
                int offsetX = 0, offsetY = 0;
                if (count > 1) {
                    double angle = 2 * Math.PI * i / count;
                    offsetX = (int)(12 * Math.cos(angle));
                    offsetY = (int)(12 * Math.sin(angle));
                }
                int px = baseX + offsetX;
                int py = baseY + offsetY;

                g2d.setColor(p.getColor());
                g2d.fillOval(px - 12, py - 12, 24, 24);
                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawOval(px - 12, py - 12, 24, 24);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 12));
                String initial = p.getName().substring(0, 1).toUpperCase();
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(initial);
                g2d.drawString(initial, px - textWidth / 2, py + 4);
            }
        }
    }
}