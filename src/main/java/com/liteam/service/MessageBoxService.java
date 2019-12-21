package com.liteam.service;

import com.liteam.entity.MessageBox;

import java.util.List;

public interface MessageBoxService {
    /**
     * 消息盒子中增加消息
     * @param receiveNumebr
     * @param content
     * @param type
     * @return
     */
    MessageBox addMessage(int userNumber, int sendNumber, int receiveNumebr, String content, int type, String nickname, String headUrl, String sex, java.sql.Date birthday);

    /**
     * 获取消息盒子中某类所有的消息
     * @param userNumber
     * @return
     */
    List<MessageBox> getMessages(int userNumber, int type);

    /**
     * 删除消息盒子中的消息
     * @param messageBoxId
     */
    void deleteMessage(int messageBoxId);

    /**
     * 读取消息
     * @param messageBoxId
     */
    void readMessage(int messageBoxId);

    /**
     * 处理消息盒子中的消息
     * @param messageBoxId
     */
    void dealMessage(int messageBoxId, int isDeal);

    /**
     * 获取该用户与该好友的某种类型的所有消息
     * @param userNumber
     * @param friendNumber
     * @param type
     * @return
     */
    List<MessageBox> getMessagesFriends(int userNumber, int friendNumber, int type);

    /**
     * 获取自己所有的消息
     * @param userNumber
     * @return
     */
    List<MessageBox> allMessages(int userNumber);

    /**
     * 获取消息盒子
     * @param messageBoxId
     * @return
     */
    MessageBox getMessage(int messageBoxId);
}
