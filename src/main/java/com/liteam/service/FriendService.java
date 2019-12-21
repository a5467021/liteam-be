package com.liteam.service;

import com.liteam.entity.Friend;
import com.liteam.entity.FriendApply;

import java.util.List;

public interface FriendService {

    /**
     * 根据账号查找好友
     * @param userNumber
     * @return
     */
    Friend queryByNumber(int friendNumber, int userNumber);

    /**
     * 根据备注查找好友
     * @param userNumber
     * @param remark
     * @return
     */
    List<Friend> queryByRemark(int userNumber, String remark);

    /**
     * 根据账号添加好友
     * @param groupId
     * @param user_number
     * @param remark
     * @return
     */
    Friend addFriendByNumber(int user_number, int groupId, int friend_number, String remark);

    /**
     * 查询此分组的所有好友
     * @param groupId
     * @return
     */
    List<Friend> findAllByGroupId(int groupId);

    /**
     * 获取该用户的所有好友
     * @param userNumber
     * @return
     */
    List<Friend> findAllFriends(int userNumber);

    /**
     * 删除好友
     * @param friendNumber
     */
    void removeFriend(int userNumber, int friendNumber);

    /**
     * 移动好友的分组
     * @param friendNumber
     * @param groupId
     */
    void modifyGroup(int userNumber, int friendNumber, int groupId);

    /**
     * 修改好友的备注
     * @param friendNumber
     * @param remark
     */
    void modifyRemark(int userNumber, int friendNumber, String remark);

    /**
     * 获取单个好友
     * @param friendNumber
     * @return
     */
    Friend findOneFriend(int userNumber, int friendNumber);

    /**
     * 申请添加好友
     * @param sendNumber
     * @param receiveNumber
     * @param groupId
     * @param remark
     * @return
     */
    FriendApply applyFriend(int sendNumber, int receiveNumber, int groupId, String remark);

    /**
     * 删除添加好友信息
     * @param sendNumber
     * @param receiveNumber
     * @return
     */
    void deleteFriendApply(int sendNumber, int receiveNumber);

    /**
     * 获取好友添加信息
     * @param sendNumber
     * @param receiveNumber
     * @return
     */
    FriendApply getFriendApply(int sendNumber, int receiveNumber);
}
