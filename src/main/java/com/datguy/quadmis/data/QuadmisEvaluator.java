package com.datguy.quadmis.data;

public class QuadmisEvaluator {
    private double btbFactor;
    private double comboFactor;
    private double heightFactor;
    private double heightFactorMin;
    private double bumpinessFactor;
    private double allClearFactor;
    private double attackFactor;
    private double parityFactor;
    private double deathFactor;

    public QuadmisEvaluator(double btbFactor, double comboFactor, double heightFactor, double heightFactorMin, double bumpinessFactor, double allClearFactor, double attackFactor, double parityFactor, double deathFactor) {
        this.btbFactor = btbFactor;
        this.comboFactor = comboFactor;
        this.heightFactor = heightFactor;
        this.heightFactorMin = heightFactorMin;
        this.bumpinessFactor = bumpinessFactor;
        this.allClearFactor = allClearFactor;
        this.attackFactor = attackFactor;
        this.parityFactor = parityFactor;
        this.deathFactor = deathFactor;
    }

    public QuadmisApplyQueue eval(QuadmisGrid grid, int depth) {
        // We essentially want to generate *every possible placement*, then prune for the ones which can actually be attained via a series of kicks:
        // Step 1: Test every possible rotation and position, and save the ones which
        // a. Don't collide with the grid
        // b. Collide with the grid 1 unit down
        // Step 2: Prune out the unattainable ones
        // a. attempt to generate a set of kicks to a piece state which exposes it to the skyline, aka. could fall into that state
        // b. ensure the kicks are reversible
        // Step 3: Score each resulting board state
        // Step 4: Generate the queue for the best scoring state

        return null;
    }
}
