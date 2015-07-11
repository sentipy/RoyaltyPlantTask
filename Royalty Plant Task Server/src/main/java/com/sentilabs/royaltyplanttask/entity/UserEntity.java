package com.sentilabs.royaltyplanttask.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

/**
 * Created by sentipy on 04/07/15.
 */

@Entity
@Table(name = "clients")
public class UserEntity {

    @Id
    @SequenceGenerator(name = "clients_id_seq", sequenceName = "clients_id_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "clients_id_seq")
    private long id;

    @Column(name = "name")
    private String username;

    @Column(name = "password")
    private String password;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserEntity {" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
