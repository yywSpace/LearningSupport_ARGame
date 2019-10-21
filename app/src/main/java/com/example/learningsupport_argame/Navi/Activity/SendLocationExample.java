package com.example.learningsupport_argame.Navi.Activity;

import android.content.Context;
import android.util.Log;

import com.example.learningsupport_argame.client.ClientLab;
import com.example.learningsupport_argame.client.UDPClient;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.net.UnknownHostException;

public class SendLocationExample {
    private final String TAG = "SendLocation";
    private int port = 3000;
    private String ip = "192.168.0.112";//"47.96.152.133";
    private UDPClient mUDPClient;
    public Thread sendLocationThread;

    public SendLocationExample(String wayName, Context context) throws IOException {

        new Thread(() -> {
            try {
                mUDPClient = new UDPClient(ClientLab.sPort, ClientLab.sIp, wayName);
                mUDPClient.Login();
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }


        }).start();

        sendLocationThread = new Thread(() -> {
            try {
                while (!Thread.interrupted()) {
                    if (mUDPClient == null) {
                        continue;
                    }
                    InputStream is = context.getAssets().open(wayName);
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String line;
                    while ((line = br.readLine()) != null) {
                        Thread.sleep(500);
                        String[] latlong = line.split(",");
                        mUDPClient.Location(Float.parseFloat(latlong[0]), Float.parseFloat(latlong[1]));
                    }
                }
            } catch (InterruptedException e) {
                Log.e(TAG, "SendLocation: ", e);
                e.printStackTrace();
            } catch (IOException e) {
                Log.e(TAG, "SendLocation: ", e);
                e.printStackTrace();
            } finally {
                mUDPClient.Logout();

            }
        });
        sendLocationThread.start();
    }
}
