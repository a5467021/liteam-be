package com.liteam.dao;

import com.liteam.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserDao extends JpaRepository<User, Integer> {
    List<User> findAllByPhone(String phone);
    List<User> findAllByNickname(String nickname);
    User findByNumber(int number);
}
