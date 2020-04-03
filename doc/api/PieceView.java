package game.views
public interface PieceView{
	/**
	Image that represents this Piece
	**/
	ImageView image;
	/**
	Play unique animation of piece as it moves
	**/
	public void moveAnimation();
	/**
	Play unique animation of piece when it is destroyed
	**/
	public void killedAnimation():
}