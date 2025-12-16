import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Print startup info
        System.out.println("========================================");
        System.out.println("  Snake & Ladder - 74 Nodes Edition");
        System.out.println("========================================");
        System.out.println("Starting game...\n");

        // Launch game in EDT (Event Dispatch Thread)
        SwingUtilities.invokeLater(() -> {
            try {
                GameGUI game = new GameGUI();
                game.setVisible(true);
            } catch (Exception e) {
                System.err.println("ERROR: Failed to start game!");
                e.printStackTrace();

                JOptionPane.showMessageDialog(null,
                        "Failed to start game!\n\n" +
                                "Possible reasons:\n" +
                                "1. board_bg.png not found\n" +
                                "2. Node positions not calibrated yet\n\n" +
                                "Solution:\n" +
                                "- Make sure board_bg.png is in project root\n" +
                                "- Run CalibrationMode.java first to calibrate nodes\n" +
                                "- Check console for detailed error messages",
                        "Game Startup Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }
}