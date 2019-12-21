package com.liteam.dao;

import com.liteam.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatDao extends JpaRepository<Chat, Integer> {
    List<Chat> findAllByUserNumber(int userNumber);
    List<Chat> findAllByUserNumberAndReceiveNumber(int userNumber, int receiveNumber);
    List<Chat> findAllByUserNumberAndSendNumber(int userNumber, int sendNumber);
}
