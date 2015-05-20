package eu.emrex.model;

import java.util.ArrayList;
import java.util.List;

public class VerificationReply {

    private String sessionId;

    private int score;

    private boolean verified;

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

}
