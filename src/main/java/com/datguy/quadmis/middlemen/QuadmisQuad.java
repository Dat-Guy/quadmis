package com.datguy.quadmis.middlemen;

import com.datguy.quadmis.QuadmisKick;
import com.datguy.quadmis.QuadmisMetaQuad;
import com.datguy.quadmis.data.QuadmisTables;
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
     */
    public QuadmisQuad(QuadmisMetaQuad metaQuad) {
        this.metaQuad = metaQuad;
        p = (Point) metaQuad.getOrigin().clone();
        rot = 0;
    }

    public Class<? extends QuadmisMetaQuad> getMetaClass() {
        return metaQuad.getClass();
    }

    public Color getColor() {
        return metaQuad.getColor();
    }

    public int getRot() {
        return rot;
    }

    public QuadmisKick getRotLeft() {
        return metaQuad.getRotLeft(rot);
    }

    public QuadmisKick getRotRight() {
        return metaQuad.getRotRight(rot);
    }

    public void rotLeft() {
        rot = (rot - 1 < 0) ? 3 : rot - 1;
    }

    public void rotRight() {
        rot = (rot + 1 > 3) ? 0 : rot + 1;
    }

    public Point getPos() {
        return (Point) this.p.clone();
    }

    public boolean[][] getShape() {
        return this.metaQuad.getShape(rot);
    }

    public void reset() {
        setPos(metaQuad.getOrigin());
        rot = 0;
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
        public Point getOrigin() {
            return QuadmisTables.Origins.O;
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
                    return new Point(QuadmisTables.Kicks.O[startRot][index].x - QuadmisTables.Kicks.O[rot][index].x,
                                     QuadmisTables.Kicks.O[startRot][index].y - QuadmisTables.Kicks.O[rot][index].y);
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
                    return new Point(QuadmisTables.Kicks.O[startRot][index].x - QuadmisTables.Kicks.O[rot][index].x,
                                     QuadmisTables.Kicks.O[startRot][index].y - QuadmisTables.Kicks.O[rot][index].y);
                }
            };
        }
    }

    public static class I implements QuadmisMetaQuad {

        private final static Color color = Color.CYAN;

        @Override
        public Point getOrigin() {
            return QuadmisTables.Origins.I;
        }

        @Override
        public Color getColor() {
            return color;
        }

        @Override
        public boolean[][] getShape(int rot) {
            return QuadmisTables.Rotations.I[rot];
        }

        @Override
        public QuadmisKick getRotLeft(int startRot) {
            int rot = startRot - 1 < 0 ? 3 : startRot - 1;
            return new QuadmisKick() {
                @Override
                public boolean[][] getKickGeometry() {
                    return QuadmisTables.Rotations.I[rot];
                }

                @Override
                public Point getOffset(int index) {
                    return new Point(QuadmisTables.Kicks.I[startRot][index].x - QuadmisTables.Kicks.I[rot][index].x,
                                     QuadmisTables.Kicks.I[startRot][index].y - QuadmisTables.Kicks.I[rot][index].y);
                }
            };
        }

        @Override
        public QuadmisKick getRotRight(int startRot) {
            int rot = startRot + 1 > 3 ? 0 : startRot + 1;
            return new QuadmisKick() {
                @Override
                public boolean[][] getKickGeometry() {
                    return QuadmisTables.Rotations.I[rot];
                }

                @Override
                public Point getOffset(int index) {
                    return new Point(QuadmisTables.Kicks.I[startRot][index].x - QuadmisTables.Kicks.I[rot][index].x,
                                     QuadmisTables.Kicks.I[startRot][index].y - QuadmisTables.Kicks.I[rot][index].y);
                }
            };
        }
    }

    public static class T implements QuadmisMetaQuad {

        private final static Color color = Color.PURPLE;

        @Override
        public Point getOrigin() {
            return QuadmisTables.Origins.T;
        }

        @Override
        public Color getColor() {
            return color;
        }

        @Override
        public boolean[][] getShape(int rot) {
            return QuadmisTables.Rotations.T[rot];
        }

        @Override
        public QuadmisKick getRotLeft(int startRot) {
            int rot = startRot - 1 < 0 ? 3 : startRot - 1;
            return new QuadmisKick() {
                @Override
                public boolean[][] getKickGeometry() {
                    return QuadmisTables.Rotations.T[rot];
                }

                @Override
                public Point getOffset(int index) {
                    return new Point(QuadmisTables.Kicks.T[startRot][index].x - QuadmisTables.Kicks.T[rot][index].x,
                                     QuadmisTables.Kicks.T[startRot][index].y - QuadmisTables.Kicks.T[rot][index].y);
                }
            };
        }

        @Override
        public QuadmisKick getRotRight(int startRot) {
            int rot = startRot + 1 > 3 ? 0 : startRot + 1;
            return new QuadmisKick() {
                @Override
                public boolean[][] getKickGeometry() {
                    return QuadmisTables.Rotations.T[rot];
                }

                @Override
                public Point getOffset(int index) {
                    return new Point(QuadmisTables.Kicks.T[startRot][index].x - QuadmisTables.Kicks.T[rot][index].x,
                                     QuadmisTables.Kicks.T[startRot][index].y - QuadmisTables.Kicks.T[rot][index].y);
                }
            };
        }
    }

    public static class L implements QuadmisMetaQuad {

        private final static Color color = Color.ORANGE;

        @Override
        public Point getOrigin() {
            return QuadmisTables.Origins.L;
        }

        @Override
        public Color getColor() {
            return color;
        }

        @Override
        public boolean[][] getShape(int rot) {
            return QuadmisTables.Rotations.L[rot];
        }

        @Override
        public QuadmisKick getRotLeft(int startRot) {
            int rot = startRot - 1 < 0 ? 3 : startRot - 1;
            return new QuadmisKick() {
                @Override
                public boolean[][] getKickGeometry() {
                    return QuadmisTables.Rotations.L[rot];
                }

                @Override
                public Point getOffset(int index) {
                    return new Point(QuadmisTables.Kicks.L[startRot][index].x - QuadmisTables.Kicks.L[rot][index].x,
                                     QuadmisTables.Kicks.L[startRot][index].y - QuadmisTables.Kicks.L[rot][index].y);
                }
            };
        }

        @Override
        public QuadmisKick getRotRight(int startRot) {
            int rot = startRot + 1 > 3 ? 0 : startRot + 1;
            return new QuadmisKick() {
                @Override
                public boolean[][] getKickGeometry() {
                    return QuadmisTables.Rotations.L[rot];
                }

                @Override
                public Point getOffset(int index) {
                    return new Point(QuadmisTables.Kicks.L[startRot][index].x - QuadmisTables.Kicks.L[rot][index].x,
                                     QuadmisTables.Kicks.L[startRot][index].y - QuadmisTables.Kicks.L[rot][index].y);
                }
            };
        }
    }

    public static class J implements QuadmisMetaQuad {

        private final static Color color = Color.BLUE;

        @Override
        public Color getColor() {
            return color;
        }

        @Override
        public Point getOrigin() {
            return QuadmisTables.Origins.J;
        }

        @Override
        public boolean[][] getShape(int rot) {
            return QuadmisTables.Rotations.J[rot];
        }

        @Override
        public QuadmisKick getRotLeft(int startRot) {
            int rot = startRot - 1 < 0 ? 3 : startRot - 1;
            return new QuadmisKick() {
                @Override
                public boolean[][] getKickGeometry() {
                    return QuadmisTables.Rotations.J[rot];
                }

                @Override
                public Point getOffset(int index) {
                    return new Point(QuadmisTables.Kicks.J[startRot][index].x - QuadmisTables.Kicks.J[rot][index].x,
                                     QuadmisTables.Kicks.J[startRot][index].y - QuadmisTables.Kicks.J[rot][index].y);
                }
            };
        }

        @Override
        public QuadmisKick getRotRight(int startRot) {
            int rot = startRot + 1 > 3 ? 0 : startRot + 1;
            return new QuadmisKick() {
                @Override
                public boolean[][] getKickGeometry() {
                    return QuadmisTables.Rotations.J[rot];
                }

                @Override
                public Point getOffset(int index) {
                    return new Point(QuadmisTables.Kicks.J[startRot][index].x - QuadmisTables.Kicks.J[rot][index].x,
                                     QuadmisTables.Kicks.J[startRot][index].y - QuadmisTables.Kicks.J[rot][index].y);
                }
            };
        }
    }

    public static class S implements QuadmisMetaQuad {

        private final static Color color = Color.GREEN;

        @Override
        public Color getColor() {
            return color;
        }

        @Override
        public Point getOrigin() {
            return QuadmisTables.Origins.S;
        }

        @Override
        public boolean[][] getShape(int rot) {
            return QuadmisTables.Rotations.S[rot];
        }

        @Override
        public QuadmisKick getRotLeft(int startRot) {
            int rot = startRot - 1 < 0 ? 3 : startRot - 1;
            return new QuadmisKick() {
                @Override
                public boolean[][] getKickGeometry() {
                    return QuadmisTables.Rotations.S[rot];
                }

                @Override
                public Point getOffset(int index) {
                    return new Point(QuadmisTables.Kicks.S[startRot][index].x - QuadmisTables.Kicks.S[rot][index].x,
                                     QuadmisTables.Kicks.S[startRot][index].y - QuadmisTables.Kicks.S[rot][index].y);
                }
            };
        }

        @Override
        public QuadmisKick getRotRight(int startRot) {
            int rot = startRot + 1 > 3 ? 0 : startRot + 1;
            return new QuadmisKick() {
                @Override
                public boolean[][] getKickGeometry() {
                    return QuadmisTables.Rotations.S[rot];
                }

                @Override
                public Point getOffset(int index) {
                    return new Point(QuadmisTables.Kicks.S[startRot][index].x - QuadmisTables.Kicks.S[rot][index].x,
                                     QuadmisTables.Kicks.S[startRot][index].y - QuadmisTables.Kicks.S[rot][index].y);
                }
            };
        }
    }

    public static class Z implements QuadmisMetaQuad {

        private final static Color color = Color.RED;

        @Override
        public Color getColor() {
            return color;
        }

        @Override
        public Point getOrigin() {
            return QuadmisTables.Origins.Z;
        }

        @Override
        public boolean[][] getShape(int rot) {
            return QuadmisTables.Rotations.Z[rot];
        }

        @Override
        public QuadmisKick getRotLeft(int startRot) {
            int rot = startRot - 1 < 0 ? 3 : startRot - 1;
            return new QuadmisKick() {
                @Override
                public boolean[][] getKickGeometry() {
                    return QuadmisTables.Rotations.Z[rot];
                }

                @Override
                public Point getOffset(int index) {
                    return new Point(QuadmisTables.Kicks.Z[startRot][index].x - QuadmisTables.Kicks.Z[rot][index].x,
                                     QuadmisTables.Kicks.Z[startRot][index].y - QuadmisTables.Kicks.Z[rot][index].y);
                }
            };
        }

        @Override
        public QuadmisKick getRotRight(int startRot) {
            int rot = startRot + 1 > 3 ? 0 : startRot + 1;
            return new QuadmisKick() {
                @Override
                public boolean[][] getKickGeometry() {
                    return QuadmisTables.Rotations.Z[rot];
                }

                @Override
                public Point getOffset(int index) {
                    return new Point(QuadmisTables.Kicks.Z[startRot][index].x - QuadmisTables.Kicks.Z[rot][index].x,
                                     QuadmisTables.Kicks.Z[startRot][index].y - QuadmisTables.Kicks.Z[rot][index].y);
                }
            };
        }
    }
}
