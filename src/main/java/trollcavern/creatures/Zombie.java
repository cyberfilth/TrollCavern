/**
 * Zombie - wanders around until it sees the player,
 * will then chase the player
 */
package trollcavern.creatures;

import trollcavern.Creature;
import trollcavern.CreatureAi;

public class Zombie extends CreatureAi {
    private final Creature player;

    public Zombie(Creature creature, Creature player) {
        super(creature);
        this.player = player;
    }

    /**
     * Zombie may wander or do nothing for its turn
     */
    public void onUpdate() {
        if (Math.random() < 0.2)
            return;

        if (creature.canSee(player.x, player.y, player.z))
            hunt(player);
        else
            wander();
    }
}
