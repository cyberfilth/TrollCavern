package trollcavern.world;

import trollcavern.Tile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Set dimensions of the world size
 */
public class WorldBuilder {

    private final int width;
    private final int height;
    private final int depth;
    private Tile[][][] tiles;
    private int[][][] regions;
    private int nextRegion;

    public WorldBuilder(int width, int height, int depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.tiles = new Tile[width][height][depth];
        this.regions = new int[width][height][depth];
        this.nextRegion = 1;
    }

    public World build() {
        return new World(tiles);
    }

    /**
     * Randomize the world tiles
     */
    private WorldBuilder randomizeTiles() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < depth; z++) {
                    tiles[x][y][z] = Math.random() < 0.5 ? Tile.FLOOR : Tile.WALL;
                }
            }
        }
        return this;
    }

    /**
     * Repeatedly smooth the randomised world tiles
     */
    private WorldBuilder smooth() {
        Tile[][][] tiles2 = new Tile[width][height][depth];
        for (int time = 0; time < 8; time++) {

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    for (int z = 0; z < depth; z++) {
                        int floors = 0;
                        int rocks = 0;

                        for (int ox = -1; ox < 2; ox++) {
                            for (int oy = -1; oy < 2; oy++) {
                                if (x + ox < 0 || x + ox >= width || y + oy < 0
                                        || y + oy >= height) {
                                    continue;
                                }

                                if (tiles[x + ox][y + oy][z] == Tile.FLOOR) {
                                    floors++;
                                } else {
                                    rocks++;
                                }
                            }
                        }
                        tiles2[x][y][z] = floors >= rocks ? Tile.FLOOR : Tile.WALL;
                    }
                }
            }
            tiles = tiles2;
        }
        return this;
    }

    /**
     * Make caves
     *
     * @return
     */
    public WorldBuilder makeCaves() {
        return randomizeTiles()
                .smooth()
                .createRegions()
                .connectRegions()
                .addExitStairs();
    }

    /**
     * Each location has a number that identifies what region of contiguous open
     * space it belongs to
     *
     * @return
     */
    private WorldBuilder createRegions() {
        regions = new int[width][height][depth];

        for (int z = 0; z < depth; z++) {
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    if (tiles[x][y][z] != Tile.WALL && regions[x][y][z] == 0) {
                        int size = fillRegion(nextRegion++, x, y, z);

                        if (size < 25) {
                            removeRegion(nextRegion - 1, z);
                        }
                    }
                }
            }
        }
        return this;
    }

    /**
     * Zeros out the region number and fills in the cave so it's solid wall.
     */
    private void removeRegion(int region, int z) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (regions[x][y][z] == region) {
                    regions[x][y][z] = 0;
                    tiles[x][y][z] = Tile.WALL;
                }
            }
        }
    }

    /**
     * The fillRegion method does a flood-fill starting with an open tile. It,
     * and any open tile it's connected to, gets assigned the same region
     * number. This is repeated until there are no unassigned empty neighbouring
     * tiles.
     *
     * @param region
     * @param x
     * @param y
     * @param z
     * @return
     */
    private int fillRegion(int region, int x, int y, int z) {
        int size = 1;
        ArrayList<Point> open = new ArrayList<>();
        open.add(new Point(x, y, z));
        regions[x][y][z] = region;

        while (!open.isEmpty()) {
            Point p = open.remove(0);

            for (Point neighbor : p.neighbors8()) {
                if (neighbor.x < 0 || neighbor.y < 0 || neighbor.x >= width || neighbor.y >= height) {
                    continue;
                }

                if (regions[neighbor.x][neighbor.y][neighbor.z] > 0
                        || tiles[neighbor.x][neighbor.y][neighbor.z] == Tile.WALL) {
                    continue;
                }

                size++;
                regions[neighbor.x][neighbor.y][neighbor.z] = region;
                open.add(neighbor);
            }
        }
        return size;
    }

    /**
     * To connect all the regions with stairs we just start at the top and
     * connect them one layer at a time
     *
     * @return
     */
    private WorldBuilder connectRegions() {
        for (int z = 0; z < depth - 1; z++) {
            connectRegionsDown(z);
        }
        return this;
    }

    /**
     * To connect two adjacent layers we look at each region that sits above
     * another region. If they haven't been connected then we connect them.
     *
     * @param z
     */
    private void connectRegionsDown(int z) {
        List<Integer> connected = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int r = regions[x][y][z] * 1000 + regions[x][y][z + 1];
                if (tiles[x][y][z] == Tile.FLOOR
                        && tiles[x][y][z + 1] == Tile.FLOOR
                        && !connected.contains(r)) {
                    connected.add(r);
                    connectRegionsDown(z, regions[x][y][z], regions[x][y][z + 1]);
                }
            }
        }
    }

    /**
     * To connect two regions, we get a list of all the locations where one is
     * directly above the other. Then, based on how much area overlaps, we
     * connect them with stairs going up and stairs going down.
     */
    private void connectRegionsDown(int z, int r1, int r2) {
        List<Point> candidates = findRegionOverlaps(z, r1, r2);
        int stairs = 0;
        do {
            Point p = candidates.remove(0);
            tiles[p.x][p.y][z] = Tile.STAIRS_DOWN;
            tiles[p.x][p.y][z + 1] = Tile.STAIRS_UP;
            stairs++;
        } while (candidates.size() / stairs > 250);
    }

    /**
     * Find out where regions overlap
     *
     * @param z
     * @param r1
     * @param r2
     * @return
     */
    private List<Point> findRegionOverlaps(int z, int r1, int r2) {
        ArrayList<Point> candidates = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (tiles[x][y][z] == Tile.FLOOR
                        && tiles[x][y][z + 1] == Tile.FLOOR
                        && regions[x][y][z] == r1
                        && regions[x][y][z + 1] == r2) {
                    candidates.add(new Point(x, y, z));
                }
            }
        }
        Collections.shuffle(candidates);
        return candidates;
    }

    /**
     * Adds stairs to exit the cavern
     *
     * @return - Stairs
     */
    private WorldBuilder addExitStairs() {
        int x = -1;
        int y = -1;

        do {
            x = (int) (Math.random() * width);
            y = (int) (Math.random() * height);
        }
        while (tiles[x][y][0] != Tile.FLOOR);

        tiles[x][y][0] = Tile.STAIRS_UP;
        return this;
    }
}
