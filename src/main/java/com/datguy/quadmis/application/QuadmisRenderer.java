package com.datguy.quadmis.application;

import com.datguy.quadmis.application.QuadmisCore;
import com.datguy.quadmis.data.QuadmisAttack;
import com.datguy.quadmis.data.QuadmisGrid;
import com.datguy.quadmis.data.QuadmisPiece;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.lang.ref.WeakReference;
import java.util.Objects;

public class QuadmisRenderer {

    private final WeakReference<QuadmisCore> invoker;

    public QuadmisRenderer(QuadmisCore core) {
        invoker = new WeakReference<>(core);
    }

    public void start() {
        QuadmisCore gotten = invoker.get();
        if (gotten != null) {
            gotten.getController().getCanvas().setDefaultRedraw(resizableCanvas -> {
                QuadmisCore gotten2 = invoker.get();
                if (gotten2 != null) {
                    renderGrid(resizableCanvas.getGraphicsContext2D(), gotten2.getGrid(), 0, 0,resizableCanvas.getWidth(), resizableCanvas.getHeight());
                }
                return null;
            });

            gotten.getController().startAnimation();
        }
    }

    public void stop() {
        QuadmisCore gotten = invoker.get();
        if (gotten != null) {
            gotten.getController().stopAnimation();
        }
    }

    public void renderGrid(GraphicsContext gc, QuadmisGrid grid, double x, double y, double width, double height) {

        gc.setFill(Color.BLACK);
        gc.fillRect(x, y, width, height);

        double blockSize = Math.min((width - 2) / 20, (height - 2) / 24);
        double outlineSize = blockSize * 0.08;
        gc.setLineWidth(outlineSize);
        gc.setStroke(Color.DIMGRAY);

        double halfPadWidth = (width - blockSize * 20) / 2;
        double halfPadHeight = (height - blockSize * 24) / 2;

        // Fills grid with visual contents
        for (int r = 23; r >= 0; r--) {
            for (int c = 0; c < 10; c++) {
                if (grid.getBlock(r, c) != null) {
                    gc.setFill(grid.getBlock(r, c).color);
                } else {
                    gc.setFill(Color.BLACK);
                }
                double xOffset = x + (c + 5) * blockSize + 1 + halfPadWidth;
                double yOffset = y + (23 - r) * blockSize + 1 + halfPadHeight;
                gc.strokeRect(xOffset, yOffset, blockSize, blockSize);
                gc.fillRect(xOffset, yOffset, blockSize, blockSize);

            }
        }

        // Grid outline
        gc.setStroke(Color.WHITESMOKE);
        gc.setLineWidth(outlineSize);
        gc.strokeRect(x + halfPadWidth + blockSize * 5, y + blockSize * 4 + halfPadHeight, blockSize * 10, blockSize * 20);

        // Draws active piece
        gc.setLineWidth(outlineSize / 2);

        boolean[][] shape = grid.getPiece().quad.getShape();
        gc.setFill(grid.getPiece().quad.getColor());

        int offX = grid.getPiece().quad.getPos().x;
        int offY = grid.getPiece().quad.getPos().y;

        for (int row = shape.length - 1; row >= 0; row--) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col]) {
                    double posX = x + (col + offX + 5) * blockSize + 1 + halfPadWidth;
                    double posY = y + (23 + row - offY) * blockSize + 1 + halfPadHeight;
                    gc.fillRect(posX, posY,
                            blockSize, blockSize);
                    gc.strokeRect(posX, posY,
                            blockSize, blockSize);
                }
            }
        }

        // Draws piece local space outline (DEBUG)
        /*gc.setStroke(Color.WHITESMOKE);
        gc.setLineWidth(outlineSize);
        gc.strokeRect(x + halfPadWidth + (offX + 5) * blockSize,
                y + halfPadHeight + (23 - offY) * blockSize,
                blockSize * shape[0].length,
                blockSize * shape.length);*/

        // Display hold box
        double holdX = x + blockSize + halfPadWidth;
        double holdY = y + halfPadHeight + blockSize * 4;

        gc.setStroke(Color.WHITESMOKE);
        gc.setLineWidth(outlineSize);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(new Font("Mono", blockSize));
        gc.strokeText("HOLD", holdX + blockSize * 2, holdY - blockSize * 0.5);
        gc.strokeRect(holdX, holdY, blockSize * 4, blockSize * 4);

        // Display held piece
        QuadmisPiece held = grid.getHold();

        if (Objects.nonNull(held)) {
            boolean[][] hShape = held.quad.getShape();
            double hHalfPad = (4 - hShape.length) * blockSize / 2;

            gc.setFill(held.quad.getColor());
            gc.setStroke(Color.WHITESMOKE);

            for (int r = 0; r < hShape.length; r++) {
                for (int c = 0; c < hShape[r].length; c++) {
                    if (hShape[r][c]) {
                        gc.fillRect(holdX + hHalfPad + blockSize * c, holdY + hHalfPad + blockSize * r,
                                blockSize, blockSize);
                        gc.strokeRect(holdX + hHalfPad + blockSize * c, holdY + hHalfPad + blockSize * r,
                                blockSize, blockSize);
                    }
                }
            }
        }

        // Display combo/b2b
        gc.setLineWidth(outlineSize / 2);
        gc.setFont(new Font("Mono", blockSize / 2));
        if (grid.getCombo() && grid.outgoingAttack.getComboCount() > 0) {
            gc.setStroke(Color.WHITESMOKE);
            gc.strokeText("Combo: " + grid.outgoingAttack.getComboCount(), holdX + blockSize * 1, holdY + blockSize * 5);
        }
        if (grid.outgoingAttack.getB2bCount() > 0) {
            gc.setStroke(Color.GOLDENROD);
            gc.strokeText("B2B: " + grid.outgoingAttack.getB2bLevel() + " (x" + grid.outgoingAttack.getB2bCount() + ")", holdX + blockSize + 1, holdY + blockSize * 6);
        }

        QuadmisAttack.QuadmisAttackByte[] lastFive = grid.outgoingAttack.getLastFive();

        gc.setStroke(Color.GRAY);
        gc.setLineWidth(outlineSize / 4);

        for (int i = 0; i < 5 && lastFive[i] != null; i++) {
            gc.strokeText(lastFive[i].toString(), holdX + blockSize + 2, holdY + blockSize * (8 + i));
        }


        // Display next queue
        double nextX = x + blockSize * 15 + halfPadWidth;
        double nextY = y + halfPadHeight + blockSize * 4;

        gc.setStroke(Color.WHITESMOKE);
        gc.setLineWidth(outlineSize);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(new Font("Mono", blockSize));
        gc.strokeText("NEXT", nextX + blockSize * 2, y + halfPadHeight + blockSize * 3.5);
        gc.strokeRect(nextX, y + halfPadHeight + blockSize * 4, blockSize * 4, blockSize * 4);

        // First within the box, next 4 at half size trailing below
        int[] queue = grid.getNextQueue();
        boolean[][] nShape = QuadmisGrid.bagToMeta[queue[0]].getShape(0);
        double nHalfPad = (4 - nShape.length) * blockSize / 2;

        gc.setFill(QuadmisGrid.bagToMeta[queue[0]].getColor());
        gc.setStroke(Color.WHITESMOKE);

        for (int r = 0; r < nShape.length; r++) {
            for (int c = 0; c < nShape[r].length; c++) {
                if (nShape[r][c]) {
                    gc.fillRect(nextX + nHalfPad + blockSize * c, nextY + nHalfPad + blockSize * r,
                            blockSize, blockSize);
                    gc.strokeRect(nextX + nHalfPad + blockSize * c, nextY + nHalfPad + blockSize * r,
                            blockSize, blockSize);
                }
            }
        }

        // now at half size!
        double halfSize = blockSize / 2;

        for (int index = 1; index < queue.length; index++) {
            gc.setFill(QuadmisGrid.bagToMeta[queue[index]].getColor());
            boolean[][] nnShape = QuadmisGrid.bagToMeta[queue[index]].getShape(0);
            double nnHalfPad = (4 - nnShape.length) * halfSize / 2;

            for (int r = 0; r < nnShape.length; r++) {
                for (int c = 0; c < nnShape[r].length; c++) {
                    if (nnShape[r][c]) {
                        gc.fillRect(nextX + nnHalfPad + halfSize * c,
                                (index + 1) * halfSize * 5 + nextY + nnHalfPad + halfSize * r,
                                halfSize, halfSize);
                        gc.strokeRect(nextX + nnHalfPad + halfSize * c,
                                (index + 1) * halfSize * 5 + nextY + nnHalfPad + halfSize * r,
                                halfSize, halfSize);
                    }
                }
            }
        }
    }
}
