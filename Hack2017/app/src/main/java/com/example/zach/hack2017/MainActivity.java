package com.example.zach.hack2017;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.os.Handler;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice = null;
    Handler handler;
    TextView myLabel;
    private final static int REQUEST_ENABLE_BT = 1;


    final byte delimiter = 33;
    int readBufferPosition = 0;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                  //  mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                   // mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                  //  mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        Button takePhoto = (Button) findViewById(R.id.takePhotoButton);
        Button takeVideo = (Button) findViewById(R.id.takeVideoButton);
        TextView status = (TextView) findViewById(R.id.status);
        myLabel = (TextView) findViewById(R.id.text);
        //create adapter
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
            }
        }
        handler = new Handler();

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                (new Thread(new BlueToothService("takePhoto"))).start();

            }
        });


    }
    public void sendBtMsg(String msg2send){
        //UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard SerialPortService ID
        UUID uuid = UUID.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ee"); //Standard SerialPortService ID
        try {

            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            if (!mmSocket.isConnected()){
                mmSocket.connect();
            }

            String msg = msg2send;
            //msg += "\n";
            OutputStream mmOutputStream = mmSocket.getOutputStream();
            mmOutputStream.write(msg.getBytes());

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    final class BlueToothService implements Runnable {

        private String btMsg;

        public BlueToothService(String msg) {
            btMsg = msg;
        }

        public void sendBtMsg(String msg2send){
            //UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard SerialPortService ID
            UUID uuid = UUID.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ee"); //Standard SerialPortService ID
            try {

                mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
                if (!mmSocket.isConnected()){
                    mmSocket.connect();
                }

                String msg = msg2send;
                //msg += "\n";
                OutputStream mmOutputStream = mmSocket.getOutputStream();
                mmOutputStream.write(msg.getBytes());

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        public void run()
        {
            sendBtMsg(btMsg);
            while(!Thread.currentThread().isInterrupted())
            {
                int bytesAvailable;
                boolean workDone = false;

                try {
                    final InputStream mmInputStream;
                    mmInputStream = mmSocket.getInputStream();
                    bytesAvailable = mmInputStream.available();
                    if(bytesAvailable > 0)
                    {

                        byte[] packetBytes = new byte[bytesAvailable];
                        Log.e("Aquarium recv bt","bytes available");
                        byte[] readBuffer = new byte[1024];
                        mmInputStream.read(packetBytes);

                        for(int i=0;i<bytesAvailable;i++)
                        {
                            byte b = packetBytes[i];
                            if(b == delimiter)
                            {
                                byte[] encodedBytes = new byte[readBufferPosition];
                                System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                final String data = new String(encodedBytes, "US-ASCII");
                                readBufferPosition = 0;

                                //The variable data now contains our full command
                                handler.post(new Runnable()
                                {
                                    public void run()
                                    {
                                        myLabel.setText(data);
                                    }
                                });

                                workDone = true;
                                break;


                            }
                            else
                            {
                                readBuffer[readBufferPosition++] = b;
                            }
                        }

                        if (workDone == true){
                            mmSocket.close();
                            break;
                        }

                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }
    };



}
