package com.liteam.service;

import com.liteam.entity.Chat;

import java.util.List;

public interface ChatService {
    /**
     * 进行聊天
     * @param userNumber
     * @param sendNumber
     * @param receiveNumber
     * @param message
     */
    Chat sendMessage(int userNumber, int sendNumber, int receiveNumber, String message);

    /**
     * 进入聊天
     * @param sendNumber
     * @param receiveNumber
     * @return
     */
    void inChat(int sendNumber, int receiveNumber);

    /**
     * 退出聊天
     * @param sendNumber
     * @param receiveNumber
     */
    void outChat(int sendNumber, int receiveNumber);

    /**
     *
     * @param userNumber
     * @return
     */
    List<Chat> allChatMessages(int userNumber);

    /**
     * 获取与好友聊天信息
     * @param userNumber
     * @return
     */
    List<Chat> chatMessages(int userNumber, int friendNumber);

    /**
     * 清空聊天记录
     * @param userNumber
     */
    void deleteMessages(int userNumber, int friendNumber);

    /**
     * 获取聊天记录数
     * @return
     */
    int count();
}
