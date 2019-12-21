package com.liteam.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "user_group")
@Data
public class User_Group implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int userGroupId;

    private int userID;

    private int groupID;
}
