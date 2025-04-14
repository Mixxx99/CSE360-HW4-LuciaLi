package application;

import java.sql.SQLException;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This page displays the student's information. It includes the ability to 
 * add a question, view all questions, or request to become a reviewer.
 */
public class StudentHomePage {

	 private final DatabaseHelper databaseHelper;
	 private final User user;
	 
	 public StudentHomePage(DatabaseHelper databaseHelper, User user) {
		 this.databaseHelper = databaseHelper;   
		 this.user = user;
	    }
	
    public void show(Stage primaryStage) {
    	VBox layout = new VBox(10);
	    layout.setStyle("-fx-alignment: center; -fx-padding: 10;");
	    
	    // Label to display Hello user
	    Label userLabel = new Label("Hello, Student!");
	    userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
	    
		  //Implement the logout button on user home page and returns to the main setup/login page & select user role
		Button logoutButton = new Button("Logout");
        	logoutButton.setOnAction(event -> {
            		LogOut logOut = new LogOut(databaseHelper, primaryStage);
            		logOut.logout(); 
        	});
        			
		
        Button addQuestion = new Button("Add Question");
	    
		addQuestion.setOnAction(event -> {
		   new insertQuestion(databaseHelper, user).show(primaryStage);
		   		System.out.println("Add Question");
		    });
	    
		Button viewallQuestions = new Button("View all Questions");
	    
		viewallQuestions.setOnAction(event -> {
		   new QuestionList(databaseHelper, user).show(primaryStage);
		        
		    });
		
		Button requestReviewer = new Button("Become Reviewer");
		requestReviewer.setOnAction(event -> {
			makeReviewerRequest();
		});
       
		
	    //layout.getChildren().addAll(userLabel, logoutButton, roleselectionButton);
	    layout.getChildren().addAll(userLabel, addQuestion, viewallQuestions, requestReviewer, logoutButton);
	    //layout.getChildren().addAll(addQuestion, viewallQuestions);
	    
	    Scene userScene = new Scene(layout, 800, 400);
	  
	    

	    // Set the scene to primary stage
	    primaryStage.setScene(userScene);
	    primaryStage.setTitle("Student Page");
    	
    }
    
    /**
     * Shows the user a popup to create their request to become a reviewer. They can
     * optionally add a message explaining why they are making the request.
     */
    private void makeReviewerRequest() {
        Stage popup = new Stage();
        popup.setTitle("Request to Become Reviewer");

        VBox popupLayout = new VBox(10);
        popupLayout.setStyle("-fx-padding: 10;");

        TextField contentInput = new TextField();
        contentInput.setPromptText("Add a short explanation of why you want reviewer permissions.");

        Button sendButton = new Button("Send Request");

        sendButton.setOnAction(e -> {
            String requestContent = contentInput.getText().trim();
            
            try {
                String sendResult = databaseHelper.addReviewerRequest(user.getUserName(), requestContent);
                popup.close();
                showRequestSendResult(sendResult);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            
        });

        popupLayout.getChildren().addAll(new Label("Reason for Request:"), contentInput, sendButton);
        Scene popupScene = new Scene(popupLayout, 500, 300);
        popup.setScene(popupScene);
        popup.show();
    }
    
    /**
     * Shows the user the result of their request. This is a message saying that the
     * request was sent, or a helpful error message.
     * @param resultMessage the result message to display. A null message is assumed to
     * represent a successfully sent request.
     */
    private void showRequestSendResult(String resultMessage) {
    	Alert alert = new Alert(AlertType.INFORMATION);
    	alert.setTitle("Send Request Result");
    	alert.setHeaderText((resultMessage == null) ? "Request Sent!" : "Oops!");
    	alert.setContentText((resultMessage == null) ?
    			"Your instructor(s) will process your request soon." :
    			resultMessage);
    	alert.showAndWait().ifPresent(rs -> {
    	    if (rs == ButtonType.OK) {
    	        alert.close();
    	    }
    	});
    }
}
