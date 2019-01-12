package trollcavern.screens;

import trollcavern.Creature;
import trollcavern.world.Item;

public class DropScreen extends InventoryBasedScreen {
    public DropScreen(Creature player) {
        super(player);
    }

    /**
     * Ask user what they want to drop
     *
     * @return - Item to drop
     */
    protected String getVerb() {
        return "drop";
    }

    /**
     * Can item be dropped
     *
     * @param item - Item to drop
     * @return - Can be dropped true or false
     */
    protected boolean isAcceptable(Item item) {
        return true;
    }

    /**
     * Drop the item
     *
     * @param item - Item to drop
     * @return - null
     */
    protected Screen use(Item item) {
        player.drop(item);
        return null;
    }
}
