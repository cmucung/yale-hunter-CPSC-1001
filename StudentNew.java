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
    
    // core state variables
    private boolean isSaving = false;
    private Student targetStudent = null; // student targeted for rescue
    private boolean permanentlyRemoved = false;
    public int scareLevel = 0; // 0 (Normal), 50 (Risky), 100 (Frozen/Removed)
    public boolean isFrozen = false;
    private boolean hasBeenSaved = false; 
    private int rescueTimer = 0; // timerfor rescue duration (in game frames)
    private final int RESCUE_DURATION = 3 * 60; // 3 seconds * 60 FPS = 180 frames

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
            // generate random tile coordinates so students won't start at the same location
            int randomCol = rand.nextInt(gp.maxWorldCol);
            int randomRow = rand.nextInt(gp.maxWorldRow);
            
            // convert to world coordinates
            int testX = randomCol * gp.tileSize;
            int testY = randomRow * gp.tileSize;
            
            // check if this tile has collision
            int tileNum = gp.tileM.mapTileNum[randomCol][randomRow];
            
            if (!gp.tileM.tile[tileNum].collision) {
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
    
    // new state and saving methods
    public void startSaving(Student target) {
        this.isSaving = true;
        this.targetStudent = target;
        this.speed = 3; // slight speed increase for rescue
    }
    
    // Called by GamePanel or self to cancel the rescue mission.
    public void stopSaving() {
        this.isSaving = false;
        this.targetStudent = null;
        this.rescueTimer = 0;
        this.speed = 2; // Restore normal speed
    }
    
    // when professor attempts to scare students 
    public void scare() {
        if (permanentlyRemoved) return; 
        
        // if the saving student is scared, the rescue mission is immediately canceled
        if (isSaving) {
            stopSaving();
        }

        scareLevel += 50;
        
        if (scareLevel >= 100) {
            if (hasBeenSaved) {
                // scared again after being rescued -> permanently removed
                permanentlyRemoved = true; 
                isFrozen = false;
            } else {
                // scared the first time -> frozen
                isFrozen = true;
                scareLevel = 100;
                
                // gamepanel must check and assign a new savior
            }
        }
    }
    
    // if successfully rescued 
    public void rescue() {
        if (isFrozen) {
            isFrozen = false;
            hasBeenSaved = true; // mark as saved once
            scareLevel = 50;     // reset scare level to 50
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
    
    //helper checks if the student is close enough to the target (in the same or adjacent tile)
    private boolean isNear(Student target) {
        int tileDistanceX = Math.abs((worldX + gp.tileSize/2) - (target.worldX + gp.tileSize/2)) / gp.tileSize;
        int tileDistanceY = Math.abs((worldY + gp.tileSize/2) - (target.worldY + gp.tileSize/2)) / gp.tileSize;
        return tileDistanceX <= 1 && tileDistanceY <= 1; 
    }
    
    // checks and handles the rescue student's timer and movement logic
    private void checkRescueStatus() {
        if (isSaving && targetStudent != null) {
            
            // check if the target still needs rescuing 
            if (!targetStudent.isFrozen() || targetStudent.isPermanentlyRemoved()) {
                stopSaving(); // Target is gone or already rescued, cancel mission
                return;
            }

            // check if near target and start rescue timer
            if (isNear(targetStudent)) {
                if (rescueTimer == 0) {
                    rescueTimer = 1; // start the timer
                } else if (rescueTimer >= RESCUE_DURATION) {
                    targetStudent.rescue();
                    stopSaving(); // rescue complete, cancel mission
                } else {
                    rescueTimer++;
                }
                // rescuer remains stationary
                direction = "center"; 
            } else {
                // move towards the target
                if (rescueTimer > 0) {
                    rescueTimer = 0; // if the rescuer moves, reset the timer
                }
                // move towards the target
                if (targetStudent.worldX < worldX) direction = "left";
                else if (targetStudent.worldX > worldX) direction = "right";
                else if (targetStudent.worldY < worldY) direction = "up";
                else direction = "down";
            }
        }
    }
    
    public void setAction() {
        // only unfrozen, unremoved, and non-saving students execute fleeing/random movement
        if (isFrozen || permanentlyRemoved || isSaving) return; 
        
        actionCounter++;
        
        // calculate distance to player
        int distanceX = Math.abs(worldX - gp.player.worldX);
        int distanceY = Math.abs(worldY - gp.player.worldY);
        int distance = (int) Math.sqrt(distanceX * distanceX + distanceY * distanceY);
        
        // detection range in pixels (e.g., 5 tiles)
        int detectionRange = gp.tileSize * 5;
        
        if (distance < detectionRange) {
            // player is close - prioritize fleeing but with some randomness
            if (actionCounter >= 30) { // change direction more frequently when fleeing
                int fleeChance = random.nextInt(100) + 1;
                
                if (fleeChance <= 70) {
                    // 70% chance to flee directly away
                    if (Math.abs(distanceX) > Math.abs(distanceY)) {
                        // move horizontally away
                        if (gp.player.worldX < worldX) {
                            direction = "right";
                        } else {
                            direction = "left";
                        }
                    } else {
                        // move vertically away
                        if (gp.player.worldY < worldY) {
                            direction = "down";
                        } else {
                            direction = "up";
                        }
                    }
                } else {
                    // 30% chance to move perpendicular (helps escape from corners)
                    if (Math.abs(distanceX) > Math.abs(distanceY)) {
                        // move vertically
                        if (random.nextBoolean()) {
                            direction = "up";
                        } else {
                            direction = "down";
                        }
                    } else {
                        // move horizontally
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
            // player is far - normal random movement
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
        
        // handle removed/frozen states first, stopping movement/action
        if (permanentlyRemoved) return;
        if (isFrozen) return;

        // handle saving behavior logic (this sets direction and speed)
        if (isSaving && targetStudent != null) {
            checkRescueStatus();
        } else {
            setAction(); // calls original setAction (fleeing/random)
        }
        
        // if the direction is 'center' (meaning the student is standing still to rescue) skip movement
        if (direction.equals("center")) {
            // still run animation while standing still
            spriteCounter++;
            if(spriteCounter > 12) {
                spriteNum = (spriteNum == 1) ? 2 : 1;
                spriteCounter = 0;
            }
            return;
        }

        collisionOn = false;
        gp.cChecker.checkTile(this);

        // If collision detected, try a different direction
        if (collisionOn == true) {
            // pick a new random direction when hitting a wall
            int i = random.nextInt(4);
            switch(i) {
                case 0: direction = "up"; break;
                case 1: direction = "down"; break;
                case 2: direction = "left"; break;
                case 3: direction = "right"; break;
            }
            actionCounter = 0; // reset counter to try new direction immediately
        } else {
            // no collision, move in current direction
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
        // do not draw if permanently removed
        if (permanentlyRemoved) return; 

        BufferedImage image = null;
        
        // calculate screen position based on player's position
        int screenX = worldX - player.worldX + player.screenX;
        int screenY = worldY - player.worldY + player.screenY;
        
        // only draw if student is on screen
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
                // stationary sprite for saving
                case "center":
                    image = down1; 
                    break;
            }

            // replaces original g2.drawImage
            if (isFrozen) {
                // frozen state: draw gray/semi-transparent overlay
                g2.setColor(new Color(150, 150, 150, 150)); 
                g2.fillRect(screenX, screenY, gp.tileSize, gp.tileSize);
                g2.drawImage(image, screenX, screenY, null);
            } else if (isSaving) {
                 // saving state: draw a different color marker (e.g., blue)
                g2.setColor(new Color(0, 100, 255, 80)); 
                g2.fillRect(screenX, screenY, gp.tileSize, gp.tileSize);
                g2.drawImage(image, screenX, screenY, null);
                
                // draw rescue timer bar
                if (rescueTimer > 0) {
                    g2.setColor(Color.YELLOW);
                    int barWidth = (int)((double)rescueTimer / RESCUE_DURATION * gp.tileSize);
                    g2.fillRect(screenX, screenY - 5, barWidth, 3);
                }
            } else if (hasBeenSaved) {
                // rescued state (risky state): draw red filter
                g2.setColor(new Color(255, 0, 0, 50)); 
                g2.fillRect(screenX, screenY, gp.tileSize, gp.tileSize);
                g2.drawImage(image, screenX, screenY, null);
            } else {
                // normal state
                g2.drawImage(image, screenX, screenY, null);
            }
            
            // draw collision box for debugging (original logic)
            g2.setColor(Color.blue);
            g2.drawRect(screenX + solidArea.x, screenY + solidArea.y, solidArea.width, solidArea.height);
        }
    }
}
