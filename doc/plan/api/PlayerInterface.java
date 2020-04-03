import javafx.scene.paint.Color;

public interface PlayerInterface{
	/**
	Check if this player is a computer player
	@return true if the player is controlled by the computer
	**/
	boolean isCPU();

	/**
	Get the score of the current player
	@return double value of current score of the player
	**/
	double getScore();

	/**
	Get the name of the current player for display purposes
	@return name of the current player
	**/
	String getName();

	/**
	Get the color of the pieces controlled by the current player
	@return the Color that the current player can control
	**/
	Color getColor();
}