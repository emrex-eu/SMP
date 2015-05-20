package eu.emrex.model;

public class VerificationReply {

    private String SessionId;

    private int score;

    private boolean verified;


    public String getSessionId() {
        return SessionId;
    }


    public void setSessionId(String sessionId) {
        SessionId = sessionId;
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

}
