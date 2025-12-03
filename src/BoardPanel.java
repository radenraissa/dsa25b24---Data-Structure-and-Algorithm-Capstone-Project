import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class BoardPanel extends JPanel {
    private BufferedImage boardImage;
    private Point[] nodeLocations;
    private Player[] players;

    public BoardPanel(Board board) {
        try {
            boardImage = ImageIO.read(getClass().getResource("/board.png"));
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Error loading image: " + e.getMessage());
            boardImage = null;
        }

        // ★ Kunci ukuran panel agar match dengan CalibrationMode
        this.setPreferredSize(new Dimension(1100, 750));
        this.setMinimumSize(new Dimension(1100, 750));
        this.setMaximumSize(new Dimension(1100, 750));
        this.setSize(new Dimension(1100, 750)); // penting

        initNodeLocations();
    }

    public void setPlayers(Player[] players) {
        this.players = players;
    }

    private void initNodeLocations() {
        nodeLocations = new Point[75];
        // ... koordinat kamu tetap sama di sini
    }

    public Point getPixelPosition(int position) {
        if (position < 1 || position >= nodeLocations.length || nodeLocations[position] == null) {
            return new Point(0, 0);
        }
        return nodeLocations[position];
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (boardImage != null) {
            // gambar penuh ukuran panel (1100x750)
            g2d.drawImage(boardImage, 0, 0, getWidth(), getHeight(), this);
        }

        if (players != null) {
            drawPlayers(g2d);
        }
    }

    private void drawPlayers(Graphics2D g2d) {
        // ... isi drawPlayers kamu tetap sama di sini
    }
}
