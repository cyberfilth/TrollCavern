/**
 * Goblin - Smart monster that can use weapons
 */
package trollcavern.creatures;

import trollcavern.Creature;
import trollcavern.CreatureAi;

public class Goblin extends CreatureAi {
    private final Creature player;

    public Goblin(Creature creature, Creature player) {
        super(creature);
        this.player = player;
    }

    /**
     * Goblin will, in order of priority, try: ranged attack, throw attack,
     * melee attack, pickup stuff, and wander if they can't do anything else.
     */
    public void onUpdate() {
        if (canUseBetterEquipment())
            useBetterEquipment();
        else if (canRangedWeaponAttack(player))
            creature.rangedWeaponAttack(player);
        else if (canThrowAt(player))
            creature.throwItem(getWeaponToThrow(), player.x, player.y, player.z);
        else if (creature.canSee(player.x, player.y, player.z))
            hunt(player);
        else if (canPickup())
            creature.pickup();
        else
            wander();
    }
}