package aboutStaff;

import aboutReviewer.ReviewList;
import aboutReviewer.ReviewerListPage;
import application.QuestionList;
import application.SetupLoginSelectionPage;
import application.User;
import databasePart1.DatabaseHelper;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * StaffPage class represents the user interface for the Staff user.
 * This page a  welcome message and couple functionality button for the Staff.
 */

public class StaffHomePage {
	
	private final DatabaseHelper databaseHelper;
	private final User currentUser;

    public StaffHomePage(DatabaseHelper databaseHelper, User currentUser) {
        this.databaseHelper = databaseHelper;
        this.currentUser = currentUser;
    }
	
	/**
     * Displays the Staff page in the provided primary stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(Stage primaryStage) {
    	VBox layout = new VBox(10);
    	
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // label to display the welcome message for the Staff
	    Label reviewerLabel = new Label("Hello, Staff!");
	    
	    reviewerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

	    
	    //Implement the logout button on Staff home page and returns to the main setup/login page
	    Button logoutButton = new Button("LogOut");
	    
	    logoutButton.setOnAction(a -> {
	    	new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
        	
	    });
	    
	    //////////////////////////////////////////////////////////////////////////////////////////

	    Button MessagesButton = new Button("My Messages");
	    
	    MessagesButton.setOnAction(a -> {
            new StaffChatPage(databaseHelper, currentUser).show(primaryStage);

        });
	    
	    ////////////////////////////////////////////////
	    ///
        Button viewallQuestions = new Button("View all Questions");
    	    
    	viewallQuestions.setOnAction(event -> {
    		   new QuestionList(databaseHelper, currentUser).show(primaryStage);
    
        });
    	
    	//////////////////////////////////////////////////
    	///reviewer list
    	
    	Button reviewerListButton = new Button("View all Reviewers");
    	
    	reviewerListButton.setOnAction(event -> {
 		   new ReviewerListPage(databaseHelper, currentUser).show(primaryStage);
 		    
        });
	    
	    layout.getChildren().addAll(reviewerLabel, MessagesButton, viewallQuestions, reviewerListButton, logoutButton);
	    Scene adminScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(adminScene);
	    primaryStage.setTitle("Admin Page");
    }
}