package logic.results;

import database.*;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Matrix
 * <p>
 * Logic for building a Matrix Table of results
 *
 * @author Leron Tolmachev, Christopher Manuel
 * @version 2017.12.06
 * <p>
 * Change Log:
 * - Refactored from Score and Scoring classes [Initial version]
 * - Reordered resultsModel (moved scores to end of results table)
 */
public class Results {
    private ArrayList<Item> items;
    private final ArrayList<MatchUp> matchUps;

    /**
     * Constructor for Results Class
     *
     * @param session Test Session which contains results
     */
    public Results(Session session) {
        Database db = new Database();
        this.items = db.getItemsByTestID(session.getTestID());
        this.matchUps = db.getMatchUps(session.getSessionID());
        db.closeConnection();
        this.items = scoreItems();
    }

    public String[] getColumnNames() {
        String[] columnNames = new String[items.size() + 1];
        for (int i = 0; i < items.size(); i++) {
            columnNames[0] = "";
            columnNames[i + 1] = items.get(i).getName();
        }
        return columnNames;
    }

    /**
     * Access Results Model created for building Matrix Table
     *
     * @return Object[][] resultsModel
     */
    public Object[][] getMatrixModel() {
        Object[][] matrixModel = new Object[items.size()][items.size() + 1];
        for (int row = 0; row < items.size(); row++) {
            Item rowItem = items.get(row);
            for (int col = 0; col < items.size(); col++) {
                Item colItem = items.get(col);
                matrixModel[row][0] = rowItem.getName();
                String winner = "";
                for (MatchUp matchUp : matchUps) {
                    if ((rowItem.getName().equals(matchUp.getItemA().getName()) &&
                            colItem.getName().equals(matchUp.getItemB().getName())) ||
                            (rowItem.getName().equals(matchUp.getItemB().getName()) &&
                                    colItem.getName().equals(matchUp.getItemA().getName()))) {
                        switch (matchUp.getDecision()) {
                            case "A":
                                winner = matchUp.getItemA().getName();
                                break;
                            case "B":
                                winner = matchUp.getItemB().getName();
                                break;
                            case "":
                                winner = "TIE";
                                break;
                            default:
                                System.out.println("ERROR WITH MATRIX CREATION?");
                                break;
                        }
                    }
                }
                matrixModel[row][col + 1] = winner;
            }
        }
        return matrixModel;
    }

    /**
     * Access Results Model created for building Results Table
     *
     * @return Object[][] resultsModel
     */
    public Object[][] getResultsModel() {
        Object[][] resultsModel = new Object[items.size()][6];
        for (int i = 0; i < items.size(); i++) {
            resultsModel[i][0] = i + 1;
            resultsModel[i][1] = items.get(i).getName();
            resultsModel[i][2] = items.get(i).getWins();
            resultsModel[i][3] = items.get(i).getLosses();
            resultsModel[i][4] = items.get(i).getTies();
            resultsModel[i][5] = items.get(i).getScore();
        }
        return resultsModel;
    }

    /**
     * Sets Score values for each Object
     *
     * @return ArrayList items
     */
    private ArrayList<Item> scoreItems() {
        for (Item score : items) {
            int id = score.getID();
            for (MatchUp matchUp : matchUps) {
                int aID = matchUp.getItemA().getID();
                int bID = matchUp.getItemB().getID();
                if (id == aID && matchUp.getDecision().equals("A")) {
                    score.setWins(score.getWins() + 1);
                } else if (id == bID && matchUp.getDecision().equals("B")) {
                    score.setWins(score.getWins() + 1);
                } else if (id == aID && matchUp.getDecision().equals("B")) {
                    score.setLosses(score.getLosses() + 1);
                } else if (id == bID && matchUp.getDecision().equals("A")) {
                    score.setLosses(score.getLosses() + 1);
                } else if ((id == aID || id == bID) && matchUp.getDecision().equals("")) {
                    score.setTies(score.getTies() + 1);
                }
            }
            score.setScore(score.getWins() - score.getLosses());
        }
        Collections.sort(items);
        return items;
    }
}
