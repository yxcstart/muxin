package com.imooc.muxin;

import com.imooc.muxin.netty.ChatMsg;
import com.imooc.muxin.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author yang
 * @create 2021-07-11 10:08
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class saveMsgTest {
    @Autowired
    private UserService userService;

    @Test
    public void test(){
        ChatMsg chatMsg = new ChatMsg();
        chatMsg.setMsg("111");
        chatMsg.setMsgId("101");
        chatMsg.setSenderId("aa");
        chatMsg.setReceiverId("bb");
        System.out.println(userService.saveMsg(chatMsg));
    }
}
