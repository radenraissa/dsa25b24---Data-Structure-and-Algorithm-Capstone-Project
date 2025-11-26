import java.awt.*;

class Player {
    private String name;
    private int position;
    private Color color;

    public Player(String name, Color color) {
        this.name = name;
        this.position = 1;
        this.color = color;
    }

    public String getName() { return name; }
    public int getPosition() { return position; }
    public Color getColor() { return color; }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return name + " @ " + position;
    }
}