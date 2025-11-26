import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;

class Dice {
    enum DiceColor {
        GREEN, RED
    }

    private DiceColor color;
    private int number;
    private Random random;

    public Dice() {
        this.random = new Random();
        this.color = DiceColor.GREEN;
        this.number = 1;
    }

    public void roll() {
        double colorProb = random.nextDouble();
        this.color = (colorProb < 0.8) ? DiceColor.GREEN : DiceColor.RED;
        this.number = random.nextInt(6) + 1;
    }

    public DiceColor getColor() { return color; }
    public int getNumber() { return number; }

    public String getColorString() {
        return color == DiceColor.GREEN ? "GREEN" : "RED";
    }

    public Color getColorObject() {
        return color == DiceColor.GREEN ? new Color(46, 204, 113) : new Color(231, 76, 60);
    }
}