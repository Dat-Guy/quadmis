package com.datguy.quadmis;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.function.Function;

public class ResizableCanvas extends Canvas {

    public ResizableCanvas() {
        widthProperty().addListener(evt -> clear(Color.BLACK));
        heightProperty().addListener(evt -> clear(Color.BLACK));
    }

    public ResizableCanvas(Function<ResizableCanvas, Object> f) {
        widthProperty().addListener(evt -> f.apply(this));
        heightProperty().addListener(evt -> f.apply(this));
    }

    private void clear(Color c) {
        double width = getWidth();
        double height = getHeight();

        GraphicsContext gc = getGraphicsContext2D();
        gc.setFill(c);
        gc.setLineWidth(0);
        gc.fillRect(0, 0, width, height);
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public double prefWidth(double height) {
        return getWidth();
    }

    @Override
    public double prefHeight(double width) {
        return getHeight();
    }


}