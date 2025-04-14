package aboutReviewer;

import application.QuestionList;
import application.SetupLoginSelectionPage;
import application.User;
import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


/**
 * ReviwerPage class represents the user interface for the reviewer user.
 * This page a  welcome message and couple functionality button for the reviewer.
 */

public class ReviwerHomePage {
	
	private final DatabaseHelper databaseHelper;
	private final User currentUser;

    public ReviwerHomePage(DatabaseHelper databaseHelper, User currentUser) {
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
	    Label reviewerLabel = new Label("Hello, Reviwer!");
	    
	    reviewerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

	    
	    //Implement the logout button on admins home page and returns to the main setup/login page
	    Button logoutButton = new Button("LogOut");
	    
	    logoutButton.setOnAction(a -> {
	    	new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
        	
	    });
	    
	    
	    //////////////////////////////////////////////////////////////////////////////////////////

	    Button ReviewList = new Button("My Review List");
	    
	    ReviewList.setOnAction(a -> {
            new ReviewList(databaseHelper, currentUser).show(primaryStage);

        });
	    
	    ////////////////////////////////////////////////
	    ///
        Button viewallQuestions = new Button("View all Questions");
    	    
    	viewallQuestions.setOnAction(event -> {
    		   new QuestionList(databaseHelper, currentUser).show(primaryStage);
    		        
        });
	    
	    layout.getChildren().addAll(reviewerLabel, ReviewList, viewallQuestions, logoutButton);
	    Scene adminScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(adminScene);
	    primaryStage.setTitle("Admin Page");
    }
}