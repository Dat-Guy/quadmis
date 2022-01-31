package com.datguy.quadmis;

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

    public QuadmisController() {
        core = new QuadmisCore();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        canvas.heightProperty().bind(root.heightProperty());

        graphics = canvas.getGraphicsContext2D();

        // Begin main loop
        new AnimationTimer(){
            @Override
            public void handle(long now) {
                core.update(now, graphics);
            }
        }.start();
    }

    public QuadmisCore getCore() {
        return core;
    }
}