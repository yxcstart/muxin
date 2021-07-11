package com.imooc.muxin.mapper;

import com.imooc.muxin.pojo.ChatMsg;
import com.imooc.muxin.utils.MyMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChatMsgMapper extends MyMapper<ChatMsg> {
}