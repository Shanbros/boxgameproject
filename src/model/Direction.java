package model;

public enum Direction {
    LEFT(0, -1), UP(-1, 0), RIGHT(0, 1), DOWN(1, 0),
    TopLEFT(-1, -1), TopRIGHT(-1, 1), BottomLEFT(1, -1), BottomRIGHT(1, 1);
    //添加斜向上与斜向下方向，后续可开发斜行模式
    private final int row;
    private final int col;

    Direction(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}