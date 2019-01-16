/**
 * Fungus - doesn't move or attack
 * randomly creates spores of itself
 */

package trollcavern.creatures;

import trollcavern.Creature;
import trollcavern.CreatureAi;
import trollcavern.generators.StuffFactory;

public class Fungus extends CreatureAi {
    private final StuffFactory factory;
    private int spreadcount;

    public Fungus(Creature creature, StuffFactory factory) {
        super(creature);
        this.factory = factory;
    }

    public void onUpdate() {
        if (spreadcount < 5 && Math.random() < 0.001) {
            spread();
        }
    }

    /**
     * Fungus creates spores
     */
    private void spread() {
        int x = creature.x + (int) (Math.random() * 11) - 5;
        int y = creature.y + (int) (Math.random() * 11) - 5;
        if (creature.canEnter(x, y, creature.z))
            return;

        creature.doAction("releases spores");
        Creature child = factory.newFungus(creature.z);
        child.x = x;
        child.y = y;
        child.z = creature.z;
        spreadcount++;
    }
}
