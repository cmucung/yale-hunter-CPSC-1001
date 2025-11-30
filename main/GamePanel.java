package main;
import javax.swing.JPanel;

import entity.Player;
import object.SuperObject;
import tile.TileManager;

import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;


public class GamePanel extends JPanel implements Runnable {
    // SCREEN SETTINGS
    final int originalTileSize = 16; // 16x16 tile
    final int scale = 3; // scale x3
   
    public final int tileSize = originalTileSize * scale; // 48x48 tile
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol; // 768 pixels
    public final int screenHeight = tileSize * maxScreenRow; // 576 pixels

    // WORLD SETTINGS
    public int maxWorldCol;
    public int maxWorldRow;
    public final int maxMap = 10;
    public int currentTimeMap = 0;

    // FULL SCREEN SETTINGS
    int screenWidth2 = screenWidth;
    int screenHeight2 = screenHeight;
    BufferedImage tempScreen;
    Graphics2D g2;

    // FPS
    int FPS = 60;

    // SYSTEM
    TileManager tileM = new TileManager(this);
    KeyHandler keyH = new KeyHandler(this);
    Sound music = new Sound();
    Sound se = new Sound();
    public CollisionChecker cChecker = new CollisionChecker(this);
    public AssetSetter aSetter = new AssetSetter(this);
    public UI ui = new UI(this);
    Config config = new Config(this);
    Thread gameThread;

    // ENTITY AND OBJECT
    public String selectedCharacter = "tim"; // "tim" or "ozan"
    public Player player = new Player(this, keyH);
    public SuperObject obj[] = new SuperObject[10];

    // GAME STATE
    public int gameState;
    public final int titleState = 0;
    public final int playState = 1;
    public final int pauseState = 2;
    public final int optionsState = 3;
    public final int gameOverState = 4;


    // Set player's default position
    int playerX = 100;
    int playerY = 100;
    int playerSpeed = 4;


    // CONSTRUCTOR
    public GamePanel() { 
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
    }

    public void setupGame() {
        aSetter.setObject();
        gameState = titleState;
        playMusic(5); // Title screen music

        // FOR FULL SCREEN)
        tempScreen = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB); // blank screen
        g2 = (Graphics2D) tempScreen.getGraphics();
    }


    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void run() {
        double drawInterval = 1000000000 / FPS; // 0.01666 seconds
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long time = 0;
        int drawCount = 0;

        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            time += currentTime - lastTime;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
                drawCount++;
            }

            if (time >= 1000000000) {
                System.out.println("FPS: " + drawCount);
                drawCount = 0;
                time = 0;
            }
        }
    }


    public void update() {
        if (gameState == playState) {
            // PLAYER
            player.update();

            // NPC
        }
    }

    // FOR FULL SCREEN
    public void drawToTempScreen() {
        // TITLE
        if (gameState == titleState) {
            ui.draw(g2);
        }

        else {
            // TILE 
            tileM.draw(g2);

            // OBJECT
            for (int i = 0; i < obj.length; i++) {
                if (obj[i] != null) {
                    obj[i].draw(g2, this);
                }
            }
            // PLAYER
            player.draw(g2);

            // UI
            ui.draw(g2);
        }
    }

    public void drawToScreen() {
        Graphics g = getGraphics();
        g.drawImage(tempScreen, 0, 0, screenWidth2, screenHeight2, null);
        g.dispose();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        // debug
        // Draw title screen
        if (gameState == titleState) {
            ui.draw(g2);
        } else {
            // TILE 
            tileM.draw(g2);

            // OBJECT
            for (int i = 0; i < obj.length; i++) {
                if (obj[i] != null) {
                    obj[i].draw(g2, this);
                }
            }
            // PLAYER
            player.draw(g2);

            // UI
            ui.draw(g2);
        }

        g2.dispose();
    }

    public void playMusic(int i) {
        
        music.setFile(i);
        music.play();
        music.loop();
    }

    public void stopMusic() {
        music.stop();
    }

    public void playSE(int i) {
        se.setFile(i);
        se.play();
    }

    public void retry() {
        // reset player position and state
        player.setDefaultValues();
        
        // reset time
        ui.playTime = 0;
        
        // reset objects
        aSetter.setObject();
    }

    public void restart() {
        // reset player position and state
        player.setDefaultValues();
        
        // reset time
        ui.playTime = 0;
        
        // reset objects
        aSetter.setObject();
    }
}
