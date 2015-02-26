package eu.emrex.model;

public class VerificationReply {

    private String SessionId;

    private int score;

    private String data;


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


    public String getData() {
        return data;
    }


    public void setData(String data) {
        this.data = data;
    }

}
