package trollcavern.world;

import trollcavern.Effect;
import trollcavern.Spell;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Item {

    private final char glyph;
    private final Color color;
    private final String name;
    private final String appearance;
    private final List<Spell> writtenSpells;
    private int foodValue;
    private int attackValue;
    private int defenseValue;
    private int thrownAttackValue;
    private int rangedAttackValue;
    private Effect quaffEffect;

    public Item(char glyph, Color color, String name, String appearance) {
        this.glyph = glyph;
        this.color = color;
        this.name = name;
        this.appearance = appearance == null ? name : appearance;
        this.thrownAttackValue = 1;
        this.writtenSpells = new ArrayList<>();
    }

    public char glyph() {
        return glyph;
    }

    public Color color() {
        return color;
    }

    public String name() {
        return name;
    }

    public String appearance() {
        return appearance;
    }

    public int foodValue() {
        return foodValue;
    }

    public void modifyFoodValue(int amount) {
        foodValue += amount;
    }

    public int attackValue() {
        return attackValue;
    }

    public void modifyAttackValue(int amount) {
        attackValue += amount;
    }

    public int defenseValue() {
        return defenseValue;
    }

    public void modifyDefenseValue(int amount) {
        defenseValue += amount;
    }

    public int thrownAttackValue() {
        return thrownAttackValue;
    }

    public void modifyThrownAttackValue(int amount) {
        thrownAttackValue += amount;
    }

    public int rangedAttackValue() {
        return rangedAttackValue;
    }

    public void modifyRangedAttackValue(int amount) {
        rangedAttackValue += amount;
    }

    public Effect quaffEffect() {
        return quaffEffect;
    }

    public void setQuaffEffect(Effect effect) {
        this.quaffEffect = effect;
    }

    public List<Spell> writtenSpells() {
        return writtenSpells;
    }

    public void addWrittenSpell(String name, int manaCost, Effect effect) {
        writtenSpells.add(new Spell(name, manaCost, effect));
    }

    /**
     * Inventory details
     *
     * @return - Item details
     */
    public String details() {
        String details = "";

        if (attackValue != 0)
            details += "  attack:" + attackValue;

        if (thrownAttackValue != 1)
            details += "  thrown:" + thrownAttackValue;

        if (rangedAttackValue > 0)
            details += "  ranged:" + rangedAttackValue;

        if (defenseValue != 0)
            details += "  defense:" + defenseValue;

        if (foodValue != 0)
            details += "  food:" + foodValue;

        return details;
    }
}
