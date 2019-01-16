package trollcavern;

import asciiPanel.AsciiPanel;
import trollcavern.world.Item;
import trollcavern.world.World;

public class Armoury {

    private static World world;

    public Armoury(World world) {
        Armoury.world = world;
    }

    /*   Weapons   */

    /**
     * Dagger
     *
     * @param depth - what level of the cavern
     * @return - Item dagger
     */
    private static Item newDagger(int depth) {
        Item item = new Item(')', AsciiPanel.white, "dagger", null);
        item.modifyAttackValue(5);
        item.modifyThrownAttackValue(5);
        world.addAtEmptyLocation(item, depth);
        return item;
    }


    /**
     * Sword
     *
     * @param depth - what level of the cavern
     * @return - Item sword
     */
    private static Item newSword(int depth) {
        Item item = new Item(')', AsciiPanel.brightWhite, "sword", null);
        item.modifyAttackValue(10);
        item.modifyThrownAttackValue(3);
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    /**
     * Staff
     *
     * @param depth - what level of the cavern
     * @return - Item staff
     */
    private static Item newStaff(int depth) {
        Item item = new Item(')', AsciiPanel.yellow, "staff", null);
        item.modifyAttackValue(5);
        item.modifyDefenseValue(3);
        item.modifyThrownAttackValue(3);
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    /**
     * Bow
     *
     * @param depth - what level of the dungeon
     * @return - Item bow
     */
    private static Item newBow(int depth) {
        Item item = new Item(')', AsciiPanel.yellow, "bow", null);
        item.modifyAttackValue(1);
        item.modifyRangedAttackValue(5);
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    /**
     * Random weapon
     *
     * @param depth - what level of the cavern
     * @return - Randomly selected weapon
     */
    public static Item randomWeapon(int depth) {
        switch ((int) (Math.random() * 3)) {
            case 0:
                return newDagger(depth);
            case 1:
                return newSword(depth);
            case 2:
                return newBow(depth);
            default:
                return newStaff(depth);
        }
    }


    /*      Armour    */


    /**
     * Light Armour
     *
     * @param depth - what level of the cavern
     * @return - Item light armour
     */
    private static Item newLightArmour(int depth) {
        Item item = new Item('[', AsciiPanel.green, "tunic", null);
        item.modifyDefenseValue(2);
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    /**
     * Medium Armour
     *
     * @param depth - what level of the cavern
     * @return - Item medium armour
     */
    private static Item newMediumArmour(int depth) {
        Item item = new Item('[', AsciiPanel.white, "chainmail", null);
        item.modifyDefenseValue(4);
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    /**
     * Heavy Armour
     *
     * @param depth - what level of the cavern
     * @return - Item heavy armour
     */
    private static Item newHeavyArmour(int depth) {
        Item item = new Item('[', AsciiPanel.brightWhite, "platemail", null);
        item.modifyDefenseValue(6);
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    /**
     * Random Armour
     *
     * @param depth - what level of the cavern
     * @return - Randomly selected armour
     */
    public static Item randomArmour(int depth) {
        switch ((int) (Math.random() * 3)) {
            case 0:
                return newLightArmour(depth);
            case 1:
                return newMediumArmour(depth);
            default:
                return newHeavyArmour(depth);
        }
    }

}
