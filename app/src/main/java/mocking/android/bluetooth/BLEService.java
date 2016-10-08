package mocking.android.bluetooth;

import java.util.UUID;

/**
 * Created by moonh on 2016-10-08.
 */

public class BLEService {
    private int kind;

    public static final int SERVICE_UUID_ONLY=1;
    public static final int SERVICE_UUID_WITH_CHARACTERISTICS=2;
    public static final int SERVICE_UUID_WITH_CHARACTERISTICS_AND_DESCRIPTOR=3;

    private UUID serviceUuid;
    private UUID characteristicUuid;
    private int properties;
    private int permission;
    private UUID descriptorUuid;

    public BLEService(UUID serviceUuid) {
        kind = SERVICE_UUID_ONLY;
        this.serviceUuid = serviceUuid;
    }

    public BLEService(UUID serviceUuid, UUID characteristicUuid, int properties, int permission) {
        kind = SERVICE_UUID_WITH_CHARACTERISTICS;
        this.serviceUuid = serviceUuid;
        this.characteristicUuid = characteristicUuid;
        this.properties = properties;
        this.permission = permission;
    }

    public BLEService(UUID serviceUuid, UUID characteristicUuid, int properties, int permission, UUID descriptorUuid) {
        kind = SERVICE_UUID_WITH_CHARACTERISTICS_AND_DESCRIPTOR;
        this.serviceUuid = serviceUuid;
        this.characteristicUuid = characteristicUuid;
        this.properties = properties;
        this.permission = permission;
        this.descriptorUuid = descriptorUuid;
    }


    public int kind() { return kind; }
    public UUID serviceUuid() { return serviceUuid; }
    public UUID characteristicUuid() { return characteristicUuid; }
    public int properties() { return properties; }
    public int permission() { return permission; }
    public UUID descriptorUuid() { return descriptorUuid; }

}
