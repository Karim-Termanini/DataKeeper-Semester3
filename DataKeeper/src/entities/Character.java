package entities;

import java.awt.image.BufferedImage;
import java.awt.Graphics;

/**
 * Gemeinsames Interface für alle Charaktere im Spiel (Spieler und Gegner).
 * Definiert grundlegende Aktionen, Render-/Update-Zyklus und Abfragen zu Status und Abmessungen.
 */
public interface Character {
    /** Aktualisiert Logik/Zustand pro Frame. */
    void update();
    /** Rendert die aktuelle Frame-Darstellung. */
    void render(Graphics g);
    /** Bewegung nach links starten/ausführen. */
    void moveLeft();
    /** Bewegung nach rechts starten/ausführen. */
    void moveRight();
    /** Springt, sofern möglich. */
    void jump();
    /** Stoppt aktuelle Bewegung. */
    void stop();
    /**
     * Aktuelle Bewegungsgeschwindigkeit.
     * @return Geschwindigkeit in Pixel/Frame
     */
    float getSpeed();
    /** Startet einen Standardangriff. */
    void attack();
    /** Führt einen Spezialangriff aus (falls verfügbar). */
    void specialAttack();
    /**
     * Gibt an, ob sich der Charakter momentan in einer Angriffsanimation befindet.
     * @return true, falls Angriffsaktion aktiv ist
     */
    boolean isAttacking();
    /**
     * Angriffs-Schadenswert für Trefferbestimmung.
     * @return Schaden pro Treffer
     */
    int getAttackDamage();
    /**
     * Trägt dem Charakter Schaden zu.
     * @param damage Schadenswert
     */
    void takeDamage(int damage);
    /** Lädt Animationsressourcen. */
    void loadAnimations();
    /** Setzt die aktuelle Animations-/Zustands-ID. */
    void setAction(int action);
    /** Fortschreiben des Animationszustands. */
    void updateAnimation();
    /** Liefert das aktuell zu zeichnende Animationsbild. */
    BufferedImage getCurrentFrame();
    /**
     * @return true, wenn der Charakter noch lebt
     */
    boolean isAlive();
    /**
     * Aktuelle Lebenspunkte (HP).
     * @return HP
     */
    int getHealth();
    /** Weltkoordinate X. */
    int getX();
    /** Weltkoordinate Y. */
    int getY();
    /** Setzt Position in Weltkoordinaten. */
    void setPosition(int x, int y);
    /** Breite in Pixeln. */
    int getWidth();
    /** Höhe in Pixeln. */
    int getHeight();
}