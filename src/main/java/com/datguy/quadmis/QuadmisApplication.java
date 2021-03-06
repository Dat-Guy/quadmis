package com.datguy.quadmis;

import com.datguy.quadmis.application.QuadmisController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import com.datguy.quadmis.application.QuadmisCore.QuadmisInputHandler;

import java.io.*;

public class QuadmisApplication extends Application {
    
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(QuadmisApplication.class.getResource("quadmis.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), Screen.getPrimary().getBounds().getWidth(), Screen.getPrimary().getBounds().getHeight());
        QuadmisController controller = fxmlLoader.getController();

        //scene.setFill(Color.BLACK);
        scene.addEventHandler(MouseEvent.ANY, new QuadmisInputHandler<>(controller.getCore()));
        scene.addEventHandler(KeyEvent.ANY, new QuadmisInputHandler<>(controller.getCore()));

        stage.setTitle("Hello!");
        stage.setOnHidden(e -> controller.stop());

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}