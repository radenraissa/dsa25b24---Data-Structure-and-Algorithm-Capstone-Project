import javax.swing.*;
import java.awt.*;

public class GameGUI {

    private JFrame frame;
    private BoardPanel boardPanel;

    public GameGUI(Board board, TurnManager turnManager) {
        frame = new JFrame("Board Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // ★ PENTING: Gunakan BoardPanel yg sudah fix-sizenya
        boardPanel = new BoardPanel(board);

        frame.add(boardPanel, BorderLayout.CENTER);

        // ★ Jangan resize! Untuk menjaga koordinat tetap akurat
        frame.pack();
        frame.setResizable(false);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public BoardPanel getBoardPanel() {
        return boardPanel;
    }
}
