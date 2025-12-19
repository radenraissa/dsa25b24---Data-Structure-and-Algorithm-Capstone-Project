import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.PriorityQueue;

public class PacmanMSTControl extends JFrame {

    private GamePanel gamePanel;
    private SidePanel sidePanel;
    private JButton btnBFS, btnDFS, btnWeighted;

    private PacmanSoundManager soundManager;

    public PacmanMSTControl() {
        setTitle("Pacman: Algorithm & Graph Analysis Tool");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        soundManager = new PacmanSoundManager();
        soundManager.startTheme();

        sidePanel = new SidePanel();
        gamePanel = new GamePanel(this, sidePanel);

        add(gamePanel, BorderLayout.CENTER);
        add(sidePanel, BorderLayout.EAST);

        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(Color.BLACK);
        controlPanel.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(50, 50, 50)));

        Color neonGreen = new Color(57, 255, 20);
        Color neonBlue = new Color(20, 200, 255);
        Color neonPurple = new Color(200, 50, 255);
        Font buttonFont = new Font("Monospaced", Font.BOLD, 12);

        btnBFS = createButton("BFS (Tree)", neonGreen, buttonFont);
        btnDFS = createButton("DFS (Tree)", neonGreen, buttonFont);
        btnWeighted = createButton("WEIGHTED (Graph)", neonBlue, buttonFont);
        JButton btnNewMap = createButton("NEW MAZE", neonPurple, buttonFont);

        JButton btnBack = createButton("⬅ BACK", Color.GRAY, buttonFont);

        // Actions
        btnBFS.addActionListener(e -> {
            gamePanel.restorePerfectMaze();
            sidePanel.configureMode(false);
            startGame("BFS");
        });

        btnDFS.addActionListener(e -> {
            gamePanel.restorePerfectMaze();
            sidePanel.configureMode(false);
            startGame("DFS");
        });

        btnWeighted.addActionListener(e -> {
            gamePanel.enableWeightedMode();
            sidePanel.configureMode(true);
            sidePanel.setLegendVisible(true);

            sidePanel.addLog(">> Weighted Mode Activated.");
            sidePanel.addLog(">> Terrain Added (Mud/Water).");
            sidePanel.addLog(">> Click Board to set Start/End.");
        });

        btnNewMap.addActionListener(e -> {
            gamePanel.generateNewBoard();
            enableButtons(true);
            sidePanel.resetStats();
            sidePanel.addLog(">> New Maze Generated.");
        });


        btnBack.addActionListener(e -> {
            // 2. Stop theme song sebelum keluar
            soundManager.stopTheme();

            new Main().setVisible(true); // Open Main Menu
            this.dispose(); // Close Pacman Window
        });

        controlPanel.add(btnBack);
        controlPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        controlPanel.add(btnBFS);
        controlPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        controlPanel.add(btnDFS);
        controlPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        controlPanel.add(btnWeighted);
        controlPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        controlPanel.add(btnNewMap);

        add(controlPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JButton createButton(String text, Color color, Font font) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(Color.BLACK);
        btn.setFont(font);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void enableButtons(boolean val) {
        btnBFS.setEnabled(val);
        btnDFS.setEnabled(val);
        btnWeighted.setEnabled(val);
    }

    private void startGame(String algorithm) {
        if(gamePanel.isSelectingPoints) {
            JOptionPane.showMessageDialog(this, "Finish selecting points first!");
            return;
        }
        enableButtons(false);
        sidePanel.resetStats();
        sidePanel.updateAlgorithm(algorithm);

        sidePanel.configureMode(gamePanel.isWeightedMode);
        sidePanel.addLog(">> Calculating path...");

        soundManager.startMove();

        gamePanel.startSimulation(algorithm);
    }

    public void onGameFinished() {
        soundManager.stopMove();
        soundManager.playFinish();

        sidePanel.updateStatus("FINISHED");
        sidePanel.addLog(">> Goal Reached!");

        if(gamePanel.getMazePlayer() != null) {
            if(gamePanel.isWeightedMode) {
                int steps = gamePanel.getMazePlayer().historyStack.size();
                int cost = gamePanel.getMazePlayer().totalTravelCost;
                sidePanel.updateMetrics(steps, cost);
                sidePanel.addLog(">> Final Cost: " + cost);
            }
        }

        Object[] options = {"Try Other Algo (Same Map)", "New Map / Reset", "Back to Menu"};
        int choice = JOptionPane.showOptionDialog(this,
                "Destination Reached!\nCheck the dashboard for stats.\nWhat next?",
                "Analysis Finished",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);

        if (choice == 0) {
            gamePanel.softResetPlayer();
            sidePanel.addLog(">> Position Reset.");

            if(gamePanel.isWeightedMode) {
                String[] algoOptions = {"Dijkstra", "A* (A-Star)"};
                int algoChoice = JOptionPane.showOptionDialog(this,
                        "Map & Points Preserved.\nChoose Rival Algorithm:", "Compare Algorithms",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, algoOptions, algoOptions[0]);

                String algo = (algoChoice == 1) ? "A*" : "Dijkstra";
                sidePanel.resetStats();
                sidePanel.configureMode(true);
                sidePanel.updateAlgorithm(algo);

                soundManager.startMove();
                gamePanel.startSimulation(algo);
            } else {
                enableButtons(true);
            }
        } else if (choice == 1) {
            gamePanel.generateNewBoard();
            sidePanel.resetStats();
            enableButtons(true);
        } else {
            // Exit Logic -> Back to Menu
            soundManager.stopTheme(); // Pastikan stop theme saat exit
            new Main().setVisible(true);
            this.dispose();
        }
    }
}

class SidePanel extends JPanel {
    private JLabel lblAlgo, lblStatus;
    private JLabel lblPathTitle;
    private JTextArea pathArea;
    private JLabel lblSteps, lblCost;
    private JTextArea logArea;
    private JPanel legendPanel;

    public SidePanel() {
        setPreferredSize(new Dimension(240, 0));
        setBackground(new Color(30, 30, 30));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("STATISTICS");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(title);
        add(Box.createRigidArea(new Dimension(0, 20)));

        lblAlgo = createInfoLabel("Algorithm: -", Color.CYAN);
        lblStatus = createInfoLabel("Status: IDLE", Color.GRAY);
        add(lblAlgo);
        add(Box.createRigidArea(new Dimension(0, 5)));
        add(lblStatus);
        add(Box.createRigidArea(new Dimension(0, 20)));

        lblPathTitle = createInfoLabel("Node Visit Order:", Color.MAGENTA);
        add(lblPathTitle);
        add(Box.createRigidArea(new Dimension(0, 5)));

        pathArea = new JTextArea("-");
        pathArea.setEditable(false);
        pathArea.setLineWrap(true);
        pathArea.setWrapStyleWord(true);
        pathArea.setBackground(new Color(30, 30, 30));
        pathArea.setForeground(Color.YELLOW);
        pathArea.setFont(new Font("Monospaced", Font.BOLD, 14));
        pathArea.setBorder(null);
        pathArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        pathArea.setMaximumSize(new Dimension(210, 60));
        add(pathArea);

        add(Box.createRigidArea(new Dimension(0, 15)));

        lblSteps = createInfoLabel("Steps: 0", Color.WHITE);
        lblCost = createInfoLabel("Total Cost: 0", Color.ORANGE);

        add(lblSteps);
        add(Box.createRigidArea(new Dimension(0, 5)));
        add(lblCost);

        add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel lblLog = new JLabel("Event Log:");
        lblLog.setForeground(Color.LIGHT_GRAY);
        lblLog.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(lblLog);
        add(Box.createRigidArea(new Dimension(0, 5)));

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setBackground(new Color(10, 10, 10));
        logArea.setForeground(new Color(0, 255, 0));
        logArea.setFont(new Font("Consolas", Font.PLAIN, 10));
        logArea.setLineWrap(true);

        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setPreferredSize(new Dimension(200, 200));
        scroll.setBorder(new LineBorder(Color.DARK_GRAY));
        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(scroll);

        add(Box.createRigidArea(new Dimension(0, 10)));
        legendPanel = new LegendPanel();
        legendPanel.setVisible(false);
        legendPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(legendPanel);
    }

    private JLabel createInfoLabel(String text, Color c) {
        JLabel l = new JLabel(text);
        l.setForeground(c);
        l.setFont(new Font("Monospaced", Font.BOLD, 12));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    public void configureMode(boolean isWeighted) {
        lblSteps.setVisible(isWeighted);
        lblCost.setVisible(isWeighted);

        if (isWeighted) {
            lblPathTitle.setText("Route:");
            pathArea.setText("Start \u2192 End");
        } else {
            lblPathTitle.setText("Node Visit Order:");
            pathArea.setText("S");
        }
        revalidate();
        repaint();
    }

    public void updateAlgorithm(String algo) {
        lblAlgo.setText("Algorithm: " + algo);
        lblStatus.setText("Status: RUNNING");
        lblStatus.setForeground(Color.YELLOW);
    }

    public void updateStatus(String status) {
        lblStatus.setText("Status: " + status);
        lblStatus.setForeground(status.equals("FINISHED") ? Color.GREEN : Color.WHITE);
    }

    public void updateMetrics(int steps, int cost) {
        if(lblSteps.isVisible()) {
            lblSteps.setText("Steps: " + steps);
            lblCost.setText("Total Cost: " + cost);
        }
    }

    public void updatePathOrder(String pathString) {
        pathArea.setText(pathString);
    }

    public void addLog(String msg) {
        logArea.append(msg + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    public void resetStats() {
        lblAlgo.setText("Algorithm: -");
        lblStatus.setText("Status: IDLE");
        lblStatus.setForeground(Color.GRAY);
        lblSteps.setText("Steps: 0");
        lblCost.setText("Total Cost: 0");
        pathArea.setText("-");
        logArea.setText("");
        setLegendVisible(false);
        lblSteps.setVisible(false);
        lblCost.setVisible(false);
    }

    public void setLegendVisible(boolean visible) {
        legendPanel.setVisible(visible);
        revalidate();
        repaint();
    }
}

class LegendPanel extends JPanel {
    public LegendPanel() {
        setBackground(new Color(30, 30, 30));
        setLayout(new GridLayout(4, 1, 5, 5));
        setBorder(BorderFactory.createTitledBorder(
                new LineBorder(Color.GRAY), "Terrain Legend",
                0, 0, new Font("Arial", Font.BOLD, 10), Color.LIGHT_GRAY));

        add(createItem(new Color(0, 0, 0), "Grass (Cost 1)"));
        add(createItem(new Color(139, 69, 19), "Mud (Cost 5)"));
        add(createItem(new Color(0, 191, 255), "Water (Cost 10)"));
    }

    private JPanel createItem(Color c, String text) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        p.setOpaque(false);
        JPanel colorBox = new JPanel();
        colorBox.setPreferredSize(new Dimension(15, 15));
        colorBox.setBackground(c);
        colorBox.setBorder(new LineBorder(Color.WHITE, 1));

        JLabel l = new JLabel(text);
        l.setForeground(Color.WHITE);
        l.setFont(new Font("Arial", Font.PLAIN, 10));
        p.add(colorBox);
        p.add(l);
        return p;
    }
}

class GamePanel extends JPanel implements ActionListener {
    private final int TILE_SIZE = 25;
    private final int ROWS = 21;
    private final int COLS = 21;
    private final int WALL = 1;
    private final int MUD = 5;
    private final int WATER = 10;
    private final int GRASS = 0;

    private int[][] maze;
    private int[][] perfectMaze;
    private int[][] terrainCosts;

    private MazePlayer player;
    private List<Point> targets = new ArrayList<>();
    private Point startPoint, endPoint;

    private javax.swing.Timer timer;
    private PacmanMSTControl mainFrame;
    private SidePanel sidePanel;

    private String currentInfo = "Select a Mode";

    public boolean isWeightedMode = false;
    public boolean isSelectingPoints = false;
    private int selectionStep = 0;

    private int nextTargetIndex = 0;
    private StringBuilder pathDisplayBuffer;

    public GamePanel(PacmanMSTControl mainFrame, SidePanel sidePanel) {
        this.mainFrame = mainFrame;
        this.sidePanel = sidePanel;
        this.setPreferredSize(new Dimension(COLS * TILE_SIZE, ROWS * TILE_SIZE + 30));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);

        generateNewBoard();

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isSelectingPoints) handleMouseClick(e.getX(), e.getY());
            }
        });
        timer = new javax.swing.Timer(60, this);
    }

    public MazePlayer getMazePlayer() { return player; }

    // Kept getPlayer for backward compatibility inside this file logic
    public MazePlayer getPlayer() { return player; }

    public void generateNewBoard() {
        generateMazePrim();

        perfectMaze = new int[ROWS][COLS];
        for(int i=0; i<ROWS; i++) System.arraycopy(maze[i], 0, perfectMaze[i], 0, COLS);

        generateTerrainData();
        targets.clear();
        resetGameFull();
    }

    public void restorePerfectMaze() {
        for(int i=0; i<ROWS; i++) System.arraycopy(perfectMaze[i], 0, maze[i], 0, COLS);

        isWeightedMode = false;
        isSelectingPoints = false;
        player = null;
        startPoint = null;
        endPoint = null;
        sidePanel.setLegendVisible(false);

        if (targets.isEmpty()) generateTargets();
        repaint();
    }

    public void resetGameFull() {
        restorePerfectMaze();
        player = new MazePlayer(Color.YELLOW, new Point(1,1), "None", ROWS, COLS);
        currentInfo = "Ready. Classic Tree Mode.";
        repaint();
    }

    public void softResetPlayer() {
        Point start = (startPoint != null) ? startPoint : new Point(1,1);
        player = new MazePlayer(Color.YELLOW, start, "None", ROWS, COLS);
        repaint();
    }

    private void handleMouseClick(int pixelX, int pixelY) {
        int col = pixelX / TILE_SIZE;
        int row = pixelY / TILE_SIZE;
        if (row < 0 || row >= ROWS || col < 0 || col >= COLS || maze[row][col] == WALL) return;

        if (selectionStep == 1) {
            startPoint = new Point(col, row);
            repaint();
            currentInfo = "Start set. Now Click for DESTINATION.";
            sidePanel.addLog("> Start Point set at ("+col+","+row+")");
            selectionStep = 2;
        } else if (selectionStep == 2) {
            endPoint = new Point(col, row);
            selectionStep = 0;
            isSelectingPoints = false;
            repaint();
            sidePanel.addLog("> Destination set at ("+col+","+row+")");

            String[] options = {"Dijkstra", "A* (A-Star)"};
            int choice = JOptionPane.showOptionDialog(this,
                    "Locations Set.\nChoose Algorithm:", "Solve Weighted Maze",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            String algo = (choice == 1) ? "A*" : "Dijkstra";

            sidePanel.updateAlgorithm(algo);
            sidePanel.configureMode(true);
            mainFrame.setVisible(true);
            startSimulation(algo);
        }
    }

    public void enableWeightedMode() {
        for(int i=0; i<ROWS; i++) System.arraycopy(perfectMaze[i], 0, maze[i], 0, COLS);
        addRandomLoops();

        isWeightedMode = true;
        player = null;
        targets.clear();
        startPoint = null;
        endPoint = null;
        isSelectingPoints = true;
        selectionStep = 1;
        currentInfo = "MODE: WEIGHTED. Click cell to set START.";
        repaint();
    }

    private void addRandomLoops() {
        Random rand = new Random();
        int loopsToAdd = (ROWS * COLS) / 8;
        for (int i = 0; i < loopsToAdd; i++) {
            int r = rand.nextInt(ROWS - 2) + 1;
            int c = rand.nextInt(COLS - 2) + 1;
            if (maze[r][c] == WALL) {
                boolean connectHorz = (maze[r][c-1] == 0 && maze[r][c+1] == 0);
                boolean connectVert = (maze[r-1][c] == 0 && maze[r+1][c] == 0);
                if (connectHorz || connectVert) maze[r][c] = 0;
            }
        }
    }

    private void generateTerrainData() {
        terrainCosts = new int[ROWS][COLS];
        Random rand = new Random();
        for(int r=0; r<ROWS; r++) {
            for(int c=0; c<COLS; c++) {
                int chance = rand.nextInt(100);
                if(chance < 65) terrainCosts[r][c] = GRASS;
                else if(chance < 85) terrainCosts[r][c] = MUD;
                else terrainCosts[r][c] = WATER;
            }
        }
    }

    private void generateMazePrim() {
        maze = new int[ROWS][COLS];
        for (int i = 0; i < ROWS; i++) Arrays.fill(maze[i], WALL);
        int startX = 1, startY = 1;
        maze[startY][startX] = 0;
        ArrayList<Point> walls = new ArrayList<>();
        addWalls(startX, startY, walls);
        Random rand = new Random();
        while (!walls.isEmpty()) {
            int index = rand.nextInt(walls.size());
            Point wall = walls.remove(index);
            List<Point> neighbors = getUnvisitedNeighbors(wall);
            if (!neighbors.isEmpty()) {
                Point neighbor = neighbors.get(0);
                maze[wall.y][wall.x] = 0;
                maze[neighbor.y][neighbor.x] = 0;
                addWalls(neighbor.x, neighbor.y, walls);
            }
        }
    }

    private void generateTargets() {
        targets.clear();
        List<Point> candidates = new ArrayList<>();
        for (int y = 1; y < ROWS - 1; y++) {
            for (int x = 1; x < COLS - 1; x++) {
                if (maze[y][x] == 0 && !(x == 1 && y == 1)) candidates.add(new Point(x,y));
            }
        }
        Collections.shuffle(candidates);
        int targetCount = Math.min(4, candidates.size());
        for(int i=0; i<targetCount; i++) targets.add(candidates.get(i));
    }

    private void addWalls(int x, int y, ArrayList<Point> walls) {
        if (y - 2 >= 0 && maze[y - 2][x] == 1) walls.add(new Point(x, y - 1));
        if (y + 2 < ROWS && maze[y + 2][x] == 1) walls.add(new Point(x, y + 1));
        if (x - 2 >= 0 && maze[y][x - 2] == 1) walls.add(new Point(x - 1, y));
        if (x + 2 < COLS && maze[y][x + 2] == 1) walls.add(new Point(x + 1, y));
    }

    private List<Point> getUnvisitedNeighbors(Point wall) {
        List<Point> list = new ArrayList<>();
        if (wall.x % 2 == 1) {
            if (wall.y - 1 >= 0 && maze[wall.y - 1][wall.x] == 0 && wall.y + 1 < ROWS && maze[wall.y + 1][wall.x] == 1) list.add(new Point(wall.x, wall.y + 1));
            else if (wall.y + 1 < ROWS && maze[wall.y + 1][wall.x] == 0 && wall.y - 1 >= 0 && maze[wall.y - 1][wall.x] == 1) list.add(new Point(wall.x, wall.y - 1));
        } else {
            if (wall.x - 1 >= 0 && maze[wall.y][wall.x - 1] == 0 && wall.x + 1 < COLS && maze[wall.y][wall.x + 1] == 1) list.add(new Point(wall.x + 1, wall.y));
            else if (wall.x + 1 < COLS && maze[wall.y][wall.x + 1] == 0 && wall.x - 1 >= 0 && maze[wall.y][wall.x - 1] == 1) list.add(new Point(wall.x - 1, wall.y));
        }
        return list;
    }

    public void startSimulation(String algorithm) {
        Color pColor = Color.YELLOW;
        if (algorithm.equals("Dijkstra")) pColor = Color.CYAN;
        if (algorithm.equals("A*")) pColor = Color.MAGENTA;
        if (algorithm.equals("DFS")) pColor = Color.RED;

        currentInfo = "Running: " + algorithm;
        Point s = (isWeightedMode && startPoint != null) ? startPoint : new Point(1,1);
        player = new MazePlayer(pColor, s, algorithm, ROWS, COLS);

        sidePanel.addLog("> Start Simulation: " + algorithm);

        if (isWeightedMode) {
            player.calculateSinglePath(maze, terrainCosts, startPoint, endPoint);
        } else {
            // BFS/DFS Mode
            player.calculateTour(maze, s, targets);
            pathDisplayBuffer = new StringBuilder("S");
            sidePanel.updatePathOrder(pathDisplayBuffer.toString());
            nextTargetIndex = 0;
        }
        timer.start();
        repaint();
    }

    private String getTargetLabel(Point p) {
        for(int i=0; i<targets.size(); i++) {
            if(targets.get(i).equals(p)) {
                return String.valueOf((char)('A' + i));
            }
        }
        return "?";
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                int x = col * TILE_SIZE;
                int y = row * TILE_SIZE;
                if (maze[row][col] == WALL) {
                    g2d.setColor(Color.BLUE);
                    g2d.fillRect(x, y, TILE_SIZE, TILE_SIZE);
                    g2d.setColor(new Color(0, 0, 100));
                    g2d.drawRect(x, y, TILE_SIZE, TILE_SIZE);
                } else {
                    if (isWeightedMode && terrainCosts != null) {
                        int cost = terrainCosts[row][col];
                        if (cost == MUD) g2d.setColor(new Color(139, 69, 19));
                        else if (cost == WATER) g2d.setColor(new Color(0, 191, 255));
                        else g2d.setColor(Color.BLACK);
                        g2d.fillRect(x, y, TILE_SIZE, TILE_SIZE);
                    }
                    g2d.setColor(new Color(255, 184, 151));
                    g2d.fillOval(x + 11, y + 11, 3, 3);
                }
            }
        }
        if (!isWeightedMode && targets != null) {
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            char[] labels = {'A', 'B', 'C', 'D'};
            for (int i = 0; i < targets.size(); i++) {
                Point t = targets.get(i);
                g2d.setColor(Color.WHITE);
                if (player != null && player.historyStack.contains(t)) g2d.setColor(Color.GRAY);
                if (i < labels.length) g2d.drawString(String.valueOf(labels[i]), t.x * TILE_SIZE + 8, t.y * TILE_SIZE + 18);
            }
        }
        if (isWeightedMode) {
            if(startPoint != null) { g2d.setColor(Color.GREEN); g2d.drawString("S", startPoint.x * TILE_SIZE + 8, startPoint.y * TILE_SIZE + 18); }
            if(endPoint != null) { g2d.setColor(Color.RED); g2d.drawString("E", endPoint.x * TILE_SIZE + 8, endPoint.y * TILE_SIZE + 18); }
        } else {
            g2d.setColor(Color.GREEN); g2d.drawString("S", 1 * TILE_SIZE + 8, 1 * TILE_SIZE + 18);
        }
        if (player != null) player.draw(g2d, TILE_SIZE);
        g2d.setColor(Color.WHITE);
        g2d.drawString(currentInfo, 10, ROWS * TILE_SIZE + 20);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (player != null && !player.finished && !player.algoType.equals("None")) {
            boolean moved = player.moveNextStep();

            if (isWeightedMode) {
                sidePanel.updateMetrics(player.historyStack.size(), player.totalTravelCost);
            } else {
                if(player.targetSequence != null && nextTargetIndex < player.targetSequence.size()) {
                    Point nextTarget = player.targetSequence.get(nextTargetIndex);
                    if(player.position.equals(nextTarget)) {
                        String label = getTargetLabel(nextTarget);
                        pathDisplayBuffer.append(" \u2192 ").append(label);
                        sidePanel.updatePathOrder(pathDisplayBuffer.toString());
                        nextTargetIndex++;
                    }
                }
            }

            if (!moved) {
                player.finished = true;
                timer.stop();
                mainFrame.onGameFinished();
            }
        }
        repaint();
    }
}

// PLAYER & ALGORITHMS
class MazePlayer {
    Color color;
    Point position;
    Stack<Point> historyStack;
    String algoType;
    List<Point> fullTourPath;

    List<Point> targetSequence = new ArrayList<>();

    int pathIndex = 0;
    boolean finished = false;
    int rows, cols;
    int totalTravelCost = 0;

    public MazePlayer(Color color, Point start, String algoType, int rows, int cols) {
        this.color = color;
        this.position = start;
        this.algoType = algoType;
        this.historyStack = new Stack<>();
        this.fullTourPath = new ArrayList<>();
        this.rows = rows;
        this.cols = cols;
        if(start != null) this.historyStack.push(start);
    }

    public void calculateSinglePath(int[][] maze, int[][] costs, Point start, Point end) {
        if (algoType.equals("Dijkstra")) {
            fullTourPath = getDijkstraPath(maze, costs, start, end);
        } else if (algoType.equals("A*")) {
            fullTourPath = getAStarPath(maze, costs, start, end);
        }
    }

    public void calculateTour(int[][] maze, Point start, List<Point> allTargets) {
        Point current = start;
        List<Point> remainingTargets = new ArrayList<>(allTargets);
        targetSequence.clear();

        while (!remainingTargets.isEmpty()) {
            Point nextTarget;
            if (algoType.equals("BFS")) {
                nextTarget = findClosestTarget(maze, current, remainingTargets);
            } else {
                Collections.shuffle(remainingTargets);
                nextTarget = remainingTargets.get(0);
            }

            targetSequence.add(nextTarget);

            List<Point> segment = (algoType.equals("BFS")) ?
                    getBFSPath(maze, current, nextTarget) : getDFSPath(maze, current, nextTarget);

            if (segment != null) {
                if (!fullTourPath.isEmpty() && !segment.isEmpty() && segment.get(0).equals(current)) {
                    segment.remove(0);
                }
                fullTourPath.addAll(segment);
                current = nextTarget;
                remainingTargets.remove(nextTarget);
            }
        }
        this.totalTravelCost = fullTourPath.size();
    }

    private List<Point> getBFSPath(int[][] maze, Point start, Point end) {
        Queue<Point> queue = new LinkedList<>();
        queue.add(start);
        boolean[][] visited = new boolean[rows][cols];
        visited[start.y][start.x] = true;
        Map<Point, Point> parents = new HashMap<>();

        while (!queue.isEmpty()) {
            Point curr = queue.poll();
            if (curr.equals(end)) break;
            int[] dx = {0, 0, -1, 1}; int[] dy = {-1, 1, 0, 0};
            for (int i = 0; i < 4; i++) {
                int nx = curr.x + dx[i]; int ny = curr.y + dy[i];
                if (isValid(maze, nx, ny) && !visited[ny][nx]) {
                    visited[ny][nx] = true;
                    Point next = new Point(nx, ny);
                    queue.add(next);
                    parents.put(next, curr);
                }
            }
        }
        return reconstructPath(parents, end);
    }

    private Point findClosestTarget(int[][] maze, Point start, List<Point> targets) {
        Point closest = null; int minDist = Integer.MAX_VALUE;
        for (Point t : targets) {
            int d = getBFSPath(maze, start, t).size();
            if (d < minDist && d > 0) { minDist = d; closest = t; }
        }
        return (closest != null) ? closest : targets.get(0);
    }

    private List<Point> getDFSPath(int[][] maze, Point start, Point end) {
        boolean[][] visited = new boolean[rows][cols];
        List<Point> path = new ArrayList<>();
        dfsHelper(maze, start, end, visited, path);
        return path;
    }

    private boolean dfsHelper(int[][] maze, Point current, Point end, boolean[][] visited, List<Point> path) {
        path.add(current);
        visited[current.y][current.x] = true;
        if (current.equals(end)) return true;
        ArrayList<Integer> dirs = new ArrayList<>(Arrays.asList(0, 1, 2, 3));
        Collections.shuffle(dirs);
        int[] dx = {0, 0, -1, 1}; int[] dy = {-1, 1, 0, 0};
        for (int d : dirs) {
            int nx = current.x + dx[d]; int ny = current.y + dy[d];
            if (isValid(maze, nx, ny) && !visited[ny][nx]) {
                if (dfsHelper(maze, new Point(nx, ny), end, visited, path)) return true;
            }
        }
        path.remove(path.size() - 1);
        return false;
    }

    private List<Point> getDijkstraPath(int[][] maze, int[][] costs, Point start, Point end) {
        int size = rows * cols;
        int[] dist = new int[size];
        Arrays.fill(dist, Integer.MAX_VALUE);
        int[] prev = new int[size];
        Arrays.fill(prev, -1);
        boolean[] isVisited = new boolean[size];
        int startIndex = start.y * cols + start.x;
        int endIndex = end.y * cols + end.x;
        dist[startIndex] = 0;

        for (int i = 0; i < size; i++) {
            int u = -1; int min = Integer.MAX_VALUE;
            for (int k = 0; k < size; k++) {
                if (!isVisited[k] && dist[k] < min) { min = dist[k]; u = k; }
            }
            if (u == -1 || dist[u] == Integer.MAX_VALUE) break;
            if (u == endIndex) break;
            isVisited[u] = true;

            int uy = u / cols; int ux = u % cols;
            int[] dx = {0, 0, -1, 1}; int[] dy = {-1, 1, 0, 0};
            for (int k = 0; k < 4; k++) {
                int nx = ux + dx[k]; int ny = uy + dy[k];
                if (isValid(maze, nx, ny)) {
                    int v = ny * cols + nx;
                    int weight = (costs[ny][nx] == 0) ? 1 : costs[ny][nx];
                    if (!isVisited[v] && dist[u] + weight < dist[v]) {
                        dist[v] = dist[u] + weight;
                        prev[v] = u;
                    }
                }
            }
        }
        this.totalTravelCost = (dist[endIndex] == Integer.MAX_VALUE) ? 0 : dist[endIndex];

        List<Point> path = new ArrayList<>();
        int curr = endIndex;
        if (dist[endIndex] == Integer.MAX_VALUE) return path;
        while (curr != -1) {
            int cy = curr / cols; int cx = curr % cols;
            path.add(new Point(cx, cy));
            curr = prev[curr];
        }
        Collections.reverse(path);
        return path;
    }

    class Node implements Comparable<Node> {
        Point pt; int cost, priority;
        public Node(Point pt, int cost, int priority) { this.pt = pt; this.cost = cost; this.priority = priority; }
        public int compareTo(Node other) { return Integer.compare(this.priority, other.priority); }
    }

    private List<Point> getAStarPath(int[][] maze, int[][] costs, Point start, Point end) {
        PriorityQueue<Node> pq = new PriorityQueue<>();
        pq.add(new Node(start, 0, 0));
        Map<Point, Point> parents = new HashMap<>();
        Map<Point, Integer> dist = new HashMap<>();
        dist.put(start, 0);

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            Point cp = current.pt;
            if (cp.equals(end)) break;

            int[] dx = {0, 0, -1, 1}; int[] dy = {-1, 1, 0, 0};
            for (int i = 0; i < 4; i++) {
                int nx = cp.x + dx[i]; int ny = cp.y + dy[i];
                Point np = new Point(nx, ny);
                if (isValid(maze, nx, ny)) {
                    int moveCost = (costs[ny][nx] == 0) ? 1 : costs[ny][nx];
                    int newCost = dist.get(cp) + moveCost;
                    if (newCost < dist.getOrDefault(np, Integer.MAX_VALUE)) {
                        dist.put(np, newCost);
                        parents.put(np, cp);
                        int heuristic = Math.abs(nx - end.x) + Math.abs(ny - end.y);
                        pq.add(new Node(np, newCost, newCost + heuristic));
                    }
                }
            }
        }
        this.totalTravelCost = dist.getOrDefault(end, 0);
        return reconstructPath(parents, end);
    }

    private List<Point> reconstructPath(Map<Point, Point> parents, Point end) {
        List<Point> path = new ArrayList<>();
        Point curr = end;
        while (curr != null) { path.add(curr); curr = parents.get(curr); }
        Collections.reverse(path);
        return path;
    }

    private boolean isValid(int[][] maze, int x, int y) {
        return x >= 0 && y >= 0 && x < cols && y < rows && maze[y][x] != 1;
    }

    public boolean moveNextStep() {
        if (fullTourPath != null && pathIndex < fullTourPath.size()) {
            Point p = fullTourPath.get(pathIndex);
            this.position = p;
            this.historyStack.push(p);
            pathIndex++;
            return true;
        }
        return false;
    }

    public void draw(Graphics2D g2, int size) {
        g2.setColor(this.color);
        g2.setStroke(new BasicStroke(3));
        if (historyStack.size() > 1) {
            for(int i=0; i<historyStack.size()-1; i++) {
                Point p1 = historyStack.get(i); Point p2 = historyStack.get(i+1);
                if (Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y) <= 1)
                    g2.drawLine(p1.x*size + size/2, p1.y*size + size/2, p2.x*size + size/2, p2.y*size + size/2);
            }
        }
        int p = 4;
        if(algoType.equals("DFS")) g2.fillRoundRect(position.x*size+p, position.y*size+p, size-p*2, size-p*2, 10, 10);
        else g2.fillArc(position.x*size+p, position.y*size+p, size-p*2, size-p*2, (pathIndex%2==0)?45:10, 300);
    }
}