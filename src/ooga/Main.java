package ooga;


import javafx.application.Application;
import javafx.stage.Stage;
import ooga.controller.Controller;

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
