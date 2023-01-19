import enums.GridSizes;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class MyFrame extends JFrame implements PropertyChangeListener, ActionListener {
    private GridPanel gridPanel;
    private JLayeredPane mainPanel;
    private JLabel gameStatusLabel;
    private JLabel gameTimerLabel;
    private JLabel gameFlagsLabel;
    private JComboBox<GridSizes> gridSizeSelection;
    private Font myFont = new Font("Arial", Font.BOLD, 20);
    private final int WIDTH = 906;
    private final int HEIGHT = 956;
    //Game variables
    private int bombs = 100;
    private int rows = 20;
    private int columns = 20;
    private int flags = bombs;
    private int time = 0;
    private Timer timer;
    public MyFrame(){
        this.timer = new Timer(1000, this);
        this.mainPanel = new JLayeredPane();
        this.mainPanel.setBounds(0, 0, WIDTH, HEIGHT);
        this.mainPanel.setBackground(Color.lightGray);
        this.setLayout(null);

        this.gridPanel = new GridPanel(rows, columns, bombs);
        this.gridPanel.setBounds(45, 75 , 800, 800);
        this.gridPanel.addPropertyChangeListener(this);

        JLabel gridSizeLabel = new JLabel("Size: ");
        gridSizeLabel.setFont(myFont);
        gridSizeLabel.setBounds(45, 40, 60, 30);
        gridSizeLabel.setHorizontalAlignment(JLabel.LEFT);

        this.gridSizeSelection = new JComboBox<>(GridSizes.values());
        this.gridSizeSelection.addActionListener(this);
        this.gridSizeSelection.setFont(myFont);
        this.gridSizeSelection.setBounds(100, 40, 150, 30);
        this.gridSizeSelection.setBorder(new LineBorder(Color.BLACK, 1));
        this.gridSizeSelection.setFocusable(false);

        this.gameStatusLabel = new JLabel();
        this.gameStatusLabel.setFont(myFont);
        this.gameStatusLabel.setHorizontalAlignment(JLabel.CENTER);
        this.gameStatusLabel.setBounds(WIDTH/2 - 100,40,200, 30);

        this.gameFlagsLabel = new JLabel("Flags: " + this.flags);
        this.gameFlagsLabel.setFont(myFont);
        this.gameFlagsLabel.setHorizontalAlignment(JLabel.RIGHT);
        this.gameFlagsLabel.setBounds(670,40,100, 30);

        this.gameTimerLabel = new JLabel("0:00");
        this.gameTimerLabel.setFont(myFont);
        this.gameTimerLabel.setHorizontalAlignment(JLabel.RIGHT);
        this.gameTimerLabel.setBounds(770,40,75, 30);

        this.setTitle("Duke Sweeper");
        this.setSize(WIDTH, HEIGHT);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setLayout(null);
        this.setResizable(false);
        this.getContentPane().setBackground(Color.LIGHT_GRAY);

        mainPanel.add(gameFlagsLabel);
        mainPanel.add(gameTimerLabel);
        mainPanel.add(gameStatusLabel);
        mainPanel.add(gridPanel);
        mainPanel.add(gridSizeLabel);
        mainPanel.add(gridSizeSelection);

        this.add(mainPanel);
        this.setVisible(true);
    }
    public String formatGameTime(){
        return String.format("%s:%s", this.time / 60, this.time % 60 < 10 ? "0" + this.time % 60 : String.valueOf(this.time % 60));
    }
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        System.out.println(evt.getPropertyName());
        if (evt.getPropertyName().equals("focusable")) {
            System.out.println("CHANGE");
            if (!this.gridPanel.getRunning()) {
                this.gameStatusLabel.setText((this.gridPanel.getGameStatus()) ? "YOU WON" : "YOU LOST");
                this.gameStatusLabel.setForeground((this.gridPanel.getGameStatus()) ? new Color(0, 145, 0) : new Color(150, 0, 0));
            } else {
                this.gameStatusLabel.setText("");
            }
        }
    }
    @Override
    public void actionPerformed(ActionEvent e){
        if (e.getSource() == this.timer) {
            if (this.gridPanel.getRunning()) {
                this.time++;
                this.gameTimerLabel.setText(formatGameTime());
            }
        }
        if (e.getSource() == this.gridSizeSelection) {
            changeDimensions((GridSizes) this.gridSizeSelection.getSelectedItem());
        }
    }
    public void setFlags(int flags) {
        this.flags += flags;
        this.gameFlagsLabel.setText("Flags: " + this.flags);
    }
    public void resetFlags(){
        this.flags = this.bombs;
        this.gameFlagsLabel.setText("Flags: " + this.flags);
    }
    public int getFlags() {
        return this.flags;
    }
    public void toggleTimer(){
        if (this.timer.isRunning()) {
            this.timer.stop();
        } else {
            this.timer.start();
        }
    }
    public void resetTime(){
        this.gameTimerLabel.setText("0:00");
        this.time = 0;
    }
    public void changeDimensions(GridSizes gridSize){
        this.timer.start();
        this.bombs = gridSize.bombs;
        this.resetFlags();
        this.gridPanel.changeBombAmount(gridSize.bombs);
        this.gridPanel.restartGame(gridSize.size);
    }
}