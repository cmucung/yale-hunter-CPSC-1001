package tile;

import java.io.IOException;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import main.GamePanel;

public class TileManager {
    GamePanel gp;
    public Tile[] tile;
    public int mapTileNum[][];
    boolean drawPath = false;
    ArrayList<String> fileNames = new ArrayList<>();
    ArrayList<String> collisionStatus = new ArrayList<>();
    
    public TileManager(GamePanel gp) {
        this.gp = gp;

        // READ TILE DATA FILES
        InputStream is = getClass().getResourceAsStream("/res/maps/old campus tile data");
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        
        // GETTING TILE NAMES AND COLLISION INFO FROM THE TILES
        String line;
        try {
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                // Read filename
                String fileName = "grass/" + line.trim();
                fileNames.add(fileName);
                // Read collision status on next line
                String collision = br.readLine();
                if (collision != null) {
                    collisionStatus.add(collision.trim());
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // INITIALIZING TILE ARRAY BASED ON NUMBER OF TILE FILES
        tile = new Tile[fileNames.size()];
        getTileImage();

        // GET MAP WORLD COL AND ROW from the actual map file
        is = getClass().getResourceAsStream("/res/maps/old campus");
        br = new BufferedReader(new InputStreamReader(is));

        try {
            String firstLine = br.readLine();
            if (firstLine != null && !firstLine.isEmpty()) {
                String maxTile[] = firstLine.split(" ");
                gp.maxWorldCol = maxTile.length;
                
                // Count total rows
                int rowCount = 1;
                while (br.readLine() != null) {
                    rowCount++;
                }
                gp.maxWorldRow = rowCount;
            } else {
                // Default map size if file is empty
                gp.maxWorldCol = 50;
                gp.maxWorldRow = 50;
            }
            mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];

            br.close();

        } catch (Exception e) {
            e.printStackTrace();
            // Set defaults if error occurs
            gp.maxWorldCol = 50;
            gp.maxWorldRow = 50;
            mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];
        }

        loadMap("/res/maps/old campus");
    }
    
    public void getTileImage() {
        for (int i = 0; i < fileNames.size(); i++) {
           
            String fileName;
            boolean collision;

            // Get a file name
            fileName = fileNames.get(i);

            // Get collision status
            if (collisionStatus.get(i).equals("true")) {
                collision = true;
            } else {
                collision = false;
            }

            setup(i, fileName, collision);
        }
    }

    public void setup(int index, String imagePath, boolean collision) {
        UtilityTool uTool = new UtilityTool();

        try {
            tile[index] = new Tile();
            tile[index].image = ImageIO.read(getClass().getResourceAsStream("/res/tiles/" + imagePath));
            tile[index].image = uTool.scaleImage(tile[index].image, gp.tileSize, gp.tileSize);
            tile[index].collision = collision;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadMap(String filePath) {
        try {
            InputStream is = getClass().getResourceAsStream(filePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            int col = 0;
            int row = 0;

            while (row < gp.maxWorldRow) {
                String line = br.readLine();
                if (line == null) break;
                
                String numbers[] = line.split(" ");
                col = 0;
                
                while (col < gp.maxWorldCol && col < numbers.length) {
                    int num = Integer.parseInt(numbers[col]);
                    mapTileNum[col][row] = num;
                    col++;
                }
                row++;
            }
            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2) {
        int worldCol = 0;
        int worldRow = 0;

        while (worldCol < gp.maxWorldCol && worldRow < gp.maxWorldRow) {
            int tileNum = mapTileNum[worldCol][worldRow];

            int worldX = worldCol * gp.tileSize;
            int worldY = worldRow * gp.tileSize;
            int screenX = worldX - gp.player.worldX + gp.player.screenX;
            int screenY = worldY - gp.player.worldY + gp.player.screenY;

            if (worldX + gp.tileSize > gp.player.worldX - gp.player.screenX - gp.tileSize &&
                worldX - gp.tileSize < gp.player.worldX + gp.player.screenX + gp.tileSize &&
                worldY + gp.tileSize > gp.player.worldY - gp.player.screenY - gp.tileSize &&
                worldY - gp.tileSize < gp.player.worldY + gp.player.screenY + gp.tileSize) {
                g2.drawImage(tile[tileNum].image, screenX, screenY, null);
            }
            worldCol++;

            if (worldCol == gp.maxWorldCol) {
                worldCol = 0;
                worldRow++;
            }
        }
    }
}
