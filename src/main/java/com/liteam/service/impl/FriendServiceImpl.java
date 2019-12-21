package com.liteam.service.impl;

import com.liteam.dao.FriendApplyDao;
import com.liteam.dao.FriendDao;
import com.liteam.dao.GroupFriendDao;
import com.liteam.entity.Friend;
import com.liteam.entity.FriendApply;
import com.liteam.entity.Group_Friend;
import com.liteam.entity.*;
import com.liteam.service.FriendService;
import com.liteam.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class FriendServiceImpl implements FriendService {

    @Resource
    private FriendDao friendDao;

    @Resource
    private UserService userService;

    @Resource
    private GroupFriendDao groupFriendDao;

    @Resource
    private FriendApplyDao friendApplyDao;

    @Override
    public Friend queryByNumber(int friendNumber, int userNumber){
        return friendDao.findByFriendNumberAndUserNumber(friendNumber, userNumber);
    }

    @Override
    public List<Friend> queryByRemark(int userNumber, String remark){
        return friendDao.findAllByUserNumberAndRemark(userNumber, remark);
    }

    @Override
    public Friend addFriendByNumber(int user_number, int groupId, int friend_number, String remark){
        Friend friend = new Friend();
        friend.setFriendNumber(friend_number);
        friend.setUserNumber(user_number);
        friend.setGroupId(groupId);
        friend.setRemark(remark);
        friendDao.save(friend);
        Group_Friend group_friend = new Group_Friend();
        group_friend.setGroupID(groupId);
        group_friend.setFriendID(friend.getFriendId());
        groupFriendDao.save(group_friend);
        return friend;
    }

    @Override
    public List<Friend> findAllByGroupId(int groupId){
        List<Friend> friends = new ArrayList<>();
        List<Group_Friend> group_friends = groupFriendDao.findAllByGroupID(groupId);
        for (Group_Friend group_friend:group_friends){
            friends.add(friendDao.getOne(group_friend.getFriendID()));
        }
        return friends;
    }

    @Override
    public List<Friend> findAllFriends(int userNumber){
        return friendDao.findAllByUserNumber(userNumber);
    }

    @Override
    public void removeFriend(int userNumber, int friendNumber){
        Friend friend = friendDao.findByFriendNumberAndUserNumber(friendNumber, userNumber);
        Group_Friend group_friend = groupFriendDao.findByFriendID(friend.getFriendId());
        groupFriendDao.delete(group_friend);
        friendDao.delete(friend);
    }

    @Override
    public void modifyGroup(int userNumber, int friendNumber, int groupId){
        Friend friend = friendDao.findByFriendNumberAndUserNumber(friendNumber, userNumber);
        Group_Friend group_friend = groupFriendDao.findByFriendID(friend.getFriendId());
        group_friend.setGroupID(groupId);
        groupFriendDao.save(group_friend);
        friend.setGroupId(groupId);
        friendDao.save(friend);
    }

    @Override
    public void modifyRemark(int userNumber, int friendNumber, String remark){
        Friend friend = friendDao.findByFriendNumberAndUserNumber(friendNumber, userNumber);
        friend.setRemark(remark);
        friendDao.save(friend);
    }

    @Override
    public Friend findOneFriend(int userNumber, int friendNumber){
        return friendDao.findByFriendNumberAndUserNumber(friendNumber, userNumber);
    }

    @Override
    public FriendApply applyFriend(int sendNumber, int receiveNumber, int groupId, String remark){
        FriendApply friendApply = new FriendApply();
        friendApply.setSendNumber(sendNumber);
        friendApply.setReceiveNumber(receiveNumber);
        friendApply.setGroupId(groupId);
        friendApply.setRemark(remark);
        friendApplyDao.save(friendApply);
        return friendApply;
    }

    @Override
    public void deleteFriendApply(int sendNumber, int receiveNumber){
        FriendApply friendApply = friendApplyDao.findBySendNumberAndReceiveNumber(sendNumber, receiveNumber);
        friendApplyDao.delete(friendApply);
    }

    @Override
    public FriendApply getFriendApply(int sendNumber, int receiveNumber){
        return friendApplyDao.findBySendNumberAndReceiveNumber(sendNumber, receiveNumber);
    }
}
