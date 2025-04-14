package application;


public class Reply {

		private int replyID;
		private int questionID;
		private String userName;
	    private String replyContent;
	    
	    
	    public Reply(int replyID, int questionID, String userName, String replyContent) {
	    	this.replyID = replyID;
	    	this.questionID = questionID;
	    	this.userName = userName;
	        this.replyContent = replyContent;
	    }
	    
	    // Constructor
	    public Reply(int questionId, String userName, String replyContent) {
	    	this.replyID = -1;
	    	this.questionID = questionId;
	    	this.userName = userName;
	        this.replyContent = replyContent;
	    }
	    

	    public int getQuestionId() { return questionID; }
	    public String getUserName() { return userName == null ? "unknown" : userName; }
	    public int getReplyId() {return replyID; }
	    public void setreplyId(int replyID) { this.replyID = replyID; }
	    public String getReply() { return replyContent; }
	    
	}
