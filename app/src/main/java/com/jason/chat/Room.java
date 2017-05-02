package com.jason.chat;

/**
 * Created by Jason on 1/6/2017.
 */

public class Room {
    private Long Id;
    private String Users;

    public Room(){

    }

    public Room(Long Id, String users) {
        this.Id = Id;
        this.Users = users;
    }

    public Long getId() {
        return Id;
    }

    public String getUsers() {
        return Users;
    }

    public void setId(Long id) {
        Id = id;
    }

    public void setUsers(String users) {
        Users = users;
    }

    public String toString(){
        return "ID: " + getId() + " USERS: " + getUsers();
    }
}
