package game.board
public interface CellInterface{
	/**
	Check if cell is open or blocked
	@return true if open, false if blocked
	**/
	public boolean getState();
}