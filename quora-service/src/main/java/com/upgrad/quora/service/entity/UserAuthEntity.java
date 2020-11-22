package com.upgrad.quora.service.entity;


import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;

@Data
@Entity
@Table(name = "user_auth")
@NamedQueries({
        @NamedQuery(name = "userAuthTokenByAccessToken", query = "select ut from UserAuthEntity ut where ut.accessToken = :accessToken "),
        @NamedQuery(name="userAuthTokenByUuid",query="select ut from UserAuthEntity ut where ut.uuid = :uuid")
})
public class UserAuthEntity {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @Column(name = "UUID")
    @Size(max = 200)
    private String uuid;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "USER_ID")
    private UserEntity user;

    @Column(name = "ACCESS_TOKEN")
    @NotNull
    @Size(max = 200)
    private String accessToken;

    @Column(name = "LOGIN_AT")
    @NotNull
    private ZonedDateTime loginAt;

    @Column(name = "EXPIRES_AT")
    @NotNull
    private ZonedDateTime expiresAt;

    @Column(name = "LOGOUT_AT")
    private ZonedDateTime logoutAt;
}
