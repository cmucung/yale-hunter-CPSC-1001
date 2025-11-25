import java.awt.*;
import java.util.ArrayList;

public class Professor {
    // position and size
    private int x, y, speed, width, height;
    private boolean studentNearby;

    // constructor
    public Professor() {
        this.studentNearby = false;
        
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

    public boolean canScare(grid[][] students){
        if (students[x + 1][y].hasStudent()){
            studentNearby = true;
        } else if (students[x - 1][y].hasStudent()){
            studentNearby = true;
        } else if (students[x][y + 1].hasStudent()){
            studentNearby = true;
        } else if (students[x][y - 1].hasStudent()){
            studentNearby = true;
        } else {
            studentNearby = false;
        }
        return studentNearby;
    }

}
