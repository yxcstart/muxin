package com.imooc.muxin.mapper;

import com.imooc.muxin.pojo.Users;
import com.imooc.muxin.pojo.VO.FriendRequestVo;
import com.imooc.muxin.pojo.VO.MyFriendsVo;
import com.imooc.muxin.utils.MyMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UsersMapperCustom extends MyMapper<Users> {
    List<FriendRequestVo> queryFriendRequest(String acceptUserId);

    List<MyFriendsVo> queryMyFriends(String userId);

    void batchUpdateMsgSigned(List<String> msgIdList);
}