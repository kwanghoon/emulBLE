package emul.bluetooth.model;

import android.graphics.Path;

import java.util.ArrayList;

/**
 * Created by khChoi on 2017-08-17.
 */

public class Scenario {
    private ArrayList<BLEState> path;

    public Scenario(ArrayList<BLEState> path) {
        this.path = path;
    }

    public ArrayList<BLEState> path() { return this.path; }
}
