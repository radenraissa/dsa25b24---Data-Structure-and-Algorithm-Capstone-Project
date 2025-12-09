import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

class GameGUI extends JFrame {
    private Board board;
    private TurnManager turnManager;
    private MovementManager movementManager;
    private Dice dice;

    private BoardPanel boardPanel;
    private DicePanel dicePanel;
    private JButton rollButton;
    private JButton playAgainButton; // NEW
    private JLabel infoLabel;
    private JLabel turnLabel;
    private JTextArea logArea;
    private JPanel scorePanel;

    private boolean isAnimating;
    private Timer animationTimer;

    private int currentRound = 1;

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
        String[] playerNames = {"Red", "Blue", "Green", "Yellow"};
        Color[] playerColors = {
                new Color(231, 76, 60),
                new Color(52, 152, 219),
                new Color(46, 204, 113),
                new Color(241, 196, 15)
        };

        int numPlayers = 2;

        for (int i = 0; i < numPlayers; i++) {
            String name = String.valueOf((char)('A' + i));
            turnManager.addPlayer(new Player(name, playerColors[i]));
        }
    }

    private void initGUI() {
        setTitle("Snake & Ladder Adventure - Round " + currentRound);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        getContentPane().setBackground(new Color(135, 206, 235));

        boardPanel = new BoardPanel(board);
        boardPanel.setPlayers(turnManager.getAllPlayers());
        add(boardPanel, BorderLayout.CENTER);

        // --- RIGHT PANEL UI ---
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(new Color(245, 245, 245));
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 2, 0, 0, new Color(189, 195, 199)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        rightPanel.setPreferredSize(new Dimension(300, 0));

        // 1. TURN LABEL
        turnLabel = new JLabel("Turn: " + turnManager.getCurrentPlayer().getName());
        turnLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        turnLabel.setForeground(new Color(44, 62, 80));
        turnLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightPanel.add(turnLabel);
        rightPanel.add(Box.createVerticalStrut(20));

        // 2. SCOREBOARD PANEL
        JLabel scoreTitle = new JLabel("🏆 SCOREBOARD");
        scoreTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        scoreTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightPanel.add(scoreTitle);
        rightPanel.add(Box.createVerticalStrut(5));

        scorePanel = new JPanel();
        scorePanel.setLayout(new BoxLayout(scorePanel, BoxLayout.Y_AXIS));
        scorePanel.setBackground(Color.WHITE);
        scorePanel.setBorder(BorderFactory.createLineBorder(new Color(200,200,200), 1));
        scorePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        scorePanel.setMaximumSize(new Dimension(280, 80));

        updateScoreBoard();
        rightPanel.add(scorePanel);
        rightPanel.add(Box.createVerticalStrut(20));

        // 3. DICE PANEL
        dicePanel = new DicePanel(dice);
        dicePanel.setBackground(new Color(0,0,0,0));
        dicePanel.setOpaque(false);
        dicePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightPanel.add(dicePanel);
        rightPanel.add(Box.createVerticalStrut(20));

        // 4. BUTTONS PANEL
        JPanel buttonContainer = new JPanel();
        buttonContainer.setLayout(new BoxLayout(buttonContainer, BoxLayout.Y_AXIS));
        buttonContainer.setBackground(new Color(245, 245, 245));
        buttonContainer.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Roll Button
        rollButton = new JButton("ROLL DICE");
        rollButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        rollButton.setBackground(new Color(46, 204, 113));
        rollButton.setForeground(Color.WHITE);
        rollButton.setFocusPainted(false);
        rollButton.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        rollButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        rollButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        rollButton.addActionListener(e -> rollDice());
        buttonContainer.add(rollButton);

        // Play Again Button (Initially Hidden)
        playAgainButton = new JButton("PLAY NEXT ROUND");
        playAgainButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        playAgainButton.setBackground(new Color(52, 152, 219)); // Blue
        playAgainButton.setForeground(Color.WHITE);
        playAgainButton.setFocusPainted(false);
        playAgainButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        playAgainButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        playAgainButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        playAgainButton.setVisible(false); // Hide initially
        playAgainButton.addActionListener(e -> startNextRound());

        buttonContainer.add(Box.createVerticalStrut(10));
        buttonContainer.add(playAgainButton);

        rightPanel.add(buttonContainer);
        rightPanel.add(Box.createVerticalStrut(15));

        // 5. INFO LABEL
        infoLabel = new JLabel("Click Roll to Start");
        infoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        infoLabel.setForeground(Color.DARK_GRAY);
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightPanel.add(infoLabel);
        rightPanel.add(Box.createVerticalStrut(15));

        // 6. LOG AREA
        logArea = new JTextArea(15, 20);
        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        logArea.setBackground(Color.WHITE);
        logArea.setForeground(new Color(50, 50, 50));

        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightPanel.add(scrollPane);

        add(rightPanel, BorderLayout.EAST);

        pack();
        setLocationRelativeTo(null);

        log("🎮 Game started!");
        log("First turn: " + turnManager.getCurrentPlayer().getName());
    }

    private void updateScoreBoard() {
        scorePanel.removeAll();
        java.util.List<Player> players = turnManager.getAllPlayers();

        for (Player p : players) {
            JPanel pRow = new JPanel(new BorderLayout());
            pRow.setBackground(Color.WHITE);
            pRow.setBorder(new EmptyBorder(5, 10, 5, 10));

            JLabel nameLbl = new JLabel("Player " + p.getName());
            nameLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            nameLbl.setForeground(p.getColor().darker());

            JLabel scoreLbl = new JLabel(String.valueOf(p.getScore()) + " pts");
            scoreLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));

            pRow.add(nameLbl, BorderLayout.WEST);
            pRow.add(scoreLbl, BorderLayout.EAST);
            scorePanel.add(pRow);
        }
        scorePanel.revalidate();
        scorePanel.repaint();
    }

    // --- FIX: This method is now CLEAN and inside the class ---
    private void startNextRound() {
        currentRound++;
        setTitle("Snake & Ladder Adventure - Round " + currentRound);

        // 1. Reset Logic
        java.util.List<Player> players = turnManager.getAllPlayers();

        for (Player p : players) {
            p.reset(); // This uses the method we added to Player.java
        }

        // 2. UI Reset
        playAgainButton.setVisible(false);
        rollButton.setVisible(true);
        rollButton.setEnabled(true);
        infoLabel.setText("Round " + currentRound + " Started!");
        log("\n=== ROUND " + currentRound + " STARTED ===");
        log("All players returned to Start.");

        // 3. Repaint board
        boardPanel.repaint();
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

        String direction = isGreen ? "FORWARD ⬆️" : "BACKWARD ⬇️";
        String colorName = isGreen ? "GREEN" : "RED";
        log(currentPlayer.getName() + " rolled " + colorName + " " + steps + " - " + direction);

        if (!isGreen) {
            ArrayList<Integer> backwardPath = new ArrayList<>();
            backwardPath.add(currentPos);

            for (int i = 0; i < steps; i++) {
                int prevPos = currentPlayer.undoStep();
                backwardPath.add(prevPos);
                if (prevPos == 1) break;
            }

            movementManager.setPath(backwardPath);
            isAnimating = true;
            infoLabel.setText("Moving backward...");
            animateMovement(currentPlayer);
            return;
        }

        boolean isPrime = isPrime(currentPos);
        int boardSize = board.getSize();

        // Dijkstra Logic with Bounce Check
        if (isGreen && isPrime && (currentPos + steps <= boardSize)) {
            log("✨ PRIME POSITION (" + currentPos + ")! Dijkstra activated!");

            DijkstraAlgorithm solver = new DijkstraAlgorithm(board);
            ArrayList<Integer> fullPath = solver.getShortestPath(currentPos, boardSize);

            ArrayList<Integer> actualPath = new ArrayList<>();
            actualPath.add(currentPos);

            int stepsTaken = 0;
            for (int i = 1; i < fullPath.size(); i++) {
                if (stepsTaken < steps) {
                    int nextNode = fullPath.get(i);
                    actualPath.add(nextNode);
                    currentPlayer.recordStep(nextNode);
                    stepsTaken++;
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

        // Normal Move with Bounce Back
        ArrayList<Integer> normalPath = new ArrayList<>();
        normalPath.add(currentPos);

        int tempPos = currentPos;
        int moveDir = 1;

        for (int i = 0; i < steps; i++) {
            if (tempPos == boardSize) {
                moveDir = -1;
            }
            else if (tempPos == 1) {
                moveDir = 1;
            }

            tempPos += moveDir;
            normalPath.add(tempPos);

            if (moveDir == 1) {
                currentPlayer.recordStep(tempPos);
            } else {
                currentPlayer.undoStep();
            }
        }

        if (moveDir == -1) {
            log("↩️ Overshot! Bouncing back.");
        }

        movementManager.setPath(normalPath);
        isAnimating = true;
        infoLabel.setText("Moving forward...");
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
        int finalPos = player.getPosition();

        // Score Logic
        int scoreEffect = board.getScoreEffect(finalPos);
        if (scoreEffect != 0) {
            player.addScore(scoreEffect);
            String sign = scoreEffect > 0 ? "+" : "";
            log("⭐ Special Node " + finalPos + "! Score: " + sign + scoreEffect);
            updateScoreBoard();
        }

        log(player.getName() + " landed on " + finalPos);

        // WIN CONDITION
        if (finalPos == board.getSize()) {
            log("🎉🎉🎉 " + player.getName() + " WINS ROUND " + currentRound + "! 🎉🎉🎉");

            JOptionPane.showMessageDialog(this,
                    "🎉 ROUND " + currentRound + " FINISHED!\n\n" +
                            "Winner: " + player.getName() + "\n" +
                            "Total Score: " + player.getScore() + "\n\n" +
                            "Click 'PLAY NEXT ROUND' to continue.",
                    "🏆 ROUND OVER 🏆",
                    JOptionPane.INFORMATION_MESSAGE);

            rollButton.setVisible(false);
            playAgainButton.setVisible(true);
            infoLabel.setText("Round Over. Play Again?");
            return;
        }

        turnManager.nextTurn();
        Player nextPlayer = turnManager.getCurrentPlayer();
        turnLabel.setText("Turn: " + nextPlayer.getName());
        rollButton.setEnabled(true);
        infoLabel.setText(" ");
        log("---");
    }

    static boolean isPrime(int n) {
        if (n <= 1) return false;
        if (n == 2) return true;
        if (n % 2 == 0) return false;
        for (int i = 3; i * i <= n; i += 2) {
            if (n % i == 0) return false;
        }
        return true;
    }

    private void log(String message) {
        logArea.append(message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
}