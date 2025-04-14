package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


/**
 * AdminHomePage class represents the user interface for the admin user. This page 
 * includes a simple welcome message for the admin and buttons to invite new users,
 * list the users, view the questions, and help a user who has forgotten their password.
 */

public class AdminHomePage {
	
	private final DatabaseHelper databaseHelper;
	private final User currentUser;

    public AdminHomePage(DatabaseHelper databaseHelper, User currentUser) {
        this.databaseHelper = databaseHelper;
        this.currentUser = currentUser;
    }
	
	/**
     * Displays the admin page in the provided primary stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(Stage primaryStage) {
    	VBox layout = new VBox(10);
    	
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // label to display the welcome message for the admin
	    Label adminLabel = new Label("Hello, Admin!");
	    
	    adminLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

	    
	    //Implement the logout button on admins home page and returns to the main setup/login page
	    Button logoutButton = new Button("LogOut");
	    
	    logoutButton.setOnAction(a -> {
	    	new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
        	
	    });
	    
	    Button listUsersButton = new Button("List Users");
	    
	    listUsersButton.setOnAction(a -> {
	    	new AdminUserListPage(databaseHelper, currentUser).show(primaryStage);
	    });
	    
	    Button forgotPassword = new Button("User Forgot Password?");
	    
	    forgotPassword.setOnAction(a -> {
	    	new SetupLoginSelectionPage(databaseHelper).show(primaryStage); //change this to a user reset password screen
        	
	        
	    });
	    //////////////////////////////////////////////////////////////////////////////////////////

	    Button inviteButton = new Button("Invite");
        	inviteButton.setOnAction(a -> {
            new InvitationPage().show(databaseHelper, primaryStage, currentUser);
        	});
        	layout.getChildren().add(inviteButton);
	    ////////////////////////////////////////////////
	    ///
        Button viewallQuestions = new Button("View all Questions");
    	    
    	viewallQuestions.setOnAction(event -> {
    		   new QuestionList(databaseHelper, currentUser).show(primaryStage);
    		        
        });
	    
	    layout.getChildren().addAll(adminLabel, listUsersButton, viewallQuestions, logoutButton, forgotPassword);
	    Scene adminScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(adminScene);
	    primaryStage.setTitle("Admin Page");
    }
}