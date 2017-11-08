package eu.emrex.model;

import java.util.ArrayList;
import java.util.List;

public class VerificationReply {

    private String sessionId;

    private String elmoGivenNames;

    private String elmoFamilyName;

    private int score;

    private boolean verified;

    private List<Integer> coursesImported;

    private List<Double> ectsImported;

    private List<String> institutions;

    private final List<String> messages;


    public VerificationReply() {
        messages = new ArrayList<String>();
    }


    public String getSessionId() {
        return sessionId;
    }


    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }


    public int getScore() {
        return score;
    }


    public void setScore(int score) {
        this.score = score;
    }


    public boolean isVerified() {
        return verified;
    }


    public void setVerified(boolean verified) {
        this.verified = verified;
    }


    public List<String> getMessages() {
        return messages;
    }


    public void addMessage(String msg) {
        messages.add(msg);
    }


    public List<String> getInstitutions() {
        return institutions;
    }


    public void setInstitutions(List<String> institutions) {
        this.institutions = institutions;
    }


    public List<Integer> getCoursesImported() {
        return coursesImported;
    }


    public void setCoursesImported(List<Integer> coursesImported) {
        this.coursesImported = coursesImported;
    }


    public List<Double> getEctsImported() {
        return ectsImported;
    }


    public void setEctsImported(List<Double> ectsImported) {
        this.ectsImported = ectsImported;
    }

    public String getElmoGivenNames() { return elmoGivenNames; }

    public void setElmoGivenNames(String elmoGivenNames) { this.elmoGivenNames = elmoGivenNames; }

    public String getElmoFamilyName() { return elmoFamilyName; }

    public void setElmoFamilyName(String elmoFamilyName) { this.elmoFamilyName = elmoFamilyName; }
}
