import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
class MovementManager {
    private Stack<Integer> movementStack;

    public MovementManager() {
        this.movementStack = new Stack<>();
    }

    public void buildMovementStack(int fromPos, int toPos) {
        movementStack.clear();

        if (fromPos < toPos) {
            for (int i = toPos; i > fromPos; i--) {
                movementStack.push(i);
            }
        } else if (fromPos > toPos) {
            for (int i = toPos; i < fromPos; i++) {
                movementStack.push(i);
            }
        }
    }

    public Integer popNextPosition() {
        if (!movementStack.isEmpty()) {
            return movementStack.pop();
        }
        return null;
    }

    public boolean hasMovement() {
        return !movementStack.isEmpty();
    }
}