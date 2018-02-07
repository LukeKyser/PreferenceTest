package database;

/**
 * User
 * <p>
 * Class for communicating and receiving information from the database about a user
 *
 * @author Brooke Higgins, Christopher Manuel, Leron Tolmachev.
 * @version 2017.11.11
 * <p>
 * Change Log:
 * - Refactored Project after Sprint One
 */
public class User {
    private final int userID;
    private final String role;
    private final String firstName;
    private final String lastName;
    private String password;
    private String email;

    /**
     * Constructor for User Class
     *
     * @param userID sets new User's userID field
     * @param role sets new User's role field
     * @param firstName sets new User's firstName field
     * @param lastName sets new User's lastName field
     * @param email sets new User's email field
     * @param password sets new user's password field
     */
    public User(int userID, String role, String firstName, String lastName, String email, String password) {
        this.userID = userID;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    /**
     * Access User's Email field
     *
     * @return String email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Mutate User's email field
     *
     * @param email desired Email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Access User's firstName field
     *
     * @return String firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Access User's userID field
     *
     * @return int userID
     */
    public int getID() {
        return userID;
    }

    /**
     * Access User's lastName field
     *
     * @return String lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Access User's password field
     *
     * @return String password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Mutate User's password field
     *
     * @param password desired password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Access User's Role field
     *
     * @return String role
     */
    public String getRole() {
        return role;
    }

    //Formatting to appropriately display Users JLists and JComboBoxes.
    @Override
    public String toString() {
        String userListing ;
        if(getEmail() == null) {
            userListing = "Select a User...";
        } else {
            int gapSize = 25 - getFirstName().length() - getLastName().length();
            String gap = new String(new char[gapSize]).replace("\0", " ");
            userListing = (getEmail().equals("") ? "Select a " + getFirstName() + "..." : getLastName() + ", " + getFirstName() + gap + getEmail());
        }
        return userListing;
    }

}
