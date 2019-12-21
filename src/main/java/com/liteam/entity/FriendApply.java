package com.liteam.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "friendApply")
@Data
public class FriendApply implements Serializable{

    @Id
    @Column(unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int friendApplyId;

    private int sendNumber;

    private int receiveNumber;

    private int groupId;

    private String remark;
}
