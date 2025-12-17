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
    private JButton statsButton;

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
        soundManager = new SoundManager();
        isAnimating = false;

        setupPlayersInput();
        initGUI();
    }

    private void setupPlayersInput() {
        Color[] playerColors = {
                new Color(231, 76, 60),
                new Color(52, 152, 219),
                new Color(46, 204, 113),
                new Color(241, 196, 15)
        };

        String[] options = {"2 Players", "3 Players", "4 Players"};
        int choice = JOptionPane.showOptionDialog(null,
                "Select Number of Players:",
                "Game Setup",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);

        int numPlayers = choice + 2;
        if (choice == -1) System.exit(0);

        for (int i = 0; i < numPlayers; i++) {
            String defaultName = "Player " + (i + 1);
            String name = JOptionPane.showInputDialog(null,
                    "Enter Name for Player " + (i + 1) + ":",
                    defaultName);

            if (name == null || name.trim().isEmpty()) {
                name = defaultName;
            }
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

        // --- PANEL KANAN ---
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
        turnLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightPanel.add(turnLabel);
        rightPanel.add(Box.createVerticalStrut(20));

        // Scoreboard Label
        JLabel scoreTitle = new JLabel("🏆 SCOREBOARD");
        scoreTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        scoreTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightPanel.add(scoreTitle);
        rightPanel.add(Box.createVerticalStrut(5));

        // Score Panel Area
        scorePanel = new JPanel();
        scorePanel.setLayout(new BoxLayout(scorePanel, BoxLayout.Y_AXIS));
        scorePanel.setBackground(Color.WHITE);
        scorePanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        scorePanel.setMaximumSize(new Dimension(280, 150));
        updateScoreBoard();
        rightPanel.add(scorePanel);
        rightPanel.add(Box.createVerticalStrut(20));

        // Dice Panel
        dicePanel = new DicePanel(dice);
        dicePanel.setOpaque(false);
        dicePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightPanel.add(dicePanel);
        rightPanel.add(Box.createVerticalStrut(20));

        // Buttons Container
        JPanel buttonContainer = new JPanel();
        buttonContainer.setLayout(new BoxLayout(buttonContainer, BoxLayout.Y_AXIS));
        buttonContainer.setBackground(new Color(245, 245, 245));
        buttonContainer.setAlignmentX(Component.CENTER_ALIGNMENT);

        rollButton = new JButton("ROLL DICE");
        setupButtonStyle(rollButton, new Color(46, 204, 113));
        rollButton.addActionListener(e -> rollDice());
        buttonContainer.add(rollButton);

        playAgainButton = new JButton("PLAY NEXT ROUND");
        setupButtonStyle(playAgainButton, new Color(52, 152, 219));
        playAgainButton.setVisible(false);
        playAgainButton.addActionListener(e -> startNextRound());
        buttonContainer.add(Box.createVerticalStrut(10));
        buttonContainer.add(playAgainButton);

        statsButton = new JButton("📊 VIEW STATS");
        setupButtonStyle(statsButton, new Color(155, 89, 182));
        statsButton.addActionListener(e -> showStatistics());
        buttonContainer.add(Box.createVerticalStrut(10));
        buttonContainer.add(statsButton);

        rightPanel.add(buttonContainer);
        rightPanel.add(Box.createVerticalStrut(15));

        // Info Label
        infoLabel = new JLabel("Click Roll to Start");
        infoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightPanel.add(infoLabel);

        // Log Area
        logArea = new JTextArea(15, 20);
        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 11));
        JScrollPane scrollPane = new JScrollPane(logArea);
        rightPanel.add(Box.createVerticalStrut(15));
        rightPanel.add(scrollPane);

        add(rightPanel, BorderLayout.EAST);
        pack();
        setLocationRelativeTo(null);
        log("🎮 Game started!");
    }

    private void setupButtonStyle(JButton btn, Color bg) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setMaximumSize(new Dimension(220, 45));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    // --- FIX: Menggunakan Logic CalculatePath yang Benar ---
    private void rollDice() {
        if (isAnimating) return;

        rollButton.setEnabled(false);
        Player currentPlayer = turnManager.getCurrentPlayer();
        int currentPos = currentPlayer.getPosition();

        dice.roll();
        dicePanel.setRolled(true);
        soundManager.playDice();

        int steps = dice.getNumber();
        boolean isGreen = dice.getColor() == Dice.DiceColor.GREEN;

        log(currentPlayer.getName() + " rolled " +
                (isGreen ? "GREEN" : "RED") + " " + steps);

        // Panggil Logic Path Calculation yang sudah ada tapi tidak terpakai
        ArrayList<Integer> path = calculatePath(currentPlayer, currentPos, steps, isGreen);

        movementManager.setPath(path);
        isAnimating = true;
        infoLabel.setText("Rolling dice...");

        delayMovementStart(currentPlayer);
    }

    // ✅ Method Logic Utama (Dijkstra + Bounce)
    private ArrayList<Integer> calculatePath(Player player, int currentPos, int steps, boolean isGreen) {
        ArrayList<Integer> path = new ArrayList<>();
        path.add(currentPos);

        // --- SKENARIO 1: MUNDUR (DADU MERAH) ---
        if (!isGreen) {
            for (int i = 0; i < steps; i++) {
                int prevPos = player.undoStep();
                path.add(prevPos);
                if (prevPos == 1) break;
            }
            infoLabel.setText("Moving backward...");
            return path;
        }

        boolean isPrime = isPrime(currentPos);
        int boardSize = board.getSize();

        // --- SKENARIO 2: DIJKSTRA (HIJAU + PRIMA + TIDAK OVERSHOOT) ---
        if (isPrime && (currentPos + steps <= boardSize)) {
            log("✨ PRIME POSITION (" + currentPos + ")! Dijkstra activated!");

            DijkstraAlgorithm solver = new DijkstraAlgorithm(board);
            ArrayList<Integer> fullPath = solver.getShortestPath(currentPos, boardSize);

            int stepsTaken = 0;
            // Ambil langkah dari path Dijkstra
            for (int i = 1; i < fullPath.size() && stepsTaken < steps; i++) {
                int nextNode = fullPath.get(i);
                path.add(nextNode);
                player.recordStep(nextNode); // Update history agar sinkron
                stepsTaken++;
            }
            infoLabel.setText("Moving via Dijkstra...");
            return path;
        }

        // --- SKENARIO 3: NORMAL MAJU + PANTUL (BOUNCE) ---
        int tempPos = currentPos;
        int moveDir = 1;

        for (int i = 0; i < steps; i++) {
            if (tempPos == boardSize) {
                moveDir = -1; // Memantul mundur
            } else if (tempPos == 1) {
                moveDir = 1;
            }

            tempPos += moveDir;
            path.add(tempPos);

            if (moveDir == 1) {
                player.recordStep(tempPos);
            } else {
                player.undoStep(); // Hapus history saat memantul
            }
        }

        if (moveDir == -1) {
            log("↩ Overshot! Bouncing back.");
        }
        infoLabel.setText("Moving forward...");

        return path;
    }

    private void delayMovementStart(Player player) {
        // Tunggu sound dadu selesai + sedikit buffer
        long diceDuration = soundManager.getDiceDuration();
        int delay = (int) Math.min(diceDuration, 1000);

        Timer startTimer = new Timer(delay, e -> {
            ((Timer) e.getSource()).stop();
            animateMovement(player);
        });
        startTimer.setRepeats(false);
        startTimer.start();
    }

    private void animateMovement(Player player) {
        animationTimer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Integer next = movementManager.popNextPosition();

                if (next != null) {
                    player.setPosition(next); // Update posisi visual & logic
                    boardPanel.repaint();
                    soundManager.playMove();
                    infoLabel.setText("Position: " + next);
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

        // 1. Cek Score Effect
        int scoreEffect = board.getScoreEffect(finalPos);
        if (scoreEffect != 0) {
            player.addScore(scoreEffect);
            String sign = scoreEffect > 0 ? "+" : "";
            log("⭐ Node " + finalPos + ": " + sign + scoreEffect + " pts");
            updateScoreBoard();
        }

        // 2. Cek Menang
        if (finalPos == board.getSize()) {
            soundManager.playVictory();
            player.addWin();
            log("🎉 " + player.getName() + " WINS ROUND " + currentRound + "!");
            updateScoreBoard();

            JOptionPane.showMessageDialog(this,
                    "🎉 CONGRATULATIONS!\n\n" +
                            "Winner: " + player.getName() + "\n" +
                            "Total Score: " + player.getScore() + "\n" +
                            "Total Wins: " + player.getWins(),
                    "Victory!",
                    JOptionPane.INFORMATION_MESSAGE);

            rollButton.setVisible(false);
            playAgainButton.setVisible(true);
            infoLabel.setText("Round " + currentRound + " finished!");
            showStatistics();
            return;
        }

        // 3. Ganti Giliran
        turnManager.nextTurn();
        Player nextPlayer = turnManager.getCurrentPlayer();
        turnLabel.setText("Turn: " + nextPlayer.getName());
        rollButton.setEnabled(true);
        infoLabel.setText(nextPlayer.getName() + "'s turn");
        log("---");
    }

    private void startNextRound() {
        currentRound++;
        setTitle("Snake & Ladder Adventure - Round " + currentRound);

        for (Player p : turnManager.getAllPlayers()) {
            p.reset();
        }

        playAgainButton.setVisible(false);
        rollButton.setVisible(true);
        rollButton.setEnabled(true);
        dicePanel.setRolled(false);
        dicePanel.repaint();
        boardPanel.repaint();

        infoLabel.setText("Round " + currentRound + " started!");
        log("\n🔄 Round " + currentRound + " started!");
        updateScoreBoard();
    }

    private void showStatistics() {
        JOptionPane.showMessageDialog(this,
                GameStats.getTop3Stats(turnManager.getAllPlayers()),
                "Leaderboard",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateScoreBoard() {
        scorePanel.removeAll();

        List<Player> players = turnManager.getAllPlayers();
        List<Player> displayList = new ArrayList<>(players);
        displayList.sort((p1, p2) -> Integer.compare(p2.getScore(), p1.getScore()));

        for (Player p : displayList) {
            JPanel playerRow = new JPanel(new BorderLayout());
            playerRow.setBackground(Color.WHITE);
            playerRow.setBorder(new EmptyBorder(2, 5, 2, 5));
            playerRow.setMaximumSize(new Dimension(260, 25));

            JLabel nameLabel = new JLabel(p.getName());
            nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            nameLabel.setForeground(p.getColor().darker());

            JLabel statsLabel = new JLabel(p.getScore() + " pts | " + p.getWins() + " W");
            statsLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

            playerRow.add(nameLabel, BorderLayout.WEST);
            playerRow.add(statsLabel, BorderLayout.EAST);
            scorePanel.add(playerRow);
        }

        scorePanel.revalidate();
        scorePanel.repaint();
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

    private void log(String msg) {
        logArea.append(msg + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
}