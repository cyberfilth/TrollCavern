package trollcavern;

import trollcavern.world.Item;

public class Inventory {
    private final Item[] items;

    public Inventory(int max) {
        items = new Item[max];
    }

    public Item[] getItems() {
        return items;
    }

    public Item get(int i) {
        return items[i];
    }

    /**
     * Add an item to the first open slot in the inventory
     *
     * @param item - Item to add
     */
    public void add(Item item) {
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null) {
                items[i] = item;
                break;
            }
        }
    }

    /**
     * Remove an item from the inventory
     *
     * @param item - Item to remove
     */
    public void remove(Item item) {
        for (int i = 0; i < items.length; i++) {
            if (items[i] == item) {
                items[i] = null;
                return;
            }
        }
    }

    /**
     * Check if the inventory is full
     *
     * @return - full or not
     */
    public boolean isFull() {
        int size = 0;
        for (Item item : items) {
            if (item != null)
                size++;
        }
        return size == items.length;
    }

    public boolean contains(Item item) {
        for (Item i : items) {
            if (i == item)
                return true;
        }
        return false;
    }

}
