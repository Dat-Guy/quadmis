package com.datguy.quadmis;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.security.Key;

public class QuadmisCore {

    // Should call all necessary classes/methods in order for the game to function,
    // but not implement anything itself other than handlers
    public QuadmisCore() {

    }

    // Main update method, called each frame
    // TODO: Make graphical/logical updates asynchronous
    public void update(long now, GraphicsContext gc) {

    }

    // Render is its own method, to organize/hope for shader
    public void render(GraphicsContext gc) {

    }

    // Handles KeyEvents
    private void handleKeyEvent(KeyEvent event) {
        // For now, just print the event name and basic data
        System.out.println(event.getEventType().getName() + ": " + event.getCharacter());
    }

    // Handles MouseEvents
    private void handleMouseEvent(MouseEvent event) {
        System.out.println(event.getEventType().getName() + ": " + event.getButton() + ", (x: " + event.getSceneX() + ", y: " + event.getSceneY() + ")");
    }


    public static class QuadmisEventHandler<T extends Event> implements EventHandler<T> {

        QuadmisCore core;

        public QuadmisEventHandler(QuadmisCore receiver) {
            super();
            core = receiver;
        }

        @Override
        public void handle(T event) {
            if (event instanceof KeyEvent) {
                core.handleKeyEvent((KeyEvent) event);
                return;
            }
            if (event instanceof MouseEvent) {
                core.handleMouseEvent((MouseEvent) event);
                return;
            }
            if (event != null) {
                System.out.println("Received unexpected event of type:");
                System.out.println(event.getClass().getCanonicalName());
            }
            System.out.println("Received null event.");
        }
    }
}
