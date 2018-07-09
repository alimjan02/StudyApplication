package com.sxt.chat.db;

import cn.bmob.v3.BmobUser;

/**
 * Created by SXT on 2018/3/22.
 */

public class User extends BmobUser {

    public User() {
        this.setTableName("_User");
    }

    /**
     * String sql = "create table " + tableName + "(_id integer primary key autoincrement," +
     * "id integer,userName varchar(64),age integer,gender varchar(2),phone varchar(64)," +
     * "height float,weight float," + "idCard varchar(64),ticket varchar(64),accountId integer)";
     */

    private Integer id;
    private String name;
    private String userName;
    private String userPwd;
    private Integer age;
    private String gender;
    private String imgUri;
    private String phone;
    private Float height;
    private Float weight;
    private String idCard;
    private String education;
    private String ticket;
    private Integer accountId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPwd() {
        return userPwd;
    }

    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getImgUri() {
        return imgUri;
    }

    public void setImgUri(String imgUri) {
        this.imgUri = imgUri;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Float getHeight() {
        return height;
    }

    public void setHeight(Float height) {
        this.height = height;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }
}
