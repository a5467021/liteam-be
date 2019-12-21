package com.liteam.service;

import com.liteam.entity.Commend;

import java.util.List;

public interface CommendService {
    /**
     * 增添评论
     * @param userNumber
     * @param friendNumber
     * @param commend
     * @return
     */
    Commend addCommend(int userNumber, int friendNumber, String commend, String headUrl);

    /**
     * 删除评论
     * @param commendId
     */
    void deleteCommend(int commendId);

    /**
     * 获取该用户的所有评论
     * @param number
     * @return
     */
    List<Commend> getCommends(int number);

    /**
     * 获取所有的评论
     * @return
     */
    List<Commend> allCommends();
}
