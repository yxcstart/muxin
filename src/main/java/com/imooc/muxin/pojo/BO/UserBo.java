package com.imooc.muxin.pojo.BO;

/**
 * @author yang
 * @create 2021-07-06 19:11
 */
public class UserBo {
    private String userId;

    private String faceData;

    private String nickname;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFaceData() {
        return faceData;
    }

    public void setFaceData(String faceData) {
        this.faceData = faceData;
    }
}
