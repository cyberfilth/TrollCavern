package trollcavern.screens;

import asciiPanel.AsciiPanel;
import trollcavern.Creature;

import java.awt.event.KeyEvent;

public class LoseScreen implements Screen {

    private final Creature player;

    public LoseScreen(Creature player) {
        this.player = player;
    }

    @Override
    public void displayOutput(AsciiPanel terminal) {
        terminal.writeCenter("R.I.P.", 3);
        terminal.writeCenter(player.causeOfDeath(), 5);
        terminal.writeCenter("-- press [enter] to restart --", 22);
    }

    public Screen respondToUserInput(KeyEvent key) {
        return key.getKeyCode() == KeyEvent.VK_ENTER ? new PlayScreen() : this;
    }
}
