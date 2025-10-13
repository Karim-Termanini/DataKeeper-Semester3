package utils;

import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;

public class AnimationManager {
    public static BufferedImage flipImageHorizontally(BufferedImage image) {
        AffineTransform transform = AffineTransform.getScaleInstance(-1, 1);
        transform.translate(-image.getWidth(), 0);
        AffineTransformOp transformOp = new AffineTransformOp(transform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return transformOp.filter(image, null);
    }

    public static BufferedImage cropImage(BufferedImage image, int x, int y, int width, int height) {
        return image.getSubimage(x, y, width, height);
    }
}