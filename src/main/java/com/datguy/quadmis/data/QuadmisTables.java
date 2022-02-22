package com.datguy.quadmis.data;

import java.awt.Point;

// Redoing the rotation system
// -> Mino's 4 rotation states are stored as a variable-size grid
// -> Possible kick positions are stored as a series of vectors, in order of preference
//      * The vector represents the offset
public class QuadmisTables {

    // Rotations are stored North -> East -> South -> West, for reference.
    public static class Rotations {
        public static final boolean[][][] O = {
            {
                {true, true},
                {true, true}
            },
            {
                {true, true},
                {true, true}
            },
            {
                {true, true},
                {true, true}
            },
            {
                {true, true},
                {true, true}
            }
        };
        public static final boolean[][][] I = {
            {
                {false, false, false, false},
                {true, true, true, true},
                {false, false, false, false},
                {false, false, false, false}
            },
            {
                {false, false, true, false},
                {false, false, true, false},
                {false, false, true, false},
                {false, false, true, false}
            },
            {
                {false, false, false, false},
                {false, false, false, false},
                {true, true, true, true},
                {false, false, false, false}
            },
            {
                {false, true, false, false},
                {false, true, false, false},
                {false, true, false, false},
                {false, true, false, false}
            }
        };
        public static final boolean[][][] T = {
            {
                {false, true, false},
                {true, true, true},
                {false, false, false}
            },
            {
                {false, true, false},
                {false, true, true},
                {false, true, false}
            },
            {
                {false, false, false},
                {true, true, true},
                {false, true, false}
            },
            {
                {false, true, false},
                {true, true, false},
                {false, true, false}
            },
        };
        public static final boolean[][][] L = {
            {
                {false, false, true},
                {true, true, true},
                {false, false, false}
            },
            {
                {false, true, false},
                {false, true, false},
                {false, true, true}
            },
            {
                {false, false, false},
                {true, true, true},
                {true, false, false}
            },
            {
                {true, true, false},
                {false, true, false},
                {false, true, false}
            },
        };
        public static final boolean[][][] J = {
            {
                {true, false, false},
                {true, true, true},
                {false, false, false}
            },
            {
                {false, true, true},
                {false, true, false},
                {false, true, false}
            },
            {
                {false, false, false},
                {true, true, true},
                {false, false, true}
            },
            {
                {false, true, false},
                {false, true, false},
                {true, true, false}
            },
        };
        public static final boolean[][][] S = {
            {
                {true, false, false},
                {true, true, true},
                {false, false, false}
            },
            {
                {false, true, true},
                {false, true, false},
                {false, true, false}
            },
            {
                {false, false, false},
                {true, true, true},
                {false, false, true}
            },
            {
                {false, true, false},
                {false, true, false},
                {true, true, false}
            },
        };
        public static final boolean[][][] Z = {
            {
                {true, true, false},
                {false, true, true},
                {false, false, false}
            },
            {
                {false, false, true},
                {false, true, true},
                {false, true, false}
            },
            {
                {false, false, false},
                {true, true, false},
                {false, true, true}
            },
            {
                {false, true, false},
                {true, true, false},
                {true, false, false}
            },
        };
    }

    // Kicks are stored as a series of coordinate offsets, where 0,0 is the top-left of local coordinate space.
    // Note that points are allowed to be negative if a point falls outside local space, i.e. with T
    // Also note that I am not including duplicate points for readability
    public static class Kicks {
        // Unsurprisingly, O's kick table is extremely simplistic
        public static final Point[][] O = {
                {new Point(0, 0)},
                {new Point(0, 0)},
                {new Point(0, 0)},
                {new Point(0, 0)}
        };
        // I shows off multiple possible kicks
        public static final Point[][] I = {
                {new Point(1,1), new Point(0,1), new Point(3,1), new Point(0,1), new Point(3,1)},
                {new Point(1,1), new Point(2,1), new Point(2,1), new Point(2,0), new Point(2,3)},
                {new Point(1,1), new Point(3,1), new Point(0,1), new Point(3,2), new Point(0,2)},
                {new Point(1,1), new Point(1,1), new Point(1,1), new Point(1,3), new Point(1,0)}
        };
        public static final Point[][] T = {
                {new Point(1,1), new Point(1,1), new Point(1,1), new Point(1,1), new Point(1,1)},
                {new Point(1,1), new Point(2,1), new Point(2,2), new Point(1,-1), new Point(2,-1)},
                {new Point(1,1), new Point(1,1), new Point(1,1), new Point(1,1), new Point(1,1)},
                {new Point(1,1), new Point(0,1), new Point(0,2), new Point(1,-1), new Point(0,-1)}
        };
        public static final Point[][] L = {
                {new Point(1,1), new Point(1,1), new Point(1,1), new Point(1,1), new Point(1,1)},
                {new Point(1,1), new Point(2,1), new Point(2,2), new Point(1,-1), new Point(2,-1)},
                {new Point(1,1), new Point(1,1), new Point(1,1), new Point(1,1), new Point(1,1)},
                {new Point(1,1), new Point(0,1), new Point(0,2), new Point(1,-1), new Point(0,-1)}
        };
        public static final Point[][] J = {
                {new Point(1,1), new Point(1,1), new Point(1,1), new Point(1,1), new Point(1,1)},
                {new Point(1,1), new Point(2,1), new Point(2,2), new Point(1,-1), new Point(2,-1)},
                {new Point(1,1), new Point(1,1), new Point(1,1), new Point(1,1), new Point(1,1)},
                {new Point(1,1), new Point(0,1), new Point(0,2), new Point(1,-1), new Point(0,-1)}
        };
        public static final Point[][] S = {
                {new Point(1,1), new Point(1,1), new Point(1,1), new Point(1,1), new Point(1,1)},
                {new Point(1,1), new Point(2,1), new Point(2,2), new Point(1,-1), new Point(2,-1)},
                {new Point(1,1), new Point(1,1), new Point(1,1), new Point(1,1), new Point(1,1)},
                {new Point(1,1), new Point(0,1), new Point(0,2), new Point(-1,1), new Point(0,-1)}
        };
        public static final Point[][] Z = {
                {new Point(1,1), new Point(1,1), new Point(1,1), new Point(1,1), new Point(1,1)},
                {new Point(1,1), new Point(2,1), new Point(2,2), new Point(1,-1), new Point(2,-1)},
                {new Point(1,1), new Point(1,1), new Point(1,1), new Point(1,1), new Point(1,1)},
                {new Point(1,1), new Point(0,1), new Point(0,2), new Point(-1,1), new Point(0,-1)}
        };
    }
}
