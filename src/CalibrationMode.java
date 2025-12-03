import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.ArrayList;

/**
 * CALIBRATION MODE - Mode Kalibrasi untuk Mapping Node Coordinates
 *
 * Cara Pakai:
 * 1. Run class ini (bukan Main.java)
 * 2. Klik setiap posisi node secara BERURUTAN dari node 1 sampai 74
 * 3. Koordinat akan ditampilkan di console
 * 4. Setelah selesai 74 node, code lengkap akan di-generate di console
 * 5. Copy-paste code tersebut ke method initNodeLocations() di BoardPanel.java
 */
public class CalibrationMode extends JFrame {
    private Image boardImage;
    private JLabel instructionLabel;
    private JLabel progressLabel;
    private int currentNode = 1;
    private static final int TOTAL_NODES = 74;
    private ArrayList<Point> nodeCoordinates;
    private CalibrationPanel boardPanel;

    public CalibrationMode() {
        nodeCoordinates = new ArrayList<>();

        setTitle("🎯 CALIBRATION MODE - Node Mapping Tool");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(44, 62, 80));

        // Load board image
        try {
            boardImage = ImageIO.read(new File("board_bg.png"));
            System.out.println("✓ Board image loaded successfully!");
            System.out.println("  Image size: " + boardImage.getWidth(null) + "x" + boardImage.getHeight(null));
        } catch (IOException e) {
            System.err.println("✗ ERROR: Cannot find 'board_bg.png'");
            System.err.println("  Make sure the image file is in the project root folder.");
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "ERROR: board_bg.png not found!\nMake sure the image is in the project root folder.",
                    "Image Load Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        // Top instruction panel
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(new Color(52, 73, 94));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        instructionLabel = new JLabel("🎯 Click on NODE 1 position");
        instructionLabel.setFont(new Font("Arial", Font.BOLD, 24));
        instructionLabel.setForeground(new Color(52, 152, 219));
        instructionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        progressLabel = new JLabel("Progress: 0 / 74 nodes");
        progressLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        progressLabel.setForeground(Color.WHITE);
        progressLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        topPanel.add(instructionLabel);
        topPanel.add(Box.createVerticalStrut(5));
        topPanel.add(progressLabel);

        add(topPanel, BorderLayout.NORTH);

        // Board panel
        boardPanel = new CalibrationPanel();
        add(boardPanel, BorderLayout.CENTER);

        // Control panel at bottom
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        bottomPanel.setBackground(new Color(52, 73, 94));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton undoButton = new JButton("⟲ UNDO Last Click");
        undoButton.setFont(new Font("Arial", Font.BOLD, 14));
        undoButton.setBackground(new Color(231, 76, 60));
        undoButton.setForeground(Color.WHITE);
        undoButton.setFocusPainted(false);
        undoButton.setPreferredSize(new Dimension(180, 40));
        undoButton.addActionListener(e -> undoLastClick());

        JButton resetButton = new JButton("🔄 RESET All");
        resetButton.setFont(new Font("Arial", Font.BOLD, 14));
        resetButton.setBackground(new Color(230, 126, 34));
        resetButton.setForeground(Color.WHITE);
        resetButton.setFocusPainted(false);
        resetButton.setPreferredSize(new Dimension(150, 40));
        resetButton.addActionListener(e -> resetCalibration());

        JButton generateButton = new JButton("✓ GENERATE Code");
        generateButton.setFont(new Font("Arial", Font.BOLD, 14));
        generateButton.setBackground(new Color(46, 204, 113));
        generateButton.setForeground(Color.WHITE);
        generateButton.setFocusPainted(false);
        generateButton.setPreferredSize(new Dimension(180, 40));
        generateButton.addActionListener(e -> generateCode());

        bottomPanel.add(undoButton);
        bottomPanel.add(resetButton);
        bottomPanel.add(generateButton);

        add(bottomPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);

        printInstructions();
    }

    private void printInstructions() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("    🎯 CALIBRATION MODE STARTED 🎯");
        System.out.println("=".repeat(70));
        System.out.println("\n📋 INSTRUCTIONS:");
        System.out.println("   1. Click on each node position IN ORDER (1 → 74)");
        System.out.println("   2. Click at the CENTER of each node square");
        System.out.println("   3. Each click will be recorded and shown in console");
        System.out.println("   4. Use 'UNDO' button if you make a mistake");
        System.out.println("   5. Use 'RESET' button to start over");
        System.out.println("   6. After all 74 nodes, click 'GENERATE Code' button");
        System.out.println("   7. Copy the generated code to BoardPanel.java\n");
        System.out.println("=".repeat(70));
        System.out.println("🖱️  Start clicking now...\n");
    }

    class CalibrationPanel extends JPanel {
        private Point mousePosition;

        public CalibrationPanel() {
            setPreferredSize(new Dimension(1100, 750));
            setBackground(new Color(44, 62, 80));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    handleNodeClick(e);
                }
            });

            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    mousePosition = e.getPoint();
                    repaint();
                }
            });
        }

        private void handleNodeClick(MouseEvent e) {
            if (currentNode <= TOTAL_NODES) {
                Point clickPoint = new Point(e.getX(), e.getY());
                nodeCoordinates.add(clickPoint);

                // Console output with formatting
                System.out.printf("✓ Node %2d → Point(%4d, %4d)\n",
                        currentNode, e.getX(), e.getY());

                currentNode++;
                updateLabels();
                repaint();

                if (currentNode > TOTAL_NODES) {
                    System.out.println("\n" + "=".repeat(70));
                    System.out.println("    ✓ CALIBRATION COMPLETE!");
                    System.out.println("    Click the 'GENERATE Code' button to get the code");
                    System.out.println("=".repeat(70) + "\n");
                }
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw board image
            if (boardImage != null) {
                g2d.drawImage(boardImage, 0, 0, getWidth(), getHeight(), this);
            } else {
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setColor(Color.RED);
                g2d.setFont(new Font("Arial", Font.BOLD, 24));
                g2d.drawString("❌ ERROR: board_bg.png not found!", 50, getHeight()/2);
                return;
            }

            // Draw all recorded nodes
            for (int i = 0; i < nodeCoordinates.size(); i++) {
                Point p = nodeCoordinates.get(i);

                // Outer glow
                g2d.setColor(new Color(46, 204, 113, 80));
                g2d.fillOval(p.x - 25, p.y - 25, 50, 50);

                // Circle
                g2d.setColor(new Color(46, 204, 113, 200));
                g2d.fillOval(p.x - 18, p.y - 18, 36, 36);

                // Border
                g2d.setColor(new Color(39, 174, 96));
                g2d.setStroke(new BasicStroke(3));
                g2d.drawOval(p.x - 18, p.y - 18, 36, 36);

                // Node number
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 14));
                String nodeNum = String.valueOf(i + 1);
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(nodeNum);
                int textHeight = fm.getAscent();
                g2d.drawString(nodeNum, p.x - textWidth/2, p.y + textHeight/2 - 2);
            }

            // Draw crosshair at mouse position
            if (mousePosition != null && currentNode <= TOTAL_NODES) {
                g2d.setColor(new Color(231, 76, 60, 230));
                g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                // Crosshair
                g2d.drawLine(mousePosition.x - 20, mousePosition.y, mousePosition.x + 20, mousePosition.y);
                g2d.drawLine(mousePosition.x, mousePosition.y - 20, mousePosition.x, mousePosition.y + 20);

                // Circle around crosshair
                g2d.setStroke(new BasicStroke(2));
                g2d.drawOval(mousePosition.x - 15, mousePosition.y - 15, 30, 30);
            }
        }
    }

    private void updateLabels() {
        if (currentNode <= TOTAL_NODES) {
            instructionLabel.setText("🎯 Click on NODE " + currentNode + " position");
            instructionLabel.setForeground(new Color(52, 152, 219));
        } else {
            instructionLabel.setText("✓ ALL NODES MAPPED!");
            instructionLabel.setForeground(new Color(46, 204, 113));
        }
        progressLabel.setText("Progress: " + nodeCoordinates.size() + " / " + TOTAL_NODES + " nodes");
    }

    private void undoLastClick() {
        if (!nodeCoordinates.isEmpty()) {
            Point removed = nodeCoordinates.remove(nodeCoordinates.size() - 1);
            currentNode--;
            updateLabels();
            System.out.println("⟲ UNDO - Removed Node " + (currentNode) + " at Point(" +
                    removed.x + ", " + removed.y + ")");
            boardPanel.repaint();
        } else {
            System.out.println("⚠️  Nothing to undo");
            JOptionPane.showMessageDialog(this,
                    "No nodes to undo!",
                    "Nothing to Undo",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void resetCalibration() {
        if (nodeCoordinates.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No nodes recorded yet!",
                    "Nothing to Reset",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to reset all " + nodeCoordinates.size() + " recorded nodes?\n" +
                        "This action cannot be undone!",
                "⚠️ Reset Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            nodeCoordinates.clear();
            currentNode = 1;
            updateLabels();
            System.out.println("\n🔄 RESET - All nodes cleared. Starting over...\n");
            boardPanel.repaint();
        }
    }

    private void generateCode() {
        if (nodeCoordinates.size() < TOTAL_NODES) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please complete all " + TOTAL_NODES + " nodes first!\n\n" +
                            "Current progress: " + nodeCoordinates.size() + " / " + TOTAL_NODES + " nodes",
                    "⚠️ Incomplete Calibration",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        System.out.println("\n" + "=".repeat(80));
        System.out.println("📋 GENERATED CODE - Copy everything below to BoardPanel.initNodeLocations()");
        System.out.println("=".repeat(80));
        System.out.println();
        System.out.println("private void initNodeLocations() {");
        System.out.println("    // Calibrated node locations for 74-node board");
        System.out.println("    // Generated by CalibrationMode on " +
                java.time.LocalDateTime.now().format(
                        java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println("    nodeLocations = new Point[" + (TOTAL_NODES + 1) + "];");
        System.out.println();

        for (int i = 0; i < nodeCoordinates.size(); i++) {
            Point p = nodeCoordinates.get(i);
            System.out.printf("    nodeLocations[%2d] = new Point(%4d, %4d);\n",
                    (i + 1), p.x, p.y);
        }

        System.out.println("}");
        System.out.println();
        System.out.println("=".repeat(80));
        System.out.println("✓ CODE GENERATED SUCCESSFULLY!");
        System.out.println("  Total nodes mapped: " + nodeCoordinates.size());
        System.out.println("  Next steps:");
        System.out.println("    1. Copy the code above");
        System.out.println("    2. Open BoardPanel.java");
        System.out.println("    3. Replace the initNodeLocations() method");
        System.out.println("    4. Save and run Main.java to test");
        System.out.println("=".repeat(80) + "\n");

        JOptionPane.showMessageDialog(
                this,
                "✓ Code generated successfully!\n\n" +
                        "Check your console/terminal and copy the generated code\n" +
                        "to the initNodeLocations() method in BoardPanel.java\n\n" +
                        "Total nodes mapped: " + nodeCoordinates.size(),
                "Success",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CalibrationMode calibration = new CalibrationMode();
            calibration.setVisible(true);
        });
    }
}