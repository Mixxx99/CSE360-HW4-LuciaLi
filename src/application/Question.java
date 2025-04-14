package application;


public class Question {
	
		private int questionId;
		private String userName;
	    private String title;
	    private String content;
	    private String tag;
	    private String tagger;
	    
	    

	    // Constructor to initialize a new User object with userName, password, and role.
	    public Question(String userName, String title, String content) {
	    	this.questionId = -1;
	    	this.userName = userName;
	    	this.title = title;
	        this.content = content;
	    }
	    
	    
	    public Question(int questionId, String userName, String title, String content, String tag, String tagger) {
	    	this.questionId = questionId;
	    	this.userName = userName;
	    	this.title = title;
	        this.content = content;
	        this.tag = tag;
	        this.tagger = tagger;
	    }
	    
	    

	    public int getQuestionId() { return questionId; }
	    public void setQuestionId(int questionId) { this.questionId = questionId; }
	    public String getUserName() { return userName == null ? "unknown" : userName; }
	    public String getTitle() { return title; }
	    public String getContent() { return content; }
	    public String getTag() { return tag; }
	    public void setTag(String tag) {this.tag = tag;}
	    
	    public String getTagger() { return tagger; }
	    public void setTagger(String tag) {this.tagger = tagger;}
	    
	    
	}
