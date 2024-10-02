package minesweeper.multimediaminesweeperv2;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.util.ArrayList;

public class MineField {
    int rightClickTries, leftClickTries, minefieldWidth, numMinesatStart, cellsUncovered, totalCells, exposedCells, flagsNo;
    boolean hasSuperMine, gameEnded;
    Cell[][] minefield;
    private Main main;

    public MineField(int gridSize, int minesNo, boolean hasSuperMine, Main main) {
        this.rightClickTries = 0;
        this.leftClickTries = 0;
        this.gameEnded = false;

        this.minefieldWidth = gridSize;
        this.numMinesatStart = minesNo;
        this.hasSuperMine = hasSuperMine;
        this.cellsUncovered = 0;
        this.totalCells = gridSize * gridSize;
        this.exposedCells = 0;
        this.flagsNo = 0;
        this.minefield = new Cell[gridSize][gridSize];
        this.main = main;
    }


    public void makeMinefield(){
        for (int i = 0; i < this.minefieldWidth; i++) {
            for (int j = 0; j < this.minefieldWidth ; j++ ){
                minefield[i][j] = new Cell(i, j);            // newCellArray[x][y] is one cell
                minefield[i][j].setOnMouseClicked(e -> {
                    try {
                        onClick(e);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                });
            }
        }
    }

    public void onClick(MouseEvent e) throws IOException {
        if (!gameEnded) {
            if (e.getButton() == MouseButton.PRIMARY) {
                leftClickTries++;

                Cell c = (Cell) e.getSource();
                expose(c.i, c.j);
            }
            else {
                Cell c = (Cell) e.getSource();

                if (minefield[c.i][c.j].exposed) return;

                if (!minefield[c.i][c.j].flagged && flagsNo < numMinesatStart) {
                    minefield[c.i][c.j].setImgFlagged();
                    flagsNo++;
                    this.rightClickTries++;
                    if (minefield[c.i][c.j].getType() == Type.SUPERMINE && this.rightClickTries < 5) {

                    }
                }
                else {
                    minefield[c.i][c.j].setImgUnflagged();
                    flagsNo--;
                }
                main.MarkedTilesLabel.setText("Flags Number: " + flagsNo);
            }
        }
    }

    public void expose(int row, int col) throws IOException {
        if (minefield[row][col].exposed) return;

        // If the current cell is a mine, return
        if (minefield[row][col].t == Type.MINE || minefield[row][col].t == Type.SUPERMINE) {
            minefield[row][col].setImage();
            this.gameEnded = true;
            main.pastRounds.add(new Quadrable<>(numMinesatStart, leftClickTries, main.durationInSecondsLeft, "Computer"));
            main.updatePrevRoundsDetails();
            main.showMines();
            main.timeline.stop();
            return;
        }

        int neighborMineCount = getNeighborMineCount(row, col);
        if (neighborMineCount == 3) minefield[row][col].t = Type.N3;
        if (neighborMineCount == 2) minefield[row][col].t = Type.N2;
        if (neighborMineCount == 1) minefield[row][col].t = Type.N1;
        // Expose the current cell
        minefield[row][col].exposed = true;
        exposedCells++;
        minefield[row][col].setImage();

        if (neighborMineCount == 0) {
            minefield[row][col].exposed = true;
            exposedCells++;
            minefield[row][col].setImage();

            // If the current cell has no neighboring mines, expose all its neighbors
            for (int i = row - 1; i <= row + 1; i++) {
                for (int j = col - 1; j <= col + 1; j++) {
                    if (i >= 0 && i < minefield.length && j >= 0 && j < minefield[0].length) {
                        expose(i, j); // recursive call to expose neighboring cells
                    }
                }
            }
        }

        if (exposedCells == numMinesatStart) {
            gameEnded = true;
            main.pastRounds.add(new Quadrable<>(numMinesatStart, leftClickTries, main.durationInSecondsLeft, "Player"));
            main.updatePrevRoundsDetails();
        }
    }

    public int getNeighborMineCount(int row, int col) {
        int count = 0;

        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                if (i >= 0 && i < minefield.length && j >= 0 && j < minefield[0].length && (minefield[i][j].t == Type.MINE || minefield[i][j].t == Type.SUPERMINE)) {
                    count++;
                }
            }
        }

        return count;
    }


    void addMines() {
        ArrayList<Triplet<Integer, Integer, Integer>> mines = new ArrayList();
        boolean superMineSet = false;
        for (int i = 0; i < numMinesatStart; ++i){
            int randomX = (int)(Math.random() * minefieldWidth);
            int randomY = (int)(Math.random() * minefieldWidth);
            if (minefield[randomX][randomY].t == Type.MINE || minefield[randomX][randomY].t == Type.SUPERMINE) { i--; }  // try again
            else {
                if (!superMineSet) {
                    minefield[randomX][randomY].t = Type.SUPERMINE;
                    superMineSet = true;
                    mines.add(new Triplet<>(randomX, randomY, 1));
                }
                else {
                    minefield[randomX][randomY].t = Type.MINE;
                    mines.add(new Triplet<>(randomX, randomY, 0));
                }
            }
        }

        // write into a file the mines arraylist
    }
};
