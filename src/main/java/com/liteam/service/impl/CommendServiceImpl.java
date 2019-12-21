package com.liteam.service.impl;

import com.liteam.dao.CommendDao;
import com.liteam.entity.Commend;
import com.liteam.service.CommendService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class CommendServiceImpl implements CommendService {

    @Resource
    private CommendDao commendDao;

    @Override
    public Commend addCommend(int userNumber, int friendNumber, String commend, String headUrl){
        Commend commendEntity = new Commend();
        commendEntity.setCommend(commend);
        commendEntity.setUserNumber(userNumber);
        commendEntity.setFriendNumber(friendNumber);
        commendEntity.setHeadUrl(headUrl);
        commendDao.save(commendEntity);
        return commendEntity;
    }

    @Override
    public void deleteCommend(int commendId){
        Commend commend = commendDao.findByCommendId(commendId);
        commendDao.delete(commend);
    }

    @Override
    public List<Commend> getCommends(int number){
        return commendDao.findAllByUserNumber(number);
    }

    @Override
    public List<Commend> allCommends(){
        return commendDao.findAll();
    }
}
