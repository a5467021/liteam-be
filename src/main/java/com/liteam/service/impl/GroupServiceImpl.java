package com.liteam.service.impl;

import com.liteam.dao.GroupDao;
import com.liteam.dao.UserGroupDao;
import com.liteam.entity.Friend;
import com.liteam.entity.Group;
import com.liteam.entity.User_Group;
import com.liteam.service.FriendService;
import com.liteam.service.GroupService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class GroupServiceImpl implements GroupService {

    @Resource
    private GroupDao groupDao;

    @Resource
    private UserGroupDao userGroupDao;

    @Resource
    private FriendService friendService;

    @Override
    public Group addGroup(int number, String groupName){
        Group group = new Group();
        group.setGroupName(groupName);
        group.setUserNumber(number);
        groupDao.save(group);
        User_Group user_group = new User_Group();
        user_group.setUserID(number);
        user_group.setGroupID(group.getGroupId());
        userGroupDao.save(user_group);
        return group;
    }

    @Override
    public String removeGroup(int number, int groupId){
        User_Group user_group = userGroupDao.findByGroupID(groupId);
        userGroupDao.delete(user_group);
        Group group = groupDao.findByGroupId(groupId);
        Group defaultGroup = getDefaultGroup(number);
        if (groupId == defaultGroup.getGroupId()){
            return null;
        }
        List<Friend> friends = friendService.findAllByGroupId(groupId);
        for (Friend friend:friends){
            friendService.modifyGroup(friend.getUserNumber(), friend.getFriendNumber(), defaultGroup.getGroupId());
        }
        groupDao.delete(group);
        return "成功";
    }

    @Override
    public Group modifyGroupName(int groupId, String group_name){
        Group group = groupDao.findByGroupId(groupId);
        group.setGroupName(group_name);
        groupDao.save(group);
        return group;
    }

    @Override
    public List<Group> getUserGroups(int number){
        return groupDao.findAllByUserNumber(number);
    }

    @Override
    public Group getDefaultGroup(int number){
        Group group = groupDao.findByUserNumberAndGroupName(number, "我的好友");
        return group;
    }

    @Override
    public Group getOne(int groupId){
        return groupDao.findByGroupId(groupId);
    }


}
