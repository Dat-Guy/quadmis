package com.datguy.quadmis.data;

import javafx.scene.paint.Color;

public class QuadmisBlock {
    public final Color color;
    private final boolean[] connections;

    public QuadmisBlock(Color color, boolean[] startingConnections) {
        this.color = color;

        if (startingConnections == null || startingConnections.length != 8) {
            startingConnections = new boolean[]{false, false, false, false, false, false, false, false};
        }
        this.connections = startingConnections;
    }

    public boolean[] getConnections() {
        return connections.clone();
    }

    public void removeConnection(int index) {
        connections[index] = false;
    }

}
