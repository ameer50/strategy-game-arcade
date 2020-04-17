Key code snippets from our Sprint 2 progress:

Controller.java
```java
gameScreen.getDashboardView().setUndoMoveClicked((e) -> {
    Move prevMove = history.undo();
    historyList.remove(historyList.size() - 1);
    Move reverseMove = prevMove.getReverseMove();
    reverseMove.setUndo(true);
    if (prevMove.isPromote()) {
        reverseMove.setPromote(true);
    }

    doMove(reverseMove);
    toggleActivePlayer();

    Map<Piece, Point2D> map = prevMove.getCapturedPiecesAndLocations();
    for (Piece capturedPiece: map.keySet()) {
        Point2D capturedPieceLocation = map.get(capturedPiece);
        board.putPieceAt(capturedPieceLocation, capturedPiece);
        activePlayer.addToScore(-capturedPiece.getValue());
        PieceView capturedPieceView = new PieceView(capturedPiece.getFullName());
        boardView.getCellAt(capturedPieceLocation).setPiece(capturedPieceView);
    }
    board.print();
});

gameScreen.getDashboardView().setRedoMoveClicked((e) -> {
    Move prevMove = history.redo();
    historyList.add(prevMove);

    doMove(prevMove);
    toggleActivePlayer();
});

board.setOnPieceCaptured((int toX, int toY) -> {
    boardView.getCellAt(toX, toY).setPiece(null);
});

board.setOnPiecePromoted((int toX, int toY) -> {
    //board.getPieceAt(toX, toY);
    boardView.getCellAt(toX, toY).setPiece(new PieceView(board.getPieceAt(toX, toY).getFullName()));
});
```

Move.java
```java
public class Move {

    private Piece piece;
    private Point2D startLocation;
    private Point2D endLocation;
    private Map<Piece, Point2D> capturedPiecesAndLocations;
    private boolean isUndo;
    private boolean isPromote;

    public Move(Point2D startLocation, Point2D endLocation) {
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        capturedPiecesAndLocations = new HashMap<>();
        isUndo = false;
        isPromote = false;
    }

    public Move getReverseMove() {
        return new Move(endLocation, startLocation);
    }

    @Override
    public String toString() {
        return String.format("%s from (%d, %d) to (%d, %d)", piece, (int) startLocation.getX(), (int) startLocation.getY(), (int) endLocation.getX(), (int) endLocation.getY());
    }
}
```

ChessBoard.java
```java
    @Override
    public List<Point2D> getValidMoves(int i, int j) {
        Piece piece = getPieceAt(i, j);
        if (piece == null) {
          return null;
        }
        List<Point2D> thisPieceValidMoves = getValidMovesIgnoreCheck(i, j);
        String color = piece.getColor();
        Point2D kingPoint = locateKings(color);
        int kingI = (int) kingPoint.getX();
        int kingJ = (int) kingPoint.getY();
        Pair<List<Point2D>, List<Point2D>> checks = getMovesAndCheckPieces(kingI, kingJ, color, true);
        List<Point2D> checkPieces = checks.getValue();
        if (piece.getType().equals(KING)) {
          List<Point2D> safeKings = getSafeKingMoves(thisPieceValidMoves, checks.getKey());
          return checkDanger(safeKings, kingI, kingJ);
        }
        if (checkPieces.size() == 0){
          return getValidMovesIgnoreCheck(i, j);
        }
    
        if (checkPieces.size() > 1) {
          return null;
        }
    
        Point2D threatLoc = checkPieces.get(0);
        List<Point2D> threatPath = getPath((int) threatLoc.getX(), (int) threatLoc.getY(), kingI, kingJ);
        threatPath.add(threatLoc);
        thisPieceValidMoves.retainAll(threatPath);
    
        return thisPieceValidMoves;
    }

    @Override
    public void doMove(Move m) {
        int startX = (int) m.getStartLocation().getX();
        int startY = (int) m.getStartLocation().getY();
        int endX = (int) m.getEndLocation().getX();
        int endY = (int) m.getEndLocation().getY();
        Piece currPiece = getPieceAt(startX, startY);
        Piece hitPiece = getPieceAt(endX, endY);
        if(!m.isUndo()) {
          currPiece.move();
        } else {
          currPiece.unMove();
        }
        int score = 0;
        if (hitPiece != null) {
          score = hitPiece.getValue();
          pieceBiMap.remove(hitPiece); // ***
        }
        pieceBiMap.forcePut(new Point2D.Double(endX, endY), currPiece);
    
        m.setPiece(currPiece);
        if(hitPiece != null) {
          this.captureAction.process(endX, endY);
          m.addCapturedPieceAndLocation(hitPiece, m.getEndLocation());
        }
        // if undo and it was a promote move before
        if (m.isPromote() && m.isUndo()) {
          // demote piece in backend
          m.getPiece().setType(PAWN);
          m.getPiece().setMovePattern("PAWN -1");
          // demote piece in frontend
          this.promoteAction.process((int) m.getEndLocation().getX(), (int) m.getEndLocation().getY());
        }
        promote(m);
        //return score;
    }
```

MoveNodeAnd.java
```java
public class MoveNodeAnd extends MoveNode {
    
    public MoveNodeAnd(List<MoveNode> children) {
    // TODO: Throw an error if 'children' has less than two nodes.
        super(children);
    }
    public MoveNodeAnd(MoveNode a, MoveNode b) {
        super(List.of(a, b));
    }
    
    @Override
    public List<Point2D> generatePoints() {
        MoveNode a = children().get(0);
        MoveNode b = children().get(1);
        return concatenate(a, b);
    }
    
    protected List<Point2D> concatenate(MoveNode a, MoveNode b) {
        if ((a.size()==0) & (b.size()==0)) {
            Point2D sum = pointSum(a.getValue(), b.getValue());
            return List.of(sum);
        }
        List<Point2D> pointsB = b.generatePoints();
        List<Point2D> pointsA = a.generatePoints();
        System.out.println(pointsA);
        System.out.println(pointsB);
        return concatPointLists(pointsA, pointsB);
        }
    
    private List<Point2D> concatPointLists(List<Point2D> listA, List<Point2D> listB) {
        List<Point2D> pointList = new ArrayList();
        for (Point2D pointA: listA) {
            for (Point2D pointB: listB) {
                pointList.add(pointSum(pointA, pointB));
            }
        }
        return pointList;
    }
}
```