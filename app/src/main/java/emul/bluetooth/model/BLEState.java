package emul.bluetooth.model;

import java.util.ArrayList;

/**
 * Created by khChoi on 2017-08-05.
 */

public abstract class BLEState {
    protected ArrayList<BLEState> next = new ArrayList<BLEState>();

    // action()
    public BLEState next(int index) {
        return this.next.get(index);
    }

    public void addNext(BLEState state) { this.next.add(state); }

    public int size() { return next.size(); }
}
