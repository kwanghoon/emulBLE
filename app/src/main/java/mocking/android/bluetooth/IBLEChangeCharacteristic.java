package mocking.android.bluetooth;

import java.util.UUID;

/**
 * Created by moonh on 2016-10-05.
 */

public interface IBLEChangeCharacteristic {
    void setResult(UUID serviceUuid, UUID characteristicUuid, byte[] bytes);
}
