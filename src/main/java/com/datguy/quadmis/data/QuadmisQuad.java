package com.datguy.quadmis.data;

import com.datguy.quadmis.QuadmisKick;
import com.datguy.quadmis.QuadmisMetaQuad;
import javafx.scene.paint.Color;

import java.awt.Point;

/**
 * Interfaces between the kick table data and high-level functionality
 */
public class QuadmisQuad {
    // Should store kick table, and current state (position/rotation within the grid)
    private Point p;
    private int rot;
    private final QuadmisMetaQuad metaQuad;

    /**
     * @param metaQuad The quad to use as a template (i.e. O)
     * @param startX The starting position's x coordinate
     * @param startY The starting position's y coordinate
     */
    public QuadmisQuad(QuadmisMetaQuad metaQuad, int startX, int startY) {
        this.metaQuad = metaQuad;
        p = new Point(startX, startY);
        rot = 0;
    }

    public Color getColor() {
        return metaQuad.getColor();
    }

    public QuadmisKick getRotLeft() {
        return metaQuad.getRotLeft(rot);
    }

    public QuadmisKick getRotRight() {
        return metaQuad.getRotRight(rot);
    }

    public void rotLeft() {
        rot = rot == 0 ? 3 : rot - 1;
    }

    public void rotRight() {
        rot = rot == 3 ? 0 : rot + 1;
    }

    public Point getPos() {
        return (Point) this.p.clone();
    }

    public boolean[][] getShape() {
        return this.metaQuad.getShape(rot);
    }

    public void setPos(Point p) {
        this.p = p;
    }

    public static class O implements QuadmisMetaQuad {

        private final static Color color = Color.YELLOW;

        @Override
        public Color getColor() {
            return color;
        }

        @Override
        public boolean[][] getShape(int rot) {
            return QuadmisTables.Rotations.O[rot];
        }

        @Override
        public QuadmisKick getRotLeft(int startRot) {
            int rot = startRot - 1 < 0 ? 3 : startRot - 1;
            return new QuadmisKick() {
                @Override
                public boolean[][] getKickGeometry() {
                    return QuadmisTables.Rotations.O[rot];
                }

                @Override
                public Point getOffset(int index) {
                    return new Point(QuadmisTables.Kicks.O[rot][index].x - QuadmisTables.Kicks.O[startRot][index].x,
                                     QuadmisTables.Kicks.O[rot][index].y - QuadmisTables.Kicks.O[startRot][index].y);
                }
            };
        }

        @Override
        public QuadmisKick getRotRight(int startRot) {
            int rot = startRot + 1 > 3 ? 0 : startRot + 1;
            return new QuadmisKick() {
                @Override
                public boolean[][] getKickGeometry() {
                    return QuadmisTables.Rotations.O[rot];
                }

                @Override
                public Point getOffset(int index) {
                    return new Point(QuadmisTables.Kicks.O[rot][index].x - QuadmisTables.Kicks.O[startRot][index].x,
                            QuadmisTables.Kicks.O[rot][index].y - QuadmisTables.Kicks.O[startRot][index].y);
                }
            };
        }
    }
}
