package eu.emrex.model;

public class VerificationRequest {

    private String SessionId;

    private String givenNames;

    private String familyName;

    private String birthDate;

    private String gender;

    private String pubKey;

    private String data;


    public String getSessionId() {
        return SessionId;
    }


    public void setSessionId(String sessionId) {
        SessionId = sessionId;
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


    public String getGender() {
        return gender;
    }


    public void setGender(String gender) {
        this.gender = gender;
    }


    public String getData() {
        return data;
    }


    public void setData(String data) {
        this.data = data;
    }


    public String getPubKey() {
        return pubKey;
    }


    public void setPubKey(String pubKey) {
        this.pubKey = pubKey;
    }

}
