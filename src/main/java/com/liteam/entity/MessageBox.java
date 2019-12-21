package com.liteam.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "messageBox")
@Data
public class MessageBox implements Serializable {

    @Id
    @Column(unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int messageBoxId;

    private int userNumber;

    private int sendNumber;

    private int receiveNumber;

    private String nickname;

    private String headUrl;

    private String sex;

    private Date birthday;

    private String content;

    private int privateChatId;

    private Timestamp time;

    private int type;

    private int isDeal;

    private int isRead;
}
