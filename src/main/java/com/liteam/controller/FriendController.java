package com.liteam.controller;

import com.liteam.entity.*;
import com.liteam.service.*;
import com.liteam.common.JsonResult;
import com.liteam.entity.*;
import com.liteam.service.*;
import com.liteam.utils.BaseController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/friend")
public class FriendController extends BaseController {

    @Resource
    private FriendService friendService;

    @Resource
    private ChatService chatService;

    @Resource
    private UserService userService;

    @Resource
    private MessageBoxService messageBoxService;

    @Resource
    private GroupService groupService;

    private JsonResult jsonResult = new JsonResult();

    /**
     * 显示该分组的所有好友
     *
     * @param token
     * @param groupId
     * @return
     */
    @GetMapping(value = "/groupAllFriends")
    public Object getGroupAllFriends(@RequestHeader("Authorization") String token, @RequestParam("groupId") int groupId) {
        return jsonResult.success(friendService.findAllByGroupId(groupId));
    }

    /**
     * 获取该用户的所有好友
     *
     * @param token
     * @return
     */
    @GetMapping(value = "/all")
    public Object getAllFriends(@RequestHeader("Authorization") String token) {
        User user = getUser();
        List<Friend> friends = friendService.findAllFriends(user.getNumber());
        ArrayList<Map<String, Object>> friendList = new ArrayList<>();
        for (Friend friend : friends) {
            Map<String, Object> map = new HashMap<>();
            User userFriend = userService.findOne(friend.getFriendNumber());
            Group group = groupService.getOne(friend.getGroupId());
            map.put("friendNumber", friend.getFriendNumber());
            map.put("userNumebr", friend.getUserNumber());
            map.put("groupId", friend.getGroupId());
            map.put("remark", friend.getRemark());
            map.put("signature", userFriend.getSignature());
            map.put("nickname", userFriend.getNickname());
            map.put("headUrl", userFriend.getHeadUrl());
            map.put("groupName", group.getGroupName());
            friendList.add(map);
        }
        return jsonResult.success(friendList);
    }

    /**
     * 删除好友
     *
     * @param token
     * @param body
     * @return
     */
    @DeleteMapping(value = "/delete")
    public Object removeFriend(@RequestHeader("Authorization") String token, @RequestBody Map<String, Object> body) {
        User user = getUser();
        if (body.containsKey("friendNumber")) {
            friendService.removeFriend(user.getNumber(), (int) body.get("friendNumber"));
            friendService.removeFriend((int) body.get("friendNumber"), user.getNumber());
            List<MessageBox> messageBoxesUser = messageBoxService.getMessagesFriends(user.getNumber(), (int) body.get("friendNumber"), 0);
            for (MessageBox messageBox:messageBoxesUser){
                messageBoxService.deleteMessage(messageBox.getMessageBoxId());
            }
            List<MessageBox> messageBoxesFriend = messageBoxService.getMessagesFriends((int) body.get("friendNumber"), user.getNumber(), 0);
            for (MessageBox messageBox:messageBoxesFriend){
                messageBoxService.deleteMessage(messageBox.getMessageBoxId());
            }
            return jsonResult.success();
        } else {
            return jsonResult.failed(400, "缺少参数", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 移动好友的分组
     *
     * @param token
     * @param body
     * @return
     */
    @PutMapping(value = "/move")
    public Object modifyGroup(@RequestHeader("Authorization") String token, @RequestBody Map<String, Object> body) {
        User user = getUser();
        if (body.containsKey("friendNumber") && body.containsKey("groupId")) {
            int friendNumber = (int) body.get("friendNumber");
            int groupId = (int) body.get("groupId");
            friendService.modifyGroup(user.getNumber(), friendNumber, groupId);
            return jsonResult.success();
        } else {
            return jsonResult.failed(400, "缺少参数", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 修改好友备注
     *
     * @param token
     * @param body
     * @return
     */
    @PutMapping(value = "/update_remark")
    public Object updateRemark(@RequestHeader("Authorization") String token, @RequestBody Map<String, Object> body) {
        User user = getUser();
        if (body.containsKey("friendNumber") && body.containsKey("remark")) {
            int friendNumber = (int) body.get("friendNumber");
            String remark = (String) body.get("remark");
            friendService.modifyRemark(user.getNumber(), friendNumber, remark);
            return jsonResult.success();
        } else {
            return jsonResult.failed(400, "缺少参数", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 清空聊天记录
     * @param token
     * @param body
     * @return
     */
    @DeleteMapping(value = "delete_chat")
    public Object deleteChat(@RequestHeader("Authorization") String token, @RequestBody Map<String, Object> body){
        User user = getUser();
        if (body.containsKey("friendNumber")) {
            int friendNumber = (int) body.get("friendNumber");
            chatService.deleteMessages(user.getNumber(), friendNumber);
            return jsonResult.success();
        } else {
            return jsonResult.failed(400, "缺少参数", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 下载与某好友的聊天记录
     * @param token
     * @param body
     * @return
     */
    @PostMapping(value = "download")
    public Object download(@RequestHeader("Authorization") String token, @RequestBody Map<String, Object> body){
        User user = getUser();
        if (body.containsKey("friendNumber")) {
            int friendNumber = (int) body.get("friendNumber");
            List<Chat> chats = chatService.chatMessages(user.getNumber(), friendNumber);
            String msg = "";
            for (Chat chat:chats){
                User userFind = userService.findOne(chat.getSendNumber());
                String time = "";
                DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                time = sdf.format(chat.getTime());
                String str = time + " " + userFind.getNickname() + "：" + chat.getMessage();
                msg += str + "\n";
            }
            return jsonResult.success(msg);
        } else {
            return jsonResult.failed(400, "缺少参数", HttpStatus.BAD_REQUEST);
        }
    }
}
