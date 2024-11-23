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
    private static int count = 0;//连续推箱计数器，用于在超级模式中判定失败与否
    private static int gameResult = 1;//0 代表失败，1 代表游戏尚未结束，2 代表游戏胜利
    //超级模式（暂定）：玩家解锁连环推箱子功能，并且可以进行斜向推箱子与移动，地图上会有特殊方格

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
        } else if ((map[tRow][tCol] == 10 || map[tRow][tCol] == 12) && normalCanBoxMove(tRow, tCol, direction)) {
            //update hero in MapMatrix
            model.getMatrix()[row][col] -= 20;
            model.getMatrix()[tRow][tCol] += 20;
            normalBoxMove(tRow, tCol, direction);
            //Update hero in GamePanel
            int finalBoxRow = tRow + count * direction.getRow();
            int finalBoxCol = tCol + count * direction.getCol();
            //代表玩家连续推动的多个箱子中的最末尾的箱子被推到的位置，但在禁用连环推箱的情况下，它代表的是玩家推动的箱子将到达的位置
            Hero h = currentGrid.removeHeroFromGrid();
            targetGrid.setHeroInGrid(h);
            //Update the row and column attribute in hero
            h.setRow(tRow);
            h.setCol(tCol);
            if (map[boxRow][boxCol] == 12) {
                checkWin();
            }
            //检测玩家移动的第一个箱子是否位于目标点，如果是则进行胜利判定
            if ((map[finalBoxRow + direction.getRow()][finalBoxCol + direction.getCol()] == 1) && gameResult == 1) {
                checkLose(finalBoxRow, finalBoxCol);
            }
            //如果玩家连续推动的多个箱子中的最末尾的箱子被推到的位置与墙接触，并且游戏未结束，则进行失败判定
            //尽量不要有位于墙的死角的目标点，否则失败判定会异常
            count = 0;//重置计数器
            checkResult();
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
            count++;//检测被推箱子数量
            model.getMatrix()[row][col] -= 10;
            model.getMatrix()[tRow][tCol] += 10;//更新箱子地图位置
            GridComponent targetGrid = view.getGridComponent(tRow, tCol);
            Box b = currentGrid.removeBoxFromGrid();
            targetGrid.setBoxInGrid(b);//更新箱子模型位置
        }
    }

    public void normalBoxMove(int row, int col, Direction direction) {
        GridComponent currentGrid = view.getGridComponent(row, col);
        int tRow = row + direction.getRow();
        int tCol = col + direction.getCol();
        if (normalCanBoxMove(row, col, direction)) {
            count++;
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

    public boolean normalCanBoxMove(int row, int col, Direction direction) {
        //判定箱子是否能被推动（禁止连环推箱）
        int[][] map = model.getMatrix();
        int tRow = row + direction.getRow();
        int tCol = col + direction.getCol();
        return map[tRow][tCol] != 1 && map[tRow][tCol] !=10 && map[tRow][tCol] !=12;//检测推箱子是否会撞墙
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
            gameResult = 2;
        }
    }

    public void checkLose(int finalBoxRow, int finalBoxCol) {
        int[][] map = model.getMatrix();
        int wall = 0;
        //代表与末尾箱接触的墙面数量
        if (map[finalBoxRow + Direction.DOWN.getRow()][finalBoxCol + Direction.DOWN.getCol()] == 1) {
            wall++;
        }
        if (map[finalBoxRow + Direction.UP.getRow()][finalBoxCol + Direction.UP.getCol()] == 1) {
            wall++;
        }
        if (map[finalBoxRow + Direction.LEFT.getRow()][finalBoxCol + Direction.LEFT.getCol()] == 1) {
            wall++;
        }
        if (map[finalBoxRow + Direction.RIGHT.getRow()][finalBoxCol + Direction.RIGHT.getCol()] == 1) {
            wall++;
        }
        if (wall >= 2) {
            gameResult = 0;
        }
        //墙面接触面大于等于2，说明已被逼入死角，游戏结束
        boolean judge = false;
        //判定是否有靠近墙的目标点
        if (wall == 1 && map[finalBoxRow][finalBoxCol] != 12) {
            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[i].length; j++) {
                    if (map[i][j] == 1) {
                        if (checkValid(map, i + 1, j + 1)) {
                            judge = true;
                        } else if (checkValid(map, i + 1, j - 1)) {
                            judge = true;
                        } else if (checkValid(map, i - 1, j + 1)) {
                            judge = true;
                        } else if (checkValid(map, i - 1, j - 1)) {
                            judge = true;
                        } else if (checkValid(map, i + 1, j)) {
                            judge = true;
                        } else if (checkValid(map, i, j + 1)) {
                            judge = true;
                        } else if (checkValid(map, i, j - 1)) {
                            judge = true;
                        } else if (checkValid(map, i - 1, j)) {
                            judge = true;
                        }
                    }
                }
            }
            //遍历地图，在所有的墙周围检测是否有位于3x3范围内的目标点（不包括已被占领的目标点），如果是则游戏失败判定无效
            if (!judge) {
                gameResult = 0;
            }
        }
        //如果墙面接触面等于1并且被推动的末尾箱没有被推到目标点上，没有在墙的3x3范围内的目标点，则游戏同样结束
    }

    public void checkResult() {
        if (gameResult == 0) {
            System.out.println("You lose!");//此行代码仅用于测试，编写完失败界面可删除
            //此处用于编写失败画面


        } else if (gameResult == 2) {
            System.out.println("You win!");//此行代码仅用于测试，编写完胜利界面可删除
            //此处用于编写胜利结算画面


        }
        gameResult = 1;//重置gameResult，进行初始化
    }

    public boolean checkValid(int[][] map, int i, int j) {
        int rows = map.length;
        if (i < 0 || i >= rows) {
            return false;
        } else {
            int cols = map[i].length;
            if (j < 0 || j >= cols) {
                return false;
            }
            // 在这里判断坐标合法的情况下，目标点的值是否符合要求
            return map[i][j] == 2 || map[i][j] == 22;
        }
    }
    //此方法用于去除九宫格检查中跃出地图边界（数组越界）的检查，同时判断墙的九宫格内是否有目标点


    //todo: add other methods such as loadGame, saveGame...

}