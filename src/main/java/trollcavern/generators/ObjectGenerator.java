package trollcavern.generators;

import asciiPanel.AsciiPanel;
import trollcavern.world.Item;
import trollcavern.world.World;

public class ObjectGenerator {
    private static World world;


    public ObjectGenerator(World world) {
        this.world = world;
    }


    /**
     * Rock
     *
     * @param depth - what level of the cavern
     * @return - Item rock
     */
    public Item newRock(int depth) {
        Item rock = new Item(',', AsciiPanel.yellow, "rock", null);
        rock.modifyThrownAttackValue(5);
        world.addAtEmptyLocation(rock, depth);
        return rock;
    }

    /**
     * Quest McGuffin
     *
     * @param depth - Level of the cavern
     * @return - Item victory item
     */
    public Item newVictoryItem(int depth) {
        Item item = new Item('*', AsciiPanel.brightWhite, "teddy bear", null);
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    /**
     * Ration
     *
     * @param depth - what level of the cavern
     * @return - Item ration
     */
    public Item newRation(int depth) {
        Item item = new Item('%', AsciiPanel.brightGreen, "ration", null);
        item.modifyFoodValue(200);
        world.addAtEmptyLocation(item, depth);
        return item;
    }
}
