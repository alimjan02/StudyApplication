package com.sxt.chat.json;

import com.sxt.chat.db.User;

import java.io.Serializable;
import java.util.List;

public class ResponseInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int OK = 0;
    public static final int ERROR = 1;
    public static final int CANCELED = 2;

    private final int code;
    private String error;
    private long csn;
    private String ticket;
    private int accountId;
    private String cmd;
    private int workerId;
    private String userName;
    private int domainId;
    private int version;
    private String RequestId;
    private VideoBase VideoBase;
    private PlayInfoList PlayInfoList;
    private User user;
    private String imgUrl;
    private List<RoomInfo> roomInfoList;
    private List<Banner> bannerInfoList;
    private List<LocationInfo> locationInfoList;

    public ResponseInfo(int code) {
        this.code = code;
        this.cmd = "";
    }

    public int getCode() {
        return code;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Banner> getBannerInfos() {
        return bannerInfoList;
    }

    public void setBannerInfos(List<Banner> bannerInfoList) {
        this.bannerInfoList = bannerInfoList;
    }

    public List<LocationInfo> getLocationInfoList() {
        return locationInfoList;
    }

    public void setLocationInfoList(List<LocationInfo> locationInfoList) {
        this.locationInfoList = locationInfoList;
    }

    public List<RoomInfo> getRoomInfoList() {
        return roomInfoList;
    }

    public void setRoomInfoList(List<RoomInfo> roomInfoList) {
        this.roomInfoList = roomInfoList;
    }

    public long getCsn() {
        return csn;
    }

    public void setCsn(long csn) {
        this.csn = csn;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public int getWorkerId() {
        return workerId;
    }

    public void setWorkerId(int workerId) {
        this.workerId = workerId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getDomainId() {
        return domainId;
    }

    public void setDomainId(int domainId) {
        this.domainId = domainId;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getRequestId() {
        return RequestId;
    }

    public void setRequestId(String requestId) {
        RequestId = requestId;
    }

    public com.sxt.chat.json.VideoBase getVideoBase() {
        return VideoBase;
    }

    public void setVideoBase(com.sxt.chat.json.VideoBase videoBase) {
        VideoBase = videoBase;
    }

    public com.sxt.chat.json.PlayInfoList getPlayInfoList() {
        return PlayInfoList;
    }

    public void setPlayInfoList(com.sxt.chat.json.PlayInfoList playInfoList) {
        PlayInfoList = playInfoList;
    }
}
