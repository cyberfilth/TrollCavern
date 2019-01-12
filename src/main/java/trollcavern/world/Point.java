package trollcavern.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A class that represents a point in space
 */
public class Point {

    public final int x;
    public final int y;
    public final int z;

    public Point(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Two points that represent the same location should be treated as equal.
     *
     * @return
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + x;
        result = prime * result + y;
        result = prime * result + z;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Point)) {
            return false;
        }
        Point other = (Point) obj;
        if (x != other.x) {
            return false;
        }
        if (y != other.y) {
            return false;
        }
        return z == other.z;
    }

    /**
     * Ask a point for a list of it's eight neighbours The list is shuffled
     * before returning so we don't introduce bias
     *
     * @return
     */
    public List<Point> neighbors8() {
        List<Point> points = new ArrayList<>();

        for (int ox = -1; ox < 2; ox++) {
            for (int oy = -1; oy < 2; oy++) {
                if (ox == 0 && oy == 0)
                    continue;

                int nx = x + ox;
                int ny = y + oy;

                points.add(new Point(nx, ny, z));
            }
        }

        Collections.shuffle(points);
        return points;
    }
}
