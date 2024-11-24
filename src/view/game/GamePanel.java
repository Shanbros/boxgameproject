package view.game;

import controller.GameController;
import model.Direction;
import model.MapMatrix;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * It is the subclass of ListenerPanel, so that it should implement those four methods: do move left, up, down ,right.
 * The class contains a grids, which is the corresponding GUI view of the matrix variable in MapMatrix.
 */
public class GamePanel extends ListenerPanel {

    private GridComponent[][] grids;
    private MapMatrix model;
    private GameController controller;
    private JLabel stepLabel;
    private int steps;
    private final int GRID_SIZE = 50;
    private int[][] move = new int[999999][2];
    //一维数组值代表玩家的移动步数，用于悔步
    //二维数组值第一位[0]储存玩家的移动数据：0 代表无数据，1 代表右移,2 代表左移,3 代表上移，4 代表下移
    //二维数组值第二位[1]储存箱子的移动数据：0 代表无数据，1 代表被移动
    private Hero hero;


    public GamePanel(MapMatrix model) {
        this.setVisible(true);
        this.setLayout(null);
        this.setFocusable(true);
        this.setSize(model.getWidth() * GRID_SIZE + 4, model.getHeight() * GRID_SIZE + 4);
        this.model = model;
        this.grids = new GridComponent[model.getHeight()][model.getWidth()];
        initialGame();
    }

    public void initialGame() {
        this.steps = 0;
        for (int i = 0; i < grids.length; i++) {
            for (int j = 0; j < grids[i].length; j++) {
                //Units digit maps to id attribute in GridComponent. (The no change value)
                grids[i][j] = new GridComponent(i, j, model.getId(i, j) % 10, this.GRID_SIZE);
                grids[i][j].setLocation(j * GRID_SIZE + 2, i * GRID_SIZE + 2);
                //Ten digit maps to Box or Hero in corresponding location in the GridComponent. (Changed value)
                switch (model.getId(i, j) / 10) {
                    case 1:
                        grids[i][j].setBoxInGrid(new Box(GRID_SIZE - 10, GRID_SIZE - 10));
                        break;
                    case 2:
                        this.hero = new Hero(GRID_SIZE - 16, GRID_SIZE - 16, i, j);
                        grids[i][j].setHeroInGrid(hero);
                        break;
                }
                this.add(grids[i][j]);
            }
        }
        this.repaint();
    }

    @Override
    public void doMoveRight() {
        System.out.println("Click RIGHT");
        if (controller.doMove(hero.getRow(), hero.getCol(), Direction.RIGHT)) {
            this.afterMove();
            move[steps][0] = 1;
        }
    }

    @Override
    public void doMoveLeft() {
        System.out.println("Click LEFT");
        if (controller.doMove(hero.getRow(), hero.getCol(), Direction.LEFT)) {
            this.afterMove();
            move[steps][0] = 2;
        }
    }

    @Override
    public void doMoveUp() {
        System.out.println("Click UP");
        if (controller.doMove(hero.getRow(), hero.getCol(), Direction.UP)) {
            this.afterMove();
            move[steps][0] = 3;
        }
    }

    @Override
    public void doMoveDown() {
        System.out.println("Click DOWN");
        if (controller.doMove(hero.getRow(), hero.getCol(), Direction.DOWN)) {
            this.afterMove();
            move[steps][0] = 4;
        }
    }

//    public void doMoveTopRight() {
//        System.out.println("Click TopRIGHT");
//        if (controller.doMove(hero.getRow(), hero.getCol(), Direction.TopRIGHT)) {
//            this.afterMove();
//        }
//    }
//
//    public void doMoveTopLeft() {
//        System.out.println("Click TopLEFT");
//        if (controller.doMove(hero.getRow(), hero.getCol(), Direction.TopLEFT)) {
//            this.afterMove();
//        }
//    }
//
//    public void doMoveBottomLeft() {
//        System.out.println("Click BottomLEFT");
//        if (controller.doMove(hero.getRow(), hero.getCol(), Direction.BottomLEFT)) {
//            this.afterMove();
//        }
//    }
//
//    public void doMoveBottomRight() {
//        System.out.println("Click BottomRIGHT");
//        if (controller.doMove(hero.getRow(), hero.getCol(), Direction.BottomRIGHT)) {
//            this.afterMove();
//        }
//    }

    public void afterMove() {
        this.steps++;
        this.stepLabel.setText(String.format("Step: %d", this.steps));
    }

    public void setStepLabel(JLabel stepLabel) {
        this.stepLabel = stepLabel;
    }

    public void afterUndo() {
        if (steps > 0) {
            Direction d = Direction.NULL;
            if (move[steps][0] == 1) {
                d = Direction.RIGHT;
            } else if (move[steps][0] == 2) {
                d = Direction.LEFT;
            } else if (move[steps][0] == 3) {
                d = Direction.UP;
            } else if (move[steps][0] == 4) {
                d = Direction.DOWN;
            }//通过move数列获取玩家移动方向
            Direction D = oppositeDirection(d);//获取玩家移动的反方向，以此推得玩家移动前位置
            int row = hero.getRow();
            int col = hero.getCol();//玩家位置，也是箱子被推前的位置
            int tRow = row + D.getRow();
            int tCol = col + D.getCol();////玩家之前的位置
            int boxRow = row + d.getRow();
            int boxCol = col + d.getCol();//箱子被推后的位置
            int[][] map = model.getMatrix();
            GridComponent currentGrid1 = getGridComponent(row, col);
            GridComponent targetGrid1 = getGridComponent(tRow, tCol);
            model.getMatrix()[row][col] -= 20;
            model.getMatrix()[tRow][tCol] += 20;
            Hero h = currentGrid1.removeHeroFromGrid();
            targetGrid1.setHeroInGrid(h);
            h.setRow(tRow);
            h.setCol(tCol);
            //以上代码从doMove方法中摘取，作用是纯粹用来使Hero反方向移动
            // 有充分理由不直接引用doMove(其为boolean方法，无法直接用)或者衍生方法(其会应用afterMove方法，导致step发生变化，从而使悔步方法出现步数判断混乱问题)
            if (move[steps][1] == 1) {
                model.getMatrix()[boxRow][boxCol] -= 10;
                model.getMatrix()[row][col] += 10;//更新箱子地图位置
                GridComponent currentGrid2 = getGridComponent(boxRow, boxCol);
                GridComponent targetGrid2 = getGridComponent(row, col);
                Box b = currentGrid2.removeBoxFromGrid();
                targetGrid2.setBoxInGrid(b);//更新箱子模型位置
            }//如果玩家移动的同时推了箱子，那么箱子也跟着反方向移动
            move[steps][0] = 0;
            move[steps][1] = 0;//去除这一步的move数据，因为悔步会去除这一步留下的所有痕迹
            this.steps--;
            this.stepLabel.setText(String.format("Step: %d", this.steps));
        }
    }
    //悔步方法，用于让玩家悔步

    public void changeBoxMoveArray(Direction direction) {
        steps ++;
        move[steps][1] = 1;
        steps --;
    }

    public Direction oppositeDirection(Direction direction) {
        if (direction == Direction.RIGHT) {
            return Direction.LEFT;
        } else if (direction == Direction.LEFT) {
            return Direction.RIGHT;
        } else if (direction == Direction.UP) {
            return Direction.DOWN;
        } else if (direction == Direction.DOWN) {
            return Direction.UP;
        } else {
            return Direction.NULL;
        }
    }//此方法用于获取反方向

    public void setController(GameController controller) {
        this.controller = controller;
    }

    public GridComponent getGridComponent(int row, int col) {
        return grids[row][col];
    }
}
