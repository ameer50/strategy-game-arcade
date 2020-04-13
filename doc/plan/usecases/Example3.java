//public class Controller{
//	//This example illustrates how our Controller would check for a winner and display a victory message once the game has been completed
//	ChessBoard board;
//	GameView gameView;
//	//from Example1.java and Example2.java
//	public void executeMovement(){
//		List<String> moves = board.getValidMoves(0, 0);
//		String move = moves.get(0);
//
//		double score = board.doMove(0, 0, move);
//		updatePlayerScores(currentPlayer, score)
//
//		//now we will use the checkWon method from the Board API to determine if there is a winner
//		//if there is, use the GameView API to display the winner text message
//		if(board.checkWon()){
//			gameView.displayWinner();
//		}
//	}
//
//	//from Example1.java and Example2.java
//	public void updatePlayerScores(Player currentPlayer, double score){
//		//use the incrementScore method in the Player API to update the Player's score using the information obtained from the Board API
//		currentPlayer.incrementScore(score);
//	}
//}