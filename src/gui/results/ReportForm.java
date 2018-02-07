package gui.results;

import database.*;
import logic.results.*;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import static resources.Constants.BUSINESS_NAME;

/**
 * ReportForm
 * <p>
 * GUI for account Results Reporting form.
 *
 * @author Leron Tolmachev, Christopher Manuel
 * @version 2017.12.08
 * <p>
 * Change Log:
 * - Implemented Sprint 2 Story features
 * - Refactored Code.
 * -
 */

public class ReportForm {

    private static final Dimension SIZE_DEFAULT = new Dimension(650, 130);

    private final JFrame frame;
    private final Report report;

    private JPanel rootPanel;
    // ComboBoxes
    private JComboBox<User> findUserComboBox;
    private JComboBox<Test> findTestComboBox;
    private JComboBox<Session> findSessionComboBox;
    // Tables
    private JTable rankingsTable;
    private JTable matrixTable;
    private JTabbedPane tabbedPane;
    private JScrollPane rankingsPane;
    private JScrollPane matrixPane;
    private JLabel businessLabel;
    // ActionListeners
    private final ActionListener selectObject= e -> {
        JComboBox comboBox = ((JComboBox) e.getSource());
        if (comboBox.getSelectedIndex() != 0) {
            makeSelection(comboBox.getActionCommand());
        } else {
            tabbedPane.setVisible(false);
            clearTable(rankingsTable);
            clearTable(matrixTable);
        }
    };
    /**
     * Constructor for ReportForm Class
     */
    public ReportForm(JFrame frame) {

        this.frame = frame;

        rootPanel.setPreferredSize(SIZE_DEFAULT); // default dimensions of panel
        businessLabel.setText(BUSINESS_NAME);

        this.report = new Report();

        // Prepare JComboBoxes

        UIManager.put("ComboBox.disabledForeground", Color.DARK_GRAY);
        tabbedPane.setVisible(false);

        findUserComboBox.setFont(new Font("Monospaced", Font.BOLD, 12));
        findUserComboBox.setActionCommand("User");
        findUserComboBox.addItem(new User(-1, null, null, null, null, null));
        for(User user : report.getUsers()) {
            findUserComboBox.addItem(user);
        }
        int itemCount = findUserComboBox.getItemCount();
        findUserComboBox.setMaximumRowCount(itemCount >= 15 ? 15 : itemCount);
        findTestComboBox.setFont(new Font("Monospaced", Font.BOLD, 12));
        findTestComboBox.setActionCommand("Test");
        findTestComboBox.setEnabled(false);

        findSessionComboBox.setFont(new Font("Monospaced", Font.BOLD, 12));
        findSessionComboBox.setActionCommand("Session");
        findSessionComboBox.setEnabled(false);
        findSessionComboBox.getColorModel();
        resetForm();

        // Add action listeners to Combo Boxes
        findSessionComboBox.addActionListener(selectObject);
        findTestComboBox.addActionListener(selectObject);
        findUserComboBox.addActionListener(selectObject);
    }

    /**
     * Access rootPanel field of ReportForm
     *
     * @return JPanel rootPanel
     */
    public JPanel getRootPanel() {
        return rootPanel;
    }

    /**
     * Builds a table to display Test Session Scores
     *
     * @param results Results calculated from the selected Session
     */
    private void buildRankingsTable(Results results) {
        Object[][] resultsModel = results.getResultsModel();
        TableModel dataModel = new
                AbstractTableModel() {
                    private final String[] columnNames = {"Rank", "Item", "Wins", "Losses", "Ties", "Score"};

                    @Override
                    public int getRowCount() {
                        return resultsModel.length;
                    }

                    @Override
                    public int getColumnCount() {
                        return columnNames.length;
                    }

                    @Override
                    public String getColumnName(int col) {
                        return columnNames[col];
                    }

                    @Override
                    public Object getValueAt(int row, int col) {
                        return resultsModel[row][col];
                    }
                };
        rankingsTable.getTableHeader().setReorderingAllowed(false);
        rankingsTable.setModel(dataModel);
        rankingsTable.setPreferredScrollableViewportSize(new Dimension(300, findTableHeight(rankingsTable)));
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( JLabel.CENTER );
        rankingsTable.setDefaultRenderer(String.class, centerRenderer);
        for(int i= 0; i < dataModel.getColumnCount() ; i++){
            rankingsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    /**
     * Builds a 2D Matrix to display Test Session MatchUp Decisions
     *
     * @param results Results calculated from the selected Session
     */
    private void buildMatrixTable(Results results) {
        Object[][] matrixModel = results.getMatrixModel();
        TableModel dataModel = new
                AbstractTableModel() {
                    private final String[] columnNames = results.getColumnNames();

                    @Override
                    public int getRowCount() {
                        return matrixModel.length;
                    }

                    @Override
                    public int getColumnCount() {
                        return columnNames.length;
                    }

                    @Override
                    public String getColumnName(int col) {
                        return columnNames[col];
                    }

                    @Override
                    public Object getValueAt(int row, int col) {
                        return matrixModel[row][col];
                    }
                };
        matrixTable.setModel(dataModel);
        matrixTable.getTableHeader().setReorderingAllowed(false);
        matrixTable.setCellSelectionEnabled(true);
        matrixTable.setPreferredScrollableViewportSize(new Dimension(620, findTableHeight(matrixTable)));
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( JLabel.CENTER );
        for(int i= 0; i < dataModel.getColumnCount() ; i++){
            matrixTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private int findTableHeight(JTable table) {
        return table.getRowHeight() * table.getModel().getRowCount();
    }

    /**
     * Clear contents of a JTable
     */
    private void clearTable(JTable table) {
        table.setModel(new AbstractTableModel() {
            private final String[] columnNames = {null};

            @Override
            public int getRowCount() {
                return 0;
            }

            @Override
            public int getColumnCount() {
                return 0;
            }

            @Override
            public String getColumnName(int col) {
                return columnNames[col];
            }

            @Override
            public Object getValueAt(int row, int col) {
                return null;
            }
        });
    }

    /**
     * Displays User Information and timestamp associated with a Test Session
     *
     * @param session Test Session completed by User
     */
    private void generateData(Session session) {
        Results results = new Results(session);
        buildRankingsTable(results);
        buildMatrixTable(results);
        tabbedPane.setVisible(true);
        tabbedPane.validate();
        rootPanel.setPreferredSize(new Dimension(620, 180 + findTableHeight(rankingsTable)));
        frame.validate();
        frame.repaint();
        frame.pack();
    }

    /**
     * Programatically populate JComboBoxes in a hierarchical order
     *
     * @param comboBoxType String indicating which JComboBox was selected
     */
    private void makeSelection(String comboBoxType) {
        switch(comboBoxType) {
            case "User":
                report.setSession(null);
                findTestComboBox.removeActionListener(selectObject);
                findTestComboBox.removeAllItems();
                findTestComboBox.addActionListener(selectObject);
                findSessionComboBox.removeActionListener(selectObject);
                findSessionComboBox.removeAllItems();
                findSessionComboBox.addActionListener(selectObject);
                report.generateSessions((User) findUserComboBox.getSelectedItem());
                ArrayList<Test> tests = report.getTests();
                findTestComboBox.addItem(new Test(-1, null, null));
                for (Test test : tests) {
                    findTestComboBox.addItem(test);
                }
                setComboBox(findTestComboBox, tests);
                break;
            case "Test":

                assert findTestComboBox.getSelectedItem() != null;
                ArrayList<Session> sessions = report.getSessions(((Test) findTestComboBox.getSelectedItem()).getName());
                findSessionComboBox.removeActionListener(selectObject);
                findSessionComboBox.removeAllItems();
                findSessionComboBox.addActionListener(selectObject);
                findSessionComboBox.addItem(new Session(-1,-1,-1, null));
                for(Session session : sessions) {
                    findSessionComboBox.addItem(session);
                }
                setComboBox(findSessionComboBox, sessions);
                break;
            case "Session":
                report.setSession((Session) findSessionComboBox.getSelectedItem());
                generateData(report.getSession());
                break;
            default:
                System.out.print("!!!!!! Error when selecting from a JComboBox !!!!!");
                break;
        }
    }
    private void setComboBox(JComboBox comboBox, ArrayList list) {
        if(list.size() == 1) {
            comboBox.setSelectedIndex(1);
            comboBox.setEnabled(false);
        } else {
            comboBox.setSelectedIndex(0);
            comboBox.setEnabled(true);
            int itemCount = comboBox.getItemCount();
            comboBox.setMaximumRowCount(itemCount >= 15 ? 15 : itemCount);
        }
    }


    /**
     * Clears contents of Form elements
     */
    private void resetForm() {
        report.setSession(null);
        findTestComboBox.setEnabled(false);
        findSessionComboBox.setEnabled(false);
        tabbedPane.setVisible(false);
        clearTable(rankingsTable);
        clearTable(matrixTable);
    }
}

