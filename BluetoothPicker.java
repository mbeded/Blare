package com.mbeded.blare;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.Set;

public class BluetoothPicker extends AppCompatActivity {

    public static final String ACTION_DEVICE_SELECTED = "android.bluetooth.devicepicker.action.DEVICE_SELECTED";

    /**
     * Ask device picker to show all kinds of BT devices
     */
    public static final int FILTER_TYPE_ALL = 0;
    /**
     * Ask device picker to show BT devices that support AUDIO profiles
     */
    public static final int FILTER_TYPE_AUDIO = 1;
    /**
     * Ask device picker to show BT devices that support Object Transfer
     */
    public static final int FILTER_TYPE_TRANSFER = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.device_picker);

        BluetoothDevice device = getSelectedDevice();

        if (device == null) {
            Log.e("PRINT", "Failed to get selected bluetooth device!");
            finish();
            return;
        }

        Intent intent = getIntent();
        //mNeedAuth = intent.getBooleanExtra(EXTRA_NEED_AUTH, false);
        //setFilter(intent.getIntExtra(EXTRA_FILTER_TYPE, FILTER_TYPE_ALL));

        sendDevicePickedIntent(device);

        finish();
    }

    private void sendDevicePickedIntent(BluetoothDevice device) {
        Intent intent = new Intent(ACTION_DEVICE_SELECTED);
        intent.putExtra(BluetoothDevice.EXTRA_DEVICE, device);
        sendBroadcast(intent, device.getAddress());
    }

    public static BluetoothDevice getSelectedDevice() {
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice itD = null;
        if (!btAdapter.isEnabled()) {
            Log.e("Printer", "Bluetooth adapter is not enabled!");
            return null;
        }

        Set<BluetoothDevice> devices = btAdapter.getBondedDevices();
        Log.i("Printer", "Automatic phone selection");

        // Take the first printer paired
        for (BluetoothDevice itDevice : devices) {
            if (itDevice.getBluetoothClass().getMajorDeviceClass() == BluetoothClass.Device.Major.PHONE) {
                Log.i("Printer", "Using phone " + itDevice.getName() + " selected automatically");
                itD =  itDevice;
            } else {

                Log.e("Printer", "No usable printer!");
                itD = null;
            }
        }
        return itD;
    }
}

