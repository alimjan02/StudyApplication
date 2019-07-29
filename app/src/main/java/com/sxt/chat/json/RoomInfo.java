package com.sxt.chat.json;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import java.io.Serializable;

/**
 * Created by 11837 on 2018/5/21.
 */

public class RoomInfo extends BaseObservable implements Serializable {
    private static final long serialVersionUID = -1589804003600796026L;
    private Integer id;
    private String objectId;
    private String room_url;
    private Integer room_size;
    private Double price;
    private String address;
    private String createdAt;
    private String updatedAt;
    private String home_name;

    @Bindable
    public String getHome_name() {
        return home_name;
    }

    public void setHome_name(String home_name) {
        this.home_name = home_name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getRoom_url() {
        return room_url;
    }

    public void setRoom_url(String room_url) {
        this.room_url = room_url;
    }

    public Integer getRoom_size() {
        return room_size;
    }

    public void setRoom_size(Integer room_size) {
        this.room_size = room_size;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
