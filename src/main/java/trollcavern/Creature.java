package trollcavern;

import trollcavern.world.Item;
import trollcavern.world.Line;
import trollcavern.world.Point;
import trollcavern.world.World;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class Creature {
    private final World world;
    private final char glyph;
    private final Color color;
    private final String name;
    private final Inventory inventory;
    private final List<Effect> effects;
    public final int x;
    public final int y;
    public final int z;
    private CreatureAi ai;
    private int maxHp;
    private int hp;
    private int attackValue;
    private int defenseValue;
    private int visionRadius;
    private int maxFood;
    private int food;
    private Item weapon;
    private Item armour;
    private int xp;
    private int level;
    private int regenHpCooldown;
    private int regenHpPer1000;
    private int maxMana;
    private int mana;
    private int regenManaCooldown;
    private int regenManaPer1000;
    private String causeOfDeath;
    private int detectCreatures;

    public Creature(World world, char glyph, Color color, String name, int maxHp, int attack, int defense) {
        this.world = world;
        this.glyph = glyph;
        this.color = color;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.attackValue = attack;
        this.defenseValue = defense;
        this.visionRadius = 9;
        this.name = name;
        this.inventory = new Inventory(20);
        this.maxFood = 1000;
        this.food = maxFood / 3 * 2;
        this.level = 1;
        this.regenHpPer1000 = 10;
        this.effects = new ArrayList<>();
        this.maxMana = 5;
        this.mana = maxMana;
        this.regenManaPer1000 = 20;
        x = 0;
        y = 0;
        z = 0;
    }

    public char glyph() {
        return glyph;
    }

    public Color color() {
        return color;
    }

    public void setCreatureAi(CreatureAi ai) {
        this.ai = ai;
    }

    public int maxHp() {
        return maxHp;
    }

    public void modifyMaxHp(int amount) {
        maxHp += amount;
    }

    public int hp() {
        return hp;
    }

    public void modifyAttackValue(int value) {
        attackValue += value;
    }

    private int attackValue() {
        return attackValue
                + (weapon == null ? 0 : weapon.attackValue())
                + (armour == null ? 0 : armour.attackValue());
    }

    public void modifyDefenseValue(int value) {
        defenseValue += value;
    }

    private int defenseValue() {
        return defenseValue
                + (weapon == null ? 0 : weapon.defenseValue())
                + (armour == null ? 0 : armour.defenseValue());
    }

    public void modifyVisionRadius(int value) {
        visionRadius += value;
    }

    public int visionRadius() {
        return visionRadius;
    }

    public String name() {
        return name;
    }

    public Inventory inventory() {
        return inventory;
    }

    public int maxFood() {
        return maxFood;
    }

    public int food() {
        return food;
    }

    public Item weapon() {
        return weapon;
    }

    public Item armour() {
        return armour;
    }

    public int xp() {
        return xp;
    }

    public void modifyXp(int amount) {
        xp += amount;

        notify("You %s %d xp.", amount < 0 ? "lose" : "gain", amount);

        while (xp > (int) (Math.pow(level, 1.75) * 25)) {
            level++;
            doAction("advance to level %d", level);
            ai.onGainLevel();
            modifyHp(level * 2, "Died from having a negative level?");
        }
    }

    public int level() {
        return level;
    }

    public void modifyRegenHpPer1000(int amount) {
        regenHpPer1000 += amount;
    }

    public List<Effect> effects() {
        return effects;
    }

    public int maxMana() {
        return maxMana;
    }

    public void modifyMaxMana(int amount) {
        maxMana += amount;
    }

    public int mana() {
        return mana;
    }

    public void modifyMana(int amount) {
        mana = Math.max(0, Math.min(mana + amount, maxMana));
    }

    public void modifyRegenManaPer1000(int amount) {
        regenManaPer1000 += amount;
    }

    /**
     * When a creature moves onto a new tile
     *
     * @param mx X coordinate
     * @param my Y coordinate
     * @param mz Z coordinate
     */
    public void moveBy(int mx, int my, int mz) {
        if (mx == 0 && my == 0 && mz == 0)
            return;

        Tile tile = world.tile(x + mx, y + my, z + mz);

        if (mz == -1) {
            if (tile == Tile.STAIRS_DOWN) {
                doAction("walk up the stairs to level %d", z + mz + 1);
            } else {
                doAction("try to go up but are stopped by the cave ceiling");
                return;
            }
        } else if (mz == 1) {
            if (tile == Tile.STAIRS_UP) {
                doAction("walk down the stairs to level %d", z + mz + 1);
            } else {
                doAction("try to go down but are stopped by the cave floor");
                return;
            }
        }

        Creature other = world.creature(x + mx, y + my, z + mz);

        modifyFood(-1);

        if (other == null)
            ai.onEnter(x + mx, y + my, z + mz, tile);
        else
            meleeAttack(other);
    }

    /**
     * Melee attack
     *
     * @param other - Creature to attack
     */
    private void meleeAttack(Creature other) {
        commonAttack(other, attackValue(), "attack the %s for %d damage", other.name);
    }

    /**
     * Throw attack, if a potion is thrown its effects are applied to the target
     *
     * @param item  - Item to throw
     * @param other - Creature to attack
     */
    private void throwAttack(Item item, Creature other) {
        commonAttack(other, attackValue / 2 + item.thrownAttackValue(), "throw a %s at the %s for %d damage", nameOf(item), other.name);
        other.addEffect(item.quaffEffect());
    }

    /**
     * Ranged weapon attack
     *
     * @param other - Creature to attack
     */
    public void rangedWeaponAttack(Creature other) {
        commonAttack(other, attackValue / 2 + weapon.rangedAttackValue(), "fire a %s at the %s for %d damage", weapon.name(), other.name);
    }

    private void commonAttack(Creature other, int attack, String action, Object... params) {
        modifyFood(-2);

        int amount = Math.max(0, attack - other.defenseValue());

        amount = (int) (Math.random() * amount) + 1;

        Object[] params2 = new Object[params.length + 1];
        System.arraycopy(params, 0, params2, 0, params.length);
        params2[params2.length - 1] = amount;

        doAction(action, params2);

        other.modifyHp(-amount, "Killed by a " + name);

        if (other.hp < 1)
            gainXp(other);
    }

    private void gainXp(Creature other) {
        int amount = other.maxHp
                + other.attackValue()
                + other.defenseValue()
                - level;

        if (amount > 0)
            modifyXp(amount);
    }


    public String causeOfDeath() {
        return causeOfDeath;
    }

    public void modifyHp(int amount, String causeOfDeath) {
        hp += amount;
        this.causeOfDeath = causeOfDeath;

        if (hp > maxHp) {
            hp = maxHp;
        } else if (hp < 1) {
            doAction("die");
            leaveCorpse();
            world.remove(this);
        }
    }

    /**
     * Corpse left behind
     */
    private void leaveCorpse() {
        Item corpse = new Item('%', color, name + " corpse", null);
        corpse.modifyFoodValue(maxHp * 5);
        world.addAtEmptySpace(corpse, x, y, z);
        for (Item item : inventory.getItems()) {
            if (item != null)
                drop(item);
        }
    }

    /**
     * Allow creature to dig through walls
     * removes 5 from Hunger
     *
     * @param wx X coordinate
     * @param wy Y coordinate
     * @param wz Z coordinate
     */
    public void dig(int wx, int wy, int wz) {
        modifyFood(-10);
        world.dig(wx, wy, wz);
        doAction("dig");
    }

    /**
     * Creature takes a turn
     * removes 1 from Hunger
     * regenerates Health
     */
    public void update() {
        modifyFood(-1);
        regenerateHealth();
        regenerateMana();
        updateEffects();
        ai.onUpdate();
    }

    /**
     * Effects of potions on creature
     */
    private void updateEffects() {
        List<Effect> done = new ArrayList<>();

        for (Effect effect : effects) {
            effect.update(this);
            if (effect.isDone()) {
                effect.end(this);
                done.add(effect);
            }
        }

        effects.removeAll(done);
    }

    private void regenerateHealth() {
        regenHpCooldown -= regenHpPer1000;
        if (regenHpCooldown < 0) {
            if (hp < maxHp) {
                modifyHp(1, "Died from regenerating health?");
                modifyFood(-1);
            }
            regenHpCooldown += 1000;
        }
    }

    private void regenerateMana() {
        regenManaCooldown -= regenManaPer1000;
        if (regenManaCooldown < 0) {
            if (mana < maxMana) {
                modifyMana(1);
                modifyFood(-1);
            }
            regenManaCooldown += 1000;
        }
    }

    /**
     * Checks that a tile can enter a space
     *
     * @param wx X coordinate
     * @param wy Y coordinate
     * @param wz Z coordinate
     * @return - Can enter true or false
     */
    public boolean canEnter(int wx, int wy, int wz) {
        return !world.tile(wx, wy, wz).isGround() || world.creature(wx, wy, wz) != null;
    }

    /**
     * Allows creatures to create messages
     *
     * @param message - Message
     * @param params  - Parameters
     */
    public void notify(String message, Object... params) {
        ai.onNotify(String.format(message, params));
    }

    /**
     * Notify nearby creatures when something happens
     *
     * @param message
     * @param params
     */
    public void doAction(String message, Object... params) {
        for (Creature other : getCreaturesWhoSeeMe()) {
            if (other == this) {
                other.notify("You " + message + ".", params);
            } else {
                other.notify(String.format("The %s %s.", name, makeSecondPerson(message)), params);
            }
        }
    }

    public void doAction(Item item, String message, Object... params) {
        if (hp < 1)
            return;

        for (Creature other : getCreaturesWhoSeeMe()) {
            if (other == this) {
                other.notify("You " + message + ".", params);
            } else {
                other.notify(String.format("The %s %s.", name, makeSecondPerson(message)), params);
            }
            other.learnName(item);
        }
    }

    private List<Creature> getCreaturesWhoSeeMe() {
        List<Creature> others = new ArrayList<>();
        int r = 9;
        for (int ox = -r; ox < r + 1; ox++) {
            for (int oy = -r; oy < r + 1; oy++) {
                if (ox * ox + oy * oy > r * r)
                    continue;

                Creature other = world.creature(x + ox, y + oy, z);

                if (other == null)
                    continue;

                others.add(other);
            }
        }
        return others;
    }

    /**
     * Manipulate the message string to make it grammatically correct
     */
    private String makeSecondPerson(String text) {
        String[] words = text.split(" ");
        words[0] = words[0] + "s";

        StringBuilder builder = new StringBuilder();
        for (String word : words) {
            builder.append(" ");
            builder.append(word);
        }

        return builder.toString().trim();
    }

    public boolean canSee(int wx, int wy, int wz) {
        return (detectCreatures > 0 && world.creature(wx, wy, wz) != null
                || ai.canSee(wx, wy, wz));
    }

    public Tile realTile(int wx, int wy, int wz) {
        return world.tile(wx, wy, wz);
    }

    public Tile tile(int wx, int wy, int wz) {
        if (canSee(wx, wy, wz))
            return world.tile(wx, wy, wz);
        else
            return ai.rememberedTile(wx, wy, wz);
    }

    /**
     * Allows creatures to see what other creatures are doing
     *
     * @param wx X coordinate
     * @param wy Y coordinate
     * @param wz Z coordinate
     * @return
     */
    public Creature creature(int wx, int wy, int wz) {
        if (canSee(wx, wy, wz))
            return world.creature(wx, wy, wz);
        else
            return null;
    }

    /**
     * Allows creature to pick up an item
     */
    public void pickup() {
        Item item = world.item(x, y, z);

        if (inventory.isFull() || item == null) {
            doAction("grab at the ground");
        } else {
            doAction("pickup a %s", nameOf(item));
            world.remove(x, y, z);
            inventory.add(item);
        }
    }

    /**
     * Allows creature to drop an item
     *
     * @param item - Item to drop
     */
    public void drop(Item item) {
        if (world.addAtEmptySpace(item, x, y, z)) {
            doAction("drop a " + nameOf(item));
            inventory.remove(item);
            unequip(item);
        } else {
            notify("There's nowhere to drop the %s.", nameOf(item));
        }
    }

    /**
     * Only allows the player to die of hunger, not NPC's
     *
     * @param amount - Amount of food
     */
    private void modifyFood(int amount) {
        food += amount;
        if (food > maxFood) {
            maxFood = (maxFood + food) / 2;
            food = maxFood;
            notify("You can't believe your stomach can hold that much!");
            modifyHp(-1, "Killed by overeating.");
        } else if (food < 1 && isPlayer()) {
            modifyHp(-1000, "Starved to death.");
        }
    }

    private boolean isPlayer() {
        return glyph == '@';
    }

    /**
     * Allows creatures to eat
     *
     * @param item - Food item
     */
    public void eat(Item item) {
        doAction("eat a " + nameOf(item));
        consume(item);
    }

    /**
     * Quaff potion
     *
     * @param item - Potion
     */
    public void quaff(Item item) {
        doAction("quaff a " + nameOf(item));
        consume(item);
    }

    private void consume(Item item) {
        if (item.foodValue() < 0)
            notify("Yuck!"); // ToDo add a random message

        addEffect(item.quaffEffect());

        modifyFood(item.foodValue());
        getRidOf(item);
    }

    private void addEffect(Effect effect) {
        if (effect == null)
            return;

        effect.start(this);
        effects.add(effect);
    }

    /**
     * Get rid of item
     *
     * @param item - Item to get rid of
     */
    private void getRidOf(Item item) {
        inventory.remove(item);
        unequip(item);
    }

    private void putAt(Item item, int wx, int wy, int wz) {
        inventory.remove(item);
        unequip(item);
        world.addAtEmptySpace(item, wx, wy, wz);
    }

    /**
     * Unequip weapons and armour
     *
     * @param item - Item to unequip
     */
    private void unequip(Item item) {
        if (item == null)
            return;

        if (item == armour) {
            if (hp > 0)
                doAction("remove a " + nameOf(item));
            armour = null;
        } else if (item == weapon) {
            if (hp > 0)
                doAction("put away a " + nameOf(item));
            weapon = null;
        }
    }

    /**
     * Equip weapons and armour
     *
     * @param item - Item to equip
     */
    public void equip(Item item) {
        if (!inventory.contains(item)) {
            if (inventory.isFull()) {
                notify("Can't equip %s since you're holding too much stuff.", nameOf(item));
                return;
            } else {
                world.remove(item);
                inventory.add(item);
            }
        }

        if (item.attackValue() == 0 && item.rangedAttackValue() == 0 && item.defenseValue() == 0)
            return;

        if (item.attackValue() + item.rangedAttackValue() >= item.defenseValue()) {
            unequip(weapon);
            doAction("wield a " + nameOf(item));
            weapon = item;
        } else {
            unequip(armour);
            doAction("put on a " + nameOf(item));
            armour = item;
        }
    }

    public Item item(int wx, int wy, int wz) {
        if (canSee(wx, wy, wz))
            return world.item(wx, wy, wz);
        else
            return null;
    }

    public String details() {
        return String.format("  level:%d  attack:%d  defense:%d  hp:%d", level, attackValue(), defenseValue(), hp);
    }

    /**
     * Throws an item and deals damage
     *
     * @param item - Item to throw
     * @param wx   X Coordinate
     * @param wy   Y Coordinate
     * @param wz   Z Coordinate
     */
    public void throwItem(Item item, int wx, int wy, int wz) {
        Point end = new Point(x, y, 0);

        for (Point p : new Line(x, y, wx, wy)) {
            if (!realTile(p.x, p.y, z).isGround())
                break;
            end = p;
        }

        wx = end.x;
        wy = end.y;

        Creature c = creature(wx, wy, wz);

        if (c != null)
            throwAttack(item, c);
        else
            doAction("throw a %s", nameOf(item));

        if (item.quaffEffect() != null && c != null)
            getRidOf(item);
        else
            putAt(item, wx, wy, wz);
    }

    public void summon(Creature other) {
        world.add(other);
    }

    public void modifyDetectCreatures(int amount) {
        detectCreatures += amount;
    }

    public void castSpell(Spell spell, int x2, int y2) {
        Creature other = creature(x2, y2, z);

        if (spell.manaCost() > mana) {
            doAction("point and mumble but nothing happens");
            return;
        } else if (other == null) {
            doAction("point and mumble at nothing");
            return;
        }

        other.addEffect(spell.effect());
        modifyMana(-spell.manaCost());
    }

    public String nameOf(Item item) {
        return ai.getName(item);
    }

    private void learnName(Item item) {
        notify("The " + item.appearance() + " is a " + item.name() + "!");
        ai.setName(item, item.name());
    }
}
