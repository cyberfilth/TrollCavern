package trollcavern.generators;

import asciiPanel.AsciiPanel;
import trollcavern.Creature;
import trollcavern.creatures.*;
import trollcavern.world.FieldOfView;
import trollcavern.world.World;

import java.util.List;

import static trollcavern.generators.Armoury.randomArmour;
import static trollcavern.generators.Armoury.randomWeapon;

public class CreatureGenerator {
    private static World world;


    public CreatureGenerator(World world) {
        this.world = world;
    }

    /**
     * Player creation
     *
     * @param messages - Status messages
     * @param fov      - Field of view
     * @return - Creature player
     */
    public Creature newPlayer(List<String> messages, FieldOfView fov) {
        Creature player = new Creature(world, '@', AsciiPanel.brightWhite, "player", 100, 20, 5);
        world.addAtEmptyLocation(player, 0);
        new Player(player, messages, fov);
        return player;
    }

    /**
     * Fungus
     *
     * @param depth- what level of the cavern
     * @return - Creature fungus
     */
    public Creature newFungus(int depth) {
        Creature fungus = new Creature(world, 'f', AsciiPanel.green, "fungus", 10, 0, 0);
        world.addAtEmptyLocation(fungus, depth);
        new Fungus(fungus, this);
        return fungus;
    }

    /**
     * Rat
     *
     * @param depth- what level of the cavern
     * @return - Creature rat
     */
    public Creature newRat(int depth) {
        Creature rat = new Creature(world, 'r', AsciiPanel.yellow, "rat", 12, 7, 0);
        world.addAtEmptyLocation(rat, depth);
        new Rat(rat);
        return rat;
    }

    /**
     * Bat
     *
     * @param depth- what level of the cavern
     * @return - Creature bat
     */
    public static Creature newBat(int depth) {
        Creature bat = new Creature(world, 'b', AsciiPanel.brightYellow, "bat", 15, 5, 2);
        world.addAtEmptyLocation(bat, depth);
        new Bat(bat);
        return bat;
    }

    /**
     * Zombie
     *
     * @param depth  - what level of the cavern
     * @param player - Player character to follow
     * @return - Creature zombie
     */
    public Creature newZombie(int depth, Creature player) {
        Creature zombie = new Creature(world, 'z', AsciiPanel.white, "zombie", 50, 10, 10);
        world.addAtEmptyLocation(zombie, depth);
        new Zombie(zombie, player);
        return zombie;
    }

    /**
     * Goblin
     *
     * @param depth  - what level of the cavern
     * @param player - Player character to follow
     * @return - Creature goblin
     */
    public Creature newGoblin(int depth, Creature player) {
        Creature goblin = new Creature(world, 'g', AsciiPanel.brightGreen, "goblin", 66, 15, 5);
        new Goblin(goblin, player);
        goblin.equip(randomWeapon(depth));
        goblin.equip(randomArmour(depth));
        world.addAtEmptyLocation(goblin, depth);
        return goblin;
    }
}
