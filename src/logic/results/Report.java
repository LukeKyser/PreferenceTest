package logic.results;

import database.*;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Report
 * <p>
 * Business Logic Class for Results Reporting package.
 *
 * @author Christopher Manuel, Leron Tolmachev
 * @version 2017.12.08
 * <p>
 * Change Log:
 * - Refactored Project after Sprint One
 * - Made small change to findSession method to account for
 * multiple sessions
 * - Added findTests method and findSession method
 */

public class Report {

    private final ArrayList<User> users;
    private ArrayList<Test> tests;
    private final HashMap<String, ArrayList<Session>> sessions;
    private Session session;

    /**
     * Constructor for Report Class
     */
    public Report() {
        Database db = new Database();
        this.users = db.getUsersWithSessions();
        db.closeConnection();
        this.sessions = new HashMap<>();
    }

    /**
     * Access Reports' session field
     *
     * @return Session session
     */
    public Session getSession() {
        return session;
    }

    /**
     * Access Reports' session field
     *
     * @return Session session
     */
    public ArrayList<Session> getSessions(String testName) {
        return sessions.get(testName);
    }

    /**
     * Mutate Report's session field
     *
     * @param session Test Session to be scored
     */
    public void setSession(Session session) {
        this.session = session;
    }

    /**
     * Mutate Report's session field
     *
     * @param sessions Test Session to be scored
     */
    private void addSession(String testName, ArrayList<Session> sessions) {
        this.sessions.put(testName, sessions);
    }

    /**
     * Retrieves a list of all Tests from the Database
     *
     * @return ArrayList tests
     */
    public ArrayList<Test> getTests() {
        return tests;
    }

    /**
     *
     *
     */
    private void setTests(int userID) {
        Database db = new Database();
        this.tests = db.getTestsByUser(userID);
        db.closeConnection();
    }


    /**
     * Retrieves a list of all Users from the Database and sorts them by Name
     *
     * @return ArrayList usersByName
     */
    public ArrayList<User> getUsers() {
        return users;
    }


    public void generateSessions(User user) {
        setTests(user.getID());
        Database db = new Database();
        ArrayList<Session> allSessions = db.getSessionsByUserID(user.getID());
        db.closeConnection();
        for(Test test : tests) {
            ArrayList<Session> testSessions = new ArrayList<>();
            for(Session session : allSessions) {
                if(session.getTestID() == test.getID()) {
                    testSessions.add(session);
                }
            }
            addSession(test.getName(), testSessions);
        }

    }
}
