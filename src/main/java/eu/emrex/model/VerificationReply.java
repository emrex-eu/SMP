package eu.emrex.model;

import java.util.ArrayList;
import java.util.List;

public class VerificationReply {

    private String SessionId;

    private int score;

    private String data;

    private final List<String> messages;
    
    public VerificationReply() {
    	messages = new ArrayList<String>();
    }

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

	public List<String> getMessages() {
		return messages;
	}
    
    public void addMessage(String msg) {
    	messages.add(msg);
    }

}
