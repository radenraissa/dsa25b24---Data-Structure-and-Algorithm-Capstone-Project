import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

public class Main extends JFrame {

    // Minecraft Color Palette
    private static final Color MC_BG_COLOR = new Color(36, 22, 13); // Dirt Brown
    private static final Color MC_BUTTON_NORMAL = new Color(110, 110, 110); // Stone Grey
    private static final Color MC_BUTTON_HOVER = new Color(130, 130, 160); // Lighter Grey (Hover)
    private static final Color MC_BUTTON_BORDER_LIGHT = new Color(251, 130, 130);
    private static final Color MC_BUTTON_BORDER_DARK = new Color(40, 40, 40);
    private static final Color MC_YELLOW = new Color(255, 255, 85); // Splash Text Yellow

    public Main() {
        setTitle("Our Game Project");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(854, 480); // 480p standard scale
        setLocationRelativeTo(null);

        // Main Panel dengan Background Custom
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Gambar background solid (Dirt Color)
                g.setColor(MC_BG_COLOR);
                g.fillRect(0, 0, getWidth(), getHeight());

                // Opsional: Jika mau nambah tekstur kotak-kotak samar
                g.setColor(new Color(255, 255, 255, 5));
                for(int i=0; i<getWidth(); i+=32) {
                    g.drawLine(i, 0, i, getHeight());
                }
                for(int i=0; i<getHeight(); i+=32) {
                    g.drawLine(0, i, getWidth(), i);
                }
            }
        };
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        setContentPane(mainPanel);

        // --- SPACER ATAS ---
        mainPanel.add(Box.createVerticalStrut(50));

        // --- TITLE HEADER (Custom Painting untuk efek 3D Logo) ---
        JPanel titlePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                String text = "CHOOSE YOUR GAME";
                Font font = new Font("Monospaced", Font.BOLD | Font.ITALIC, 48);
                FontMetrics fm = g2d.getFontMetrics(font);
                int textWidth = fm.stringWidth(text);
                int x = (getWidth() - textWidth) / 2;
                int y = 50;

                // 1. Gambar Bayangan Hitam (Offset)
                g2d.setFont(font);
                g2d.setColor(Color.BLACK);
                g2d.drawString(text, x + 4, y + 4);

                // 2. Gambar Teks Utama (Abu-abu)
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.drawString(text, x, y);
            }
        };
        titlePanel.setPreferredSize(new Dimension(800, 100));
        titlePanel.setMaximumSize(new Dimension(800, 100));
        titlePanel.setOpaque(false); // Transparan agar background dirt terlihat
        mainPanel.add(titlePanel);

        // --- BUTTONS PANEL ---
        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new GridLayout(2, 1, 10, 10)); // Gap 10px
        btnPanel.setOpaque(false);
        btnPanel.setBorder(new EmptyBorder(20, 200, 50, 200)); // Padding kiri kanan besar agar tombol di tengah

        JButton btnLadder = createMinecraftButton("Snake & Ladder");
        JButton btnPacman = createMinecraftButton("Pacman Maze");

        // --- ACTIONS ---
        btnLadder.addActionListener(e -> {
            this.dispose();
            SwingUtilities.invokeLater(() -> {
                try {
                    GameGUI game = new GameGUI();
                    game.setVisible(true);
                } catch (Exception ex) { ex.printStackTrace(); }
            });
        });

        btnPacman.addActionListener(e -> {
            this.dispose();
            SwingUtilities.invokeLater(() -> {
                try {
                    PacmanMSTControl pacman = new PacmanMSTControl();
                    pacman.setVisible(true);
                } catch (Exception ex) { ex.printStackTrace(); }
            });
        });

        btnPanel.add(btnLadder);
        btnPanel.add(btnPacman);
        mainPanel.add(btnPanel);

        // --- FOOTER ---
        JLabel footer = new JLabel("Copyright 089 & 097 Inc. Do not distribute!");
        footer.setFont(new Font("SansSerif", Font.PLAIN, 12));
        footer.setForeground(Color.WHITE);
        footer.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(footer);
        mainPanel.add(Box.createVerticalStrut(20));
    }

    // --- HELPER: MEMBUAT TOMBOL GAYA MINECRAFT ---
    private JButton createMinecraftButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Monospaced", Font.BOLD, 18));
        btn.setForeground(Color.DARK_GRAY);
        btn.setBackground(MC_BUTTON_NORMAL);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Membuat efek border 3D kotak-kotak (Bevel)
        Border outside = BorderFactory.createBevelBorder(BevelBorder.RAISED, MC_BUTTON_BORDER_LIGHT, MC_BUTTON_BORDER_DARK);
        Border inside = new EmptyBorder(10, 20, 10, 20); // Padding teks
        btn.setBorder(new CompoundBorder(outside, inside));

        // Hover Effect (Ganti warna & border saat mouse masuk)
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(MC_BUTTON_HOVER);
                btn.setBorder(new CompoundBorder(
                        BorderFactory.createBevelBorder(BevelBorder.RAISED, new Color(124, 124, 207), MC_BUTTON_BORDER_DARK),
                        inside));
                btn.setForeground(new Color(7, 23, 80)); // Teks jadi agak kuning
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(MC_BUTTON_NORMAL);
                btn.setBorder(new CompoundBorder(outside, inside));
                btn.setForeground(Color.DARK_GRAY);
            }
        });

        return btn;
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}