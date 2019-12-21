package com.liteam.controller;

import com.liteam.entity.Commend;
import com.liteam.entity.User;
import com.liteam.entity.UserOnline;
import com.liteam.service.FriendService;
import com.liteam.service.UserService;
import com.liteam.common.JsonResult;
import com.liteam.entity.*;
import com.liteam.service.ChatService;
import com.liteam.service.CommendService;
import com.liteam.utils.BaseController;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/data")
public class DataController extends BaseController{

    @Resource
    private FriendService friendService;

    @Resource
    private ChatService chatService;

    @Resource
    private UserService userService;

    @Resource
    private CommendService commendService;

    private UserOnline userOnline = new UserOnline();

    private JsonResult jsonResult = new JsonResult();

    /**
     * 获取用户数
     * @return
     */
    @GetMapping(value = "/user_number")
    public Object userNumber(){
        return jsonResult.success(userService.count());
    }

    /**
     * 获取用户在线数
     * @return
     */
    @GetMapping(value = "/user_online")
    public Object userOnline(){
        List<User> users = userOnline.getUsers();
        return jsonResult.success(users.size());
    }

    /**
     * 获取聊天记录数
     * @return
     */
    @GetMapping(value = "/chat_number")
    public Object friendsOnline(){
        return jsonResult.success(chatService.count());
    }

    /**
     * 获取好友印象
     * @return
     */
    @GetMapping(value = "/commend")
    public Object commend(){
        List<Commend> commends = commendService.allCommends();
        Map<String, Integer> map = new HashMap<>();
        for (Commend commend:commends){
            if (map.containsKey(commend.getCommend())){
                map.put(commend.getCommend(), map.get(commend.getCommend())+1);
            }else {
                map.put(commend.getCommend(), 1);
            }
        }
        return jsonResult.success(map);
    }

    /**
     * 返回男女数
     * @return
     */
    @GetMapping(value = "/sex")
    public Object userSex(){
        List<User> users = userService.all();
        int men = 0;
        int women = 0;
        int unknown = 0;
        for (User user:users){
            if (user.getSex().equals("男")){
                men++;
            }else if(user.getSex().equals("女")){
                women++;
            }else {
                unknown++;
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("men", men);
        map.put("women", women);
        map.put("unkown", unknown);
        return jsonResult.success(map);
    }
}
