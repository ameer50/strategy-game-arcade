### Project Plan

* Responsibilities:

Eric:
Primary: Backend - AI
Secondary: Frontend (general, for now)

Amjad:
Primary: Backend - Controller; facilitating information flow between model and view.
Secondary: Backend - Board, Pieces, and core components.

Ameer:
Primary: Frontend (general, for now)
Secondary:

Karthik:
Primary: Backend -- Board, Pieces, and core components.
Secondary: Backend - Controller; facilitating information flow between model and view.

Michael:
Primary: XML and parsing, helping with Controller and Backend (Player object)
Secondary: Miscellaneous backend work

* Timeline:

SPRINT 1 (Apr 9): 
The goal in this sprint is to get the game of checkers (and hopefully chess) working within the general framework that we have set up. The frontend should have most of its basic functionality -- there is a board with cells and pieces on it, and the user can click on pieces to see which moves are available. There should be two options -- to play with two people on the same machine, or to play with a CPU. The XML parsing should be relatively functional. The AI should be set up with a basic algorithm, to be sure that conrol can be passed to it and back to the user. 

SPRINT 2 (Apr 16):
Refactoring will be done at the beginning of Sprint 2. By the end of this sprint, we should have checkers, chess, and Go done. There should be testing done on more 'alternative' game types. Clicking on a piece should light up the cells that it can move to and display a pop-up of the move type (in the case of chess). There should be basic animations and Images associated with pieces (instead of Shapes in JavaFX).The users should be able to save and load games. The frontend should show summary statistics (turns taken, points per player, AI statistics). The backend should be able to 'undo' a move. The XML file formatting and parsing should be where it will be for the rest of the project. The CPU should be able to perform Just-in-Time algorithms with a certain time limit (ex. minimax).

SPRINT 3 (Apr 24):
Refactoring will be done at the beginning of Sprint 3, too. This sprint should include extensions such as the use of different Cell types and different Piece types. These should have been tested in Sprint 2, but will now be implemented in full. The idea of a 'strategy' game should be tested to its limits in this stage -- and there should be 4 new game types on top of checkers, chess, and Go. A 'dark mode' should now be possible in the frontend, and the user should be able to choose between a variety of CPU algorithms to test out and play against.
