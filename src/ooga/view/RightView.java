package ooga.view;

import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;

public class RightView {


    private HBox console;

    public RightView(){

        console = new HBox();

        createConsole();

    }

    private void createConsole(){
        TextArea area = new TextArea();
        console.getChildren().add(area);
        console.setLayoutX(750);
        console.setLayoutY(35);

    }

    public HBox getConsole(){
        return console;
    }
}
