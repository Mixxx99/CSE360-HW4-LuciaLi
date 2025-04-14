package aboutStaff;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;
import java.sql.SQLException;
import java.util.List;

import application.User;

public class StaffChatPage {

    private final DatabaseHelper databaseHelper;
    private final User currentUser; // 当前 staff 用户

    public StaffChatPage(DatabaseHelper databaseHelper, User currentUser) {
        this.databaseHelper = databaseHelper;
        this.currentUser = currentUser;
    }
    
    public void show(Stage primaryStage) {
        // MAIN LAYOUT
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        
        // NEW: 页面标题
        Label headerLabel = new Label("Chat");
        headerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // NEW: 下拉框，用于选择对话的收件人（其他 staff 用户）
        ComboBox<String> recipientCombo = new ComboBox<>();
        recipientCombo.setPromptText("Select recipient");
        try {
            List<String> staffNames = databaseHelper.getStaffList();
            recipientCombo.getItems().addAll(staffNames);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // NEW: 对话消息展示区域
        ListView<HBox> conversationListView = new ListView<>();
        conversationListView.setPrefHeight(400);
        
        // NEW: 消息输入区和发送按钮（在下面的 HBox 中）
        HBox inputBox = new HBox(10);
        TextField messageField = new TextField();
        messageField.setPrefWidth(600);
        Button sendButton = new Button("Send");
        sendButton.setOnAction(e -> {
            String recipient = recipientCombo.getValue();
            String messageText = messageField.getText().trim();
            if (recipient == null || recipient.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a recipient.");
                alert.showAndWait();
                return;
            }
            if (messageText.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Message cannot be empty.");
                alert.showAndWait();
                return;
            }
            try {
                boolean success = databaseHelper.submitStaffMessage(currentUser.getUserName(), recipient, messageText);
                if (success) {
                    messageField.clear();
                    loadConversation(recipient, conversationListView); // NEW: 刷新对话列表
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to send message.");
                    alert.showAndWait();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
        inputBox.getChildren().addAll(messageField, sendButton);
        
        // NEW: 下拉框监听——当收件人变化时，刷新对话列表
        recipientCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            loadConversation(newVal, conversationListView);
        });
        
        // NEW: 返回按钮
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            // 返回到 staff 主菜单页面（例如 StaffMessagePage 页面）
            new StaffHomePage(databaseHelper, currentUser).show(primaryStage);
        });
        
        layout.getChildren().addAll(headerLabel, recipientCombo, conversationListView, inputBox, backButton);
        Scene scene = new Scene(layout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Staff Chat");
        primaryStage.show();
    }
    
    // NEW: 加载当前对话双方的消息，依据选定的 recipient
    private void loadConversation(String recipient, ListView<HBox> conversationListView) {
        try {
            // 调用 databaseHelper.getConversation() 获取对话消息
            List<StaffMessage> messages = databaseHelper.getConversation(currentUser.getUserName(), recipient);
            conversationListView.getItems().clear();
            for (StaffMessage msg : messages) {
                Label msgLabel = new Label(msg.getSender() + ": " + msg.getMessageContent());
                HBox messageBox = new HBox(msgLabel);
                // NEW: 根据消息发送者调整对齐方式及背景色
                if (msg.getSender().equals(currentUser.getUserName())) {
                    messageBox.setStyle("-fx-alignment: center-right; -fx-background-color: #d4f7dc; -fx-padding: 5;");
                } else {
                    messageBox.setStyle("-fx-alignment: center-left; -fx-background-color: #f0f0f0; -fx-padding: 5;");
                }
                conversationListView.getItems().add(messageBox);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
