package com.example.learningsupport_argame.client;

public class MessageData {

    public MessageData(String message ,MessageType messageType) {
        MessageType = messageType;
        Message = message;
    }
    /// <summary>
    /// 消息类型
    /// </summary>
    public MessageType MessageType;
    /// <summary>
    /// 消息内容
    /// </summary>
    public String Message;
}