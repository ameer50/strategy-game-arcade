README
====

This project implements a suite of strategy-based games enhanced with CPU functionality. Games include Chess, Checkers,
Othello, Connect4, and even Custom games.

Names: Ameer Syedibrahim, Karthik Ramachandran, Michael Xue, Eric Carlson, Amjad Syedibrahim

### Timeline

Start Date: 4/2/2020

Finish Date: 4/24/2020

Hours Spent: 500 hours

### Primary Roles

Ameer - Frontend/Controller (Primary), Backend (Secondary) 

- I worked designing the menu screen, popups, and game screen to allow for a flexible design. 
- Specifically, I helped design the board view, pieces arrangement,  dashboard view, and the buttons that the user would click to interact with the game.
- Minor frontend features added: Dark mode, New Window, class to easily make Popups
- I helped build the Othello and Connect4 game in the Backend, Controller, and Frontend. This involved implementing the methods in the game-specific classes in the backend and working with the existing design to integrate it in the controller and frontend.
- Worked on core logic for Othello including a map of pieceTrails, a necessary component of Othello which could be generalized
- Assisted with the design and implementation of the Move class, the key component of the Controller that allowed for a conceptual definition of what a move means across multiple diverse games.

Karthik - Backend (Primary), generalist role (minimal)
* Wrote the Chess game to follow rules of chess including promoting pieces, determining checkmate, preventing pieces from moving into check, and even enforcing pinning on blocking pieces
* Designed and implemented the Piece class to be generalizable to all games while storing sufficient information to be useful
* Designed the Board abstract class to contain appropriate methods for all games while balancing abstraction with game-specific functionality
* Other roles include working on AI to increase speed of algorithm and standardizing games to avoid errors, helping debug and implement recursion in checkers, and minor additions to controller

Michael - Frontend/Controller (Primary), Backend (Secondary)
* Wrote the controller and designed the flow of data between backend and frontend for various games and features
* Assisted with frontend for creating the board and cells, the dashboard, popups, and various screens
* Created the design of the Player, History, and Move classes
    * Enabled scoring, undo/redo, and capturing/converting pieces functionality
* Helped build Othello and ConnectFour on both the backend and frontend
* Worked on error handling/refactoring

Eric - AI/Custom games(Primary), JSON (secondary)
* Designed all AI algorithms, including random and trivial (first available) move and alpha-beta minimax, which travels move trees down several layers before selecting the highest scoring first move. 
* Worked with backend team to make repeated board creations as efficient as possible to speed up AI move generation
* Implemented custom game JSON format to enable users to create their own custom moves in any manner they choose while ensuring CustomBoard is able to handle most changes to what user wants
* Worked on shifting from XML to JSON to allow for greater ease of customizability for users without impacting the rest of the project

Amjad - Backend (Primary), Controller (Very Minimal)

- I worked primarily on the game of Checkers, which is one of the main game options the player has.
- More specifically, I worked on expanding the design to be flexible and generalize to the various combinations of moving, promoting and stacking that the player can do
- Other roles include, Error Handling, JUnit Testing, Structuring data away from the classes into properties files.


### Resources Used

Stack Overflow

- https://stackoverflow.com/questions/40384558/javafx-how-to-center-the-center-node-on-a-borderpane-according-to-the-stage
- https://stackoverflow.com/questions/37689441/javafx-text-control-setting-the-fill-color
- https://stackoverflow.com/questions/49059786/center-a-vbox-in-the-middle-of-a-borderpane-in-javafx
- https://stackoverflow.com/questions/22412759/how-to-set-listview-border-in-javafx
- https://stackoverflow.com/questions/36652453/javafx-inserting-image-into-a-gridpane


### Running the Program

Main class: Controller.java

Data files needed: 
* Running requires the following jars:
    * https://github.com/google/guava/wiki/Release23 (guava-23.0.jar)
    * http://www.java2s.com/Code/Jar/j/Downloadjsonsimple111jar.htm (json simple 1.1.1)

Features implemented:
* All games
    * Click on pieces to highlight valid moves and make moves
    * Error handling on loaded files and invalid moves
    * Icons to allow for adding new pieces to the board in Connect 4 and Othello
    * Undo and redo moves
    * Load from and save state to JSON
    * Three different AI levels: trivial, random, and alpha-beta minimax
    * History - detailed log of every piece and its initial and final location
    * Scoring
    * New Window
    * Dark Mode - inverts colors of screen
    * Skip Turn - allows user to skip their turn
    * Return to Menu

* Chess
    * Piece movements
    * Checkmate fully implemented
    * Pinned pieces can't move
    * Pieces can't move if king is under check
    * Promoting pawns to queens with undo functinoality

* Checkers
    * Piece movements
    * Stacking multiple kills (zigzags, multiple jumps, etc.)
    * Multidirectional piece movements
    * Undo promotions
    * Promotion to monarchs

* Othello
    * Piece additions to the board from icon
    * Trail of pieces are tracked (converted pieces map changes coins from one color to the other)
    * Player score increases when converting pieces of the other color
    * Pieces on the board can't be clicked once placed
    * If board is full or neither player can place a piece, return the winner as the player with most pieces on the board

* Connect 4
    * Piece additions to the board from icon
    * Piece needed variable which keeps track of number of pieces in a row to win (for n number in a row, can generalize to Connect n)
    * Pieces on the board can't be clicked once placed
    * If board is full and neither team has won, returns tie

* Custom
    * Users can set up basic move patterns, or combine them into compound move patterns, to define how pieces function
    * Backend work supports any combination of methods generated in order to ensure complete customizability
    * Pieces can be killed and scores can be added
    * Full functionality with AI

### Notes/Assumptions

Assumptions or Simplifications:
* Assume player must click on a cell with a piece in order to move it (reason for icons for piece-generating games like Othello and Connect 4)
* Converted pieces and promoted pieces behave the same way and are assumed to be the same. Our Move class has a map that holds convertedPieces. This concept of transforming n number of pieces from one piece to another pieces is used in games such as Othello, Chess, and Checkers, and thus required a mechanism for generalization.
* Made icons part of the frontend and backend in order to preserve the implementation of setOnPieceClicked lambda

Interesting data files:
* JSON data file for loading in initial game states and configurations.
* CSS Style Sheets for different color schemes and themes
* Properties files for dumping data structures that are too cumbersome to put in the class

Known Bugs:
* Player 1 always moves first, so black will move first in Chess if black is selected as player 1
* Undo move leads to a turn issue for AI for one turn forcing the user to control the AI team

### Impressions

Our impressions of the project as a team are very positive. We were very pleased with the flexibility we had in choosing
what game types to implement. Given the three week time constraint we had on this project, we found the requirments of
the project to be very plausible and reasonable for our team. We were able to accomplish the goal of the project with 
each teammate putting in roughly 3-4 hours of work a day. We also felt that this project did a tremendous job of teaching
us about design patterns and other functionalities in Java that allow for the creation of a data-driven program. Overall, the performance of Team Hirudinea was solid and the project came together nicely.