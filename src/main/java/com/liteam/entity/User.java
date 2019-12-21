package com.liteam.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.hibernate.annotations.Proxy;
import org.hibernate.validator.constraints.Length;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.security.Principal;
import java.sql.Date;

@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "user")
@Data
public class User implements Serializable, Principal {
    @Id
    @Column(unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_JDPS_content")
    @SequenceGenerator(name = "SEQ_JDPS_content", sequenceName = "SEQ_JDPS_CONTENT", initialValue = 100000, allocationSize = 1)
    private int number;

    @NotNull(message = "昵称不能为空")
    @Length(max = 16, message = "昵称过长")
    private String nickname;

    @NotNull(message = "密码不能为空")
    private String password;

    @NotNull(message = "手机号不能为空")
    @Pattern(regexp = "^(1[34578]\\d{9})$", message = "手机号格式错误")
    private String phone;

    private String headUrl;

    private String nativePlace;

    private String signature;

    private String sex;

    private Date birthday;

    @Override
    public String getName() {
        return String.valueOf(number);
    }
}
