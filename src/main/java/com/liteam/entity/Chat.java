package com.liteam.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import java.sql.Timestamp;

@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "chat")
@Data
public class Chat {
    @Id
    @Column(unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int chatId;

    private int userNumber;

    private int sendNumber;

    private int receiveNumber;

    private String message;

    private String pictureUrl;

    private Timestamp time;
}
