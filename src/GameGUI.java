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
    private SoundManager soundManager;

    private BoardPanel boardPanel;
    private DicePanel dicePanel;
    private JButton rollButton;
    private JButton playAgainButton;
    private JButton statsButton; // NEW: Stats Button

    private JLabel infoLabel;
    private JLabel turnLabel;
    private JTextArea logArea;
    private JPanel scorePanel;

    private boolean isAnimating;
    private Timer animationTimer;
    private int currentRound = 1;

    public GameGUI() {
        // Initialize Game Logic
        board = new Board();
        turnManager = new TurnManager();
        movementManager = new MovementManager();
        dice = new Dice();
        isAnimating = false;

        // 1. Setup Players (Input Dialogs)
        setupPlayersInput();

        // 2. Build UI
        initGUI();
    }

    // --- NEW: DYNAMIC PLAYER SETUP ---
    private void setupPlayersInput() {
        // Colors for up to 4 players
        Color[] playerColors = {
                new Color(231, 76, 60),   // Red
                new Color(52, 152, 219),  // Blue
                new Color(46, 204, 113),  // Green
                new Color(241, 196, 15)   // Yellow
        };

        // 1. Ask Number of Players
        String[] options = {"2 Players", "3 Players", "4 Players"};
        int choice = JOptionPane.showOptionDialog(null,
                "Select Number of Players:",
                "Game Setup",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);

        int numPlayers = choice + 2; // Index 0 -> 2 players, etc.
        if (choice == -1) System.exit(0); // Exit if closed

        // 2. Ask Names
        for (int i = 0; i < numPlayers; i++) {
            String defaultName = "Player " + (i + 1);
            String name = JOptionPane.showInputDialog(null,
                    "Enter Name for Player " + (i + 1) + ":",
                    defaultName);

            if (name == null || name.trim().isEmpty()) {
                name = defaultName;
            }

            // Create Player
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

        // Turn Label
        turnLabel = new JLabel("Turn: " + turnManager.getCurrentPlayer().getName());
        turnLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        turnLabel.setForeground(new Color(44, 62, 80));
        turnLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightPanel.add(turnLabel);
        rightPanel.add(Box.createVerticalStrut(20));

        // Scoreboard
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

        // Flexible height based on player count
        scorePanel.setMaximumSize(new Dimension(280, 150));

        updateScoreBoard();
        rightPanel.add(scorePanel);
        rightPanel.add(Box.createVerticalStrut(20));

        // Dice Panel
        dicePanel = new DicePanel(dice);
        dicePanel.setBackground(new Color(0,0,0,0));
        dicePanel.setOpaque(false);
        dicePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightPanel.add(dicePanel);
        rightPanel.add(Box.createVerticalStrut(20));

        // Buttons Container
        JPanel buttonContainer = new JPanel();
        buttonContainer.setLayout(new BoxLayout(buttonContainer, BoxLayout.Y_AXIS));
        buttonContainer.setBackground(new Color(245, 245, 245));
        buttonContainer.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Roll Button
        rollButton = new JButton("ROLL DICE");
        setupButtonStyle(rollButton, new Color(46, 204, 113));
        rollButton.addActionListener(e -> rollDice());
        buttonContainer.add(rollButton);

        // Play Again Button (Hidden)
        playAgainButton = new JButton("PLAY NEXT ROUND");
        setupButtonStyle(playAgainButton, new Color(52, 152, 219));
        playAgainButton.setVisible(false);
        playAgainButton.addActionListener(e -> startNextRound());
        buttonContainer.add(Box.createVerticalStrut(10));
        buttonContainer.add(playAgainButton);

        // NEW: Stats Button
        statsButton = new JButton("📊 VIEW STATS");
        setupButtonStyle(statsButton, new Color(155, 89, 182)); // Purple
        statsButton.addActionListener(e -> showStatistics());
        buttonContainer.add(Box.createVerticalStrut(10));
        buttonContainer.add(statsButton);

        rightPanel.add(buttonContainer);
        rightPanel.add(Box.createVerticalStrut(15));

        // Info Label
        infoLabel = new JLabel("Click Roll to Start");
        infoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        infoLabel.setForeground(Color.DARK_GRAY);
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightPanel.add(infoLabel);
        rightPanel.add(Box.createVerticalStrut(15));

        // Log Area
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
        log("Players: " + turnManager.getAllPlayers().size());
    }

    // Helper to style buttons
    private void setupButtonStyle(JButton btn, Color bg) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Fix max width for alignment
        btn.setMaximumSize(new Dimension(220, 45));
    }

    // --- NEW: Show Stats Logic ---
    private void showStatistics() {
        String stats = GameStats.getTop3Stats(turnManager.getAllPlayers());
        JOptionPane.showMessageDialog(this, stats, "🏆 Leaderboard", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateScoreBoard() {
        scorePanel.removeAll();
        java.util.List<Player> players = turnManager.getAllPlayers();

        for (Player p : players) {
            JPanel pRow = new JPanel(new BorderLayout());
            pRow.setBackground(Color.WHITE);
            pRow.setBorder(new EmptyBorder(5, 10, 5, 10));

            JLabel nameLbl = new JLabel(p.getName());
            nameLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            nameLbl.setForeground(p.getColor().darker());

            // Show Score and Wins
            JLabel scoreLbl = new JLabel(p.getScore() + " pts (" + p.getWins() + " W)");
            scoreLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));

            pRow.add(nameLbl, BorderLayout.WEST);
            pRow.add(scoreLbl, BorderLayout.EAST);
            scorePanel.add(pRow);
        }
        scorePanel.revalidate();
        scorePanel.repaint();
    }

    private void startNextRound() {
        currentRound++;
        setTitle("Snake & Ladder Adventure - Round " + currentRound);

        java.util.List<Player> players = turnManager.getAllPlayers();
        for (Player p : players) {
            p.reset();
        }

        playAgainButton.setVisible(false);
        rollButton.setVisible(true);
        rollButton.setEnabled(true);
        infoLabel.setText("Round " + currentRound + " Started!");
        log("\n=== ROUND " + currentRound + " STARTED ===");
        log("All players returned to Start.");

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

        String direction = isGreen ? "FORWARD ⬆" : "BACKWARD ⬇";
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
            log("↩ Overshot! Bouncing back.");
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

        int scoreEffect = board.getScoreEffect(finalPos);
        if (scoreEffect != 0) {
            player.addScore(scoreEffect);
            String sign = scoreEffect > 0 ? "+" : "";
            log("⭐ Special Node " + finalPos + "! Score: " + sign + scoreEffect);
            updateScoreBoard();
        }

        log(player.getName() + " landed on " + finalPos);

        if (finalPos == board.getSize()) {
            // NEW: Add win count
            player.addWin();
            updateScoreBoard(); // Update UI to show new win count

            log("🎉🎉🎉 " + player.getName() + " WINS ROUND " + currentRound + "! 🎉🎉🎉");

            JOptionPane.showMessageDialog(this,
                    "🎉 ROUND " + currentRound + " FINISHED!\n\n" +
                            "Winner: " + player.getName() + "\n" +
                            "Total Score: " + player.getScore() + "\n" +
                            "Total Wins: " + player.getWins() + "\n\n" +
                            "Click 'PLAY NEXT ROUND' to continue.",
                    "🏆 ROUND OVER 🏆",
                    JOptionPane.INFORMATION_MESSAGE);

            rollButton.setVisible(false);
            playAgainButton.setVisible(true);
            infoLabel.setText("Round Over. Play Again?");

            // Show stats automatically at end of round? Optional.
            // showStatistics();
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