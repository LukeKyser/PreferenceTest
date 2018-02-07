import gui.dev.DevMenuForm;

import javax.swing.*;
/**
 * UserLoginMain
 *
 * UserLoginMain Class, Currently launches a menu to select a module GUI.
 *
 * @author Leron Tolmachev
 * @version 2017.11.11
 *
 * Change Log:
 * - Refactored Project after Sprint One
 * - -
 */
class Main {

    /**
     * Main Method of Project
     *
     * @param args arguments passed into Main
     */
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(Main::createGUI);
    }

    /**
     * Creates application window containing root JPanel of Project
     */
    private static void createGUI() {
        JFrame frame = new JFrame("Dev Menu");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(new DevMenuForm().getRootPanel());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }


}
