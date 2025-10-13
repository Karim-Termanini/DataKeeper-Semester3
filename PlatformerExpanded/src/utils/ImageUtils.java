package utils;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.imageio.ImageIO;

public class ImageUtils {
    public static BufferedImage loadImage(String path, Class<?> clazz) {
        try (InputStream is = clazz.getResourceAsStream(path)) {
            if (is != null) {
                return ImageIO.read(is);
            } else {
                System.err.println("❌ Image not found: " + path);
            }
        } catch (Exception e) {
            System.err.println("❌ Error loading image: " + path);
        }
        return null;
    }
}

