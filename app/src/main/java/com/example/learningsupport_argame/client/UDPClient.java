package com.example.learningsupport_argame.client;


import com.google.gson.Gson;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UDPClient {


    private int port;
    private String ip;
    private Gson gson;
    private DatagramSocket socket;
    private String userName;
    private OnReceiveUserList mOnReceiveUserList;

    public UDPClient(int port, String ip, String userName) throws SocketException, UnknownHostException {
        this.port = port;
        this.ip = ip;
        gson = new Gson();
        socket = new DatagramSocket();
        this.userName = userName;
        receive();
    }



    public void Login() {
        MessageData data = new MessageData(userName, MessageType.Login);
        String msg = gson.toJson(data);
        sendMessage(msg);
    }

    public void Logout() {
        MessageData data = new MessageData(userName, MessageType.LogOut);
        String msg = gson.toJson(data);
        sendMessage(msg);
    }

    public void Location(float x, float y) {
        MessageData data = new MessageData(userName + "," + x + "," + y, MessageType.Location);
        String msg = gson.toJson(data);
        sendMessage(msg);
    }

    public void UserList() {
        String msg = gson.toJson(new MessageData(userName, MessageType.UserList));
        sendMessage(msg);
    }

    public void sendMessage(String message) {
        DatagramPacket packet = createPacket(ip, port, message);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receive() {
        new Thread(() -> {
            try {
                while (true) {
                    byte[] data = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(data, data.length);
                    socket.receive(packet);
                    String userListStr = new String(data, 0, packet.getLength());
                    if (mOnReceiveUserList != null)
                        mOnReceiveUserList.onReceiveUserList(userListStr);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }


    public DatagramPacket createPacket(String ip, int port, String message) {
        try {
            InetAddress address = InetAddress.getByName(ip);
            byte[] data = message.getBytes();
            //创建数据报，包含发送的数据信息
            DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
            return packet;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setOnReceiveUserList(OnReceiveUserList onReceiveUserList) {
        mOnReceiveUserList = onReceiveUserList;
    }
}
