module card24game.card24game {
    requires javafx.controls;
    requires javafx.fxml;


    opens card24game.card24game to javafx.fxml;
    exports card24game.card24game;
}