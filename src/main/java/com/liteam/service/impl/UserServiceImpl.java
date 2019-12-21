package com.liteam.service.impl;

import com.liteam.service.FriendService;
import com.liteam.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.TextCodec;
import com.liteam.dao.UserDao;
import com.liteam.entity.Group;
import com.liteam.entity.User;
import com.liteam.service.GroupService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.mindrot.jbcrypt.BCrypt;

import javax.annotation.Resource;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserDao userDao;

    @Resource
    private GroupService groupService;

    @Resource
    private FriendService friendService;


    @Value("application.secret")
    private String secret;

    @Override
    public User register(String nickname, String password, String phone) {
        String salt = BCrypt.gensalt();
        String pwdHash = BCrypt.hashpw(password, salt);
        User user = new User();
        user.setNickname(nickname);
        user.setPhone(phone);
        user.setPassword(pwdHash);
        user.setHeadUrl("/avatar.png");
        user.setNativePlace("中国");
        user.setSignature("这个人很懒，他什么也没有留下");
        user.setSex("男");
        java.sql.Date date = new java.sql.Date(System.currentTimeMillis());
        user.setBirthday(date);
        userDao.save(user);
        groupService.addGroup(user.getNumber(), "我的好友");
        Group group = groupService.getDefaultGroup(user.getNumber());
        friendService.addFriendByNumber(user.getNumber(), group.getGroupId(), user.getNumber(), "我");
        return user;
    }

    @Override
    public String generateToken(User user) {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.HOUR, 12);
        Date exp = now.getTime();
        return Jwts.builder()
                .claim(
                        "number", user.getNumber()
                ).setIssuedAt(Calendar.getInstance().getTime())
                .setExpiration(exp)
                .signWith(SignatureAlgorithm.HS256, TextCodec.BASE64.encode(secret)).compact();
    }

    @Override
    public User verifyToken(String token) {
        try {
            Claims body = Jwts.parser()
                    .setSigningKey(TextCodec.BASE64.encode(secret))
                    .parseClaimsJws(token)
                    .getBody();
            Integer number = (Integer) body.get("number");
            return userDao.findByNumber(number);
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public User checkUser(int number, String password) {
        User user = userDao.findByNumber(number);
        if (user == null){
            return null;
        }
        if (BCrypt.checkpw(password, user.getPassword())) {
            return user;
        } else return null;
    }

    @Override
    public User modifyPhone(int number, String phone){
        User user = userDao.findByNumber(number);
        user.setPhone(phone);
        userDao.save(user);
        return user;
    }

    @Override
    public User modifyNickname(int number, String nickname){
        User user = userDao.findByNumber(number);
        user.setNickname(nickname);
        userDao.save(user);
        return user;
    }

    @Override
    public User modifyBasicInformation(int number, String native_place, String signature, String sex, java.sql.Date birthday){
        User user = userDao.findByNumber(number);
        user.setBirthday(birthday);
        user.setSignature(signature);
        user.setSex(sex);
        user.setNativePlace(native_place);
        userDao.save(user);
        return user;
    }

    @Override
    public User modifyHead(int number, String head_url){
        User user = userDao.findByNumber(number);
        user.setHeadUrl(head_url);
        userDao.save(user);
        return user;
    }

    @Override
    public User findOne(int number){
        return userDao.findByNumber(number);
    }

    @Override
    public List<User> findAllUsersByNickname(String nickname){
        return userDao.findAllByNickname(nickname);
    }

    @Override
    public List<User> findAllUsersByPhone(String phone){
        return userDao.findAllByPhone(phone);
    }

    @Override
    public int count(){
        return (int)userDao.count();
    }

    @Override
    public List<User> all(){
        return userDao.findAll();
    }
}
