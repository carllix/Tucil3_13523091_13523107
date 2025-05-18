package gui;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class RushHourApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load loading screen first
            FXMLLoader loadingLoader = new FXMLLoader(getClass().getResource("LoadingScreen.fxml"));
            Parent loadingRoot = loadingLoader.load();
            Scene loadingScene = new Scene(loadingRoot);

            primaryStage.setTitle("Rush Hour Solver - Loading...");
            primaryStage.setScene(loadingScene);

            // Set fixed size for consistency
            primaryStage.setWidth(800);
            primaryStage.setHeight(600);
            primaryStage.setResizable(false);
            primaryStage.show();

            // After 3 seconds, switch to MainView
            PauseTransition delay = new PauseTransition(Duration.seconds(3));
            delay.setOnFinished(event -> {
                try {
                    FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("MainView.fxml"));
                    Parent mainRoot = mainLoader.load();
                    Scene mainScene = new Scene(mainRoot);
                    primaryStage.setScene(mainScene);
                    primaryStage.setTitle("Rush Hour Solver");

                    // Optional: keep size locked after transition
                    primaryStage.setWidth(800);
                    primaryStage.setHeight(600);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
            delay.play();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}