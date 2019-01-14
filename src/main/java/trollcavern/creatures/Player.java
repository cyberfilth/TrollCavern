package trollcavern.creatures;

import trollcavern.Creature;
import trollcavern.CreatureAi;
import trollcavern.Tile;
import trollcavern.world.FieldOfView;
import trollcavern.world.Item;

import java.util.List;

public class Player extends CreatureAi {

    private final List<String> messages;
    private final FieldOfView fov;

    public Player(Creature creature, List<String> messages, FieldOfView fov) {
        super(creature);
        this.messages = messages;
        this.fov = fov;
    }

    /**
     * Override the onEnter tile method to allow digging
     *
     * @param x
     * @param y
     * @param z
     * @param tile
     */
    @Override
    public void onEnter(int x, int y, int z, Tile tile) {
        if (tile.isGround()) {
            creature.x = x;
            creature.y = y;
            creature.z = z;

            Item item = creature.item(creature.x, creature.y, creature.z);
            if (item != null)
                creature.notify("There's a " + creature.nameOf(item) + " here.");

        } else if (tile.isDiggable()) {
            creature.dig(x, y, z);
        }
    }

    public Tile rememberedTile(int wx, int wy, int wz) {
        return fov.tile(wx, wy, wz);
    }

    /**
     * Determines what the player character can see
     *
     * @param wx
     * @param wy
     * @param wz
     * @return
     */
    public boolean canSee(int wx, int wy, int wz) {
        return fov.isVisible(wx, wy, wz);
    }

    @Override
    public void onNotify(String message) {
        messages.add(message);
    }

    /**
     * Ensures that the player doesn't automatically change stats when levelling up
     */
    public void onGainLevel() {
    }
}
