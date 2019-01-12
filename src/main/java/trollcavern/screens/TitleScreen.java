package trollcavern.screens;

import asciiPanel.AsciiPanel;

import java.awt.event.KeyEvent;

public class TitleScreen implements Screen {

    @Override
    public void displayOutput(AsciiPanel terminal) {
        terminal.write("Troll Cavern", 1, 1);
        terminal.writeCenter("-- press [enter] to start --", 22);

    }

    @Override
    public Screen respondToUserInput(KeyEvent key) {
        return key.getKeyCode() == KeyEvent.VK_ENTER ? new PlayScreen() : this;
    }
}
