package utils;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.imageio.ImageIO;

public class ImageUtils {
    public static BufferedImage loadImage(String path, Class<?> clazz) {
        // Attempt filesystem-based load from ./res first
        try {
            java.io.File file = new java.io.File("res" + path);
            if (file.exists()) {
                return ImageIO.read(file);
            }
        } catch (Exception ignore) {}
        // Fallback to classpath for packaged runs
        try (InputStream is = clazz.getResourceAsStream(path)) {
            if (is != null) {
                return ImageIO.read(is);
            } else {
                if (utils.Constants.DEBUG_LOGS) {
                    System.err.println("❌ Image not found: " + path);
                }
            }
        } catch (Exception e) {
            if (utils.Constants.DEBUG_LOGS) {
                System.err.println("❌ Error loading image: " + path);
            }
        }
        return null;
    }
}

