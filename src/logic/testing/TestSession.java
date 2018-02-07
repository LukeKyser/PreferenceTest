package logic.testing;

import database.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ListIterator;
import java.util.Random;

/**
 * TestSession
 * <p>
 * Generate unique Test Session for logged in user based upon test items
 *
 * @author Leron Tolmachev
 * @version 2017.12.08
 * <p>
 * Change Log:
 * - Refactored Project after Sprint One
 * -
 */
public class TestSession {

    private final int userID;
    private final ArrayList<Test> tests;
    private Test test;
    private ArrayList<Item> items;
    private ArrayList<MatchUp> matchUpList;
    private ListIterator<MatchUp> matchUps;
    private MatchUp matchUp;

    /**
     * Constructor for TestSession Class
     *
     * @param userID int indicates which User is logged in
     */
    public TestSession(int userID) {
        this.userID = userID;
        Database db = new Database();
        this.tests = db.getTests();
        db.closeConnection();
    }

    /**
     * Get an ArrayList of tests
     *
     * @return ArrayList<Test> tests
     */
    public ArrayList<Test> getTests() {
        return tests;
    }

    /**
     * Get a test
     *
     * @return Test test
     */
    public Test getTest() {
        return test;
    }

    /**
     * Set a test
     *
     * @param test Test
     */
    private void setTest(Test test) {
        this.test = test;
    }

    /**
     * Set test items
     *
     * @param testID int
     */
    private void setItems(int testID) {
        Database db = new Database();
        this.items = db.getItemsByTestID(testID);
        db.closeConnection();
    }

    /**
     * Get a MatchUp
     *
     * @return matchUp MatchUp
     */
    public MatchUp getMatchUp() {
        return matchUp;
    }

    /**
     * Get an ArrayList of MatchUps
     *
     * @return ArrayList<MatchUp> matchUpList
     */
    public ArrayList<MatchUp> getMatchUpList() {
        return matchUpList;
    }


    /**
     * Create the order for which the items will be displayed
     */
    // "QUESTION? / TIE / SORT / RandomItems / CUSTOM
    private void createMatchUps() {
        int questionNumber = 0;
        String itemOrder = test.getSettings("ItemOrder");
        Item itemX = null;
        Item itemY = null;
        switch(test.getSettings("SortMethod")) {
            case "Sequential":
                for(int i = 0; i < items.size(); i++) {
                    itemX = items.get(i);
                    for(int ii = i + 1; ii < items.size(); ii++) {
                        itemY = items.get(ii);
                        questionNumber++;
                        addMatchUp(questionNumber, itemOrder, itemX, itemY);
                    }
                }
                break;
            case "Rotational":
                for(int i = 1; i < items.size(); i++) {
                    for(int ii = 0; ii < items.size() - i; ii ++) {
                        itemX = items.get(ii);
                        itemY = items.get(ii + i);
                        questionNumber++;
                        addMatchUp(questionNumber, itemOrder, itemX, itemY);
                    }
                }
                break;
            case "Random":
                ArrayList<MatchUp> randomMatchUps = new ArrayList<>();

                for(int i = 0; i < items.size(); i++) {
                    itemX = items.get(i);
                    for(int ii = i + 1; ii < items.size(); ii++) {
                        itemY = items.get(ii);
                        randomMatchUps.add(new MatchUp(-1, itemX, itemY, null));
                    }
                }
                Collections.shuffle(randomMatchUps);
                for(MatchUp matchUp : randomMatchUps) {
                    questionNumber++;
                    addMatchUp(questionNumber, itemOrder, matchUp.getItemA(), matchUp.getItemB());
                }
                break;
            case "Random (Unbiased)":
                randomMatchUps = new ArrayList<>();
                MatchUp lastMatchUp = null;

                for(int i = 0; i < items.size(); i++) {
                    itemX = items.get(i);
                    for(int ii = i + 1; ii < items.size(); ii++) {
                        itemY = items.get(ii);
                        randomMatchUps.add(new MatchUp(-1, itemX, itemY, null));
                    }
                }
                int testSize = randomMatchUps.size();
                for(int i = 0; i < testSize; i++) {
                    Collections.shuffle(randomMatchUps);
                    if(lastMatchUp == null || randomMatchUps.size() == 1) {
                        lastMatchUp = randomMatchUps.get(0);
                    } else {
                        Item itemA = lastMatchUp.getItemA();
                        Item itemB = lastMatchUp.getItemB();
                        for(MatchUp matchUp : randomMatchUps) {
                            if(itemA != matchUp.getItemA() && itemB != matchUp.getItemA() &&
                                    itemA != matchUp.getItemB() && itemB != matchUp.getItemB()) {
                                lastMatchUp = matchUp;
                            }
                        }
                    }
                    randomMatchUps.remove(lastMatchUp);
                    addMatchUp(i + 1, itemOrder, lastMatchUp.getItemA(), lastMatchUp.getItemB());
                }
                break;
            case "Custom":
                String[] itemPairs = test.getSettings("CustomMatchUps").split(",");
                String[] pairedItem;
                for(int i = 0; i < itemPairs.length; i++) {
                    pairedItem = itemPairs[i].split("\\|");
                    for(Item item : items) {
                        if(item.getName().equals(pairedItem[0])){
                            itemX = item;
                        } else if(item.getName().equals(pairedItem[1])) {
                            itemY = item;
                        }
                    }
                    addMatchUp(i + 1, itemOrder, itemX, itemY);
                }
                break;

        }
    }

    /**
     * Add a MatchUp to the database
     *
     * @param questionNumber int
     * @param itemOrder String
     * @param itemX Item
     * @param itemY Item
     */
    private void addMatchUp(int questionNumber, String itemOrder, Item itemX, Item itemY) {
        Item itemA;
        Item itemB;
        if(itemOrder.equals("Random")) {
            int r = new Random().nextInt(2);
            itemA = r == 0 ? itemX : itemY;
            itemB = r == 0 ? itemY : itemX;
        } else {
            itemA = itemX;
            itemB = itemY;
        }
        System.out.println("Q:" + String.valueOf(questionNumber) + " - A: " + itemA.getName() + " / B: " + itemB.getName());
        matchUpList.add(new MatchUp(questionNumber, itemA, itemB, null));
    }

    /**
     * Get the next question in the test
     *
     * @param selection String
     * @return MatchUp matchUp
     */
    public MatchUp nextQuestion(String selection) {
        if(matchUps.hasNext()) {
            if(selection != null) {
                matchUp.setDecision(selection);
            }
            matchUp = matchUps.next();
        } else {
            matchUp.setDecision(selection);
            matchUp = null;
            testCompleted();
        }
        return matchUp;
    }

    /**
     * Return to the previous question in the test
     *
     * @return MatchUp matchUp
     */
    public MatchUp previousQuestion() {
        if(matchUps.hasPrevious()) {
            return matchUps.previous();
        } else {
            return new MatchUp(-1, null, null, null);
        }
    }

    /**
     * Add a completed test to the database
     */
    private void testCompleted() {
        Session session = new Session(-1, userID, test.getID(), null);
        session.setTimestamp();
        Database db = new Database();
        int sessionID = db.insertSession(session.getUserID(),session.getTestID(),session.getTimestamp());
        for(MatchUp matchUp : getMatchUpList()) {
            db.insertMatchUp(sessionID, matchUp.getQuestionNumber(), matchUp.getItemA().getID(), matchUp.getItemB().getID(), matchUp.getDecision());
        }
        db.closeConnection();
    }

    /**
     * Generate a list of matchUps based on the selected test
     *
     * @param selectedTest Test
     */
    public void generate(Test selectedTest) {
        setTest(selectedTest);
        if(selectedTest != null) {
            setItems(test.getID());
            this.matchUpList = new ArrayList<>();
            createMatchUps();
            this.matchUps = matchUpList.listIterator();
        } else {
            this.test = null;
            this.items = null;
            this.matchUp = null;
            this.matchUpList = null;
            this.matchUps = null;
        }
    }
}