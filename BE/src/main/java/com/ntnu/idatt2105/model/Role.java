package com.ntnu.idatt2105.model;

import java.util.ArrayList;
import java.util.Set;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="role")
public class Role{
    @Id
    @Column(name = "id")
    private long id;

    @Column(name="name")
    private String name;
    
    @JsonIgnore
    @OneToMany(mappedBy = "role", cascade = CascadeType.REFRESH)
    private Set<User> users;

    public Role(){}

    public Role(long id, String name){
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Set<User> getUsers() {
        return users;
    }
    public void setUsers(Set<User> users) {
        this.users = users;
    }
}
