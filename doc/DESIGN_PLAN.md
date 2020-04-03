## Introduction:

We want this project to accommodate a variety of strategy-based games that consist of pieces on a rectangular grid. The most basic instances of this would be checkers, Go, and chess. The more complicated instances of this would be essentially extensions of chess, with different types of pieces that can interact with other pieces in more unique ways. In addition, more complicated versions of this archetype could include different types of cells on the board (for instance, cells that act as walls). The key components of the game will be the board, the cells, the pieces, the players, and events that correspond to players' chosen moves. The priority, then, will be to provide an easy way to specify the types and properies of pieces and cells -- this would include the conditions that must be satisfied for a piece to be 'defeated', or removed, and the conditions that must be satisfied in order for a piece to perform certain moves. To allow for this to happen, the file associated with a certain game type would have to include different 'Event' types, too.

## Overview:

At a high level, we will look to implement a Board class that represents the full gameboard -- it will contain all of the Cell objects, which represent each spot on the board, and all of the Piece object, which represent the various active pieces (such as a knight in a chess). Player objects will interact with the Board to determine which options are available to make pieces perform actions and then choose an action.

In order to determine the rules of the game, we will have an XMLReader parse an XML file that either represents a predetermined game type or a game type that is inputted by the user. To allow an XML file to specify types of Pieces and Events, there will ultimately have to be some structure imposed on these that can be leveraged by the XML file. We have decided that, for sufficient generality, a Piece object will have *health*, *move types*, and *status*. *Health* is straightforward -- each Piece has a certain amount of health, which can be modified by Events, and once that health reaches zero, the piece is removed from the board. *Move types* are instances of the Event class that are associated with a certain type of piece. They will be accessed to determine what a piece can do and to perform that move. *Status* is probably the most complex property of a Piece, but may be necessary in order to implement more 'exotic' games. Essentially, a status is a property that performs an Event on a Piece on each turn. This Event could be contingent on certain condions or not. The most straightforward example of this could be the 'poisoned' condition, in which a Piece loses one health each turn.

The information contained in an XML file will be able to determine the number of Pieces, the types of Pieces (including names, which could be used for an enum), the properties of these types (health and move types), and the Events that are associated with particular move types and statuses.

## APIs:

### Frontend API:
- GameView:
    - Display Winner
- PieceView:
    * Due to the fact that animations associated with certain events are a possible extension (even though we do not plan to implement them), we should have a PieceView class associated with each Piece. In practice, this class will probably just contain an Image. The GameView would move this image or remove it from the view based on the moves that are performed.
    - MoveAnimation, KilledAnimation
- CellView:
    * We plan to have the user interact with pieces by clicking on them and then viewing available moves. Part of this would include 'lighting up' cells that the piece can move to. Due to this, there would need to be a CellView class with methods that light it up or turn it off.
- MoveDisplayer:
    * This class will take in the possible moves of a piece that is clicked on and then call various methods on the GameView to visualize moves. For instance, it could call GameView.lightUpCell(2, 3) to light up cell B3.


### Backend API:

- Board (Abstract Class)
    - checkWon(): external; return 'true' if a player has won.
    - checkValidMoves(int, int): external; return valid moves the piece at the index
        - called when user clicks on piece
    - getPieceAt(int, int): external; returns the type of piece at the cell, or null if there is no cell
    - doMove(index, index, String): external; once a move is determined to be valid, it will move the piece at the index (using reflection on the String) and update the board
    - initStartingPosition(): internal; load configuration from the XML
    - isValidCell(index, index): internal; checks if the index is valid in the board

- CellInterface
    - getState(): returns whether the cell is open or 'blocked'

- PieceInterface
    - getMovePattern(): external; returns move type
    - getHealth(): external; returns the health of a piece (could be used in more complex games)
    - getValue(): external; returns the score of killing a certain page
    - isActive(): external; returns if the piece can be moved on this turn
    - changeActive(): external; makes the piece active if it is active and inactive if it is not active

- PlayerInterface
    - isCPU(): returns true if the player is a CPU
    - getScore(): returns the player's current score
    - getName(): returns the player's name
    - getColor(): returns the color of their pieces

- CPU
    - generateMove(StrategyType)

## Design Details:

The controller will call isCPU() on the PlayerInterface to determine whether the move will be determined by the user or whether the controller will consult the AI. Control will be passed to the AI by calling generateMove on the CPU with the StrategyType enum. Then, doMove() will be called using the data that is returned. It will call getScore(), getName(), and getColor() in order to pass relevant information to the view. The controller will call isActive() to determine whether the user can interact with a piece. When the user clicks a piece, the controller calls getPieceAt() and checkValidMoves() to receive the moves that are possible with this piece. It will then call doMove() with the indexes of the cell that the user clicks to move to (using the string associated with the move type). On the frontend side of this, the GameDisplayer will be called by the controller with data generated from checkValidMoves().

## Example Games:

Three example games are chess, checkers, and Go. These three games are completely different in terms of the way they are played, including how the pieces operate and move, how players win and interact with one another, etc. However, our design is generalizable enough to work on any games that incorporate boards that are grids and players that move pieces. For instance, chess pieces are moved differently from checker pieces, but this can merely be accounted for by different implementations of checkValidMoves() for respective pieces in each game. Pieces can be captured in each game, and each game can have different implementations of checkWin() to indicate a player winning. 

## Design Considerations:

We spent time considering how to implement different types of moves. Initially we considered having different types of Piece classes that extend from a single Piece interface. However, we realized this would severely limit the customizability for the user in terms of what rules they could input. We wanted to ensure that users could take existing games like chess and make semi-significant changes to how each piece can move in the game. As a result, we decided that having a single Piece concrete class with several instantiated objects with different parameters would be a smarter move. In order to account for different types of movements, we will have users follow some sort of naming convention when defining various move patterns that will be stored in the corresponding Piece object. Then, we will use reflection in the Board class to determine what cells would be valid to move into and which cells to light up.


We were deciding on the feasibility between having a single Board class that accepts the type of board game as a parameter, which would allow us to execute the board functionalities. However, we later realized how ineffective this would be as the board would need to be passed around to determine when the game was one, etc. We ultimately decided it was better to have a single Board Game Interface which would accomodate the different game types with methods such as checkWon which could check if the different games were won based on their specific criteria.