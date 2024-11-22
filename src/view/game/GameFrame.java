package view.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Objects;

import controller.GameController;
import model.MapMatrix;
import view.FrameUtil;



public class GameFrame extends JFrame {


    private GameController controller;
    private JButton restartBtn;
    private JButton loadBtn;

    private JButton upBtn;
    private JButton downBtn;
    private JButton rightBtn;
    private JButton leftBtn;
    //四个操控按钮创建:上下左右的移动
    private JButton undoBtn;
    //撤销当前步（悔步）按钮
    private JLabel stepLabel;
    private GamePanel gamePanel;


    public GameFrame(int width, int height, MapMatrix mapMatrix) {
        this.setTitle("2024 CS109 Project Demo");
        this.setLayout(null);
        this.setSize(width, height);
        gamePanel = new GamePanel(mapMatrix);
        gamePanel.setLocation(30, height / 2 - gamePanel.getHeight() / 2);
        this.add(gamePanel);
        this.controller = new GameController(gamePanel, mapMatrix);
        gamePanel.setController(controller);


        ImageIcon down = new ImageIcon(Objects.requireNonNull(GameFrame.class.getResource("move down.png")));
        ImageIcon up = new ImageIcon(Objects.requireNonNull(GameFrame.class.getResource("move up.png")));
        ImageIcon left = new ImageIcon(Objects.requireNonNull(GameFrame.class.getResource("move left.png")));
        ImageIcon right = new ImageIcon(Objects.requireNonNull(GameFrame.class.getResource("move right.png")));
        ImageIcon undo = new ImageIcon(Objects.requireNonNull(GameFrame.class.getResource("move undo.png")));
        //导入按键图片

        this.restartBtn = FrameUtil.createButton(this, "Restart", new Point(gamePanel.getWidth() + 110, 120), 80, 50);
        this.loadBtn = FrameUtil.createButton(this, "Load", new Point(gamePanel.getWidth() + 110, 180), 80, 50);
        this.stepLabel = FrameUtil.createJLabel(this, "Start", new Font("serif", Font.ITALIC, 22), new Point(gamePanel.getWidth() + 130, 70), 180, 50);
        this.upBtn = FrameUtil.createButton(this, "", new Point(gamePanel.getWidth() + 130, 250), 50, 50);
        this.downBtn = FrameUtil.createButton(this, "", new Point(gamePanel.getWidth() + 130, 320), 50, 50);
        this.leftBtn = FrameUtil.createButton(this, "", new Point(gamePanel.getWidth() + 60, 320), 50, 50);
        this.rightBtn = FrameUtil.createButton(this, "", new Point(gamePanel.getWidth() + 200, 320), 50, 50);
        this.undoBtn = FrameUtil.createButton(this, "", new Point(gamePanel.getWidth() + 130, 20), 50, 50);
        gamePanel.setStepLabel(stepLabel);

        this.upBtn.setIcon(up);
        this.downBtn.setIcon(down);
        this.leftBtn.setIcon(left);
        this.rightBtn.setIcon(right);
        this.undoBtn.setIcon(undo);
        //给按钮渲染图片
        this.upBtn.setPreferredSize(new Dimension(up.getIconWidth(), up.getIconHeight()));
        this.downBtn.setPreferredSize(new Dimension(down.getIconWidth(), down.getIconHeight()));
        this.leftBtn.setPreferredSize(new Dimension(left.getIconWidth(), left.getIconHeight()));
        this.rightBtn.setPreferredSize(new Dimension(right.getIconWidth(), right.getIconHeight()));
        this.undoBtn.setPreferredSize(new Dimension(right.getIconWidth(), right.getIconHeight()));
        //使得按钮与图片契合

        this.restartBtn.addActionListener(e -> {
            controller.restartGame();
            gamePanel.requestFocusInWindow();//enable key listener
        });
        this.loadBtn.addActionListener(e -> {
            String string = JOptionPane.showInputDialog(this, "Input path:");
            System.out.println(string);
            gamePanel.requestFocusInWindow();//enable key listener
        });
        this.upBtn.addActionListener(l -> {
            gamePanel.doMoveUp();
            gamePanel.requestFocusInWindow();//使键盘夺回焦点，避免键盘移动失灵
        });
        this.downBtn.addActionListener(l -> {
            gamePanel.doMoveDown();
            gamePanel.requestFocusInWindow();
        });
        this.leftBtn.addActionListener(l -> {
            gamePanel.doMoveLeft();
            gamePanel.requestFocusInWindow();
        });
        this.rightBtn.addActionListener(l -> {
            gamePanel.doMoveRight();
            gamePanel.requestFocusInWindow();
        });
        this.undoBtn.addActionListener(l -> {
            gamePanel.afterUndo();
            gamePanel.requestFocusInWindow();
        });
        //todo: add other button here
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        this.addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent e) {
                gamePanel.requestFocusInWindow();
            }
        });//此方法能在键盘失灵时强制使其再次获得焦点
    }


}
