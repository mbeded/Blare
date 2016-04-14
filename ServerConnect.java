package com.mbeded.blare;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

public class ServerConnect extends Thread {
    private BluetoothSocket bTSocket;

    public ServerConnect() { }

    public void acceptConnect(BluetoothAdapter bTAdapter, UUID mUUID) {
        BluetoothServerSocket temp = null;
        Log.d("Printer", "sa1");
        try {
            Log.d("Printer", "sa2");
            temp = bTAdapter.listenUsingRfcommWithServiceRecord("Service_Name", mUUID);
            Log.d("Printer", "sa3");
        } catch(IOException e) {
            Log.d("Printer", "Could not get a BluetoothServerSocket:" + e.toString());
        }
        while(true) {
            try {
                Log.d("Printer", "sa4");
                assert temp != null;
                bTSocket = temp.accept();
                Log.d("Printer", "sa5");
            } catch (IOException e) {
                Log.d("Printer", "Could not accept an incoming connection.");
                break;
            }
            if (bTSocket != null) {
                try {
                    Log.d("Printer", "sa6");
                    temp.close();
                } catch (IOException e) {
                    Log.d("Printer", "Could not close ServerSocket:" + e.toString());
                }
                break;
            }
        }
    }

    public void closeConnect() {
        try {
            bTSocket.close();
        } catch(IOException e) {
            Log.d("Printer", "Could not close connection:" + e.toString());
        }
    }
}