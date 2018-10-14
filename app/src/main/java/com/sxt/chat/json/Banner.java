package com.sxt.chat.json;

import java.io.Serializable;

/**
 * Created by 11837 on 2018/5/21.
 */

public class Banner implements Serializable {
    private static final long serialVersionUID = -1589804003600796026L;
    private String objectId;
    private String createdAt;
    private String updatedAt;
    private String url;
    private String description;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
