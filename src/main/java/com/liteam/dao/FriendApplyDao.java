package com.liteam.dao;

import com.liteam.entity.FriendApply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendApplyDao extends JpaRepository<FriendApply, Integer> {
    FriendApply findBySendNumberAndReceiveNumber(int sendNumber, int receiveNumber);
}
