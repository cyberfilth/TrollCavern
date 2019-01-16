package trollcavern;

import asciiPanel.AsciiPanel;
import trollcavern.world.Item;
import trollcavern.world.World;

import java.awt.*;
import java.util.List;
import java.util.*;

public class PotionGenerator {
    private World world;
    private Map<String, Color> potionColors;
    private List<String> potionAppearances;

    public PotionGenerator(World world) {
        this.world = world;
        setUpPotionAppearances();

        setUpPotionAppearances();
    }

    /**
     * Potion colours
     */
    private void setUpPotionAppearances() {
        potionColors = new HashMap<>();
        potionColors.put("red potion", AsciiPanel.brightRed);
        potionColors.put("yellow potion", AsciiPanel.brightYellow);
        potionColors.put("green potion", AsciiPanel.brightGreen);
        potionColors.put("cyan potion", AsciiPanel.brightCyan);
        potionColors.put("blue potion", AsciiPanel.brightBlue);
        potionColors.put("magenta potion", AsciiPanel.brightMagenta);
        potionColors.put("dark potion", AsciiPanel.brightBlack);
        potionColors.put("grey potion", AsciiPanel.white);
        potionColors.put("light potion", AsciiPanel.brightWhite);

        potionAppearances = new ArrayList<>(potionColors.keySet());
        Collections.shuffle(potionAppearances);
    }


    /**
     * Health potion
     * One time effect
     *
     * @param depth - Level of cavern
     * @return - Item
     */
    private Item newPotionOfHealth(int depth) {
        String appearance = potionAppearances.get(0);
        final Item item = new Item('!', potionColors.get(appearance), "health potion", appearance);
        item.setQuaffEffect(new Effect(1) {
            public void start(Creature creature) {
                if (creature.hp() == creature.maxHp())
                    return;

                creature.modifyHp(15, "Killed by a health potion?");
                creature.doAction(item, "look healthier");
            }
        });

        world.addAtEmptyLocation(item, depth);
        return item;
    }

    /**
     * Potion of Mana
     * @param depth
     * @return
     */
    private Item newPotionOfMana(int depth) {
        String appearance = potionAppearances.get(1);
        final Item item = new Item('!', potionColors.get(appearance), "mana potion", appearance);
        item.setQuaffEffect(new Effect(1) {
            public void start(Creature creature) {
                if (creature.mana() == creature.maxMana())
                    return;

                creature.modifyMana(10);
                creature.doAction(item, "look restored");
            }
        });
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    /**
     * Potion of slow health
     * @param depth
     * @return
     */
    private Item newPotionOfSlowHealth(int depth) {
        String appearance = potionAppearances.get(2);
        final Item item = new Item('!', potionColors.get(appearance), "slow health potion", appearance);
        item.setQuaffEffect(new Effect(100) {
            public void start(Creature creature) {
                creature.doAction(item, "look a little better");
            }

            public void update(Creature creature) {
                super.update(creature);
                creature.modifyHp(1, "Killed by a slow health potion?");
            }
        });

        world.addAtEmptyLocation(item, depth);
        return item;
    }


    /**
     * Poison potion
     * Affects creature each turn
     *
     * @param depth - Level of cavern
     * @return - Item
     */
    private Item newPotionOfPoison(int depth) {
        String appearance = potionAppearances.get(3);
        final Item item = new Item('!', potionColors.get(appearance), "poison potion", appearance);
        item.setQuaffEffect(new Effect(20) {
            public void start(Creature creature) {
                creature.doAction(item, "look sick");
            }

            public void update(Creature creature) {
                super.update(creature);
                creature.modifyHp(-1, "Died of poison.");
            }
        });

        world.addAtEmptyLocation(item, depth);
        return item;
    }

    /**
     * Potion of archer
     * improves vision radius
     *
     * @param depth
     * @return
     */
    private Item newPotionOfArcher(int depth) {
        String appearance = potionAppearances.get(5);
        Item item = new Item('!', potionColors.get(appearance), "archers potion", appearance);
        item.setQuaffEffect(new Effect(20) {
            public void start(Creature creature) {
                creature.modifyVisionRadius(3);
                creature.doAction(item, "look more alert");
            }

            public void end(Creature creature) {
                creature.modifyVisionRadius(-3);
                creature.doAction("look less alert");
            }
        });

        world.addAtEmptyLocation(item, depth);
        return item;
    }

    /**
     * Potion of Experience
     *
     * @param depth
     * @return
     */
    private Item newPotionOfExperience(int depth) {
        String appearance = potionAppearances.get(6);
        Item item = new Item('!', potionColors.get(appearance), "experience potion", appearance);
        item.setQuaffEffect(new Effect(20) {
            public void start(Creature creature) {
                creature.doAction(item, "look more experienced");
                creature.modifyXp(creature.level() * 5);
            }
        });

        world.addAtEmptyLocation(item, depth);
        return item;
    }


    /**
     * Warrior potion
     * Affects creature at the start and restores it at the end
     */
    private Item newPotionOfWarrior(int depth) {
        String appearance = potionAppearances.get(4);
        Item item = new Item('!', potionColors.get(appearance), "warrior's potion", appearance);
        item.setQuaffEffect(new Effect(20) {
            public void start(Creature creature) {
                creature.modifyAttackValue(5);
                creature.modifyDefenseValue(5);
                creature.doAction(item, "look stronger");
            }

            public void end(Creature creature) {
                creature.modifyAttackValue(-5);
                creature.modifyDefenseValue(-5);
                creature.doAction("look less strong");
            }
        });

        world.addAtEmptyLocation(item, depth);
        return item;
    }


    /**
     * Randomises which potion is found
     *
     * @param depth - Level of cavern
     * @return - Potion found
     */
    public void randomPotion(int depth) {
        switch ((int) (Math.random() * 9)) {
            case 0:
                newPotionOfHealth(depth);
                return;
            case 1:
                newPotionOfHealth(depth);
                return;
            case 2:
                newPotionOfMana(depth);
                return;
            case 3:
                newPotionOfMana(depth);
                return;
            case 4:
                newPotionOfSlowHealth(depth);
                return;
            case 5:
                newPotionOfPoison(depth);
                return;
            case 6:
                newPotionOfWarrior(depth);
                return;
            case 7:
                newPotionOfArcher(depth);
                return;
            default:
                newPotionOfExperience(depth);
        }
    }
}
