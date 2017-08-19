package emul.bluetooth.model.util;

import emul.bluetooth.model.BLEState;

/**
 * Created by khChoi on 2017-08-18.
 */

public class VertexBLEState extends Vertex {
    private BLEState bleState;
    public VertexBLEState(String stateNumber, BLEState bleState) {
        this(stateNumber, Vertex.INTERMEDIATE_STATE, bleState);
    }

    public VertexBLEState(String stateNumber, int kind, BLEState bleState) {
        super(stateNumber, kind);
        this.bleState = bleState;
    }
}
