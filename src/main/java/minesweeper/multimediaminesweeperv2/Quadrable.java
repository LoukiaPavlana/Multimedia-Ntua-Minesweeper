package minesweeper.multimediaminesweeperv2;

public class Quadrable<T1, T2, T3, T4> {

    private final T1 x;
    private final T2 y;
    private final T3 z;
    private final T4 w;

    public Quadrable(T1 x, T2 y, T3 z, T4 w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public T1 getX() { return x; }
    public T2 getY() { return y; }
    public T3 getZ() { return z; }
    public T4 getW() { return w; }
}