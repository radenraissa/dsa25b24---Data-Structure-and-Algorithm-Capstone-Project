import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

class GameGUI extends JFrame {
    private Board board;
    private TurnManager turnManager;
    private MovementManager movementManager;
    private Dice dice;

    private BoardPanel boardPanel;
    private DicePanel dicePanel;
    private JButton rollButton;
    private JLabel infoLabel;
    private JLabel turnLabel;
    private JTextArea logArea;

    private boolean isAnimating;
    private Timer animationTimer;

    public GameGUI() {
        board = new Board();
        turnManager = new TurnManager();
        movementManager = new MovementManager();
        dice = new Dice();
        isAnimating = false;

        setupPlayers();
        initGUI();
    }

    private void setupPlayers() {
        String[] colors = {"Player 1 (Red)", "Player 2 (Blue)", "Player 3 (Green)", "Player 4 (Yellow)"};
        Color[] playerColors = {
                new Color(231, 76, 60),
                new Color(52, 152, 219),
                new Color(46, 204, 113),
                new Color(241, 196, 15)
        };

        String input = JOptionPane.showInputDialog(
                this, "Enter number of players (2-4):", "Game Setup", JOptionPane.QUESTION_MESSAGE);

        int numPlayers = 2;
        if (input != null && !input.trim().isEmpty()) {
            try {
                numPlayers = Integer.parseInt(input);
                if (numPlayers < 2 || numPlayers > 4) numPlayers = 2;
            } catch (NumberFormatException e) {
                numPlayers = 2;
            }
        }

        for (int i = 0; i < numPlayers; i++) {
            String name = JOptionPane.showInputDialog(
                    this, "Enter name for " + colors[i] + ":", "Player " + (i+1), JOptionPane.QUESTION_MESSAGE);
            if (name == null || name.trim().isEmpty()) {
                name = "Player " + (i + 1);
            }
            turnManager.addPlayer(new Player(name, playerColors[i]));
        }
    }

    private void initGUI() {
        setTitle("Ular Tangga Game - GUI Version");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(48, 71, 186));  // Biru tua (kiri atas)

        boardPanel = new BoardPanel(board);
        boardPanel.setPlayers(turnManager.getAllPlayers());
        add(boardPanel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(new Color(48, 71, 186));  // Biru tua (kiri atas)
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        turnLabel = new JLabel("Turn: " + turnManager.getCurrentPlayer().getName());
        turnLabel.setFont(new Font("Arial", Font.BOLD, 18));
        turnLabel.setForeground(Color.WHITE);
        turnLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightPanel.add(turnLabel);
        rightPanel.add(Box.createVerticalStrut(20));

        dicePanel = new DicePanel(dice);
        dicePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightPanel.add(dicePanel);
        rightPanel.add(Box.createVerticalStrut(20));

        rollButton = new JButton("ROLL DICE");
        rollButton.setFont(new Font("Arial", Font.BOLD, 16));
        rollButton.setBackground(new Color(91, 192, 235));  // Biru muda/cyan (kiri bawah)
        rollButton.setForeground(Color.WHITE);
        rollButton.setFocusPainted(false);
        rollButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        rollButton.addActionListener(e -> rollDice());
        rightPanel.add(rollButton);
        rightPanel.add(Box.createVerticalStrut(20));

        infoLabel = new JLabel(" ");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        infoLabel.setForeground(Color.WHITE);
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightPanel.add(infoLabel);
        rightPanel.add(Box.createVerticalStrut(20));

        logArea = new JTextArea(15, 20);
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightPanel.add(scrollPane);

        add(rightPanel, BorderLayout.EAST);

        pack();
        setLocationRelativeTo(null);
        setResizable(false);

        log("Game started! " + turnManager.getCurrentPlayer().getName() + "'s turn.");
    }

    private void rollDice() {
        if (isAnimating) return;

        rollButton.setEnabled(false);
        Player currentPlayer = turnManager.getCurrentPlayer();
        int currentPos = currentPlayer.getPosition();

        dice.roll();
        dicePanel.setRolled(true);

        int steps = dice.getNumber();
        boolean isGreen = (dice.getColor() == Dice.DiceColor.GREEN);

        String direction = isGreen ? "MAJU (Green)" : "MUNDUR (Red)";
        log(currentPlayer.getName() + " rolled " + direction + ": " + steps);

        // ============================================================
        // SKENARIO 1: DADU MERAH (MUNDUR) - TETAP PAKAI STACK (REVISI 2)
        // ============================================================
        if (!isGreen) {
            ArrayList<Integer> backwardPath = new ArrayList<>();
            backwardPath.add(currentPos);

            for (int i = 0; i < steps; i++) {
                int prevPos = currentPlayer.undoStep(); // Mundur sesuai jejak sejarah
                backwardPath.add(prevPos);

                if (prevPos == 1) break;
            }

            movementManager.setPath(backwardPath);
            isAnimating = true;
            infoLabel.setText("Retracing steps...");
            animateMovement(currentPlayer);
            return;
        }

        // ============================================================
        // SKENARIO 2: MAJU - POSISI PRIMA (DIJKSTRA AKTIF)
        // ============================================================
        boolean isPrime = isPrime(currentPos);

        if (isGreen && isPrime) {
            log("✨ POSISI PRIMA (" + currentPos + ")! Dijkstra Aktif! ✨");

            DijkstraAlgorithm solver = new DijkstraAlgorithm(board);
            ArrayList<Integer> fullPath = solver.getShortestPath(currentPos, 100);

            // LOGIKA BARU (KEMBALI KE ASAL):
            // Setiap perpindahan dihitung 1 langkah (termasuk naik tangga)

            ArrayList<Integer> actualPath = new ArrayList<>();
            actualPath.add(currentPos);

            int stepsTaken = 0;
            // Kita loop path hasil Dijkstra
            for (int i = 1; i < fullPath.size(); i++) {
                // Sesuai request kamu:
                if (stepsTaken < steps) {
                    int nextNode = fullPath.get(i);
                    actualPath.add(nextNode);

                    // REKAM JEJAK (PENTING BUAT MUNDUR NANTI)
                    currentPlayer.recordStep(nextNode);

                    stepsTaken++; // Selalu nambah 1, entah itu jalan biasa atau tangga
                } else {
                    break;
                }
            }

            movementManager.setPath(actualPath);
            isAnimating = true;
            infoLabel.setText("Moving via Dijkstra...");
            animateMovement(currentPlayer);
            return;
        }

        // ============================================================
        // SKENARIO 3: MAJU BIASA (NON-PRIMA = TANGGA MATI)
        // ============================================================
        ArrayList<Integer> normalPath = new ArrayList<>();
        normalPath.add(currentPos);

        int tempPos = currentPos;
        for (int i = 0; i < steps; i++) {
            tempPos++;
            if (tempPos > 100) {
                tempPos--;
                break;
            }

            normalPath.add(tempPos);

            // REKAM JEJAK (PENTING BUAT MUNDUR NANTI)
            currentPlayer.recordStep(tempPos);
        }

        movementManager.setPath(normalPath);
        isAnimating = true;
        infoLabel.setText("Moving...");
        animateMovement(currentPlayer);
    }

    private void animateMovement(Player player) {
        animationTimer = new Timer(300, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Integer nextPos = movementManager.popNextPosition();
                if (nextPos != null) {
                    player.setPosition(nextPos);
                    boardPanel.repaint();
                    infoLabel.setText("Position: " + nextPos);
                } else {
                    animationTimer.stop();
                    isAnimating = false;
                    finishTurn(player);
                }
            }
        });
        animationTimer.start();
    }

    private void finishTurn(Player player) {
        int currentPos = player.getPosition();

        // Check if landed on ladder
        Ladder ladder = board.getLadderAt(currentPos);
        if (ladder != null) {
            int newPos = ladder.getTop();
            player.setPosition(newPos);
            boardPanel.repaint();
            log("🪜 " + player.getName() + " naik tangga ke posisi " + newPos + "!");
        }

        log(player.getName() + " is now at position " + player.getPosition());

        if (player.getPosition() == 100) {
            log("🎉 " + player.getName() + " WINS! 🎉");
            JOptionPane.showMessageDialog(this,
                    player.getName() + " wins the game!",
                    "Game Over",
                    JOptionPane.INFORMATION_MESSAGE);
            rollButton.setEnabled(false);
            return;
        }

        turnManager.nextTurn();
        turnLabel.setText("Turn: " + turnManager.getCurrentPlayer().getName());
        rollButton.setEnabled(true);
        infoLabel.setText(" ");
        log("---");
    }

    private int calculateSteps() {
        int steps = 0;
        if (dice.getColor() == Dice.DiceColor.GREEN) {
            steps = dice.getNumber();
        } else {
            steps = -dice.getNumber();
        }
        return steps;
    }

    static boolean isPrime(int n)
    {
        if (n <= 1) {
            return false;
        }

        for (int i = 2; i < n; i++) {
            if (n % i == 0){
                return false;
            }
        }
        return true;
    }

    private void log(String message) {
        logArea.append(message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
}