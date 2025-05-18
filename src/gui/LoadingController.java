package gui;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class LoadingController {

    @FXML
    private ProgressBar progressBar;

    @FXML
    private ImageView carImage;

    @FXML
    public void initialize() {
        // Load image from resources
        carImage.setImage(new Image(getClass().getResource("/gui/assets/loading.png").toExternalForm()));

        // Simulate loading
        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(e -> {
            try {
                Stage stage = (Stage) progressBar.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("MainView.fxml"));
                Scene mainScene = new Scene(loader.load());
                stage.setScene(mainScene);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        delay.play();
    }
}
