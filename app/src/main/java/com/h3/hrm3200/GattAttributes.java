package com.h3.hrm3200;

import java.util.HashMap;

/**
 * Created by moonhyeonah on 2015. 4. 4..
 */
public class GattAttributes {
    private static HashMap<String, String> attributes = new HashMap();

    public final static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
    public final static String HEART_RATE_SERVICE = "0000180d-0000-1000-8000-00805f9b34fb";

    public final static String BATTERY_SERVICE = "0000180f-0000-1000-8000-00805f9b34fb";
    public final static String BATTERY_LEVEL = "00002a19-0000-1000-8000-00805f9b34fb";

    public final static String MHEALTH_MEASUREMENT_WRITE = "0000ff02-0000-1000-8000-00805f9b34fb";
    public final static String MHEALTH_MEASUREMENT_READ = "0000ff01-0000-1000-8000-00805f9b34fb";
    public final static String MHEALTH_SERVICE = "0000ff00-0000-1000-8000-00805f9b34fb";

    public final static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    static {
        // Sample Services.
        attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Heart Rate Service");
        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
        // Sample Characteristics.
        attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement");
        attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
