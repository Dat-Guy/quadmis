package com.datguy.quadmis;

import com.datguy.quadmis.data.QuadmisGrid;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.lang.ref.WeakReference;

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

    public void renderGrid(GraphicsContext gc, QuadmisGrid grid, double x, double y, double width, double height) {

        gc.setFill(Color.BLACK);
        gc.fillRect(x, y, width, height);

        double blockSize = Math.min((width - 2) / 10, (height - 2) / 24);
        gc.setLineWidth(0);

        double halfPadWidth = (width - blockSize * 10) / 2;
        double halfPadHeight = (height - blockSize * 24) / 2;

        // Fills grid with visual contents
        for (int r = 23; r >= 0; r--) {
            for (int c = 0; c < 10; c++) {
                if (grid.getBlock(r, c) != null) {
                    gc.setFill(grid.getBlock(r, c).color);
                    gc.fillRect(x + c * blockSize + 1 + halfPadWidth, y + (23 - r) * blockSize + 1 + halfPadHeight, blockSize, blockSize);
                }
            }
        }

        // Grid outline
        gc.setStroke(Color.WHITESMOKE);
        gc.setLineWidth(1);
        gc.strokeRect(x + halfPadWidth, y + blockSize * 4 + halfPadHeight, blockSize * 10, blockSize * 20);

        // Draws active piece
        gc.setLineWidth(0);

        boolean[][] shape = grid.getPiece().quad.getShape();
        gc.setFill(grid.getPiece().quad.getColor());

        int offX = grid.getPiece().quad.getPos().x;
        int offY = grid.getPiece().quad.getPos().y;

        for (int row = shape.length - 1; row >= 0; row--) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col]) {
                    gc.fillRect(x + (col + offX) * blockSize + 1 + halfPadWidth,
                            y + (23 + row - offY) * blockSize + 1 + halfPadHeight,
                            blockSize, blockSize);
                }
            }
        }

        // Draws piece local space outline (DEBUG)
        gc.setStroke(Color.WHITESMOKE);
        gc.setLineWidth(1);
        gc.strokeRect(x + halfPadWidth + offX * blockSize,
                y + halfPadHeight + (23 - offY) * blockSize,
                blockSize * shape[0].length,
                blockSize * shape.length);

    }
}
