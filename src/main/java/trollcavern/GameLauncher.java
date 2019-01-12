package trollcavern;

import asciiPanel.AsciiFont;
import asciiPanel.AsciiPanel;
import trollcavern.screens.Screen;
import trollcavern.screens.TitleScreen;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GameLauncher extends JFrame implements KeyListener {
    private static final long serialVersionUID = -7895776404902719682L;

    private final AsciiPanel terminal;
    private Screen screen;

    private GameLauncher() {
        super("Troll Cavern");
        terminal = new AsciiPanel(110, 40, AsciiFont.CP437_9x16);
        add(terminal);
        pack();
        screen = new TitleScreen();
        addKeyListener(this);
        repaint();
    }

    public static void main(String[] args) {
        GameLauncher app = new GameLauncher();
        app.setLocationRelativeTo(null); // Place JFrame in centre of screen
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        app.setVisible(true);
    }

    @Override
    public void repaint() {
        terminal.clear();
        screen.displayOutput(terminal);
        super.repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        screen = screen.respondToUserInput(e);
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}
