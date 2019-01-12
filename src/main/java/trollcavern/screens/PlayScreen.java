package trollcavern.screens;

import asciiPanel.AsciiPanel;
import trollcavern.Creature;
import trollcavern.StuffFactory;
import trollcavern.Tile;
import trollcavern.world.FieldOfView;
import trollcavern.world.Item;
import trollcavern.world.World;
import trollcavern.world.WorldBuilder;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class PlayScreen implements Screen {

    private final int screenWidth;
    private final int screenHeight;
    private final List<String> messages;
    private final FieldOfView fov;
    private World world;
    private Creature player;
    private Screen subscreen;

    /**
     * Track the world that's been created
     */
    public PlayScreen() {
        screenWidth = 80;
        screenHeight = 23;
        messages = new ArrayList<>();
        createWorld();
        fov = new FieldOfView(world);

        StuffFactory stuffFactory = new StuffFactory(world);
        createCreatures(stuffFactory);
        createItems(stuffFactory);
    }

    /**
     * Create items
     *
     * @param factory - item factory
     */
    private void createItems(StuffFactory factory) {
        for (int z = 0; z < world.depth(); z++) {
            for (int i = 0; i < world.width() * world.height() / 50; i++) { // changed from 20 to 50
                factory.newRock(z);
            }
            factory.newRation(z);
            factory.randomArmour(z);
            factory.randomWeapon(z);
            factory.randomWeapon(z);
            for (int i = 0; i < z + 1; i++) {
                factory.randomPotion(z);
                factory.randomSpellBook(z);
                factory.randomSpellBook(z);
                factory.randomSpellBook(z);
                factory.randomSpellBook(z);
            }
        }
        factory.newVictoryItem(world.depth() - 1);
    }

    /**
     * Create creatures
     *
     * @param stuffFactory - creature factory
     */
    private void createCreatures(StuffFactory stuffFactory) {
        player = stuffFactory.newPlayer(messages, fov);
        for (int z = 0; z < world.depth(); z++) {
            for (int i = 0; i < 8; i++) {
                stuffFactory.newFungus(z); // Fungus
            }
            for (int i = 0; i < 20; i++) {
                stuffFactory.newBat(z); // Bat
            }
//            for (int i = 0; i < z + 3; i++) { // for (int i = 0; i < z - 2; i++) can be used to start zombies at level 3
//                stuffFactory.newZombie(z, player); // Zombie
//            }
            for (int i = 0; i < z * 2 + 1; i++) {
                stuffFactory.newZombie(z, player); // Zombie
                stuffFactory.newGoblin(z, player); // Goblin
            }
        }
    }

    /**
     * Creates the game world
     */
    private void createWorld() {
        world = new WorldBuilder(90, 32, 5)
                .makeCaves()
                .build();
    }

    /**
     * Tells us how far along the X axis we should scroll
     *
     * @return X
     */
    private int getScrollX() {
        return Math.max(0, Math.min(player.x - screenWidth / 2, world.width() - screenWidth));
    }

    /**
     * Tells us how far along the Y axis we should scroll
     *
     * @return Y
     */
    private int getScrollY() {
        return Math.max(0, Math.min(player.y - screenHeight / 2, world.height() - screenHeight));
    }


    @Override
    public void displayOutput(AsciiPanel terminal) {
        int left = getScrollX();
        int top = getScrollY();

        displayTiles(terminal, left, top);
        SidePanel(terminal);
        displayMessages(terminal, messages);

        terminal.writeCenter("-- Troll Cavern --", 23);
        displayStats(terminal);

        if (subscreen != null)
            subscreen.displayOutput(terminal);
    }

    /**
     * Character sheet
     *
     * @param terminal
     */
    private void displayStats(AsciiPanel terminal) {
        String playerName = String.format("%8s", player.name());
        String playerLevel = String.format("Level %d", player.level());
        String statsHP = String.format("Health: %3d/%3d", player.hp(), player.maxHp());
        String statsMana = String.format("Mana: %d/%d", player.mana(), player.maxMana());
        String statsXP = String.format("XP:     %3d", player.xp());
        String statsHunger = String.format("%8s", hunger());

        terminal.write(playerName, 85, 2);
        terminal.write(playerLevel, 85, 4);
        terminal.write(statsHP, 85, 6);
        terminal.write(statsMana, 85, 8);
        terminal.write(statsXP, 85, 10);
        terminal.write(statsHunger, 85, 12);
    }

    /**
     * Show status messages based on current hunger
     *
     * @return - Hunger level
     */
    private String hunger() {
        if (player.food() < player.maxFood() * 0.1)
            return "Starving";
        else if (player.food() < player.maxFood() * 0.3)
            return "Hungry";
        else if (player.food() > player.maxFood() * 0.95)
            return "Sated";
        else if (player.food() > player.maxFood() * 0.85)
            return "Full belly";
        else
            return "";
    }

    /**
     * Display messages to screen
     */
    private void displayMessages(AsciiPanel terminal, List<String> messages) {
        for (int i = 0; i < messages.size(); i++) {
            terminal.writeCenter(messages.get(i), 25 + i);
        }
        if (subscreen == null)
            messages.clear();
    }

    /**
     * Method to display some tiles. This takes a left and top to know which
     * section of the world it should display.
     */
    private void displayTiles(AsciiPanel terminal, int left, int top) {
        fov.update(player.x, player.y, player.z, player.visionRadius());
        for (int x = 0; x < screenWidth; x++) {
            for (int y = 0; y < screenHeight; y++) {
                int wx = x + left;
                int wy = y + top;
                if (player.canSee(wx, wy, player.z))
                    terminal.write(world.glyph(wx, wy, player.z), x, y, world.color(wx, wy, player.z));
                else
                    terminal.write(fov.tile(wx, wy, player.z).glyph(), x, y, Color.darkGray);
            }
        }
    }

    // ToDo add this method to a separate class

    /**
     * Draw a panel on the right of the screen
     *
     * @param terminal - AsciiPanel
     */
    private void SidePanel(AsciiPanel terminal) {
        terminal.setCursorPosition(81, 1);
        // top row
        terminal.write((char) 218, AsciiPanel.yellow);
        for (int x = 0; x <= 25; x++) {
            terminal.write((char) 196, AsciiPanel.yellow);
        }
        terminal.write((char) 191, AsciiPanel.yellow);

        // vertical box lines
        int yPOSbox = 2;
        for (int x = 0; x <= 18; x++) {
            terminal.setCursorPosition(81, yPOSbox);
            terminal.write((char) 179, AsciiPanel.yellow).write("                          ").write((char) 179, AsciiPanel.yellow);
            yPOSbox++;
        }

        // bottom of row
        terminal.setCursorPosition(81, 21);
        terminal.write((char) 192, AsciiPanel.yellow);
        for (int x = 0; x <= 25; x++) {
            terminal.write((char) 196, AsciiPanel.yellow);
        }
        terminal.write((char) 217, AsciiPanel.yellow);
    }

    /**
     * Scroll the screen based on user input
     *
     * @param key - Key pressed
     * @return - Result
     */
    @Override
    public Screen respondToUserInput(KeyEvent key) {
        int level = player.level();

        if (subscreen != null) {
            subscreen = subscreen.respondToUserInput(key);
        } else {
            switch (key.getKeyCode()) {
                case KeyEvent.VK_LEFT: // LEFT
                case KeyEvent.VK_NUMPAD4:
                case KeyEvent.VK_H:
                    player.moveBy(-1, 0, 0);
                    break;
                case KeyEvent.VK_RIGHT: // RIGHT
                case KeyEvent.VK_NUMPAD6:
                case KeyEvent.VK_L:
                    player.moveBy(1, 0, 0);
                    break;
                case KeyEvent.VK_UP: // UP
                case KeyEvent.VK_NUMPAD8:
                case KeyEvent.VK_K:
                    player.moveBy(0, -1, 0);
                    break;
                case KeyEvent.VK_DOWN: // DOWN
                case KeyEvent.VK_NUMPAD2:
                case KeyEvent.VK_J:
                    player.moveBy(0, 1, 0);
                    break;
                case KeyEvent.VK_Y: // DIAGONAL UP / LEFT
                case KeyEvent.VK_NUMPAD7:
                    player.moveBy(-1, -1, 0);
                    break;
                case KeyEvent.VK_U: // DIAGONAL UP / RIGHT
                case KeyEvent.VK_NUMPAD9:
                    player.moveBy(1, -1, 0);
                    break;
                case KeyEvent.VK_B: // DIAGONAL DOWN / LEFT
                case KeyEvent.VK_NUMPAD1:
                    player.moveBy(-1, 1, 0);
                    break;
                case KeyEvent.VK_N: // DIAGONAL DOWN / RIGHT
                case KeyEvent.VK_NUMPAD3:
                    player.moveBy(1, 1, 0);
                    break;
                case KeyEvent.VK_D:
                    subscreen = new DropScreen(player);
                    break;
                case KeyEvent.VK_E:
                    subscreen = new EatScreen(player);
                    break;
                case KeyEvent.VK_W:
                    subscreen = new EquipScreen(player);
                    break;
                case KeyEvent.VK_X:
                    subscreen = new ExamineScreen(player);
                    break;
                case KeyEvent.VK_SEMICOLON:
                    subscreen = new LookScreen(player, "Looking",
                            player.x - getScrollX(),
                            player.y - getScrollY());
                    break;
                case KeyEvent.VK_T:
                    subscreen = new ThrowScreen(player,
                            player.x - getScrollX(),
                            player.y - getScrollY());
                    break;
                case KeyEvent.VK_F:
                    if (player.weapon() == null || player.weapon().rangedAttackValue() == 0)
                        player.notify("You don't have a ranged weapon equipped.");
                    else
                        subscreen = new FireWeaponScreen(player,
                                player.x - getScrollX(),
                                player.y - getScrollY());
                    break;
                case KeyEvent.VK_Q:
                    subscreen = new QuaffScreen(player);
                    break;
                case KeyEvent.VK_R:
                    subscreen = new ReadScreen(player,
                            player.x - getScrollX(),
                            player.y - getScrollY());
                    break;
            }

            switch (key.getKeyChar()) {
                case 'g':
                case ',':
                    player.pickup();
                    break;
                case '<':
                    if (userIsTryingToExit())
                        return userExits();
                    else
                        player.moveBy(0, 0, -1);
                    break;
                case '>':
                    player.moveBy(0, 0, 1);
                    break;
                case '?':
                    subscreen = new HelpScreen();
                    break;
            }
        }

        if (player.level() > level)
            subscreen = new LevelUpScreen(player, player.level() - level);

        if (subscreen == null)
            world.update();

        if (player.hp() < 1)
            return new LoseScreen(player);

        return this;
    }

    private boolean userIsTryingToExit() {
        return player.z == 0 && world.tile(player.x, player.y, player.z) == Tile.STAIRS_UP;
    }

    private Screen userExits() {
        for (Item item : player.inventory().getItems()) {
            if (item != null && item.name().equals("teddy bear"))
                return new WinScreen();
        }
        player.modifyHp(0, "Died while cowardly fleeing the caves.");
        return new LoseScreen(player);
    }
}
