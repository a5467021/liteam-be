package com.liteam.dao;

import com.liteam.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendDao extends JpaRepository<Friend, Integer> {
    List<Friend> findAllByUserNumber(int number);
    Friend findByFriendNumberAndUserNumber(int friendNumber, int userNumber);
    List<Friend> findAllByUserNumberAndRemark(int userNumber, String remark);
}
