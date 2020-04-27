## DESIGN.md

**Names**:
Ameer Syedibrahim, Karthik Ramachandran, Michael Xue, Eric Carlson, Amjad Syedibrahim

**Team Roles and Responsibilities**:
Ameer:    Frontend/Controller (Primary), Backend (Secondary)
Karthik:  Backend (Primary), generalist role (minimal)
Michael:  Frontend/Controller (Primary), Backend (Secondary)
Eric:     AI/Custom games (Primary), JSON (secondary)
Amjad:    Backend (Primary), Controller (Very Minimal)

**Design Goals**:
* Make adding new games as simple as possible, requiring little to no change on existing controller and frontend code
* Maintain encapsulation to ensure that every class can function independently, wihtout needing to know the implementation of any other classes. This especially applies with the Board classes
* Ensure a high degree of customizability to allow users to easily make changes to existing or custom games
* Allow for a reasonably efficient backend to allow for a user-friendly AI that doesn't take too long to run
* Minimize data flows between the frontend and backend classes to make debugging easier and allow for a more stable game experience
* Use CSS and properties files to minimize hardcoded strings and constants necessary

**High Level Design**:

At a high level, our design follows the MVC model. The model handles all the backend processing, including calculating valid moves, executing moves, checking if a player has won, and updating player scores. The view handles all the displays, including the menu screen, the game screen (BoardView and DashboardView), and popups for setting selections, errors, and winner display. The controller acts as the middleman between the view and the model, exchanging information between the two when the user interacts with the frontend. The typical flow of our program is that when a user interacts with a frontend component, a listener set up in the controller triggers the information to be sent to the backend, which processes it and sends it back to the frontend to make the appropriate display. It also serves as the starting point of our program and sets up the game. 

**Assumptions that Affect Design**:
Game states must be set up following the format of the JSON files, game will not load without this format being accurate.

**Example Games and Differences**:

**Chess**: Has move patterns for every piece, allows any piece to adopt a different move pattern if fed in from JSON file. Extensive move validation to account for pinning, check, and checkmate

**Checkers**: Each color has its own move pattern, and allows for the options to make multiple moves in one turn if the player is in a position to stack pieces.

**Connect 4**: Pieces are added to the board instead of moved from pre-existing states. Goal is to get four in a row, but can generalize to Connect N. Pieces on the board also cannot be clicked (cannot modify the board).

**Othello**: Pieces are also added to the board instead of moved from pre-existing states, and cannot be removed, similar to Connect 4. Pieces convert from one color to another when a user places a piece down and points are added to his/her score.

**Custom Games**: Pieces are moved from pre-existing spaces. These pieces remove oppenents' pieces my moving on top of them (like chess), and cannot stack like in checkers. The user wins when they have eliminated all of the opponent's pieces. The move patterns for the various piece types (defined by the user) can range from relatively interesting to extremely exotic, depending on what the JSON file specifies.



**How to Add New Features**:

Adding features such as new move patterns for a game piece can be done by defining the movement in a properties file and adding the new piece type to the JSON file. In the frontend, new features can be added by building elements on top of the GameScreen class, which has easy access to both the Board View and Dashboard View. Thus, a component can easily be added to show a new metric of the game to the user. Due to the fact that the frontend has a well-defined communication protocol with the controller, it would be very easy to incorporate new UI elements that would have access to the necessary backend components. This could include, for instance, buttons that change various values of the game state.

To create a new game, the user simply creates a class that extends the abstract Board class and implements the three key methods -- doMove, checkWon, and getValidMoves. These three methods dictate the logic of the game. By just defining how a move is caldulated, checking if the user has won, and returning a list of valid moves, the necessary framework for the game is constructed. This requires only minimal introduction of functionality in the backend, as the Controller and frontend can accomodate the new games with no code additions. If the user just wants to create a simple game in which pieces can move in any sort of way and the user wins by eliminating all of the other player's characters, they could create a new JSON file for CustomBoard. 