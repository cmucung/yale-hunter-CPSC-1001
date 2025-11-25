import java.util.*;
import java.util.List;
import java.awt.*;

public class Rescuer extends Student {
    private Integer targetX = null;
    private Integer targetY = null;

    private int unfreezeCounter = 0; // make sure it stays for 3 turns

    public Rescuer() {  // same functions as student, but it just has the objective to rescue if needed 
        super();
    }

    @Override
    public Direction getMove(String[][] grid) {
        // no target, find nearest frozen student
        if (targetX == null || targetY == null) {
            findFrozenStudent(grid);
        }

        // movement if there is target
        if (targetX != null && targetY != null) {
            if (isAdjacent(targetX, targetY)) {
                unfreezeCounter++;
                // stay still for three turns
                if (unfreezeCounter < 3) {
                    return Direction.CENTER;
                }
                // unfreeze student
                unfreezeStudent(grid, targetX, targetY);

                targetX = null;
                targetY = null;
                unfreezeCounter = 0;
                return Direction.CENTER;
            }
            // if not adjacent, move optimally
            return moveTowardTarget(targetX, targetY, grid);
        }
        // else move like parent class
        return super.getMove(grid);

    }

    // helper functions 

    private boolean isAdjacent(int fx, int fy) {
        int dx = Math.abs(getX() - fx);
        int dy = Math.abs(getY() - fy);
        return dx + dy == 1;

    }

    private void findFrozenStudent(String[][] grid) {
        // scan grid for frozen student character marker
        int rows = grid.length; 
        int cols = grid[0].length;

        int bestDistance = Integer.MAX_VALUE;

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                if (grid[y][x].equals("//frozen student character")) {
                    int d = Math.abs(getX() - x) + Math.abs(getY() - y);

                    if(d < bestDistance) {
                        bestDistance = d;
                        targetX = x;
                        targetY = y;
                    }
                }
            }
        }
    }

    private Direction moveTowardTarget(int fx, int fy, String[][] grid) {
        int cx = getX();
        int cy = getY();

        int dx = fx - cx;
        int dy = fy - cy;

        // move horizontally first 
        if (dx > 0 && isInside(cx + 1, cy, grid)) {
            return Direction.EAST;
        }
        if (dx < 0 && isInside(cx - 1, cy, grid)) {
            return Direction.WEST;
        }

        // then vertically 
        if (dy > 0 && isInside(cx, cy + 1, grid)) {
            return Direction.SOUTH;
        }
        if (dy < 0 && isInside(cx, cy - 1, grid)) {
            return Direction.NORTH;
        }

        return Direction.CENTER;
    }

    private boolean isInside(int x, int y, String[][] grid) {
        return x >= 0 && x < grid[0].length && y >= 0 && y < grid.length;
    }

    private void unfreezeStudent(String[][] grid, int x, int y) {
        grid[y][x] = '//string for regular student';
    }
}
