package com.example.learningsupport_argame.Client;

/// <summary>
/// 简单的协议类型
/// </summary>
public class MessageType {
    /// <summary>
    /// 聊天室中聊天
    /// </summary>
    public static int ChatInRoom = 0;
    /// <summary>
    /// 两人聊天 userName,otherUserName,message
    /// </summary>
    public static int ChatToUser = 1;
    /// <summary>
    /// 登陆 userName,ipEndPoint
    /// </summary>
    public static int Login = 2;
    /// <summary>
    /// 登出 name
    /// </summary>
    public static int LogOut = 3;
    /// <summary>
    /// 在线人员列表 name,ipEndPoint,x,y;name1,ipEndPoint1,x1,y1;...
    /// </summary>
    public static int UserList = 4;
    /// <summary>
    /// 每个用户的位置
    /// </summary>
    public static int Location = 5;
    /// <summary>
    /// 两人之间的消息列表 userName,otherUserName,messageList
    /// messageList格式 userX:message,userX:message,....
    /// </summary>
    public static int MessageList = 6;
}
