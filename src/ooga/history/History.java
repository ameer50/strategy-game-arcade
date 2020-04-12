package ooga.history;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class History {

    private List<Move> moveHistory = new ArrayList<>();
    private int movePointer = -1;
    private boolean undoDisabled;
    private boolean redoDisabled;

    public void addNewMove(Move m) {
        int lastMoveIndex = moveHistory.size() - 1;
        while (movePointer < lastMoveIndex) {
            moveHistory.remove(lastMoveIndex);
            lastMoveIndex--;
        }
        movePointer = lastMoveIndex + 1;
        moveHistory.add(m);
        disableUndoOrRedo();
    }

    public void undo() throws IndexOutOfBoundsException {
        if (movePointer <= 0) throw new IndexOutOfBoundsException();
        movePointer--;
        disableUndoOrRedo();
    }

    public void redo() throws IndexOutOfBoundsException {
        if (movePointer <= 0) throw new IndexOutOfBoundsException();
        movePointer++;
        disableUndoOrRedo();
    }

    private void disableUndoOrRedo() {
        undoDisabled = movePointer <= 0;
        redoDisabled = movePointer >= moveHistory.size() - 1;
    }
}
