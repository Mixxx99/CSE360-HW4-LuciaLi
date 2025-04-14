package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


import databasePart1.*;

/**
 * The SetupLoginSelectionPage class allows users to choose between setting up a new account
 * or logging into an existing account. It provides two buttons for navigation to the respective pages.
 */
public class RoleSelection {
	
    private DatabaseHelper databaseHelper = new DatabaseHelper();

    public RoleSelection(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
	}

	public void show(Stage primaryStage) {
		VBox layout = new VBox();
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // Label to display Hello user
	    Label userLabel = new Label("Hello User, Please Select Your Role!");
	    userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
		
    	// Buttons to select Login / Setup options that redirect to respective pages
        Button studentButton = new Button("Student");
        Button instructorButton = new Button("Instructor");
        Button staffButton = new Button("Staff");
        Button reviewerButton = new Button("Reviewer");
        
        studentButton.setOnAction(a -> {
            new SetupAccountPage(databaseHelper).show(primaryStage);
        });
        instructorButton.setOnAction(a -> {
        	new UserLoginPage(databaseHelper).show(primaryStage);
        });
        
        staffButton.setOnAction(a -> {
            new SetupAccountPage(databaseHelper).show(primaryStage);
        });
        reviewerButton.setOnAction(a -> {
        	new UserLoginPage(databaseHelper).show(primaryStage);
        });

        VBox layout1 = new VBox(10);
        layout1.setStyle("-fx-padding: 20; -fx-alignment: center;");
        layout1.getChildren().addAll(studentButton, instructorButton, staffButton, reviewerButton);

        primaryStage.setScene(new Scene(layout1, 800, 400));
        primaryStage.setTitle("Role Selection");
        primaryStage.show();
    }
}
