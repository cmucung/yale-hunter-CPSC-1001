import java.awt.*;
import java.util.ArrayList;

public class Professor {
    // position and size
    private int x, y;
    private int size;
    private boolean studentNearby;

    // constructor
    public Professor() {
        this.studentNearby = false;
        
    }

    // movement based on a single key character (W/A/S/D)
    public void move(char key) {
        switch (Character.toLowerCase(key)) {
            case 'w': // up
                y -= size;
                break;
            case 'a': // left
                x -= size;
                break;
            case 's': // down
                y += size;
                break;
            case 'd': // right
                x += size;
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
