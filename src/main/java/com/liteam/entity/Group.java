package com.liteam.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "gro")
@Data
public class Group implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int groupId;

    private int userNumber;

    private String groupName;
}
