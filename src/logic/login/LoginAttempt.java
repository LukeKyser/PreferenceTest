package logic.login;

import database.Database;
import database.User;

import static gui.MainGUI.*;


/**
 * TestSession
 * <p>
 * Generate unique Test Session for logged in user based upon test items
 *
 * @author Brooke Higgins and Leron Tolmachev
 * @version 2017.11.11
 * <p>
 * Change Log:
 * - Refactored Project after Sprint One
 * - Created Class to handle business logic of UserLogin Package
 */
public class LoginAttempt {

    private User user;

    /**
     * Constructor for TestSession Class
     */
    public LoginAttempt() {
        this.user = new User(-1, "User", null, null, null, null);
    }

    /**
     * Access LoginAttempt's User field
     *
     * @return User user
     */
    public User getUser() {
        return user;
    }

    /**
     * Mutate LoginAttempt's user field
     *
     * @param user stores information entered in to Form fields
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Checks whether User's credentials match Database record, if so, redirects User to appropriate destination Form
     *
     * @return boolean match
     */
    public boolean login() {
        boolean match = false;
        Database db = new Database();
        User compareUser = db.getUserByEmail(user.getEmail());
        db.closeConnection();
        if(compareUser != null && user.getPassword().equals(compareUser.getPassword())) {
            match = true;
            setUser(compareUser);
            switch (user.getRole()) {
                case "Admin":
                    createSetupGUI();
                    break;
                case "Therapist":
                    createReportGUI();
                    break;
                case "User":
                    createTestGUI(user.getID());
                    break;
                default:
                    System.out.println("!!!!! INVALID ROLE !!!!!");
                    break;
            }
        }
        return match;
    }

    /**
     * Checks whether User already exist in Database, if not, inserts User
     *
     * @return boolean registered
     */
    public boolean register() {
        boolean registered = false;
        Database db = new Database();
        if(db.getUserByEmail(user.getEmail()) == null) {
            db.insertUser(user.getFirstName(), user.getLastName(), user.getEmail(), user.getPassword());
            registered = true;
        }
        db.closeConnection();
        return registered;
    }

    public boolean validatePassword(String password) {
        return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*([0-9]|[!@#$%^&+=]))(?=\\S+$).{8,}$");
    }

    public boolean validateEmailAddress(String email) {
        return email.matches("^[(a-zA-Z-0-9-\\_\\+\\.)]+@[(a-z-A-z)]+\\.[(a-zA-z)]{2,3}$");
    }
}

