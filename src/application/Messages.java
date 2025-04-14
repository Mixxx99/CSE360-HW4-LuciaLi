package application;


public class Messages {

		private int messageID;
		private int questionID;
		private String userName;
	    private String messagesContent;
	    
	    
	    public Messages(int messageID, int questionID, String userName, String messagesContent) {
	    	this.messageID = messageID;
	    	this.questionID = questionID;
	    	this.userName = userName;
	        this.messagesContent = messagesContent;
	    }
	    
	    // Constructor
	    public Messages(int questionId, String userName, String messagesContent) {
	    	this.messageID = -1;
	    	this.questionID = questionId;
	    	this.userName = userName;
	        this.messagesContent = messagesContent;
	    }
	    

	    public int getQuestionId() { return questionID; }
	    public String getUserName() { return userName == null ? "unknown" : userName; }
	    public int getMessageId() {return messageID; }
	    public void setMessageId(int messageID) { this.messageID = messageID; }
	    public String getMessages() { return messagesContent; }
	    //public void setMessages(String messagesContent) { this.messagesContent = messagesContent; }
	    
	}