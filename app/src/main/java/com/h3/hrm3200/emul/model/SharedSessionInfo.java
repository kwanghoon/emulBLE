package com.h3.hrm3200.emul.model;

import emul.bluetooth.model.BLEState;

/**
 * Created by khChoi on 2017-08-17.
 */

public class SharedSessionInfo {
    public int sessionNumber;

    public int dataTotalCount;  // N

    public int dataCount;  // 0, 1, 2, .. N

    public int heartrate;
    public int calorie;
    public int steps;
}
