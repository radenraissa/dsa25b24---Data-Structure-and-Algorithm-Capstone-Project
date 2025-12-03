import javax.swing.*;
import java.awt.*;
import java.awt.event.*; // Penting untuk Mouse Listener
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.*;
import java.util.List;

class BoardPanel extends JPanel {
    private Board board;
    private List<Player> players;
    private Image boardImage;
    private Point[] nodeLocations;

    public BoardPanel(Board board) {
        this.board = board;
        this.players = new ArrayList<>();

        try {
            // Pastikan nama file gambar benar
            boardImage = ImageIO.read(new File("board_bg.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Sesuaikan dimensi ini dengan ukuran asli gambar Anda
        // Klik kanan pada file gambar -> Properties -> Details untuk lihat ukurannya
        setPreferredSize(new Dimension(1100, 750));

        // --- ALAT KALIBRASI ---
        // Kode ini akan mencetak koordinat saat Anda klik gambar
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("nodeLocations[COUNTER] = new Point(" + e.getX() + ", " + e.getY() + ");");
            }
        });

        initNodeLocations();
    }

    private void initNodeLocations() {
        // Sesuaikan ukuran array
        nodeLocations = new Point[82]; // 81 kotak + index 0 buffer

        // Data Mapping Manual Anda:
        nodeLocations[1] = new Point(107, 62);
        nodeLocations[2] = new Point(174, 91);
        nodeLocations[3] = new Point(244, 92);
        nodeLocations[4] = new Point(333, 85);
        nodeLocations[5] = new Point(404, 86);
        nodeLocations[6] = new Point(484, 83);
        nodeLocations[7] = new Point(557, 87);
        nodeLocations[8] = new Point(650, 90);
        nodeLocations[9] = new Point(714, 87);
        nodeLocations[10] = new Point(799, 85);
        nodeLocations[11] = new Point(874, 80);
        nodeLocations[12] = new Point(951, 87);
        nodeLocations[13] = new Point(1038, 85);
        nodeLocations[14] = new Point(1037, 177);
        nodeLocations[15] = new Point(1041, 258);
        nodeLocations[16] = new Point(1038, 357);
        nodeLocations[17] = new Point(1037, 426);
        nodeLocations[18] = new Point(1027, 508);
        nodeLocations[19] = new Point(1032, 601);
        nodeLocations[20] = new Point(1043, 677);
        nodeLocations[21] = new Point(963, 667);
        nodeLocations[22] = new Point(886, 674);
        nodeLocations[23] = new Point(791, 662);
        nodeLocations[24] = new Point(721, 672);
        nodeLocations[25] = new Point(637, 672);
        nodeLocations[26] = new Point(549, 671);
        nodeLocations[27] = new Point(457, 673);
        nodeLocations[28] = new Point(383, 671);
        nodeLocations[29] = new Point(306, 671);
        nodeLocations[30] = new Point(221, 674);
        nodeLocations[31] = new Point(124, 658);
        nodeLocations[32] = new Point(62, 667);
        nodeLocations[33] = new Point(47, 594);
        nodeLocations[34] = new Point(40, 526);
        nodeLocations[35] = new Point(53, 424);
        nodeLocations[36] = new Point(53, 364);
        nodeLocations[37] = new Point(55, 281);
        nodeLocations[38] = new Point(61, 196);
        nodeLocations[39] = new Point(133, 195);
        nodeLocations[40] = new Point(205, 195);
        nodeLocations[41] = new Point(295, 187);
        nodeLocations[42] = new Point(347, 187);
        nodeLocations[43] = new Point(428, 196);
        nodeLocations[44] = new Point(497, 196);
        nodeLocations[45] = new Point(572, 208);
        nodeLocations[46] = new Point(649, 193);
        nodeLocations[47] = new Point(728, 200);
        nodeLocations[48] = new Point(778, 201);
        nodeLocations[49] = new Point(853, 198);
        nodeLocations[50] = new Point(931, 199);
        nodeLocations[51] = new Point(934, 287);
        nodeLocations[52] = new Point(934, 315);
        nodeLocations[53] = new Point(938, 425);
        nodeLocations[54] = new Point(927, 501);
        nodeLocations[55] = new Point(927, 587);
        nodeLocations[56] = new Point(846, 577);
        nodeLocations[57] = new Point(783, 580);
        nodeLocations[58] = new Point(713, 580);
        nodeLocations[59] = new Point(632, 581);
        nodeLocations[60] = new Point(567, 579);
        nodeLocations[61] = new Point(506, 589);
        nodeLocations[62] = new Point(437, 588);
        nodeLocations[63] = new Point(354, 578);
        nodeLocations[64] = new Point(206, 581);
        nodeLocations[65] = new Point(150, 578);
        nodeLocations[66] = new Point(155, 428);
        nodeLocations[67] = new Point(155, 372);
        nodeLocations[68] = new Point(151, 285);
        nodeLocations[69] = new Point(233, 298);
        nodeLocations[70] = new Point(292, 302);
        nodeLocations[71] = new Point(364, 300);
        nodeLocations[72] = new Point(452, 298);
        nodeLocations[73] = new Point(532, 296);
        nodeLocations[74] = new Point(609, 307);
        nodeLocations[75] = new Point(752, 293);
        nodeLocations[76] = new Point(825, 392);
        nodeLocations[77] = new Point(742, 483);
        nodeLocations[78] = new Point(591, 462);
        nodeLocations[79] = new Point(409, 466);
        nodeLocations[80] = new Point(377, 463);
        nodeLocations[81] = new Point(269, 403);
    }

    // ... (Sisa method setPlayers, paintComponent, drawPlayers, getPixelPosition SAMA SEPERTI SEBELUMNYA) ...
    // Copy paste sisa method di bawah ini agar file lengkap:

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
            g2d.drawImage(boardImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.drawString("Gambar board_bg.png tidak ditemukan!", 50, 50);
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

                g2d.setColor(player.getColor());
                g2d.fillOval(px - 15, py - 15, 30, 30);
                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawOval(px - 15, py - 15, 30, 30);

                g2d.setColor(Color.BLACK); // Ganti warna font jadi hitam biar jelas
                g2d.setFont(new Font("Arial", Font.BOLD, 14));
                String initial = player.getName().substring(0, 1).toUpperCase();
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(initial);
                g2d.drawString(initial, px - textWidth / 2, py + 5);
            }
        }
    }

    private Point getPixelPosition(int logicalPos) {
        if (logicalPos < 1) logicalPos = 1;

        // Safety: Jika mapping belum sampai 100, kembalikan posisi terakhir yang ada
        if (logicalPos >= nodeLocations.length) logicalPos = nodeLocations.length - 1;

        if (nodeLocations[logicalPos] != null) {
            return nodeLocations[logicalPos];
        }
        return new Point(50,50); // Default ke pojok kiri atas
    }
}