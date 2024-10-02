package minesweeper.multimediaminesweeperv2;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.paint.Color;

enum Type {
    MINE,
    SUPERMINE,
    N1,
    N2,
    N3,
    EMPTY
}
public class Cell extends Button {
    public int i, j;
    int nearMines;
    boolean flagged, exposed;
    Type t;
    Image img;

    /**
     * constructor for cell
     * @param i position i of cell
     * @param j position j of cell
     *
     */
    public Cell(int i, int j) {
        super();
        setBackground(Background.fill(Color.WHITE));
        setBorder(Border.stroke(Color.BLACK));
        this.i = i;
        this.j = j;
        this.nearMines = 0;
        this.flagged = false;
        this.t = Type.EMPTY;
        this.exposed = false;
        this.img = null;
        setGraphic(null);
        setMaxSize(100.0, 100.0);
        setText("");
    }

    /**
     * Returns the type of this cell.
     *
     * @return the type of this cell
     */
    public Type getType() {
        return this.t;
    }

    /**
     * Sets the image associated with this cell based on its type.
     */
    public void setImage() {
        if (this.t == Type.EMPTY) {
            this.img = null;
            setGraphic(null);
            setText("0");
            setStyle("-fx-text-fill: pink;");
        }
        else if (this.t == Type.MINE) {
            this.img = new Image("C:/Users/louki/IdeaProjects/MultiMediaMinesweeper/src/pictures/mine.jpg");
            ImageView iv = new ImageView(img);
            iv.setFitWidth(20.0);
            iv.setFitHeight(20.0);
            setGraphic(iv);
        }
        else if (this.t == Type.SUPERMINE) {
            this.img = new Image("C:/Users/louki/IdeaProjects/MultiMediaMinesweeper/src/pictures/supermine.png");
            ImageView iv = new ImageView(img);
            iv.setFitWidth(20.0);
            iv.setFitHeight(20.0);
            setGraphic(iv);
        }
        else if (this.t == Type.N1) {
            this.img = null;
            setGraphic(null);
            setText("1");
            setStyle("-fx-text-fill: blue;");
        }
        else if (this.t == Type.N2) {
            this.img = null;
            setGraphic(null);
            setText("2");
            setStyle("-fx-text-fill: green;");

        }
        else if (this.t == Type.N3) {
            this.img = null;
            setGraphic(null);
            setText("3");
            setStyle("-fx-text-fill: red;");
        }

    }

    /**
     * Sets the image associated with this cell to be null and unflags the cell.
     */
    public void setImgUnflagged() {
        this.img = null;
        setGraphic(null);
        this.flagged = false;
    }

    /**
     * Sets the image flag associated with this cell to be null and flags the cell.
     */
    public void setImgFlagged() {
        this.img = new Image("C:/Users/louki/IdeaProjects/MultiMediaMinesweeper/src/pictures/Black_flag.png");
        ImageView iv = new ImageView(img);
        iv.setFitWidth(30.0);
        iv.setFitHeight(30.0);
        setGraphic(iv);
        this.flagged = true;
    }

}