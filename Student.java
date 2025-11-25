import java.awt.*;
import java.util.ArrayList;



public class Student{

    private static Student rescuerStudent = null;
    private int totalNumStudent = 0;

    private int x;
    private int y;
    private int width;
    private int height;
    private boolean alive = true;
    private boolean awake = true;
    private final String[] neighbors = {" ", " ", " ", " ", " "};

	// constants for directions
	public static enum Direction {
		NORTH, SOUTH, EAST, WEST, CENTER
	};


    public Student(){

        if (rescuerStudent == null){

            //if there is no student created, we create the first student which is the rescuer
            rescuerStudent = this;
        }
    }


    public void reset() {
        rescuerStudent = null;
        totalNumStudent = 0;
    }

    //idea here: if a student has been saved once, it becomes red once it gets up, because it's in a risky state. 
    // If the student is scared to dizziness again, he or she will immeidately be kicked out of the game and disappear

    public Direction getMove(String[][] grid){

        ArrayList <Direction> directionList = new ArrayList<>();

        int currentX = getX();
        int currentY = getY();

        int rows = grid.length;
        int cols = grid[0].length;

        if (!grid[currentX][Math.floorMod(currentY + 1, cols)].equals("ROCK")){
            directionList.add(Direction.SOUTH);
        }
        if (!grid[currentX][Math.floorMod(currentY - 1, cols)].equals("ROCK")){
            directionList.add(Direction.NORTH);
        }
        if (!grid[Math.floorMod(currentX + 1, rows)][currentY].equals("ROCK")){
            directionList.add(Direction.EAST);
        }if (!grid[Math.floorMod(currentX - 1, rows)][currentY].equals("ROCK")){
            directionList.add(Direction.WEST);
        }

        if (directionList.isEmpty()) {
            return Direction.CENTER; 
        }else{

            int getIndex = (int)(Math.random() * directionList.size());

            return directionList.get(getIndex);
        }

    }





    public final int getHeight() {
		return height;
	}

	// Returns the animal that is 1 square in the given direction away
	// from this animal.  A blank space, " ", signifies an empty square.
	public final String getNeighbor(Direction direction) {
		return neighbors[direction.ordinal()];
	}

	// Returns the width of the game simulation world.
	public final int getWidth() {
		return width;
	}

	// Returns this animal's current x-coordinate.
	public final int getX() {
		return x;
	}

	// Returns this animal's current y-coordinate.
	public final int getY() {
		return y;
	}
	
	// Returns true if this animal is currently alive.
	// This will return false if this animal has lost a fight and died.
	public final boolean isAlive() {
		return alive;
	}

	// Returns true if this animal is currently awake.
	// This will temporarily return false if this animal has eaten too much food
	// and fallen asleep.
	public final boolean isAwake() {
		return awake;
	}

	// Sets whether or not this animal is currently alive.
	// This method is called by the simulator and not by your animal itself.
	public final void setAlive(boolean alive) {
		this.alive = alive;
	}

	// Sets whether or not this animal is currently awake.
	// This method is called by the simulator and not by your animal itself.
	public final void setAwake(boolean awake) {
		this.awake = awake;
	}

	// Sets the height of the game simulation world to be the given value,
	// so that future calls to getHeight will return this value.
	// This method is called by the simulator and not by your animal itself.
	public final void setHeight(int height) {
		this.height = height;
	}

	// Sets the neighbor of this animal in the given direction to be the given value,
	// so that future calls to getNeighbor in that direction will return this value.
	// This method is called by the simulator and not by your animal itself.
	public final void setNeighbor(Direction direction, String value) {
		neighbors[direction.ordinal()] = value;
	}

	// Sets the width of the game simulation world to be the given value.
	// so that future calls to getWidth will return this value.
	// This method is called by the simulator and not by your animal itself.
	public final void setWidth(int width) {
		this.width = width;
	}

	// Sets this animal's memory of its x-coordinate to be the given value.
	// so that future calls to getX will return this value.
	// This method is called by the simulator and not by your animal itself.
	public final void setX(int x) {
		this.x = x;
	}

	// Sets this animal's memory of its y-coordinate to be the given value.
	// so that future calls to getY will return this value.
	// This method is called by the simulator and not by your animal itself.
	public final void setY(int y) {
		this.y = y;
	}

	// These methods are provided to inform you about the result of fights, sleeping, etc.
	// You can override these methods in your Bulldog to be informed of these events.

	// called when you win a fight against another animal
	public void win() {}

	// called when you lose a fight against another animal, and die
	public void lose() {}

	// called when your animal is put to sleep for eating too much food
	public void sleep() {}

	// called when your animal wakes up from sleeping
	public void wakeup() {}
	
	// called when your critter mates with another critter
	public void mate() {}
	
	// called when your critter is done mating with another critter
	public void mateEnd() {}





}