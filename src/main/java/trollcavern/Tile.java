package trollcavern;

import asciiPanel.AsciiPanel;
import java.awt.*;

import static trollcavern.screens.ColourPalette.*;


public enum Tile {

    FLOOR((char) 250, caveDarkerBrown, "A dirt and rock cave floor."),
    WALL((char) 177, caveBrown, "A dirt and rock cave wall."),
    BOUNDS('x', AsciiPanel.brightBlack, "Beyond the edge of the world."),
    STAIRS_DOWN('>', AsciiPanel.white, "A stone staircase that goes down."),
    STAIRS_UP('<', AsciiPanel.white, "A stone staircase that goes up."),
    UNKNOWN(' ', AsciiPanel.white, "(unknown)");

    private final char glyph;
    private final Color color;
    private final String description;

    Tile(char glyph, Color color, String description) {
        this.glyph = glyph;
        this.color = color;
        this.description = description;
    }

    public char glyph() {
        return glyph;
    }

    public Color color() {
        return color;
    }

    public String details() {
        return description;
    }

    /**
     * Allows this tile to be dug through
     */
    public boolean isDiggable() {
        return this == Tile.WALL;
    }

    /**
     * Checks if the tile can be walked on or dug
     */
    public boolean isGround() {
        return this != WALL && this != BOUNDS;
    }
}
