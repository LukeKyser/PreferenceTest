package database;

import java.awt.image.BufferedImage;

/**
 * Item
 * <p>
 * Item Class for test items in comparison test
 *
 * @author Leron Tolmachev, Christopher Manuel
 * @version 2017.12.04
 * <p>
 * Change Log:
 * - Refactored Project after Sprint One
 * - Consolidated Setup, Test, and Results Item Class into one
 * -    Luke Kyser: Added BufferedImage field and getters/setters.
 * -    Constructor takes a BufferedImage object which is null by default.
 */
public class Item implements Comparable<Item>{
    private final int id;
    private final String name;
    private BufferedImage image;
    private int score;
    private int wins;
    private int losses;
    private int ties;

    /**
     * Constructor for Item class
     *
     * @param itemID   unique identifier for each Item
     * @param itemName name of Item to be displayed to user
     * @param testID   Test with which Item is associated
     */
    public Item(int itemID, int testID, String itemName, BufferedImage image) {
        this.id = itemID;
        this.name = itemName;
        this.image = image;
        this.score = 0;
        this.wins = 0;
        this.losses = 0;
        this.ties = 0;
    }

    /**
     * Access Item's id field
     *
     * @return int Item ID
     */
    public int getID() {
        return id;
    }

    /**
     * Access Item's losses field
     *
     * @return int losses
     */
    public int getLosses() {
        return losses;
    }

    /**
     * Mutate Item's losses field
     *
     * @param losses sum of how many times preference was given to another item
     */
    public void setLosses(int losses) {
        this.losses = losses;
    }

    /**
     * Access Item's name field
     *
     * @return int Item name
     */
    public String getName() {
        return name;
    }

    /**
     * Access Item's score field
     *
     * @return int score
     */
    public int getScore() {
        return score;
    }

    /**
     * Mutate Item's score field
     *
     * @param score sum of Item's wins, ties, and losses
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Access Item's ties field
     *
     * @return int ties
     */
    public int getTies() {
        return ties;
    }

    /**
     * Mutate Item's ties field
     *
     * @param ties sum of how many times no preference was given
     */
    public void setTies(int ties) {
        this.ties = ties;
    }

    /**
     * Access Item's wins field
     *
     * @return int wins
     */
    public int getWins() {
        return wins;
    }

    /**
     * Mutate Item's wins field
     *
     * @param wins sum of times preference was given to Item
     */
    public void setWins(int wins) {
        this.wins = wins;
    }

    /**
     * Access Item's image field
     *
     * @return BufferedImage image
     */
    public BufferedImage getImage() {
        return image;
    }

    /**
     * Mutate Item's image field
     *
     * @param image Image to be displayed along with Item
     */
    public void setImage(BufferedImage image) { this.image = image; }

    // Allows item array to be sorted by score
    @Override
    public int compareTo(Item compareItem) {

        int compareScore = compareItem.getScore();

        return compareScore - this.getScore();
    }

    //Formatting to appropriately display Items in JLists and JComboBoxes.
    @Override
    public String toString() {
        return getName() == null ? "Select an Item..." : getName();
    }
}
