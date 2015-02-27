package eu.emrex.model;

public class DataRequestEncrypted {

    private String sessionId;
    private String encData;


    public DataRequestEncrypted(String sessionId, String encData) {
        super();
        this.sessionId = sessionId;
        this.encData = encData;
    }


    public String getSessionId() {
        return sessionId;
    }


    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }


    public String getEncData() {
        return encData;
    }


    public void setEncData(String encData) {
        this.encData = encData;
    }

}
