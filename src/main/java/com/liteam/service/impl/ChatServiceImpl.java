package com.liteam.service.impl;

import com.liteam.dao.ChatDao;
import com.liteam.dao.FriendDao;
import com.liteam.entity.Chat;
import com.liteam.entity.Friend;
import com.liteam.entity.MessageBox;
import com.liteam.service.ChatService;
import com.liteam.service.MessageBoxService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {

    @Resource
    private ChatDao chatDao;

    @Resource
    private FriendDao friendDao;

    @Resource
    private MessageBoxService messageBoxService;

    @Override
    public Chat sendMessage(int userNumber, int sendNumber, int receiveNumber, String message){
        Chat chat = new Chat();
        chat.setMessage(message);
        chat.setUserNumber(userNumber);
        chat.setReceiveNumber(receiveNumber);
        chat.setSendNumber(sendNumber);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        chat.setTime(timestamp);
        chatDao.save(chat);
        return chat;
    }

    @Override
    public void inChat(int sendNumebr, int receiveNumber){
        Friend friend = friendDao.findByFriendNumberAndUserNumber(receiveNumber, sendNumebr);
        friend.setIsChat(1);
        friendDao.save(friend);
    }

    @Override
    public void outChat(int sendNumebr, int receiveNumber){
        Friend friend = friendDao.findByFriendNumberAndUserNumber(receiveNumber, sendNumebr);
        friend.setIsChat(0);
        friendDao.save(friend);
    }

    @Override
    public List<Chat> allChatMessages(int userNumber){
        return chatDao.findAllByUserNumber(userNumber);
    }

    @Override
    public List<Chat> chatMessages(int userNumber, int friendNumber){
        List<Chat> chatsFrom = chatDao.findAllByUserNumberAndReceiveNumber(userNumber, friendNumber);
        List<Chat> chatsTo = chatDao.findAllByUserNumberAndSendNumber(userNumber, friendNumber);
        chatsFrom.addAll(chatsTo);
        return chatsFrom;
    }

    @Override
    public void deleteMessages(int userNumber, int friendNumber){
        List<Chat> chats = chatMessages(userNumber, friendNumber);
        for (Chat chat:chats){
            chatDao.delete(chat);
        }
        List<MessageBox> messageBoxes = messageBoxService.getMessagesFriends(userNumber, friendNumber, 0);
        for (MessageBox messageBox:messageBoxes){
            messageBoxService.deleteMessage(messageBox.getMessageBoxId());
        }
    }

    @Override
    public int count(){
        return (int)chatDao.count();
    }
}
