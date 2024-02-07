package org.example;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.Random;

public class PhysSim extends JPanel implements ActionListener, MouseMotionListener, MouseListener {

    static class Cell { //cell class
        int state;
        float hue;

        public Cell(int state, float hue) {
            this.state = state;
            this.hue = hue;
        }
    }

    private int cols, rows;
    private static final int w = 7;    // size of the cell
    private Cell[][] grid; // our grid a 2D array of our cell class;
    private float hueValue = 1;



    public PhysSim() {
        cols = 500 / w;
        rows = 500 / w;
        grid = make2DArray(cols, rows);
        addMouseMotionListener(this);
        addMouseListener(this);
        Timer timer = new Timer(7, this);  // ~144 fps  1000ms / desired fps
        timer.start();
    }

    private Cell[][] make2DArray(int cols, int rows) { //init array of cells with state 0
        Cell[][] arr = new Cell[cols][rows];
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                arr[i][j] = new Cell(0, 0);
            }
        }
        return arr;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                if (grid[i][j].state == 1) { //Pixel state 1 shifting hue sand.
                    g.setColor(Color.getHSBColor(grid[i][j].hue, 1, 1));
                } else if (grid[i][j].state == 2){
                    g.setColor(Color.DARK_GRAY);  //Pixel state 2 Gray Bricks.
                } else if (grid[i][j].state == 0){
                    g.setColor(Color.black);  //Pixel state 0 default state dead.
                }
                g.fillRect(i * w, j * w, w, w);

                g.setColor(Color.black); //grid outline settings
                g.drawRect(i * w, j * w, w, w);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        updateGrid();
        repaint();
    }

    private void updateGrid() {
        Cell[][] nextGrid = make2DArray(cols, rows); //update grid
        Random rand = new Random();
        //TODO: add water state and restructure code
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                //cell state logic
                if (grid[i][j].state > 0) {

                    boolean valid = j < rows - 1;
                    boolean moveRight = rand.nextBoolean();
                    if (valid && grid[i][j].state == 2) {
                        nextGrid[i][j] = new Cell(grid[i][j].state, grid[i][j].hue);
                    } else if (valid && grid[i][j + 1].state == 0) {
                        nextGrid[i][j + 1] = new Cell(grid[i][j].state, grid[i][j].hue);
                    } else if(valid && moveRight && i < cols - 1 && grid[i + 1][j + 1].state == 0 && grid[i + 1][j].state != 2) {
                        nextGrid[i + 1][j + 1] = new Cell(grid[i][j].state, grid[i][j].hue);
                    } else if (valid && !moveRight && i > 0 && grid[i - 1][j + 1].state == 0 && grid[i - 1][j].state != 2) {
                        nextGrid[i - 1][j + 1] = new Cell(grid[i][j].state, grid[i][j].hue);
                    } else {
                        nextGrid[i][j] = new Cell(grid[i][j].state, grid[i][j].hue);
                    }
                }
            }
        }
        grid = nextGrid;
    }



    public static void main(String[] args) {
        JFrame frame = new JFrame("Sand Sim");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(new PhysSim(), BorderLayout.CENTER);
        frame.setSize(514, 537);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        int mcol = (int) Math.floor((double) e.getX() / w);
        int mrow = (int) Math.floor((double) e.getY() / w);
        if(e.isShiftDown()){ //if shift is down add bricks instead of sand
            if (isInsideGrid(mcol, mrow) && (grid[mcol][mrow].state != 1 && grid[mcol][mrow].state != 2)){
                grid[mcol][mrow] = new Cell(2, hueValue);
            }
        } else { //add sand
            addSand(mcol, mrow);
        }
    }

    private void addSand(int x, int y){ //add sand to the grid
        Random r = new Random();
        int matrix = 3; // size of matrix around the mouse
        int extent = (int) Math.floor((double) matrix / 2);
        for ( int i = -extent; i <= extent; i++){
            for ( int j = -extent; j <= extent; j++){
                if (r.nextFloat() < 0.75) {
                    int col = x + i;
                    int row = y + j;
                    if (isInsideGrid(col, row) && (grid[col][row].state != 1 && grid[col][row].state != 2 )) {
                        grid[col][row] = new Cell(1, hueValue);
                    }
                }
            }
        }
        hueValue += 0.001f; //speed at which hue changes
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int mcol = (int) Math.floor((double) e.getX() / w);
        int mrow = (int) Math.floor((double) e.getY() / w);
        Random r = new Random();

        /*int matrix = 7; // size of matrix around the mouse
        int extent = (int) Math.floor(matrix/2);
        for ( int i = -extent; i <= extent; i++){
            for ( int j = -extent; j <= extent; j++){
                if (r.nextFloat() < 0.75) {
                    int col = mcol + i;
                    int row = mrow + j;
                    if (isInsideGrid(mcol, mrow) && (grid[mcol][mrow].state != 1 && grid[mcol][mrow].state != 2 )) {
                            grid[mcol][mrow] = new Cell(1, hueValue);
                    } else if(grid[mcol][mrow].state == 1){
                        grid[mcol][mrow] = new Cell(2, grid[mcol][mrow].hue);
                    }
               /* }
            }
        }
        hueValue += 0.001f; //speed at which hue changes*/

        //Hold shift and click to print state of the grid in console;
        if (e.isShiftDown()){
            for (int i = 0; i < grid.length; i++) {
                System.out.print("[");
                for (int j = 0; j < grid[i].length; j++) {

                    System.out.print(grid[j][i].state > 0 ? grid[j][i].state : "0");
                    System.out.print(" ");

                }
                System.out.println("]");
            }
            System.out.println("\n\n");
        }
    }

    //check if mouse is within grid
    private boolean isInsideGrid(int col, int row) {
        return col >= 0 && col < cols && row >= 0 && row < rows;
    }

    @Override
    public void mouseMoved(MouseEvent e) {}
    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
}

