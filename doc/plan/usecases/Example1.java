public class Controller{
	//This example illustrates how our Controller would execute a single move selected from the potential valid moves a given piece could make
	ChessBoard board;
	
	public void executeMovement(){
		//using the getValidMoves method in the Board API we obtain all the possible moves for the piece at board cell 0, 0
		List<String> moves = board.checkValidMoves(0, 0);
		String move = moves.get(0);
		//Using the doMove method in the Board API we execute the instruction stored in the first valid move sequence from the selected piece
		double score = board.doMove(0, 0, move);
	}
}