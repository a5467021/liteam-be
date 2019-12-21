package com.liteam.dao;

import com.liteam.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupDao extends JpaRepository<Group, Integer> {
    List<Group> findAllByUserNumber(int userNumber);
    Group findByGroupId(int groupId);
    Group findByUserNumberAndGroupName(int userNumber, String groupName);
}
