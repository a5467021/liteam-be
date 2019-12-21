package com.liteam.service;

import com.liteam.entity.Group;

import java.util.List;

public interface GroupService {

    /**
     * 增加分组
     * @param groupName
     * @return
     */
    Group addGroup(int number, String groupName);

    /**
     * 删除分组
     * @param groupId
     */
    String removeGroup(int number, int groupId);

    /**
     * 改变分组的名字
     * @param groupId
     * @param group_name
     * @return
     */
    Group modifyGroupName(int groupId, String group_name);

    /**
     * 返回该用户所有的分组
     * @param number
     * @return
     */
    List<Group> getUserGroups(int number);

    /**
     * 获取默认分组 我的好友
     * @param number
     * @return
     */
    Group getDefaultGroup(int number);

    /**
     * 根据groupId获取分组
     * @param groupId
     * @return
     */
    Group getOne(int groupId);
}
