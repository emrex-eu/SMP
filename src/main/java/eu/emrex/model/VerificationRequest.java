package eu.emrex.model;

public class VerificationRequest {

    private String sessionId;

    private String givenNames;

    private String familyName;

    private String birthDate;

    private String pubKey;

    private String data64;


    public String getSessionId() {
        return sessionId;
    }


    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }


    public String getGivenNames() {
        return givenNames;
    }


    public void setGivenNames(String givenNames) {
        this.givenNames = givenNames;
    }


    public String getFamilyName() {
        return familyName;
    }


    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }


    public String getBirthDate() {
        return birthDate;
    }


    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }


    public String getData64() {
        return data64;
    }


    public void setData64(String data64) {
        this.data64 = data64;
    }


    public String getPubKey() {
        return pubKey;
    }


    public void setPubKey(String pubKey) {
        this.pubKey = pubKey;
    }

}
