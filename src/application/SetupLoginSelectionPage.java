package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

import databasePart1.*;

/**
 * The SetupLoginSelectionPage class allows users to choose between setting up a new account
 * or logging into an existing account. It provides two buttons for navigation to the respective pages.
 */
public class SetupLoginSelectionPage {
	
    private final DatabaseHelper databaseHelper;

    public SetupLoginSelectionPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
        
    	
    	// Buttons to select Login / Setup options that redirect to respective pages
        Button setupButton = new Button("SetUp");
        Button loginButton = new Button("Login");
        
        
        /////////////////////////////////////////////////////////////////////Only for test
        
        Button deleteUsersButton = new Button("Delete All Users");
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        deleteUsersButton.setOnAction(a -> {
            try {
                databaseHelper.deleteAllUsers(); 
                errorLabel.setText("All users have been deleted successfully."); // Correct usage for success message
            } catch (SQLException e) {
                errorLabel.setText("Database Error: " + e.getMessage()); // Correct usage for error message
                e.printStackTrace();
            }
        });
        
        ////////////////////////////////////////////////////////////////////////////////
        setupButton.setOnAction(a -> {
        	try {
                if (databaseHelper.isDatabaseEmpty()) {
                    
                    new AdminSetupPage(databaseHelper).show(primaryStage);
                
                } else {
                    
                    new SetupAccountPage(databaseHelper).show(primaryStage);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                
            }
        });
        
        loginButton.setOnAction(a -> {
        	new UserLoginPage(databaseHelper).show(primaryStage);
        });
        /////////////////////////////////////////////////////////////////////////////////////

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        //layout.getChildren().addAll(setupButton, loginButton);
       
        layout.getChildren().addAll(setupButton, loginButton, deleteUsersButton);

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Account Setup");
        primaryStage.show();
    }
}