## API_CHANGES.md

#### Frontend API

Most of our frontend API changed over the course of the project, making this the document to constult to see the frontend API. The '+' icon means that a part of the API was added and a crossthrough means that a part of the API was removed. An arrow '->' icon means that the seft side has been converted to the right side. For both the frontend and the backend, the method names are self-explanatory -- due to this, document just serves as a list representing the methods of the API.

~~PieceInterface~~:
(Removed the piece interface since all Piece functionality is identical -- we didn't need a separate API to manage them.)

**GameView -> GameScreen**:
* ~~DisplayWinner~~
* \+ public BoardView getBoardView()
* \+ public DashboardView getDashboardView()
* \+ public void toggleGameDarkMode()
* \+ public void enableGameCSS(String cssStyle)

**PieceView**:
* ~~MoveAnimation~~
* ~~KilledAnimation~~
* \+ public String getColor()
* \+ public ImageView getImage()
* \+ public String getPieceName()

~~CellView:~~
* ~~lightUpCell~~

~~MoveDisplayer~~

**\+ MenuScreen**:
* public void setGameButtonListener(EventHandler\<ActionEvent\> e)
* public String getGameChoice()
* public String getFileChoice()
* public boolean getIsGameOnePlayer()
* public String getPlayerOneColor()
* public String getPlayerTwoColor()
* public String getPlayerOneName()
* public String getPlayerTwoName()
* public void toggleMenuDarkMode()
* public boolean isDarkMode()
* public String getStrategyType()

**\+ BoardView**:
* public void initialize()
* public void createBoardColors()
* public List\<CellView\> getIcons()
* public void arrangePlayerIcons(String icon, String playerOneColor, String playerTwoColor)
* public StackPane\[\] getCells()
* public void highlightValidMoves(List\<Point2D\> validMoves)
* public void doMove(Move m)
* public void replenishIcon(Move m)
* public void setOnPieceClicked(ProcessCoordinateInterface function)
* public void setOnMoveClicked(ProcessCoordinateInterface function)
* public void setSelectedLocation(Point2D coordinate)
* public Point2D getSelectedLocation()
* public CellView getCellAt(Point2D location)

**\+ CellView**: 
* public void setPieceView(PieceView pieceView)
* public PieceView getPieceView()
* public void toggleYellow()
* public void toggleRed()
* public void toggleNoBorder()
* public void setOnClickFunction()
* public void setPieceClicked(ProcessCoordinateInterface clicked)
* public void setMoveClicked(ProcessCoordinateInterface clicked)
* public void setNoBorderFunction(ProcessCoordinateInterface clicked)

#### Backend API

**Board (Abstract Class)**
* ~~initStartingPosition()~~
* public void checkWon()
* \+ public boolean isGameOver()
* public List checkValidMoves(int, int) -> public List getValidMoves(Point2D coordinate)
* getPieceAt(int, int) -> getPieceAt(Point2D point)
* \+ public void putPieceAt(Point2D location, Piece piece)
* doMove(index, index, String) -> public void doMove(Move move)
* isValidCell(index, index) -> public boolean isCellInBounds(Point2D location)
* \+ public List\<Point2D\> getPointsOfColor(String color)
* \+ public void print()
* public int getHeight()
* public int getWidth()
* \+ public int getScore(String color)
* \+ public List\<Move\> getPossibleMoves(String color)
* \+ public Board getCopy()
* \+ public void setOnPiecePromoted(ProcessCoordinateInterface promoteAction)
* \+ public void addPlayerIcons(String playerOneColor, String playerTwoColor)
* \+ public BiMap<Point2D, Piece> getPieceBiMap()
* \+ public Map<String, String> getPieceMovePatterns()
* \+ public Map<String, Integer> getPieceScores()

~~**CellInterface**:~~
* ~~getState():~~

**PieceInterface**:

*  public String getMovePattern()
*  getScore() -> public int getValue()
*  public String getName()
*  public String getColor()
*  \+ public String getType()
*  public List\<Point2D\> getDisplacements()
* ~~isActive()~~
* ~~changeActive()~~
* \+ public void setType(String type)
* \+ setColor(String color)
* \+ setValue(int value)
* \+ setMovePattern(String pattern)

**PlayerInterface**
This class was changed to support a standard player class and a CPU player class, as opposed to just one sort of player that could choose a move by calling generateMove().
* \+ public abstract boolean isCPU()
* \+ public void doMove(Move move)
* \+ public void addToScore(int amount)
* \+ public IntegerProperty getScore()
* \+ public String getName()
* \+ public String getColor()
* \+ public Board getBoard()