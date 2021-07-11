package com.imooc.muxin.netty;

import java.io.Serializable;

/**
 * @author yang
 * @create 2021-07-11 9:17
 */
public class ChatMsg implements Serializable {
    private static final long serialVersionUID = 3611169682695799175L;

    private String senderId;
    private String receiverId;
    private String msg;
    private String msgId;

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }
}
