package trollcavern.screens;

import trollcavern.Creature;
import trollcavern.world.Item;

public class ReadScreen extends InventoryBasedScreen {

    private final int sx;
    private final int sy;

    public ReadScreen(Creature player, int sx, int sy) {
        super(player);
        this.sx = sx;
        this.sy = sy;
    }

    @Override
    protected String getVerb() {
        return "read";
    }

    @Override
    protected boolean isAcceptable(Item item) {
        return !item.writtenSpells().isEmpty();
    }

    @Override
    protected Screen use(Item item) {
        return new ReadSpellScreen(player, sx, sy, item);
    }
}
