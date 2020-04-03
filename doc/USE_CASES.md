# USE CASES

Karthik Use Cases:
1. Clicking a piece on the board
board.checkValidMoves(x, y) -> getPieceAt(x, y).getMovePattern() -> call move method using reflection
2. Update player scores
Increment the Player object's score with the value obtained from piece.getValue() when piece.getHealth() reaches 0
3. Display winning player
Call GameView.displayWinner() once board.checkWon() returns true to display some sort of graphic message on screen
4. Animate a piece being destroyed
When piece.getHealth() reaches 0, call pieceView.killedAnimation()
5. Allow for player vs player or player vs CPU
When creating Player objects, set one to "Player" and the other to either "Player" or "CPU" as desired by the user
6. Go from home screen to game screen
Set the onClick listener of each game button to create a new Scene object that creates the necessary type of Board object
7. Allow different sized boards
When reading from the XML, accept height and width as parameters and pass them into the Board object's constructor in the controller. Board.initStartingPosition() will create the necessary number of spaces in the data structure that represents the board
8. Add a timer option 
Add a JavaFX timer to each player object and display the time as it elapses

Amjad Use Cases:
1. Change in rules for pieces
Allow for pieces to have more or less abilities the user can set
2. Contoller for seperation of model and view
Resolve program architecture such that the classes of the model and view have information exchanged by the controller
3. Allow creation of the board in the controller
Allow the controller to make the board by calling the board constructor
4. Create multiple boards for multiple games at once
Implement feature of creating multiple boards (in controller) to run multiple games
5. Be able to view two games at once
Work with view to make the display show two different games that each can be played, essentiallu thy program would be run twice
6. Combine CPU information with controller
In the CPU option, automatically fill the controller information after each iteration to update the CPU's moves quicker
7. Seperate data access for minimal knowlegde of each class
Construct program layout such that each class has the bare necessities of data, and have the controller be the intermediary if another class wishes to access data it does not have
8. Add options for the player to edit their privilege
Add a feature where the user can grant themselves access to change game state variables that are


Ameer Use Cases:
1. Light up the clicked piece.
Receive onClick information from cell type and display a red box around the corresponding cell.
2. Show potential movement options with a yellow square.
Recieve the list of game cell indices for possible moves and display with a yellow box around the appropriate cells.
3. Animate a piece's movement.
Use the JavaFX package to slowly display the movement of the desired piece once a validMove is determined in the backend.
4. Make a killed piece appear on the right-hand side with other removed pieces.
Delete the ImageView from the cell and create a new ImageView object in the compartment for removed pieces.
5. Dark mode.
Switch out the existing style sheet for a new one with different color values.
6. Allow a user to pick white or black pieces.
Produce a pop-up in the beginning that allows the user to pick which color they wish to play with. This will be an event that is triggered upon selecting the desired game.
7. Menu to select player preferences for opponent.
Produce another pop-up that displays two options for pvp or pv-cpu and pass information to backend.
8. Pop-up message for winning game.
Once the backend has determined a winner, display a popup screen that is triggered on a win and display
a win message.

Michael Use Cases:
1. Click a cell on the grid that has no piece
Call method that checks if there is a piece at that cell; if not, do nothing
2. Move a piece to an invalid place on the grid
Check if selected spot on grid is in the list of valid places a piece can go; if not, do nothing or display message
3. Capture an opponent piece
If user first selects his/her piece then selects an opponent piece (assuming that cell is in the valid locations), move piece to that location and delete the opponent's piece
4. Update the history after a move
Hold the moved piece's original location and final location, add to an observable list that can be displayed in frontend
5. Go from game screen to home screen
Click quit and change the scene as the event handler
6. Allow a piece to have different potential movement pattern from default
Specify different movement pattern for a piece in XML, change how a piece's movement pattern is initialized accordingly
7. Add a piece captured to a player's captured pieces in backend and frontend
When a piece is captured, add to the appropriate player's observable list of captured pieces and display in frontend
8. Allow user to add a new piece to board in middle of game
Have button to specify the new piece type and another button to add it to board, then click the desired location in the cell

Eric Use Cases:

* AI

1. Change control based on whether the Player is a CPU or a person on the computer.
The method isCPU() will be called on the Player -- it will return true if the player is a CPU. In the case that isCPU returns true, control will be passed to the AI by calling AI.generateMove(). The type of the AI should already be known at this time, and if it is to be changed, it will be changed before calling generateMove (so, there is no need for a parameter that specifies the AI's strategy).

2. The AI chooses a move.
At the end of the project, there will be a handful of algorithms that the AI can use to generate an "intelligent" move. These will be represented by an enum, and methods will either be called by (1) associating methods directly with the enum, or (2) using reflection, which probably isn't recommended, but could be used to demonstrate the material we have learned...

3. The User chooses a type of AI.
To start off, there should be a default AI algorithm that is used in the absence of the user choosing an algorithm. In the case that the user does choose an algorithm, there will be a UI element that generates options using the enum within the AI class (it would just convert them to strings to add to a ChoiceBox, for instance). This ChoiceBox will call the changeAlgorithm(String) method on the AI class, which will just change the previous enum to the new enum represented by that String. changeAlgorithm will not take an enum as a parameter because that would give the view access to the AI class that does not make sense.

* Frontend

4. Creating a game from the splash page.
In the end, there will be two options in the splash page -- the user can either load an XML file or choose from a list of pre-defined games (checkers, chess, Go, and the 4 more complex games we decide to implement). The user will choose one of these from a UI element such as a ChoiceBox. When the 'LOAD GAME' button is clicked, the controller will check the RadioButton (with choices 'load file' or 'choose standard game') for one of these two options, and then call generateGameFromXML(File) or generateStandardGame(String). Both of these will end up creating a *new* window for the game that has been started. Any number of games can be generated with this setup and played simultaneously.

5. Summary statistics are generated for the game state.
There are not too many statistics associated with any given game -- as of now, the method getStatsPoints() will be called on the Board by the controller, which will then be passed to the frontend. To accommodate games with different sorts of statistics, reflection could be used in the following way: each game type will have a list of the sorts of statistics it has (Points, TotalHealth, etc.), and methods will be called using reflection.
List statList = new ArrayList<>();
for (String statType: statTypes) {
    Method getStat = Board.class.getMethod(String.format("%s%s", "get", statType), null);
    String currentStat = getStat.invoke(currentBoard);
    statList.add(currentStat);
}


6. Summary statistics are generated for the AI.
Statistics can be specific to a perticular type of AI, too. For instance, the alpha-beta minimax algorithm could include the number of moves ahead that the algorithm got to before its time ran out. The time required to generate the move is an interesting stat that the user may want to know, too, especially if the AI didn't take all of the time allotted to it because it found an optimal move before time ran out. Because the types of AI are determined by us, reflection does not seem necessary -- there will just be a getStats() method that is different for each type of AI. This method will use the enum associated with the AI to determine which specific method to call to generate summary statistics.

7. The user clicks on a piece for which there are no valid moves.
In a box beneath the board labeled 'move options', there is an Text that says 'No valid move options for this piece'. This alert will be generated in the case that validMoves() produces an empty list for the piece that the user clicked on -- the string will come from the noValidMove element of a properties file.

8. The user chooses an 'attack' for a piece (as opposed to moving it).
The 'move options' box mentioned above will be used to display 'attack' options for different pieces. For instance, a rook with two options to attack could have the buttons 'attack B3' and 'attack 1A'. The user can either click these buttons or click on the pieces that they want to attack to process this move. Like with any move, the doMove() method will be called.