import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D; // Import baru untuk koordinat presisi
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.ArrayList;

public class CalibrationMode extends JFrame {
    private Image boardImage;
    private JLabel instructionLabel;
    private JLabel progressLabel;
    private int currentNode = 1;
    private static final int TOTAL_NODES = 74; // Sesuaikan jumlah kotak board Anda

    // Kita simpan koordinat sebagai Double (0.0 sampai 1.0)
    private ArrayList<Point2D.Double> nodeCoordinates;
    private CalibrationPanel boardPanel;

    public CalibrationMode() {
        nodeCoordinates = new ArrayList<>();

        setTitle("🎯 RELATIVE CALIBRATION MODE (Percentage Based)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(44, 62, 80));

        try {
            boardImage = ImageIO.read(new File("board_bg.png"));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "ERROR: board_bg.png not found!");
            System.exit(1);
        }

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(new Color(52, 73, 94));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        instructionLabel = new JLabel("🎯 Click CENTER of NODE 1");
        instructionLabel.setFont(new Font("Arial", Font.BOLD, 24));
        instructionLabel.setForeground(new Color(52, 152, 219));
        instructionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        progressLabel = new JLabel("Progress: 0 / " + TOTAL_NODES);
        progressLabel.setForeground(Color.WHITE);
        progressLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        topPanel.add(instructionLabel);
        topPanel.add(progressLabel);
        add(topPanel, BorderLayout.NORTH);

        boardPanel = new CalibrationPanel();
        add(boardPanel, BorderLayout.CENTER);

        JButton generateButton = new JButton("✓ GENERATE CODE");
        generateButton.setFont(new Font("Arial", Font.BOLD, 16));
        generateButton.setBackground(new Color(46, 204, 113));
        generateButton.setForeground(Color.WHITE);
        generateButton.addActionListener(e -> generateCode());

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(44, 62, 80));
        bottomPanel.add(generateButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Ukuran window agak besar biar akurat saat klik
        setSize(1200, 800);
        setLocationRelativeTo(null);
    }

    class CalibrationPanel extends JPanel {
        public CalibrationPanel() {
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (currentNode <= TOTAL_NODES) {
                        // RUMUS RELATIF: Posisi Klik dibagi Ukuran Panel
                        double relativeX = (double) e.getX() / getWidth();
                        double relativeY = (double) e.getY() / getHeight();

                        nodeCoordinates.add(new Point2D.Double(relativeX, relativeY));

                        System.out.printf("Node %d: %.4f, %.4f%n", currentNode, relativeX, relativeY);

                        currentNode++;
                        instructionLabel.setText(currentNode > TOTAL_NODES ? "DONE! Click Generate" : "Click Node " + currentNode);
                        progressLabel.setText("Progress: " + nodeCoordinates.size() + " / " + TOTAL_NODES);
                        repaint();
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (boardImage != null) {
                g.drawImage(boardImage, 0, 0, getWidth(), getHeight(), this);
            }

            // Gambar titik yang sudah diklik
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            for (int i = 0; i < nodeCoordinates.size(); i++) {
                Point2D.Double p = nodeCoordinates.get(i);

                // Konversi balik dari Relatif ke Pixel untuk digambar
                int px = (int) (p.x * getWidth());
                int py = (int) (p.y * getHeight());

                g2d.setColor(Color.RED);
                g2d.fillOval(px - 5, py - 5, 10, 10);
                g2d.setColor(Color.WHITE);
                g2d.drawString(String.valueOf(i + 1), px + 8, py + 5);
            }
        }
    }

    private void generateCode() {
        System.out.println("\n// --- COPY CODE DI BAWAH INI KE BoardPanel.java ---");
        System.out.println("private void initNodeLocations() {");
        System.out.println("    nodeLocations = new Point2D.Double[" + (TOTAL_NODES + 1) + "];");

        for (int i = 0; i < nodeCoordinates.size(); i++) {
            Point2D.Double p = nodeCoordinates.get(i);
            // Format output menggunakan Point2D.Double
            System.out.printf("    nodeLocations[%d] = new Point2D.Double(%.4f, %.4f);%n", (i + 1), p.x, p.y);
        }
        System.out.println("}");
        System.out.println("// ------------------------------------------------");

        JOptionPane.showMessageDialog(this, "Code generated in Console!");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CalibrationMode().setVisible(true));
    }
}