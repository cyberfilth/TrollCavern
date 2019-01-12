package trollcavern.world;

import trollcavern.Creature;

import java.util.List;

public class Path {

    private static final PathFinder pf = new PathFinder();

    private final List<Point> points;

    public Path(Creature creature, int x, int y) {
        points = pf.findPath(creature,
                new Point(creature.x, creature.y, creature.z),
                new Point(x, y, creature.z),
                300);
    }

    public List<Point> points() {
        return points;
    }
}
