package mocking.android.bluetooth;

import java.util.UUID;

/**
 * Created by moonhyeonah on 2016. 5. 23..
 */
public class BluetoothGattDescriptor {
    public static final byte[] ENABLE_NOTIFICATION_VALUE = {0x01, 0x00};

    protected byte[] mValue;
    protected UUID uuid;

    public BluetoothGattDescriptor(UUID uuid) {
        this.uuid = uuid;
    }

    // BluetoothGatt.writeDescriptor() 호출하면 불리는?
    public boolean setValue(byte[] value) {
        mValue = value;
        return true;
    }

    public UUID getUuid() {
        return uuid;
    }
}
