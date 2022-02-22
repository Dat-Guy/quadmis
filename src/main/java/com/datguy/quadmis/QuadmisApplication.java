package com.datguy.quadmis;

import com.datguy.quadmis.data.QuadmisBlock;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import com.datguy.quadmis.QuadmisCore.QuadmisEventHandler;

import java.io.IOException;

public class QuadmisApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(QuadmisApplication.class.getResource("quadmis.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), Screen.getPrimary().getBounds().getWidth(), Screen.getPrimary().getBounds().getHeight());
        QuadmisController controller = fxmlLoader.getController();

        //scene.setFill(Color.BLACK);
        scene.addEventHandler(MouseEvent.ANY, new QuadmisEventHandler<>(controller.getCore()));
        scene.addEventHandler(KeyEvent.ANY, new QuadmisEventHandler<>(controller.getCore()));

        stage.setTitle("Hello!");

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}