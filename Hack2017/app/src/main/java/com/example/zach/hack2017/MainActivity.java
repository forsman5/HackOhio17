package com.example.zach.hack2017;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.*;
import java.io.*;

public class MainActivity extends Activity {

    Button btnOn, btnOff;
    TextView messageBox, phoneNumberBox;
    Handler bluetoothIn;
    String totalMessage="";
    String sentString="";
    String lastString = "";
    int cycle=0;
    int trials = 0;
    final int handlerState = 0;                        //used to identify handler message
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder recDataString = new StringBuilder();
    private StringBuilder phoneDataString = new StringBuilder();
    private boolean editingMessage = true;
    public ConnectedThread mConnectedThread;

    // SPP UUID service - this should work for most devices
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // String for MAC address
    private static String address;
    private static final int PERMISSION_REQUEST_CODE = 1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.SEND_SMS)
                    == PackageManager.PERMISSION_DENIED) {


                String[] permissions = {Manifest.permission.SEND_SMS};

                requestPermissions(permissions, PERMISSION_REQUEST_CODE);

            }
        }
        //Link the buttons and textViews to respective views
        messageBox = (TextView) findViewById(R.id.MessageBox);
        phoneNumberBox = (TextView) findViewById(R.id.PhoneNumberBox);
        final Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view)
            {
                sentString = "";
                int numOfChars = (int)(Math.random()*100 + 1);
                String[] chars = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z","0","1","2","8","9","10"};
                for(int i = 0; i<numOfChars;i++)
                {
                    sentString += chars[(int)(Math.random()*chars.length)];
                }
                mConnectedThread.write(sentString+".");
            }
        });
        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {                                     //if message is what we want
                    String readMessage = (String) msg.obj;                                                                // msg.arg1 = bytes from connect thread
                    totalMessage += readMessage;
                    if(totalMessage.indexOf('|')!=-1)
                    {
                        try{
                            totalMessage+=" ";
                            String[] parts = totalMessage.split("\\|");
                            for(int i = 0;i<parts.length-1;i++) {

                                String url = "https://api.wolframalpha.com/v2/query?input="+URLEncoder.encode(parts[i])+"&format=plaintext&output=JSON&appid=78LRJ3-46VG7L6YQ3";
                                new RetrieveFromURL().execute(url);

                            }


                            long time= System.currentTimeMillis();
                            android.util.Log.i("Time Class ", " Time value in millisecinds "+time);
                            totalMessage=parts[parts.length-1];
                            totalMessage = totalMessage.substring(0,totalMessage.length()-1);
                        } catch(Exception exce)
                        {
                            System.out.print("fffff");
                        }
                        //lastString = sentString;
                        //sentString = "";
                        //int numOfChars = (int)(Math.random()*80 + 1);
                        //String[] chars = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z","0","1","2","8","9","10"};
                        //for(int i = 0; i<numOfChars;i++)
                        //{
                        //    sentString += chars[(int)(Math.random()*chars.length)];
                        //}
                        //totalMessage="";
                        //trials++;
                        //mConnectedThread.write(sentString+".");

                    }
                }
            }
        };

        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        checkBTState();

    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connecetion with BT device using UUID
    }

    @Override
    public void onResume() {
        super.onResume();

        if(btSocket==null||!btSocket.isConnected()) {

            //Get MAC address from DeviceListActivity via intent
            Intent intent = getIntent();

            //Get the MAC address from the DeviceListActivty via EXTRA
            address = intent.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);

            //create device and set the MAC address
            BluetoothDevice device = btAdapter.getRemoteDevice(address);

            try {
                btSocket = createBluetoothSocket(device);
            } catch (IOException e) {
                Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_LONG).show();
            }
            // Establish the Bluetooth socket connection.
            try {
                btSocket.connect();
            } catch (IOException e) {
                try {
                    btSocket.close();
                } catch (IOException e2) {
                    //insert code to deal with this
                }
            }
            mConnectedThread = new ConnectedThread(btSocket);
            mConnectedThread.start();

            //I send a character when resuming.beginning transmission to check device is connected
            //If it is not an exception will be thrown in the write method and finish() will be called
            mConnectedThread.write(" ");
            editingMessage = false;
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        /*try
        {
            //Don't leave Bluetooth sockets open when leaving activity
            btSocket.close();
        } catch (IOException e2) {
            //insert code to deal with this
        }*/
    }

    //Checks that the Android device Bluetooth is available and prompts to be turned on if off
    private void checkBTState() {

        if(btAdapter==null) {
            Toast.makeText(getBaseContext(), "Device does not support bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }



    //create new class for connect thread
    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            // Keep looping to listen for received messages
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);            //read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);
                    // Send the obtained bytes to the UI Activity via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
        //write method
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e) {
                //if you cannot write, close the application
                Toast.makeText(getBaseContext(), "Connection Failure", Toast.LENGTH_LONG).show();
                finish();

            }
        }
    }
    class RetrieveFromURL extends AsyncTask<String, Void, String> {

        private Exception exception;

        protected String doInBackground(String... urls) {
            try {
                String xml = "";
                URL oracle = new URL(urls[0]);
                URLConnection yc = oracle.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        yc.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null)
                    xml+=inputLine+"\n";
                in.close();

                final JSONObject obj = new JSONObject(xml);
                final JSONObject geodata = obj.getJSONObject("queryresult");
                JSONArray pods = geodata.getJSONArray("pods");
                final int n = pods.length();
                for (int index = 0; index < n; ++index) {
                    final JSONObject pod = pods.getJSONObject(index);
                    String title = pod.getString("title");
                    if(!title.equals("Input")&&!title.equals("Plot")&&!title.equals("Geometric figure")&&!title.equals("Properties as a real function")&&!title.equals("Alternate forms")&&!title.equals("Alternate form")&&!title.equals("Input interpretation"))
                    {
                        String ans = pod.getJSONArray("subpods").getJSONObject(0).getString("plaintext");
                        mConnectedThread.write(ans+"         |");
                        break;

                    }
                }
                return xml;
            } catch (Exception e) {
                this.exception = e;

                return null;
            }
        }

        protected void onPostExecute(String feed) {
            // TODO: check this.exception
            // TODO: do something with the feed
        }
    }
}

//package com.example.zach.hack2017;
//
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothDevice;
//import android.bluetooth.BluetoothServerSocket;
//import android.bluetooth.BluetoothSocket;
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.design.widget.BottomNavigationView;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
//import android.os.Handler;
//
//import org.w3c.dom.Text;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.util.Set;
//import java.util.UUID;
//
//import static android.provider.ContactsContract.Intents.Insert.NAME;
//
//public class MainActivity extends AppCompatActivity {
//
//    BluetoothSocket mmSocket;
//    BluetoothDevice mmDevice = null;
//    Handler handler;
//    TextView myLabel;
//    BluetoothAdapter mBluetoothAdapter;
//    private final static int REQUEST_ENABLE_BT = 1;
//
//
//    final byte delimiter = 33;
//    int readBufferPosition = 0;
//
//    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
//            = new BottomNavigationView.OnNavigationItemSelectedListener() {
//
//        @Override
//        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//            switch (item.getItemId()) {
//                case R.id.navigation_home:
//                  //  mTextMessage.setText(R.string.title_home);
//                    return true;
//                case R.id.navigation_dashboard:
//                   // mTextMessage.setText(R.string.title_dashboard);
//                    return true;
//                case R.id.navigation_notifications:
//                  //  mTextMessage.setText(R.string.title_notifications);
//                    return true;
//            }
//            return false;
//        }
//
//    };
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
//        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
//        Button takePhoto = (Button) findViewById(R.id.takePhotoButton);
//        Button takeVideo = (Button) findViewById(R.id.takeVideoButton);
//        TextView status = (TextView) findViewById(R.id.status);
//        myLabel = (TextView) findViewById(R.id.text);
//        //create adapter
//        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        if (!mBluetoothAdapter.isEnabled()) {
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//        }
//        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
//
//        if (pairedDevices.size() > 0) {
//            // There are paired devices. Get the name and address of each paired device.
//            for (BluetoothDevice device : pairedDevices) {
//                String deviceName = device.getName();
//                String deviceHardwareAddress = device.getAddress(); // MAC address
//            }
//        }
//
//
//
//        handler = new Handler();
//
//        takePhoto.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                (new Thread(new BlueToothService(("takePhoto"))).start();
//
//            }
//        });
//
//
//    }
//    public void sendBtMsg(String msg2send){
//        //UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard SerialPortService ID
//        UUID uuid = UUID.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ee"); //Standard SerialPortService ID
//        try {
//
//            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
//            if (!mmSocket.isConnected()){
//                mmSocket.connect();
//            }
//
//            String msg = msg2send;
//            //msg += "\n";
//            OutputStream mmOutputStream = mmSocket.getOutputStream();
//            mmOutputStream.write(msg.getBytes());
//
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//    }
//
//
//
//
//
//
//    final class BlueToothService implements Runnable {
//
//        private String btMsg;
//
//        public BlueToothService(String msg) {
//            btMsg = msg;
//        }
//
//        public void sendBtMsg(String msg2send){
//            //UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard SerialPortService ID
//            UUID uuid = UUID.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ee"); //Standard SerialPortService ID
//            try {
//
//                mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
//                if (!mmSocket.isConnected()){
//                    mmSocket.connect();
//                }
//
//                String msg = msg2send;
//                //msg += "\n";
//                OutputStream mmOutputStream = mmSocket.getOutputStream();
//                mmOutputStream.write(msg.getBytes());
//
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//
//        }
//
//        public void run()
//        {
//            sendBtMsg(btMsg);
//            while(!Thread.currentThread().isInterrupted())
//            {
//                int bytesAvailable;
//                boolean workDone = false;
//
//                try {
//                    final InputStream mmInputStream;
//                    mmInputStream = mmSocket.getInputStream();
//                    bytesAvailable = mmInputStream.available();
//                    if(bytesAvailable > 0)
//                    {
//
//                        byte[] packetBytes = new byte[bytesAvailable];
//                        byte[] readBuffer = new byte[1024];
//                        mmInputStream.read(packetBytes);
//
//                        for(int i=0;i<bytesAvailable;i++)
//                        {
//                            byte b = packetBytes[i];
//                            if(b == delimiter)
//                            {
//                                byte[] encodedBytes = new byte[readBufferPosition];
//                                System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
//                                final String data = new String(encodedBytes, "US-ASCII");
//                                readBufferPosition = 0;
//
//                                //The variable data now contains our full command
//                                handler.post(new Runnable()
//                                {
//                                    public void run()
//                                    {
//                                        myLabel.setText(data);
//                                    }
//                                });
//
//                                workDone = true;
//                                break;
//
//
//                            }
//                            else
//                            {
//                                readBuffer[readBufferPosition++] = b;
//                            }
//                        }
//
//                        if (workDone == true){
//                            mmSocket.close();
//                            break;
//                        }
//
//                    }
//                } catch (IOException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//
//            }
//        }
//    };
//
//
//
//
//
//    private class AcceptThread extends Thread {
//        private final BluetoothServerSocket mmServerSocket;
//
//        public AcceptThread() {
//            // Use a temporary object that is later assigned to mmServerSocket
//            // because mmServerSocket is final.
//            BluetoothServerSocket tmp = null;
//            try {
//                // MY_UUID is the app's UUID string, also used by the client code.
//                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
//            } catch (IOException e) {
//                Log.e(TAG, "Socket's listen() method failed", e);
//            }
//            mmServerSocket = tmp;
//        }
//
//        public void run() {
//            BluetoothSocket socket = null;
//            // Keep listening until exception occurs or a socket is returned.
//            while (true) {
//                try {
//                    socket = mmServerSocket.accept();
//                } catch (IOException e) {
//                    Log.e(TAG, "Socket's accept() method failed", e);
//                    break;
//                }
//
//                if (socket != null) {
//                    // A connection was accepted. Perform work associated with
//                    // the connection in a separate thread.
//                    manageMyConnectedSocket(socket);
//                    try {
//                        mmServerSocket.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    break;
//                }
//            }
//        }
//
//        // Closes the connect socket and causes the thread to finish.
//        public void cancel() {
//            try {
//                mmServerSocket.close();
//            } catch (IOException e) {
//                Log.e(TAG, "Could not close the connect socket", e);
//            }
//        }
//    }
//
//
//
//}
