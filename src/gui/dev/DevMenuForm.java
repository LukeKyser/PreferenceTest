package gui.dev;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static gui.MainGUI.*;

/**
 * DevMenuForm
 *
 * A GUI form which allows for selecting a specific module GUI to load.
 * Currently only supports AdminSetup and Test.
 *
 * @author Leron Tolmachev
 * @version 2017.11.11
 *
 * Change Log:
 * - Refactored Project after Sprint One.
 * - Updated with correct button destinations for all modules
 * -
 */
public class DevMenuForm {
    private JPanel rootPanel;
    private JButton adminSetupButton;
    private JButton userLoginButton;
    private JButton testButton;
    private JButton resultReportingButton;

    /**
     * Constructor for the DevMenuForm Class
     */
    public DevMenuForm() {
        rootPanel.setPreferredSize(new Dimension(300,200));
        adminSetupButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                createSetupGUI();
            }
        });
        userLoginButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                createLoginGUI();
            }
        });
        testButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                createTestGUI(1011);
            }
        });
        resultReportingButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                createReportGUI();
            }
        });
    }

    /**
     * Access DevMenuForm's rootPanel field
     *
     * @return JPanel rootPanel
     */
    public JPanel getRootPanel() {
        return rootPanel;
    }

}
