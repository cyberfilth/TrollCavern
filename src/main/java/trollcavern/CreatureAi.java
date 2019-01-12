package trollcavern;

import trollcavern.world.Item;
import trollcavern.world.Line;
import trollcavern.world.Path;
import trollcavern.world.Point;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreatureAi {
    protected final Creature creature;
    private final Map<String, String> itemNames;

    protected CreatureAi(Creature creature) {
        this.creature = creature;
        this.creature.setCreatureAi(this);
        this.itemNames = new HashMap<>();
    }

    public boolean canSee(int wx, int wy, int wz) {
        if (creature.z != wz)
            return false;

        if ((creature.x - wx) * (creature.x - wx) + (creature.y - wy) * (creature.y - wy) > creature.visionRadius() * creature.visionRadius())
            return false;

        for (Point p : new Line(creature.x, creature.y, wx, wy)) {
            if (creature.realTile(p.x, p.y, wz).isGround() || p.x == wx && p.y == wy)
                continue;

            return false;
        }

        return true;
    }

    protected boolean canRangedWeaponAttack(Creature other) {
        return creature.weapon() != null
                && creature.weapon().rangedAttackValue() > 0
                && creature.canSee(other.x, other.y, other.z);
    }

    protected boolean canThrowAt(Creature other) {
        return creature.canSee(other.x, other.y, other.z)
                && getWeaponToThrow() != null;
    }

    protected Item getWeaponToThrow() {
        Item toThrow = null;

        for (Item item : creature.inventory().getItems()) {
            if (item == null || creature.weapon() == item || creature.armour() == item)
                continue;

            if (toThrow == null || item.thrownAttackValue() > toThrow.attackValue())
                toThrow = item;
        }

        return toThrow;
    }

    protected boolean canPickup() {
        return creature.item(creature.x, creature.y, creature.z) != null
                && !creature.inventory().isFull();
    }

    protected void useBetterEquipment() {
        int currentWeaponRating = creature.weapon() == null ? 0 : creature.weapon().attackValue() + creature.weapon().rangedAttackValue();
        int currentArmorRating = creature.armour() == null ? 0 : creature.armour().defenseValue();

        for (Item item : creature.inventory().getItems()) {
            if (item == null)
                continue;

            boolean isArmor = item.attackValue() + item.rangedAttackValue() < item.defenseValue();

            if (item.attackValue() + item.rangedAttackValue() > currentWeaponRating
                    || isArmor && item.defenseValue() > currentArmorRating) {
                creature.equip(item);
            }
        }
    }


    protected boolean canUseBetterEquipment() {
        int currentWeaponRating = creature.weapon() == null ? 0 : creature.weapon().attackValue() + creature.weapon().rangedAttackValue();
        int currentArmourRating = creature.armour() == null ? 0 : creature.armour().defenseValue();

        for (Item item : creature.inventory().getItems()) {
            if (item == null)
                continue;

            boolean isArmour = item.attackValue() + item.rangedAttackValue() < item.defenseValue();

            if (item.attackValue() + item.rangedAttackValue() > currentWeaponRating
                    || isArmour && item.defenseValue() > currentArmourRating)
                return true;
        }

        return false;
    }

    public Tile rememberedTile(int wx, int wy, int wz) {
        return Tile.UNKNOWN;
    }

    /**
     * The hunt method finds a path to the target and moves to it
     *
     * @param target - Player
     */
    protected void hunt(Creature target) {
        List<Point> points = new Path(creature, target.x, target.y).points();

        int mx = points.get(0).x - creature.x;
        int my = points.get(0).y - creature.y;

        creature.moveBy(mx, my, 0);
    }

    /**
     * Checks whether a creature can enter a space or bumps into a wall
     *
     * @param x    X Coordinate
     * @param y    Y Coordinate
     * @param z    Z Coordinate
     * @param tile - Space to move into
     */
    public void onEnter(int x, int y, int z, Tile tile) {
        if (tile.isGround()) {
            creature.x = x;
            creature.y = y;
            creature.z = z;
        } else {
            creature.doAction("bump into a wall");
        }
    }

    /**
     * Creature can move randomly
     */
    protected void wander() {
        int mx = (int) (Math.random() * 3) - 1;
        int my = (int) (Math.random() * 3) - 1;

        Creature other = creature.creature(creature.x + mx, creature.y + my, creature.z);

        if (other != null && other.name().equals(creature.name())
                || !creature.tile(creature.x + mx, creature.y + my, creature.z).isGround())
            return;
        else
            creature.moveBy(mx, my, 0);
    }

    /**
     * Apply a stat benefit when a creature gains a level
     */
    public void onGainLevel() {
        new LevelUpController().autoLevelUp(creature);
    }

    public void onUpdate() {
    }

    public void onNotify(String message) {
    }

    public String getName(Item item) {
        String name = itemNames.get(item.name());

        return name == null ? item.appearance() : name;
    }

    public void setName(Item item, String name) {
        itemNames.put(item.name(), name);
    }
}
