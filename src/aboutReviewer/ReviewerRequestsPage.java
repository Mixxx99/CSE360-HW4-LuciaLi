package aboutReviewer;

import java.sql.SQLException;
import java.util.List;

import application.InstructorHomePage;
import application.LogOut;
import application.User;
import databasePart1.DatabaseHelper;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This page displays all of the requests students have made to become reviewers. It 
 * displays the username of the student who made the request and a short message they
 * left. The instructor can approve or deny the request, or view the student's work
 * to make their decision.
 */
public class ReviewerRequestsPage {
	private final DatabaseHelper databaseHelper;
	private final User user;
	 
	public ReviewerRequestsPage(DatabaseHelper databaseHelper, User user) {
		this.databaseHelper = databaseHelper;   
		this.user = user;
	}
	
	/**
	 * Displays the reviewer requests page. This has a list of requests, which include
	 * the student's username and message, in addition to approve, deny, and see work 
	 * buttons.
	 * @param primaryStage the stage on which to display this page.
	 */
    public void show(Stage primaryStage) {
    	VBox layout = new VBox(10);
    	layout.setStyle("-fx-alignment: center; -fx-padding: 10;");
    	
	    Label userLabel = new Label("Approve or deny students' requests to become reviewers.");
	    userLabel.setStyle("-fx-font-size: 16px;");
	    
	    
		Button logoutButton = new Button("Logout");
       	logoutButton.setOnAction(_ -> {
       		LogOut logOut = new LogOut(databaseHelper, primaryStage);
       		logOut.logout(); 
       	});
		
       	Button backButton = new Button("Back");
       	backButton.setOnAction(_ -> {
       		new InstructorHomePage(databaseHelper, user).show(primaryStage);
       	});
       	
       	
       	ListView<HBox> requestsListView = new ListView<>();

	    try {
	        ObservableList<ReviewerRequest> requests = databaseHelper.getReviewerRequests();

	        for (ReviewerRequest request : requests) {
	            Label requestLabel = new Label(request.getUserName() + ": " + request.getContent());   
	            Button approveButton = new Button("Approve");
	            Button denyButton = new Button("Deny");
	            Button seeWorkButton = new Button("See Work");
	            
	            HBox questionItem = new HBox(10, requestLabel, approveButton, denyButton, seeWorkButton);
            	
	            approveButton.setOnAction(_ -> {
	            	try {
		            	databaseHelper.approveReviewerRequest(request.getUserName());
		            	requestsListView.getItems().remove(questionItem);
	            	} catch (SQLException e) {
	            		e.printStackTrace();
	            	}
	            });
	            denyButton.setOnAction(_ -> {
	            	try {
		            	databaseHelper.denyReviewerRequest(request.getUserName());
		            	requestsListView.getItems().remove(questionItem);
	            	} catch (SQLException e) {
	            		e.printStackTrace();
	            	}
	            });   
	            //seeWorkButton.setOnAction();   
		        // TO IMPLEMENT \\


	            requestsListView.getItems().add(questionItem);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
      
		
	    layout.getChildren().addAll(userLabel, requestsListView, backButton, logoutButton);
	    
	    Scene reviewerRequestsScene = new Scene(layout, 800, 400);
	  
	    // Set the scene to primary stage
	    primaryStage.setScene(reviewerRequestsScene);
	    primaryStage.setTitle("Reviewer Requests Page");
   	
    }
}
