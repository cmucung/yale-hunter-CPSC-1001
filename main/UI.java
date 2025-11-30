package main;

import entity.Entity;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.io.IOException;

public class UI {
    GamePanel gp;
    Font arial_40; Font arial_80B;
    Graphics2D g2;
    BufferedImage studentImage;
    public boolean messageOn = false;
    public String message = "";
    public boolean gameFinished = false;
    int messageCounter = 0;
    public int commandNum = 0;
    public int titleScreenState = 0; // 0: first screen, 1: second screen

    int subState = 0;
    
    
    double totalTime;
    double playTime;
    DecimalFormat dFormat = new DecimalFormat("#0.00");
    

    public UI(GamePanel gp) {
        this.gp = gp;

        this.totalTime = 5.00;
        this.playTime = 0;

        arial_40 = new Font("Arial", Font.PLAIN, 40);
        arial_80B = new Font("Arial", Font.BOLD, 80);
    }

    public void showMessage(String text) {
        message = text;
        messageOn = true;
    }

    public void draw(Graphics2D g2) {
        this.g2 = g2;
        
        g2.setFont(arial_40);
        g2.setColor(Color.white);

        // TITLE SCREEN
        if (gp.gameState == gp.titleState) {
            drawTitleScreen();
        }

        // PLAY SCREEN
        if (gp.gameState == gp.playState) {
            // TIME
            playTime += (double)1/60;
            double timeLeft = totalTime - playTime;
            g2.drawString("Time: " + dFormat.format(timeLeft), 20, 50);
            if (timeLeft <= 0) {
                gp.stopMusic();
                gp.gameState = gp.gameOverState;
            }
        }
        
        // OPTIONS STATE
        if (gp.gameState == gp.optionsState) {
            drawOptionsScreen();
        }
        
        // GAME OVER SCREEN
        if (gp.gameState == gp.gameOverState) {
            drawGameOverScreen();
        }
    }

    public void drawOptionsScreen() {
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // SUB WINDOW - adjust height based on subState
        int frameX = gp.tileSize * 2;
        int frameY = gp.tileSize;
        int frameWidth = gp.screenWidth - (gp.tileSize * 4);
        int frameHeight;
        
        // calculate appropriate height for each subState
        switch(subState) {
            case 0: 
                frameHeight = gp.tileSize * 9; // main options menu
                break;
            case 1: 
                frameHeight = gp.tileSize * 7; // end game confirmation
                break;
            default:
                frameHeight = gp.screenHeight - (gp.tileSize * 2);
        }

        drawSubWindow(frameX, frameY, frameWidth, frameHeight);

        switch(subState) {
            case 0: options_top(frameX, frameY, frameWidth); break;
            case 1: options_endGameConfirmation(frameX, frameY); break;
        }

        gp.keyH.enterPressed = false;
    }

    public void options_top(int frameX, int frameY, int frameWidth) {
        
        int textX;
        int textY;

        // TITLE
        String text = "Options";
        textX = getXforCenteredText(text);
        textY = frameY + gp.tileSize;
        g2.drawString(text, textX, textY);

        // MUSIC
        textX = frameX + gp.tileSize;
        textY += gp.tileSize * 2;
        int musicY = textY; // Save Y position for volume bar
        g2.drawString("Music", textX, textY);
        if (commandNum == 0) {
            g2.drawString(">", textX - 25, textY);
        }

        // SOUND EFFECTS
        textY += gp.tileSize;
        int seY = textY; // Save Y position for volume bar
        g2.drawString("Sound Effects", textX, textY);
        if (commandNum == 1) {
            g2.drawString(">", textX - 25, textY);
        }

        // END GAME
        textY += gp.tileSize;
        g2.drawString("End Game", textX, textY);
        if (commandNum == 2) {
            g2.drawString(">", textX - 25, textY);
            if (gp.keyH.enterPressed == true) {
                subState = 1;
                commandNum = 0;
            }
        }

        // BACK
        textY += gp.tileSize * 2;
        g2.drawString("Back", textX, textY);
        if (commandNum == 3) {
            g2.drawString(">", textX - 25, textY);
            if (gp.keyH.enterPressed == true) {
                gp.gameState = gp.playState;
                commandNum = 0;
            }
        }

        // MUSIC VOLUME - aligned to the right and vertically with text
        textX = frameX + frameWidth - gp.tileSize - 120;
        textY = musicY - 20; // Align with baseline of text
        g2.drawRect(textX, textY, 120, 24);
        int volumeWidth = 24 * gp.music.volumeScale;
        g2.fillRect(textX, textY, volumeWidth, 24);

        // SE VOLUME - aligned to the right and vertically with text
        textY = seY - 20; // Align with baseline of text
        g2.drawRect(textX, textY, 120, 24);
        volumeWidth = 24 * gp.se.volumeScale;
        g2.fillRect(textX, textY, volumeWidth, 24);

        // SAVE OPTIONS
        gp.config.saveConfig();
    }
    


    public void options_endGameConfirmation(int frameX, int frameY) {
        int textX;
        int textY = frameY + gp.tileSize * 2;

        String text = "Are you sure you want to end";
        textX = getXforCenteredText(text);
        g2.drawString(text, textX, textY);
        
        textY += gp.tileSize;
        text = "the game?";
        textX = getXforCenteredText(text);
        g2.drawString(text, textX, textY);

        // YES
        textY += gp.tileSize * 2;
        text = "Yes";
        textX = getXforCenteredText(text);
        g2.drawString(text, textX, textY);
        if (commandNum == 0) {
            g2.drawString(">", textX - 40, textY);
            if (gp.keyH.enterPressed == true) {
                gp.stopMusic();
                gp.ui.titleScreenState = 0;
                gp.ui.commandNum = 0;
                gp.gameState = gp.titleState;
                gp.restart();
                gp.playMusic(5);
                subState = 0;
            }
        }

        // NO
        textY += gp.tileSize;
        text = "No";
        textX = getXforCenteredText(text);
        g2.drawString(text, textX, textY);
        if (commandNum == 1) {
            g2.drawString(">", textX - 40, textY);
            if (gp.keyH.enterPressed == true) {
                subState = 0;
                commandNum = 2;
            }
        }
    }

    public void drawSubWindow(int x, int y, int width, int height) {
        Color c = new Color(0, 0, 0, 200);
        g2.setColor(c);
        g2.fillRoundRect(x, y, width, height, 35, 35);

        c = new Color(255, 255, 255);
        g2.setColor(c);
        g2.setStroke(new java.awt.BasicStroke(4));
        g2.drawRoundRect(x + 5, y + 5, width - 10, height - 10, 25, 25);
    }

    public void drawGameOverScreen() {
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        g2.setFont(arial_80B);
        // Shadow
        String text = "GAME OVER";
        int x = getXforCenteredText(text);
        int y = gp.tileSize * 4;
        g2.drawString(text, x, y);

        // Main
        g2.setColor(Color.white);
        g2.drawString(text, x - 4, y - 4);   

        // Retry option
        g2.setFont(arial_40);
        text = "Retry";
        x = getXforCenteredText(text);
        y += gp.tileSize * 4;
        g2.drawString(text, x, y);
        if (commandNum == 0) {
            g2.drawString(">", x - 40, y);
        }

        // Back to title option
        text = "Quit";
        x = getXforCenteredText(text);
        y += gp.tileSize;
        g2.drawString(text, x, y);
        if (commandNum == 1) {
            g2.drawString(">", x - gp.tileSize, y);
        }  
    } 

    public void drawTitleScreen() {
        if (titleScreenState == 0) {
            // BACKGROUND
            g2.setColor(new Color(0, 0, 0));
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

            // TITLE NAME
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 96F));
            String text = "Yale Hunter";
            int x = getXforCenteredText(text);
            int y = gp.tileSize * 3;

            g2.setColor(Color.white);
            g2.drawString(text, x, y);

            // MENU
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 48F));

            text = "NEW GAME";
            x = getXforCenteredText(text);
            y += gp.tileSize * 4;
            g2.drawString(text, x, y);
            if (commandNum == 0) {
                g2.drawString(">", x - gp.tileSize, y);
            }

            text = "INSTRUCTIONS";
            x = getXforCenteredText(text);
            y += gp.tileSize;
            g2.drawString(text, x, y);
            if (commandNum == 1) {
                g2.drawString(">", x - gp.tileSize, y);
            }

            text = "QUIT";
            x = getXforCenteredText(text);
            y += gp.tileSize;
            g2.drawString(text, x, y);
            if (commandNum == 2) {
                g2.drawString(">", x - gp.tileSize, y);
            }
        } 
        else if (titleScreenState == 1) {
            // PROFESSOR SELECTION SCREEN
            g2.setColor(Color.white);
            g2.setFont(g2.getFont().deriveFont(42F));

            String text = "Select Your Professor";
            int x = getXforCenteredText(text);
            int y = gp.tileSize * 3;
            g2.drawString(text, x, y);

            text= "Professor Tim Barron";
            x = getXforCenteredText(text);
            y += gp.tileSize * 3;
            g2.drawString(text, x, y);
            if (commandNum == 0) {
                g2.drawString(">", x - gp.tileSize, y);
            }

            text= "Professor Ozan Erat";
            x = getXforCenteredText(text);
            y += gp.tileSize;
            g2.drawString(text, x, y);
            if (commandNum == 1) {
                g2.drawString(">", x - gp.tileSize, y);
            }

            text= "Back";
            x = getXforCenteredText(text);
            y += gp.tileSize * 2;
            g2.drawString(text, x, y);
            if (commandNum == 2) {
                g2.drawString(">", x - gp.tileSize, y);
            }

        } else if (titleScreenState == 2) {
            // INSTRUCTIONS SCREEN
            g2.setColor(Color.white);
            g2.setFont(g2.getFont().deriveFont(42F));

            String text = "Instructions";
            int x = getXforCenteredText(text);
            int y = gp.tileSize * 3;
            g2.drawString(text, x, y);

            g2.setFont(g2.getFont().deriveFont(28F));
            text = "Use W,A,S,D keys to move your professor.";
            x = getXforCenteredText(text);
            y += gp.tileSize * 2;
            g2.drawString(text, x, y);
            text = "Scare all the students before time runs out!";
            x = getXforCenteredText(text);
            y += gp.tileSize;
            g2.drawString(text, x, y);
            text= "Back";
            x = getXforCenteredText(text);
            y += gp.tileSize * 2;
            g2.drawString(text, x, y);
            if (commandNum == 0) {
                g2.drawString(">", x - gp.tileSize, y);
            }
        }        
    }

    public int getXforCenteredText(String text) {
        int length = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        int x = gp.screenWidth / 2 - length / 2;
        return x;
    }

    public int getXforAlignToRightText(String text, int tailX) {
        int length = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        int x = tailX - length;
        return x;
    }
}
