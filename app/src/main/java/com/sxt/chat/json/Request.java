package com.sxt.chat.json;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Request implements Serializable {

    private static final long serialVersionUID = 1L;

    private int domainId;
    private int seniorId;
    private long startTime;
    private long endTime;
    private String locale;
    private List<String> ids;
    private Map<String, String> parameters;
    private int areaId;
    private int cityId;
    private int provinceId;
    private int userId;

    public Request() {
    }

    public void setDomainId(int domainId) {
        this.domainId = domainId;
    }

    public boolean hasDomainId() {
        return domainId > 0;
    }

    public int getDomainId() {
        if (domainId <= 0) {
            throw new IllegalArgumentException("parameter domainId is required.");
        }
        return domainId;
    }

    public void setSeniorId(int seniorId) {
        this.seniorId = seniorId;
    }

    public boolean hasSeniorId() {
        return seniorId > 0;
    }

    public int getSeniorId() {
        if (seniorId <= 0) {
            throw new IllegalArgumentException("parameter seniorId is required.");
        }
        return seniorId;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getEndTime() {
        if (endTime == 0) {
            return System.currentTimeMillis() + 3650L * 24 * 3600 * 1000;
        }
        return endTime;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getLocale() {
        if (locale == null || locale.isEmpty()) {
            return "en_US";
        }
        return locale;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    public List<String> getIds() {
        return ids;
    }

    public void addId(String id) {
        if (ids == null) {
            ids = new ArrayList<String>();
        }
        ids.add(id);
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public String getParameter(String param, boolean required) {
        String value = null;
        if (parameters != null) {
            value = parameters.get(param);
        }
        if (value == null && required) {
            throw new IllegalArgumentException("Missing required parameter: " + param);
        }
        return value;
    }

    public int getIntParameter(String param) {
        try {
            return Integer.parseInt(getParameter(param, true));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid parameter value: " + param, e);
        }
    }

    public int getIntParameter(String param, int defValue) {
        try {
            String value = getParameter(param, false);
            if (value == null || value.isEmpty()) {
                return defValue;
            }
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defValue;
        }
    }

    public long getLongParameter(String param) {
        try {
            return Long.parseLong(getParameter(param, true));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid parameter value: " + param, e);
        }
    }

    public long getLongParameter(String param, long defValue) {
        try {
            String value = getParameter(param, false);
            if (value == null || value.isEmpty()) {
                return defValue;
            }
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return defValue;
        }
    }

    public void setParameter(String param, Object value) {
        if (param != null && value != null) {
            if (parameters == null) {
                parameters = new HashMap<String, String>();
            }
            parameters.put(param, value.toString());
        }
    }

    public int getAreaId() {
        return areaId;
    }

    public void setAreaId(int areaId) {
        this.areaId = areaId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
