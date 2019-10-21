package com.example.learningsupport_argame.client;

/// <summary>
/// 简单的协议类型
/// </summary>
public enum MessageType {
    /// <summary>
    /// 聊天室中聊天
    /// </summary>
    ChatInRoom,
    /// <summary>
    /// 两人聊天 userName,otherUserName,message
    /// </summary>
    ChatToUser,
    /// <summary>
    /// 登陆 userName,ipEndPoint
    /// </summary>
    Login,
    /// <summary>
    /// 登出 name
    /// </summary>
    LogOut,
    /// <summary>
    /// 在线人员列表 name,ipEndPoint,x,y;name1,ipEndPoint1,x1,y1;...
    /// </summary>
    UserList,
    /// <summary>
    /// 每个用户的位置
    /// </summary>
    Location,
}
