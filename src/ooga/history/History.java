package ooga.history;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class History {

    private List<Move> moveHistory = new ArrayList<>();
    private int movePointer = -1;

    public void addMove(Move m) {
        int lastMoveIndex = moveHistory.size() - 1;
        while (movePointer < lastMoveIndex) {
            moveHistory.remove(lastMoveIndex);
            lastMoveIndex--;
        }
        movePointer = lastMoveIndex + 1;
        moveHistory.add(m);
    }

    public Move undo() throws IndexOutOfBoundsException {
        if (movePointer < 0) throw new IndexOutOfBoundsException();
        return moveHistory.get(movePointer--);
    }

    public Move redo() throws IndexOutOfBoundsException {
        if (movePointer >= moveHistory.size() - 1) throw new IndexOutOfBoundsException();
        return moveHistory.get(++movePointer);
    }

    public boolean isUndoDisabled() {
        return movePointer < 0;
    }

    public boolean isRedoDisabled() {
        return movePointer >= moveHistory.size() - 1;
    }
}
