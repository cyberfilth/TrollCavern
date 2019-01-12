package trollcavern.screens;

import asciiPanel.AsciiPanel;
import trollcavern.Creature;
import trollcavern.world.Item;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

public abstract class InventoryBasedScreen implements Screen {
    final Creature player;
    private final String letters;

    InventoryBasedScreen(Creature player) {
        this.player = player;
        this.letters = "abcdefghijklmnopqrstuvwxyz";
    }

    protected abstract String getVerb();

    protected abstract boolean isAcceptable(Item item);

    protected abstract Screen use(Item item);

    /**
     * Display the list in the lower left corner of the screen
     * and prompt user what to do
     *
     * @param terminal - AsciiPanel
     */
    public void displayOutput(AsciiPanel terminal) {
        ArrayList<String> lines = getList();

        int y = 23 - lines.size();
        int x = 4;

        if (lines.size() > 0)
            terminal.clear(' ', x, y, 20, lines.size());

        for (String line : lines) {
            terminal.write(line, x, y++);
        }

        terminal.clear(' ', 0, 23, 80, 1);
        terminal.write("What would you like to " + getVerb() + "?", 2, 23);

        terminal.repaint();
    }

    /**
     * Creates a list of items and a letter corresponding to the inventory space
     *
     * @return - lines of inventory
     */
    private ArrayList<String> getList() {
        ArrayList<String> lines = new ArrayList<String>();
        Item[] inventory = player.inventory().getItems();

        for (int i = 0; i < inventory.length; i++) {
            Item item = inventory[i];

            if (item == null || !isAcceptable(item))
                continue;

            String line = letters.charAt(i) + " - " + item.glyph() + " " + player.nameOf(item);

            if (item == player.weapon() || item == player.armour())
                line += " (equipped)";

            lines.add(line);
        }
        return lines;
    }

    /**
     * The user can press escape to go back to playing the game,
     * select a valid character to use, or some invalid key that
     * will prompt the user again
     *
     * @param key - Key input from user
     * @return - Option selected
     */
    public Screen respondToUserInput(KeyEvent key) {
        char c = key.getKeyChar();

        Item[] items = player.inventory().getItems();

        if (letters.indexOf(c) > -1
                && items.length > letters.indexOf(c)
                && items[letters.indexOf(c)] != null
                && isAcceptable(items[letters.indexOf(c)]))
            return use(items[letters.indexOf(c)]);
        else if (key.getKeyCode() == KeyEvent.VK_ESCAPE)
            return null;
        else
            return this;
    }
}
