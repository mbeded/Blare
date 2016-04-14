package com.mbeded.blare;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    BluetoothAdapter blueDevice = BluetoothAdapter.getDefaultAdapter();

    private ProgressBar spinner;
    private Boolean isGetPress = false;
    private Boolean isSndPress = false;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab, get, snd;
    private Animation fab_open_get, fab_close_get, fab_open_snd, fab_close_snd, rotate_forward, rotate_backward;

    ListView paired_list;
    ListView avail_list;
    ListView app_list;
    ArrayAdapter<String> clear;
    ArrayAdapter<String> pairadapter;
    ArrayAdapter<String> availadapter;
    ArrayAdapter<String> appadapter;

    String pairStore = "";
    String availStore = "";
    List<String> appItems = new ArrayList<String>();
    List<String> pairedItems = new ArrayList<String>();
    List<String> availItems = new ArrayList<String>();
    List<String> clearItems = new ArrayList<String>();
    List<String> storeappItems = new ArrayList<String>();
    List<String> storepairedItems = new ArrayList<String>();
    List<String> storeavailItems = new ArrayList<String>();
    List<BluetoothDevice> foundItems = new ArrayList<BluetoothDevice>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        get = (FloatingActionButton) findViewById(R.id.get);
        snd = (FloatingActionButton) findViewById(R.id.snd);
        fab_open_get = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open_get);
        fab_close_get = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close_get);
        fab_open_snd = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open_snd);
        fab_close_snd = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close_snd);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);
        fab.setOnClickListener(this);
        get.setOnClickListener(this);
        snd.setOnClickListener(this);
    }

    public String getPhoto() {
        String[] projection = new String[]{
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.MIME_TYPE
        };
        final Cursor cursor = getContentResolver()
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
                        null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");

        assert cursor != null;
        if (cursor.moveToFirst()) {
            String imageLocation = cursor.getString(1);
            File imageFile = new File(imageLocation);
            if (imageFile.exists()) {   // TODO: is there a better way to do this?
                return imageLocation;
            } else {
                Log.d("Printer", "NOFILE");
                return null;
            }
        } else {
            Log.d("Printer", "NOTINGHAPPENED");
            return null;
        }
    }

    public void sendFile(BluetoothDevice device) {

        Log.d("Printer", "Sending file...");

        File manualFile = new File(getPhoto());
        Uri uri = Uri.fromFile(manualFile);
        String type = "application/jpg";

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType(type);
        sharingIntent.setClassName("com.android.bluetooth", "com.android.bluetooth.opp.BluetoothOppLauncherActivity");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(sharingIntent);
        Log.d("Printer", "dfhg file...");
    }


    private AdapterView.OnItemClickListener apper = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> ava, View va, int posa, long arga) {
            Log.d("Printer", "Apper");
            storepairedItems.add(storeappItems.get(posa));
            storeappItems.remove(posa);
            app_list = (ListView) findViewById(R.id.appList);
            appadapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1,
                    storeappItems);
            app_list.setAdapter(appadapter);
            paired_list = (ListView) findViewById(R.id.pairedList);
            pairadapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1,
                    storepairedItems);
            paired_list.setAdapter(pairadapter);
        }
    };

    private AdapterView.OnItemClickListener creator = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> avc, View vc, int posc, long argc) {
            Log.d("Printer", "Creator");
            appItems.add(storepairedItems.get(posc));
            storepairedItems.remove(posc);
            String info = ((TextView) vc).getText().toString();
            String name = info.substring(0, info.length() - 17);
            String address = info.substring(info.length() - 17);
            Log.d("Printer", info);
            Log.d("Printer", name);
            Log.d("Printer", address);
            app_list = (ListView) findViewById(R.id.appList);
            app_list.setOnItemClickListener(apper);
            app_list.setVisibility(View.VISIBLE);
            appadapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1,
                    appItems);
            app_list.setAdapter(appadapter);
        }
    };

    private AdapterView.OnItemClickListener pairer = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> avp, View vp, int posp, long argp) {
            Log.d("Printer", "pairer");
            ClientConnect clientConnect = new ClientConnect();
            BluetoothDevice currdevice = foundItems.get(posp);
            UUID myUUID = UUID.fromString("ec79da00-853f-11e4-b4a9-0800200c9a66");
            clientConnect.connect(currdevice, myUUID);
            Log.w("Printer", String.valueOf(myUUID));
            updateAvailApp(posp, currdevice.getName(), currdevice.getAddress());
            sendFile(currdevice);
        }
    };

    public void updateAvailApp(int pos, String name, String add) {
        appItems.add(storeavailItems.get(pos));
        storeavailItems.remove(pos);
        Log.d("Printer", name);
        Log.d("Printer", add);
        app_list = (ListView) findViewById(R.id.appList);
        app_list.setOnItemClickListener(apper);
        app_list.setVisibility(View.VISIBLE);
        appadapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, appItems);
        app_list.setAdapter(appadapter);
    }

    public void bondedPrint() {
        TextView appHeader = (TextView) findViewById(R.id.appView);
        appHeader.setVisibility(View.VISIBLE);
        TextView availHeader = (TextView) findViewById(R.id.availView);
        availHeader.setVisibility(View.VISIBLE);
        TextView pairedHeader = (TextView) findViewById(R.id.pairView);
        pairedHeader.setVisibility(View.VISIBLE);
        Set<BluetoothDevice> pairedDevices = blueDevice.getBondedDevices();
        if (pairedDevices.size() > 0) {
            Log.d("Printer", "foundDevices");
            for (BluetoothDevice device : pairedDevices) {
                Log.w("Printer", device.getName() + "\n" + device.getAddress());
                pairStore = device.getName() + "\n" + device.getAddress();
                Log.d("Printer", "listUpdated");
                pairedItems.add(pairStore);
            }
            paired_list = (ListView) findViewById(R.id.pairedList);
            paired_list.setOnItemClickListener(creator);
            paired_list.setVisibility(View.VISIBLE);
            pairadapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1,
                    pairedItems);
            paired_list.setAdapter(pairadapter);
            storepairedItems = pairedItems;
            Log.d("Printer", "listPrinted");
        } else {
            Log.d("Printer", "NofoundDevices");
        }
        spinner = (ProgressBar) findViewById(R.id.load);
        spinner.setVisibility(View.VISIBLE);
        blueDevice.startDiscovery();
    }

    public void availPrint() {
        spinner = (ProgressBar) findViewById(R.id.load);
        spinner.setVisibility(View.INVISIBLE);
        avail_list = (ListView) findViewById(R.id.availList);
        avail_list.setOnItemClickListener(pairer);
        avail_list.setVisibility(View.VISIBLE);
        availadapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1,
                availItems);
        avail_list.setAdapter(availadapter);
        storeavailItems = availItems;
        Log.d("Printer", "listAvail");
    }

    public void clearDisplay() {
        TextView appHeader = (TextView) findViewById(R.id.appView);
        TextView pairedHeader = (TextView) findViewById(R.id.pairView);
        TextView availHeader = (TextView) findViewById(R.id.availView);
        spinner = (ProgressBar) findViewById(R.id.load);
        spinner.setVisibility(View.INVISIBLE);
        appHeader.setVisibility(View.INVISIBLE);
        pairedHeader.setVisibility(View.INVISIBLE);
        availHeader.setVisibility(View.INVISIBLE);
        app_list = (ListView) findViewById(R.id.appList);
        paired_list = (ListView) findViewById(R.id.pairedList);
        avail_list = (ListView) findViewById(R.id.availList);
        storeappItems = appItems;
        storepairedItems = pairedItems;
        storeavailItems = availItems;
        pairedItems.clear();
        availItems.clear();
        appItems.clear();
        paired_list = (ListView) findViewById(R.id.pairedList);
        avail_list = (ListView) findViewById(R.id.availList);
        clear = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1,
                clearItems);
        app_list.setAdapter(clear);
        paired_list.setAdapter(clear);
        avail_list.setAdapter(clear);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d("Printer", "Inside!");
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                if (state == BluetoothAdapter.STATE_ON && isSndPress) {
                    isSndPress = false;
                    Log.d("Printer", "State-on sender");
                    bondedPrint();
                } else if (state == BluetoothAdapter.STATE_ON && isGetPress) {
                    isGetPress = false;
                    Log.d("Printer", "State-on getter");
                    ServerConnect serverConnect = new ServerConnect();
                    UUID myUUID = UUID.fromString("ec79da00-853f-11e4-b4a9-0800200c9a66");
                    serverConnect.acceptConnect(blueDevice, myUUID);
                    Log.w("Printer", String.valueOf(myUUID));
                }
            }
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                Log.d("Printer", "DiscoveryStarted");
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.d("Printer", "DiscoveryFinished");
                availPrint();
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                foundItems.add(device);
                Log.w("Printer", device.getName() + "\n" + device.getAddress());
                availStore = device.getName() + "\n" + device.getAddress();
                availItems.add(availStore);
            }
        }
    };

    public void animateFAB() {
        if (isFabOpen) {
            ViewCompat.animate(fab);
            fab.startAnimation(rotate_backward);
            snd.startAnimation(fab_close_snd);
            get.startAnimation(fab_close_get);
            snd.setClickable(false);
            get.setClickable(false);
            blueDevice.disable();
            clearDisplay();
            Log.d("Printer", "close");
        } else {
            fab.startAnimation(rotate_forward);
            snd.startAnimation(fab_open_snd);
            get.startAnimation(fab_open_get);
            snd.setClickable(true);
            get.setClickable(true);
            Log.d("Printer", "open");
        }
        isFabOpen = !isFabOpen;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fab:
                animateFAB();
                break;
            case R.id.get:
                Log.d("Printer", "get");
                if (!blueDevice.isEnabled()) {
                    Intent discoverableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    startActivityForResult(discoverableBtIntent, 1);
                    isGetPress = true;
                }
                break;
            case R.id.snd:
                Log.d("Printer", "snd");
                if (!blueDevice.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, 1);
                    isSndPress = true;
                } else {
                    clearDisplay();
                    bondedPrint();
                    Log.d("Printer", "startCommand");
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }
}