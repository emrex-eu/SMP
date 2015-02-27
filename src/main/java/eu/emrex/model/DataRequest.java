package eu.emrex.model;

public class DataRequest {

    private String sessionId;
    private String encKey;
    private String returnUrl;


    public DataRequest(String sessionId, String encKey, String returnUrl) {
        super();
        this.sessionId = sessionId;
        this.encKey = encKey;
        this.returnUrl = returnUrl;
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


    public String getReturnUrl() {
        return returnUrl;
    }


    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

}
