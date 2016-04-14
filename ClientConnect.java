package com.mbeded.blare;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

public class ClientConnect extends Thread {
    private BluetoothSocket bTSocket = null;

    public boolean connect(BluetoothDevice bTDevice, UUID mUUID) {
        Log.d("Printer", "entered here");
        //BluetoothSocket temp = null;
        try {
            Log.d("Printer", "ca1");
            bTSocket = bTDevice.createRfcommSocketToServiceRecord(mUUID);
            Log.d("Printer", "ca2");
        } catch (IOException e) {
            Log.d("Printer", "Could not create RFCOMM socket:" + e.toString());
            return false;
        }
        try {
            Log.d("Printer", "ca3");
            bTSocket.connect();
            Log.d("Printer", "ca4");
        } catch (IOException e) {
            Log.d("Printer", "Could not connect: " + e.toString());
            try {
                bTSocket.close();
                Log.d("Printer", "ca5");
            } catch (IOException close) {
                Log.d("Printer", "Could not close connection:" + e.toString());
                return false;
            }
        }
        Log.d("Printer", "ca6");
        return true;
    }

    public boolean cancel() {
        try {
            bTSocket.close();
        } catch (IOException e) {
            Log.d("Printer", "Could not close connection:" + e.toString());
            return false;
        }
        return true;
    }
}
