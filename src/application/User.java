package application;

import java.util.ArrayList;

import databasePart1.DatabaseHelper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;

/**
 * The User class represents a user entity in the system.
 * It contains the user's details such as userName, password, and role.
 */
public class User {
    private String userName;
    private String password;
    private RoleList roles;
    ///////////////// 
    private String email;
    private String name;
    private DatabaseHelper databaseHelper;
    private boolean isCurrentUser = false;
    private MenuButton roleMenuButton;
    private boolean menuIsSet = false;
    /////////////////
    
    /*
    // ChangeListener specific to this application. Updates both the user's local data and the database.
    private class RoleChangeListener implements ChangeListener<String> {
		public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
     		if (!isCurrentUser) {
				role = newValue;
	     		databaseHelper.updateUserRole(userName, newValue);
     		}
     		updateComboBoxSelection();
     	}
    }*/

    // Constructor to initialize a new User object with userName, password, and role.
    public User( String userName, String password, RoleList roles, String email, String name) {
        this.userName = userName;
        this.password = password;
        if (roles != null) this.roles = new RoleList(roles);
        /////////// 
        this.email = email;
        this.name = name;
        /////////////
    }

    //For login
    //////////////////////////////////////////////////////////
    public User( String userName, String password, RoleList roles) {
        this.userName = userName;
        this.password = password;
        if (roles != null) this.roles = new RoleList(roles);
    }
    //////////////////////////////////////////////////////////
    
    public void addRoleList(RoleList roles) {
    	if (roles != null) {
    		this.roles = new RoleList(roles);
    		this.roles.setUser(this);
    	}
    }
    
    // Sets the roles of the user from an ArrayList.
    public void setRoles(ArrayList<String> roles) {
    	if (roles != null) this.roles.setRoles(roles);
    }
    
    /**
     * Sets the roles of the user from a boolean array.
     * @param roles [isAdmin, isStaff, isInstructor, isStudent, isReviewer]
     */
    public void setRoles(boolean[] roles) {
    	if (roles != null) this.roles.setRoles(roles);
    }
    
    public boolean isAdmin() { return roles.getIsAdmin(); }
    public boolean isInstructor() { return roles.getIsInstructor(); }
    public boolean isStaff() { return roles.getIsStaff(); }
    public boolean isStudent() { return roles.getIsStudent(); }
    public boolean isReviewer() { return roles.getIsReviewer(); }

    public String getUserName() { return userName; }
    public String getPassword() { return password; }
    public ObservableList<String> getRolesAsStrings() { return roles.getRoles(); }
    public RoleList getRoleList() { return roles; }
    public void printRoles() {
    	System.out.println(roles.toString());
    }
    //////////////////////
    public String getEmail() { return email; }
    public String getName() { return name; }
    //////////////////
    public void createSingleRoleSelector(ComboBox<String> roleBox) {
    	roles.singleRoleSelector(roleBox);
    }
    
    public MenuButton getRoleMenuButton() { 
    	if (!menuIsSet) {
    		roleMenuButton = new MenuButton("change roles");
    		roles.multiRoleSelector(roleMenuButton);
    		menuIsSet = true;
    	}
    	return roleMenuButton;
    }
    
    public void setCurrentUser() { isCurrentUser = true; }
    public boolean isCurrentUser() { return isCurrentUser; }
}
