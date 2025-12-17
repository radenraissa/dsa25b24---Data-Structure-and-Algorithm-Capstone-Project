import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class Main extends JFrame {

    public Main() {
        setTitle("Capstone Project Game Portal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 450);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(44, 62, 80));

        // --- TITLE HEADER ---
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(44, 62, 80));
        titlePanel.setBorder(new EmptyBorder(40, 0, 20, 0));

        JLabel title = new JLabel("CHOOSE YOUR GAME!");
        title.setFont(new Font("Poppins", Font.BOLD, 36));
        title.setForeground(Color.BLACK);
        titlePanel.add(title);

        add(titlePanel, BorderLayout.NORTH);

        // --- BUTTONS PANEL ---
        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new GridLayout(2, 1, 20, 20));
        btnPanel.setBackground(new Color(44, 62, 80));
        btnPanel.setBorder(new EmptyBorder(30, 100, 50, 100));

        JButton btnLadder = createMenuButton("🐍 SNAKE & LADDER", new Color(46, 204, 113));
        JButton btnPacman = createMenuButton("ᗧ••• PACMAN MAZE", new Color(241, 196, 15));

        // --- ACTIONS ---

        // 1. Launch Snake & Ladder
        btnLadder.addActionListener(e -> {
            this.dispose(); // Close Menu
            SwingUtilities.invokeLater(() -> {
                try {
                    GameGUI game = new GameGUI();
                    game.setVisible(true);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        });

        // 2. Launch Pacman Maze
        btnPacman.addActionListener(e -> {
            this.dispose(); // Close Menu
            SwingUtilities.invokeLater(() -> {
                try {
                    PacmanMSTControl pacman = new PacmanMSTControl();
                    pacman.setVisible(true);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        });

        btnPanel.add(btnLadder);
        btnPanel.add(btnPacman);
        add(btnPanel, BorderLayout.CENTER);

        // --- FOOTER ---
        JLabel footer = new JLabel("Select a game to start playing");
        footer.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        footer.setForeground(Color.LIGHT_GRAY);
        footer.setHorizontalAlignment(SwingConstants.CENTER);
        footer.setBorder(new EmptyBorder(0,0,20,0));
        add(footer, BorderLayout.SOUTH);
    }

    private JButton createMenuButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 20));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}