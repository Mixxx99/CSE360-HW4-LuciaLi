package application;

import aboutReviewer.ReviewerRequestsPage;
import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This page display's the instructor's information. From here, the instructor can
 * navigate to a page that lets them approve or deny students' requests to become
 * reviewers.
 */
public class InstructorHomePage {
	private final DatabaseHelper databaseHelper;
	private final User user;
	 
	public InstructorHomePage(DatabaseHelper databaseHelper, User user) {
		this.databaseHelper = databaseHelper;   
		this.user = user;
	}
	
	/**
	 * Displays the instructor's information and the button to go to the reviewer requests
	 * page on the given stage.
	 * @param primaryStage the stage on which to display the page.
	 */
    public void show(Stage primaryStage) {
    	VBox layout = new VBox(10);
    	layout.setStyle("-fx-alignment: center; -fx-padding: 10;");
    	
	    Label userLabel = new Label("Hello, Instructor!");
	    userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
	    
		//Implement the logout button on user home page and returns to the main setup/login page & select user role
		Button logoutButton = new Button("Logout");
       	logoutButton.setOnAction(_ -> {
       		LogOut logOut = new LogOut(databaseHelper, primaryStage);
       		logOut.logout(); 
       	});
       			
		Button requestsButton = new Button("View Reviewer Requests");
		requestsButton.setOnAction(_ -> {
			new ReviewerRequestsPage(databaseHelper, user).show(primaryStage);
		});
		
	    layout.getChildren().addAll(userLabel, requestsButton, logoutButton);
	    
	    Scene userScene = new Scene(layout, 800, 400);
	  
	    // Set the scene to primary stage
	    primaryStage.setScene(userScene);
	    primaryStage.setTitle("Instructor Page");
   	
   }
}
