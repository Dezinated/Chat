package com.jason.chat;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jason on 1/6/2017.
 */

public class RoomsList {
    public Map<String, Room> rooms = new HashMap<>();

    public Map<String, Room> getRooms() {
        return rooms;
    }

    public void setRooms(Map<String, Room> rooms) {
        this.rooms = rooms;
    }

    public RoomsList(){

    }

    public RoomsList(Map<String, Room> rooms){
        this.rooms = rooms;
    }
}
