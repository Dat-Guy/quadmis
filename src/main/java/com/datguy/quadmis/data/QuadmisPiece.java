package com.datguy.quadmis.data;

import java.lang.ref.WeakReference;

public class QuadmisPiece {
    // Handles the active piece, interprets user input
    // Should store:
    // -> The piece's current geometry in local space
    // -> The piece's position in the grid

    public final WeakReference<QuadmisGrid> parent;
    public final QuadmisQuad quad;

    public QuadmisPiece(QuadmisQuad quad, QuadmisGrid parent) {
        this.quad = quad;
        this.parent = new WeakReference<>(parent); // stored as a weak reference to prevent circular references
    }

}