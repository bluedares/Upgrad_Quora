package com.upgrad.quora.service.entity;

import com.upgrad.quora.service.common.UserRole;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@Entity
@Table(name = "users")
@NamedQueries(
        {
                @NamedQuery(name = "userByUuid", query = "select u from UserEntity u where u.uuid = :uuid"),
                @NamedQuery(name = "userByEmail", query = "select u from UserEntity u where u.email =:email")
        }
)
public class UserEntity implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "uuid")
    @Size(max = 200)
    private String uuid;

    @Column(name = "firstname")
    @NotNull
    @Size(max = 50)
    private String firstName;

    @Column(name = "lastname")
    @NotNull
    @Size(max = 50)
    private String lastName;

    @Column(name = "username")
    @NotNull
    @Size(max = 50)
    private String userName;

    @Column(name = "email")
    @NotNull
    @Size(max = 100)
    private String email;

    @Column(name = "password")
    @NotNull
    @Size(max = 30)
    private String password;

    @Column(name = "salt")
    @NotNull
    @Size(max = 100)
    private String salt;

    @Column(name = "country")
    @Size(max = 30)
    private String country;

    @Column(name = "aboutme")
    @Size(max = 200)
    private String aboutMe;

    @Column(name = "dob")
    @Size(max = 50)
    private String dob;

    @Column(name = "role")
    @Size(max = 30)
    private UserRole role;

    @Column(name = "contactnumber")
    @Size(max = 30)
    private String contactNumber;

}
