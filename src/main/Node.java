package main;

import java.awt.*;
import java.awt.geom.*;

class Node {
    private int id;
    private Point2D.Double position;
    private static final int RADIUS = 25;
    private boolean dragging = false;

    public Node(int id, double x, double y) {
        this.id = id;
        this.position = new Point2D.Double(x, y);
    }

    public int getId() { return id; }
    public Point2D.Double getPosition() { return position; }
    public int getRadius() { return RADIUS; }
    public boolean isDragging() { return dragging; }
    public void setDragging(boolean dragging) { this.dragging = dragging; }

    public void setPosition(double x, double y) {
        this.position.x = x;
        this.position.y = y;
    }

    public boolean contains(int x, int y) {
        double dx = x - position.x;
        double dy = y - position.y;
        return (dx * dx + dy * dy) <= (RADIUS * RADIUS);
    }

    public void draw(Graphics2D g2d) {
        Color nodeColor = dragging ? new Color(255, 140, 0) : new Color(70, 130, 180);
        g2d.setColor(nodeColor);
        g2d.fillOval((int)(position.x - RADIUS), (int)(position.y - RADIUS),
                RADIUS * 2, RADIUS * 2);
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval((int)(position.x - RADIUS), (int)(position.y - RADIUS),
                RADIUS * 2, RADIUS * 2);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        FontMetrics fm = g2d.getFontMetrics();
        String label = String.valueOf(id);
        int textWidth = fm.stringWidth(label);
        int textHeight = fm.getAscent();
        g2d.drawString(label, (int)(position.x - textWidth/2),
                (int)(position.y + textHeight/3));
    }
}