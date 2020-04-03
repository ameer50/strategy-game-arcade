import javafx.scene.paint.Color;

public interface CellView{

	/**
	Make cell appear lit up on game board
	**/
	void turnOn();

	/**
	Set color of the cell in display
	@param color the color of the cell
	**/
	void setColor(Color color);
}