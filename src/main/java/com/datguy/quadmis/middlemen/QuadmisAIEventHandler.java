package com.datguy.quadmis.middlemen;

import com.datguy.quadmis.ai.QuadmisAI;
import com.datguy.quadmis.ai.QuadmisCostAI;
import com.datguy.quadmis.data.QuadmisAttack;
import com.datguy.quadmis.data.QuadmisGrid;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class QuadmisAIEventHandler implements QuadmisAbstractEventHandler {

    private final QuadmisGrid grid;
    private final QuadmisAI ai;
    private double piecesPerSecond;
    private final Timer timer = new Timer();

    public QuadmisAIEventHandler(QuadmisGrid grid, double pPS) {
        this.grid = grid;
        ai = new QuadmisCostAI();
        piecesPerSecond = pPS;
    }

    @Override
    public void start() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                ai.step(grid);
            }
        }, 0, (long) Math.floor(1 / piecesPerSecond * 1000));
    }

    @Override
    public void stop() {
        timer.cancel();
    }

    @Override
    public void setAutoLock(long millis) {
        grid.lock();
    }

    @Override
    public void cancelAutoLock() {

    }

    @Override
    public void setAttackTrigger(QuadmisAttack.QuadmisAttackByte attack) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Objects.requireNonNull(grid.outgoingAttack.getTarget()).applyAttack(attack);
            }
        }, attack.getDelay(TimeUnit.MILLISECONDS));
    }
}
