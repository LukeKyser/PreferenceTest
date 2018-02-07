package gui.setup;

import database.*;
import gui.customSwingClasses.DragAndDropPicturePanel;
import logic.setup.TestSetup;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.net.URL;

import static gui.MainGUI.changeSetupGUI;
import static resources.Constants.BUSINESS_NAME;

/**
 * SetupForm
 * <p>
 * GUI for Test Setup form.
 *
 * @author Luke Kyser
 * @version 2017.11.28
 * <p>
 * Change Log:
 * - Added Sprint 2 Functionality
 * - Refactored Project Architecture and classes
 */
public class SetupForm {

    private static final Dimension SIZE_SMALL = new Dimension(650, 150);
    private static final Dimension SIZE_LARGE = new Dimension(650, 450);
    private static final String TEST_LOCKED_TEXT = "Editing for this test has been disabled because\nat least one user has completed this test.";
    private static final String DELETE_CONFIRM_TEXT = "Are you sure you want to delete this test?";
    private static final String DELETE_LOCKED_CONFIRM_TEXT = "This test has been been completed by\nat least one user. Deleting it will also\ndelete all test results for this test.\nAre you sure you want to delete this test?";
    private static final String DELETE_CONFIRM_TITLE= "Delete Test?";
    private static final String TEST_LOCKED_TITLE = "Locked Test";
    private static final String CANCEL_BUTTON_BACK = "<< Select another test";
    private static final String CANCEL_BUTTON_CANCEL = "Cancel Changes";
    private static final String CANCEL_CONFIRM_TEXT = "Cancel your changes and return\nto test selection screen?";
    private static final String CANCEL_CONFIRM_TITLE = "Cancel Changes";
    private static final String TEST_SELECTION_TITLE = "Comparison Test Setup";
    private static final String TEST_EDIT_TITLE = " Test Setup";
    private static final String BLANK_ITEM_WARNING = "Add Item field cannot be blank";
    private static final String ITEM_DUPLICATE_WARNING = "already exists in Test list";
    private static final String ITEM_COUNT_TEXT = "Number of Items: ";
    private static final String FIND_IMAGES_URL = "https://www.google.com/search?&tbm=isch&tbs=isz:lt,islt:qsvga,ic:trans&q=";
    private static final String NEW_TEST_TEXT = "Please enter a new test name.";
    private static final String NEW_TEST_TITLE = "Create New Test";
    private static final String NEW_TEST_BLANK_WARNING = "Test name cannot be left blank.\n";
    private static final String NEW_TEST_DUPLICATE_WARNING = "\" already exists.\n";

    private final TestSetup testSetup;
    private final JFrame frame;

    private JPanel rootPanel;
    // Company Name and Window Description
    private JLabel businessLabel;
    private JLabel descriptionLabel;
    // Add Item
    private JTextField addItemField;
    private JButton addItemButton;
    private JList<Item> itemList;
    private DefaultListModel<Item> itemListModel;
    private JButton removeItemButton;
    private JLabel itemCountLabel;
    // Navigation Buttons
    private JButton cancelButton;
    private JButton deleteButton;
    private JLabel warningLabel;
    private JComboBox<Test> testSelectionComboBox;
    private JButton createTestButton;
    private JButton findImagesButton;
    private JButton removeImageButton;
    private JPanel imagePlaceholderPanel;
    private DragAndDropPicturePanel picturePanel;
    private JButton customizeButton;
    private JPanel testEditPanel;
    private JPanel testSelectionPanel;
    // Constants

    /**
     * Constructor for SetupForm Class
     *
     * @param testSetup stores the business logic for AdminSetup Package
     * @param frame JFrame containing SetupForm GUI
     */
    public SetupForm(TestSetup testSetup, JFrame frame) {

        this.testSetup = testSetup;
        this.frame = frame;
        rootPanel.setPreferredSize(SIZE_SMALL);
        frame.setTitle(TEST_SELECTION_TITLE);

        businessLabel.setText(BUSINESS_NAME);
        testEditPanel.setVisible(false);
        warningLabel.setIcon(new ImageIcon(getClass().getResource("/Resources/warning.gif")));
        warningLabel.setVisible(false);
        itemListModel = new DefaultListModel<>();
        updateTestList();
        picturePanel.setColor(frame.getBackground());

        // Create Action Listeners

        ActionListener selectTest = e -> {
            if (testSelectionComboBox.getSelectedItem() != null && testSelectionComboBox.getSelectedIndex() != 0) {
                testSetup.setTest((Test) testSelectionComboBox.getSelectedItem());
                itemList.clearSelection();
                updateItemList();
                toggleFrame();
                validateTest(testSetup.findSessions());
                picturePanel.setDimensions(imagePlaceholderPanel.getSize());
                deleteButton.setEnabled(false);
            }
        };
        ActionListener createTest = e -> {
            String newTestName;
            String errorText = "";
            boolean validTestName;
            do {
                validTestName = true;
                newTestName = JOptionPane.showInputDialog(null, errorText + NEW_TEST_TEXT, NEW_TEST_TITLE,
                        errorText.isEmpty() ? JOptionPane.QUESTION_MESSAGE : JOptionPane.ERROR_MESSAGE);
                if (newTestName.isEmpty()) {
                    errorText = NEW_TEST_BLANK_WARNING;
                    validTestName = false;
                } else {
                    for (Test test : testSetup.getTests()) {
                        if (newTestName.toLowerCase().equals(test.getName().toLowerCase())) {
                            validTestName = false;
                            errorText = "Test \"" + newTestName.toUpperCase() + NEW_TEST_DUPLICATE_WARNING;
                        }

                    }
                }
            } while(!validTestName);
            testSetup.setTest(new Test(-1, newTestName, null));
            toggleFrame();
            validateTest(false);
        };
        ActionListener addItem = e -> {
            if (!displayWarning(addItemField.getText().trim())) {
                testSetup.getTest().addItem(new Item(-1, testSetup.getTest().getID(), addItemField.getText().trim(), null));
                picturePanel.setImage(null);
                updateItemList();
            }
            addItemField.setText("");
            addItemField.grabFocus();
        };
        ActionListener removeItem = e -> {
            if (!itemList.isSelectionEmpty()) {
                int index = itemList.getSelectedIndex();
                testSetup.getTest().removeItem(index);
                updateItemList();
            }
        };
        ListSelectionListener selectItem = e -> {
            picturePanel.setVisible(false);
            if(!e.getValueIsAdjusting()) {
                if (itemList.isSelectionEmpty() || itemList.getSelectedValue().getImage() == null) {
                    picturePanel.setImage(null);
                    picturePanel.setVisible(true);
                    removeImageButton.setEnabled(false);
                } else {
                    picturePanel.setImage(itemList.getSelectedValue().getImage());
                    picturePanel.setVisible(true);
                    if (testSetup.findSessions()){
                        removeImageButton.setEnabled(false);
                    } else {
                        removeImageButton.setEnabled(true);
                    }
                }
            }
        };
        PropertyChangeListener addImage = e -> {
            itemList.getSelectedValue().setImage(picturePanel.getImage());
            removeImageButton.setEnabled(true);
        };
        ActionListener findImages = e -> {
            imageSearch(FIND_IMAGES_URL + itemList.getSelectedValue().getName());
        };
        ActionListener removeImage = e -> {
            itemList.getSelectedValue().setImage(null);
            picturePanel.setImage(null);
            removeImageButton.setEnabled(false);
        };
        ActionListener cancel = e -> {
            if(confirmDecision(e.getActionCommand()) == 0) {
                toggleFrame();
            }
        };
        ActionListener delete = e -> {
            if(testSetup.getTest().getID() == -1) {
                toggleFrame();

            } else {
                if(confirmDecision("Delete") == 0) {
                    testSetup.deleteTest(testSetup.findSessions());
                    updateTestList();
                    toggleFrame();
                }
            }
        };
        ActionListener customizeTest = e -> changeSetupGUI(testSetup, this);

        // Add Action Listeners
        testSelectionComboBox.addActionListener(selectTest);
        createTestButton.addActionListener(createTest);
        addItemField.addActionListener(addItem);
        addItemButton.addActionListener(addItem);
        removeItemButton.addActionListener(removeItem);
        itemList.addListSelectionListener (selectItem);
        picturePanel.addPropertyChangeListener(addImage);
        customizeButton.addActionListener(customizeTest);
        findImagesButton.addActionListener(findImages);
        removeImageButton.addActionListener(removeImage);
        cancelButton.addActionListener(cancel);
        deleteButton.addActionListener(delete);
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

    /**
     * Checks whether a warning needs to be displayed and if so, displays the warning.
     *
     * @param input text currently in the addItemField
     * @return boolean warning
     */
    private boolean displayWarning(String input) {
        boolean warning;
        if(input.isEmpty()) {
            warningLabel.setText(BLANK_ITEM_WARNING);
            warning = true;
        } else {
            boolean duplicate = false;
            for (Item item : testSetup.getTest().getItems()) {
                if (item.getName().toLowerCase().equals(input.toLowerCase())) {
                    duplicate = true;
                }
            }
            if(duplicate) {
                warningLabel.setText("\"" + input.toUpperCase() + "\"" + ITEM_DUPLICATE_WARNING);
                warning = true;
            } else {
                warningLabel.setText("");
                warning = false;
            }
        }
        warningLabel.setVisible(warning);
        return warning;
    }

    private void validateTest(boolean lockedTest) {
        warningLabel.setVisible(false);
        int itemCount = itemList.getModel().getSize();
        if(itemCount < 1) {
            addItemField.setEnabled(true);
            addItemButton.setEnabled(true);
            removeItemButton.setEnabled(false);
            findImagesButton.setEnabled(false);
            removeImageButton.setEnabled(false);
            customizeButton.setEnabled(false);
        } else if(itemCount == 1) {
            addItemField.setEnabled(true);
            addItemButton.setEnabled(true);
            removeItemButton.setEnabled(true);
            findImagesButton.setEnabled(true);
            customizeButton.setEnabled(false);
        } else {
            addItemField.setEnabled(true);
            addItemButton.setEnabled(true);
            removeItemButton.setEnabled(true);
            findImagesButton.setEnabled(true);
            customizeButton.setEnabled(true);
        }
        if(lockedTest) {
            addItemField.setEnabled(false);
            addItemButton.setEnabled(false);
            removeItemButton.setEnabled(false);
            findImagesButton.setEnabled(false);
            removeImageButton.setEnabled(false);
            cancelButton.setText(CANCEL_BUTTON_BACK);
            cancelButton.setActionCommand("Back");
            customizeButton.setEnabled(false);
            JOptionPane.showMessageDialog(null, TEST_LOCKED_TEXT, TEST_LOCKED_TITLE, JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Update itemList to display appropriate Items
     */
    private void updateItemList() {
        itemListModel = new DefaultListModel<>();
        if(testSetup.getTest().getItems() != null) {
            for(Item item : testSetup.getTest().getItems()) {
                itemListModel.addElement(item);
            }
        } else {
            System.out.println(testSetup.getTest().getName() + " test has no Items");
        }
        itemList.setModel(itemListModel);
        itemCountLabel.setText(ITEM_COUNT_TEXT + testSetup.getTest().getItems().size());
        picturePanel.setVisible(false);
        validateTest(false);
    }

    /**
     * Update testList to display appropriate Tests
     */
    private void updateTestList() {
        testSelectionComboBox.removeAllItems();
        testSelectionComboBox.addItem(new Test(-1, null, null));
        testSetup.setTests();
        for(Test test : testSetup.getTests()) {
            testSelectionComboBox.addItem(test);
        }
        int itemCount = testSelectionComboBox.getItemCount();
        testSelectionComboBox.setMaximumRowCount(itemCount >= 15 ? 15 : itemCount);
    }

    private static void imageSearch(String urlString) {
        try {
            Desktop.getDesktop().browse(new URL(urlString.replace(" ", "%20")).toURI());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int confirmDecision(String button) {
        String text;
        String title;
        switch(button) {
            case "Cancel":
                text = CANCEL_CONFIRM_TEXT;
                title = CANCEL_CONFIRM_TITLE;
                break;
            case "Delete":
                text = testSetup.findSessions() ? DELETE_LOCKED_CONFIRM_TEXT: DELETE_CONFIRM_TEXT;
                title = DELETE_CONFIRM_TITLE;
                break;
            default:
                return 0;
        }
        return JOptionPane.showOptionDialog(null,
                text, title, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);
    }

    private void toggleFrame() {
        if(frame.getTitle().equals(TEST_SELECTION_TITLE)) {
            frame.setTitle(testSetup.getTest().getName() + TEST_EDIT_TITLE);
            descriptionLabel.setText(testSetup.getTest().getName() + TEST_EDIT_TITLE);
            testSelectionPanel.setVisible(false);
            testEditPanel.setVisible(true);
            cancelButton.setText(CANCEL_BUTTON_CANCEL);
            cancelButton.setActionCommand("Cancel");
            updateItemList();
            addItemField.grabFocus();
            rootPanel.setPreferredSize(SIZE_LARGE);
        } else {
            frame.setTitle(TEST_SELECTION_TITLE);
            descriptionLabel.setText(TEST_SELECTION_TITLE);
            testSelectionPanel.setVisible(true);
            testSelectionComboBox.setSelectedIndex(0);
            testEditPanel.setVisible(false);
            rootPanel.setPreferredSize(SIZE_SMALL);
        }
        frame.validate();
        frame.repaint();
        frame.pack();
    }
}
