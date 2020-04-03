// adding a move to history

public class History {
    private List<Move> historyOfMoves;

    public History () {
        historyOfMoves = new ArrayList<>();
    }

    public void addNewMove (Move m) {
        historyOfMoves.append(m);
    }
}

public class Move {

    private String initialPos;
    private String finalPos;

    public Move (String initialPos, String finalPos) {
        this.initialPos = initialPos;
        this.finalPos = finalPos;
    }

    public String toString () {
        return String.format("%s to %s", initialPos, finalPos);
    }
}