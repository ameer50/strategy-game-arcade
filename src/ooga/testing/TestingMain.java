package ooga.testing;

import javafx.application.Application;
import javafx.stage.Stage;
import ooga.controller.Controller;

public class TestingMain extends Application {

    public static void main (String[] args) {
      launch();
    }

    @Override
    public void start(Stage stage) {
        CustomController c = new CustomController(stage);
    }
}
