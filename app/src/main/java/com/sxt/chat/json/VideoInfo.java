package com.sxt.chat.json;

import java.io.Serializable;

public class VideoInfo implements Serializable {
    private static final long serialVersionUID = -1589804003600796026L;
    private Integer id;
    private String objectId;
    private String createdAt;
    private String updatedAt;

    private String imageUrl;
    private String videoUrl;
    private String title;
    private Integer star;
    private Integer type;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getStar() {
        return star;
    }

    public void setStar(Integer star) {
        this.star = star;
    }
}
