package aboutReviewer;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import databasePart1.*;

import java.sql.SQLException;
import java.util.List;

import application.AdminHomePage;
import application.AnswersList;
import application.Question;
import application.StudentHomePage;
import application.User;

public class ReviewList {

    private final DatabaseHelper databaseHelper;
    private final User user;

    public ReviewList(DatabaseHelper databaseHelper, User user) {
        this.databaseHelper = databaseHelper;
        this.user = user;
    }

    public void show(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 10;");

        Label titleLabel = new Label("My Reviews");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        ListView<HBox> reviewListView = new ListView<>();

        try {
            // 获取当前用户的所有 review 数据
            List<Review> reviews = databaseHelper.getAllReviewsByUser(user.getUserName());

            for (Review review : reviews) {
                // 显示 review 内容的简略信息（例如：只显示前 50 个字符）
                Label reviewLabel = new Label("Review for Q#" + review.getQuestionId() + ": " + shorten(review.getReviewContent(), 50));
                Button viewButton = new Button("View Full");
                Button editButton = new Button("Edit");
                Button deleteButton = new Button("Delete");

                HBox reviewItem = new HBox(10, reviewLabel, viewButton, editButton, deleteButton);

                // 点击 View Full 按钮：通过 review 的 questionId 查找对应问题，并跳转到问题详情页
                viewButton.setOnAction(e -> {
                    try {
                        // 假设 getQuestionById() 方法可以返回对应的 Question 对象
                        Question question = databaseHelper.getQuestionById(review.getQuestionId());
                        if (question != null) {
                            new AnswersList(databaseHelper, question, user).show(primaryStage);
                        } else {
                            showAlert("Error", "Original question not found.");
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                });

                editButton.setOnAction(e -> showEditPopup(review, primaryStage));

                deleteButton.setOnAction(e -> {
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this review?", ButtonType.YES, ButtonType.NO);
                    confirm.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.YES) {
                            try {
                                databaseHelper.deleteReview(review.getReviewId());
                                System.out.println("Review deleted successfully.");
                                show(primaryStage); // 刷新页面
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
                });

                reviewListView.getItems().add(reviewItem);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Button backButton = new Button("Back");

        if (user.isAdmin()) {
            backButton.setOnAction(a -> new AdminHomePage(databaseHelper, user).show(primaryStage));
        } else if (user.isStudent()) {
            backButton.setOnAction(a -> new StudentHomePage(databaseHelper, user).show(primaryStage));
        } else if (user.isReviewer()) {
            backButton.setOnAction(a -> new ReviwerHomePage(databaseHelper, user).show(primaryStage));
        }

        layout.getChildren().addAll(titleLabel, reviewListView, backButton);
        Scene scene = new Scene(layout, 800, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("My Reviews");
        primaryStage.show();
    }

    //
    private String shorten(String text, int length) {
        if (text == null) return "";
        return text.length() <= length ? text : text.substring(0, length) + "...";
    }

    // 
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    //edit review pop up
    private void showEditPopup(Review review, Stage primaryStage) {
        Stage popup = new Stage();
        popup.setTitle("Edit Review");

        VBox popupLayout = new VBox(10);
        popupLayout.setStyle("-fx-padding: 10;");

        TextArea contentInput = new TextArea(review.getReviewContent());
        contentInput.setPrefSize(400, 200);

        Button saveButton = new Button("Save Changes");
        saveButton.setOnAction(e -> {
            String newContent = contentInput.getText().trim();
            if (!newContent.isEmpty()) {
                try {
                    boolean success = databaseHelper.updateReview(review.getReviewId(), newContent);
                    if (success) {
                        System.out.println("Review updated successfully!");
                        popup.close();
                        show(primaryStage);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Input Error");
                alert.setHeaderText(null);
                alert.setContentText("Review content cannot be empty!");
                alert.showAndWait();
            }
        });

        popupLayout.getChildren().addAll(new Label("Edit Review:"), contentInput, saveButton);
        Scene popupScene = new Scene(popupLayout, 500, 300);
        popup.setScene(popupScene);
        popup.show();
    }
}