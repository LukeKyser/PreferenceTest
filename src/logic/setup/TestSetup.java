package logic.setup;

import database.*;

import java.util.ArrayList;

/**
 * Class TestSetup.java.
 *
 * @author Luke Kyser
 * @version 2017.11.28
 *
 * Change Log:
 * - Refactored Project after Sprint One
 * -
 */
public class TestSetup {
    private Test test;
    private ArrayList<Test> tests;

    /**
     * Constructor for TestSetup Class
     */
    public TestSetup(){
        setTests();
    }

    public Test getTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
        if(test != null && test.getID() != -1) {
            setItems(test.getID());
        }
    }

    public ArrayList<Item> getItems() {
        return test.getItems();
    }

    private void setItems(int testID) {
        Database db = new Database();
        test.setItems(db.getItemsByTestID(testID));
        db.closeConnection();
    }

    public void setTests() {
        Database db = new Database();
        tests = db.getTests();
        db.closeConnection();
    }

    public ArrayList<Test> getTests() {
        return tests;
    }

    /**
     * Check whether no Sessions exist for the current TestID
     *
     * @return boolean foundSessions
     */
    public boolean findSessions() {
        boolean foundSessions = false;
        if(test.getID() != -1) {
            Database db = new Database();
            foundSessions = !db.getSessionsByTestID(test.getID()).isEmpty();
            db.closeConnection();
        }
        return foundSessions;
    }

    /**
     * Submit changes to Test and Items to Database. If testID equals -1, the
     * test is new and doesn't exist in the database. Otherwise, the items
     * in the test need to be deleted in the database before the new list of items
     * is added.
     *
     */
    public void completeSetup() {
        Database db = new Database();

        if (test.getID() != -1) {
            db.deleteFromDatabase("ITEM", test.getID());
        }
        test.setID(db.insertTest(test.getID(), test.getName(),test.getSettings("all")));
        for(Item item : test.getItems()) {
            db.insertItem(test.getID(), item.getName(), item.getImage());
        }
    }

    public void deleteTest(boolean lockedTest) {
        Database db = new Database();
        if(lockedTest) {
            ArrayList<Session> sessions = db.getSessionsByTestID(test.getID());
            for(Session session : sessions) {
                db.deleteFromDatabase("MATCHUP", session.getSessionID());
            }
            db.deleteFromDatabase("SESSION", test.getID());
        }
        db.deleteFromDatabase("ITEM", test.getID());
        db.deleteFromDatabase("TEST", test.getID());
        db.closeConnection();
        setTest(new Test(-1, null, null));
    }

}