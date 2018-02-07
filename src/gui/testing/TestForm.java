package gui.testing;

import database.*;
import gui.customSwingClasses.PicturePanel;
import logic.testing.TestSession;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static resources.Constants.BUSINESS_NAME;

/**
 * TestForm
 *
 * GUI for Testing form.
 *
 * @author Leron Tolmachev
 * @version 2017.11.11
 *
 * Change Log:
 * - Refactored Project after Sprint One
 *
 */
public class TestForm {

    private static final Color UNSELECTED_BACKGROUND = Color.decode("#F5F6F7");
    private static final Color SELECTED_BACKGROUND = Color.decode("#7FB5F1"); // Old selected color
    private static final Color SELECTED_BORDER = Color.decode("#1681FA");
    private static final Dimension SIZE_SMALL = new Dimension(650, 115);
    private static final Dimension SIZE_LARGE = new Dimension(650, 475);
    private static final String TEST_TITLE = " Comparison Test - Question #";
    private static final String WINDOW_TITLE = "Select a Comparison Test";
    private static final String ABORT_TEST_TITLE = "Abort Test Session?";
    private static final String ABORT_TEST_TEXT = "Cancel current Test Session and\nreturn to test selection screen?";
    private static final String COMPLETE_TEST_TITLE = "Test Completed";
    private static final String COMPLETE_TEST_TEXT = "Thank you for completing this test.\nWould you like to take another test?";
    private static final String PROGRESS_TEXT_1 = "Question ";
    private static final String PROGRESS_TEXT_2 = " out of ";

    private final TestSession session;
    private final JFrame frame;
    private JPanel rootPanel;
    // Company Name and Window Description
    private JLabel businessLabel;
    private JLabel questionLabel;
    // Test Selection
    private JComboBox<Test> testChooserComboBox;
    // Test Options
    private JPanel testTakinganel;
    private JPanel buttonsPanel;
    private final ButtonGroup buttonGroup = new ButtonGroup();
    private JPanel itemAPanel;
    private PicturePanel itemAImage;
    private JRadioButton itemARadioButton;
    private JLabel orLabel;
    private JPanel itemBPanel;
    private PicturePanel itemBImage;
    private JRadioButton itemBRadioButton;
    private JPanel item0Panel;
    private JRadioButton item0RadioButton;
    // Navigation Buttons
    private JButton backButton;
    private JProgressBar progressBar;
    private JButton submitButton;
    private JPanel testSelectionPanel;

    private boolean wentBack = false;

    /**
     * Constructor for the TestForm Class
     *
     * @param session stores the business logic for Test Package
     * @param frame JFrame containing TestForm GUI
     */
    public TestForm(TestSession session, JFrame frame) {
        this.session = session;
        this.frame = frame;

        rootPanel.setPreferredSize(SIZE_SMALL);
        frame.setTitle(WINDOW_TITLE);

        // Hide testTakinganel
        businessLabel.setText(BUSINESS_NAME);
        testTakinganel.setVisible(false);
        // Set testChooserComboBox contents
        testChooserComboBox.setMaximumRowCount(10);
        testChooserComboBox.addItem(new Test(-1, null, "Which do you prefer?\\I can't decide\\Sequential\\Ordered\\ "));
        for(Test test : session.getTests()) {
            testChooserComboBox.addItem(test);
        }
        int itemCount = testChooserComboBox.getItemCount();
        testChooserComboBox.setMaximumRowCount(itemCount >= 15 ? 15 : itemCount);

        // Initialize buttonGroup
        buttonGroup.add(itemARadioButton);
        itemARadioButton.setActionCommand("A");
        buttonGroup.add(itemBRadioButton);
        itemBRadioButton.setActionCommand("B");
        buttonGroup.add(item0RadioButton);
        item0RadioButton.setActionCommand("");

        // Create listeners for button selection
        MouseAdapter selectOption = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                String selection;
                if(e.getSource() instanceof JPanel) {
                    selection = ((JRadioButton)((JPanel) e.getSource()).getComponent(0)).getActionCommand();
                } else {
                    selection = buttonGroup.getSelection().getActionCommand();
                }
                formatSelection(selection);

            }
        };
        // Add Listeners
        itemAPanel.addMouseListener(selectOption);
        itemARadioButton.addMouseListener(selectOption);
        itemBPanel.addMouseListener(selectOption);
        itemBRadioButton.addMouseListener(selectOption);
        item0Panel.addMouseListener(selectOption);
        item0RadioButton.addMouseListener(selectOption);

        ActionListener selectTest = e -> {
            if(testChooserComboBox.getSelectedIndex() != 0) {
                session.generate((Test) testChooserComboBox.getSelectedItem());
                toggleFrame();
                testChooserComboBox.setEnabled(false);
                displayMatchUp(session.nextQuestion(null));
                questionLabel.setText(session.getTest().getSettings("QuestionText"));
                item0RadioButton.setText(session.getTest().getSettings("TieText"));
            }
        };

        testChooserComboBox.addActionListener(selectTest);

        ActionListener backButton = e -> {
            if (!wentBack) {
                session.previousQuestion();
                wentBack = true;
            }
            MatchUp matchUp = session.previousQuestion();
            if (matchUp.getQuestionNumber() == -1) {
                if (confirmDecision("Abort") == 0) {
                    resetForm();
                }
            } else {
                displayMatchUp(matchUp);
            }
        };

        this.backButton.addActionListener(backButton);

        // Adds listener for answer submission
        ActionListener submitButton = e -> {

            if(wentBack) {
                session.nextQuestion(session.getMatchUp().getDecision());
                wentBack = false;
            }
            displayMatchUp(session.nextQuestion(getSelection()));
        };

        this.submitButton.addActionListener(submitButton);
    }

    /**
     * Access TestForm's GUI root panel
     *
     * @return JPanel rootPanel
     */
    public JPanel getRootPanel() {
        return rootPanel;
    }

    /**
     * Access currently selected Test option
     *
     * @return JRadioButton
     */
    private String getSelection() {
        String selection = null;
        if(itemARadioButton.isSelected()) {
            selection = "A";
        } else if (itemBRadioButton.isSelected()) {
            selection = "B";
        } else if (item0RadioButton.isSelected()) {
            selection = "";
        }
        return selection;
    }

    /**
     * Clear selection from TestForm
     */
    private void clearSelection() {
        buttonGroup.clearSelection();
        itemAImage.setBgColor(UNSELECTED_BACKGROUND);
        itemAImage.repaint();
        itemBImage.setBgColor(UNSELECTED_BACKGROUND);
        itemBImage.repaint();
        Component[] components = buttonsPanel.getComponents();
        for(Component c : components) {
            if(c instanceof JPanel) {
                ((JPanel) c).setBorder(BorderFactory.createRaisedBevelBorder());
//                ((JPanel) c).setBorder(BorderFactory.createMatteBorder(3,3,3,3,Color.BLACK));
                ((JPanel) c).getComponent(0).setBackground(UNSELECTED_BACKGROUND);
            }
            c.setBackground(UNSELECTED_BACKGROUND);
        }
    }

    /**
     * Hide question-related GUI elements and display completion message
     */
    private void testCompleted() {
        frame.setVisible(false);
        if(confirmDecision("Complete") == 0) {
            frame.setVisible(true);
            resetForm();
        } else {
            frame.dispose();
        }
    }

    /**
     * Confirm whether User wants to abort current Test
     *
     * @return int dialogResponse
     */
    private int confirmDecision(String resetType) {
        String text = "Return to Test Selection Menu?";
        String title = "Exit Test";
        switch(resetType) {
            case "Abort":
                text = ABORT_TEST_TEXT;
                title = ABORT_TEST_TITLE;
                break;
            case "Complete":
                text = COMPLETE_TEST_TEXT;
                title = COMPLETE_TEST_TITLE;
                break;
            default:
                System.out.print("!!!!! Error with Dialog Box !!!!!");
                break;
        }
        return JOptionPane.showOptionDialog(null,
                text, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
    }

    /**
     * Paint buttons so that selected Option appears pressed
     *
     * @param selection String indicating selected option
     */
    private void formatSelection(String selection) {
        clearSelection();
        JPanel panel = null;
        PicturePanel image = null;
        JRadioButton button = null;
        if(selection == null) {
            submitButton.setEnabled(false);
        } else{

            switch(selection) {
                case "A":
                    panel = itemAPanel;
                    image = itemAImage;
                    button = itemARadioButton;
                    break;
                case "B":
                    panel = itemBPanel;
                    image = itemBImage;
                    button = itemBRadioButton;
                    break;
                default:
                    panel = item0Panel;
                    button = item0RadioButton;
                    break;
            }
        }
        if(button != null && panel != null) {
            button.setSelected(true);
            button.setBackground(SELECTED_BACKGROUND);
            if(image != null) {
                image.setBgColor(SELECTED_BACKGROUND);
                image.repaint();
            }
            panel.setBackground(SELECTED_BACKGROUND);
//            panel.setBorder(BorderFactory.createLoweredBevelBorder());
            panel.setBorder(BorderFactory.createMatteBorder(2,2,2,2, SELECTED_BORDER));
            submitButton.setEnabled(true);
        }
    }

    /**
     * Populate testTakinganel with Question data
     *
     * @param matchUp Matchup containing data for Test Question
     */
    private void displayMatchUp(MatchUp matchUp) {
        if(matchUp != null) {
            frame.setTitle(session.getTest().getName() + TEST_TITLE + String.valueOf(matchUp.getQuestionNumber()));
            itemARadioButton.setText(matchUp.getItemA().getName());
            itemAImage.setImage(matchUp.getItemA().getImage());
            itemBRadioButton.setText(matchUp.getItemB().getName());
            itemBImage.setImage(matchUp.getItemB().getImage());
            formatSelection(matchUp.getDecision());
            progressBar.setString(PROGRESS_TEXT_1 + matchUp.getQuestionNumber() + PROGRESS_TEXT_2 + session.getMatchUpList().size());
            progressBar.setValue((matchUp.getQuestionNumber() - 1) * 100 / session.getMatchUpList().size());
            submitButton.setText(matchUp.getQuestionNumber() == session.getMatchUpList().size() ? "Finish" : "Next >>");
            submitButton.setEnabled(matchUp.getDecision() != null);
        } else{
            testCompleted();
        }
    }

    private void resetForm() {
        wentBack = false;
        clearSelection();
        testChooserComboBox.setEnabled(true);
        testChooserComboBox.setSelectedIndex(0);
        session.generate(null);
        toggleFrame();
    }

    private void toggleFrame() {
        if(frame.getTitle().equals(WINDOW_TITLE)) {
            testSelectionPanel.setVisible(false);
            testTakinganel.setVisible(true);
            rootPanel.setPreferredSize(SIZE_LARGE);
        } else {
            frame.setTitle(WINDOW_TITLE);
            testSelectionPanel.setVisible(true);
            testTakinganel.setVisible(false);
            rootPanel.setPreferredSize(SIZE_SMALL);
        }
        frame.validate();
        frame.repaint();
        frame.pack();
    }
}


