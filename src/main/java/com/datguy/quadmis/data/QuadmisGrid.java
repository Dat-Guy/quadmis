package com.datguy.quadmis.data;

import com.datguy.quadmis.QuadmisInputHandler;
import com.datguy.quadmis.QuadmisKick;
import javafx.scene.paint.Color;

import java.awt.Point;
import java.lang.ref.WeakReference;

public class QuadmisGrid {

    private static final int[] bagPool = new int[]{0, 1, 2, 3, 4, 5, 6};

    // Should handle storage of blocks within grid, basic collision/clear checks and handling the results of such checks
    // There are 2 layers of storage:
    // Logical storage (used to determine collisions)
    // Rendering storage (used to draw the game)
    // These are separated in order to facilitate cleaner code as well as isolate bugs
    boolean[][] grid = new boolean[40][];
    QuadmisBlock[][] visualGrid = new QuadmisBlock[40][];

    // Since death occurs when the piece is blocked from spawning, the grid should handle piece creation,
    // as collision checks have been allocated to the grid
    private QuadmisPiece piece;
    private WeakReference<QuadmisInputHandler> parentHandler;
    private boolean resting = false;

    // Okay so why is the bag 14 instead of 7?
    // Simple - the piece queue must show 5 pieces, even if it crosses bags
    private final int[] bag = new int[14];
    private int bagPos = 0;

    public QuadmisGrid() {
        // Populate the grid as empty
        for (var r = 0; r < 40; r++) {
            grid[r] = new boolean[10];
            visualGrid[r] = new QuadmisBlock[10];
            for (var c = 0; c < 10; c++) {
                grid[r][c] = false;
                visualGrid[r][c] = null;
            }
        }

        grid[0][0] = true;
        visualGrid[0][0] = new QuadmisBlock(Color.color(0.5, 0, 1), new boolean[8]);
        grid[1][0] = true;
        visualGrid[1][0] = new QuadmisBlock(Color.color(0, 0, 1), new boolean[8]);
        grid[2][0] = true;
        visualGrid[2][0] = new QuadmisBlock(Color.color(1, 1, 0), new boolean[8]);

        int[] temp = QuadmisUtil.shuffled(bagPool);
        int[] temp2 = QuadmisUtil.shuffled(bagPool);
        System.arraycopy(temp, 0, bag, 0, 7);
        System.arraycopy(temp2, 0, bag, 7, 7);

        generateNextPiece();
        // Tetris is weird - rows count from the bottom up
    }

    public void setParentHandler(QuadmisInputHandler handler) {
        parentHandler = new WeakReference<>(handler);
    }

    public void generateNextPiece() {
        piece = new QuadmisPiece(new QuadmisQuad(new QuadmisQuad.O(), 4, 10), this);
        bagPos++;

        if (bagPos == 7) {
            int[] temp = QuadmisUtil.shuffled(bagPool);
            System.arraycopy(temp, 0, bag, 0, 7);
        }

        if (bagPos == 14) {
            bagPos = 0;
            int[] temp = QuadmisUtil.shuffled(bagPool);
            System.arraycopy(temp, 0, bag, 7, 7);
        }
    }

    public QuadmisPiece getPiece() {
        return piece;
    }

    public QuadmisBlock getBlock(int r, int c) {
        return visualGrid[r][c];
    }

    public void applyGravity() {
        if (!resting) {
            Point temp = piece.quad.getPos();
            temp.y--;
            piece.quad.setPos(temp);
        }

        if (collides(piece.quad.getShape(), piece.quad.getPos().x, piece.quad.getPos().y - 1)) {
            resting = true;
            parentHandler.get().setAutoLock();
        }
    }

    public void lock() {

    }

    public void applyCWKick(Point offset) {
        piece.quad.rotLeft();
        piece.quad.setPos(new Point(piece.quad.getPos().x + offset.x, piece.quad.getPos().y + offset.y));
    }

    public void applyCCWKick(Point offset) {
        piece.quad.rotRight();
        piece.quad.setPos(new Point(piece.quad.getPos().x + offset.x, piece.quad.getPos().y + offset.y));
    }

    public int testKick(QuadmisQuad quad, QuadmisKick kick) {
        int x1 = quad.getPos().x;
        int y1 = quad.getPos().y;
        boolean[][] rotShape = kick.getKickGeometry();
        int index = 0;

        while (true) {
            Point offset = kick.getOffset(index);
            if (offset == null) {
                return -1;
            }

            int x2 = x1 + offset.x;
            int y2 = y1 + offset.y;

            if (!collides(rotShape, x2, y2)) {
                return index;
            }

            index++;
        }
    }

    public boolean collides(boolean[][] shape, int x, int y) {
        for (var r = 0; r < shape.length; r++) {
            for (var c = 0; c < shape.length; c++) {
                if (shape[r][c] && grid[y+r][x+c]) {
                    return true;
                }
            }
        }
        return false;
    }

    // ya'know what would be neat? Connected block textures, like in tetr.io plus!
    // However, to do so, we'd need to store extra data to generate textures
}
