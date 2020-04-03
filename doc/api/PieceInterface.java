package game.board
public interface PieceInterface{
	/**
	Get the move pattern of the selected piece
	@return move pattern string
	**/
	public String getMovePattern();

	/**
	In more complex games, return health of the piece
	@return double value of current piece health
	**/
	public double getHealth();

	/**
	Get the score obtained from killing this piece
	@return value of current piece
	**/
	public double getValue();

	/**
	Check if this piece can be moved on the current turn
	@return true if piece is active, false if not
	**/
	public boolean isActive();

	/**
	Set status of piece to desired value
	@param active whether or not this piece is active
	**/
	public void setActive(boolean active);
}