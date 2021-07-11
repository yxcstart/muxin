package com.imooc.muxin.pojo.VO;

/**
 * @author yang
 * @create 2021-07-06 19:11
 */
public class MyFriendsVo {
    private String friendUserId;

    private String friendUserName;

    private String friendFaceImage;

    private String friendNickname;

    public String getFriendUserId() {
        return friendUserId;
    }

    public void setFriendUserId(String friendUserId) {
        this.friendUserId = friendUserId;
    }

    public String getFriendUserName() {
        return friendUserName;
    }

    public void setFriendUserName(String friendUserName) {
        this.friendUserName = friendUserName;
    }

    public String getFriendFaceImage() {
        return friendFaceImage;
    }

    public void setFriendFaceImage(String friendFaceImage) {
        this.friendFaceImage = friendFaceImage;
    }

    public String getFriendNickname() {
        return friendNickname;
    }

    public void setFriendNickname(String friendNickname) {
        this.friendNickname = friendNickname;
    }

    @Override
    public String toString() {
        return "MyFriendsVo{" +
                "friendUserId='" + friendUserId + '\'' +
                ", friendUserName='" + friendUserName + '\'' +
                ", friendFaceImage='" + friendFaceImage + '\'' +
                ", friendNickname='" + friendNickname + '\'' +
                '}';
    }
}
