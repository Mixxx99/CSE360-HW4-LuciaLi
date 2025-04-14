package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import databasePart1.*;

import java.sql.SQLException;
import java.util.List;

public class PrivateMessages {
	
	private final DatabaseHelper databaseHelper;
	private final Question question;
	private final User user;
	

	public PrivateMessages(DatabaseHelper databaseHelper, Question question, User user) {
        this.databaseHelper = databaseHelper;
        this.question = question;
        this.user = user; 
        
    }
    
	 public void show(Stage primaryStage) throws SQLException {
		 //must get table and messages to print
		 databaseHelper.printTableSchema();
		 databaseHelper.printAllMessages();
		 
		 	
	        VBox layout = new VBox(10);
	        layout.setStyle("-fx-padding: 10;");

	        Label questionTitle = new Label("Messages related to Question: " + question.getTitle());
	        questionTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
	        Label questionContent = new Label(question.getContent());
	        

	        ListView<HBox> MessagesTable = new ListView<>();
	        displayMessages(MessagesTable, primaryStage);
	        
	        Label warningLabel = new Label();
	        warningLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

	        TextArea messageInput = new TextArea();
	        messageInput.setPromptText("Message to " + user.getUserName() + ": ");
	        messageInput.setPrefRowCount(6); 
	        messageInput.setWrapText(true);

	        Button submitButton = new Button("Send");
	        submitButton.setOnAction(e -> sendMessage(messageInput, warningLabel, MessagesTable, primaryStage));
	        
	       

	        Button backButton = new Button("Back");
	        backButton.setOnAction(e -> new QuestionList(databaseHelper, user).show(primaryStage));

	        layout.getChildren().addAll(questionTitle, questionContent, MessagesTable, messageInput, submitButton, warningLabel,  backButton);
	        primaryStage.setScene(new Scene(layout, 800, 400));
	        primaryStage.setTitle("Your Private Messages");
	        }
	    
	 
	   public void displayMessages(ListView<HBox> messagesList, Stage primaryStage) {
	        try {
	            List<Messages> messages = databaseHelper.getMessageForQuestion(question.getQuestionId());
	            messagesList.getItems().clear();
	            
	            if (messages.isEmpty()) {
	                Label noMessagesLabel = new Label("No messages yet.");
	                HBox noMessagesBox = new HBox(10, noMessagesLabel);
	                messagesList.getItems().add(noMessagesBox);
	                return;
	            }

	            for (Messages m : messages) {
	            	//String questionUser = question.getUserName();
	            	if (user.getUserName().equals(question.getUserName()) || user.isAdmin()) {
	                Label messageLabel = new Label(m.getUserName() + ": " + m.getMessages());
	                HBox messageItem = new HBox(10, messageLabel);

	                    Button editButton = new Button("Edit");
	                    messageItem.getChildren().addAll(editButton);
	                
	                messagesList.getItems().add(messageItem);
	            	 } else {
	            		 Label notCorrectUser = new Label("You cant view these messages.");
	            	 }
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	    public void sendMessage(TextArea messagesInput, Label warningLabel, ListView<HBox> messagesList, Stage primaryStage) {
	        String messageTextBox = messagesInput.getText().trim();
	        if (!messageTextBox.isEmpty()) {
	            try {
	                Messages newMessage = new Messages(question.getQuestionId(), user.getUserName(), messageTextBox);
	                databaseHelper.sendMessage(newMessage);
	                messagesInput.clear();
	                displayMessages(messagesList, primaryStage);
	                warningLabel.setText("");
	            } catch (SQLException ex) {
	                ex.printStackTrace();
	            }
	        } else { 
	        	
	        	warningLabel.setText("You cannot send a blank message");
	        }
	    }

	}
