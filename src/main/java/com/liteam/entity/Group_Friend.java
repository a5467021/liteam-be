package com.liteam.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "group_friend")
@Data
public class Group_Friend implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int groupFriendId;

    private int groupID;

    private int friendID;
}
