public class Controller{
	//This example illustrates how our Controller would update the score of a player following a game move
	ChessBoard board;
	Player currentPlayer
	//from Example1.java
	public void executeMovement(){
		
		List<String> moves = board.getValidMoves(0, 0);
		String move = moves.get(0);
		
		double score = board.doMove(0, 0, move);
		//now use the score value to increase the active player's score as necessary
		updatePlayerScores(currentPlayer, score)
	}


	public void updatePlayerScores(Player currentPlayer, double score){
		//use the incrementScore method in the Player API to update the Player's score using the information obtained from the Board API
		currentPlayer.incrementScore(score);
	}
}