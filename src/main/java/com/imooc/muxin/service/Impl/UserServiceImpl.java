package com.imooc.muxin.service.Impl;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.imooc.muxin.enums.MsgActionEnum;
import com.imooc.muxin.enums.MsgSignFlagEnum;
import com.imooc.muxin.enums.SearchFriendsStatusEnum;
import com.imooc.muxin.mapper.*;
import com.imooc.muxin.netty.ChatMsg;
import com.imooc.muxin.netty.DataContent;
import com.imooc.muxin.netty.UserChannelRel;
import com.imooc.muxin.pojo.FriendsRequest;
import com.imooc.muxin.pojo.MyFriends;
import com.imooc.muxin.pojo.Users;
import com.imooc.muxin.pojo.VO.FriendRequestVo;
import com.imooc.muxin.pojo.VO.MyFriendsVo;
import com.imooc.muxin.service.UserService;
import com.imooc.muxin.idworker.Sid;
import com.imooc.muxin.utils.FastDFSClient;
import com.imooc.muxin.utils.FileUtils;
import com.imooc.muxin.utils.JsonUtils;
import com.imooc.muxin.utils.QRCodeUtils;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @author yang
 * @create 2021-07-06 16:54
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private Sid sid;

    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    @Autowired
    private FastDFSClient fastDFSClient;

    @Autowired
    private QRCodeUtils qrCodeUtils;

    @Autowired
    private MyFriendsMapper myFriendsMapper;

    @Autowired
    private FriendsRequestMapper friendsRequestMapper;

    @Autowired
    private UsersMapperCustom usersMapperCustom;

    @Autowired
    private ChatMsgMapper chatMsgMapper;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean queryUsernameIsExist(String username) {
        // ??????username?????????????????????
        Users user = new Users();
        user.setUsername(username);

        // usersmapper???selectone??????????????????????????????????????????????????????????????????????????????
        // ??????res????????????????????????null??????????????????????????????
        Users result = usersMapper.selectOne(user);

        return result != null;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserForLogin(String username, String pwd) {
        // ??????????????????
        Example userExample = new Example(Users.class);
        Criteria criteria = userExample.createCriteria();

        criteria.andEqualTo("username", username);
        criteria.andEqualTo("password", pwd);

        Users result = usersMapper.selectOneByExample(userExample);

        return result;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Users saveUsers(Users user) {
        String userId = sid.nextShort();
        //?????????????????????????????????????????????

        String qrCodePath = "E://user" + userId + "qrcode.png";

        qrCodeUtils.createQRCode(qrCodePath, "muxin_qrcode:" + user.getUsername());
        MultipartFile qrcodeFile = FileUtils.fileToMultipart(qrCodePath);

        //??????
        String qrCodeUrl = "";
        try {
            qrCodeUrl = fastDFSClient.uploadQRCode(qrcodeFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        user.setQrcode(qrCodeUrl);
        user.setFaceImageBig("http://112.74.47.33:88/imooc/M00/00/00/rBz5XGDm4KyAUIb-AA7efqtOUg0727.PNG");
        user.setFaceImage("http://112.74.47.33:88/imooc/M00/00/00/rBz5XGDm4KyAUIb-AA7efqtOUg0727_80x80.PNG");
        user.setId(userId);
        usersMapper.insert(user);

        return user;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Users updateUserInfo(Users user) {
        usersMapper.updateByPrimaryKeySelective(user);
        return queryUserById(user.getId());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public String upload(MultipartFile file) throws Exception {
        if (file != null) {
            String fileName = file.getOriginalFilename();
            // ????????????????????????
            if (StringUtils.isNotBlank(fileName)) {
                // ???????????????
                String fileNameArr[] = fileName.split("\\.");
                String suffix = fileNameArr[fileNameArr.length - 1];

                if (!suffix.equalsIgnoreCase("png") &&
                        !suffix.equalsIgnoreCase("jpg") &&
                        !suffix.equalsIgnoreCase("jpeg")) {
                    return null;
                }

                StorePath storePath = fastFileStorageClient.uploadImageAndCrtThumbImage(file.getInputStream(),
                        file.getSize(),
                        suffix,
                        null);

                String path = storePath.getFullPath();

                return path;
            }
        }

        return null;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users getQrcodeUrl(Users user) {
        Users result = usersMapper.selectOne(user);
        if (result.getQrcode() != null) return result;
        return null;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Integer preconditionSearchFriends(String myUserId, String friendUsername) {
        Users user = queryUserByUsername(friendUsername);
        // 1. ???????????????????????????????????????[????????????]
        if (user == null) {
            return SearchFriendsStatusEnum.USER_NOT_EXIST.status;
        }
        // 2. ?????????????????????????????????[??????????????????]
        if (myUserId.equals(user.getId())) {
            return SearchFriendsStatusEnum.NOT_YOURSELF.status;
        }
        // 3. ?????????????????????????????????????????????[??????????????????????????????]
        Example example = new Example(MyFriends.class);
        Criteria criteria = example.createCriteria();
        criteria.andEqualTo("myUserId", myUserId);
        criteria.andEqualTo("myFriendUserId", user.getId());
        MyFriends myFriendsRel = myFriendsMapper.selectOneByExample(example);
        if (myFriendsRel != null) {
            return SearchFriendsStatusEnum.ALREADY_FRIENDS.status;
        }

        return SearchFriendsStatusEnum.SUCCESS.status;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserByUsername(String username) {
        Example ue = new Example(Users.class);
        Criteria criteria = ue.createCriteria();
        criteria.andEqualTo("username", username);
        return usersMapper.selectOneByExample(ue);

    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void sendFriendRequest(String myUserId, String friendUsername) {
        Users friend = queryUserByUsername(friendUsername);

        Example example = new Example(FriendsRequest.class);
        Criteria criteria = example.createCriteria();
        criteria.andEqualTo("sendUserId", myUserId);
        criteria.andEqualTo("acceptUserId", friend.getId());
        FriendsRequest friendsRequest = friendsRequestMapper.selectOneByExample(example);
        if (friendsRequest == null) {
            String requestId = sid.nextShort();
            FriendsRequest request = new FriendsRequest();
            request.setId(requestId);
            request.setSendUserId(myUserId);
            request.setAcceptUserId(friend.getId());
            request.setRequestDateTime(new Date());
            friendsRequestMapper.insert(request);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<FriendRequestVo> queryFriendRequest(String acceptUserId) {
        return usersMapperCustom.queryFriendRequest(acceptUserId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void deleteFriendRequest(String acceptUserId, String sendUserId) {
        Example example = new Example(FriendsRequest.class);
        Criteria criteria = example.createCriteria();
        criteria.andEqualTo("acceptUserId", acceptUserId);
        criteria.andEqualTo("sendUserId", sendUserId);
        friendsRequestMapper.deleteByExample(example);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void passFriendRequest(String acceptUserId, String sendUserId) {
        Example example = new Example(MyFriends.class);
        Criteria criteria = example.createCriteria();
        criteria.andEqualTo("myUserId", acceptUserId);
        criteria.andEqualTo("myFriendUserId", sendUserId);
        MyFriends myFriends = myFriendsMapper.selectOneByExample(example);
        if(myFriends==null){
            saveFriends(acceptUserId, sendUserId);
            saveFriends(sendUserId, acceptUserId);
        }
        deleteFriendRequest(acceptUserId, sendUserId);

        // ??????websocket???????????????????????????????????????????????????????????????????????????
        Channel channel = UserChannelRel.get(sendUserId);
        if (channel!=null){
            DataContent dataContent = new DataContent();
            dataContent.setAction(MsgActionEnum.PULL_FRIEND.type);

            channel.writeAndFlush(new TextWebSocketFrame(JsonUtils.objectToJson(dataContent)));
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<MyFriendsVo> queryMyFriends(String userId) {
        List<MyFriendsVo> myFriendsVos = usersMapperCustom.queryMyFriends(userId);
        return myFriendsVos;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public String saveMsg(ChatMsg chatMsg) {
        com.imooc.muxin.pojo.ChatMsg msgDB = new com.imooc.muxin.pojo.ChatMsg();

        String MsgSid = sid.nextShort();
        msgDB.setId(MsgSid);
        msgDB.setAcceptUserId(chatMsg.getReceiverId());
        msgDB.setSendUserId(chatMsg.getSenderId());
        msgDB.setCreateTime(new Date());
        msgDB.setSignFlag(MsgSignFlagEnum.unsign.type);
        msgDB.setMsg(chatMsg.getMsg());

        chatMsgMapper.insert(msgDB);

        return MsgSid;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateMsgSigned(List<String> msgIdList) {
        usersMapperCustom.batchUpdateMsgSigned(msgIdList);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<com.imooc.muxin.pojo.ChatMsg> getUnReadMsgList(String acceptUserId) {
        Example example = new Example(com.imooc.muxin.pojo.ChatMsg.class);
        Criteria criteria = example.createCriteria();
        criteria.andEqualTo("signFlag",0);
        criteria.andEqualTo("acceptUserId",acceptUserId);

        List<com.imooc.muxin.pojo.ChatMsg> chatMsgList = chatMsgMapper.selectByExample(example);

        return chatMsgList;
    }

    private void saveFriends(String acceptUserId, String sendUserId){
        MyFriends myFriends = new MyFriends();
        String recordId = sid.nextShort();
        myFriends.setId(recordId);
        myFriends.setMyFriendUserId(sendUserId);
        myFriends.setMyUserId(acceptUserId);
        myFriendsMapper.insert(myFriends);
    }


    private Users queryUserById(String id) {
        return usersMapper.selectByPrimaryKey(id);
    }


}
