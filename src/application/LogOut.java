package application;

import databasePart1.DatabaseHelper;
import javafx.scene.control.Button;


import javafx.stage.Stage;

public class LogOut {
    protected final DatabaseHelper databaseHelper;
    protected final Stage primaryStage;

    public LogOut(DatabaseHelper databaseHelper, Stage primaryStage) {
        this.databaseHelper = databaseHelper;
        this.primaryStage = primaryStage;
    }

    protected Button createLogoutButton() {
        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(event -> logout());
        return logoutButton;
    }

    public void logout() {  
        SetupLoginSelectionPage loginPage = new SetupLoginSelectionPage(databaseHelper);
        loginPage.show(primaryStage);
    }
}
