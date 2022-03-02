package com.datguy.quadmis.application;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class QuadmisController implements Initializable {

    @FXML
    private VBox root;
    @FXML
    private ResizableCanvas canvas;
    private GraphicsContext graphics;

    private final QuadmisCore core;

    private AnimationTimer renderTimer;

    public QuadmisController() {
        core = new QuadmisCore(this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        canvas.widthProperty().bind(root.widthProperty());
        canvas.heightProperty().bind(root.heightProperty());

        graphics = canvas.getGraphicsContext2D();
        core.start();
    }

    public void stop() {
        core.stop();
    }

    public void startAnimation() {
        renderTimer = new AnimationTimer() {

            @Override
            public void handle(long now) {
                core.render();
            }
        };
        renderTimer.start();
    }

    public void stopAnimation() {
        renderTimer.stop();
    }

    public QuadmisCore getCore() {
        return core;
    }

    public ResizableCanvas getCanvas() {
        return canvas;
    }

    public GraphicsContext getGraphics() {
        return graphics;
    }
}