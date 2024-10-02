module minesweeper.multimediaminesweeperv2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens minesweeper.multimediaminesweeperv2 to javafx.fxml;
    exports minesweeper.multimediaminesweeperv2;
}