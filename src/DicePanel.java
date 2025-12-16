import javax.swing.*;
import java.awt.*;

class DicePanel extends JPanel {
    private Dice dice;
    private boolean rolled;

    public DicePanel(Dice dice) {
        this.dice = dice;
        this.rolled = false;
        setPreferredSize(new Dimension(200, 150));
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

        if (!rolled) {
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            g2d.drawString("Click ROLL to start", 25, 75);
            return;
        }

        // Draw single dice in center
        drawDice(g2d, 65, 30, dice);
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
}