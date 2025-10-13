package inputs;

import main.GamePanel;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public record KeyboardInputs(GamePanel gamePanel) implements KeyListener {

    @Override
    public void keyPressed(KeyEvent e) {
        gamePanel.handleKeyPressed(e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        gamePanel.handleKeyReleased(e.getKeyCode());
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}
