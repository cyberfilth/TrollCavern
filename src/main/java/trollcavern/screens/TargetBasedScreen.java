package trollcavern.screens;

import asciiPanel.AsciiPanel;
import trollcavern.Creature;
import trollcavern.world.Line;
import trollcavern.world.Point;

import java.awt.event.KeyEvent;

public class TargetBasedScreen implements Screen {
    final Creature player;
    private final int sx;
    private final int sy;
    String caption;
    private int x;
    private int y;

    TargetBasedScreen(Creature player, String caption, int sx, int sy) {
        this.player = player;
        this.caption = caption;
        this.sx = sx;
        this.sy = sy;
    }


    /**
     * Draw a line from the player to the target
     *
     * @param terminal - Update AsciiPanel display
     */
    @Override
    public void displayOutput(AsciiPanel terminal) {
        for (Point p : new Line(sx, sy, sx + x, sy + y)) {
            if (p.x < 0 || p.x >= 80 || p.y < 0 || p.y >= 24)
                continue;
            terminal.write('*', p.x, p.y, AsciiPanel.brightMagenta);
        }
        terminal.clear(' ', 0, 23, 80, 1);
        terminal.write(caption, 0, 23);
    }

    /**
     * The user can change what's being targeted with the movement keys, select a target with Enter,
     * or cancel with Escape. If the user tries to target something it can't, like firing out of range,
     * then we go back to where we were targeting before
     *
     * @param key - Key input
     * @return - coordinates
     */
    @Override
    public Screen respondToUserInput(KeyEvent key) {
        int px = x;
        int py = y;

        switch (key.getKeyCode()) {
            case KeyEvent.VK_LEFT: // LEFT
            case KeyEvent.VK_NUMPAD4:
            case KeyEvent.VK_H:
                x--;
                break;
            case KeyEvent.VK_RIGHT: // RIGHT
            case KeyEvent.VK_NUMPAD6:
            case KeyEvent.VK_L:
                x++;
                break;
            case KeyEvent.VK_UP: // UP
            case KeyEvent.VK_NUMPAD8:
            case KeyEvent.VK_K:
                y--;
                break;
            case KeyEvent.VK_DOWN: // DOWN
            case KeyEvent.VK_NUMPAD2:
            case KeyEvent.VK_J:
                y++;
                break;
            case KeyEvent.VK_Y: // DIAGONAL UP / LEFT
            case KeyEvent.VK_NUMPAD7:
                x--;
                y--;
                break;
            case KeyEvent.VK_U: // DIAGONAL UP / RIGHT
            case KeyEvent.VK_NUMPAD9:
                x++;
                y--;
                break;
            case KeyEvent.VK_B: // DIAGONAL DOWN / LEFT
            case KeyEvent.VK_NUMPAD1:
                x--;
                y++;
                break;
            case KeyEvent.VK_N: // DIAGONAL DOWN / RIGHT
            case KeyEvent.VK_NUMPAD3:
                x++;
                y++;
                break;
            case KeyEvent.VK_ENTER:
                selectWorldCoordinate(player.x + x, player.y + y, sx + x, sy + y);
                return null;
            case KeyEvent.VK_ESCAPE:
                return null;
        }

        if (!isAcceptable(player.x + x, player.y + y)) {
            x = px;
            y = py;
        }

        enterWorldCoordinate(player.x + x, player.y + y, sx + x, sy + y);

        return this;
    }

    /**
     * Checks that the selected tile is an acceptable target
     *
     * @param x - X Coordinate
     * @param y - Y Coordinate
     * @return - True / False target is acceptable
     */
    boolean isAcceptable(int x, int y) {
        return true;
    }

    void enterWorldCoordinate(int x, int y, int screenX, int screenY) {
    }

    void selectWorldCoordinate(int x, int y, int screenX, int screenY) {
    }
}
