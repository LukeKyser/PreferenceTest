package gui.login;

import database.User;
import logic.login.LoginAttempt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import static gui.MainGUI.*;
import static resources.Constants.BUSINESS_NAME;

/**
 * LoginForm
 * <p>
 * GUI for user Login form.
 *
 * @author Brooke Higgins
 * @version 2017.11.03
 * <p>
 * Change Log:
 * - Refactored Project after Sprint One
 * -
 */

public class LoginForm {

    private static final String INVALID_LOGIN_TEXT = "<html><body style='text-align: center;'>We were unable to locate a user account<br />with the information you provided.<br />Please try again.</body></html>";
    private static final String INVALID_LOGIN_TITLE = "Invalid Login Credentials";

    private final LoginAttempt loginAttempt;

    private JPanel rootPanel;
    // Company Name and Window Description
    private JLabel businessLabel;
    private JLabel descriptionLabel;
    // User Details
    private JLabel emailLabel;
    private JTextField emailField;
    private JLabel passwordLabel;
    private JPasswordField passwordField;
    // Navigation Buttons
    private JButton createAccountButton;
    private JButton logInButton;
    private JLabel emailIcon;
    private JLabel passwordIcon;

    /**
     * Constructor for the LoginForm Class
     *
     * @param loginAttempt stores the business logic for UserLogin Package
     * @param frame JFrame containing LoginForm GUI
     */
    public LoginForm(LoginAttempt loginAttempt, JFrame frame) {

        this.loginAttempt = loginAttempt;

        rootPanel.setPreferredSize(new Dimension(650, 200));

        ImageIcon WARNING_ICON = new ImageIcon(getClass().getResource("/Resources/warning.gif"));
        emailIcon.setIcon(WARNING_ICON);
        emailIcon.setVisible(false);
        passwordIcon.setIcon(WARNING_ICON);
        passwordIcon.setVisible(false);


        frame.setTitle("Log In to Existing User Account");
        businessLabel.setText(BUSINESS_NAME);

        emailField.setText(loginAttempt.getUser().getEmail());
        passwordField.setText(loginAttempt.getUser().getPassword());

        // Create Action Listeners
        ActionListener createAccount = e -> {
            loginAttempt.setUser(new User(-1, "User", null, null, emailField.getText(), passwordField.getText()));
            changeLoginGUI();
        };
        ActionListener login = e -> {
            if (!emailIcon.isVisible() && !passwordIcon.isVisible()) {
                loginAttempt.getUser().setEmail(emailField.getText());
                loginAttempt.getUser().setPassword(passwordField.getText());
                if (loginAttempt.login()) {
                    frame.dispose();
                    System.out.println(" - SUCCESSFUL LOGIN - ");
                } else {
                    JOptionPane.showMessageDialog(null,
                            INVALID_LOGIN_TEXT,
                            INVALID_LOGIN_TITLE,
                            JOptionPane.INFORMATION_MESSAGE);
                    passwordField.setText("");
                }
            } else {
                if(passwordIcon.isVisible()) {
                    passwordField.grabFocus();
                }
                if(emailIcon.isVisible()) {
                    emailField.grabFocus();
                }
            }
        };

        FocusAdapter validateField = new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                String field = ((JTextField) e.getComponent()).getToolTipText().substring(6, ((JTextField) e.getComponent()).getToolTipText().indexOf(" "));
                validate(field);
            }
        };

        // Add Action Listeners
        createAccountButton.addActionListener(createAccount);
        logInButton.addActionListener(login);
        emailField.addFocusListener(validateField);
        passwordField.addFocusListener(validateField);
        passwordField.addActionListener(login);

    }

    /**
     * Returns the rootPanel to the UserLoginMain Class.
     *
     * @return JPanel rootPanel
     */
    public JPanel getRootPanel() {
        return rootPanel;
    }

    private void validate(String field) {
        switch(field) {
            case "Email":
                emailIcon.setVisible(!loginAttempt.validateEmailAddress(emailField.getText()));
                break;
            case "Password":
                passwordIcon.setVisible(!loginAttempt.validatePassword(passwordField.getText()));
                break;
        }
    }
}

