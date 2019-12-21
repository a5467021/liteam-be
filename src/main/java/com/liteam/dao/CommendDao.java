package com.liteam.dao;

import com.liteam.entity.Commend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommendDao extends JpaRepository<Commend, Integer> {
    Commend findByCommendId(int commendId);
    List<Commend> findAllByUserNumber(int userNumber);
}
