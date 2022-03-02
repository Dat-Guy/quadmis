package com.datguy.quadmis.ai;

import com.datguy.quadmis.data.QuadmisGrid;

public class QuadmisCostAI implements QuadmisAI {

    @Override
    public void step(QuadmisGrid grid) {
        grid.applyGravity();
    }
}
