package org.altbeacon.beacon.service.scanner;

import android.bluetooth.BluetoothDevice;

import org.altbeacon.beacon.Beacon;

/**
 * Created by jh on 17-5-9.
 */

public interface OnFailScanCallback {
    void onScanFailed(int code);

    void onNearCallBack(Beacon beacon);
}
