package trollcavern.world;

import trollcavern.Creature;
import trollcavern.Tile;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The game world container
 */
public class World {

    private final Tile[][][] tiles;

    private final Item[][][] items;

    private final int width;
    private final int height;
    private final int depth;
    private final List<Creature> creatures;

    public World(Tile[][][] tiles) {
        this.tiles = tiles;
        this.width = tiles.length;
        this.height = tiles[0].length;
        this.depth = tiles[0][0].length;
        this.creatures = new ArrayList<>();
        this.items = new Item[width][height][depth];
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public int depth() {
        return depth;
    }

    /**
     * Get the creature at a specific location
     *
     * @param x X Coordinate
     * @param y Y Coordinate
     * @param z Z Coordinate
     * @return
     */
    public Creature creature(int x, int y, int z) {
        for (Creature c : creatures) {
            if (c.x == x && c.y == y && c.z == z) {
                return c;
            }
        }
        return null;
    }

    /**
     * Get details about tiles
     *
     * @param x X Coordinate
     * @param y Y Coordinate
     * @param z Z Coordinate
     * @return
     */
    public Tile tile(int x, int y, int z) {
        if (x < 0 || x >= width || y < 0 || y >= height || z < 0 || z >= depth) {
            return Tile.BOUNDS;
        } else {
            return tiles[x][y][z];
        }
    }

    /**
     * Determine what item is in a location
     *
     * @param x X Coordinate
     * @param y Y Coordinate
     * @param z Z Coordinate
     * @return
     */
    public Item item(int x, int y, int z) {
        return items[x][y][z];
    }

    public char glyph(int x, int y, int z) {
        Creature creature = creature(x, y, z);
        if (creature != null)
            return creature.glyph();

        if (item(x, y, z) != null)
            return item(x, y, z).glyph();

        return tile(x, y, z).glyph();
    }

    public Color color(int x, int y, int z) {
        Creature creature = creature(x, y, z);
        if (creature != null)
            return creature.color();

        if (item(x, y, z) != null)
            return item(x, y, z).color();

        return tile(x, y, z).color();
    }

    /**
     * Removing an item
     *
     * @param x X Coordinate
     * @param y Y Coordinate
     * @param z Z Coordinate
     */
    public void remove(int x, int y, int z) {
        items[x][y][z] = null;
    }

    /**
     * Allow digging into walls
     *
     * @param x X Coordinate
     * @param y Y Coordinate
     * @param z Z Coordinate
     */
    public void dig(int x, int y, int z) {
        if (tile(x, y, z).isDiggable()) {
            tiles[x][y][z] = Tile.FLOOR;
        }
    }

    /**
     * Checks for empty spaces to add items
     *
     * @param item - Item to add
     * @param x    X Coordinate
     * @param y    Y Coordinate
     * @param z    Z Coordinate
     */
    public boolean addAtEmptySpace(Item item, int x, int y, int z) {
        if (item == null)
            return true;

        List<Point> points = new ArrayList<>();
        List<Point> checked = new ArrayList<>();

        points.add(new Point(x, y, z));

        while (!points.isEmpty()) {
            Point p = points.remove(0);
            checked.add(p);

            if (!tile(p.x, p.y, p.z).isGround())
                continue;

            if (items[p.x][p.y][p.z] == null) {
                items[p.x][p.y][p.z] = item;
                Creature c = this.creature(p.x, p.y, p.z);
                if (c != null)
                    c.notify("A %s lands between your feet.", c.nameOf(item));
                return true;
            } else {
                List<Point> neighbors = p.neighbors8();
                neighbors.removeAll(checked);
                points.addAll(neighbors);
            }
        }
        return false;
    }

    /**
     * Adds new creature on an empty space
     *
     * @param creature - Creature to add
     * @param z        - Level of the cavern
     */
    public void addAtEmptyLocation(Creature creature, int z) {
        int x;
        int y;

        do {
            x = (int) (Math.random() * width);
            y = (int) (Math.random() * height);
        } while (!tile(x, y, z).isGround() || creature(x, y, z) != null);

        creature.x = x;
        creature.y = y;
        creature.z = z;
        creatures.add(creature);
    }

    /**
     * Add an item in an empty location
     *
     * @param item  - Item type
     * @param depth - level of the cavern
     */
    public void addAtEmptyLocation(Item item, int depth) {
        int x;
        int y;

        do {
            x = (int) (Math.random() * width);
            y = (int) (Math.random() * height);
        }
        while (!tile(x, y, depth).isGround() || item(x, y, depth) != null);

        items[x][y][depth] = item;
    }

    /**
     * Remove a killed creature from the map
     *
     * @param other - Creature to remove
     */
    public void remove(Creature other) {
        creatures.remove(other);
    }

    /**
     * Tells the creature that its their turn
     */
    public void update() {
        List<Creature> toUpdate = new ArrayList<>(creatures);
        for (Creature creature : toUpdate) {
            creature.update();
        }
    }

    /**
     * Remove an item from the map
     *
     * @param item - Item to remove
     */
    public void remove(Item item) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < depth; z++) {
                    if (items[x][y][z] == item) {
                        items[x][y][z] = null;
                        return;
                    }
                }
            }
        }
    }

    public void add(Creature pet) {
        creatures.add(pet);
    }
}
