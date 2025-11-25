import java.awt.*;
import java.util.ArrayList;

public class Professor {
    // position and size
    private int x, y, speed, width, height;

    // constructor
    public Professor() {
        // default starting position and movement speed
        
    }
    
    // movement based on a single key character (W/A/S/D)
    public void move(char key) {
        switch (Character.toLowerCase(key)) {
            case 'w': // up
                y -= speed;
                break;
            case 'a': // left
                x -= speed;
                break;
            case 's': // down
                y += speed;
                break;
            case 'd': // right
                x += speed;
                break;
            default:
                // other keys - no movement
                break;
        }
    }

    // simple accessors
    public int getX() { return x; }
    public int getY() { return y; }
    public Rectangle getBounds() { return new Rectangle(x, y, width, height); }
}
