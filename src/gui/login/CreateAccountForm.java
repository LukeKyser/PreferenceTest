package gui.login;

import database.User;
import logic.login.LoginAttempt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import static gui.MainGUI.changeLoginGUI;
import static resources.Constants.BUSINESS_NAME;

/**
 * @author Brooke Higgins and Leron Tolmachev
 * @version 2017.11.15
 *          <p>
 *          Change Log:
 *          - Refactored Project after Sprint One
 */
public class CreateAccountForm {

    private final JFrame frame;
    private final LoginAttempt loginAttempt;

    private JPanel rootPanel;
    private JPanel contentPanel;
    // Company Name and Window Description
    private JLabel businessLabel;
    private JLabel descriptionLabel;
    // User Name Details
    private JLabel firstNameLabel;
    private JTextField firstNameField;
    private JLabel lastNameLabel;
    private JTextField lastNameField;
    // User Email Details
    private JLabel emailLabel;
    private JTextField emailField;
    private JLabel emailConfirmLabel;
    private JTextField emailConfirmField;
    // User Password Details
    private JLabel passwordLabel;
    private JPasswordField passwordField;
    private JLabel passwordConfirmLabel;
    private JPasswordField passwordConfirmField;
    // Navigation Buttons
    private JButton clearButton;
    private JButton loginButton;
    private JButton submitButton;
    private JLabel firstNameIcon;
    private JLabel lastNameIcon;
    private JLabel emailIcon;
    private JLabel emailConfirmIcon;
    private JLabel passwordIcon;
    private JLabel passwordConfirmIcon;

    /**
     * Constructor for the CreateAccountForm Class
     *
     * @param loginAttempt stores the business logic for UserLogin Package
     * @param frame        JFrame containing CreateAccountForm GUI
     */
    public CreateAccountForm(LoginAttempt loginAttempt, JFrame frame) {

        rootPanel.setPreferredSize(new Dimension(650, 375));

        this.frame = frame;
        this.loginAttempt = loginAttempt;

        frame.setTitle("Create a New User Account");
        businessLabel.setText(BUSINESS_NAME);

        emailField.setText(loginAttempt.getUser().getEmail());
        passwordField.setText(loginAttempt.getUser().getPassword());
        firstNameField.requestFocusInWindow();

        ImageIcon WARNING_ICON = new ImageIcon(getClass().getResource("/Resources/warning.gif"));
        firstNameIcon.setIcon(WARNING_ICON);
        firstNameIcon.setVisible(false);
        lastNameIcon.setIcon(WARNING_ICON);
        lastNameIcon.setVisible(false);
        emailIcon.setIcon(WARNING_ICON);
        emailIcon.setVisible(false);
        emailConfirmIcon.setIcon(WARNING_ICON);
        emailConfirmIcon.setVisible(false);
        passwordIcon.setIcon(WARNING_ICON);
        passwordIcon.setVisible(false);
        passwordConfirmIcon.setIcon(WARNING_ICON);
        passwordConfirmIcon.setVisible(false);

        //Create Action Listeners
        ActionListener clear = e -> {
            firstNameField.setText("");
            lastNameField.setText("");
            emailField.setText("");
            emailConfirmField.setText("");
            passwordField.setText("");
            passwordConfirmField.setText("");
        };
        ActionListener login = e -> {
            loginAttempt.getUser().setEmail(emailField.getText());
            loginAttempt.getUser().setPassword(passwordField.getText());
            changeLoginGUI();
        };

        ActionListener register = e -> registerAttempt();

        FocusAdapter validateField = new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                String field = ((JTextField) e.getComponent()).getToolTipText();
                validate(field);
            }
        };

        //Add Action Listeners
        firstNameField.addFocusListener(validateField);
        lastNameField.addFocusListener(validateField);
        emailField.addFocusListener(validateField);
        emailConfirmField.addFocusListener(validateField);
        passwordField.addFocusListener(validateField);
        passwordConfirmField.addFocusListener(validateField);
        passwordConfirmField.addActionListener(register);
        clearButton.addActionListener(clear);
        loginButton.addActionListener(login);
        submitButton.addActionListener(register);

    }

    /**
     * Returns the rootPanel to the UserLoginMain Class.
     *
     * @return the rootPanel.
     */
    public JPanel getRootPanel() {
        return rootPanel;
    }

    private void registerAttempt() {
        if(!firstNameField.getText().isEmpty() &&
                !lastNameField.getText().isEmpty() &&
                loginAttempt.validateEmailAddress(emailField.getText()) &&
                emailConfirmField.getText().toLowerCase().equals(emailField.getText().toLowerCase()) &&
                loginAttempt.validatePassword(passwordField.getText()) &&
                passwordConfirmField.getText().equals(passwordField.getText())) {
            loginAttempt.setUser(new User(-1, "User", firstNameField.getText(), lastNameField.getText(), emailField.getText().toLowerCase(), passwordField.getText()));
            if (loginAttempt.register()) {
                frame.dispose();
                loginAttempt.login();
            }
        } else {
            System.out.println("NOPE");
        }
    }

    private void validate(String field) {
        switch(field) {
            case "First Name":
                firstNameIcon.setVisible(firstNameField.getText().isEmpty() || firstNameField.getText() == null);
                break;
            case "Last Name":
                lastNameIcon.setVisible(lastNameField.getText().isEmpty() || lastNameField.getText() == null);
                break;
            case "Email Address":
                emailIcon.setVisible(!loginAttempt.validateEmailAddress(emailField.getText()));
                break;
            case "Confirm Email Address":
                emailConfirmIcon.setVisible(!emailField.getText().toLowerCase().equals(emailConfirmField.getText().toLowerCase()));
                break;
            case "Password":
                passwordIcon.setVisible(!loginAttempt.validatePassword(passwordField.getText()) || passwordField.getText().length() < 8);
                break;
            case "Confirm Password":
                passwordConfirmIcon.setVisible(!passwordField.getText().equals(passwordConfirmField.getText()));
                break;
        }
    }
}
