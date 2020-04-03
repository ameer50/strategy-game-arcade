package game.player
public interface PlayerInterface{
	/**
	Check if this player is a computer player
	@return true if the player is controlled by the computer
	**/
	public boolean isCPU();	

	/**
	Get the score of the current player
	@return double value of current score of the player
	**/
	public double getScore();

	/**
	Get the name of the current player for display purposes
	@return name of the current player
	**/
	public String getName();

	/**
	Get the color of the pieces controlled by the current player
	@return the Color that the current player can control
	**/
	public Color getColor();
}