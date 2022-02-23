package com.datguy.quadmis.data;

import com.datguy.quadmis.QuadmisInputHandler;
import com.datguy.quadmis.QuadmisKick;

import java.awt.Point;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class QuadmisGrid {

    private static final int[] bagPool = new int[]{0, 1, 2, 3, 4, 5, 6};

    // Should handle storage of blocks within grid, basic collision/clear checks and handling the results of such checks
    // There are 2 layers of storage:
    // Logical storage (used to determine collisions)
    // Rendering storage (used to draw the game)
    // These are separated in order to facilitate cleaner code as well as isolate bugs
    boolean[][] grid = new boolean[40][];
    private QuadmisBlock[][] visualGrid = new QuadmisBlock[40][];

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

        int[] temp = QuadmisUtil.shuffled(bagPool);
        int[] temp2 = QuadmisUtil.shuffled(bagPool);
        System.arraycopy(temp, 0, bag, 0, 7);
        System.arraycopy(temp2, 0, bag, 7, 7);

        System.out.println(Arrays.toString(bag));

        generateNextPiece();
        // Tetris is weird - rows count from the bottom up
    }

    public void setParentHandler(QuadmisInputHandler handler) {
        parentHandler = new WeakReference<>(handler);
        handler.setGravity(500);
    }

    public void generateNextPiece() {

        piece = new QuadmisPiece(new QuadmisQuad(switch (bag[bagPos]) {
            case 0 -> new QuadmisQuad.O();
            case 1 -> new QuadmisQuad.I();
            case 2 -> new QuadmisQuad.T();
            case 3 -> new QuadmisQuad.L();
            case 4 -> new QuadmisQuad.J();
            case 5 -> new QuadmisQuad.S();
            case 6 -> new QuadmisQuad.Z();
            default -> null;
        }, 4, 22), this);

        System.out.println("Generated piece with " + bag[bagPos] + " index");

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
        try {
            return visualGrid[r][c];
        } catch (RuntimeException e) {
            return getBlock(r, c);
        }
    }

    public void applyGravity() {
        if (!resting) {
            if (collides(piece.quad.getShape(), piece.quad.getPos().x, piece.quad.getPos().y - 1)) {
                System.out.println("Attempted to fall through ground, starting autolock timer.");
                resting = true;
                parentHandler.get().setAutoLock();
            } else {
                Point temp = piece.quad.getPos();
                temp.y--;
                piece.quad.setPos(temp);
            }
        }
    }

    public void lock() {
        boolean[][] shape = piece.quad.getShape();
        int offX = piece.quad.getPos().x;
        int offY = piece.quad.getPos().y;

        if (offY > 20) {
            parentHandler.get().setGravity(0);
        }

        for (int row = shape.length - 1; row >= 0; row--) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col]) {
                    grid[offY - row][offX + col] = true;
                    visualGrid[offY - row][offX + col] = new QuadmisBlock(piece.quad.getColor(), new boolean[8]);
                }
            }
        }

        for (int row = grid.length - 1; row >= 0; row--) {
            boolean full = true;
            for (int col = 0; col < grid[row].length; col++) {
                if (!grid[row][col]) {
                    full = false;
                    break;
                }
            }
            if (full) {
                for (int row2 = row; row2 < grid.length - 1; row2++) {
                    for (int col = 0; col < grid[row2].length; col++) {
                        grid[row2][col] = grid[row2 + 1][col];
                        visualGrid[row2][col] = visualGrid[row2 + 1][col];
                    }
                }

                for (int col = 0; col < grid[grid.length - 1].length; col++) {
                    grid[grid.length - 1][col] = false;
                    visualGrid[grid.length - 1][col] = null;
                }
            }
        }

        generateNextPiece();
        resting = false;
    }

    public void applyCWKick(Point offset) {
        piece.quad.rotRight();
        piece.quad.setPos(new Point(piece.quad.getPos().x + offset.x, piece.quad.getPos().y - offset.y));
    }

    public void applyCCWKick(Point offset) {
        piece.quad.rotLeft();
        piece.quad.setPos(new Point(piece.quad.getPos().x + offset.x, piece.quad.getPos().y - offset.y));
    }

    public boolean collides(boolean[][] shape, int x, int y) {
        try {
            for (var r = 0; r < shape.length; r++) {
                for (var c = 0; c < shape.length; c++) {
                    if (shape[r][c] && grid[y - r][x + c]) {
                        return true;
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            return true;
        }

        return false;
    }

    public void applyCCWRot() {
        QuadmisKick kick = piece.quad.getRotLeft();

        int index = 0;
        Point offset = kick.getOffset(index);

        while (collides(kick.getKickGeometry(), piece.quad.getPos().x + offset.x, piece.quad.getPos().y - offset.y)) {
            index++;
            try {
                offset = kick.getOffset(index);
            } catch (ArrayIndexOutOfBoundsException e) {
                return;
            }
        }

        if (resting) {
            resting = false;
            parentHandler.get().cancelAutoLock();
        }

        applyCCWKick(offset);
    }

    public void applyCWRot() {
        QuadmisKick kick = piece.quad.getRotRight();

        int index = 0;
        Point offset = kick.getOffset(index);

        while (collides(kick.getKickGeometry(), piece.quad.getPos().x + offset.x, piece.quad.getPos().y - offset.y)) {
            index++;
            try {
                offset = kick.getOffset(index);
            } catch (ArrayIndexOutOfBoundsException e) {
                return;
            }
        }

        if (resting) {
            resting = false;
            parentHandler.get().cancelAutoLock();
        }

        applyCWKick(offset);
    }

    public void applyShiftLeft() {
        if (!collides(piece.quad.getShape(), piece.quad.getPos().x - 1, piece.quad.getPos().y)) {
            piece.quad.setPos(new Point(piece.quad.getPos().x - 1, piece.quad.getPos().y));
        }

        if (resting) {
            resting = false;
            parentHandler.get().cancelAutoLock();
        }
    }

    public void applyShiftRight() {
        if (!collides(piece.quad.getShape(), piece.quad.getPos().x + 1, piece.quad.getPos().y)) {
            piece.quad.setPos(new Point(piece.quad.getPos().x + 1, piece.quad.getPos().y));
        }

        if (resting) {
            resting = false;
            parentHandler.get().cancelAutoLock();
        }
    }

    public void applyHarddrop() {
        while (!resting) {
            applyGravity();
        }
        parentHandler.get().cancelAutoLock();
        lock();
    }
    // ya'know what would be neat? Connected block textures, like in tetr.io plus!
    // However, to do so, we'd need to store extra data to generate textures
}
