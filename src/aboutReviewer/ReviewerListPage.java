package aboutReviewer;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;
import java.sql.SQLException;
import java.util.List;

import aboutStaff.StaffHomePage;
import application.User;

public class ReviewerListPage {

	public final DatabaseHelper databaseHelper;
    public final User user; // 当前用户（假定只有有权限的用户可看到此页面）

    public ReviewerListPage(DatabaseHelper databaseHelper, User user) {
        this.databaseHelper = databaseHelper;
        this.user = user;
    }
    
    public void show(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 10;");
        
        Label titleLabel = new Label("Reviewer List");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        ListView<HBox> reviewerListView = new ListView<>();
        try {
            // 获取所有 reviewer 用户（假设 databaseHelper.getReviewers() 已实现）
            List<User> reviewers = databaseHelper.getReviewerList();
            reviewerListView.getItems().clear();
            
            if (reviewers.isEmpty()) {
                Label noReviewersLabel = new Label("No reviewers found.");
                HBox noReviewersBox = new HBox(10, noReviewersLabel);
                reviewerListView.getItems().add(noReviewersBox);
            } else {
                for (User reviewer : reviewers) {
                    // 对于每个 reviewer 显示用户名和 ban 按钮
                    Label reviewerLabel = new Label(reviewer.getUserName());
                    Button banButton = new Button("Ban");
                    banButton.setOnAction(e -> {
                        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, 
                            "Are you sure you want to ban " + reviewer.getUserName() + "?", 
                            ButtonType.YES, ButtonType.NO);
                        confirm.showAndWait().ifPresent(response -> {
                            if (response == ButtonType.YES) {
                                try {
                                    boolean success = databaseHelper.banReviewer(reviewer.getUserName());
                                    if (success) {
                                        System.out.println("Reviewer " + reviewer.getUserName() + " banned.");
                                        show(primaryStage); // 刷新列表
                                    } else {
                                        System.out.println("Banning failed.");
                                    }
                                } catch (SQLException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        });
                    });
                    HBox reviewerItem = new HBox(10, reviewerLabel, banButton);
                    reviewerListView.getItems().add(reviewerItem);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> new StaffHomePage(databaseHelper, user).show(primaryStage));
        
        layout.getChildren().addAll(titleLabel, reviewerListView, backButton);
        primaryStage.setScene(new Scene(layout, 400, 400));
        primaryStage.setTitle("Reviewer List");
        primaryStage.show();
    }
}
