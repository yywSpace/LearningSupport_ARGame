package com.example.learningsupport_argame.client;

import java.net.SocketException;
import java.net.UnknownHostException;

public class ClientLab {
    public static int sPort = 3000;
    public static String sUserName = "android";
    public static String sIp = "47.96.152.133";//192.168.1.107

    private static UDPClient sUDPClient;

    public static UDPClient getInstance(int port, String ip, String userName) {
        if (sUDPClient == null) {
            try {
                sUDPClient = new UDPClient(port,ip,userName);
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        return sUDPClient;
    }
}
