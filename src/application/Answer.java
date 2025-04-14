package application;


public class Answer {
    private int answerId;
    private int questionId;
    private String userName;
    private String answerContent;

    public Answer(int answerId, int questionId, String userName, String answerContent) {
        this.answerId = answerId;
        this.questionId = questionId;
        this.userName = userName;
        this.answerContent = answerContent;
    }

    public Answer(int questionId, String userName, String answerContent) {
        this.answerId = -1; 
        this.questionId = questionId;
        this.userName = userName;
        this.answerContent = answerContent;
    }

    public int getAnswerId() { return answerId; }
    public int getQuestionId() { return questionId; }
    public String getUserName() { return userName == null ? "unknown" : userName; }
    public String getAnswerContent() { return answerContent; }

    public void setAnswerId(int answerId) { this.answerId = answerId; }
}
