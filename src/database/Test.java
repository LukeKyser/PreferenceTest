package database;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class Test.java.
 *
 * @author Luke Kyser
 * @version 2017.11.21
 *
 * Change Log:
 * - Class created.
 * -
 */

public class Test {
    private int testID;
    private final String testName;
    private ArrayList<Item> items;
    private final HashMap<String, String> settings = new HashMap<>();

    /**
     * Constructor for Test Class
     *
     * @param testID set Test's testID field
     * @param testName set Test's testName field
     */
    public Test(int testID, String testName, String settings) {
        this.testID = testID;
        this.testName = testName;
        this.items = new ArrayList<Item>();
        setSettings(settings);
    }

    /**
     * Access Test's testID field
     *
     * @return int testID
     */
    public int getID(){
        return testID;
    }

    /**
     * Mutate Test's testID field
     *
     * @param testID desired testID
     */
    public void setID(int testID) {
        this.testID = testID;
    }

    /**
     * Access Test's testName field
     *
     * @return String testName
     */
    public String getName(){
        return testName;
    }

    /**
     * Access Test's items field
     *
     * @return ArrayList items
     */
    public ArrayList<Item> getItems() {
        return items;
    }

    /**
     * Mutate Test's items field
     *
     * @param items ArrayList of items
     */
    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }

    /**
     * Access Test's testOrder field
     *
     * @return String testOrder
     */
    public String getSettings(String key) {
        return settings.get(key);
    }

    /**
     * Mutate Test's settings field
     *
     * @param settings formatted String to be split into HashMap entries
     */
    public void setSettings(String settings) {
        if(settings == null) {
            this.settings.put("QuestionText", "Which do you prefer?");
            this.settings.put("TieText", "I can't decide.");
            this.settings.put("SortMethod", "Random");
            this.settings.put("ItemOrder", "Random");
            this.settings.put("CustomMatchUps", "none");
        } else {
            String[] splitSettings = settings.split("\\\\");
            this.settings.put("QuestionText", splitSettings[0]);
            this.settings.put("TieText", splitSettings[1]);
            this.settings.put("SortMethod", splitSettings[2]);
            this.settings.put("ItemOrder", splitSettings[3]);
            this.settings.put("CustomMatchUps", splitSettings[4]);
            this.settings.put("all", settings);
        }
    }

    /**
     * Add an item to Test's items list
     *
     * @param item Item to be added to items list
     */
    public void addItem(Item item) {
        items.add(item);
    }

    /**
     * Remove an item from Test's items list
     *
     * @param index position of Item in items list
     */
    public void removeItem(int index){
        items.remove(index);
    }

    //Formatting to appropriately display Tests in JLists and JComboBoxes.
    @Override
    public String toString() {
        return getName() == null ? "Select a Test..." : getName();
    }
}