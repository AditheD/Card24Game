package card24game.card24game;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * Starts the JavaFX Card 24 Game.
 */
public class Card24Application extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                Card24Application.class.getResource("game.fxml")
        );

        Scene scene = new Scene(loader.load(), 800, 500);

        scene.getStylesheets().add(Objects.requireNonNull(Card24Application.class.getResource("style.css")).toExternalForm()
        );

        stage.setTitle("Card Game - 24");
        stage.setScene(scene);
        stage.setMinWidth(800);
        stage.setMinHeight(500);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
