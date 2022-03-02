package com.datguy.quadmis.application;

import com.datguy.quadmis.data.QuadmisGrid;
import com.datguy.quadmis.middlemen.QuadmisAIEventHandler;
import com.datguy.quadmis.middlemen.QuadmisAbstractEventHandler;
import com.datguy.quadmis.middlemen.QuadmisEventHandler;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.lang.ref.WeakReference;

public class QuadmisCore {

    private final QuadmisGrid grid;
    private final QuadmisGrid grid2;

    private final QuadmisEventHandler inputHandler;
    private final QuadmisAIEventHandler inputHandler2;

    private final QuadmisRenderer renderer;

    private final WeakReference<QuadmisController> controller;

    // Should call all necessary classes/methods in order for the game to function,
    // but not implement anything itself other than handlers
    public QuadmisCore(QuadmisController controller) {
        grid = new QuadmisGrid();
        grid2 = new QuadmisGrid();

        inputHandler = new QuadmisEventHandler(grid);
        inputHandler2 = new QuadmisAIEventHandler(grid2, 1);

        renderer = new QuadmisRenderer(this);
        this.controller = new WeakReference<>(controller);

        grid.setParentHandler(inputHandler);
        grid2.setParentHandler(inputHandler2);

        grid.outgoingAttack.setTarget(grid2);
        grid2.outgoingAttack.setTarget(grid);

        inputHandler.setGravity(500);
    }

    public void start() {
        inputHandler.start();
        inputHandler2.start();
        renderer.start();
    }

    public void stop() {
        inputHandler.stop();
        inputHandler2.stop();
        renderer.stop();
    }

    public void render() {
        QuadmisController gotten = controller.get();
        if (gotten != null) {
            renderer.renderGrid(gotten.getGraphics(), grid, 0, 0, gotten.getCanvas().getWidth() / 2, gotten.getCanvas().getHeight());
            renderer.renderGrid(gotten.getGraphics(), grid2, gotten.getCanvas().getWidth() / 2, 0, gotten.getCanvas().getWidth() / 2, gotten.getCanvas().getHeight());
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


    public static class QuadmisInputHandler<T extends Event> implements EventHandler<T> {

        QuadmisCore core;

        public QuadmisInputHandler(QuadmisCore receiver) {
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
