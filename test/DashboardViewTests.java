import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import ooga.history.History;
import ooga.history.Move;
import ooga.json.JSONProcessor;
import ooga.view.DashboardView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.DukeApplicationTest;

import java.awt.geom.Point2D;

import static org.junit.jupiter.api.Assertions.*;

public class DashboardViewTests extends DukeApplicationTest {

    private History history;
    private DashboardView dashboardView;
    private ObservableList<Move> historyList;
    private ListView<Move> historyDisplay;

    @BeforeEach
    public void setUp() {
        String gameJSON = String.format("resources/Chess/defaultWhite.json");
        JSONProcessor processor = new JSONProcessor();
        processor.parse(gameJSON);
        dashboardView = new DashboardView();

        history = new History();
        historyList = FXCollections.observableArrayList();
        historyDisplay = dashboardView.getHistoryDisplay();
        historyDisplay.setItems(historyList);
    }

    @Test
    public void testScoreBinding() {
        IntegerProperty playerOneScore = new SimpleIntegerProperty(5);
        IntegerProperty playerTwoScore = new SimpleIntegerProperty(10);
        dashboardView.bindScores(playerOneScore, playerTwoScore);

        assertEquals(playerOneScore.getValue(), Integer.parseInt(dashboardView.getPlayerOneScoreText().getText()));
        assertEquals(playerTwoScore.getValue(), Integer.parseInt(dashboardView.getPlayerTwoScoreText().getText()));
    }

    @Test
    public void testUndoRedoButtonsDisabled() {
        assertTrue(dashboardView.getUndoButton().isDisabled());
        assertTrue(dashboardView.getRedoButton().isDisabled());
    }

    @Test
    public void testHistoryDisplay() {
        Move m = new Move(new Point2D.Double(6, 7), new Point2D.Double(5, 7));
        history.addMove(m);
        historyList.add(m);
        dashboardView.setUndoRedoButtonsDisabled(history.isUndoDisabled(), history.isRedoDisabled());

        assertEquals(m.toString(), historyDisplay.getItems().get(0).toString());
    }

    @Test
    public void testUndo() {
        Move m = new Move(new Point2D.Double(6, 7), new Point2D.Double(5, 7));
        history.addMove(m);
        historyList.add(m);

        history.undo();
        historyList.remove(historyList.size() - 1);
        dashboardView.setUndoRedoButtonsDisabled(history.isUndoDisabled(), history.isRedoDisabled());

        assertTrue(dashboardView.getUndoButton().isDisabled());
        assertFalse(dashboardView.getRedoButton().isDisabled());
        assertEquals(0, historyDisplay.getItems().size());
    }

    @Test
    public void testRedo() {
        Move m = new Move(new Point2D.Double(6, 7), new Point2D.Double(5, 7));
        history.addMove(m);
        historyList.add(m);

        history.undo();
        historyList.remove(historyList.size() - 1);

        Move prevMove = history.redo();
        historyList.add(prevMove);
        dashboardView.setUndoRedoButtonsDisabled(history.isUndoDisabled(), history.isRedoDisabled());

        assertTrue(dashboardView.getRedoButton().isDisabled());
        assertFalse(dashboardView.getUndoButton().isDisabled());
        assertEquals(1, historyDisplay.getItems().size());
        assertEquals(prevMove.toString(), historyDisplay.getItems().get(0).toString());
    }

}
