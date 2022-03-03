package com.datguy.quadmis.data;

import com.datguy.quadmis.middlemen.QuadmisAbstractEventHandler;
import com.datguy.quadmis.QuadmisKick;
import com.datguy.quadmis.QuadmisMetaQuad;
import com.datguy.quadmis.middlemen.QuadmisQuad;
import javafx.scene.paint.Color;

import java.awt.Point;
import java.lang.ref.WeakReference;
import java.util.*;

public class QuadmisGrid {

    // TODO: Add hashtable converting bagPool indices to pieces (both meta and tables)
    private static final int[] bagPool = new int[]{0, 1, 2, 3, 4, 5, 6};
    private static final int bagSize = 7;

    // Used to translate bag draws to pieces, as well as the next queue to renderable shapes
    public static final QuadmisMetaQuad[] bagToMeta = new QuadmisMetaQuad[]{new QuadmisQuad.O(),
                                                                            new QuadmisQuad.I(),
                                                                            new QuadmisQuad.T(),
                                                                            new QuadmisQuad.L(),
                                                                            new QuadmisQuad.J(),
                                                                            new QuadmisQuad.S(),
                                                                            new QuadmisQuad.Z()};

    // Should handle storage of blocks within grid, basic collision/clear checks and handling the results of such checks
    // There are 2 layers of storage:
    // Logical storage (used to determine collisions)
    // Rendering storage (used to draw the game)
    // These are separated in order to facilitate cleaner code as well as isolate bugs
    final boolean[][] grid = new boolean[40][];
    private final QuadmisBlock[][] visualGrid = new QuadmisBlock[40][];

    // Since death occurs when the piece is blocked from spawning, the grid should handle piece creation,
    // as collision checks have been allocated to the grid
    private QuadmisPiece piece;
    private QuadmisPiece hold;
    private boolean swapLock = false;
    private WeakReference<QuadmisAbstractEventHandler> parentHandler;
    public final QuadmisAttack outgoingAttack = new QuadmisAttack(50, this);
    private boolean resting = false;
    private boolean combo = false;

    // Okay so why is the bag 14 instead of 7?
    // Simple - the piece queue must show 5 pieces, even if it crosses bags
    private final int[] bag = new int[bagSize * 2];
    private int bagPos;

    private final LinkedList<QuadmisAttack.QuadmisAttackByte> queuedAttack = new LinkedList<>();

    public QuadmisGrid() {
        reset();
    }

    public void setParentHandler(QuadmisAbstractEventHandler handler) {
        parentHandler = new WeakReference<>(handler);
    }

    public void generateNextPiece() {
        piece = new QuadmisPiece(new QuadmisQuad(bagToMeta[bag[bagPos]]), this);
        swapLock = false;

        bagPos++;

        if (bagPos == bagSize) {
            int[] temp = QuadmisUtil.shuffled(bagPool);
            System.arraycopy(temp, 0, bag, 0, bagSize);
        }

        if (bagPos == bagSize * 2) {
            bagPos = 0;
            int[] temp = QuadmisUtil.shuffled(bagPool);
            System.arraycopy(temp, 0, bag, bagSize, bagSize);
        }
    }

    public QuadmisPiece getPiece() {
        return piece;
    }

    public boolean getSwapLock() {
        return swapLock;
    }

    public QuadmisPiece getHold() {
        return hold;
    }

    public int[] getNextQueue() {
        if (bagPos < bagSize + 2) {
            return Arrays.copyOfRange(bag, bagPos, bagPos + (bagSize - 1));
        } else {
            int topSize = bagSize * 2 - bagPos;
            int[] topPart = Arrays.copyOfRange(bag, bagPos, bagSize * 2);
            int[] bottomPart = Arrays.copyOfRange(bag, 0, (bagSize - 2) - topSize);
            int[] out = new int[bagSize - 2];
            System.arraycopy(topPart, 0, out, 0, topPart.length);
            System.arraycopy(bottomPart, 0, out, topSize, bottomPart.length);
            return out;
        }
    }

    public int getBagPos() {
        return bagPos % bagSize;
    }

    public QuadmisBlock getBlock(int r, int c) {
        try {
            return visualGrid[r][c];
        } catch (RuntimeException e) {
            return getBlock(r, c);
        }
    }

    public boolean getCombo() {
        return combo;
    }

    public LinkedList<QuadmisAttack.QuadmisAttackByte> getQueuedAttack() {
        return queuedAttack;
    }

    public void applyGravity() {
        if (!resting) {
            if (collides(piece.quad.getShape(), piece.quad.getPos().x, piece.quad.getPos().y - 1)) {
                resting = true;
                Objects.requireNonNull(parentHandler.get()).setAutoLock(500); // TODO: Support master levels
            } else {
                Point temp = piece.quad.getPos();
                temp.y--;
                piece.quad.setPos(temp);
            }
        }
    }

    public void lock() {
        // If it's a T, checks if it's either a spin or a mini spin
        boolean tSpin = piece.quad.getMetaClass() == QuadmisQuad.T.class &&
                isTSpin(piece.quad.getRot(), piece.quad.getPos().x, piece.quad.getPos().y);
        boolean miniTSpin = piece.quad.getMetaClass() == QuadmisQuad.T.class &&
                isMiniTSpin(piece.quad.getRot(), piece.quad.getPos().x, piece.quad.getPos().y);

        boolean[][] shape = piece.quad.getShape();
        int offX = piece.quad.getPos().x;
        int offY = piece.quad.getPos().y;

        boolean allAbove = true;

        for (int row = shape.length - 1; row >= 0; row--) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col]) {
                    grid[offY - row][offX + col] = true;
                    visualGrid[offY - row][offX + col] = new QuadmisBlock(piece.quad.getColor(), new boolean[8]);
                    allAbove = allAbove && (offY - row >= 20);
                }
            }
        }

        // Checks one of the game over conditions (piece completely outside the lower half of the grid (rows 1-20)
        if (allAbove)  {
            reset();
            return;
        }


        // Clear lines
        int clearCount = 0;

        for (int row = grid.length - 1; row >= 0; row--) {
            // Checks if a row is completely filled
            boolean full = true;
            for (int col = 0; col < grid[row].length; col++) {
                if (!grid[row][col]) {
                    full = false;
                    break;
                }
            }
            if (full) {
                clearCount++;

                // Shifts EVERY ROW above the cleared row down by one
                for (int row2 = row; row2 < grid.length - 1; row2++) {
                    for (int col = 0; col < grid[row2].length; col++) {
                        grid[row2][col] = grid[row2 + 1][col];
                        visualGrid[row2][col] = visualGrid[row2 + 1][col];
                    }
                }

                // Fills the top row with empty data
                for (int col = 0; col < grid[grid.length - 1].length; col++) {
                    grid[grid.length - 1][col] = false;
                    visualGrid[grid.length - 1][col] = null;
                }
            }
        }

        if (clearCount > 0) {
            Set<QuadmisAttack.QuadmisClear.Flags> flags = new HashSet<>();

            if (tSpin) {
                flags.add(QuadmisAttack.QuadmisClear.Flags.SPIN);
            } else if (miniTSpin) {
                flags.add(QuadmisAttack.QuadmisClear.Flags.MINI_SPIN);
            }

            if (combo) {
                flags.add(QuadmisAttack.QuadmisClear.Flags.COMBO);
            }

            combo = true;

            outgoingAttack.pushClear(new QuadmisAttack.QuadmisClear(clearCount, flags));
            Objects.requireNonNull(parentHandler.get()).setAttackTrigger(outgoingAttack.getLast());

            // System.out.println(outgoingAttack);
        } else {
            combo = false;
            resolveAttack();
        }

        generateNextPiece();

        // Checks if the piece collides with the grid on generation (game over)
        if (collides(piece.quad.getShape(), piece.quad.getPos().x, piece.quad.getPos().y)) {
            reset();
            return;
        }

        if (resting) {
            Objects.requireNonNull(parentHandler.get()).cancelAutoLock();
            resting = false;
        }
    }

    public void resolveAttack() {
        for (Iterator<QuadmisAttack.QuadmisAttackByte> attackIterator = queuedAttack.iterator(); attackIterator.hasNext(); ) {
            QuadmisAttack.QuadmisAttackByte attack = attackIterator.next();

            int topRow = 40;

            loop:
            for (int r = grid.length - 1; r >= 0; r--) {
                for (int c = 0; c < grid[r].length; c++) {
                    if (grid[r][c])
                        break loop;
                }
                topRow = r;
            }

            if (topRow + attack.attackAmount >= 40) {
                reset();
                return;
            }

            for (int r = grid.length - 1; r >= attack.attackAmount; r--) {
                for (int c = 0; c < grid[r].length; c++) {
                    grid[r][c] = grid[r - attack.attackAmount][c];
                    visualGrid[r][c] = visualGrid[r - attack.attackAmount][c];
                }
            }

            int emptyCol = (int) Math.floor(Math.random() * grid[0].length);

            for (int r = 0; r < attack.attackAmount; r++) {
                for (int c = 0; c < grid[r].length; c++) {
                    if (c != emptyCol) {
                        grid[r][c] = true;
                        visualGrid[r][c] = new QuadmisBlock(Color.GRAY, new boolean[8]);
                    } else {
                        grid[r][c] = false;
                        visualGrid[r][c] = null;
                    }
                }
            }
        }

        queuedAttack.clear();
    }

    public void applyCWKick(Point offset) {
        piece.quad.rotRight();
        piece.quad.setPos(new Point(piece.quad.getPos().x + offset.x, piece.quad.getPos().y - offset.y));
    }

    public void applyCCWKick(Point offset) {
        piece.quad.rotLeft();
        piece.quad.setPos(new Point(piece.quad.getPos().x + offset.x, piece.quad.getPos().y - offset.y));
    }

    // TODO: Make this not stupid
    public boolean collides(boolean[][] shape, int x, int y) {
        try {
            for (var r = 0; r < shape.length; r++) {
                for (var c = 0; c < shape.length; c++) {
                    if (shape[r][c] && (y - r < 0 || x + c > grid[y - r].length || grid[y - r][x + c])) {
                        return true;
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            return true;
        }

        return false;
    }

    public boolean isMiniTSpin(int rot, int x, int y) {
        if (collides(piece.quad.getShape(), x, y)) {
            return false;
        }

        boolean A = collides(QuadmisTables.Spins.T.A[rot], x, y);
        boolean B = collides(QuadmisTables.Spins.T.B[rot], x, y);
        boolean C = collides(QuadmisTables.Spins.T.C[rot], x, y);
        boolean D = collides(QuadmisTables.Spins.T.D[rot], x, y);

        // C & D & (A | B)
        return C && D && (A || B);
    }

    public boolean isTSpin(int rot, int x, int y) {
        if (collides(piece.quad.getShape(), x, y)) {
            return false;
        }

        boolean A = collides(QuadmisTables.Spins.T.A[rot], x, y);
        boolean B = collides(QuadmisTables.Spins.T.B[rot], x, y);
        boolean C = collides(QuadmisTables.Spins.T.C[rot], x, y);
        boolean D = collides(QuadmisTables.Spins.T.D[rot], x, y);

        // A & B & (C | D)
        return A && B && (C || D);

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
            Objects.requireNonNull(parentHandler.get()).cancelAutoLock();
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
            Objects.requireNonNull(parentHandler.get()).cancelAutoLock();
        }

        applyCWKick(offset);
    }

    public void applyShiftLeft() {
        if (!collides(piece.quad.getShape(), piece.quad.getPos().x - 1, piece.quad.getPos().y)) {
            piece.quad.setPos(new Point(piece.quad.getPos().x - 1, piece.quad.getPos().y));
        }

        if (resting) {
            resting = false;
            Objects.requireNonNull(parentHandler.get()).cancelAutoLock();
        }
    }

    public void applyShiftRight() {
        if (!collides(piece.quad.getShape(), piece.quad.getPos().x + 1, piece.quad.getPos().y)) {
            piece.quad.setPos(new Point(piece.quad.getPos().x + 1, piece.quad.getPos().y));
        }

        if (resting) {
            resting = false;
            Objects.requireNonNull(parentHandler.get()).cancelAutoLock();
        }
    }

    public void applyHardDrop() {
        while (!collides(piece.quad.getShape(), piece.quad.getPos().x, piece.quad.getPos().y - 1)) {
            piece.quad.setPos(new Point(piece.quad.getPos().x, piece.quad.getPos().y - 1));
        }
        lock();
    }

    public void applyFirmDrop() {
        while (!collides(piece.quad.getShape(), piece.quad.getPos().x, piece.quad.getPos().y - 1)) {
            piece.quad.setPos(new Point(piece.quad.getPos().x, piece.quad.getPos().y - 1));
        }
    }

    public void applyHold() {
        if (!swapLock) {
            swapLock = true;

            if (resting) {
                resting = false;
                Objects.requireNonNull(parentHandler.get()).cancelAutoLock();
            }

            if (Objects.isNull(hold)) {
                hold = piece;
                generateNextPiece();
            } else {
                QuadmisPiece tmp = piece;
                piece = hold;
                hold = tmp;
            }

            hold.quad.reset();
        }
    }

    public void applyAttack(QuadmisAttack.QuadmisAttackByte attack) {
        queuedAttack.addLast(attack);
    }

    public int reduceAttack(int amount) {
        Iterator<QuadmisAttack.QuadmisAttackByte> attackIt = queuedAttack.iterator();
        while (amount > 0 && attackIt.hasNext()) {
            QuadmisAttack.QuadmisAttackByte attack = attackIt.next();
            if (attack.attackAmount <= amount) {
                amount -= attack.attackAmount;
                queuedAttack.removeFirst();
            } else {
                attack.attackAmount -= amount;
                amount = 0;
            }
        }
        return amount;
    }

    public void internalReset() {
        // Populate the grid as empty
        for (var r = 0; r < 40; r++) {
            grid[r] = new boolean[10];
            visualGrid[r] = new QuadmisBlock[10];
            for (var c = 0; c < 10; c++) {
                grid[r][c] = false;
                visualGrid[r][c] = null;
            }
        }


        bagPos = 0;

        int[] temp = QuadmisUtil.shuffled(bagPool);
        int[] temp2 = QuadmisUtil.shuffled(bagPool);
        System.arraycopy(temp, 0, bag, 0, bagSize);
        System.arraycopy(temp2, 0, bag, bagSize, bagSize);

        outgoingAttack.flush();

        generateNextPiece();
        hold = null;

        if (resting) {
            resting = false;
            Objects.requireNonNull(parentHandler.get()).cancelAutoLock();
        }
    }

    public void reset() {
        if (parentHandler != null) {
            Objects.requireNonNull(parentHandler.get()).handleReset();
        }

        internalReset();
    }
    // You know what would be neat? Connected block textures, like in tetr.io plus!
    // However, to do so, we'd need to store extra data to generate textures
}
