public interface PieceInterface{
	/**
	Get the move pattern of the selected piece
	@return move pattern string
	**/
	String getMovePattern();

	/**
	In more complex games, return health of the piece
	@return double value of current piece health
	**/
	double getHealth();

	/**
	Get the score obtained from killing this piece
	@return value of current piece
	**/
	double getValue();

	/**
	Check if this piece can be moved on the current turn
	@return true if piece is active, false if not
	**/
	boolean isActive();

	/**
	Set status of piece to desired value
	@param active whether or not this piece is active
	**/
	void setActive(boolean active);
}