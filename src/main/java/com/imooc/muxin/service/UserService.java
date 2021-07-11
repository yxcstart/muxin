package com.imooc.muxin.service;

import com.imooc.muxin.netty.ChatMsg;
import com.imooc.muxin.pojo.Users;
import com.imooc.muxin.pojo.VO.FriendRequestVo;
import com.imooc.muxin.pojo.VO.MyFriendsVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author yang
 * @create 2021-07-06 16:53
 */

public interface UserService {
    /**
     * 判断用户名是否存在
     * @param username
     * @return
     */
    boolean queryUsernameIsExist(String username);

    /**
     * 查询用户是否存在
     * @param username
     * @param pwd
     * @return
     */
    Users queryUserForLogin(String username,String pwd);

    /**
     * 用户注册
     * @param user
     * @return
     */
    Users saveUsers(Users user);

    /**
     * 更新用户记录
     * @param user
     * @return
     */
    Users updateUserInfo(Users user);

    /**
     * 上传图片
     * @param file
     * @return
     * @throws Exception
     */
    String upload(MultipartFile file) throws Exception;

    /**
     * 获取用户二维码
     * @param user
     * @return
     */
    Users getQrcodeUrl(Users user);

    Integer preconditionSearchFriends(String myUserId,String friendUsername);

    /**
     * 根据用户名查询用户
     * @param username
     * @return
     */
    Users queryUserByUsername(String username);

    /**
     * 发送添加好友请求
     * @param myUserId
     * @param friendUsername
     */
    void sendFriendRequest(String myUserId, String friendUsername);

    /**
     * 查找好友添加请求
     * @param acceptUserId
     * @return
     */
    List<FriendRequestVo> queryFriendRequest(String acceptUserId);

    /**
     * 删除好友请求
     * @param acceptUserId
     * @param sendUserId
     */
    void deleteFriendRequest(String acceptUserId,String sendUserId);

    /**
     * 通过好友请求
     * 1.保存好友
     * 2.逆向保存好友
     * 3.删除请求记录
     * @param acceptUserId
     * @param sendUserId
     */
    void passFriendRequest(String acceptUserId,String sendUserId);

    /**
     * 查询好友列表
     * @param userId
     */
    List<MyFriendsVo> queryMyFriends(String userId);

    /**
     * 保存消息到数据库并返回消息的主键id
     * @param
     * @return
     */
    String saveMsg(ChatMsg chatMsg);

    /**
     * 批量签收
     * @param msgIdList
     */
    void updateMsgSigned(List<String> msgIdList);

    /**
     * 获取未签收消息
     * @param acceptUserId
     * @return
     */
    List<com.imooc.muxin.pojo.ChatMsg> getUnReadMsgList(String acceptUserId);


}
