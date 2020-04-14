package ooga.history;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class History {

    private List<Move> moveHistory = new ArrayList<>();
    private int movePointer = -1;
    private boolean undoDisabled;
    private boolean redoDisabled;

    public void addMove(Move m) {
        int lastMoveIndex = moveHistory.size() - 1;
        while (movePointer < lastMoveIndex) {
            moveHistory.remove(lastMoveIndex);
            lastMoveIndex--;
        }
        movePointer = lastMoveIndex + 1;
        moveHistory.add(m);
        disableUndoOrRedo();
    }

    public Move undo() throws IndexOutOfBoundsException {
        if (movePointer < 0) throw new IndexOutOfBoundsException();
        disableUndoOrRedo();
        return moveHistory.get(movePointer--);
    }

    public Move redo() throws IndexOutOfBoundsException {
        if (movePointer >= moveHistory.size() - 1) throw new IndexOutOfBoundsException();
        disableUndoOrRedo();
        return moveHistory.get(++movePointer);
    }

    private void disableUndoOrRedo() {
        undoDisabled = movePointer <= 0;
        redoDisabled = movePointer >= moveHistory.size() - 1;
    }
}
