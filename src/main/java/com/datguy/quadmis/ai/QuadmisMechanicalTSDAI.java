package com.datguy.quadmis.ai;

import com.datguy.quadmis.data.QuadmisGrid;
import com.datguy.quadmis.middlemen.QuadmisQuad;
import org.jetbrains.annotations.NotNull;

public class QuadmisMechanicalTSDAI implements QuadmisAI {

    private boolean firstBag = true;
    private boolean firstStep = true;

    private boolean bagPairity = false;
    private boolean firstPairity = true;

    // Describes which pieces of the current bag have been placed, from left to right:
    // J, Z, T, S, O, L, I
    private int[] placed;
    private int iRight;
    private boolean unresolvedHold;

    public QuadmisMechanicalTSDAI() {
        reset();
    }

    @Override
    public void step(@NotNull QuadmisGrid grid) {

        int bagPos = grid.getBagPos() == 0 ? 7 : grid.getBagPos() - 1;

        if (firstStep) {
            int[] nextRaw = grid.getNextQueue();
            Class[] nextAsMeta = new Class[nextRaw.length + 1];
            nextAsMeta[0] = grid.getPiece().quad.getMetaClass();

            int index = 1;
            for (int raw : nextRaw) {
                nextAsMeta[index++] = QuadmisGrid.bagToMeta[raw].getClass();
            }

            boolean oFirst = false;
            boolean tFirst = false;

            for (Class quad : nextAsMeta) {
                System.out.println("Sieve: " + quad.getName().split("\\$")[1] + " (T: " + tFirst + ", O: " + oFirst + ")");

                if (quad == QuadmisQuad.O.class) {
                    if (tFirst)
                        break;
                    oFirst = true;
                }
                if (quad == QuadmisQuad.T.class) {
                    if (oFirst)
                        break;
                    tFirst = true;
                }
                if ((!tFirst && (quad == QuadmisQuad.J.class || quad == QuadmisQuad.Z.class)) || (!oFirst && quad == QuadmisQuad.S.class)) {
                    System.out.println("== Reset ==");
                    grid.internalReset();
                    step(grid);
                    return;
                }
            }

            // Beyond this point the AI will begin MTSDv2 loop
            firstStep = false;
            firstBagStep(grid);
        } else if (firstBag) {
            if (bagPos == 7) {
                firstBag = false;
            }
            firstBagStep(grid);
        } else {

            Class pieceClass = grid.getPiece().quad.getMetaClass();
            Class holdClass = grid.getHold() != null ? grid.getHold().quad.getMetaClass() : null;

            if (pieceClass == QuadmisQuad.J.class) {
                if (placed[0] - placed[1] == 1) {
                    grid.applyHold();
                    step(grid);
                    return;
                }
                placeJ(grid);
                placed[0]++;
            } else if (pieceClass == QuadmisQuad.Z.class) {
                if (placed[0] - placed[1] == -1) {
                    grid.applyHold();
                    step(grid);
                    return;
                }
                placeZ(grid);
                placed[1]++;
            } else if (pieceClass == QuadmisQuad.O.class) {
                if (placed[5] - placed[4] == -1) {
                    grid.applyHold();
                    step(grid);
                    return;
                }
                placeO(grid);
                placed[4]++;
                if (holdClass == QuadmisQuad.S.class) {
                    unresolvedHold = true;
                }
            } else if (pieceClass == QuadmisQuad.L.class) {
                placeL(grid);
                placed[5]++;
            } else if (pieceClass == QuadmisQuad.I.class) {
                if (bagPairity && firstPairity) {
                    firstPairity = false;
                    placeI(grid);
                    iRight++;
                } else {
                    if (bagPairity) {
                        grid.applyCCWRot();
                        grid.applyShiftLeft();
                        grid.applyFirmDrop();
                        grid.applyShiftRight();
                        grid.applyShiftRight();
                    } else {
                        placeI(grid);
                        iRight++;
                    }
                    placed[6]++;
                }
            }

            if (pieceClass == QuadmisQuad.S.class) {
                if (placed[3] - placed[4] == 0) {
                    grid.applyHold();
                    step(grid);
                    return;
                }
                placed[3]++;
                placeS(grid);
            }

            if (pieceClass == QuadmisQuad.T.class) {
                if (!isTopLayerReady()) {
                    grid.applyHold();
                    step(grid);
                    return;
                }
                grid.applyCWRot();
                grid.applyShiftLeft();
                grid.applyFirmDrop();
                grid.applyCWRot();
                placed[2]++;
            }

            grid.applyHardDrop();

            if (isTopLayerReady() && holdClass == QuadmisQuad.T.class) {
                grid.applyHold();
            }

            if (!(isTopLayerReady() && pieceClass == QuadmisQuad.T.class) && unresolvedHold) {
                grid.applyHold();
                unresolvedHold = false;
            }

            if (bagPos == 7) {
                bagPairity = !bagPairity;
            }
        }
    }

    @Override
    public void reset() {
        firstBag = true;
        firstStep = true;

        bagPairity = false;
        firstPairity = true;

        placed = new int[]{1, 1, 0, 1, 1, 1, 1};
        iRight = 1;
        unresolvedHold = false;
    }

    public void firstBagStep(QuadmisGrid grid) {
        Class pieceClass = grid.getPiece().quad.getMetaClass();

        if (pieceClass == QuadmisQuad.T.class) {
            grid.applyCCWRot();
            grid.applyCCWRot();
            grid.applyShiftLeft();
            grid.applyShiftLeft();
            grid.applyShiftLeft();
            grid.applyShiftLeft();
        } else if (pieceClass == QuadmisQuad.O.class) {
            placeO(grid);
        } else if (pieceClass == QuadmisQuad.I.class) {
            placeI(grid);
        } else if (pieceClass == QuadmisQuad.J.class) {
            placeJ(grid);
        } else if (pieceClass == QuadmisQuad.L.class) {
            placeL(grid);
        } else if (pieceClass == QuadmisQuad.S.class) {
            placeS(grid);
        } else if (pieceClass == QuadmisQuad.Z.class) {
            placeZ(grid);
        }
        grid.applyHardDrop();
    }

    public boolean isTopLayerReady() {
        return placed[3] <= placed[0] && placed[3] <= placed[1] && placed[3] <= placed[4] && placed[3] <= placed[5] && placed[3] <= (iRight * 2 - 1);
    }

    public void placeJ(QuadmisGrid grid) {
        grid.applyCWRot();
        grid.applyShiftLeft();
        grid.applyShiftLeft();
        grid.applyShiftLeft();
        grid.applyShiftLeft();
    }

    public void placeZ(QuadmisGrid grid) {
        grid.applyCCWRot();
        grid.applyShiftLeft();
        grid.applyFirmDrop();
        grid.applyShiftLeft();
    }

    public void placeS(QuadmisGrid grid) {
        grid.applyShiftRight();
    }

    public void placeO(QuadmisGrid grid) {
        grid.applyShiftRight();
        grid.applyShiftRight();
        grid.applyFirmDrop();
        grid.applyShiftLeft();
        grid.applyGravity();
        grid.applyGravity();
        grid.applyGravity();
        grid.applyShiftRight();
    }

    public void placeL(QuadmisGrid grid) {
        grid.applyCCWRot();
        grid.applyShiftRight();
        grid.applyShiftRight();
        grid.applyShiftRight();
        grid.applyShiftRight();
    }

    public void placeI(QuadmisGrid grid) {
        grid.applyCWRot();
        grid.applyShiftRight();
        grid.applyShiftRight();
        grid.applyShiftRight();
        grid.applyShiftRight();
    }
}
