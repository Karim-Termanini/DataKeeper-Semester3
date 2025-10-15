package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class VictoryScreen {
    private int tick = 0;
    public void update() { tick++; }
    public void render(Graphics g, int w, int h) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(0,0,0,200));
        g2.fillRect(0,0,w,h);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 48));
        String title = "SIEG!";
        g2.drawString(title, (w - g2.getFontMetrics().stringWidth(title))/2, h/3);
        // Epilogue text inline
        g2.setFont(new Font("Arial", Font.PLAIN, 20));
        String[] lines = new String[]{
            "Bit hat das System vor den Glitches geschützt.",
            "Es war nicht nur ein digitales Wesen,",
            "sondern vielmehr die Verkörperung des digitalen Bewusstseins des Kindes,",
            "dem der Computer gehörte, und dass die Erinnerungen, die er gesammelt hat,",
            "seine eigenen sind...."
        };
        int y = h/3 + 60;
        for (String line : lines) {
            g2.drawString(line, (w - g2.getFontMetrics().stringWidth(line))/2, y);
            y += 28;
        }
        g2.setFont(new Font("Arial", Font.ITALIC, 18));
        String hint = "ENTER: Zurück zum Menü";
        // Subtle pulsing opacity for the hint using tick
        double s = 0.5 + 0.5 * Math.sin(tick / 12.0);
        int alpha = (int)(120 + 100 * s); // range ~120-220
        alpha = Math.max(0, Math.min(255, alpha));
        g2.setColor(new Color(255, 255, 255, alpha));
        g2.drawString(hint, (w - g2.getFontMetrics().stringWidth(hint))/2, h - 80);
    }
}
