package aboutStaff;

import java.sql.Timestamp;

public class StaffMessage {
    private int messageId;
    private String sender;
    private String recipient;
    private String messageContent;
    private Timestamp sentTime;

    public StaffMessage(int messageId, String sender, String recipient, String messageContent, Timestamp sentTime) {
        this.messageId = messageId;
        this.sender = sender;
        this.recipient = recipient;
        this.messageContent = messageContent;
        this.sentTime = sentTime;
    }

    public int getMessageId() {
        return messageId;
    }

    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public Timestamp getSentTime() {
        return sentTime;
    }
}
