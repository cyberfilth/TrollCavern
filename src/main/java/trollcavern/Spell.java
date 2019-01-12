package trollcavern;

public class Spell {

    private final String name;
    private final int manaCost;
    private final Effect effect;

    public Spell(String name, int manaCost, Effect effect) {
        this.name = name;
        this.manaCost = manaCost;
        this.effect = effect;
    }

    public String name() {
        return name;
    }

    public int manaCost() {
        return manaCost;
    }

    public Effect effect() {
        return new Effect(effect);
    }

    @SuppressWarnings("SameReturnValue")
    public boolean requiresTarget() {
        return true;
    }
}
