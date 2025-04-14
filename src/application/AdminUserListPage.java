package application;

import java.sql.SQLException;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.cell.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.*;


/**
 * AdminUserListPage class represents another user interface for the admin user.
 * This page lists all users' Username, Name, Email, and Roles.
 */
public class AdminUserListPage {
	
	private final DatabaseHelper databaseHelper;
	
	private User currentUser;

    public AdminUserListPage(DatabaseHelper databaseHelper, User currentUser) {
        this.databaseHelper = databaseHelper;
        this.currentUser = currentUser;
    }
	
    /**
     * Displays the admin user list page in the provided primary stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(Stage primaryStage) {
    	VBox layout = new VBox(10);
    	
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    Label adminLabel = new Label("Here is the user information:");
	    
	    adminLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

	    
	    Button logoutButton = new Button("LogOut");
	    
	    logoutButton.setOnAction(a -> {
	    	new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
        	
	    });
	    
        Label errorMessage = new Label("");
        errorMessage.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
	    
	    
	    TableView<User> table = new TableView<User>();
	    
	    TableColumn<User, String> nameCol = new TableColumn<User, String>("Name");
	    nameCol.setMinWidth(150);
        nameCol.setCellValueFactory(
                new PropertyValueFactory<User, String>("name"));
	    
	    TableColumn<User, String> userNameCol = new TableColumn<User, String>("Username");
	    userNameCol.setMinWidth(150);
	    userNameCol.setCellValueFactory(
                new PropertyValueFactory<User, String>("userName"));
	    
	    TableColumn<User, String> emailCol = new TableColumn<User, String>("Email");
	    emailCol.setMinWidth(250);
        emailCol.setCellValueFactory(
                new PropertyValueFactory<User, String>("email"));
        
        // Here the user's ComboBox is added to the table.
        // This has the correct list of roles and selected role and makes necessary updates when changed.
	    TableColumn<User, String> roleCol = new TableColumn<>("Role");
	    roleCol.setMinWidth(200);
	    roleCol.setCellValueFactory(new PropertyValueFactory<User, String>("roleMenuButton"));
        
        table.setItems(databaseHelper.getUsers(currentUser));
	    
	    
	    table.getColumns().addAll(nameCol, userNameCol, emailCol, roleCol);
	    
	    Button deleteButton = new Button("Delete User");
        
        deleteButton.setOnAction(a -> deleteUser(table, errorMessage));
	    
	    layout.getChildren().addAll(adminLabel, table, deleteButton, logoutButton, errorMessage);
	    Scene userListScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(userListScene);
	    primaryStage.setTitle("Admin User List Page");
    }
    
    /**
     * Deletes the selected user from the table, as long as they are not the current user.
     * @param table the table of users from which to delete the selected one.
     * @param errorMessage should be set with the error message to display if this fails.s
     */
    private void deleteUser(TableView<User> table, Label errorMessage) {
    	User selected = table.getSelectionModel().getSelectedItem();
    	if (selected != null) {
    		if (!selected.isCurrentUser()) {
				Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure?", ButtonType.YES, ButtonType.NO);
			    confirm.showAndWait().ifPresent(response -> {
			        
			        if (response == ButtonType.YES) {
			            try {
			            	databaseHelper.deleteUser(selected);
		    				table.getItems().remove(selected);
		            		errorMessage.setText("");

			            } catch (SQLException ex) {
			            	errorMessage.setText("Error deleting user.");
			                ex.printStackTrace();
			            }
			        } else {
			        	errorMessage.setText("");
			        }
			    });
    		} else {
        		errorMessage.setText("Error: you cannot delete your own account.");
    		}
    	} else {
    		errorMessage.setText("Error: must select user to delete.");
    	}
    }
    
    
    
	        
}