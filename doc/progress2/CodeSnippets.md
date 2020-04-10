Key code snippets from our Sprint 1 progress:

Controller.java
```java
private void setListeners(){
        myBoardView.setOnPieceClicked((int x, int y) -> {
            myBoardView.setSelectedLocation(x, y);
            myBoardView.highlightValidMoves(myBoard.getValidMoves(x, y));
        });

        myBoardView.setOnMoveClicked((int x, int y) -> {
            myBoard.doMove((int) myBoardView.getSelectedLocation().getX(), (int) myBoardView.getSelectedLocation().getY(), x, y);
            myBoardView.movePiece(x, y);
            myBoard.checkWon();
        });
    }
```
The above code connects our frontend to the backend using the Board API to pass information from the backend to our frontend classes, which handle the input of certain cells in order to move pieces and highlight available moves as necessary.

CellClickedInterface.java
```java
@FunctionalInterface
public interface CellClickedInterface {

    void clickCell(int x, int y);
}
```

This interface allows for diverse implementations of on click events for clicking cells depending on the function we need.

ChessBoard.java
```java
@Override
  public List<Point2D> getValidMoves(int x, int y){
    Piece piece = getPieceAt(x, y);
    if(piece == null){
      return null;
    }
    String movePattern = piece.getMovePattern();
    String moveType = movePattern.split(" ")[0].toLowerCase();
    int moveDist = Integer.parseInt(movePattern.split(" ")[1]);
    try {
      Method methodToCall = this.getClass().getDeclaredMethod(moveType, int.class, int.class, int.class, piece.getClass());
      Object returnVal = methodToCall.invoke(this, x, y, moveDist, piece);
      return ((List<Point2D>)returnVal);
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      //e.printStackTrace();
      System.out.println("Error handling method " + moveType);
    }
    return null;
  }
```
This method returns a list of points that a given piece has the ability to move to. It uses reflection to call the appropriate method from a given cell. An example of these reflected methods is shown below:
```java
private List<Point2D> up(int x, int y, int dist, Piece piece){
    List<Point2D> ret = new ArrayList<>();
    int inc = 1;
    while(inc <= dist || dist < 0){
      int newX = x - inc;
      Point2D newPoint = findPoint(newX, y, piece);
      if(newPoint != null) {
        ret.add(newPoint);
        if(getPieceAt(newX, y) != null){
          break;
        }
      }
      else{
        break;
      }
      inc++;
    }
    return ret;
  }
```

StrategyAI.java
```java
public List<Integer> generateMove() {
    long startTime = System.currentTimeMillis();
    List<Integer> moveCoordinates;
    switch (myStrategy) {
      case TRIVIAL:
        moveCoordinates = generateTrivialMove();
      case RANDOM:
        moveCoordinates =  generateRandomMove();
      case BRUTE_FORCE:
        moveCoordinates = generateBruteForceMove();
      case ALPHA_BETA:
        moveCoordinates = generateAlphaBetaMove();
      default:
        moveCoordinates = generateTrivialMove();
    }
    long endTime = System.currentTimeMillis();
    moveTimes.add((double) startTime - endTime);
    return moveCoordinates;
  }

  public List<Integer> generateTrivialMove() {
    for (int i=0; i<myBoard.getWidth(); i++) {
      for (int j=0; j<myBoard.getHeight(); j++) {
        if (myBoard.getValidMoves(i, j) != null) {
          if (myBoard.getValidMoves(i, j).size() != 0) {
            Point2D moveTo = myBoard.getValidMoves(i, j).get(0);
            return Arrays.asList((int) moveTo.getX(), (int) moveTo.getY(), i, j);
          }
        }
      }
    }
    // TODO: replace with an exception.
    System.out.println("AI could not find a piece");
    return new ArrayList<>();
  }
```
The very basic AI algorithm we have for the CPU and an outline of our goal for AI implementation. If we are unable to quickly make meaningful progress on the AI we will begin looking into public APIs to accomplish this purpose.