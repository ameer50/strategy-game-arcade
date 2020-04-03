import javafx.scene.image.ImageView;

public interface PieceView{

	/**
	Play unique animation of piece as it moves
	**/
	void moveAnimation();
	/**
	Play unique animation of piece when it is destroyed
	**/
	void killedAnimation();
}