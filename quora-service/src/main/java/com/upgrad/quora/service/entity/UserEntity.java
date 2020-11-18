package com.upgrad.quora.service.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Table(name = "users")
/*@NamedQueries(
        {
                @NamedQuery(name = "userByUuid", query = "select u from UserEntity u where u.uuid = :uuid"),
                @NamedQuery(name = "userByEmail", query = "select u from UserEntity u where u.email =:email")
        }
)*/
public class UserEntity implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "uuid")
    @Size(max = 200)
    private String uuid;

    @Column(name = "firstName")
    @NotNull
    @Size(max = 30)
    private String firstName;

    @Column(name = "lastName")
    @NotNull
    @Size(max = 30)
    private String lastName;

    @Column(name = "userName")
    @NotNull
    @Size(max = 30)
    private String userName;
}
