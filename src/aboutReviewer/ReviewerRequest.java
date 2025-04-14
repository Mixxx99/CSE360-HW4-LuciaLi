package aboutReviewer;

/**
 * A simple data storage device for student requests to become reviewers.
 */
public class ReviewerRequest {
	private String userName;
	private String content;
	
	/**
	 * Creates a reviewer request. The default constructor should not be used.
	 * @param userName the username of the user who sent the request.
	 * @param content the content of the request.
	 */
	public ReviewerRequest(String userName, String content) {
		this.userName = userName;
		this.content = content;
	}
	
	public String getUserName() { return userName; }
	public String getContent() { return content; }
}
