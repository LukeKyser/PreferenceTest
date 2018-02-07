package database;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Session
 *
 * A class to represent a record within the Database's SESSION table
 *
 * @author Leron Tolmachev, Christopher Manuel
 * @version 2017.12.02
 *
 * Change Log:
 * - Refactored Project after Sprint One
 */
public class Session {
    private final int sessionID;
    private final int userID;
    private final int testID;
    private String timestamp;

    /**
     * Constructor for Session Class
     *
     * @param sessionID unique identifier for this Session
     * @param userID identifies which User completed this Session
     * @param testID identifies Test with which this Session is associated
     * @param timestamp identifies date and time at which Session was completed
     */
    public Session(int sessionID, int userID, int testID, String timestamp) {
        this.sessionID = sessionID;
        this.userID = userID;
        this.testID = testID;
        this.timestamp = timestamp;
    }

    /**
     * Access Session's sessionID field
     *
     * @return int sessionID
     */
    public int getSessionID() {
        return sessionID;
    }

    /**
     * Access Session's timestamp field
     *
     * @return int testID
     */
    public int getTestID() {
        return testID;
    }

    /**
     * Access Session's timestamp field
     *
     * @return String timestamp
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Mutate TestSessions's timestamp field to current datetime
     */
    public void setTimestamp() {
        this.timestamp = String.valueOf(LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm:ss a")));
    }

    /**
     * Access Session's userID field
     *
     * @return int userID
     */
    public int getUserID() {
        return userID;
    }

    //Formatting to appropriately display Session Times in JLists and JComboBoxes.
    @Override
    public String toString() { return getTimestamp() == null ? "Select a Session Date..." : getTimestamp(); }
}
