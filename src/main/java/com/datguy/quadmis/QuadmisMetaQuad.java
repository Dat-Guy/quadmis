package com.datguy.quadmis;

import javafx.scene.paint.Color;

public interface QuadmisMetaQuad {
    // Kick tables are structured for
    // [current rotation][row][col]
    // boolean[][][] leftTable = new boolean[4][][];
    // boolean[][][] rightTable = new boolean[4][][];

    // Gets the current shape
    boolean[][] getShape(int rot);

    // Gets the kick table entry for a CCW rotation
    QuadmisKick getRotLeft(int startRot);

    // Gets the kick table entry for a CW rotation
    QuadmisKick getRotRight(int startRot);

    // Gets the color
    default Color getColor(){
        return Color.color(1, 1, 1);
    }
}