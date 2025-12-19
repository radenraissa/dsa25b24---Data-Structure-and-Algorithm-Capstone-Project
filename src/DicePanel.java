import javax.swing.*;
import java.awt.*;

class DicePanel extends JPanel {
    private Dice dice;
    private boolean rolled;
    private static final int DICE_SIZE = 70;
    private static final Color DICE_BG_COLOR = new Color(67, 97, 238); // biru
    private static final int BG_PADDING = 12;
    private Timer rollAnimationTimer;
    private int animationCount = 0;

    public DicePanel(Dice dice) {
        this.dice = dice;
        this.rolled = false;
        setPreferredSize(new Dimension(240, 200));
        setBackground(new Color(67, 97, 238));  // Biru medium (kanan atas)
    }

    public void setRolled(boolean rolled) {
        this.rolled = rolled;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int x = (getWidth() - DICE_SIZE) / 2;
        int y = (getHeight() - DICE_SIZE) / 2 - 10;

// ⬛ background biru di belakang dadu + angka
        int bgY = y - BG_PADDING;
        int bgW = 220;
        int bgX = (getWidth() - bgW) / 2;
        int bgH = DICE_SIZE + 55 + BG_PADDING * 2; // extra buat angka

        g2d.setColor(new Color(65, 166, 239));
        g2d.fillRoundRect(bgX, bgY, bgW, bgH, 20, 20);

        if (!rolled) {
            String text = "Click ROLL to start";
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 14));

            FontMetrics fm = g2d.getFontMetrics();
            int textX = (getWidth() - fm.stringWidth(text)) / 2;
            int textY = (getHeight() + fm.getAscent()) / 2 - 5;

            g2d.drawString(text, textX, textY);
            return;
        }

// 🎲 gambar dadu di atas background
        drawDice(g2d, x, y, dice);
    }

    private void drawDice(Graphics2D g2d, int x, int y, Dice dice) {
        g2d.setColor(dice.getColorObject());
        g2d.fillRoundRect(x, y, 70, 70, 10, 10);
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(x, y, 70, 70, 10, 10);

        g2d.setColor(Color.WHITE);
        int number = dice.getNumber();
        drawDots(g2d, x + 35, y + 35, number);

        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString(String.valueOf(number), x + 28, y + 95);
    }

    private void drawDots(Graphics2D g2d, int cx, int cy, int number) {
        int dotSize = 10;
        int offset = 18;

        switch (number) {
            case 1:
                g2d.fillOval(cx - dotSize/2, cy - dotSize/2, dotSize, dotSize);
                break;
            case 2:
                g2d.fillOval(cx - offset - dotSize/2, cy - offset - dotSize/2, dotSize, dotSize);
                g2d.fillOval(cx + offset - dotSize/2, cy + offset - dotSize/2, dotSize, dotSize);
                break;
            case 3:
                g2d.fillOval(cx - offset - dotSize/2, cy - offset - dotSize/2, dotSize, dotSize);
                g2d.fillOval(cx - dotSize/2, cy - dotSize/2, dotSize, dotSize);
                g2d.fillOval(cx + offset - dotSize/2, cy + offset - dotSize/2, dotSize, dotSize);
                break;
            case 4:
                g2d.fillOval(cx - offset - dotSize/2, cy - offset - dotSize/2, dotSize, dotSize);
                g2d.fillOval(cx + offset - dotSize/2, cy - offset - dotSize/2, dotSize, dotSize);
                g2d.fillOval(cx - offset - dotSize/2, cy + offset - dotSize/2, dotSize, dotSize);
                g2d.fillOval(cx + offset - dotSize/2, cy + offset - dotSize/2, dotSize, dotSize);
                break;
            case 5:
                g2d.fillOval(cx - offset - dotSize/2, cy - offset - dotSize/2, dotSize, dotSize);
                g2d.fillOval(cx + offset - dotSize/2, cy - offset - dotSize/2, dotSize, dotSize);
                g2d.fillOval(cx - dotSize/2, cy - dotSize/2, dotSize, dotSize);
                g2d.fillOval(cx - offset - dotSize/2, cy + offset - dotSize/2, dotSize, dotSize);
                g2d.fillOval(cx + offset - dotSize/2, cy + offset - dotSize/2, dotSize, dotSize);
                break;
            case 6:
                g2d.fillOval(cx - offset - dotSize/2, cy - offset - dotSize/2, dotSize, dotSize);
                g2d.fillOval(cx + offset - dotSize/2, cy - offset - dotSize/2, dotSize, dotSize);
                g2d.fillOval(cx - offset - dotSize/2, cy - dotSize/2, dotSize, dotSize);
                g2d.fillOval(cx + offset - dotSize/2, cy - dotSize/2, dotSize, dotSize);
                g2d.fillOval(cx - offset - dotSize/2, cy + offset - dotSize/2, dotSize, dotSize);
                g2d.fillOval(cx + offset - dotSize/2, cy + offset - dotSize/2, dotSize, dotSize);
                break;
        }
    }

    public void playRollAnimation(Runnable onFinish) {
        animationCount = 0;
        rolled = true;

        rollAnimationTimer = new Timer(200, e -> {
            dice.forceRandomDisplay();

            repaint();
            animationCount++;

            if (animationCount >= 10) {
                rollAnimationTimer.stop();
                onFinish.run(); // balik ke GameGUI
            }
        });

        rollAnimationTimer.start();
    }

}