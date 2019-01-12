package trollcavern;

import asciiPanel.AsciiPanel;
import trollcavern.creatures.Bat;
import trollcavern.creatures.Fungus;
import trollcavern.creatures.Goblin;
import trollcavern.creatures.Zombie;
import trollcavern.world.FieldOfView;
import trollcavern.world.Item;
import trollcavern.world.World;

import java.awt.*;
import java.util.List;
import java.util.*;

public class StuffFactory {
    private final World world;
    private Map<String, Color> potionColors;
    private List<String> potionAppearances;

    public StuffFactory(World world) {
        this.world = world;

        setUpPotionAppearances();
    }

    /**
     * Potion colours
     */
    private void setUpPotionAppearances() {
        potionColors = new HashMap<String, Color>();
        potionColors.put("red potion", AsciiPanel.brightRed);
        potionColors.put("yellow potion", AsciiPanel.brightYellow);
        potionColors.put("green potion", AsciiPanel.brightGreen);
        potionColors.put("cyan potion", AsciiPanel.brightCyan);
        potionColors.put("blue potion", AsciiPanel.brightBlue);
        potionColors.put("magenta potion", AsciiPanel.brightMagenta);
        potionColors.put("dark potion", AsciiPanel.brightBlack);
        potionColors.put("grey potion", AsciiPanel.white);
        potionColors.put("light potion", AsciiPanel.brightWhite);

        potionAppearances = new ArrayList<String>(potionColors.keySet());
        Collections.shuffle(potionAppearances);
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
        new PlayerAi(player, messages, fov);
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
     * Bat
     *
     * @param depth- what level of the cavern
     * @return - Creature bat
     */
    public Creature newBat(int depth) {
        Creature bat = new Creature(world, 'b', AsciiPanel.brightYellow, "bat", 15, 5, 0);
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
    public void newZombie(int depth, Creature player) {
        Creature zombie = new Creature(world, 'z', AsciiPanel.white, "zombie", 50, 10, 10);
        world.addAtEmptyLocation(zombie, depth);
        new Zombie(zombie, player);
    }

    /**
     * Goblin
     *
     * @param depth  - what level of the cavern
     * @param player - Player character to follow
     * @return - Creature goblin
     */
    public void newGoblin(int depth, Creature player) {
        Creature goblin = new Creature(world, 'g', AsciiPanel.brightGreen, "goblin", 66, 15, 5);
        new Goblin(goblin, player);
        goblin.equip(randomWeapon(depth));
        goblin.equip(randomArmour(depth));
        world.addAtEmptyLocation(goblin, depth);
    }

    /**
     * Rock
     *
     * @param depth - what level of the cavern
     * @return - Item rock
     */
    public void newRock(int depth) {
        Item rock = new Item(',', AsciiPanel.yellow, "rock", null);
        rock.modifyThrownAttackValue(5);
        world.addAtEmptyLocation(rock, depth);
    }

    /**
     * Quest McGuffin
     *
     * @param depth - Level of the cavern
     * @return - Item victory item
     */
    public void newVictoryItem(int depth) {
        Item item = new Item('*', AsciiPanel.brightWhite, "teddy bear", null);
        world.addAtEmptyLocation(item, depth);
    }

    /**
     * Ration
     *
     * @param depth - what level of the cavern
     * @return - Item ration
     */
    public void newRation(int depth) {
        Item item = new Item('%', AsciiPanel.brightGreen, "ration", null);
        item.modifyFoodValue(200);
        world.addAtEmptyLocation(item, depth);
    }


    /**
     * Dagger
     *
     * @param depth - what level of the cavern
     * @return - Item dagger
     */
    private Item newDagger(int depth) {
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
    private Item newSword(int depth) {
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
    private Item newStaff(int depth) {
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
    private Item newBow(int depth) {
        Item item = new Item(')', AsciiPanel.yellow, "bow", null);
        item.modifyAttackValue(1);
        item.modifyRangedAttackValue(5);
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    /**
     * Health potion
     * <p>
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
     *
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
     *
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
     * <p>
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
     * <p>
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
     * <p>
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
     * White Mages Spell Book
     * Healing spells
     *
     * @param depth
     * @return
     */
    private Item newWhiteMagesSpellbook(int depth) {
        Item item = new Item('+', AsciiPanel.brightWhite, "white mage's spellbook", null);
        item.addWrittenSpell("minor heal", 4, new Effect(1) {
            public void start(Creature creature) {
                if (creature.hp() == creature.maxHp())
                    return;

                creature.modifyHp(20, "Killed by a minor heal spell?");
                creature.doAction("look healthier");
            }
        });

        item.addWrittenSpell("major heal", 8, new Effect(1) {
            public void start(Creature creature) {
                if (creature.hp() == creature.maxHp())
                    return;

                creature.modifyHp(50, "Killed by a major heal spell?");
                creature.doAction("look healthier");
            }
        });

        item.addWrittenSpell("slow heal", 12, new Effect(50) {
            public void update(Creature creature) {
                super.update(creature);
                creature.modifyHp(2, "Killed by a slow heal spell?");
            }
        });

        item.addWrittenSpell("inner strength", 16, new Effect(50) {
            public void start(Creature creature) {
                creature.modifyAttackValue(2);
                creature.modifyDefenseValue(2);
                creature.modifyVisionRadius(1);
                creature.modifyRegenHpPer1000(10);
                creature.modifyRegenManaPer1000(-10);
                creature.doAction("seem to glow with inner strength");
            }

            public void update(Creature creature) {
                super.update(creature);
                if (Math.random() < 0.25)
                    creature.modifyHp(1, "Killed by inner strength spell?");
            }

            public void end(Creature creature) {
                creature.modifyAttackValue(-2);
                creature.modifyDefenseValue(-2);
                creature.modifyVisionRadius(-1);
                creature.modifyRegenHpPer1000(-10);
                creature.modifyRegenManaPer1000(10);
            }
        });

        world.addAtEmptyLocation(item, depth);
        return item;
    }

    /**
     * Blue Mages Spell Book
     * Various effects
     *
     * @param depth
     * @return
     */
    private Item newBlueMagesSpellbook(int depth) {
        Item item = new Item('+', AsciiPanel.brightBlue, "blue mage's spellbook", null);

        item.addWrittenSpell("blood to mana", 1, new Effect(1) {
            public void start(Creature creature) {
                int amount = Math.min(creature.hp() - 1, creature.maxMana() - creature.mana());
                creature.modifyHp(-amount, "Killed by a blood to mana spell.");
                creature.modifyMana(amount);
            }
        });

        item.addWrittenSpell("blink", 6, new Effect(1) {
            public void start(Creature creature) {
                creature.doAction("fade out");

                int mx = 0;
                int my = 0;

                do {
                    mx = (int) (Math.random() * 11) - 5;
                    my = (int) (Math.random() * 11) - 5;
                }
                while (creature.canEnter(creature.x + mx, creature.y + my, creature.z)
                        && creature.canSee(creature.x + mx, creature.y + my, creature.z));

                creature.moveBy(mx, my, 0);

                creature.doAction("fade in");
            }
        });

        item.addWrittenSpell("summon bats", 11, new Effect(1) {
            public void start(Creature creature) {
                for (int ox = -1; ox < 2; ox++) {
                    for (int oy = -1; oy < 2; oy++) {
                        int nx = creature.x + ox;
                        int ny = creature.y + oy;
                        if (ox == 0 && oy == 0
                                || creature.creature(nx, ny, creature.z) != null)
                            continue;

                        Creature bat = newBat(0);

                        if (bat.canEnter(nx, ny, creature.z)) {
                            world.remove(bat);
                            continue;
                        }

                        bat.x = nx;
                        bat.y = ny;
                        bat.z = creature.z;

                        creature.summon(bat);
                    }
                }
            }
        });

        item.addWrittenSpell("detect creatures", 16, new Effect(75) {
            public void start(Creature creature) {
                creature.doAction("look far off into the distance");
                creature.modifyDetectCreatures(1);
            }

            public void end(Creature creature) {
                creature.modifyDetectCreatures(-1);
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

    /**
     * Randomises which spellbook is found
     *
     * @param depth - Level of cavern
     * @return - Spellbook found
     */
    public void randomSpellBook(int depth) {
        switch ((int) (Math.random() * 2)) {
            case 0:
                newWhiteMagesSpellbook(depth);
                return;
            default:
                newBlueMagesSpellbook(depth);
        }
    }


    /**
     * Light Armour
     *
     * @param depth - what level of the cavern
     * @return - Item light armour
     */
    private Item newLightArmour(int depth) {
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
    private Item newMediumArmour(int depth) {
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
    private Item newHeavyArmour(int depth) {
        Item item = new Item('[', AsciiPanel.brightWhite, "platemail", null);
        item.modifyDefenseValue(6);
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    /**
     * Random weapon
     *
     * @param depth - what level of the cavern
     * @return - Randomly selected weapon
     */
    public Item randomWeapon(int depth) {
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

    /**
     * Random Armour
     *
     * @param depth - what level of the cavern
     * @return - Randomly selected armour
     */
    public Item randomArmour(int depth) {
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
