package ooga.custom;

import javafx.application.Application;
import javafx.stage.Stage;

public class TestingMain extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        TestingController c = new TestingController(stage);
    }
}
