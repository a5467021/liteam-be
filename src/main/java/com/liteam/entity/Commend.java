package com.liteam.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "commend")
@Data
public class Commend implements Serializable{

    @Id
    @Column(unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int commendId;

    private int userNumber;

    private int friendNumber;

    private String commend;

    private String headUrl;
}
