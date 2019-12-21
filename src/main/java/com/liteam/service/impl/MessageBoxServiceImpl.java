package com.liteam.service.impl;


import com.liteam.dao.MessageBoxDao;
import com.liteam.entity.MessageBox;
import com.liteam.service.MessageBoxService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Service
public class MessageBoxServiceImpl implements MessageBoxService{

    @Resource
    private MessageBoxDao messageBoxDao;

    @Override
    public MessageBox addMessage(int userNumber, int sendNumber, int receiveNumebr, String content, int type, String nickname, String headUrl, String sex, java.sql.Date birthday){
        MessageBox messageBox = new MessageBox();
        messageBox.setContent(content);
        messageBox.setUserNumber(userNumber);
        messageBox.setNickname(nickname);
        messageBox.setHeadUrl(headUrl);
        messageBox.setBirthday(birthday);
        messageBox.setSex(sex);
        messageBox.setSendNumber(sendNumber);
        messageBox.setReceiveNumber(receiveNumebr);
        messageBox.setType(type);
        Date date = new Date();
        Timestamp timeStamp = new Timestamp(date.getTime());
        messageBox.setTime(timeStamp);
        messageBoxDao.save(messageBox);
        return messageBox;
    }

    @Override
    public List<MessageBox> getMessages(int userNumber, int type){
        return messageBoxDao.findAllByUserNumberAndType(userNumber, type);
    }

    @Override
    public void deleteMessage(int messageBoxId){
        MessageBox messageBox = messageBoxDao.findByMessageBoxId(messageBoxId);
        messageBoxDao.delete(messageBox);
    }

    @Override
    public void readMessage(int messageBoxId){
        MessageBox messageBox = messageBoxDao.findByMessageBoxId(messageBoxId);
        messageBox.setIsRead(1);
        messageBoxDao.save(messageBox);
    }

    @Override
    public void dealMessage(int messageBoxId, int isDeal){
        MessageBox messageBox = messageBoxDao.findByMessageBoxId(messageBoxId);
        messageBox.setIsDeal(isDeal);
        messageBoxDao.save(messageBox);
    }

    @Override
    public List<MessageBox> getMessagesFriends(int userNumber, int friendNumber, int type){
        List<MessageBox> messageBoxesReceive = messageBoxDao.findAllByUserNumberAndReceiveNumberAndType(userNumber,friendNumber, type);
        List<MessageBox> messageBoxesSend = messageBoxDao.findAllByUserNumberAndSendNumberAndType(userNumber,friendNumber, type);
        messageBoxesReceive.addAll(messageBoxesSend);
        return messageBoxesReceive;
    }

    @Override
    public List<MessageBox> allMessages(int userNumber){
        return messageBoxDao.findAllByUserNumber(userNumber);
    }

    @Override
    public MessageBox getMessage(int messageBoxId){
        return messageBoxDao.findByMessageBoxId(messageBoxId);
    }
}
