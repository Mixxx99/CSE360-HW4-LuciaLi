package aboutReviewer;


public class Review {

		private int reviewId;
		private int questionId;
		private String userName;
		private String reviewContent;

	    
	    
		public Review(int reviewId, int questionId, String userName, String reviewContent) {
	        this.reviewId = reviewId;
	        this.questionId = questionId;
	        this.userName = userName;
	        this.reviewContent = reviewContent;
	    }
	    
	    // Constructor
		public Review(int questionId, String userName, String reviewContent, int rating) {
	        this.reviewId = -1;
	        this.questionId = questionId;
	        this.userName = userName;
	        this.reviewContent = reviewContent;
	    }
	    
		public int getReviewId() {return reviewId;}
	    public void setReviewId(int reviewId) {this.reviewId = reviewId;}
	    
	    public int getQuestionId() {return questionId;}	    
	    public String getUserName() {return userName == null ? "unknown" : userName;}
	    
	    public String getReviewContent() {return reviewContent;}
	    public void setReviewContent(String reviewContent) {this.reviewContent = reviewContent;}

	}
