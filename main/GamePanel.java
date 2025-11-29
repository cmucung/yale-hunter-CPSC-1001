package main;
import javax.swing.JPanel;

import entity.Player;
import entity.Student;
import tile.TileManager;

import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;


public class GamePanel extends JPanel implements Runnable {
    // Game panel code here
    // screen settings
    final int originalTileSize = 16; // 16x16 tile
    final int scale = 3;
   
    public final int tileSize = originalTileSize * scale; // 48x48 tile
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol; // 768 pixels
    public final int screenHeight = tileSize * maxScreenRow; // 576 pixels

    public int maxWorldCol;
    public int maxWorldRow;
    public final int worldWidth = tileSize * maxWorldCol;
    public final int worldHeight = tileSize * maxWorldRow;
    public final int maxMap = 10;
    public int currentTimeMap = 0;


    // FPS
    int FPS = 60;

    // SYSTEM
    public TileManager tileM = new TileManager(this);
    KeyHandler keyH = new KeyHandler(this);
    public UI ui = new UI(this);
    Thread gameThread;
    public CollisionChecker cChecker = new CollisionChecker(this);

    // ENTITY
    public Player player = new Player(this, keyH);
    public Student[] students = new Student[4]; // Array to hold multiple students

    // GAME STATE
    public int gameState;
    public final int titleState = 0;
    public final int playState = 1;
    public final int pauseState = 2;


    // Set player's default position
    int playerX = 100;
    int playerY = 100;
    int playerSpeed = 4;


    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
    }

    public void setupGame() {
        gameState = titleState;
        
        // Initialize students with valid positions
        for (int i = 0; i < students.length; i++) {
            students[i] = new Student(this);
            students[i].setValidPosition();
        }
    }


    public void startGameThread() {
        setupGame();
        gameThread = new Thread(this);
        gameThread.start();
    }

    /*
    @Override
    public void run() {
        // Game loop code here
        double drawInterval = 1000000000 / FPS; // 0.01666 seconds
        double nextDrawTime = System.nanoTime() + drawInterval;


        while (gameThread != null) {


            update();


            repaint();


            try {
                double remainingTime = nextDrawTime - System.nanoTime();
                remainingTime = remainingTime / 1000000;


                if (remainingTime < 0) {
                    remainingTime = 0;
                }


                Thread.sleep((long) remainingTime);


                nextDrawTime += drawInterval;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    } 
    */

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
            player.update();
            
            // Update all students
            for (int i = 0; i < students.length; i++) {
                if (students[i] != null) {
                    students[i].update();
                }
            }
        }
    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        // Draw title screen
        if (gameState == titleState) {
            ui.draw(g2);
        } else {
            // Draw tiles first (background)
            tileM.draw(g2);
        
            // Draw students
            for (int i = 0; i < students.length; i++) {
                if (students[i] != null) {
                    students[i].draw(g2, player);
                }
            }
            
            // Draw player second (foreground)
            player.draw(g2);

            // Draw UI last (on top of everything)
            ui.draw(g2);
        }

        g2.dispose();
    }
}
