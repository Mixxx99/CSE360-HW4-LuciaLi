package application;


import databasePart1.*;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * InvitePage class represents the page where an admin can generate an invitation code.
 * The invitation code is displayed upon clicking a button.
 */

public class InvitationPage {
	
	/**
     * Displays the Invite Page in the provided primary stage.
     * 
     * @param databaseHelper An instance of DatabaseHelper to handle database operations.
     * @param primaryStage   The primary stage where the scene will be displayed.
     */
    public void show(DatabaseHelper databaseHelper,Stage primaryStage, User user) {
    	VBox layout = new VBox();
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // Label to display the title of the page
	    Label userLabel = new Label("Invite ");
	    userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
	    /////////////////////////////////////////////////////////////////
	    MenuButton roleMenu = new MenuButton("Select user's role(s)");
        RoleList invitedRoles = new RoleList(databaseHelper, "");
        invitedRoles.multiRoleSelector(roleMenu);
	    //////////////////////////////////////////////
	    
	    // Button to generate the invitation code
	    Button showCodeButton = new Button("Generate Invitation Code");
	    
	    // Label to display the generated invitation code
	    Label inviteCodeLabel = new Label(""); ;
        inviteCodeLabel.setStyle("-fx-font-size: 14px; -fx-font-style: italic;");
        
        showCodeButton.setOnAction(a -> {
        // Generate the invitation code using the databaseHelper and set it to the label
        	
		if (invitedRoles.getIsAdmin() || invitedRoles.getIsInstructor() ||
				invitedRoles.getIsStaff() || invitedRoles.getIsStudent() ||
				invitedRoles.getIsReviewer()) {
            String invitationCode = databaseHelper.generateInvitationCode(invitedRoles);
            inviteCodeLabel.setText(invitationCode);
		}else{
		    inviteCodeLabel.setText("Please select a role first.");
		}
        });
        
        
        Button logoutButton = new Button("LogOut");
	    
	    logoutButton.setOnAction(a -> {
	    	new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
        	
	    });
        
	    Button backButton = new Button("Back");
	    
	    backButton.setOnAction(a -> {
	    	new AdminHomePage(databaseHelper, user).show(primaryStage);
        	
	    });
	    
        layout.getChildren().addAll(userLabel, showCodeButton, roleMenu, inviteCodeLabel, backButton, logoutButton);
	    Scene inviteScene = new Scene(layout, 800, 400);
	    //////////////////////////////////////////////////

	    // Set the scene to primary stage
	    primaryStage.setScene(inviteScene);
	    primaryStage.setTitle("Invite Page");
    	
    }
}