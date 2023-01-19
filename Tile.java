import enums.TileImages;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Tile extends JLabel {

    private boolean clicked = false;
    private boolean bomb = false;
    private boolean starting = false;
    private boolean flagged = false;
    private int bombAround = 0;
    private int xPos;
    private int yPos;
    private ImageIcon tileImage;

    public Tile(int x, int y, int size) {
        this.xPos = x;
        this.yPos = y;

        this.setBorder(new LineBorder(Color.BLACK, 1));
        this.setPreferredSize(new Dimension(size, size));
        this.setBackground(Color.GRAY);
        this.setOpaque(true);
    }

    public void displayImage(){

        if (this.isFlagged() && !this.isBomb()){
            Image image = new ImageIcon(String.format("images/wrong-flag.png", this.bombAround)).getImage().getScaledInstance(this.getWidth(), this.getHeight(), Image.SCALE_DEFAULT);
            this.tileImage = new ImageIcon(image);
        }
        if (this.tileImage != null && (this.bombAround == 0 || this.isFlagged())) {
            this.setIcon(this.tileImage);
        }

    }
    public boolean isClicked() {
        return clicked;
    }
    public void setClicked(boolean clicked) {
        this.clicked = clicked;

        if (this.bombAround > 0) {
            Image image = new ImageIcon(String.format("images/Numbers/number-%d.png", this.bombAround)).getImage().getScaledInstance(this.getWidth(), this.getHeight(), Image.SCALE_DEFAULT);
            this.tileImage = new ImageIcon(image);
            this.setIcon(this.tileImage);
        } else {
            this.setBackground(Color.lightGray);
        }
    }
    public boolean isBomb() {
        return bomb;
    }

    public void setBomb(boolean bomb) {
        this.bomb = bomb;
        Image initialImage = new ImageIcon(String.format("images/coffee-duke.png")).getImage().getScaledInstance(this.getWidth(), this.getHeight(), Image.SCALE_DEFAULT);
        this.tileImage = new ImageIcon(initialImage);
    }

    public boolean isStarting() {
        return starting;
    }

    public void setStarting(boolean starting) {
        this.starting = starting;
    }

    public boolean isFlagged() {
        return flagged;
    }

    public void setFlagged(boolean flagged) {
        this.flagged = flagged;

        if (flagged) {
            Image initialImage = new ImageIcon(String.format("images/flag.png")).getImage().getScaledInstance(this.getWidth(), this.getHeight(), Image.SCALE_DEFAULT);
            this.setIcon(new ImageIcon(initialImage));
        } else {
            this.setIcon(null);
        }
    }

    public int getBombAround() {
        return bombAround;
    }

    public void setBombAround(int bombAround) {
        this.bombAround = bombAround;
        this.tileImage = new ImageIcon(String.format("images//Numbers//number-%d.png", bombAround));
    }

    public int getxPos() {
        return xPos;
    }

    public void setxPos(int xPos) {
        this.xPos = xPos;
    }

    public int getyPos() {
        return yPos;
    }

    public void setyPos(int yPos) {
        this.yPos = yPos;
    }

    public void checkBombsAround(Tile[][] grid) {

        int[][] offsets = {{0, 1}, {0, -1}, {-1, 0}, {1, 0}, {-1, 1}, {-1, -1}, {1, -1}, {1, 1}};

        int bombs = 0;

        for (int[] offset : offsets) {
            try {
                if (grid[this.yPos + offset[1]][this.xPos + offset[0]].isBomb()) {
                    bombs++;
                }
            } catch (IndexOutOfBoundsException e) {
                continue;
            }
        }
        this.setBombAround(bombs);
    }
}
