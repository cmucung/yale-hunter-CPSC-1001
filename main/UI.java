package main;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

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
    
    
    double totalTime;
    double playTime;
    DecimalFormat dFormat = new DecimalFormat("#0.00");
    

    public UI(GamePanel gp) {
        this.gp = gp;

        this.totalTime = 120.00;
        this.playTime = 0;

        arial_40 = new Font("Arial", Font.PLAIN, 40);
        arial_80B = new Font("Arial", Font.BOLD, 80);
    }

    public void showMessage(String text) {
        message = text;
        messageOn = true;
    }

    public void draw(Graphics2D g2) {
        /*this.g2 = g2;
        g2.setColor(Color.white);

        // TITLE STATE
        if (gp.gameState == gp.titleState) {
            drawTitleScreen();
        }
        if (gameFinished == true) {

            g2.setFont(arial_40);
            g2.setColor(Color.white);

            String text;
            int textLength;
            int x;
            int y;
            
            text = "You win!";
            textLength = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();

            x = gp.screenWidth / 2 - textLength / 2;
            y = gp.screenHeight / 2 - (gp.tileSize * 3);

            g2.drawString(text, x, y);

            g2.setFont(arial_80B);
            g2.setColor(Color.yellow);

            text = "Congratulations!";
            textLength = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();

            x = gp.screenWidth / 2 - textLength / 2;
            y = gp.screenHeight / 2 + (gp.tileSize * 2);

            g2.drawString(text, x, y);

            gp.gameThread = null;
        } else {
            g2.setFont(arial_40);
            g2.setColor(Color.white);

            // TIME
            playTime += (double)1 / gp.FPS;
            g2.drawString("Time Left: " + dFormat.format(totalTime - playTime), gp.tileSize / 2, gp.tileSize);

            // MESSAGE
            if (messageOn == true) {
                g2.setFont(g2.getFont().deriveFont(30F));
                g2.drawString(message, gp.tileSize / 2, gp.tileSize * 5);

                messageCounter++;
                if (messageCounter > 120) {
                    messageCounter = 0;
                    messageOn = false;
                }
            }
                */
            this.g2 = g2;
            g2.setFont(arial_40);
            g2.setColor(Color.white);

            // TITLE STATE 
            if (gp.gameState == gp.titleState) {
                drawTitleScreen();
            }

            // PLAY STATE
            if (gp.gameState == gp.playState) {
            
            }
        }

        public void drawTitleScreen() {
            // BACKGROUND
            g2.setColor(new Color(70, 120, 80));
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

            text = "LOAD GAME";
            x = getXforCenteredText(text);
            y += gp.tileSize;
            g2.drawString(text, x, y);

            text = "QUIT";
            x = getXforCenteredText(text);
            y += gp.tileSize;
            g2.drawString(text, x, y);
        }

        public int getXforCenteredText(String text) {
            int length = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
            int x = gp.screenWidth / 2 - length / 2;
            return x;
    }
}
