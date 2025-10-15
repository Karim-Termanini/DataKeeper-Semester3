package ui;

import java.awt.*;

public class EpilogueScreen {
    public void update() {}

    public void render(Graphics g, int w, int h) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(10, 10, 20));
        g.fillRect(0, 0, w, h);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 28));

        String[] lines = new String[]{
            "Es war nicht nur ein digitales Wesen,",
            "sondern vielmehr die Verkörperung des",
            "digitalen Bewusstseins des Kindes,",
            "dem der Computer gehörte.",
            "Die Erinnerungen, die es geschützt hat,",
            "sind seine eigenen."
        };

        int y = h/2 - lines.length * 20;
        for (String line : lines) {
            drawCentered(g, line, w, y);
            y += 40;
        }

        g.setFont(new Font("Arial", Font.PLAIN, 18));
        drawCentered(g, "Drücke ENTER, um zum Menü zurückzukehren", w, h - 60);
    }

    private void drawCentered(Graphics g, String text, int w, int y) {
        FontMetrics fm = g.getFontMetrics();
        int x = (w - fm.stringWidth(text)) / 2;
        g.drawString(text, x, y);
    }
}
