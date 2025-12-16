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

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(new Color(245, 245, 245));
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 2, 0, 0, new Color(189, 195, 199)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        rightPanel.setPreferredSize(new Dimension(300, 0));

        turnLabel = new JLabel("Turn: " + turnManager.getCurrentPlayer().getName());
        turnLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        turnLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightPanel.add(turnLabel);
        rightPanel.add(Box.createVerticalStrut(20));

        JLabel scoreTitle = new JLabel("🏆 SCOREBOARD");
        scoreTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightPanel.add(scoreTitle);
        rightPanel.add(Box.createVerticalStrut(5));

        scorePanel = new JPanel();
        scorePanel.setLayout(new BoxLayout(scorePanel, BoxLayout.Y_AXIS));
        scorePanel.setBackground(Color.WHITE);
        scorePanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        scorePanel.setMaximumSize(new Dimension(280, 150));
        updateScoreBoard();
        rightPanel.add(scorePanel);
        rightPanel.add(Box.createVerticalStrut(20));

        dicePanel = new DicePanel(dice);
        dicePanel.setOpaque(false);
        dicePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightPanel.add(dicePanel);
        rightPanel.add(Box.createVerticalStrut(20));

        rollButton = new JButton("ROLL DICE");
        setupButtonStyle(rollButton, new Color(46, 204, 113));
        rollButton.addActionListener(e -> rollDice());
        rightPanel.add(rollButton);

        playAgainButton = new JButton("PLAY NEXT ROUND");
        setupButtonStyle(playAgainButton, new Color(52, 152, 219));
        playAgainButton.setVisible(false);
        playAgainButton.addActionListener(e -> startNextRound());
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(playAgainButton);

        statsButton = new JButton("📊 VIEW STATS");
        setupButtonStyle(statsButton, new Color(155, 89, 182));
        statsButton.addActionListener(e -> showStatistics());
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(statsButton);

        infoLabel = new JLabel("Click Roll to Start");
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightPanel.add(Box.createVerticalStrut(15));
        rightPanel.add(infoLabel);

        logArea = new JTextArea(15, 20);
        logArea.setEditable(false);
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

    private void rollDice() {
        if (isAnimating) return;

        rollButton.setEnabled(false);
        Player currentPlayer = turnManager.getCurrentPlayer();
        int currentPos = currentPlayer.getPosition();

        // Roll dice dan putar sound
        dice.roll();
        dicePanel.setRolled(true);
        dicePanel.repaint();
        soundManager.playDice();

        int steps = dice.getNumber();
        boolean isGreen = dice.getColor() == Dice.DiceColor.GREEN;
        log(currentPlayer.getName() + " rolled " + (isGreen ? "GREEN" : "RED") + " " + steps);

        // Hitung path pergerakan
        ArrayList<Integer> path = new ArrayList<>();
        path.add(currentPos);

        if (!isGreen) {
            // Mundur
            for (int i = 0; i < steps; i++) {
                int prev = currentPlayer.undoStep();
                path.add(prev);
                if (prev == 1) break;
            }
        } else {
            // Maju
            for (int i = 0; i < steps; i++) {
                currentPos++;
                if (currentPos > board.getSize()) {
                    currentPos = board.getSize();
                    break;
                }
                path.add(currentPos);
                currentPlayer.recordStep(currentPos);
            }
        }

        movementManager.setPath(path);
        isAnimating = true;
        infoLabel.setText("Rolling dice...");

        // Delay 1 detik setelah suara dadu selesai, baru mulai gerak
        delayMovementStart(currentPlayer);
    }

    private void delayMovementStart(Player player) {
        // Tunggu sampai sound dadu selesai
        long diceDuration = soundManager.getDiceDuration();

        Timer diceFinishTimer = new Timer((int)diceDuration, e -> {
            ((Timer) e.getSource()).stop();

            // Setelah sound dadu selesai, tunggu 1 detik lagi
            Timer delayTimer = new Timer(10, ev -> {
                ((Timer) ev.getSource()).stop();
                infoLabel.setText("Moving...");
                animateMovement(player);
            });
            delayTimer.setRepeats(false);
            delayTimer.start();
        });
        diceFinishTimer.setRepeats(false);
        diceFinishTimer.start();
    }

    private void animateMovement(Player player) {
        // Timer untuk setiap langkah dengan jeda 0.5 detik
        animationTimer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Integer next = movementManager.popNextPosition();

                if (next != null) {
                    player.setPosition(next);
                    boardPanel.repaint();

                    // Putar sound move untuk setiap langkah
                    soundManager.playMove();
                    infoLabel.setText("Position: " + next);

                } else {
                    // Animasi selesai
                    animationTimer.stop();
                    isAnimating = false;
                    finishTurn(player);
                }
            }
        });
        animationTimer.start();
    }

    private void finishTurn(Player player) {
        // Cek apakah player mencapai finish
        if (player.getPosition() >= board.getSize()) {
            soundManager.playVictory();
            player.addWin();
            log("🎉 " + player.getName() + " wins round " + currentRound + "!");
            updateScoreBoard();

            JOptionPane.showMessageDialog(this,
                    player.getName() + " wins round " + currentRound + "!",
                    "Victory!",
                    JOptionPane.INFORMATION_MESSAGE);

            rollButton.setVisible(false);
            playAgainButton.setVisible(true);
            infoLabel.setText("Round " + currentRound + " finished!");
            return;
        }

        // Ganti giliran ke player berikutnya
        turnManager.nextTurn();
        Player nextPlayer = turnManager.getCurrentPlayer();
        turnLabel.setText("Turn: " + nextPlayer.getName());
        rollButton.setEnabled(true);
        infoLabel.setText(nextPlayer.getName() + "'s turn");
        log("→ " + nextPlayer.getName() + "'s turn");
    }

    private void startNextRound() {
        currentRound++;
        setTitle("Snake & Ladder Adventure - Round " + currentRound);

        // Reset semua player ke posisi awal
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
        log("\n🎮 Round " + currentRound + " started!");
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
        // Sort berdasarkan wins (descending)
        players.sort((p1, p2) -> Integer.compare(p2.getWins(), p1.getWins()));

        for (Player p : players) {
            JPanel playerRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
            playerRow.setBackground(Color.WHITE);
            playerRow.setMaximumSize(new Dimension(260, 30));

            // Color indicator
            JLabel colorBox = new JLabel("  ");
            colorBox.setOpaque(true);
            colorBox.setBackground(p.getColor());
            colorBox.setBorder(BorderFactory.createLineBorder(Color.BLACK));

            JLabel nameLabel = new JLabel(p.getName() + ": " + p.getWins() + " wins");
            nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            playerRow.add(colorBox);
            playerRow.add(nameLabel);
            scorePanel.add(playerRow);
        }

        scorePanel.revalidate();
        scorePanel.repaint();
    }

    private void log(String msg) {
        logArea.append(msg + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
}