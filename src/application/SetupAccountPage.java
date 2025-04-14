package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

import databasePart1.*;

/**
 * SetupAccountPage class handles the account setup process for new users.
 * Users provide their userName, password, and a valid invitation code to register.
 */
public class SetupAccountPage {
	
    private final DatabaseHelper databaseHelper;
    // DatabaseHelper to handle database operations.
    public SetupAccountPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    /**
     * Displays the Setup Account page in the provided stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(Stage primaryStage) {
    	// Input fields for userName, password, and invitation code
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter userName");
        userNameField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);
        
        TextField inviteCodeField = new TextField();
        inviteCodeField.setPromptText("Enter InvitationCode");
        inviteCodeField.setMaxWidth(250);
        
        /////////////////////////////////
        TextField emailField = new TextField(); 		////
        emailField.setPromptText("Enter email");
        emailField.setMaxWidth(250);
        
        TextField nameField = new TextField(); 		////
        nameField.setPromptText("Enter name");
        nameField.setMaxWidth(250);
        //////////////////////////////////
        
        
        // Label to display error messages for invalid input or registration issues
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        

        Button setupButton = new Button("Setup");
        
        setupButton.setOnAction(a -> {
        	// Retrieve user input
            String userName = userNameField.getText();
            String password = passwordField.getText();
            String code = inviteCodeField.getText();
            
            //////////////////////////
            String email = emailField.getText();			/////
            String name = nameField.getText();	
            //////////////////////////////////////
            
            
            try {
            	// Check if the user already exists
            	if(!databaseHelper.doesUserExist(userName)) {
            		
            		// Validate the invitation code
            		if(databaseHelper.validateInvitationCode(code)) {
            			
            			// Create a new user and register them in the database
            			RoleList roles = databaseHelper.getRolesByInvitationCode(code);
						//check the validation of inputs
						String userNameValidity = UserNameRecognizer.checkForValidUserName(userName);
	            		String passwordValidity = PasswordEvaluator.evaluatePassword(password);
	            		String emailValidity = emailRecognizer.checkForValidemail(email);	/////
	            		String nameValidity = nameRecognizer.checkForValidName(name);
				
            			if (roles != null) {
            				if (userNameValidity.equals("") && 
	            				passwordValidity.equals("") && emailValidity.equals("") && nameValidity.equals("")) {
			// If the user write all the information correctrly and roll is not null, then make a new account
						User user=new User(userName, password, roles, email, name);  //role
				                databaseHelper.register(user);
				                System.out.println("new user registered!");
				                
				             // Navigate to the Welcome Login Page
				                new WelcomeLoginPage(databaseHelper).show(primaryStage,user);
		            		}
		            		else if (!userNameValidity.equals("")){
		            			errorLabel.setText(userNameValidity);
		            		}
		            		else if (!passwordValidity.equals("")) {
		            			errorLabel.setText(passwordValidity);
		            		}
		            		else if (!emailValidity.equals("")) {
		            			errorLabel.setText(emailValidity);
		            		}
		            		else {																/////
		            			errorLabel.setText(nameValidity);
		            		}
            			} else {
            				System.err.println("Error: role acquisition failed.");
            			}
            		}
            		else {
            			errorLabel.setText("Please enter a valid invitation code");
            		}
            	}
            	else {
            		errorLabel.setText("This useruserName is taken!!.. Please use another to setup an account");
            	}
            	
            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();
            }
        });

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        layout.getChildren().addAll(userNameField, passwordField, inviteCodeField, emailField, nameField, setupButton, errorLabel);
        
        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Account Setup");
        primaryStage.show();
    }
}

