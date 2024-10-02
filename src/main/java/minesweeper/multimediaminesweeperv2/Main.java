package minesweeper.multimediaminesweeperv2;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.fxml.Initializable;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.util.Duration;

public class Main extends Application implements Initializable {
    String Scenario_ID;
    MineField mf;
    int gridSize;
    public Timeline timeline;
    public int durationInSeconds, durationInSecondsLeft;
    ArrayList<Quadrable<Integer, Integer, Integer, String>> pastRounds = new ArrayList<>();

    @FXML
    private GridPane gridGame;
    @FXML
    public Label timeLabel;
    @FXML
    public Label MarkedTilesLabel;
    @FXML
    public Label MinesLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    public void initialize() {

        timeline = new Timeline(
            new KeyFrame(Duration.seconds(1), e -> {
                durationInSecondsLeft--;
                timeLabel.setText("Time left: " + durationInSecondsLeft);
                // code to end the game
                if (durationInSecondsLeft <= 0) {
                    timeline.stop();
                    mf.gameEnded = true;
                    this.pastRounds.add(new Quadrable<>(mf.numMinesatStart, mf.leftClickTries, durationInSecondsLeft, "Computer"));
                    try {
                        updatePrevRoundsDetails();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    try {
                        showMines();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            })
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play(); // start the timeline

        mf.makeMinefield();
        mf.addMines();

        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                gridGame.add(mf.minefield[i][j], i, j);
            }
        }

    }

    public ArrayList<Quadrable<Integer, Integer, Integer, String>> getPrevRoundDetails() throws IOException {
        ArrayList<Quadrable<Integer, Integer, Integer, String>> rounds = new ArrayList<>();
        String fileName = "medialab/rounds/pastRounds.txt";
        String line;

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))) {
            int i = 0;
            // get the first 5 lines (if they exist)
            while (((line = bufferedReader.readLine()) != null) && i < 5) {
                String[] splitLine = line.split("-");
                rounds.add(new Quadrable<>(Integer.parseInt(splitLine[0]), Integer.parseInt(splitLine[1]), Integer.parseInt(splitLine[2]), splitLine[3]));
                i++;
            }
            return rounds;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Parent root = null;
        try{
            URL url = new File("src/main/resources/minesweeper.fxml").toURI().toURL();
            root = FXMLLoader.load(url);
        } catch(Exception e){e.printStackTrace();}
        primaryStage.setScene(new Scene(root,1000,1000));
        primaryStage.setTitle("MediaLab MineSweeper");
        primaryStage.setResizable(true);
        String background_color = "whitesmoke";
        root.setStyle("-fx-background-color:" + background_color + ";");
        primaryStage.show();
    }

    public void startAction(ActionEvent actionEvent) throws IOException {
        loadAction(actionEvent);
    }

    public void loadAction(ActionEvent actionEvent) throws IOException {
        ArrayList<Integer> input = new ArrayList<>();
        String errorMsg = "";

        if (Scenario_ID == null) {
            TextInputDialog lDialog = new TextInputDialog("Scenario_ID");
            lDialog.setHeaderText("Enter your game Scenario_ID (number):");
            lDialog.showAndWait();
            Scenario_ID = lDialog.getEditor().getText();
        }

        File directory = new File("medialab");
        File file = new File(directory, "SCENARIO-" + Scenario_ID + ".txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                input.add(Integer.valueOf(line));
            }
        } catch (IOException e) {
            throwAlert("IOException", "Necessary file was not present!");
        }
        if (input.size() != 4) {
            throwAlert("InvalidDescriptionException", "The Scenario should have exactly 4 lines\n(difficulty level, number of mines, time, existance of supermine)");
            return;
        };
        if (input.get(0) != 0 && input.get(0) != 1) {
            errorMsg += "Invalid Difficulty Level (Valid: 0 or 1)\n";
            throwAlert("InvalidValueException", errorMsg);
            return;
        }
        if (input.get(0) == 0) {
            if (input.get(1) < 9 || input.get(1) > 11) {errorMsg += "Invalid number of mines when level is equal to 0 (Valid: 9 - 11)\n";}
            if (input.get(2) < 120 || input.get(2) > 180) {errorMsg += "Invalid time when level is equal to 0 (Valid: 120 - 180)\n";}
            if (input.get(3) != 0) {errorMsg += "You can not add a super mine when the level is set to 0\n";}
        }
        if (input.get(0) == 1) {
            if (input.get(1) < 35 || input.get(1) > 45) {errorMsg += "Invalid number of mines when level is equal to 1 (Valid: 35 - 45)\n";}
            if (input.get(2) < 240 || input.get(2) > 360) {errorMsg += "Invalid time when level is equal to 1 (Valid: 240 - 360)\n";}
            if (input.get(3) != 1) {errorMsg += "You must have a super mine when the level is set to 1\n";}

        }

        if (!errorMsg.equals("")) {
            throwAlert("InvalidValueException", errorMsg);
            return;
        }


        gridSize = input.get(0) == 1 ? 16 : 9;
        int minesNo = input.get(1);
        MinesLabel.setText("Mines Number: " + minesNo);
        durationInSeconds = input.get(2);
        durationInSecondsLeft = durationInSeconds;
        boolean hasSuperMine = input.get(3) == 1;

        mf = new MineField(gridSize, minesNo, hasSuperMine, this);

        initialize();
    }

    public void updatePrevRoundsDetails() throws IOException {
        String fileName = "medialab/rounds/pastRounds.txt";
        String line;

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))) {
            int i = 0;
            // get the first 4 lines (if they exist)
            while (((line = bufferedReader.readLine()) != null) && i < 4) {
                String[] splitLine = line.split("-");
                this.pastRounds.add(new Quadrable<>(Integer.parseInt(splitLine[0]), Integer.parseInt(splitLine[1]), Integer.parseInt(splitLine[2]), splitLine[3]));
                i++;
            }
        }
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8))) {
            // write all words in the txt file
            int i = 0;
            for (Quadrable<Integer, Integer, Integer, String> t : this.pastRounds) {
                if (++i == this.pastRounds.size()) writer.write(t.getX() + "-" + t.getY() + "-" + t.getZ() + "-" + t.getW());
                else writer.write(t.getX() + "-" + t.getY() + "-" + t.getZ() + "-" + t.getW() + "\n");
            }
        }
    }

    public void throwAlert(String exceptionID, String errMessage) throws IOException {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(exceptionID + "Error!");
        alert.setHeaderText(null);
        alert.setGraphic(null);
        alert.setContentText(errMessage);
        alert.showAndWait();
    }

    public void getRoundsAction(ActionEvent actionEvent) throws IOException {
        StringBuilder pastRounds = new StringBuilder();

        ArrayList<Quadrable<Integer,Integer, Integer, String>> rounds = getPrevRoundDetails();
        for (Quadrable<Integer,Integer, Integer, String> entry : rounds) {
            pastRounds.append("Mines Number: ")
                    .append(entry.getX()).append(", Number of Tries: ")
                    .append(entry.getY()).append(", Time Given: ").append(entry.getZ()).append(", Winner: ").append(entry.getW()).append("\n");
        }

        if (rounds.size() == 0) pastRounds.append("No past rounds available.");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Past Rounds");
        alert.setHeaderText(null);
        alert.setGraphic(null);
        alert.setContentText(pastRounds.toString());
        alert.showAndWait();
    }

    public void exitAction(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void createAction(ActionEvent actionEvent) throws IOException {
        String scenario = null, level = null, numMines = null, time = null;
        String superMine = null;
        // Create a new TextInputDialog
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Input Dialog");
        dialog.setHeaderText("Please enter the following values:");

        // Set the content text and input fields
        TextField scenarioInput = new TextField();
        TextField levelInput = new TextField();
        TextField minesNoInput = new TextField();
        TextField timeInput = new TextField();
        TextField superMineInput = new TextField();
        dialog.getDialogPane().setContent(new VBox(9,
                new Label("Scenario:"), scenarioInput,
                new Label("Level (0 or 1):"), levelInput,
                new Label("Number of Mines: \n for level 0: 9-11 \n for level 1: 35-45 "), minesNoInput,
                new Label("Time: \n for level 0: 120-180 \n for level 1: 240-360 "), timeInput,
                new Label("Super Mine (true:1 or false:0) \n on level 0 you cannot have a supermine"), superMineInput

        ));

        // Wait for the dialog to be closed and get the input values
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            scenario = scenarioInput.getText();
            level = levelInput.getText();
            numMines = minesNoInput.getText();
            time = timeInput.getText();
            superMine = superMineInput.getText();
        }



        // Write the scenario data to a file
        try {
            // Create the file name
            String fileName = "SCENARIO-" + scenario + ".txt";

            // Create the file object
            File file = new File("medialab/" + fileName);

            // Create the FileWriter object
            FileWriter writer = new FileWriter(file);

            // Write the scenario data to the file
            writer.write(level + "\n");
            writer.write(numMines + "\n");
            writer.write(time + "\n");
            writer.write(superMine + "\n");

            // Close the FileWriter object
            writer.close();

            // Print a success message
            throwAlert("FILE CREATED", "Scenario with ID " + scenario + " was created successfully.");

        } catch (IOException e) {
            // Print an error message
            throwAlert("FILE NOT CREATED", "Scenario with ID " + scenario + " could not be generated.");
        }
    }

    public void showMines() throws IOException {
        for (int i = 0; i < mf.minefieldWidth; i++) {
            for (int j = 0; j < mf.minefieldWidth; j++) {
                if (mf.minefield[i][j].t == Type.MINE || mf.minefield[i][j].t == Type.SUPERMINE) {
                    mf.minefield[i][j].setImage();
                }
            }
        }
        mf.gameEnded = true;
        timeline.stop();
        this.pastRounds.add(new Quadrable<>(mf.numMinesatStart, mf.leftClickTries, durationInSecondsLeft, "Computer"));
        updatePrevRoundsDetails();
    }

    public void solutionAction(ActionEvent actionEvent) throws IOException {
        showMines();
    }
}