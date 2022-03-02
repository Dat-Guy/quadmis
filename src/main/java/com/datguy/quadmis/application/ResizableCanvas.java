package com.datguy.quadmis.application;

import javafx.beans.InvalidationListener;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.function.Function;

public class ResizableCanvas extends Canvas {

    private InvalidationListener widthListener;
    private InvalidationListener heightListener;

    public ResizableCanvas() {
        System.out.println("Setting default invalidation draw");
        widthListener = evt -> clear(Color.BLACK);
        heightListener = evt -> clear(Color.BLACK);
        widthProperty().addListener(widthListener);
        heightProperty().addListener(heightListener);
    }

    public ResizableCanvas(Function<ResizableCanvas, Object> f) {
        System.out.println("Setting custom invalidation draw");
        widthListener = evt -> f.apply(this);
        heightListener = evt -> f.apply(this);
        widthProperty().addListener(widthListener);
        heightProperty().addListener(heightListener);
    }

    public void setDefaultRedraw(Function<ResizableCanvas, Object> f) {
        System.out.println("Updating invalidation draw");
        widthProperty().removeListener(widthListener);
        heightProperty().removeListener(heightListener);
        widthListener = evt -> f.apply(this);
        heightListener = evt -> f.apply(this);
        widthProperty().addListener(widthListener);
        heightProperty().addListener(heightListener);
    }

    public void clear(Color c) {
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