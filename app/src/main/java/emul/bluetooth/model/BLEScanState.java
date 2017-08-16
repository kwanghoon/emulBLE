package emul.bluetooth.model;

import com.h3.hrm3200.Log;

import java.util.ArrayList;

import mocking.android.bluetooth.IBLEScan;

/**
 * Created by khChoi on 2017-08-05.
 */

public class BLEScanState extends BLEState {
    private String btdev_address;
    private String btdev_name;

    public BLEScanState(String btdev_address, String btdev_name) {
        this(null, btdev_address, btdev_name);
    }

    public BLEScanState(ArrayList<BLEState> next, String btdev_address, String btdev_name) {
        this.next = next;
        this.btdev_address = btdev_address;
        this.btdev_name = btdev_name;
    }

    @Override
    public void setupAction() {
        Log.v("[ HRM3200 ]", "BLEScanState - Push the bluetooth scan button ...");
    }

    @Override
    public void action(IBLEScan iblescan) {
        // Condition: This method should be called in doBLEScan()

        // Return address and name
        iblescan.setBLEAddressName(new String[] {
                btdev_address, btdev_name
        });
    }
}
