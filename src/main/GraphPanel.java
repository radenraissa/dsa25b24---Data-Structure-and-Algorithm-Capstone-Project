package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class GraphPanel extends JPanel {
    private Graph graph;
    private Node draggedNode = null;

    public GraphPanel(Graph graph) {
        this.graph = graph;
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.WHITE);

        // Add mouse listeners for dragging
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                draggedNode = graph.findNodeAt(e.getX(), e.getY());
                if (draggedNode != null) {
                    draggedNode.setDragging(true);
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (draggedNode != null) {
                    draggedNode.setDragging(false);
                    draggedNode = null;
                    setCursor(Cursor.getDefaultCursor());
                    repaint();
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (draggedNode != null) {
                    draggedNode.setPosition(e.getX(), e.getY());
                    repaint();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                Node node = graph.findNodeAt(e.getX(), e.getY());
                if (node != null) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                } else {
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        };

        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        if (graph != null) {
            graph.draw(g2d);
        }
    }
}