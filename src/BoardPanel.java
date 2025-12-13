import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.*;
import java.util.List;

class BoardPanel extends JPanel {
    private Board board;
    private List<Player> players;
    private Image boardImage;

    // Ganti array Point jadi Point2D.Double (untuk menyimpan desimal)
    private Point2D.Double[] nodeLocations;

    public BoardPanel(Board board) {
        this.board = board;
        this.players = new ArrayList<>();

        try {
            boardImage = ImageIO.read(new File("board_bg.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        setPreferredSize(new Dimension(1100, 750));
        setBackground(new Color(44, 62, 80));

        initNodeLocations();
    }

    private void initNodeLocations() {
        nodeLocations = new Point2D.Double[75];
        nodeLocations[1] = new Point2D.Double(0.8755, 0.1575);
        nodeLocations[2] = new Point2D.Double(0.8265, 0.2038);
        nodeLocations[3] = new Point2D.Double(0.7753, 0.2295);
        nodeLocations[4] = new Point2D.Double(0.7313, 0.1781);
        nodeLocations[5] = new Point2D.Double(0.6889, 0.1336);
        nodeLocations[6] = new Point2D.Double(0.6347, 0.1113);
        nodeLocations[7] = new Point2D.Double(0.5893, 0.1575);
        nodeLocations[8] = new Point2D.Double(0.5498, 0.2089);
        nodeLocations[9] = new Point2D.Double(0.5198, 0.2757);
        nodeLocations[10] = new Point2D.Double(0.4934, 0.3442);
        nodeLocations[11] = new Point2D.Double(0.5403, 0.3938);
        nodeLocations[12] = new Point2D.Double(0.5930, 0.4178);
        nodeLocations[13] = new Point2D.Double(0.6501, 0.4092);
        nodeLocations[14] = new Point2D.Double(0.7042, 0.3836);
        nodeLocations[15] = new Point2D.Double(0.7540, 0.3442);
        nodeLocations[16] = new Point2D.Double(0.8075, 0.3065);
        nodeLocations[17] = new Point2D.Double(0.8587, 0.2911);
        nodeLocations[18] = new Point2D.Double(0.8829, 0.3596);
        nodeLocations[19] = new Point2D.Double(0.8982, 0.4281);
        nodeLocations[20] = new Point2D.Double(0.8960, 0.5017);
        nodeLocations[21] = new Point2D.Double(0.8426, 0.5308);
        nodeLocations[22] = new Point2D.Double(0.7862, 0.5599);
        nodeLocations[23] = new Point2D.Double(0.7291, 0.5497);
        nodeLocations[24] = new Point2D.Double(0.6676, 0.5497);
        nodeLocations[25] = new Point2D.Double(0.6215, 0.5993);
        nodeLocations[26] = new Point2D.Double(0.6252, 0.6678);
        nodeLocations[27] = new Point2D.Double(0.6801, 0.6986);
        nodeLocations[28] = new Point2D.Double(0.7401, 0.6918);
        nodeLocations[29] = new Point2D.Double(0.7980, 0.6815);
        nodeLocations[30] = new Point2D.Double(0.8499, 0.7209);
        nodeLocations[31] = new Point2D.Double(0.8302, 0.7894);
        nodeLocations[32] = new Point2D.Double(0.7921, 0.8442);
        nodeLocations[33] = new Point2D.Double(0.7430, 0.8836);
        nodeLocations[34] = new Point2D.Double(0.6823, 0.8767);
        nodeLocations[35] = new Point2D.Double(0.6252, 0.8579);
        nodeLocations[36] = new Point2D.Double(0.5695, 0.8699);
        nodeLocations[37] = new Point2D.Double(0.5132, 0.8870);
        nodeLocations[38] = new Point2D.Double(0.4561, 0.9058);
        nodeLocations[39] = new Point2D.Double(0.3982, 0.9007);
        nodeLocations[40] = new Point2D.Double(0.3777, 0.8322);
        nodeLocations[41] = new Point2D.Double(0.4173, 0.7808);
        nodeLocations[42] = new Point2D.Double(0.4517, 0.7192);
        nodeLocations[43] = new Point2D.Double(0.4846, 0.6610);
        nodeLocations[44] = new Point2D.Double(0.4810, 0.5856);
        nodeLocations[45] = new Point2D.Double(0.4253, 0.5582);
        nodeLocations[46] = new Point2D.Double(0.3697, 0.5753);
        nodeLocations[47] = new Point2D.Double(0.3324, 0.6387);
        nodeLocations[48] = new Point2D.Double(0.3001, 0.7021);
        nodeLocations[49] = new Point2D.Double(0.2731, 0.7723);
        nodeLocations[50] = new Point2D.Double(0.2474, 0.8322);
        nodeLocations[51] = new Point2D.Double(0.2130, 0.8973);
        nodeLocations[52] = new Point2D.Double(0.1552, 0.9127);
        nodeLocations[53] = new Point2D.Double(0.1083, 0.8699);
        nodeLocations[54] = new Point2D.Double(0.1186, 0.8014);
        nodeLocations[55] = new Point2D.Double(0.1479, 0.7363);
        nodeLocations[56] = new Point2D.Double(0.1471, 0.6610);
        nodeLocations[57] = new Point2D.Double(0.1223, 0.5908);
        nodeLocations[58] = new Point2D.Double(0.0988, 0.5223);
        nodeLocations[59] = new Point2D.Double(0.1340, 0.4623);
        nodeLocations[60] = new Point2D.Double(0.1903, 0.4623);
        nodeLocations[61] = new Point2D.Double(0.2482, 0.4469);
        nodeLocations[62] = new Point2D.Double(0.3031, 0.4178);
        nodeLocations[63] = new Point2D.Double(0.3536, 0.3870);
        nodeLocations[64] = new Point2D.Double(0.3953, 0.3373);
        nodeLocations[65] = new Point2D.Double(0.4143, 0.2654);
        nodeLocations[66] = new Point2D.Double(0.4341, 0.2038);
        nodeLocations[67] = new Point2D.Double(0.3880, 0.1575);
        nodeLocations[68] = new Point2D.Double(0.3302, 0.1610);
        nodeLocations[69] = new Point2D.Double(0.2826, 0.2021);
        nodeLocations[70] = new Point2D.Double(0.2423, 0.2620);
        nodeLocations[71] = new Point2D.Double(0.2013, 0.3134);
        nodeLocations[72] = new Point2D.Double(0.1435, 0.3288);
        nodeLocations[73] = new Point2D.Double(0.1164, 0.2603);
        nodeLocations[74] = new Point2D.Double(0.0842, 0.1952);
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

        if (boardImage != null) {
            // Gambar meregang memenuhi panel
            g2d.drawImage(boardImage, 0, 0, getWidth(), getHeight(), this);
        }

        drawPlayers(g2d);
    }

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

            // Ambil posisi PIXEL yang sudah dihitung responsif
            Point pixelPos = getPixelPosition(position);
            int baseX = pixelPos.x;
            int baseY = pixelPos.y;

            int numPlayers = playersAtPos.size();
            for (int i = 0; i < numPlayers; i++) {
                Player player = playersAtPos.get(i);
                int offsetX = 0, offsetY = 0;
                if (numPlayers > 1) {
                    double angle = 2 * Math.PI * i / numPlayers;
                    offsetX = (int) (15 * Math.cos(angle));
                    offsetY = (int) (15 * Math.sin(angle));
                }

                int px = baseX + offsetX;
                int py = baseY + offsetY;

                // Gambar Pion Sederhana
                g2d.setColor(player.getColor());
                g2d.fillOval(px - 15, py - 15, 30, 30);
                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawOval(px - 15, py - 15, 30, 30);

                // Inisial Nama
                g2d.setFont(new Font("Arial", Font.BOLD, 14));
                String initial = player.getName().substring(0, 1).toUpperCase();
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(initial, px - fm.stringWidth(initial)/2, py + fm.getAscent()/2 - 3);
            }
        }
    }

    // --- RUMUS SAKTI AGAR FLEKSIBEL ---
    private Point getPixelPosition(int logicalPos) {
        if (logicalPos < 1) logicalPos = 1;
        if (logicalPos >= nodeLocations.length) logicalPos = nodeLocations.length - 1;

        Point2D.Double relativePoint = nodeLocations[logicalPos];

        if (relativePoint == null) return new Point(0, 0);

        // Disini keajaibannya:
        // Koordinat Pixel = Persentase * Ukuran Layar Saat Ini
        int x = (int) (relativePoint.x * getWidth());
        int y = (int) (relativePoint.y * getHeight());

        return new Point(x, y);
    }
}