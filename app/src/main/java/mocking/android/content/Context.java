package mocking.android.content;

import mocking.android.bluetooth.BluetoothManager;

/**
 * Created by moonhyeonah on 2016. 5. 22..
 */
public class Context extends android.test.mock.MockContext {
    @Override
    public Object getSystemService(String name) {
        if (name.equals(android.content.Context.BLUETOOTH_SERVICE))
            return new BluetoothManager();
        else
            return super.getSystemService(name);
    }
}
