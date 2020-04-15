package ooga;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ooga.controller.Controller;
import ooga.view.GameScreen;
import ooga.xml.XMLParser;

/**
 * Feel free to completely change this code or delete it entirely. 
 */
public class Main extends Application {
    /**
     * Start of the program.
     */
    public static void main (String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) {

        Controller c = new Controller(primaryStage);


    }
}
