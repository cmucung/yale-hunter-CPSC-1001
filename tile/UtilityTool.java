package tile;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class UtilityTool {
    
    public BufferedImage scaleImage(BufferedImage original, int width, int height) {
        // Always use TYPE_INT_ARGB to preserve colors properly
        BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = scaledImage.createGraphics();
        g2.drawImage(original, 0, 0, width, height, null);
        g2.dispose();
        return scaledImage;
    }
}
