package com.imooc.muxin;

import com.imooc.muxin.mapper.UsersMapperCustom;
import com.imooc.muxin.pojo.MyFriends;
import com.imooc.muxin.pojo.VO.MyFriendsVo;
import com.imooc.muxin.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author yang
 * @create 2021-07-09 21:45
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class queryMyFriendsTest {
    @Autowired
    private UserService userService;
    @Autowired
    private UsersMapperCustom usersMapperCustom;

    @Test
    public void queryMyFriendsTest(){
        List<MyFriendsVo> voList = usersMapperCustom.queryMyFriends("210708DY06Y5YFW0");
        for (MyFriendsVo myFriends:voList){
            System.out.println(myFriends);
        }
    }
}
