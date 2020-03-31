package com.example.learningsupport_argame.Client;

public class MessageData {

    public MessageData(String message ,int messageType) {
        MessageType = messageType;
        Message = message;
    }
    /// <summary>
    /// 消息类型
    /// </summary>
    public int MessageType;
    /// <summary>
    /// 消息内容
    /// </summary>
    public String Message;
}