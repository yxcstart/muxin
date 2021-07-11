package com.imooc.muxin.controller;

import com.imooc.muxin.enums.OperatorFriendRequestTypeEnum;
import com.imooc.muxin.enums.SearchFriendsStatusEnum;
import com.imooc.muxin.pojo.BO.UserBo;
import com.imooc.muxin.pojo.ChatMsg;
import com.imooc.muxin.pojo.Users;
import com.imooc.muxin.pojo.VO.MyFriendsVo;
import com.imooc.muxin.pojo.VO.UserVo;
import com.imooc.muxin.service.UserService;
import com.imooc.muxin.utils.FastDFSClient;
import com.imooc.muxin.utils.IMoocJSONResult;
import com.imooc.muxin.utils.MD5Utils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author yang
 * @create 2021-07-06 16:48
 */
@RestController
@RequestMapping("u")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private FastDFSClient fastDFSClient;

    @PostMapping("/registerOrLogin")
    public IMoocJSONResult registerOrLogin(@RequestBody Users user) throws Exception {
        //判空
        if (StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword())) {
            return IMoocJSONResult.errorMsg("username or password is null");
        }

        // username exist Login   not exit register
        boolean usernameIsExist = userService.queryUsernameIsExist(user.getUsername());
        Users userResult = null;
        if (usernameIsExist) {
            // Login
            userResult = userService.queryUserForLogin(user.getUsername(), MD5Utils.getMD5Str(user.getPassword()));
            if (userResult == null) {
                return IMoocJSONResult.errorMsg("username or password is incorrect");
            }
        } else {
            // register
            user.setNickname(user.getUsername());
            user.setFaceImage("");
            user.setFaceImageBig("");
            user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
            userResult = userService.saveUsers(user);
        }

        UserVo userVo = new UserVo();
        // 把两个相似的entity里面的属性做拷贝
        BeanUtils.copyProperties(userResult, userVo);
        return IMoocJSONResult.ok(userVo);
    }


    @PostMapping("/uploadFace")
    public IMoocJSONResult uploadFaceBase64(@RequestBody UserBo userBo) throws Exception {
        String url = userBo.getFaceData();
        System.out.println(url);

        // 获取缩略图的url
        String thump="_80x80.";
        int i = url.lastIndexOf(".");
        String thumpImgUrl=url.substring(0, i)+thump+url.substring(i+1);

        // 更新用户头像
        Users user = new Users();
        user.setId(userBo.getUserId());
        user.setFaceImageBig(url);
        user.setFaceImage(thumpImgUrl);

        Users result = userService.updateUserInfo(user);
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(result,userVo);
        return IMoocJSONResult.ok(userVo);
    }

    @PostMapping(value="/upload", headers="content-type=multipart/form-data")
    public String uploadFace(MultipartFile file,
                             HttpServletRequest request,
                             HttpServletResponse response) throws Exception {

        // 使用fastdfs上传文件
        String url = userService.upload(file);

        System.out.println(url);

        String fdfsServer = "http://112.74.47.33:88/";
        return fdfsServer + url;
    }

    @PostMapping("/setNickname")
    public IMoocJSONResult setNickname(@RequestBody UserBo userBo) throws Exception {
        // 更新昵称
        Users user = new Users();
        if (userBo.getNickname()==null||userBo.getNickname().length()>12){
            return IMoocJSONResult.errorMsg("nickname is null or longer than 12");
        }
        user.setId(userBo.getUserId());
        user.setNickname(userBo.getNickname());

        Users result = userService.updateUserInfo(user);
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(result,userVo);
        return IMoocJSONResult.ok(userVo);
    }

    @PostMapping("/getQrcode")
    public IMoocJSONResult getQrcode(@RequestBody UserBo userBo){
        String userId = userBo.getUserId();

        if(userId==null||userId.length()==0){
            return IMoocJSONResult.errorMsg("userid is null");
        }
        Users user = new Users();
        user.setId(userBo.getUserId());
        Users result = userService.getQrcodeUrl(user);
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(result,userVo);
        return IMoocJSONResult.ok(userVo);
    }

    @PostMapping("/search")
    public IMoocJSONResult searchUser(String myUserId,String friendUsername){
        // 0. 判断 myUserId friendUsername 不能为空
        if (StringUtils.isBlank(myUserId)||StringUtils.isBlank(friendUsername)){
            return IMoocJSONResult.errorMsg("");
        }
        // 前置条件 - 1. 搜索的用户如果不存在，返回[无此用户]
        // 前置条件 - 2. 搜索账号是你自己，返回[不能添加自己]
        // 前置条件 - 3. 搜索的朋友已经是你的好友，返回[该用户已经是你的好友]
        Integer status = userService.preconditionSearchFriends(myUserId, friendUsername);
        if (status == SearchFriendsStatusEnum.SUCCESS.status){
            Users user = userService.queryUserByUsername(friendUsername);
            UserVo userVo = new UserVo();
            BeanUtils.copyProperties(user, userVo);
            return IMoocJSONResult.ok(userVo);
        }else {
            String msg = SearchFriendsStatusEnum.getMsgByKey(status);
            return IMoocJSONResult.errorMsg(msg);
        }
    }

    @PostMapping("/addFriendRequest")
    public IMoocJSONResult addFriendRequest(String myUserId,String friendUsername){
        // 0. 判断 myUserId friendUsername 不能为空
        if (StringUtils.isBlank(myUserId)||StringUtils.isBlank(friendUsername)){
            return IMoocJSONResult.errorMsg("");
        }
        // 前置条件 - 1. 搜索的用户如果不存在，返回[无此用户]
        // 前置条件 - 2. 搜索账号是你自己，返回[不能添加自己]
        // 前置条件 - 3. 搜索的朋友已经是你的好友，返回[该用户已经是你的好友]
        Integer status = userService.preconditionSearchFriends(myUserId, friendUsername);
        if (status == SearchFriendsStatusEnum.SUCCESS.status){
            userService.sendFriendRequest(myUserId,friendUsername);
        }else {
            String msg = SearchFriendsStatusEnum.getMsgByKey(status);
            return IMoocJSONResult.errorMsg(msg);
        }
        return IMoocJSONResult.ok();
    }

    @PostMapping("/queryFriendRequest")
    public IMoocJSONResult queryFriendRequest(String acceptUserId){
        if (StringUtils.isBlank(acceptUserId)){
            return IMoocJSONResult.errorMsg("");
        }
        return IMoocJSONResult.ok(userService.queryFriendRequest(acceptUserId));
    }

    @PostMapping("/operaFriendRequest")
    public IMoocJSONResult operaFriendRequest(String acceptUserId,String sendUserId,Integer operaType){
        if (StringUtils.isBlank(acceptUserId)||StringUtils.isBlank(sendUserId)||operaType==null){
            return IMoocJSONResult.errorMsg("");
        }

        if (StringUtils.isBlank(OperatorFriendRequestTypeEnum.getMsgByType(operaType))){
            return IMoocJSONResult.errorMsg("");
        }

        if (operaType.equals(OperatorFriendRequestTypeEnum.IGNORE.type)){
            userService.deleteFriendRequest(acceptUserId, sendUserId);
        }else if (operaType.equals(OperatorFriendRequestTypeEnum.PASS.type)){
            userService.passFriendRequest(acceptUserId, sendUserId);
        }
        List<MyFriendsVo> myFriendsVo = userService.queryMyFriends(acceptUserId);
        return IMoocJSONResult.ok(myFriendsVo);
    }

    /**
     * 查询好友列表
     * @param userId
     * @return
     */
    @PostMapping("/myFriends")
    public IMoocJSONResult myFriends(String userId){
        if (StringUtils.isBlank(userId)){
            return IMoocJSONResult.errorMsg("");
        }
        List<MyFriendsVo> myFriendsVo = userService.queryMyFriends(userId);
        return IMoocJSONResult.ok(myFriendsVo);
    }

    @PostMapping("/getUnReadMsgList")
    public IMoocJSONResult getUnReadMsgList(String acceptUserId){
        if (StringUtils.isBlank(acceptUserId)){
            return IMoocJSONResult.errorMsg("");
        }
        List<ChatMsg> unReadMsgList = userService.getUnReadMsgList(acceptUserId);
        return IMoocJSONResult.ok(unReadMsgList);
    }

}
