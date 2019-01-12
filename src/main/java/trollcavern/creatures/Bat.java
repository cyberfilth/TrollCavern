/**
 * Bat - Moves at 2x speed
 */
package trollcavern.creatures;

import trollcavern.Creature;
import trollcavern.CreatureAi;

public class Bat extends CreatureAi {
    public Bat(Creature creature) {
        super(creature);
    }

    public void onUpdate() {
        wander();
        wander();
    }
}
