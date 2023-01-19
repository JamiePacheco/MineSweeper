import javax.sound.midi.SysexMessage;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

public class GridPanel extends JPanel implements MouseListener{

    public final int SIZE = 800;
    private int rows;
    private int columns;
    private int bombs;
    private int tilesLeft;
    private boolean running;
    private boolean gameStatus = true;
    private Tile startingTile;
    private Tile[][] grid;

    public GridPanel(int rows, int columns, int bombs){
        this.rows = rows;
        this.columns = columns;
        this.bombs = bombs;
        this.tilesLeft = columns * rows;
        this.running = true;

        this.setLayout(new GridLayout(rows, columns, 0, 0));
        this.addMouseListener(this);
        this.setFocusable(true);

        setGrid(rows, columns);
    }
    public void startGame(){
        generateBombs();
        generateTileNumbers();
    }

    public void restartGame(int size){

        this.rows = size;
        this.columns = size;

        System.out.println(String.format("Rows: %d\nColumns:%d", this.rows, this.columns));

        setGrid(rows, columns);
        ((MyFrame)SwingUtilities.getAncestorOfClass(MyFrame.class, this)).resetFlags();
        ((MyFrame)SwingUtilities.getAncestorOfClass(MyFrame.class, this)).resetTime();

        this.tilesLeft = columns * rows;
        this.startingTile = null;
        this.gameStatus = true;
        this.running = true;

        this.setFocusable(true);
    }

    public boolean checkGameStatus(){
        if (this.tilesLeft == this.bombs && this.gameStatus == true) {
            running = false;
            this.setFocusable(false);
            System.out.println("YOU WON!");
            revealBoard();
        } else if (!this.gameStatus) {
            running = false;
            this.setFocusable(false);
            System.out.println("YOU LOST!");
            revealBoard();
        }

        return this.gameStatus;
    }

    public void setGrid(int rows, int columns){
        this.removeAll();
        this.setLayout(new GridLayout(rows, columns, 0, 0));
        this.grid = new Tile[rows][columns];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++){
                grid[i][j] = new Tile(j, i, this.SIZE/columns);
                this.add(grid[i][j]);
            }
        }
    }

    public void revealBoard(){

        ((MyFrame)SwingUtilities.getAncestorOfClass(MyFrame.class, this)).toggleTimer();

        for(int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                Tile tile = grid[i][j];
                tile.displayImage();
            }
        }

    }

    public boolean openBoard(Tile startingTile){

        if (startingTile.isBomb()) {
            return false;
        }

        if (startingTile.getBombAround() != 0) {
            return true;
        }

        LinkedList<Tile> tileQueue = new LinkedList<>();
        LinkedList<Tile> tilesChecked = new LinkedList<>();
        tileQueue.add(startingTile);
        tilesChecked.add(startingTile);

        int[][] offsets = {{0,1}, {0,-1},{-1,0},{1,0},{-1,1},{-1,-1},{1,-1},{1,1}};

        int tileFlags = 0;

        while(tileQueue.size() > 0) {
            Tile tile = tileQueue.pop();
//            System.out.println(String.format("Tile at [%d, %d]", tile.getxPos(), tile.getyPos()));
            for (int[] offset : offsets) {
                try {
                    Tile offsetTile = grid[tile.getyPos() + offset[1]][tile.getxPos() + offset[0]];
                    if (!tilesChecked.contains(offsetTile) && !offsetTile.isClicked()) {
                        if (offsetTile.getBombAround() == 0) {
                            tileQueue.add(offsetTile);
                        }

                        if (offsetTile.isFlagged()) {
                            offsetTile.setFlagged(false);
                            tileFlags++;
                        }

                        offsetTile.setClicked(true);
                        tilesLeft--;
                        tilesChecked.add(offsetTile);
                    }
                } catch (IndexOutOfBoundsException exp) {
                }
            }
        }

        ((MyFrame)SwingUtilities.getAncestorOfClass(MyFrame.class, this)).setFlags(tileFlags);

        return true;
    }

    public void generateBombs(){
        Random rand = new Random();
        ArrayList<Tile> validTiles = new ArrayList<>();

        int[] startingCords = {startingTile.getxPos(), startingTile.getyPos()};
        int[][] offsets = {{0,1}, {0,-1},{-1,0},{1,0},{-1,1},{-1,-1},{1,-1},{1,1}};

        for (Tile[] rows : grid) {
            validTiles.addAll(Arrays.stream(rows).toList());
        }

        for (int[] offset : offsets) {
            try{
                validTiles.remove(grid[startingCords[1] + offset[1]][startingCords[0] + offset[0]]);
            } catch (IndexOutOfBoundsException exception){
                continue;
            }
        }
        validTiles.remove(this.startingTile);

        for (int i = 0; i < bombs && i < this.tilesLeft; i++) {
            Tile bombTile = validTiles.get(rand.nextInt(validTiles.size()));

            bombTile.setBomb(true);
            validTiles.remove(bombTile);
        }
    }

    public void generateTileNumbers(){
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.columns; j++) {
                Tile tile = grid[i][j];
                if (!tile.isBomb() && !tile.isStarting()) {
                    tile.checkBombsAround(this.grid);
                }
            }
        }
    }

    public boolean leftClick(Point point){

        Tile tile = (Tile) this.getComponentAt(point);

        if (tile.isFlagged()) {
            return true;
        }

        if (tile.isBomb()) {
            return false;
        }

        if (!tile.isClicked()) {
            if (this.startingTile == null) {
                tile.setStarting(true);
                this.startingTile = tile;
                startGame();
            }
            tile.setClicked(true);
            tilesLeft--;
        }

        boolean openResult = openBoard(tile);

        return openResult;
    }

    public void rightClick(Point point) {
        Tile tile = (Tile) this.getComponentAt(point);

        if (tile.isClicked()) {
            return;
        }
        if (tile.isFlagged()) {
            tile.setFlagged(false);
            ((MyFrame)SwingUtilities.getAncestorOfClass(MyFrame.class, this)).setFlags(1);
        } else if (((MyFrame)SwingUtilities.getAncestorOfClass(MyFrame.class, this)).getFlags() > 0){
            tile.setFlagged(true);
            ((MyFrame)SwingUtilities.getAncestorOfClass(MyFrame.class, this)).setFlags(-1);
        }
    }

    public boolean getRunning(){
        return this.running;
    }
    public boolean getGameStatus(){
        return this.gameStatus;
    }

    public void changeBombAmount(int bombs) {
        this.bombs = bombs;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (running) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                this.gameStatus = leftClick(e.getPoint());
                this.checkGameStatus();
            } else if (e.getButton() == MouseEvent.BUTTON3) {
                rightClick(e.getPoint());
            }
        } else {
            restartGame(this.columns);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public String toString(){
        return String.format("Game Status: %s",this.gameStatus);
    }
}