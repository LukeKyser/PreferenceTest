package database;

/**
 * MatchUp
 *
 * A class to represent a record within the Database's MATCHUP table
 *
 * @author Leron Tolmachev
 * @version 2017.11.11
 *
 * Change Log:
 * - Refactored Project after Sprint One
 * -
 */
public class MatchUp {

    private int questionNumber;
    private Item itemA;
    private Item itemB;
    private String decision;

    /**
     * Constructor for MatchUp Class
     *
     * @param questionNumber numerical position MatchUp will appear in TestSession
     * @param itemA Item representing the first (left) option in a MatchUp
     * @param itemB Item representing the second (right) option in a MatchUp
     */
    public MatchUp(int questionNumber, Item itemA, Item itemB, String decision) {
        this.questionNumber = questionNumber;
        this.itemA = itemA;
        this.itemB = itemB;
        this.decision = decision;
    }

    /**
     * Access MatchUp's decision field
     *
     * @return String decision
     */
    public String getDecision() {
        return decision;
    }

    /**
     * Mutate MatchUp's decision field
     *
     * @param selection String of "A", "B", or "" indicating user's selection
     */
    public void setDecision(String selection) {
        System.out.println("Answer #" + getQuestionNumber() + ": " + selection);
        decision = selection;
    }

    /**
     * Access MatchUp's first (left) Item field
     *
     * @return Item itemA
     */
    public Item getItemA() {
        return itemA;
    }

    public void setItemA(Item itemA) {
        this.itemA = itemA;
    }

    /**
     * Access MatchUp's second (right) Item field
     *
     * @return Item itemB
     */
    public Item getItemB() {
        return itemB;
    }

    public void setItemB(Item itemB) {
        this.itemB = itemB;
    }

    /**
     * Access MatchUp's question number field
     *
     * @return int questionNumber
     */
    public int getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(int questionNumber) {
        this.questionNumber = questionNumber;
    }

    //Formatting to appropriately display Users JLists and JComboBoxes.
    @Override
    public String toString() {
        String matchUpListItem ;
        int gapSize = 23 - getItemA().getName().length() ;
        String gap = new String(new char[gapSize]).replace("\0", " ");
        matchUpListItem = ((getQuestionNumber() < 10 ? "# " : "#") + getQuestionNumber() + ":"+ gap + itemA.getName() + " | " + itemB.getName());
        return matchUpListItem;
    }

}
