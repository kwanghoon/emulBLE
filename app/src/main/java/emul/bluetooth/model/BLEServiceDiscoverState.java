package emul.bluetooth.model;

import com.h3.hrm3200.Log;

import java.util.ArrayList;
import mocking.android.bluetooth.BLEService;
import mocking.android.bluetooth.IBLEDiscoverService;

/**
 * Created by khChoi on 2017-08-05.
 */

public class BLEServiceDiscoverState extends BLEState {
    private int succ_or_fail;
    private ArrayList<BLEService> bleServiceList;

    public BLEServiceDiscoverState(int succ_or_fail, ArrayList<BLEService> bleServiceList) {
        this(null, succ_or_fail, bleServiceList);
    }

    public BLEServiceDiscoverState(ArrayList<BLEState> next, int succ_or_fail, ArrayList<BLEService> bleServiceList) {
        this.next = next;
        this.succ_or_fail = succ_or_fail;
        this.bleServiceList = bleServiceList;
    }

    @Override
    public void setupAction() {
    }

    @Override
    public void action(IBLEDiscoverService ibleDiscoverService) {
        // Condtion: This method should be called in doDiscoverService()

        // Return succ_or_fail and bleServiceList
        ibleDiscoverService.setResult(succ_or_fail, bleServiceList);
    }
}
