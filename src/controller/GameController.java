package controller;

import model.Direction;
import model.MapMatrix;
import view.game.GamePanel;
import view.game.GridComponent;
import view.game.Hero;
import view.game.Box;

/**
 * It is a bridge to combine GamePanel(view) and MapMatrix(model) in one game.
 * You can design several methods about the game logic in this class.
 */
public class GameController {
    private final GamePanel view;
    private final MapMatrix model;

    public GameController(GamePanel view, MapMatrix model) {
        this.view = view;
        this.model = model;
        view.setController(this);
    }

    public void restartGame() {
        System.out.println("Do restart game here");
    }

    public boolean doMove(int row, int col, Direction direction) {
        GridComponent currentGrid = view.getGridComponent(row, col);
        //target row can column.
        int tRow = row + direction.getRow();
        int tCol = col + direction.getCol();
        int boxRow = tRow + direction.getRow();
        int boxCol = tCol + direction.getCol();
        GridComponent targetGrid = view.getGridComponent(tRow, tCol);
        int[][] map = model.getMatrix();
        if (map[tRow][tCol] == 0 || map[tRow][tCol] == 2) {
            //update hero in MapMatrix
            model.getMatrix()[row][col] -= 20;
            model.getMatrix()[tRow][tCol] += 20;
            //Update hero in GamePanel
            Hero h = currentGrid.removeHeroFromGrid();
            targetGrid.setHeroInGrid(h);
            //Update the row and column attribute in hero
            h.setRow(tRow);
            h.setCol(tCol);
            return true;
        } else if ((map[tRow][tCol] == 10 || map[tRow][tCol] == 12) && canBoxMove(tRow, tCol, direction)) {
            //update hero in MapMatrix
            model.getMatrix()[row][col] -= 20;
            model.getMatrix()[tRow][tCol] += 20;
            boxMove(tRow, tCol, direction);
            //Update hero in GamePanel
            Hero h = currentGrid.removeHeroFromGrid();
            targetGrid.setHeroInGrid(h);
            //Update the row and column attribute in hero
            h.setRow(tRow);
            h.setCol(tCol);
            if (map[boxRow][boxCol] == 12) {
                checkWin();
            }
            //检测玩家移动的第一个箱子是否位于目标点，如果是则进行胜利判定
            return true;
        } else {
            return false;
        }
    }

    public void boxMove(int row, int col, Direction direction) {
        GridComponent currentGrid = view.getGridComponent(row, col);
        int[][] map = model.getMatrix();
        int tRow = row + direction.getRow();
        int tCol = col + direction.getCol();
        if ((map[tRow][tCol] == 10 || map[tRow][tCol] == 12) && canBoxMove(tRow, tCol, direction)) {
            boxMove(tRow, tCol, direction);
        }
        //多个箱子堆叠时会发生连续推动
        if (canBoxMove(row, col, direction)) {
            model.getMatrix()[row][col] -= 10;
            model.getMatrix()[tRow][tCol] += 10;//更新箱子地图位置
            GridComponent targetGrid = view.getGridComponent(tRow, tCol);
            Box b = currentGrid.removeBoxFromGrid();
            targetGrid.setBoxInGrid(b);//更新箱子模型位置
        }
    }

    public boolean canBoxMove(int row, int col, Direction direction) {
        //判定箱子是否能被推动
        int[][] map = model.getMatrix();
        int tRow = row + direction.getRow();
        int tCol = col + direction.getCol();
        if (map[tRow][tCol] == 10 || map[tRow][tCol] == 12) {
            return canBoxMove(tRow, tCol, direction);//检测连续推箱的下一个箱子是否能被推
        } else return map[tRow][tCol] != 1;//检测推箱子是否会撞墙
    }

    public void checkWin() {
        int[][] map = model.getMatrix();
        boolean whetherWin = true;
        for (int[] intS : map) {
            for (int anInt : intS) {
                if (anInt == 10) {
                    whetherWin = false;
                    break;
                }
            }
            //遍历地图，如果没有未处于目标点的箱子则判定为胜利
        }
        if (whetherWin) {
            System.out.println("You win!");//此行代码仅用于测试，编写完胜利界面可删除
            //此处用于编写胜利结算画面
        }
    }

    public void checkLose() {

    }

    //todo: add other methods such as loadGame, saveGame...

}
