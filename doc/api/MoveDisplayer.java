package game.views
public interface MoveDisplayer{
	/**
	Get valid moves from backend to display on screen when a piece is selected
	Helper method for displayValidMoves
	**/
	private void getValidMoves();

	/**
	Light up the valid moves on screen when piece is selected
	**/
	public void displayValidMoves();
}