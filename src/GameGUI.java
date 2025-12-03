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
        board = new Board(); // Board dengan 74 nodes
        turnManager = new TurnManager();
        movementManager = new MovementManager();
        dice = new Dice();
        isAnimating = false;

        setupPlayers();
        initGUI();

        System.out.println("\n🎮 Game initialized!");
        System.out.println("   Board size: " + board.getSize() + " nodes");
        System.out.println("   Players: " + turnManager.getAllPlayers().size());
        System.out.println("   Ready to play!\n");
    }

    private void setupPlayers() {
        String[] playerNames = {"Red", "Blue", "Green", "Yellow"};
        Color[] playerColors = {
                new Color(231, 76, 60),   // Red
                new Color(52, 152, 219),   // Blue
                new Color(46, 204, 113),   // Green
                new Color(241, 196, 15)    // Yellow
        };

        // Default 2 players
        int numPlayers = 2;

        for (int i = 0; i < numPlayers; i++) {
            String name = String.valueOf((char)('A' + i));
            turnManager.addPlayer(new Player(name, playerColors[i]));
        }
    }

    private void initGUI() {
        setTitle("Modified Snake & Ladder - 74 Nodes Edition");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(48, 71, 186));

        // Board panel (center)
        boardPanel = new BoardPanel(board);
        boardPanel.setPlayers(turnManager.getAllPlayers());
        add(boardPanel, BorderLayout.CENTER);

        // Right control panel
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(new Color(48, 71, 186));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Turn label
        turnLabel = new JLabel("Turn: " + turnManager.getCurrentPlayer().getName());
        turnLabel.setFont(new Font("Arial", Font.BOLD, 20));
        turnLabel.setForeground(Color.WHITE);
        turnLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightPanel.add(turnLabel);
        rightPanel.add(Box.createVerticalStrut(20));

        // Dice panel
        dicePanel = new DicePanel(dice);
        dicePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightPanel.add(dicePanel);
        rightPanel.add(Box.createVerticalStrut(20));

        // Roll button
        rollButton = new JButton("🎲 ROLL DICE");
        rollButton.setFont(new Font("Arial", Font.BOLD, 16));
        rollButton.setBackground(new Color(91, 192, 235));
        rollButton.setForeground(Color.WHITE);
        rollButton.setFocusPainted(false);
        rollButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        rollButton.setMaximumSize(new Dimension(180, 45));
        rollButton.addActionListener(e -> rollDice());
        rightPanel.add(rollButton);
        rightPanel.add(Box.createVerticalStrut(20));

        // Info label
        infoLabel = new JLabel(" ");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        infoLabel.setForeground(Color.WHITE);
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightPanel.add(infoLabel);
        rightPanel.add(Box.createVerticalStrut(20));

        // Log area
        logArea = new JTextArea(15, 20);
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        logArea.setBackground(new Color(44, 62, 80));
        logArea.setForeground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightPanel.add(scrollPane);

        add(rightPanel, BorderLayout.EAST);

        pack();
        setLocationRelativeTo(null);

        log("🎮 Game started!");
        log("First turn: " + turnManager.getCurrentPlayer().getName());
        log("Click 'ROLL DICE' to begin");
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

        // RED DICE - Mundur dengan undo history
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

        // GREEN DICE + PRIME POSITION - Dijkstra aktif
        boolean isPrime = isPrime(currentPos);

        if (isGreen && isPrime) {
            log("✨ PRIME POSITION (" + currentPos + ")! Dijkstra activated!");

            DijkstraAlgorithm solver = new DijkstraAlgorithm(board);
            ArrayList<Integer> fullPath = solver.getShortestPath(currentPos, board.getSize());

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

        // GREEN DICE + NON-PRIME - Maju biasa
        ArrayList<Integer> normalPath = new ArrayList<>();
        normalPath.add(currentPos);

        int tempPos = currentPos;
        for (int i = 0; i < steps; i++) {
            tempPos++;

            if (tempPos > board.getSize()) {
                tempPos--;
                break;
            }

            normalPath.add(tempPos);
            currentPlayer.recordStep(tempPos);
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
        log(player.getName() + " landed on position " + finalPos);

        // Check win condition
        if (finalPos == board.getSize()) {
            log("🎉🎉🎉 " + player.getName() + " WINS! 🎉🎉🎉");
            JOptionPane.showMessageDialog(this,
                    "🎉 Congratulations!\n\n" +
                            player.getName() + " wins the game!\n" +
                            "Final position: " + board.getSize(),
                    "🏆 GAME OVER 🏆",
                    JOptionPane.INFORMATION_MESSAGE);
            rollButton.setEnabled(false);
            return;
        }

        // Next turn
        turnManager.nextTurn();
        Player nextPlayer = turnManager.getCurrentPlayer();
        turnLabel.setText("Turn: " + nextPlayer.getName());
        rollButton.setEnabled(true);
        infoLabel.setText(" ");
        log("---");
    }

    // Check if number is prime
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