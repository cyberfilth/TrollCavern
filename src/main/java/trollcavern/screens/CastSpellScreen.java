package trollcavern.screens;

import trollcavern.Creature;
import trollcavern.Spell;

class CastSpellScreen extends TargetBasedScreen {
    private final Spell spell;

    public CastSpellScreen(Creature player, String caption, int sx, int sy, Spell spell) {
        super(player, caption, sx, sy);
        this.spell = spell;
    }

    public void selectWorldCoordinate(int x, int y, int screenX, int screenY) {
        player.castSpell(spell, x, y);
    }
}
