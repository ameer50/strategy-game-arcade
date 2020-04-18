package ooga.custom;

import javafx.application.Application;
import javafx.stage.Stage;

public class CustomMain extends Application {

    public static void main (String[] args) {
      launch();
    }

    @Override
    public void start(Stage stage) {
        CustomController c = new CustomController(stage);
    }
}
