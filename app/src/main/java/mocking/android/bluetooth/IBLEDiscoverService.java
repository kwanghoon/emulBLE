package mocking.android.bluetooth;

import java.util.ArrayList;

/**
 * Created by moonhyeonah on 2016. 10. 3..
 */
public interface IBLEDiscoverService {
    void setResult(int succ_or_fail, ArrayList<BLEService> bleServiceList);

}
