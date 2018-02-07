package database;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;

/**
 * ResultsDB
 * <p>
 * Class used for communicating with database, and for placing items
 * into arrays and then passing that information to the form(s).
 *
 * @author Brooke Higgins, Christopher Manuel, Leron Tolmachev, and Luke Kyser
 * @version 2017.11.29
 *
 * Change Log:
 * - Refactored Project after Sprint One
 * - Added insertImage method
 * - Added getTestByName method
 * - Changed insertItem to add an image to the item in the database
 * - Changed getItemByItemID and getItemsByTestID to add images to items from database
 * - Added getAllSessionsForUser method
 * - Removed getAllSessionsForUser method to refactor a different way of obtaining the needed data
 */
public class Database {
    // Final Database Strings
    private static final String DB_SERVER = "*****************";
    private static final String DATABASE = "******************";
    private static final String DB_USERNAME = "******************";
    private static final String DB_PASSWORD = "********************";
    private static final String DB_CONNECTION = "jdbc:jtds:sqlserver://"
            + DB_SERVER + "/" + DATABASE + ";user=" + DB_USERNAME + ";password=" + DB_PASSWORD;
    // Private variables
    private Connection mConnection = null;

    /**
     * Constructor for Database Class
     */
    public Database() {
        connect();
    }

    /**
     * Retrieve a single Item from Database whose ItemID matches the parameter
     *
     * @return Item item
     */
    private Item getItemByItemID(int itemID) {
        String itemQuery = "SELECT * FROM ITEM WHERE ItemID = ?;";
        ArrayList<Item> items = new ArrayList<>();
        try {
            PreparedStatement stmt = mConnection.prepareStatement(itemQuery);
            stmt.setInt(1, itemID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                InputStream stream = rs.getBinaryStream("Image");
                BufferedImage image = null;

                try {
                    if (stream != null)
                        image = ImageIO.read(stream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                items.add(new Item(rs.getInt("ItemID"),
                        rs.getInt("TestID"),
                        rs.getString("Name"),
                        image));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items.get(0);
    }

    /**
     * Retrieve all Items from Database whose TestID matches the parameter
     *
     * @return ArrayList items
     */
    public ArrayList<Item> getItemsByTestID(int testID) {
        String itemQuery = "SELECT * FROM ITEM WHERE TestID = ?;";
        ArrayList<Item> items = new ArrayList<>();
        try {
            PreparedStatement stmt = mConnection.prepareStatement(itemQuery);
            stmt.setInt(1, testID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                InputStream stream = rs.getBinaryStream("Image");
                BufferedImage image = null;

                try {
                    if (stream != null)
                        image = ImageIO.read(stream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                items.add(new Item(rs.getInt("ItemID"),
                        rs.getInt("TestID"),
                        rs.getString("Name"),
                        image));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    /**
     * Insert Item object into the Database's ITEM table
     *
     * @param testID indicates which Test an Item belongs to
     * @param name indicates the name of the Item
     */
    public void insertItem(int testID, String name, BufferedImage image) {
        String query = "INSERT INTO ITEM (TestID, Name, Image) VALUES (?, ?, ?)";
        try {
            PreparedStatement stmt = mConnection.prepareStatement(query);
            stmt.setInt(1, testID);
            stmt.setString(2, name);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            InputStream inputStream = null;
            int length = 0;

            try {
                if (image != null) {
                    ImageIO.write(image, "png", outputStream);
                    inputStream = new ByteArrayInputStream(outputStream.toByteArray());
                    length = inputStream.available();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            stmt.setBinaryStream(3, inputStream, length);

            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieve all MatchUps from Database whose SessionID matches the parameter
     *
     * @param sessionID indicates the Session with which all desired MatchUps are associated
     * @return ArrayList matchUps
     */
    public ArrayList<MatchUp> getMatchUps(int sessionID) {
        String itemsQuery = "SELECT QNumber, ItemID_A, ItemID_B, ISNULL(Decision,'')" +
                " AS Decision FROM MATCHUP WHERE SessionID = ?;";
        ArrayList<MatchUp> matchUps = new ArrayList<>();
        try {
            PreparedStatement stmt = mConnection.prepareStatement(itemsQuery);
            stmt.setInt(1, sessionID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                matchUps.add(new MatchUp(rs.getInt("QNumber"),
                        (getItemByItemID(rs.getInt("ItemID_A"))),
                        (getItemByItemID(rs.getInt("ItemID_B"))),
                        rs.getString("Decision")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return matchUps;
    }

    /**
     * Insert MatchUp object into the Database's MATCHUP table
     *
     * @param sessionID indicates which Session a MatchUp belongs to
     * @param questionNumber indicates order in which MatchUp was presented to user
     * @param itemAID indicates item presented on Left side of test form
     * @param itemBID indicates item presented on Right side of test form
     * @param decision indicates which of the above items user selected (can be "" for neither)
     */
    public void insertMatchUp(int sessionID, int questionNumber, int itemAID, int itemBID, String decision) {
        String query = "INSERT INTO MATCHUP (SessionID, QNumber, ItemID_A, ItemID_B, Decision)" +
                " VALUES (?, ?, ?, ?, ?);";
        try {
            PreparedStatement stmt = mConnection.prepareStatement(query);
            stmt.setInt(1, sessionID);
            stmt.setInt(2, questionNumber);
            stmt.setInt(3, itemAID);
            stmt.setInt(4, itemBID);
            stmt.setString(5, decision);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieve all Sessions from Database whose TestID matches the parameter
     *
     * @return ArrayList sessions
     */
    public ArrayList<Session> getSessionsByTestID(int testID) {
        String sessionQuery = "SELECT * FROM SESSION WHERE TestID = ? ORDER BY Timestamp;";
        ArrayList<Session> sessions = new ArrayList<>();
        try {
            PreparedStatement stmt = mConnection.prepareStatement(sessionQuery);
            stmt.setInt(1, testID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                sessions.add(new Session(rs.getInt("SessionID"),
                        rs.getInt("UserID"),
                        rs.getInt("TestID"),
                        rs.getString("Timestamp")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sessions;
    }

    /**
     * Retrieves all Sessions from Database whose UserID matches the parameter
     *
     * @param userID int representing userID of desired Session
     * @return ArrayList sessions
     */
    public ArrayList<Session> getSessionsByUserID(int userID) {
        String sessionQuery = "SELECT * FROM SESSION WHERE UserID = ? ORDER BY Timestamp;";
        ArrayList<Session> sessions = new ArrayList<>();
        try {
            PreparedStatement stmt = mConnection.prepareStatement(sessionQuery);
            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                sessions.add(new Session(rs.getInt("SessionID"),
                        rs.getInt("UserID"),
                        rs.getInt("TestID"),
                        rs.getString("Timestamp")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sessions;
    }

    /**
     * Insert Session object into the Database's SESSION table
     *
     * @param userID indicates which User completed the Test
     * @param testID indicates which Test User completed
     * @param timestamp indicates date and time of Test completion
     */
    public int insertSession(int userID, int testID, String timestamp) {
        String query = "INSERT INTO SESSION (UserID, TestID, Timestamp) " +
                "VALUES (?, ?, ?); SELECT SCOPE_IDENTITY() AS ID;";
        try {
            PreparedStatement stmt = mConnection.prepareStatement(query);
            stmt.setInt(1, userID);
            stmt.setInt(2, testID);
            stmt.setString(3, timestamp);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("ID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Retrieves all Tests from Database
     *
     * @return ArrayList test
     */
    public ArrayList<Test> getTests() {
        String testQuery = "SELECT * FROM TEST ORDER BY Name";
        ArrayList<Test> tests = new ArrayList<>();
        try {
            PreparedStatement stmt = mConnection.prepareStatement(testQuery);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tests.add(new Test(rs.getInt("TestID"),
                        rs.getString("Name"),
                        rs.getString("Settings")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tests;
    }

    /**
     * Retrieves Names and IDs of all Tests taken by a specific User from Database
     *
     * @return ArrayList test
     */
    public ArrayList<Test> getTestsByUser(int userID) {
        String testQuery = "SELECT DISTINCT TEST.TestID, Name FROM TEST " +
                "JOIN SESSION ON (TEST.TestID = SESSION.TestID) WHERE UserID = ? ORDER BY Name;";
        ArrayList<Test> tests = new ArrayList<>();
        try {
            PreparedStatement stmt = mConnection.prepareStatement(testQuery);
            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tests.add(new Test(rs.getInt("TestID"),
                        rs.getString("Name"),
                        null));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tests;
    }

    /**
     * Insert Test object into Database's TEST table
     *
     * @param testName name of the Test
     * @return int testID
     */
    public int insertTest(int id, String testName, String settings) {
        if (id == -1) {
            String query = "INSERT INTO TEST (Name, Settings) VALUES (?, ?); SELECT SCOPE_IDENTITY() AS ID;";
            try {
                PreparedStatement stmt = mConnection.prepareStatement(query);
                stmt.setString(1, testName);
                stmt.setString(2, settings);
                ResultSet rs = stmt.executeQuery();
                if(rs.next()) {
                    return rs.getInt("ID");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            String query = "UPDATE TEST SET Settings = ? WHERE TestID = ?;";
            try {
                PreparedStatement stmt = mConnection.prepareStatement(query);
                stmt.setString(1, settings);
                stmt.setInt(2, id);
                stmt.execute();
                return id;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }



    /**
     * Retrieve a single User from Database whose Email matches the parameter
     *
     * @param email String representing Email address of desired user
     * @return User user
     */
    public User getUserByEmail(String email) {
        String emailQuery = "SELECT * FROM USER_ACCOUNT WHERE Email = ?;";
        ArrayList<User> users = new ArrayList<>();
        try {
            PreparedStatement stmt = mConnection.prepareStatement(emailQuery);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                users.add(new User(rs.getInt("UserID"),
                        rs.getString("Role"),
                        rs.getString("FirstName"),
                        rs.getString("LastName"),
                        rs.getString("Email"),
                        rs.getString("Password")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users.size() == 0 ? null : users.get(0);
    }



    /**
     * Retrieve identity information (but not passwords) for all Users in
     * Database who have completed a Test Session and whose Role is User
     *
     * @return ArrayList users
     */
    public ArrayList<User> getUsersWithSessions() {
        String userQuery = "SELECT DISTINCT USER_ACCOUNT.UserID, FirstName, LastName, Email " +
                "FROM USER_ACCOUNT JOIN SESSION ON (USER_ACCOUNT.UserID = SESSION.UserID) " +
                "WHERE Role = 'User' ORDER BY LastName, FirstName, Email;";
        ArrayList<User> users = new ArrayList<>();
        try {
            PreparedStatement stmt = mConnection.prepareStatement(userQuery);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                users.add(new User(rs.getInt("UserID"), "User",
                        rs.getString("FirstName"),
                        rs.getString("LastName"),
                        rs.getString("Email"),
                        null));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    /**
     * Insert User object the Database's USER_ACCOUNT table
     *
     * @param firstName First name of new User
     * @param lastName Last name of new User
     * @param email Email address of new User
     * @param password Password of new User
     */
    public void insertUser(String firstName, String lastName, String email, String password) {
        String query = "INSERT INTO USER_ACCOUNT (Role, FirstName, LastName, Email, Password) " +
                "VALUES ('User', ?, ?, ?, ?); SELECT SCOPE_IDENTITY() AS ID;";
        try {
            PreparedStatement stmt = mConnection.prepareStatement(query);
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, email);
            stmt.setString(4, password);
            stmt.executeUpdate();
            System.out.println("Account successfully created");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Close connection to Database
     */
    public void closeConnection() {
        if (mConnection != null) {
            try {
                mConnection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Create a connection to the Database
     */
    private void connect() {
        if (mConnection != null)
            return;
        try {
            mConnection = DriverManager.getConnection(DB_CONNECTION);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteFromDatabase(String table, int id) {
        String criteria = table.toUpperCase().equals("MATCHUP") ? "SessionID" : "TestID";
        String deleteQuery = "DELETE FROM " + table.toUpperCase() + " WHERE " + criteria + " = ?";
        try {
            PreparedStatement stmt = mConnection.prepareStatement(deleteQuery);
            stmt.setInt(1, id);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
