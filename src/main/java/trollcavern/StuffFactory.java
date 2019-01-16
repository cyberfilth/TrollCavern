package trollcavern;

import asciiPanel.AsciiPanel;
import trollcavern.creatures.*;
import trollcavern.screens.PlayScreen;
import trollcavern.world.FieldOfView;
import trollcavern.world.Item;
import trollcavern.world.World;

import java.awt.*;
import java.util.List;
import java.util.*;

import static trollcavern.Armoury.*;
import static trollcavern.Armoury.randomArmour;

public class StuffFactory {
    private World world;


    public StuffFactory(World world) {
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
    public Creature newBat(int depth) {
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

    /**
     * White Mages Spell Book
     * Healing spells
     *
     * @param depth
     * @return
     */
    public Item newWhiteMagesSpellbook(int depth) {
        Item item = new Item('+', AsciiPanel.brightWhite, "white mage's spellbook", null);
        item.addWrittenSpell("minor heal", 4, new Effect(1) {
            public void start(Creature creature) {
                if (creature.hp() == creature.maxHp())
                    return;
                else {
                    creature.modifyHp(20, "Killed by a minor heal spell?");
                    creature.doAction("look healthier");
                }
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
    private void newBlueMagesSpellbook(int depth) {
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

                int mx;
                int my;

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

}
