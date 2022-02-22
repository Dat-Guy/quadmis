package com.datguy.quadmis;

import com.datguy.quadmis.data.QuadmisGrid;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

public class QuadmisRenderer {

    private final Timer timer = new Timer();
    private TimerTask renderTask;

    private WeakReference<QuadmisCore> invoker;

    public QuadmisRenderer(QuadmisCore core) {
        invoker = new WeakReference<>(core);

        renderTask = new TimerTask() {
            @Override
            public void run() {
                if (invoker.get() == null) {
                    this.cancel();
                    return;
                }
                renderGrid(invoker.get().getController().getGraphics(), invoker.get().getGrid(), 0, 0, invoker.get().getController().getCanvas().getWidth(), invoker.get().getController().getCanvas().getHeight());
            }
        };
    }

    public void start() {
        invoker.get().getController().getCanvas().setDefaultRedraw(resizableCanvas -> {
            renderGrid(invoker.get().getController().getGraphics(), invoker.get().getGrid(), 0, 0, invoker.get().getController().getCanvas().getWidth(), invoker.get().getController().getCanvas().getHeight());
            return null;
        });
        // Render at 120 FPS
        timer.scheduleAtFixedRate(renderTask, 0, 1000 / 120);
    }

    public void renderGrid(GraphicsContext gc, QuadmisGrid grid, double x, double y, double width, double height) {

        gc.setFill(Color.BLACK);
        gc.fillRect(x, y, width, height);

        double blockSize = Math.min((width - 2) / 10, (height - 2) / 24);
        gc.setLineWidth(0);

        double halfPadWidth = (width - blockSize * 10) / 2;
        double halfPadHeight = (height - blockSize * 24) / 2;

        // Initially let's just fill each square
        for (int r = 23; r >= 0; r--) {
            for (int c = 0; c < 10; c++) {
                if (grid.getBlock(r, c) != null) {
                    gc.setFill(grid.getBlock(r, c).color);
                    gc.fillRect(x + c * blockSize + 1 + halfPadWidth, y + (23 - r) * blockSize + 1 + halfPadHeight, blockSize, blockSize);
                }
            }
        }

        gc.setStroke(Color.WHITESMOKE);
        gc.setLineWidth(1);
        gc.strokeRect(x + halfPadWidth, y + blockSize * 4 + halfPadHeight, blockSize * 10, blockSize * 20);

        boolean[][] shape = grid.getPiece().quad.getShape();
        gc.setFill(grid.getPiece().quad.getColor());

        int offX = grid.getPiece().quad.getPos().x;
        int offY = grid.getPiece().quad.getPos().y;

        for (int row = shape.length - 1; row >= 0; row--) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col]) {
                    gc.fillRect(x + (col + offX) * blockSize + 1 + halfPadWidth, y + (row + offY) * blockSize + 1 + halfPadHeight, blockSize, blockSize);
                }
            }
        }
    }
}
