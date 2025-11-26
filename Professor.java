import java.awt.*;
import main.GamePanel;
import main.KeyHandler;
import java.util.ArrayList;

import entity.Player;

public class Professor extends Player {
    // extends Player so we can inherit the x, y, and size variables
    // private int x, y;
    // private int size;
    private boolean studentNearby;
    private int scareCooldown;
    private int scareCount;

    private ArrayList<Student> students; // reference to student list

    // constructor
    public Professor(GamePanel gp, KeyHandler keyH, ArrayList<Student> students) {
        super(gp, keyH);
        this.students = students;
        this.studentNearby = false;
        this.scareCount = 0;
        this.scareCooldown = 0;
    }

    @Override
    public void setDefaultValues() {
        super.setDefaultValues(); // using Player's default setup

        // if we want we can customize if professor starts in a different location
    }

    @Override
    public void getPlayerImage() {
        // Load professor-specific sprites instead
        up1 = setup("professor_up_1");
        up2 = setup("professor_up_2");
        down1 = setup("professor_down_1");
        down2 = setup("professor_down_2");
        left1 = setup("professor_left_1");
        left2 = setup("professor_left_2");
        right1 = setup("professor_right_1");
        right2 = setup("professor_right_2");
    }

    @Override
    public void update() {
        super.update(); // movement logic from Player

        // professor specific behavior
        checkForNearbyStudents();

        if (scareCooldown > 0) {
            scareCooldown--;
        }

        // This handles scare action (triggered by pressing X)
        if (keyH.scarePressed) {
            attemptScare();
        }

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

    // I think this approach would work best for determining if a student is nearby
    private void checkForNearbyStudents() {
        studentNearby = false;

        for (Student student : students) {
            int distanceX = Math.abs(student.worldX - worldX) / gp.tileSize;
            int distanceY = Math.abs(student.worldY - worldY) / gp.tileSize;
            int studentDistance = distanceX + distanceY;

            if (studentDistance == 1) {
                studentNearby = true;
                break;
            }
        }
    }

    // The grid approach wouldn't work bc Player class
    // operates movement & location stuff through a coordinate system
    public boolean canScare(grid[][] students) {
        if (students[x + 1][y].hasStudent()) {
            studentNearby = true;
        } else if (students[x - 1][y].hasStudent()) {
            studentNearby = true;
        } else if (students[x][y + 1].hasStudent()) {
            studentNearby = true;
        } else if (students[x][y - 1].hasStudent()) {
            studentNearby = true;
        } else {
            studentNearby = false;
        }
        return studentNearby;
    }

    private void attemptScare() {
        if (studentNearby && scareCooldown == 0) {
            for (Student student : students) {
                int distanceX = Math.abs(student.worldX - worldX) / gp.tileSize;
                int distanceY = Math.abs(student.worldY - worldY) / gp.tileSize;
                int studentDistance = distanceX + distanceY;

                if (studentDistance == 1 && student.isAwake()) {
                    student.getScared();
                    scareCount++;
                }
            }
            scareCooldown = 10;
        }
    }

    public void scareCooldown() {
        if (scareCooldown > 0) {
            scareCooldown--;
        }
    }

    public void scare(ArrayList<Student> students, char key) {
        if (Character.toLowerCase(key) == 'x' && studentNearby && scareCooldown == 0) {
            for (Student student : students) {
                if (Math.abs(student.getX() - x) + Math.abs(student.getY() - y) == 1) {
                    student.getScared();
                    scareCount++;
                }
            }
            scareCooldown = 10; // reset cooldown
        }
    }

    public int getScareCount() {
        return scareCount;
    }
    
}
