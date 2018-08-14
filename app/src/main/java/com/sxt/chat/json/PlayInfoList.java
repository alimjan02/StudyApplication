package com.sxt.chat.json;

import java.util.List;

public class PlayInfoList {
    /**
     *  "PlayInfoList":{
     "PlayInfo":Array[3]
     }
     */
    private List<PlayInfo> PlayInfo;

    public List<com.sxt.chat.json.PlayInfo> getPlayInfo() {
        return PlayInfo;
    }

    public void setPlayInfo(List<com.sxt.chat.json.PlayInfo> playInfo) {
        PlayInfo = playInfo;
    }
}