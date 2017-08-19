package emul.bluetooth.model.util;

import emul.bluetooth.model.BLEState;
import emul.bluetooth.model.util.Vertex;

/**
 * Created by khChoi on 2017-08-18.
 */

public class VertexBLEState extends Vertex {
    private BLEState bleState;
    public VertexBLEState(String stateNumber, BLEState bleState) {
        super(stateNumber);
        this.bleState = bleState;
    }
}
