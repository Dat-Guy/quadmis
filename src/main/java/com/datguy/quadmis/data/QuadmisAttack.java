package com.datguy.quadmis.data;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Responsible for evaluating a series of line clears in order to produce outgoing attack power.
 * TODO: Support target as a board state associated with individual attack values
 */
public class QuadmisAttack {
    private WeakReference<QuadmisGrid> target;
    private WeakReference<QuadmisGrid> parent;

    private final LinkedList<QuadmisAttackByte> attackQueue = new LinkedList<>();
    private final long attackDelay;

    private int comboCount;
    private int b2bCount;

    public QuadmisAttack(long attackDelay, QuadmisGrid p) {
        comboCount = 0;
        b2bCount = 0;
        this.attackDelay = attackDelay;
        target = new WeakReference<>(null);
        parent = new WeakReference<>(p);
    }

    @Override
    public String toString() {
        AtomicReference<String> listOut = new AtomicReference<>("");
        attackQueue.forEach(attackByte -> listOut.set(listOut.get() + "\n" + attackByte));
        return "Attack Queue (" + hashCode() + "), Combo: " + comboCount + ", B2B: " + b2bCount + listOut.get();
    }

    public QuadmisAttackByte getLast() {
        return attackQueue.getLast();
    }

    public QuadmisAttackByte[] getLastFive() {
        QuadmisAttackByte[] out = new QuadmisAttackByte[5];
        Iterator<QuadmisAttackByte> descend = attackQueue.descendingIterator();

        for (int i = 0; descend.hasNext() && i < 5; i++)
            out[i] = descend.next();

        return out;
    }

    public int getB2bCount() {
        return b2bCount - 1;
    }

    public int getComboCount() {
        return comboCount;
    }

    public int getB2bLevel() {
        return convertBTBToLevel(b2bCount - 1);
    }

    public void flush() {
        attackQueue.clear();
        comboCount = 0;
        b2bCount = 0;
    }

    public void pushClear(QuadmisClear clear) {
        if (clear.flags.contains(QuadmisClear.Flags.COMBO)) {
            comboCount++;
        } else {
            comboCount = 0;
        }

        if (clear.flags.contains(QuadmisClear.Flags.SPIN) ||
                clear.flags.contains(QuadmisClear.Flags.MINI_SPIN) ||
                clear.clearCount == 4) {
            int rawAmount = convertClearToAttack(clear);
            int amount = Objects.requireNonNull(parent.get()).reduceAttack(rawAmount);

            attackQueue.addLast(new QuadmisAttackByte(amount, clear, attackDelay, TimeUnit.MILLISECONDS));
            b2bCount++;
        } else {
            int rawAmount = convertClearToAttack(clear);
            int amount = Objects.requireNonNull(parent.get()).reduceAttack(rawAmount);

            b2bCount = 0;
            attackQueue.addLast(new QuadmisAttackByte(amount, clear, attackDelay, TimeUnit.MILLISECONDS));
        }
    }

    public void setTarget(QuadmisGrid grid) {
        target = new WeakReference<>(grid);
    }

    public QuadmisGrid getTarget() {
        return target.get();
    }

    @Contract(pure = true)
    public boolean nextAvailable() {
        return attackQueue.pollFirst() != null && Objects.requireNonNull(attackQueue.pollFirst()).getDelay(TimeUnit.NANOSECONDS) <= 0;
    }

    public QuadmisAttackByte popAttack() {
        return attackQueue.pop();
    }

    @Contract(pure = true)
    public int convertClearToAttack(@NotNull QuadmisClear clear) {
        if ((!clear.flags.contains(QuadmisClear.Flags.SPIN) && !(clear.flags.contains(QuadmisClear.Flags.MINI_SPIN) && b2bCount > 0)) && clear.clearCount == 1) {
            return comboCount >= 2 ? (int) Math.floor(Math.log1p(comboCount)) : 0;
        } else {
            if (clear.flags.contains(QuadmisClear.Flags.SPIN)) {
                return (2 * clear.clearCount + convertBTBToLevel(b2bCount)) * (comboCount + 4) / 4;
            } else if (clear.flags.contains(QuadmisClear.Flags.MINI_SPIN)) {
                return convertBTBToLevel(b2bCount) * (comboCount + 4) / 4;
            } else if (clear.clearCount == 4) {
                return (4 + convertBTBToLevel(b2bCount)) * (comboCount + 4) / 4;
            } else {
                return (clear.clearCount == 3 ? 2 : 1) * (comboCount + 4) / 4;
            }
        }
    }

    public int convertBTBToLevel(int btb) {
        if (btb < 1) {
            return 0;
        } else if (btb < 3) {
            return 1;
        } else if (btb < 8) {
            return 2;
        } else if (btb < 24) {
            return 3;
        } else if (btb < 67) {
            return 4;
        } else if (btb < 185) {
            return 5;
        } else if (btb < 504) {
            return 6;
        } else if (btb < 1370) {
            return 7;
        } else {
            return 8;
        }
    }

    public static class QuadmisClear {
        public enum Flags {
            COMBO,
            SPIN,
            MINI_SPIN
        }

        public final HashSet<Flags> flags = new HashSet<>();
        public final int clearCount;

        public QuadmisClear(int clearCount, Set<Flags> f) {
            flags.addAll(f);
            this.clearCount = clearCount;
        }
    }

    public static class QuadmisAttackByte implements Delayed {
        public int attackAmount;
        public final QuadmisClear clear;
        private final long startNanoTime = System.nanoTime();
        private final long nanoDuration;

        public QuadmisAttackByte(int attackAmount, QuadmisClear c, long delay, TimeUnit unit) {
            nanoDuration = TimeUnit.NANOSECONDS.convert(delay, unit);
            clear = c;
            this.attackAmount = attackAmount;
        }

        public QuadmisAttackByte(int attackAmount, QuadmisClear c, Duration duration) {
            nanoDuration = duration.toNanos();
            clear = c;
            this.attackAmount = attackAmount;
        }

        @Override
        public String toString() {
            StringBuilder s = new StringBuilder();
            if (clear.flags.contains(QuadmisClear.Flags.MINI_SPIN)) {
                s.append("mini T-Spin ");
            } else if (clear.flags.contains(QuadmisClear.Flags.SPIN)) {
                s.append("T-Spin ");
            }
            switch (clear.clearCount) {
                case 4 -> s.append("Quad ");
                case 3 -> s.append("Triple ");
                case 2 -> s.append("Double ");
                case 1 -> s.append("Single ");
            }

            s.append(attackAmount);

            return s.toString();
        }

        @Override
        public long getDelay(@NotNull TimeUnit unit) {
            return unit.convert(nanoDuration - (System.nanoTime() - startNanoTime), TimeUnit.NANOSECONDS);
        }

        @Override
        public int compareTo(@NotNull Delayed o) {
            long sD = getDelay(TimeUnit.NANOSECONDS);
            long cD = o.getDelay(TimeUnit.NANOSECONDS);
            return Long.compare(sD, cD);
        }
    }
}