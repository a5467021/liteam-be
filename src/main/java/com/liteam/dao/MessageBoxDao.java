package com.liteam.dao;

import com.liteam.entity.MessageBox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageBoxDao extends JpaRepository<MessageBox, Integer> {
    List<MessageBox> findAllByUserNumberAndReceiveNumberAndType(int userNumber, int receiveNumber, int type);
    List<MessageBox> findAllByUserNumberAndSendNumberAndType(int userNumber, int sendNumber, int type);
    List<MessageBox> findAllByUserNumber(int userNumber);
    List<MessageBox> findAllByUserNumberAndType(int userNumber, int type);
    MessageBox findByMessageBoxId(int messageBoxId);
}
