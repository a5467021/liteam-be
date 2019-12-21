package com.liteam.service;

import com.liteam.entity.User;

import java.sql.Date;
import java.util.List;

public interface UserService {

    /**
     * 用户注册
     * @param nickname
     * @param password
     * @param phone
     * @return
     */
    User register(String nickname, String password, String phone);

    /**
     * 生成token
     *
     * @param user
     * @return
     */
    String generateToken(User user);

    /**
     * 进行token验证
     * @param token
     * @return
     */
    User verifyToken(String token);

    /**
     * 进行密码验证
     *
     * @param number
     * @param password
     * @return
     */
    User checkUser(int number, String password);

    /**
     * 修改电话号码
     * @param number
     * @param phone
     * @return
     */
    User modifyPhone(int number, String phone);

    /**
     * 修改基础信息
     * @param number
     * @param nickname
     * @param native_place
     * @param resume
     * @param signature
     * @param sex
     * @param birthday
     * @param commend
     * @return
     */
    User modifyBasicInformation(int number, String native_place, String signature, String sex, Date birthday);

    /**
     * 修改头像
     * @param number
     * @param head_url
     * @return
     */
    User modifyHead(int number, String head_url);

    /**
     * 修改昵称
     * @param number
     * @param nickname
     * @return
     */
    User modifyNickname(int number, String nickname);

    /**
     * 根据账号查询某个用户
     * @param number
     * @return
     */
    User findOne(int number);

    /**
     * 通过昵称查找所有用户
     * @param nickname
     * @return
     */
    List<User> findAllUsersByNickname(String nickname);

    /**
     * 通过电话号查找所有用户
     * @param nickname
     * @return
     */
    List<User> findAllUsersByPhone(String phone);

    /**
     * 获取用户数
     * @return
     */
    int count();

    /**
     * 返回所有用户
     * @return
     */
    List<User> all();
}
