package com.datguy.quadmis;

import com.datguy.quadmis.data.QuadmisGrid;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.lang.ref.WeakReference;

public class QuadmisCore {

    private final QuadmisGrid grid;
    private final QuadmisInputHandler inputHandler;
    private final QuadmisRenderer renderer;

    private final WeakReference<QuadmisController> controller;

    // Should call all necessary classes/methods in order for the game to function,
    // but not implement anything itself other than handlers
    public QuadmisCore(QuadmisController controller) {
        grid = new QuadmisGrid();
        inputHandler = new QuadmisInputHandler(grid);
        renderer = new QuadmisRenderer(this);
        this.controller = new WeakReference<>(controller);

        grid.setParentHandler(inputHandler);
    }

    public void start() {
        inputHandler.start();
        renderer.start();
    }

    public void stop() {
        inputHandler.stop();
        renderer.stop();
    }

    public void render() {
        QuadmisController gotten = controller.get();
        if (gotten != null) {
            renderer.renderGrid(gotten.getGraphics(), grid, 0, 0, gotten.getCanvas().getWidth(), gotten.getCanvas().getHeight());
        }
    }

    public QuadmisController getController() {
        return controller.get();
    }

    // Handles KeyEvents
    private void handleKeyEvent(KeyEvent event) {
        // For now, just print the event name and basic data
        // System.out.println(event.getEventType().getName() + ": " + event.getCharacter());

        // We're going to want to start intercepting key presses and using them to control the game.
        // Luckily, the guidelines already define this for us:

        // Moving the piece L/R should move once initially, then after a certain amount of time has elapsed, if the
        // key is still held, begin moving the piece left/right

        // As long as down is held, make the piece fall faster
        // Spacebar hard-drops (should make the piece phase down until it collides, then lock it, all on a single tick)
        // z-x spins left-right, c holds, a flips (TODO: add flip functionality)
        inputHandler.handleKeyEvent(event);
    }

    // Handles MouseEvents
    private void handleMouseEvent(MouseEvent event) {

        // System.out.println(event.getEventType().getName() + ": " + event.getButton() + ", (x: " + event.getSceneX() + ", y: " + event.getSceneY() + ")");
    }

    public QuadmisGrid getGrid() {
        return grid;
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
