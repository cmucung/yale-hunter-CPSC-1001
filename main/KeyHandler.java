package main;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class KeyHandler implements KeyListener {
    GamePanel gp;
    public boolean upPressed, downPressed, leftPressed, rightPressed;
    
    public KeyHandler(GamePanel gp) {
        this.gp = gp;
    }
   
    @Override
    public void keyTyped(KeyEvent e) {
    }


    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        // TITLE STATE
        if (gp.gameState == gp.titleState) {

            // CHECK WHICH SCREEN WE ARE ON
            if (gp.ui.titleScreenState == 0) {
                if (code == KeyEvent.VK_W) {
                gp.ui.commandNum--;
                if (gp.ui.commandNum < 0) {
                    gp.ui.commandNum = 2;
                    }
                } 
                if (code == KeyEvent.VK_S) {
                    gp.ui.commandNum++;
                    if (gp.ui.commandNum > 2) {
                        gp.ui.commandNum = 0;
                    }
                }
                if (code == KeyEvent.VK_ENTER) {
                    if (gp.ui.commandNum == 0) {
                        gp.ui.titleScreenState = 1;
                        gp.ui.commandNum = 0;
                    }
                    if (gp.ui.commandNum == 1) {
                        // instructions screen
                        gp.ui.titleScreenState = 2;
                        gp.ui.commandNum = 0;
                    }
                    if (gp.ui.commandNum == 2) {
                        // exit the game
                        System.exit(0);
                    }
                }
            }
            else if (gp.ui.titleScreenState == 1) {
                if (code == KeyEvent.VK_W) {
                    gp.ui.commandNum--;
                    if (gp.ui.commandNum < 0) {
                        gp.ui.commandNum = 2;
                    }
                } 
                if (code == KeyEvent.VK_S) {
                    gp.ui.commandNum++;
                    if (gp.ui.commandNum > 2) {
                        gp.ui.commandNum = 0;
                    }
                }
                if (code == KeyEvent.VK_ENTER) {
                    if (gp.ui.commandNum == 0) {
                        System.out.println("Play as Professor Tim Barron");
                        gp.gameState = gp.playState;
                        // gp.playMusic(0);
                    }
                    if (gp.ui.commandNum == 1) {
                        System.out.println("Play as Professor Ozan Erat");
                    }
                    if (gp.ui.commandNum == 2) {
                        gp.ui.titleScreenState = 0;
                    }
                }
            }
            else if (gp.ui.titleScreenState == 2) {
                if (code == KeyEvent.VK_ENTER) {
                    if (gp.ui.commandNum == 0) {
                        gp.ui.titleScreenState = 0;
                    }
                }
            }
        }

        // PLAY STATE 
        if (gp.gameState == gp.playState) {
            if (code == KeyEvent.VK_W) {
                upPressed = true;
            }
            if (code == KeyEvent.VK_S) {
                downPressed = true;
            }
            if (code == KeyEvent.VK_A) {
                leftPressed = true;
            }
            if (code == KeyEvent.VK_D) {
                rightPressed = true;
            }
       }
    }


    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
       
        if (code == KeyEvent.VK_W) {
            upPressed = false;
        }
        if (code == KeyEvent.VK_S) {
            downPressed = false;
        }
        if (code == KeyEvent.VK_A) {
            leftPressed = false;
        }
        if (code == KeyEvent.VK_D) {
            rightPressed = false;
        }
    }
}
