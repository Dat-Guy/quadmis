package com.datguy.quadmis;

import com.datguy.quadmis.data.QuadmisGrid;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Interprets user input into useful game events
 */
public class QuadmisInputHandler {

    private final QuadmisGrid grid;
    private final Timer timer = new Timer();
    private TimerTask autolock;
    private TimerTask gravity = null;
    // How should the game internally handle events? Let's look at other tetris games for a moment:
    // -> Guidelines:
    // # DAS (Delayed Auto Shift) - The time it takes from initially holding down a key until thr action begins repitition
    // # ARR (Automatic Repeat Rate) - The frequency at which an action (moving left/right) is repeated once the DAS period
    // elapses. Note that the shift should occur BEFORE the first ARR delay. Can be 0, which will shift the piece left/right
    // Until it collides with a wall/blocks
    // # SDF (Soft Drop Factor) - The amount by which soft dropping should increase gravity. Can be 0, which will
    // do a firm drop (hard drop without lock-in)

    // These in of themselves require knowledge of defaults:

    // Gravity -> (0.8 - ((level - 1) * 0.007))^(level - 1) seconds/line fallen
    // ALD -> Auto Lock delay. Starts at 500 ms, and decreases by 50ms every master level. Why?
    // Well, at level 20, it takes <10ms in order for a piece to fall from the top of the grid to the bottom.
    // Since Tetris is designed to run at 60 FPS, that means at level 20 it takes a single frame for the piece to fall from the top
    // of the board to the stack. Therefore, any increase in gravity from here is arbitrary.

    // However, auto-lock delay is still 30 frames at L20, or 500 ms! Therefore, you can begin decreasing that window with
    // Master (M) levels, which also increase to 20. At M20, the piece falls to the board in 1 frame, then you only have
    // 3 frames (50 ms) to move the piece before auto-lock engages.

    // The input handler, since it's responsible for turning events into other events, needs to maintain
    // bidirectional communication.

    public QuadmisInputHandler(QuadmisGrid grid) {
        this.grid = grid; // The receiver of events
    }

    public void start() {
        // There's nothing to start, so...
    }

    public void stop() {
        timer.cancel(); // Destroy the timer
    }

    public void setGravity(int millisInterval) {
        if (gravity != null) {
            gravity.cancel();
        }
        gravity = new TimerTask() {
            @Override
            public void run() {
                grid.applyGravity();
            }
        };
        timer.schedule(gravity, 0, millisInterval);
    }

    public void setAutoLock() {
        autolock = new TimerTask() {
            @Override
            public void run() {
                grid.lock();
            }
        };
        timer.schedule(autolock, 500);
    }

    public void cancelAutoLock() {
        autolock.cancel();
    }

    public void handleKeyEvent(KeyEvent event) {
        if (event.getEventType() == KeyEvent.KEY_PRESSED) {
            if (event.getCode() == KeyCode.Z) {
                grid.applyCCWRot();
            }
            if (event.getCode() == KeyCode.X) {
                grid.applyCWRot();
            }
            if (event.getCode() == KeyCode.LEFT) {
                grid.applyShiftLeft();
            }
            if (event.getCode() == KeyCode.RIGHT) {
                grid.applyShiftRight();
            }
            if (event.getCode() == KeyCode.SPACE) {
                grid.applyHarddrop();
            }
            if (event.getCode() == KeyCode.DOWN) {
                for (int i = 0; i < 5; i++) {
                    grid.applyGravity();
                }
            }
            if (event.getCode() == KeyCode.R) {
                grid.reset();
            }
        }
    }
}