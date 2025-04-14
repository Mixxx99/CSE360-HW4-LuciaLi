package application;

import java.util.ArrayList;

import databasePart1.DatabaseHelper;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;

/**
 * This class encapsulates a user's possible roles. These roles include admin, staff,
 * instructor, student, and reviewer, and can be accessed as private booleans.
 */
public class RoleList {
	private User user;
	private DatabaseHelper databaseHelper;
	private BooleanProperty isAdmin = new SimpleBooleanProperty();
	private BooleanProperty isStaff = new SimpleBooleanProperty();
	private BooleanProperty isInstructor = new SimpleBooleanProperty();
	private BooleanProperty isStudent = new SimpleBooleanProperty();
	private BooleanProperty isReviewer = new SimpleBooleanProperty();
	
	/**
	 * Sets the user and databaseHelper to null.
	 */
	public RoleList() {
		this.user = null;
		this.databaseHelper = null;
	}
	
	/**
	 * Sets this role list's values to a copy of the other role list's values.
	 * @param copy the role list to copy.
	 */
	public RoleList(RoleList copy) {
		this.user = copy.user;
		this.databaseHelper = copy.databaseHelper;
		this.isAdmin.set(copy.getIsAdmin());
		this.isStaff.set(copy.getIsStaff());
		this.isInstructor.set(copy.getIsInstructor());
		this.isStudent.set(copy.getIsStudent());
		this.isReviewer.set(copy.getIsReviewer());
	}
	
	/**
	 * Sets this role list's values based on the values included in the ArrayList roles.
	 * @param user the user this role list is associated with.
	 * @param databaseHelper the databaseHelper this role list is associated with.
	 * @param roles a list of roles to set to true from the following: "admin", "student",
	 * "instructor", "reviewer", "staff".
	 */
	public RoleList(User user, DatabaseHelper databaseHelper, ArrayList<String> roles) {
		this.user = user;
		this.databaseHelper = databaseHelper;
		if (roles != null) setRoles(roles);
	}
	
	/**
	 * Creates a new roleList from a set of booleans, among other things.
	 * @param user the user whose roles these are
	 * @param databaseHelper the associated databaseHelper
	 * @param roles [isAdmin, isStaff, isInstructor, isStudent, isReviewer]
	 */
	public RoleList(User user, DatabaseHelper databaseHelper, boolean[] roles) {
		this.user = user;
		this.databaseHelper = databaseHelper;
		if (roles != null) setRoles(roles);
	}
	
	/**
	 * Creates a role list from a set of roles given as a string.
	 * @param user the user this list belongs to.
	 * @param databaseHelper the database.
	 * @param roles of the following: "admin", "staff", "student", "reviewer", "instructor",
	 * any included as a substring in roles will be set to true.
	 */
	public RoleList(User user, DatabaseHelper databaseHelper, String roles) {
		this.user = user;
		this.databaseHelper = databaseHelper;
		if (roles != null) setRoles(roles);
	}
	
	/**
	 * Creates a role list from a set of roles given as a string. user is set to null.
	 * @param databaseHelper the database.
	 * @param roles of the following: "admin", "staff", "student", "reviewer", "instructor",
	 * any included as a substring in roles will be set to true.
	 */
	public RoleList(DatabaseHelper databaseHelper, String roles) {
		this.user = null;
		this.databaseHelper = databaseHelper;
		if (roles != null) setRoles(roles);
	}
	
	/**
	 * Sets this role list's values based on the values included in the ArrayList roles.
	 * user is set to null.
	 * @param databaseHelper the databaseHelper this role list is associated with.
	 * @param roles a list of roles to set to true from the following: "admin", "student",
	 * "instructor", "reviewer", "staff".
	 */
	public RoleList(DatabaseHelper databaseHelper, ArrayList<String> roles) {
		this.user = null;
		this.databaseHelper = databaseHelper;
		if (roles != null) setRoles(roles);
	}
	
	/**
	 * Creates a new roleList from a set of booleans, among other things.
	 * @param databaseHelper the associated databaseHelper
	 * @param roles [isAdmin, isStaff, isInstructor, isStudent, isReviewer]
	 */
	public RoleList(DatabaseHelper databaseHelper, boolean[] roles) {
		this.user = null;
		this.databaseHelper = databaseHelper;
		if (roles != null) setRoles(roles);
	}
	
	/**
	 * Any roles from the following list may be set to true: "admin", "staff", "instructor",
	 * "student", "staff", by including them in roles.
	 * @param roles the roles to set to true. Any omitted will be left unchanged.
	 */
	public void setRoles(ArrayList<String> roles) {
		if (roles == null) return;
		
		if (roles.contains("admin")) {
			isAdmin.set(true);
		}
		if (roles.contains("staff")) {
			isStaff.set(true);
		}
		if (roles.contains("instructor")) {
			isInstructor.set(true);
		}
		if (roles.contains("student")) {
			isStudent.set(true);
		}
		if (roles.contains("reviewer")) {
			isReviewer.set(true);
		}
	}
	
	/**
	 * If the following substrings are found in roles, their values in this role list will
	 * be set to true: "admin", "staff", "instructor", "student", "reviewer".
	 * @param roles the roles to set to true. Any roles not included will be left unchanged.
	 */
	public void setRoles(String roles) {
		if (roles == null) return;
		
		if (roles.contains("admin")) {
			isAdmin.set(true);
		}
		if (roles.contains("staff")) {
			isStaff.set(true);
		}
		if (roles.contains("instructor")) {
			isInstructor.set(true);
		}
		if (roles.contains("student")) {
			isStudent.set(true);
		}
		if (roles.contains("reviewer")) {
			isReviewer.set(true);
		}
	}
	
	/**
	 * Sets the roles of this roleList.
	 * @param roles [isAdmin, isStaff, isInstructor, isStudent, isReviewer]
	 */
	public void setRoles(boolean[] roles) {
		if (roles == null) return;
		
		isAdmin.set(roles[0]);
		isStaff.set(roles[1]);
		isInstructor.set(roles[2]);
		isStudent.set(roles[3]);
		isReviewer.set(roles[4]);
	}
	
	/**
	 * Gets the roles set to true in this role list as an observable list.
	 * @return the roles from the following list that are set to true, as strings: "staff",
	 * "student", "instructor", "reviewer", "admin".
	 */
	public ObservableList<String> getRoles() {
		ObservableList<String> roleList = FXCollections.observableArrayList();
		if (getIsAdmin()) {
			roleList.add("admin");
		}
		if (getIsStaff()) {
			roleList.add("staff");
		}
		if (getIsInstructor()) {
			roleList.add("instructor");
		}
		if (getIsStudent()) {
			roleList.add("student");
		}
		if (getIsReviewer()) {
			roleList.add("reviewer");
		}
		return roleList;
	}
	
	public boolean getIsAdmin() { return isAdmin.get(); }
	public boolean getIsStaff() { return isStaff.get(); }
	public boolean getIsInstructor() { return isInstructor.get(); }
	public boolean getIsStudent() { return isStudent.get(); }
	public boolean getIsReviewer() { return isReviewer.get(); }
	
	public void setUser(User user) { this.user = user; }
	
	/**
	 * Puts all of the roles currently assigned as true in this role list into the given
	 * combo box.
	 * @param roleBox will contain the roles that are assigned to this role list.
	 */
	public void singleRoleSelector(ComboBox<String> roleBox) {
        roleBox.setItems(getRoles());
        roleBox.setMaxWidth(250);
	}
	
	/**
	 * Returns a menu button that contains all of the possible roles with the ones that
	 * are currently true in this role list checked. This method also adds listeners so
	 * that changing which roles are checked changes both the properties of this object
	 * and the user's roles in the database.
	 * @param menu the menu button to modify.
	 */
	public void multiRoleSelector(MenuButton menu) {
		ObservableList<MenuItem> items = menu.getItems();
		CheckMenuItem adminItem = new CheckMenuItem("admin");
		adminItem.selectedProperty().set(getIsAdmin());
		adminItem.selectedProperty().addListener(new MenuChangeListener("isAdmin"));
		items.add(adminItem);
		CheckMenuItem instructorItem = new CheckMenuItem("instructor");
		instructorItem.selectedProperty().set(getIsInstructor());
		instructorItem.selectedProperty().addListener(new MenuChangeListener("isInstructor"));
		items.add(instructorItem);
		CheckMenuItem staffItem = new CheckMenuItem("staff");
		staffItem.selectedProperty().set(getIsStaff());
		staffItem.selectedProperty().addListener(new MenuChangeListener("isStaff"));
		items.add(staffItem);
		CheckMenuItem studentItem = new CheckMenuItem("student");
		studentItem.selectedProperty().set(getIsStudent());
		studentItem.selectedProperty().addListener(new MenuChangeListener("isStudent"));
		items.add(studentItem);
		CheckMenuItem reviewerItem = new CheckMenuItem("reviewer");
		reviewerItem.selectedProperty().set(getIsReviewer());
		reviewerItem.selectedProperty().addListener(new MenuChangeListener("isReviewer"));
		items.add(reviewerItem);
		
	}
	
	/**
	 * ChangeListener specific to this application. Updates both the user's local data and the database.
	 */
    private class MenuChangeListener implements ChangeListener<Boolean> {
    	private String databaseProperty;
    	private BooleanProperty superProperty;
    	
    	/**
    	 * Sets the property this menu listener modifies.
    	 */
    	MenuChangeListener(String databaseProperty) {
    		this.databaseProperty = databaseProperty;
    		if (databaseProperty.equals("isAdmin")) {
    			superProperty = isAdmin;
    		} else if (databaseProperty.equals("isInstructor")) {
    			superProperty = isInstructor;
    		} else if (databaseProperty.equals("isStaff")) {
    			superProperty = isStaff;
    		} else if (databaseProperty.equals("isStudent")) {
    			superProperty = isStudent;
    		} else if (databaseProperty.equals("isReviewer")) {
    			superProperty = isReviewer;
    		} else {
    			System.err.println("***ERROR*** Invalid database property.");
    		}
    	}
    	
    	/**
    	 * Changes the selected property in the database.
    	 */
		public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
     		if (superProperty != isAdmin) {
     			if (user != null) {
     				databaseHelper.updateUserRole(user.getUserName(), databaseProperty, newValue);
     			}
         		superProperty.set(newValue);
     		}
     	}
    }
	
    /**
     * Prints a comma-separated list of the roles set to true in this role list.
     */
	public String toString() {
		String roles = "";
		if (getIsAdmin()) {
			roles += "admin, ";
		}
		if (getIsInstructor()) {
			roles += "instructor, ";
		}
		if (getIsStaff()) {
			roles += "staff, ";
		}
		if (getIsStudent()) {
			roles += "student, ";
		}
		if (getIsReviewer()) {
			roles += "reviewer";
		}
		return roles;
	}
}
