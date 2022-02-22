package com.datguy.quadmis;

import java.awt.Point;

public interface QuadmisKick {
    boolean[][] getKickGeometry();
    // Note: should return null on OOB
    Point getOffset(int index);
}
