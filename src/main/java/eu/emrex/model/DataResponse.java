package eu.emrex.model;

import java.util.Date;

public class DataResponse {

    private String sessionId;
    private String certificate;
    private String encKey;
    private String data;
    private Date timestamp;


    public DataResponse(String sessionId, String certificate, String encKey, String data, Date timestamp) {
        super();
        this.sessionId = sessionId;
        this.setCertificate(certificate);
        this.encKey = encKey;
        this.data = data;
        this.timestamp = timestamp;
    }


    public String getSessionId() {
        return sessionId;
    }


    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }


    public String getEncKey() {
        return encKey;
    }


    public void setEncKey(String encKey) {
        this.encKey = encKey;
    }


    public String getData() {
        return data;
    }


    public void setData(String data) {
        this.data = data;
    }


    public Date getTimestamp() {
        return timestamp;
    }


    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }


    public String getCertificate() {
        return certificate;
    }


    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }
}
