package com.sxt.chat.json;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Descripition
 * @Auther liubing
 * @CreateTime 2017/7/13
 * @Version
 * @Since
 */
public class ResponseInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int OK = 0;
    public static final int ERROR = 1;
    public static final int CANCELED = 2;

    private List<Map<String, Object>> resultData;
    private final int code;
    private String error;
    private long csn;
    private String ticket;
    private int accountId;
    private int onDuty;
    private String cameraUrl;
    private String workerPayment;
    private Map<String, String> extraMap;
    private double ratioOfPackages;
    private String payResult;
    private List<Date> dates;
    private boolean isOldClient;
    private int appraisalPayment;
    private int count;
    private String cmd;
    private String videoLink;
    private int workerId;
    private int instrudeAlarmStatus;
    private int unusualAlarmLevel;
    private String trainCode;
    private String userName;
    private int domainId;
    private int version;

    public ResponseInfo(int code) {
        this.code = code;
        this.cmd = "";
    }

    public List<Map<String, Object>> getResultData() {
        return resultData;
    }

    public void setResultData(List<Map<String, Object>> resultData) {
        this.resultData = resultData;
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

    public int getOnDuty() {
        return onDuty;
    }

    public void setOnDuty(int onDuty) {
        this.onDuty = onDuty;
    }

    public String getCameraUrl() {
        return cameraUrl;
    }

    public void setCameraUrl(String cameraUrl) {
        this.cameraUrl = cameraUrl;
    }

    public String getWorkerPayment() {
        return workerPayment;
    }

    public void setWorkerPayment(String workerPayment) {
        this.workerPayment = workerPayment;
    }

    public Map<String, String> getExtraMap() {
        return extraMap;
    }

    public void setExtraMap(Map<String, String> extraMap) {
        this.extraMap = extraMap;
    }

    public double getRatioOfPackages() {
        return ratioOfPackages;
    }

    public void setRatioOfPackages(double ratioOfPackages) {
        this.ratioOfPackages = ratioOfPackages;
    }

    public String getPayResult() {
        return payResult;
    }

    public void setPayResult(String payResult) {
        this.payResult = payResult;
    }

    public List<Date> getDates() {
        return dates;
    }

    public void setDates(List<Date> dates) {
        this.dates = dates;
    }

    public boolean isOldClient() {
        return isOldClient;
    }

    public void setOldClient(boolean oldClient) {
        isOldClient = oldClient;
    }

    public int getAppraisalPayment() {
        return appraisalPayment;
    }

    public void setAppraisalPayment(int appraisalPayment) {
        this.appraisalPayment = appraisalPayment;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getVideoLink() {
        return videoLink;
    }

    public void setVideoLink(String videoLink) {
        this.videoLink = videoLink;
    }

    public int getWorkerId() {
        return workerId;
    }

    public void setWorkerId(int workerId) {
        this.workerId = workerId;
    }

    public int getInstrudeAlarmStatus() {
        return instrudeAlarmStatus;
    }

    public void setInstrudeAlarmStatus(int instrudeAlarmStatus) {
        this.instrudeAlarmStatus = instrudeAlarmStatus;
    }

    public int getUnusualAlarmLevel() {
        return unusualAlarmLevel;
    }

    public void setUnusualAlarmLevel(int unusualAlarmLevel) {
        this.unusualAlarmLevel = unusualAlarmLevel;
    }

    public String getTrainCode() {
        return trainCode;
    }

    public void setTrainCode(String trainCode) {
        this.trainCode = trainCode;
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
}
