package com.liteam.controller;

import com.jcraft.jsch.SftpException;
import com.liteam.entity.*;
import com.liteam.service.*;
import com.liteam.entity.*;
import com.liteam.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.security.Principal;
import java.sql.Date;
import java.util.*;

//@RestController //注册一个Controller，WebSocket的消息处理需要放在Controller下
//@RequestMapping("/socket")
//public class WebSocketController {
//
//    @Autowired
//    private SimpMessagingTemplate messagingTemplate;    //Spring WebSocket消息发送模板
//
//    //发送广播通知
//    @MessageMapping("/addNotice")   //接收客户端发来的消息，客户端发送消息地址为：/app/addNotice
//    @SendTo("/topic/notice")        //向客户端发送广播消息（方式一），客户端订阅消息地址为：/topic/notice
//    public WsMessage notice(String notice, Principal fromUser) {
//        //TODO 业务处理
//        WsMessage msg = new WsMessage();
//        msg.setFromName(fromUser.getName());
//        msg.setContent(notice);
//
//        //向客户端发送广播消息（方式二），客户端订阅消息地址为：/topic/notice
////        messagingTemplate.convertAndSend("/topic/notice", msg);
//        return msg;
//    }
//
//    //发送点对点消息
//    @MessageMapping("/msg")         //接收客户端发来的消息，客户端发送消息地址为：/app/msg
//    @SendToUser("/queue/msg/result") //向当前发消息客户端（就是自己）发送消息的发送结果，客户端订阅消息地址为：/user/queue/msg/result
//    public boolean sendMsg(WsMessage message, Principal fromUser){
//        //TODO 业务处理
//        message.setFromName(fromUser.getName());
//
//        //向指定客户端发送消息，第一个参数Principal.name为前面websocket握手认证通过的用户name（全局唯一的），客户端订阅消息地址为：/user/queue/msg/new
//        messagingTemplate.convertAndSendToUser(message.getToName(), "/queue/msg/new", message);
//        return true;
//    }
//}

@RestController
@RequestMapping("/socket")
public class WebSocketController {

    @Resource
    private UserService userService;

    @Resource
    private MessageBoxService messageBoxService;

    @Resource
    private ChatService chatService;

    @Resource
    private FriendService friendService;

    @Resource
    private GroupService groupService;

    @Resource
    private CommendService commendService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    private UserOnline userOnline = new UserOnline();

    /**
     * 进行连接并获取本人的所有消息
     */
    @MessageMapping(value = "/connect")
    public void connect(Principal fromUser){
        int number = Integer.parseInt(fromUser.getName());
        ArrayList<User> users = userOnline.getUsers();
        if (!users.contains((User) fromUser)){
            users.add((User) fromUser);
        }
        userOnline.setUsers(users);
        List<MessageBox> messageBoxesGossip = messageBoxService.getMessages(number, 0);
        Collections.sort(messageBoxesGossip,new Comparator<Object>(){
            @Override
            public int compare(Object o1, Object o2) {
                return ((MessageBox) o2).getMessageBoxId() - ((MessageBox) o1).getMessageBoxId();
            }
        });
        String sendToGossip = "/queue/gossip";
        simpMessagingTemplate.convertAndSendToUser(fromUser.getName(), sendToGossip, messageBoxesGossip);
        List<MessageBox> messageBoxesApply = messageBoxService.getMessages(number, 1);
        List<MessageBox> messageBoxesCommend = messageBoxService.getMessages(number, 2);
        messageBoxesApply.addAll(messageBoxesCommend);
        Collections.sort(messageBoxesApply,new Comparator<Object>(){
            @Override
            public int compare(Object o1, Object o2) {
                return ((MessageBox) o2).getMessageBoxId() - ((MessageBox) o1).getMessageBoxId();
            }
        });
        String sendToNotice = "/queue/notice";
        simpMessagingTemplate.convertAndSendToUser(fromUser.getName(), sendToNotice, messageBoxesApply);
        List<Friend> friendsNew = friendService.findAllFriends(number);
        for (Friend friendNew:friendsNew){
            ArrayList<Map<String, Object>> friendList = allFriends(friendNew.getFriendNumber());
            String sendTo = "/queue/friends";
            simpMessagingTemplate.convertAndSendToUser(String.valueOf(friendNew.getFriendNumber()), sendTo, friendList);
        }
    }

    /**
     * 获取所有的好友
     */
    @MessageMapping(value = "/friends")
    public void friends(Principal fromUser){
        int number = Integer.parseInt(fromUser.getName());
        ArrayList<Map<String, Object>> friendList = allFriends(number);
        String sendTo = "/queue/friends";
        simpMessagingTemplate.convertAndSendToUser(fromUser.getName(), sendTo, friendList);
    }

    /**
     * 进入与某好友的聊天
     * @param messageBox
     */
    @MessageMapping(value = "/inChat")
    public void inChat(MessageBox messageBox, Principal fromUser){
        int sendNumber = Integer.parseInt(fromUser.getName());
        int receiveNumber = messageBox.getReceiveNumber();
        List<MessageBox> messageBoxes = messageBoxService.getMessagesFriends(sendNumber, receiveNumber, 0);
        for (MessageBox messageBoxNew:messageBoxes){
            messageBoxService.readMessage(messageBoxNew.getMessageBoxId());
        }
        chatService.inChat(sendNumber, receiveNumber);
        String sendToChat = "/queue/chat";
        List<Chat> chats = chatService.chatMessages(sendNumber, receiveNumber);
        Collections.sort(chats,new Comparator<Object>(){
            @Override
            public int compare(Object o1, Object o2) {
                return ((Chat) o1).getChatId() - ((Chat) o2).getChatId();
            }
        });
        simpMessagingTemplate.convertAndSendToUser(String.valueOf(sendNumber), sendToChat, chats);
    }

    /**
     * 进行聊天
     * @param chat
     */
    @MessageMapping(value = "/chat")
    public void chat(Chat chat, Principal fromUser) {
        int sendNumber = Integer.parseInt(fromUser.getName());
        int receiveNumber = chat.getReceiveNumber();
        String message = chat.getMessage();
        Chat chatNew = chatService.sendMessage(sendNumber, sendNumber, receiveNumber, message);
        chatService.sendMessage(receiveNumber, sendNumber, receiveNumber, message);
        String sendToChat = "/queue/chat";
        String sendToGossip = "/queue/gossip";
        Friend friend = friendService.findOneFriend(receiveNumber, sendNumber);
        User user = userService.findOne(sendNumber);
        User userReceive = userService.findOne(receiveNumber);
        MessageBox messageBoxSend = messageBoxService.addMessage(sendNumber, sendNumber, receiveNumber, message, 0, userReceive.getNickname(), userReceive.getHeadUrl(), userReceive.getSex(), userReceive.getBirthday());
        messageBoxService.readMessage(messageBoxSend.getMessageBoxId());
        simpMessagingTemplate.convertAndSendToUser(String.valueOf(sendNumber), sendToChat, chatNew);
        if (friend.getIsChat() == 1){
            MessageBox messageBox = messageBoxService.addMessage(receiveNumber, sendNumber, receiveNumber, message, 0, user.getNickname(), user.getHeadUrl(), user.getSex(), user.getBirthday());
            messageBoxService.readMessage(messageBox.getMessageBoxId());
            simpMessagingTemplate.convertAndSendToUser(String.valueOf(receiveNumber), sendToChat, chatNew);
        } else {
            messageBoxService.addMessage(receiveNumber, sendNumber, receiveNumber, message, 0, user.getNickname(), user.getHeadUrl(), user.getSex(), user.getBirthday());
            List<MessageBox> messageBoxesReceive = messageBoxService.getMessages(receiveNumber, 0);
            Collections.sort(messageBoxesReceive,new Comparator<Object>(){
                @Override
                public int compare(Object o1, Object o2) {
                    return ((MessageBox) o2).getMessageBoxId() - ((MessageBox) o1).getMessageBoxId();
                }
            });
            simpMessagingTemplate.convertAndSendToUser(String.valueOf(receiveNumber), sendToGossip, messageBoxesReceive);
        }
    }

    /**
     * 退出与某好友的聊天
     * @param chat
     */
    @MessageMapping(value = "/outChat")
    public void outChat(Chat chat, Principal fromUser){
        int sendNumber = Integer.parseInt(fromUser.getName());
        int receiveNumber = chat.getReceiveNumber();
        chatService.outChat(sendNumber, receiveNumber);
        List<MessageBox> messageBoxesGossip = messageBoxService.getMessages(sendNumber, 0);
        Collections.sort(messageBoxesGossip,new Comparator<Object>(){
            @Override
            public int compare(Object o1, Object o2) {
                return ((MessageBox) o2).getMessageBoxId() - ((MessageBox) o1).getMessageBoxId();
            }
        });
        String sendToGossip = "/queue/gossip";
        simpMessagingTemplate.convertAndSendToUser(fromUser.getName(), sendToGossip, messageBoxesGossip);
        List<MessageBox> messageBoxesApply = messageBoxService.getMessages(sendNumber, 1);
        List<MessageBox> messageBoxesCommend = messageBoxService.getMessages(sendNumber, 2);
        messageBoxesApply.addAll(messageBoxesCommend);
        Collections.sort(messageBoxesApply,new Comparator<Object>(){
            @Override
            public int compare(Object o1, Object o2) {
                return ((MessageBox) o2).getMessageBoxId() - ((MessageBox) o1).getMessageBoxId();
            }
        });
        String sendToNotice = "/queue/notice";
        simpMessagingTemplate.convertAndSendToUser(fromUser.getName(), sendToNotice, messageBoxesApply);
    }

    /**
     * 申请添加为好友
     * @param friendApplyNew
     */
    @MessageMapping(value = "/apply")
    public void applyFriends(FriendApply friendApplyNew, Principal fromUser){
        int sendNumber = Integer.parseInt(fromUser.getName());
        int receiveNumber = friendApplyNew.getReceiveNumber();
        int groupId = friendApplyNew.getGroupId();
        String remark = friendApplyNew.getRemark();
        User user = userService.findOne(sendNumber);
        FriendApply friendApply = friendService.getFriendApply(sendNumber, receiveNumber);
        if (friendApply != null){
            friendService.deleteFriendApply(sendNumber, receiveNumber);
        }
        friendService.applyFriend(sendNumber, receiveNumber, groupId, remark);
        List<MessageBox> messageBoxes = messageBoxService.getMessagesFriends(sendNumber, receiveNumber, 1);
        if (messageBoxes != null){
            for (MessageBox messageBox:messageBoxes){
                messageBoxService.deleteMessage(messageBox.getMessageBoxId());
            }
        }
        messageBoxService.addMessage(receiveNumber, sendNumber, receiveNumber, "该用户请求添加您为好友", 1, user.getNickname(), user.getHeadUrl(), user.getSex(), user.getBirthday());
        String sendTo = "/queue/notice";
        List<MessageBox> messageBoxesReceive = messageBoxService.getMessages(receiveNumber, 1);
        List<MessageBox> messageBoxesSend = messageBoxService.getMessages(receiveNumber, 2);
        messageBoxesReceive.addAll(messageBoxesSend);
        Collections.sort(messageBoxesReceive,new Comparator<Object>(){
            @Override
            public int compare(Object o1, Object o2) {
                return ((MessageBox) o2).getMessageBoxId() - ((MessageBox) o1).getMessageBoxId();
            }
        });
        simpMessagingTemplate.convertAndSendToUser(String.valueOf(receiveNumber), sendTo, messageBoxesReceive);
    }

    /**
     * 处理好友申请
     * @param userFriend
     */
    @MessageMapping(value = "/deal")
    public void dealApply(UserFriend userFriend, Principal fromUser){
        int number = Integer.parseInt(fromUser.getName());
        User user = userService.findOne(number);
        int sendNumber = userFriend.getSendNumber();
        int isDeal = userFriend.getIsDeal();
        FriendApply friendApply = friendService.getFriendApply(sendNumber, number);
        if (isDeal == 1){
            int groupId = userFriend.getGroupId();
            String remark = userFriend.getRemark();
            friendService.addFriendByNumber(number, groupId, sendNumber, remark);
            friendService.addFriendByNumber(sendNumber, friendApply.getGroupId(), number, friendApply.getRemark());
            messageBoxService.addMessage(sendNumber, number, sendNumber, "该用户同意添加您为好友", 1, user.getNickname(), user.getHeadUrl(), user.getSex(), user.getBirthday());
        }
        if (isDeal == 2){
            messageBoxService.addMessage(sendNumber, number, sendNumber, "该用户拒绝添加您为好友", 1, user.getNickname(), user.getHeadUrl(), user.getSex(), user.getBirthday());
        }
        List<MessageBox> messageBoxes = messageBoxService.getMessagesFriends(sendNumber, number, 1);
        for (MessageBox messageBox:messageBoxes){
            if (messageBox.getIsDeal() == 0){
                messageBoxService.dealMessage(messageBox.getMessageBoxId(), isDeal);
            }
        }
        String sendTo = "/queue/notice";
        List<MessageBox> messageBoxesSend = messageBoxService.getMessagesFriends(number, sendNumber, 1);
        for (MessageBox messageBox:messageBoxesSend){
            if (messageBox.getIsDeal() == 0){
                messageBoxService.dealMessage(messageBox.getMessageBoxId(), isDeal);
            }
        }
        List<MessageBox> messageBoxesReceive = messageBoxService.getMessages(sendNumber, 1);
        List<MessageBox> messageBoxesReceive1 = messageBoxService.getMessages(sendNumber, 2);
        messageBoxesReceive.addAll(messageBoxesReceive1);
        List<MessageBox> messageBoxesSendTo = messageBoxService.getMessages(number, 1);
        List<MessageBox> messageBoxesSendTo1 = messageBoxService.getMessages(number, 2);
        messageBoxesSendTo.addAll(messageBoxesSendTo1);
        Collections.sort(messageBoxesReceive,new Comparator<Object>(){
            @Override
            public int compare(Object o1, Object o2) {
                return ((MessageBox) o2).getMessageBoxId() - ((MessageBox) o1).getMessageBoxId();
            }
        });
        Collections.sort(messageBoxesSendTo,new Comparator<Object>(){
            @Override
            public int compare(Object o1, Object o2) {
                return ((MessageBox) o2).getMessageBoxId() - ((MessageBox) o1).getMessageBoxId();
            }
        });
        simpMessagingTemplate.convertAndSendToUser(String.valueOf(sendNumber), sendTo, messageBoxesReceive);
        simpMessagingTemplate.convertAndSendToUser(String.valueOf(number), sendTo, messageBoxesSendTo);
        ArrayList<Map<String, Object>> friendListSend = allFriends(number);
        String sendToSend = "/queue/friends";
        simpMessagingTemplate.convertAndSendToUser(String.valueOf(number), sendToSend, friendListSend);
        ArrayList<Map<String, Object>> friendListReceive = allFriends(sendNumber);
        String sendToReceive = "/queue/friends";
        simpMessagingTemplate.convertAndSendToUser(String.valueOf(sendNumber), sendToReceive, friendListReceive);
    }

    /**
     *
     * 修改电话号
     * @param userFriend
     * @return
     */
    @MessageMapping(value = "/update_phone")
    public void modifyPhone(UserFriend userFriend, Principal fromUser){
        int number = Integer.parseInt(fromUser.getName());
        User modifyUser = userService.modifyPhone(number, userFriend.getPhone());
        modifyUser.setPassword(null);
        Map<String, Object> map = new HashMap<>();
        map.put("user", modifyUser);
        List<Commend> commends = commendService.getCommends(number);
        map.put("commends", commends);
        String sendTo = "/queue/user";
        simpMessagingTemplate.convertAndSendToUser(fromUser.getName(), sendTo, map);
        List<Friend> friendsNew = friendService.findAllFriends(number);
        for (Friend friendNew:friendsNew){
            ArrayList<Map<String, Object>> friendList = allFriends(friendNew.getFriendNumber());
            String sendToFriend = "/queue/friends";
            simpMessagingTemplate.convertAndSendToUser(String.valueOf(friendNew.getFriendNumber()), sendToFriend, friendList);
        }
    }

    /**
     * 修改昵称
     * @param userFriend
     * @return
     */
    @MessageMapping(value = "/update_nickname")
    public void modifyNickname(UserFriend userFriend, Principal fromUser){
        int number = Integer.parseInt(fromUser.getName());
        User modifyUser = userService.modifyNickname(number, userFriend.getNickname());
        modifyUser.setPassword(null);
        Map<String, Object> map = new HashMap<>();
        map.put("user", modifyUser);
        List<Commend> commends = commendService.getCommends(number);
        map.put("commends", commends);
        String sendTo = "/queue/user";
        simpMessagingTemplate.convertAndSendToUser(fromUser.getName(), sendTo, map);
        List<Friend> friendsNew = friendService.findAllFriends(number);
        for (Friend friendNew:friendsNew){
            ArrayList<Map<String, Object>> friendList = allFriends(friendNew.getFriendNumber());
            String sendToFriend = "/queue/friends";
            simpMessagingTemplate.convertAndSendToUser(String.valueOf(friendNew.getFriendNumber()), sendToFriend, friendList);
        }
    }

    /**
     * 修改基础信息
     * @param userFriend
     * @return
     */
    @MessageMapping(value = "/update_user_info")
    public void modifyBasicInformation(UserFriend userFriend, Principal fromUser){
        int number = Integer.parseInt(fromUser.getName());
        Date date = Date.valueOf(userFriend.getBirthday());
        User modifyUser = userService.modifyBasicInformation(number, userFriend.getNativePlace(), userFriend.getSignature(), userFriend.getSex(), date);
        modifyUser.setPassword(null);
        Map<String, Object> map = new HashMap<>();
        map.put("user", modifyUser);
        List<Commend> commends = commendService.getCommends(number);
        map.put("commends", commends);
        String sendTo = "/queue/user";
        simpMessagingTemplate.convertAndSendToUser(fromUser.getName(), sendTo, map);
        List<Friend> friendsNew = friendService.findAllFriends(number);
        for (Friend friendNew:friendsNew){
            ArrayList<Map<String, Object>> friendList = allFriends(friendNew.getFriendNumber());
            String sendToFriend = "/queue/friends";
            simpMessagingTemplate.convertAndSendToUser(String.valueOf(friendNew.getFriendNumber()), sendToFriend, friendList);
        }
    }

    /**
     *
     * 修改头像
     * @param userFriend
     * @return
     */
    @MessageMapping(value = "/update_head_url")
    public void modifyHead(UserFriend userFriend, Principal fromUser){
        int number = Integer.parseInt(fromUser.getName());
        MultipartFile file = userFriend.getHeadUrl();
        try{
            byte[] bytes = file.getBytes();
            String name = file.getOriginalFilename();
            FileConverse fileConverse = new FileConverse();
            fileConverse.login();
            fileConverse.upload(name, bytes);
            String url = fileConverse.url();
            fileConverse.logout();
            User modifyUser = userService.modifyHead(number, url);
            modifyUser.setPassword(null);
            Map<String, Object> map = new HashMap<>();
            map.put("user", modifyUser);
            List<Commend> commends = commendService.getCommends(number);
            map.put("commends", commends);
            String sendTo = "/queue/user";
            simpMessagingTemplate.convertAndSendToUser(fromUser.getName(), sendTo, map);
        }
        catch (SftpException e){e.printStackTrace();}
        catch (IOException ex){ex.printStackTrace();}
        List<Friend> friendsNew = friendService.findAllFriends(number);
        for (Friend friendNew:friendsNew){
            ArrayList<Map<String, Object>> friendList = allFriends(friendNew.getFriendNumber());
            String sendToFriend = "/queue/friends";
            simpMessagingTemplate.convertAndSendToUser(String.valueOf(friendNew.getFriendNumber()), sendToFriend, friendList);
        }
    }

    /**
     * 好友添加评价
     * @param userFriend
     */
    @MessageMapping(value = "/add_commend")
    public void commend(UserFriend userFriend, Principal fromUser){
        int sendNumber = Integer.parseInt(fromUser.getName());
        User user = userService.findOne(sendNumber);
        int receiveNumber = userFriend.getReceiveNumber();
        String commend = userFriend.getCommend();
        commendService.addCommend(sendNumber, receiveNumber, commend, user.getHeadUrl());
        messageBoxService.addMessage(receiveNumber, sendNumber, receiveNumber, "评价你:" + commend, 2, user.getNickname(), user.getHeadUrl(), user.getSex(), user.getBirthday());
        List<MessageBox> messageBoxesApply = messageBoxService.getMessages(receiveNumber, 1);
        List<MessageBox> messageBoxesCommend = messageBoxService.getMessages(receiveNumber, 2);
        messageBoxesApply.addAll(messageBoxesCommend);
        Collections.sort(messageBoxesApply,new Comparator<Object>(){
            @Override
            public int compare(Object o2, Object o1) {
                return ((MessageBox) o2).getMessageBoxId() - ((MessageBox) o1).getMessageBoxId();
            }
        });
        String sendToNotice = "/queue/notice";
        simpMessagingTemplate.convertAndSendToUser(String.valueOf(receiveNumber), sendToNotice, messageBoxesApply);
    }

    /**
     * 断开连接
     */
    @MessageMapping(value = "/outConnect")
    public void outConnect(Principal fromUser){
        int number = Integer.parseInt(fromUser.getName());
        ArrayList<User> users = userOnline.getUsers();
        users.remove((User)fromUser);
        userOnline.setUsers(users);
        List<Friend> friendsNew = friendService.findAllFriends(number);
        for (Friend friendNew:friendsNew){
            ArrayList<Map<String, Object>> friendList = allFriends(friendNew.getFriendNumber());
            String sendTo = "/queue/friends";
            simpMessagingTemplate.convertAndSendToUser(String.valueOf(friendNew.getFriendNumber()), sendTo, friendList);
        }
    }

    /**
     * 删除消息
     * @param userFriend
     * @return
     */
    @MessageMapping(value = "delete_message")
    public void deleteMessage(UserFriend userFriend, Principal fromUser){
        User user = (User)fromUser;
        int number = Integer.parseInt(fromUser.getName());
        int messageBoxId = userFriend.getMessageBoxId();
        MessageBox messageBox = messageBoxService.getMessage(messageBoxId);
        messageBoxService.deleteMessage(messageBox.getMessageBoxId());
        List<MessageBox> messageBoxesGossip = messageBoxService.getMessages(number, 0);
        String sendToGossip = "/queue/gossip";
        Collections.sort(messageBoxesGossip,new Comparator<Object>(){
            @Override
            public int compare(Object o1, Object o2) {
                return ((MessageBox) o2).getMessageBoxId() - ((MessageBox) o1).getMessageBoxId();
            }
        });
        simpMessagingTemplate.convertAndSendToUser(fromUser.getName(), sendToGossip, messageBoxesGossip);
        List<MessageBox> messageBoxesApply = messageBoxService.getMessages(number, 1);
        List<MessageBox> messageBoxesCommend = messageBoxService.getMessages(number, 2);
        messageBoxesApply.addAll(messageBoxesCommend);
        String sendToNotice = "/queue/notice";
        Collections.sort(messageBoxesApply,new Comparator<Object>(){
            @Override
            public int compare(Object o2, Object o1) {
                return ((MessageBox) o2).getMessageBoxId() - ((MessageBox) o1).getMessageBoxId();
            }
        });
        simpMessagingTemplate.convertAndSendToUser(fromUser.getName(), sendToNotice, messageBoxesApply);
    }

    /**
     * 删除聊天消息
     * @param userFriend
     * @return
     */
    @MessageMapping(value = "delete_messages")
    public void deleteMessages(UserFriend userFriend, Principal fromUser){
        User user = (User)fromUser;
        int number = Integer.parseInt(fromUser.getName());
        int friendNumber = userFriend.getFriendNumber();
        List<MessageBox> messageBoxList = messageBoxService.getMessagesFriends(number, friendNumber, 0);
        for (MessageBox messageBox:messageBoxList){
            messageBoxService.deleteMessage(messageBox.getMessageBoxId());
        }
        List<MessageBox> messageBoxesGossip = messageBoxService.getMessages(number, 0);
        String sendToGossip = "/queue/gossip";
        Collections.sort(messageBoxesGossip,new Comparator<Object>(){
            @Override
            public int compare(Object o1, Object o2) {
                return ((MessageBox) o2).getMessageBoxId() - ((MessageBox) o1).getMessageBoxId();
            }
        });
        simpMessagingTemplate.convertAndSendToUser(fromUser.getName(), sendToGossip, messageBoxesGossip);
        List<MessageBox> messageBoxesApply = messageBoxService.getMessages(number, 1);
        List<MessageBox> messageBoxesCommend = messageBoxService.getMessages(number, 2);
        messageBoxesApply.addAll(messageBoxesCommend);
        String sendToNotice = "/queue/notice";
        Collections.sort(messageBoxesApply,new Comparator<Object>(){
            @Override
            public int compare(Object o2, Object o1) {
                return ((MessageBox) o2).getMessageBoxId() - ((MessageBox) o1).getMessageBoxId();
            }
        });
        simpMessagingTemplate.convertAndSendToUser(fromUser.getName(), sendToNotice, messageBoxesApply);
    }

    /**
     * 读取消息
     * @param userFriend
     * @return
     */
    @MessageMapping(value = "read_message")
    public void readMessage(UserFriend userFriend, Principal fromUser){
        User user = (User)fromUser;
        int number = Integer.parseInt(fromUser.getName());
        int friendNumber = userFriend.getFriendNumber();
        int type = userFriend.getType();
        List<MessageBox> messageBoxes = messageBoxService.getMessagesFriends(user.getNumber(), friendNumber, type);
        for (MessageBox messageBox:messageBoxes){
            messageBoxService.readMessage(messageBox.getMessageBoxId());
        }
        List<MessageBox> messageBoxesGossip = messageBoxService.getMessages(number, 0);
        String sendToGossip = "/queue/gossip";
        Collections.sort(messageBoxesGossip,new Comparator<Object>(){
            @Override
            public int compare(Object o1, Object o2) {
                return ((MessageBox) o2).getMessageBoxId() - ((MessageBox) o1).getMessageBoxId();
            }
        });
        simpMessagingTemplate.convertAndSendToUser(fromUser.getName(), sendToGossip, messageBoxesGossip);
        List<MessageBox> messageBoxesApply = messageBoxService.getMessages(number, 1);
        List<MessageBox> messageBoxesCommend = messageBoxService.getMessages(number, 2);
        messageBoxesApply.addAll(messageBoxesCommend);
        String sendToNotice = "/queue/notice";
        Collections.sort(messageBoxesApply,new Comparator<Object>(){
            @Override
            public int compare(Object o1, Object o2) {
                return ((MessageBox) o2).getMessageBoxId() - ((MessageBox) o1).getMessageBoxId();
            }
        });
        simpMessagingTemplate.convertAndSendToUser(fromUser.getName(), sendToNotice, messageBoxesApply);
    }

    /**
     * 读取系统消息
     * @return
     */
    @MessageMapping(value = "/read_notice")
    public void readMessage(Principal fromUser){
        int number = Integer.parseInt(fromUser.getName());
        List<MessageBox> messageBoxesNotice = messageBoxService.getMessages(number, 1);
        List<MessageBox> messageBoxesCommend = messageBoxService.getMessages(number, 2);
        messageBoxesNotice.addAll(messageBoxesCommend);
        for (MessageBox messageBox:messageBoxesNotice){
            messageBoxService.readMessage(messageBox.getMessageBoxId());
        }
        Collections.sort(messageBoxesNotice,new Comparator<Object>(){
            @Override
            public int compare(Object o1, Object o2) {
                return ((MessageBox) o2).getMessageBoxId() - ((MessageBox) o1).getMessageBoxId();
            }
        });
        String sendToNotice = "/queue/notice";
        List<MessageBox> messageBoxesNotice1 = messageBoxService.getMessages(number, 1);
        List<MessageBox> messageBoxesCommend1 = messageBoxService.getMessages(number, 2);
        messageBoxesNotice1.addAll(messageBoxesCommend1);
        simpMessagingTemplate.convertAndSendToUser(fromUser.getName(), sendToNotice, messageBoxesNotice1);
    }

    /**
     * 两边获取所有的好友
     */
    @MessageMapping(value = "/friends_two")
    public void friendsTwo(Friend friend, Principal fromUser){
        int number = Integer.parseInt(fromUser.getName());
        ArrayList<Map<String, Object>> friendList = allFriends(number);
        String sendTo = "/queue/friends";
        simpMessagingTemplate.convertAndSendToUser(fromUser.getName(), sendTo, friendList);
        ArrayList<Map<String, Object>> friendList1 = allFriends(friend.getFriendNumber());
        simpMessagingTemplate.convertAndSendToUser(String.valueOf(friend.getFriendNumber()), sendTo, friendList1);
    }

    /**
     * 获取消息盒子
     * @param fromUser
     */
    @MessageMapping(value = "messageBox")
    public void messageBox(Principal fromUser){
        int number = Integer.parseInt(fromUser.getName());
        List<MessageBox> messageBoxesGossip = messageBoxService.getMessages(number, 0);
        Collections.sort(messageBoxesGossip,new Comparator<Object>(){
            @Override
            public int compare(Object o1, Object o2) {
                return ((MessageBox) o2).getMessageBoxId() - ((MessageBox) o1).getMessageBoxId();
            }
        });
        String sendToGossip = "/queue/gossip";
        simpMessagingTemplate.convertAndSendToUser(fromUser.getName(), sendToGossip, messageBoxesGossip);
        List<MessageBox> messageBoxesApply = messageBoxService.getMessages(number, 1);
        List<MessageBox> messageBoxesCommend = messageBoxService.getMessages(number, 2);
        messageBoxesApply.addAll(messageBoxesCommend);
        Collections.sort(messageBoxesApply,new Comparator<Object>(){
            @Override
            public int compare(Object o1, Object o2) {
                return ((MessageBox) o2).getMessageBoxId() - ((MessageBox) o1).getMessageBoxId();
            }
        });
        String sendToNotice = "/queue/notice";
        simpMessagingTemplate.convertAndSendToUser(fromUser.getName(), sendToNotice, messageBoxesApply);
    }

    private ArrayList<Map<String, Object>> allFriends(int number){
        List<Friend> friends = friendService.findAllFriends(number);
        ArrayList<Map<String, Object>> friendList = new ArrayList<>();
        ArrayList<User> users = userOnline.getUsers();
        for (Friend friend:friends){
            Map<String, Object> map = new HashMap<>();
            User userFriend = userService.findOne(friend.getFriendNumber());
            Group group = groupService.getOne(friend.getGroupId());
            map.put("friendNumber", friend.getFriendNumber());
            map.put("userNumebr", friend.getUserNumber());
            map.put("remark", friend.getRemark());
            map.put("groupId", friend.getGroupId());
            map.put("signature", userFriend.getSignature());
            map.put("nickname", userFriend.getNickname());
            map.put("headUrl", userFriend.getHeadUrl());
            map.put("groupName", group.getGroupName());
            if(!users.contains(userFriend)){
                map.put("isOnline", false);
            }else {
                map.put("isOnline", true);
            }
            friendList.add(map);
        }
        return friendList;
    }
}
