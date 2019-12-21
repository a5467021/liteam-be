package com.liteam.dao;

import com.liteam.entity.User_Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserGroupDao extends JpaRepository<User_Group, Integer> {
    User_Group findByGroupID(int groupId);
}
