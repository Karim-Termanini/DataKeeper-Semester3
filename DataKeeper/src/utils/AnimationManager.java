package utils;

import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;

/**
 * Dienstprogrammklasse für einfache Bildoperationen, die bei Animationen häufig benötigt werden
 * (z. B. horizontales Spiegeln oder Zuschneiden von Frames).
 * <p>
 * Hinweis: Diese Klasse enthält nur statische Hilfsmethoden und ist nicht instanzierbar.
 */
public final class AnimationManager {

    /**
     * Privater Konstruktor, um die Instanziierung der reinen Utility-Klasse zu verhindern.
     */
    private AnimationManager() {}

    /**
     * Spiegelt ein Bild horizontal (linke/rechte Seite vertauschen).
     *
     * @param image Quellbild
     * @return gespiegelt es Bild (neues Objekt)
     */
    public static BufferedImage flipImageHorizontally(BufferedImage image) {
        AffineTransform transform = AffineTransform.getScaleInstance(-1, 1);
        transform.translate(-image.getWidth(), 0);
        AffineTransformOp transformOp = new AffineTransformOp(transform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return transformOp.filter(image, null);
    }

    /**
     * Schneidet einen rechteckigen Bereich aus einem Bild aus.
     *
     * @param image  Quellbild
     * @param x      linke obere Ecke (x)
     * @param y      linke obere Ecke (y)
     * @param width  Breite des Ausschnitts
     * @param height Höhe des Ausschnitts
     * @return Teilbild (View auf das Quellbild)
     */
    public static BufferedImage cropImage(BufferedImage image, int x, int y, int width, int height) {
        return image.getSubimage(x, y, width, height);
    }
}