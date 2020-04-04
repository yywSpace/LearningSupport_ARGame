package com.example.learningsupport_argame.Client;


import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketImplFactory;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UDPClient {
    private String TAG = "UDPClient";
    private int port;
    private String ip;
    private Gson gson;
    private DatagramSocket socket;
    private String userName;
    private OnReceiveUserList mOnReceiveUserList;
    private OnReceiveMessageList mOnReceiveMessageList;
    private OnReceiveUserChat mOnReceiveUserChat;

    private String mOnlineUserList = "";
    private Map<String, String> mMessageMap = new HashMap<>();

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

    public void MessageList(String otherUser) {
        String msg = gson.toJson(new MessageData(userName + "," + otherUser, MessageType.MessageList));
        sendMessage(msg);
    }

    public void ChatToUser(String otherName, String content) {
        String msg = gson.toJson(new MessageData(userName + "," + otherName + "," + content, MessageType.ChatToUser));
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
                    String message = new String(data, 0, packet.getLength());
                    Log.d(TAG, "receive: " + message);
                    MessageData messageData = new Gson().fromJson(message, MessageData.class);
                    if (messageData == null)
                        continue;
                    Log.d(TAG, "receive: " + message);

                    switch (messageData.MessageType) {
                        case 6://MessageList
                            String[] args = messageData.Message.split(";");
                            String userPair = args[0];
                            String messageList = "";
                            if (args.length == 2)
                                messageList = args[1];
                            Log.d(TAG, "receive: " + userPair);
                            if (mOnReceiveMessageList != null) {
                                mOnReceiveMessageList.onReceiveMessageList(messageList);
                            }
                            break;
                        case 4://UserList
                            String userListStr = messageData.Message;
                            mOnlineUserList = userListStr;
                            if (mOnReceiveUserList != null)
                                mOnReceiveUserList.onReceiveUserList(userListStr);
                            break;
                        case 1://ChatToUser
                            String msg = messageData.Message;
                            String[] userArgs = msg.split(",");
                            String senderName = userArgs[0];
                            String content;
                            if (userArgs.length < 3)
                                content = "";
                            else
                                content = userArgs[2];
                            mMessageMap.put(senderName, content);
                            if (mOnReceiveUserChat != null) {
                                mOnReceiveUserChat.onReceiveUserChat(senderName, content);
                            }
                            break;
                    }


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

    public List<String> onlineUser() {
        List<String> userList = new ArrayList<>();
        String[] users = mOnlineUserList.split(";");
        for (int i = 0; i < users.length; i++) {
            String[] userArgs = users[i].split(",");
            String userName = userArgs[0];
            userList.add(userName);
        }
        return userList;
    }

    public Map<String, String> getMessageMap() {
        return mMessageMap;
    }

    public void setOnReceiveUserList(OnReceiveUserList onReceiveUserList) {
        mOnReceiveUserList = onReceiveUserList;
    }

    public void setOnReceiveMessageList(OnReceiveMessageList onReceiveMessageList) {
        mOnReceiveMessageList = onReceiveMessageList;
    }

    public void setOnReceiveUserChat(OnReceiveUserChat onReceiveUserChat) {
        mOnReceiveUserChat = onReceiveUserChat;
    }
}