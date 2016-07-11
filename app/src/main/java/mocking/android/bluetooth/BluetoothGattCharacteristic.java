package mocking.android.bluetooth;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by moonhyeonah on 2016. 5. 22..
 */
public class BluetoothGattCharacteristic {
    public final static int FORMAT_UINT8 = 0x00000011;
    public final static int FORMAT_UINT16 = 0x00000012;

    public static final int PROPERTY_NOTIFY = 0x00000010;              // MHEALTH_MEASUREMENT_READ
    public static final int PROPERTY_WRITE_NO_RESPONSE = 0x00000004;   // MHEALTH_MEASUREMENT_WRITE
    public final static int PROPERTY_READ = 0x00000002;

    public final static int PERMISSION_WRITE = 0x00000010;

    private UUID uuid;
    private byte[] value;
    private int properties;
    private int permissions;
    protected List<BluetoothGattDescriptor> descriptors;

    BluetoothGattCharacteristic(UUID uuid, int properties, int permissions) {
        this.uuid = uuid;
        this.properties = properties;
        this.permissions = permissions;
        descriptors = new ArrayList<BluetoothGattDescriptor>();
    }
    public UUID getUuid() {
        return uuid;
    }

    public BluetoothGattDescriptor getDescriptor(UUID uuid) {
        for (BluetoothGattDescriptor descriptor : descriptors) {
            if (descriptor.getUuid().equals(uuid)) {
                return descriptor;
            }
        }
        return null;
    }
    public int getProperties(){
        return properties;
    }
    public Integer getIntValue(int formatType, int offset) {
        int val = value[offset] & 0xff;
        return val;
    }
    public byte[] getValue() {
        return value;
    }
    public boolean setValue(byte[] value) {
        this.value = value;
        return true;
    }
    boolean addDescriptor(BluetoothGattDescriptor descriptor) {
        descriptors.add(descriptor);
        return true;
    }

    boolean addDescriptor(UUID descriptorUuid) {
        descriptors.add(new BluetoothGattDescriptor(descriptorUuid));
        return true;
    }
}
