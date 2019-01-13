/**
 * Rat
 */
package trollcavern.creatures;

import trollcavern.Creature;
import trollcavern.CreatureAi;
import trollcavern.screens.PlayScreen;

public class Rat extends CreatureAi {

    public Rat(Creature creature) {
        super(creature);

    }

    public void onUpdate() {// if can see player, squeaks and runs away
        wander();


    }
}
