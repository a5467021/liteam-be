package com.liteam.entity;


import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;

public class UserFriend {
    private String nickname;

    private String phone;

    private int messageBoxId;

    private MultipartFile headUrl;

    private String nativePlace;

    private String signature;

    private String sex;

    private String birthday;

    private int sendNumber;

    private int receiveNumber;

    private int groupId;

    private String remark;

    private int isDeal;

    private String commend;

    private int friendNumber;

    private int type;

    public int getFriendNumber() {
        return friendNumber;
    }

    public void setFriendNumber(int friendNumber) {
        this.friendNumber = friendNumber;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCommend() {
        return commend;
    }

    public void setCommend(String commend) {
        this.commend = commend;
    }

    public MultipartFile getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(MultipartFile headUrl) {
        this.headUrl = headUrl;
    }

    public String getNativePlace() {
        return nativePlace;
    }

    public void setNativePlace(String nativePlace) {
        this.nativePlace = nativePlace;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public int getSendNumber() {
        return sendNumber;
    }

    public void setSendNumber(int sendNumber) {
        this.sendNumber = sendNumber;
    }

    public int getReceiveNumber() {
        return receiveNumber;
    }

    public void setReceiveNumber(int receiveNumber) {
        this.receiveNumber = receiveNumber;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getIsDeal() {
        return isDeal;
    }

    public void setIsDeal(int isDeal) {
        this.isDeal = isDeal;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getMessageBoxId() {
        return messageBoxId;
    }

    public void setMessageBoxId(int messageBoxId) {
        this.messageBoxId = messageBoxId;
    }
}
