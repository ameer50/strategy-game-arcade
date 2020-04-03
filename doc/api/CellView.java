package game.views
public interface CellView{
	private boolean light;

	/**
	Make cell appear lit up on game board
	**/
	public void turnOn();

	/**
	Set color of the cell in display
	@param color the color of the cell
	**/
	public void setColor(Color color);
}