import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

class BoardPanel extends JPanel {
    private Board board;
    private List<Player> players;

    public BoardPanel(Board board) {
        this.board = board;
        this.players = new ArrayList<>();
        setPreferredSize(new Dimension(600, 600));
        setBackground(new Color(240, 248, 255)); // Alice Blue background
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

        drawSnakePath(g2d);
        drawPositionCircles(g2d);
        drawLadders(g2d);
        drawPlayers(g2d);
    }

    // Menggambar jalur ular sebagai garis tebal berkelok-kelok
    private void drawSnakePath(Graphics2D g2d) {
        List<Point> path = board.getSnakePath();

        // Gambar shadow untuk efek 3D
        g2d.setColor(new Color(0, 0, 0, 30));
        g2d.setStroke(new BasicStroke(28));
        for (int i = 0; i < path.size() - 1; i++) {
            Point p1 = path.get(i);
            Point p2 = path.get(i + 1);
            g2d.drawLine(p1.x + 3, p1.y + 3, p2.x + 3, p2.y + 3);
        }

        // Gambar jalur ular dengan gradient
        for (int i = 0; i < path.size() - 1; i++) {
            Point p1 = path.get(i);
            Point p2 = path.get(i + 1);

            // Gradient dari pink ke biru
            float ratio = (float) i / path.size();
            Color color1 = new Color(255, 182, 193); // Pink
            Color color2 = new Color(173, 216, 230); // Light blue

            int r = (int) (color1.getRed() + ratio * (color2.getRed() - color1.getRed()));
            int gb = (int) (color1.getGreen() + ratio * (color2.getGreen() - color1.getGreen()));
            int b = (int) (color1.getBlue() + ratio * (color2.getBlue() - color1.getBlue()));

            g2d.setColor(new Color(r, gb, b));
            g2d.setStroke(new BasicStroke(25));
            g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
    }

    // Menggambar lingkaran di setiap posisi dengan nomor
    private void drawPositionCircles(Graphics2D g2d) {
        List<Point> path = board.getSnakePath();

        for (int i = 0; i < path.size(); i++) {
            Point p = path.get(i);
            int position = i + 1;

            // Warna berbeda untuk START dan FINISH
            if (position == 1) {
                // START - kuning dengan bintang
                g2d.setColor(new Color(255, 223, 0));
                g2d.fillOval(p.x - 20, p.y - 20, 40, 40);
                g2d.setColor(new Color(255, 165, 0));
                g2d.setStroke(new BasicStroke(3));
                g2d.drawOval(p.x - 20, p.y - 20, 40, 40);

                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.BOLD, 10));
                g2d.drawString("START", p.x - 18, p.y + 5);
            } else if (position == board.getSize()) {
                // FINISH - hijau dengan mahkota
                g2d.setColor(new Color(46, 204, 113));
                g2d.fillOval(p.x - 20, p.y - 20, 40, 40);
                g2d.setColor(new Color(39, 174, 96));
                g2d.setStroke(new BasicStroke(3));
                g2d.drawOval(p.x - 20, p.y - 20, 40, 40);

                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 9));
                g2d.drawString("FINISH", p.x - 18, p.y + 5);
            } else {
                // Posisi biasa - putih dengan nomor
                g2d.setColor(Color.WHITE);
                g2d.fillOval(p.x - 12, p.y - 12, 24, 24);
                g2d.setColor(new Color(100, 100, 100));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawOval(p.x - 12, p.y - 12, 24, 24);

                // Nomor posisi
                g2d.setColor(new Color(50, 50, 50));
                g2d.setFont(new Font("Arial", Font.BOLD, 10));
                String numStr = String.valueOf(position);
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(numStr);
                g2d.drawString(numStr, p.x - textWidth / 2, p.y + 4);
            }
        }
    }

    // Menggambar arrow hijau shortcuts
    private void drawLadders(Graphics2D g2d) {
        for (Ladder ladder : board.getLadders()) {
            Point p1 = board.getSnakePosition(ladder.getBottom());
            Point p2 = board.getSnakePosition(ladder.getTop());

            // Shadow
            g2d.setColor(new Color(0, 0, 0, 50));
            g2d.setStroke(new BasicStroke(10));
            g2d.drawLine(p1.x + 2, p1.y + 2, p2.x + 2, p2.y + 2);

            // Arrow hijau
            g2d.setColor(new Color(46, 204, 113));
            g2d.setStroke(new BasicStroke(8));
            g2d.drawLine(p1.x, p1.y, p2.x, p2.y);

            // Kepala panah
            drawArrowHead(g2d, p1.x, p1.y, p2.x, p2.y);
        }
    }

    private void drawArrowHead(Graphics2D g2d, int x1, int y1, int x2, int y2) {
        double angle = Math.atan2(y2 - y1, x2 - x1);
        int arrowLength = 18;

        int[] xPoints = new int[3];
        int[] yPoints = new int[3];

        xPoints[0] = x2;
        yPoints[0] = y2;

        xPoints[1] = (int) (x2 - arrowLength * Math.cos(angle - Math.PI / 6));
        yPoints[1] = (int) (y2 - arrowLength * Math.sin(angle - Math.PI / 6));

        xPoints[2] = (int) (x2 - arrowLength * Math.cos(angle + Math.PI / 6));
        yPoints[2] = (int) (y2 - arrowLength * Math.sin(angle + Math.PI / 6));

        g2d.fillPolygon(xPoints, yPoints, 3);
    }

    // Menggambar pion pemain
    private void drawPlayers(Graphics2D g2d) {
        Map<Integer, List<Player>> playersByPosition = new HashMap<>();

        for (Player player : players) {
            int pos = player.getPosition();
            playersByPosition.putIfAbsent(pos, new ArrayList<>());
            playersByPosition.get(pos).add(player);
        }

        for (Map.Entry<Integer, List<Player>> entry : playersByPosition.entrySet()) {
            int position = entry.getKey();
            List<Player> playersAtPos = entry.getValue();

            Point pos = board.getSnakePosition(position);

            int numPlayers = playersAtPos.size();
            for (int i = 0; i < numPlayers; i++) {
                Player player = playersAtPos.get(i);

                int offsetX = 0, offsetY = 0;
                if (numPlayers > 1) {
                    double angle = 2 * Math.PI * i / numPlayers;
                    offsetX = (int) (20 * Math.cos(angle));
                    offsetY = (int) (20 * Math.sin(angle));
                }

                int px = pos.x + offsetX;
                int py = pos.y + offsetY;

                // Shadow
                g2d.setColor(new Color(0, 0, 0, 80));
                g2d.fillOval(px - 13, py - 11, 26, 26);

                // Pion
                g2d.setColor(player.getColor());
                g2d.fillOval(px - 15, py - 15, 30, 30);
                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(3));
                g2d.drawOval(px - 15, py - 15, 30, 30);

                // Initial
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 14));
                String initial = player.getName().substring(0, 1).toUpperCase();
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(initial);
                g2d.drawString(initial, px - textWidth / 2, py + 5);
            }
        }
    }
}