package entity;

import main.GamePanel;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.Rectangle;
import java.awt.Color;
import java.util.Random;
import tile.UtilityTool;

public class Student extends Entity {
    
    protected GamePanel gp;
    private Random random;
    private int actionCounter = 0;

    // =========================================================
    // Core State Variables
    // =========================================================
    // Rescue State (Dynamic Assignment)
    private boolean isSaving = false;
    private Student targetStudent = null; // The student targeted for rescue
    private boolean permanentlyRemoved = false;
    public int scareLevel = 0; // 0 (Normal), 50 (Risky), 100 (Frozen/Removed)
    public boolean isFrozen = false;
    private boolean hasBeenSaved = false; 
    private int rescueTimer = 0; // Timer for rescue duration (in game frames)
    private final int RESCUE_DURATION = 3 * 60; // 3 seconds * 60 FPS = 180 frames
    // =========================================================

    public Student(GamePanel gp) {
        this.gp = gp;
        this.random = new Random();

        solidArea = new Rectangle(0, 0, gp.tileSize, gp.tileSize);
        solidArea.x = 0;
        solidArea.y = 0;
        solidArea.width = 32;
        solidArea.height = 32;

        setDefaultValues();
        getStudentImage();
    }

    public void setDefaultValues() {
        speed = 2;
        direction = "down";
    }
    
    public void setValidPosition() {
        Random rand = new Random();
        boolean validPosition = false;
        int attempts = 0;
        
        while (!validPosition && attempts < 100) {
            // Generate random tile coordinates so students won't start at the same location
            int randomCol = rand.nextInt(gp.maxWorldCol);
            int randomRow = rand.nextInt(gp.maxWorldRow);
            
            // Convert to world coordinates
            int testX = randomCol * gp.tileSize;
            int testY = randomRow * gp.tileSize;
            
            // Check if this tile has collision
            int tileNum = gp.tileM.mapTileNum[randomCol][randomRow];
            
            if (!gp.tileM.tile[tileNum].collision) {
                // Valid position found!
                worldX = testX;
                worldY = testY;
                validPosition = true;
            }
            
            attempts++;
        }
        
        // if no valid position found after 100 attempts
        if (!validPosition) {
            worldX = gp.tileSize * 25;
            worldY = gp.tileSize * 40;
        }
    }

    public void getStudentImage() {
        // using tim sprites as placeholders for now, can change later
        up1 = setup("tim_up_1");
        up2 = setup("tim_up_2");
        down1 = setup("tim_down_1");
        down2 = setup("tim_down_2");
        left1 = setup("tim_left_1");      
        left2 = setup("tim_left_2");
        right1 = setup("tim_right_1");
        right2 = setup("tim_right_2");
    }

    public BufferedImage setup(String imageName) {
        UtilityTool uTool = new UtilityTool();
        BufferedImage image = null;

        try {
            // using player images as placeholder
            image = ImageIO.read(getClass().getResourceAsStream("/player/" + imageName + ".png"));
            image = uTool.scaleImage(image, gp.tileSize, gp.tileSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }
    
    // =========================================================
    // New State and Saving Methods
    // =========================================================
    
    /**
     * Called by GamePanel to assign a rescue mission to this student.
     */
    public void startSaving(Student target) {
        this.isSaving = true;
        this.targetStudent = target;
        this.speed = 3; // Slight speed increase for rescue
    }
    
    /**
     * Called by GamePanel or self to cancel the rescue mission.
     */
    public void stopSaving() {
        this.isSaving = false;
        this.targetStudent = null;
        this.rescueTimer = 0;
        this.speed = 2; // Restore normal speed
    }
    
    /**
     * Called when the Professor attempts to scare students.
     */
    public void scare() {
        if (permanentlyRemoved) return; 
        
        // If the saving student is scared, the rescue mission is immediately canceled
        if (isSaving) {
            stopSaving();
        }

        scareLevel += 50;
        
        if (scareLevel >= 100) {
            if (hasBeenSaved) {
                // State 2: Scared again after being rescued -> Permanently Removed
                permanentlyRemoved = true; 
                isFrozen = false;
            } else {
                // State 1: Scared the first time -> Frozen
                isFrozen = true;
                scareLevel = 100;
                
                // CRITICAL: Student is frozen. GamePanel must check and assign a new savior.
            }
        }
    }
    
    /**
     * Called when the target student is successfully rescued.
     */
    public void rescue() {
        if (isFrozen) {
            isFrozen = false;
            hasBeenSaved = true; // Mark as saved once
            scareLevel = 50;     // Reset scare level to 50 (Risky state)
        }
    }
    
    public boolean isFrozen() {
        return isFrozen;
    }
    
    public boolean isSaving() {
        return isSaving;
    }
    
    public boolean isPermanentlyRemoved() {
        return permanentlyRemoved;
    }
    
    /**
     * Helper: Checks if the student is close enough to the target (in the same or adjacent tile)
     */
    private boolean isNear(Student target) {
        int tileDistanceX = Math.abs((worldX + gp.tileSize/2) - (target.worldX + gp.tileSize/2)) / gp.tileSize;
        int tileDistanceY = Math.abs((worldY + gp.tileSize/2) - (target.worldY + gp.tileSize/2)) / gp.tileSize;
        return tileDistanceX <= 1 && tileDistanceY <= 1; 
    }
    
    /**
     * Checks and handles the rescue student's timer and movement logic.
     */
    private void checkRescueStatus() {
        if (isSaving && targetStudent != null) {
            
            // 1. Check if the target still needs rescuing 
            if (!targetStudent.isFrozen() || targetStudent.isPermanentlyRemoved()) {
                stopSaving(); // Target is gone or already rescued, cancel mission
                return;
            }

            // 2. Check if near target and start rescue timer
            if (isNear(targetStudent)) {
                if (rescueTimer == 0) {
                    rescueTimer = 1; // Start the timer
                } else if (rescueTimer >= RESCUE_DURATION) {
                    targetStudent.rescue();
                    stopSaving(); // Rescue complete, cancel mission
                } else {
                    rescueTimer++;
                }
                // Rescuer remains stationary
                direction = "center"; 
            } else {
                // 3. Move towards the target
                if (rescueTimer > 0) {
                    rescueTimer = 0; // If the rescuer moves, reset the timer
                }
                // Simple pathfinding: move towards the target
                if (targetStudent.worldX < worldX) direction = "left";
                else if (targetStudent.worldX > worldX) direction = "right";
                else if (targetStudent.worldY < worldY) direction = "up";
                else direction = "down";
            }
        }
    }
    
    // =========================================================

    public void setAction() {
        // Only unfrozen, unremoved, and non-saving students execute fleeing/random movement
        if (isFrozen || permanentlyRemoved || isSaving) return; 
        
        actionCounter++;
        
        // calculate distance to player
        int distanceX = Math.abs(worldX - gp.player.worldX);
        int distanceY = Math.abs(worldY - gp.player.worldY);
        int distance = (int) Math.sqrt(distanceX * distanceX + distanceY * distanceY);
        
        // detection range in pixels (e.g., 5 tiles)
        int detectionRange = gp.tileSize * 5;
        
        if (distance < detectionRange) {
            // Player is close - prioritize fleeing but with some randomness
            if (actionCounter >= 30) { // Change direction more frequently when fleeing
                int fleeChance = random.nextInt(100) + 1;
                
                if (fleeChance <= 70) {
                    // 70% chance to flee directly away
                    if (Math.abs(distanceX) > Math.abs(distanceY)) {
                        // Move horizontally away
                        if (gp.player.worldX < worldX) {
                            direction = "right";
                        } else {
                            direction = "left";
                        }
                    } else {
                        // Move vertically away
                        if (gp.player.worldY < worldY) {
                            direction = "down";
                        } else {
                            direction = "up";
                        }
                    }
                } else {
                    // 30% chance to move perpendicular (helps escape from corners)
                    if (Math.abs(distanceX) > Math.abs(distanceY)) {
                        // Move vertically
                        if (random.nextBoolean()) {
                            direction = "up";
                        } else {
                            direction = "down";
                        }
                    } else {
                        // Move horizontally
                        if (random.nextBoolean()) {
                            direction = "left";
                        } else {
                            direction = "right";
                        }
                    }
                }
                actionCounter = 0;
            }
        } else {
            // Player is far - normal random movement
            if (actionCounter >= 120) {
                int i = random.nextInt(100) + 1;
                
                if (i <= 25) {
                    direction = "up";
                } else if (i <= 50) {
                    direction = "down";
                } else if (i <= 75) {
                    direction = "left";
                } else {
                    direction = "right";
                }
                
                actionCounter = 0;
            }
        }
    }

    public void update() {
        
        // NEW: Handle removed/frozen states first, stopping movement/action
        if (permanentlyRemoved) return;
        if (isFrozen) return;

        // NEW: Handle saving behavior logic (this sets direction and speed)
        if (isSaving && targetStudent != null) {
            checkRescueStatus();
        } else {
            setAction(); // Calls original setAction (fleeing/random)
        }
        
        // If the direction is 'center' (meaning the student is standing still to rescue), skip movement
        if (direction.equals("center")) {
            // Still run animation while standing still
            spriteCounter++;
            if(spriteCounter > 12) {
                spriteNum = (spriteNum == 1) ? 2 : 1;
                spriteCounter = 0;
            }
            return;
        }

        // --- Original Movement/Collision Logic Follows ---
        collisionOn = false;
        gp.cChecker.checkTile(this);

        // If collision detected, try a different direction
        if (collisionOn == true) {
            // Pick a new random direction when hitting a wall
            int i = random.nextInt(4);
            switch(i) {
                case 0: direction = "up"; break;
                case 1: direction = "down"; break;
                case 2: direction = "left"; break;
                case 3: direction = "right"; break;
            }
            actionCounter = 0; // Reset counter to try new direction immediately
        } else {
            // No collision, move in current direction
            switch (direction) {
                case "up":
                    worldY -= speed;
                    break;
                case "down":
                    worldY += speed;
                    break;
                case "left":
                    worldX -= speed;
                    break;
                case "right":
                    worldX += speed;
                    break;
            }
        }

        spriteCounter++;
        if(spriteCounter > 12) {
            if(spriteNum == 1) {
                spriteNum = 2;
            }
            else if(spriteNum == 2) {
                spriteNum = 1;
            }
            spriteCounter = 0;
        }
    }

    public void draw(Graphics2D g2, Player player) {
        // NEW: Do not draw if permanently removed
        if (permanentlyRemoved) return; 

        BufferedImage image = null;
        
        // Calculate screen position based on player's position
        int screenX = worldX - player.worldX + player.screenX;
        int screenY = worldY - player.worldY + player.screenY;
        
        // Only draw if student is on screen
        if (worldX + gp.tileSize > player.worldX - player.screenX &&
            worldX - gp.tileSize < player.worldX + player.screenX &&
            worldY + gp.tileSize > player.worldY - player.screenY &&
            worldY - gp.tileSize < player.worldY + player.screenY) {
            
            switch (direction) {
                case "up":
                    if (spriteNum == 1) {
                        image = up1;
                    } else if (spriteNum == 2) {
                        image = up2;
                    }
                    break;
                case "down":
                    if (spriteNum == 1) {
                        image = down1;
                    } else if (spriteNum == 2) {
                        image = down2;
                    }
                    break;
                case "left":
                    if (spriteNum == 1) {
                        image = left1;
                    } else if (spriteNum == 2) {
                        image = left2;
                    }
                    break;
                case "right":
                    if (spriteNum == 1) {
                        image = right1;
                    } else if (spriteNum == 2) {
                        image = right2;
                    }
                    break;
                // NEW: Stationary sprite for saving
                case "center":
                    image = down1; 
                    break;
            }

            // NEW: State-based drawing logic (replaces original g2.drawImage)
            if (isFrozen) {
                // Frozen State: Draw gray/semi-transparent overlay
                g2.setColor(new Color(150, 150, 150, 150)); 
                g2.fillRect(screenX, screenY, gp.tileSize, gp.tileSize);
                g2.drawImage(image, screenX, screenY, null);
            } else if (isSaving) {
                 // Saving State: Draw a different color marker (e.g., blue)
                g2.setColor(new Color(0, 100, 255, 80)); 
                g2.fillRect(screenX, screenY, gp.tileSize, gp.tileSize);
                g2.drawImage(image, screenX, screenY, null);
                
                // Draw rescue timer bar
                if (rescueTimer > 0) {
                    g2.setColor(Color.YELLOW);
                    int barWidth = (int)((double)rescueTimer / RESCUE_DURATION * gp.tileSize);
                    g2.fillRect(screenX, screenY - 5, barWidth, 3);
                }
            } else if (hasBeenSaved) {
                // Rescued State (Risky State): Draw red filter
                g2.setColor(new Color(255, 0, 0, 50)); 
                g2.fillRect(screenX, screenY, gp.tileSize, gp.tileSize);
                g2.drawImage(image, screenX, screenY, null);
            } else {
                // Normal State
                g2.drawImage(image, screenX, screenY, null);
            }
            
            // Draw collision box for debugging (original logic)
            g2.setColor(Color.blue);
            g2.drawRect(screenX + solidArea.x, screenY + solidArea.y, solidArea.width, solidArea.height);
        }
    }
}
