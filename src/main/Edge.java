package main;

import java.awt.*;
import java.awt.geom.*;

class Edge {
    private Node source;
    private Node target;
    private boolean directed;
    private int weight;

    public Edge(Node source, Node target, int weight, boolean directed) {
        this.source = source;
        this.target = target;
        this.weight = weight;
        this.directed = directed;
    }

    public void draw(Graphics2D g2d) {
        Point2D.Double start = source.getPosition();
        Point2D.Double end = target.getPosition();

        // Draw edge line
        g2d.setColor(new Color(100, 100, 100));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine((int)start.x, (int)start.y, (int)end.x, (int)end.y);

        // Draw arrow if directed
        if (directed) {
            drawArrow(g2d, start, end);
        }

        // Draw weight label with "km" unit
        int midX = (int)((start.x + end.x) / 2);
        int midY = (int)((start.y + end.y) / 2);

        // Draw background for weight label (larger for "km" text)
        g2d.setColor(new Color(220, 20, 60));
        g2d.fillRoundRect(midX - 22, midY - 12, 44, 24, 8, 8);
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(midX - 22, midY - 12, 44, 24, 8, 8);

        // Draw weight with "km"
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 11));
        String weightStr = weight + " km";
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(weightStr);
        g2d.drawString(weightStr, midX - textWidth/2, midY + 4);
    }

    private void drawArrow(Graphics2D g2d, Point2D.Double start, Point2D.Double end) {
        double angle = Math.atan2(end.y - start.y, end.x - start.x);
        int arrowSize = 10;

        // Calculate arrow position (at edge of target node)
        double arrowX = end.x - target.getRadius() * Math.cos(angle);
        double arrowY = end.y - target.getRadius() * Math.sin(angle);

        int[] xPoints = {
                (int)arrowX,
                (int)(arrowX - arrowSize * Math.cos(angle - Math.PI/6)),
                (int)(arrowX - arrowSize * Math.cos(angle + Math.PI/6))
        };
        int[] yPoints = {
                (int)arrowY,
                (int)(arrowY - arrowSize * Math.sin(angle - Math.PI/6)),
                (int)(arrowY - arrowSize * Math.sin(angle + Math.PI/6))
        };

        g2d.fillPolygon(xPoints, yPoints, 3);
    }
}