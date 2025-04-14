package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import aboutReviewer.Review;

public class AnswersList {

    private final DatabaseHelper databaseHelper;
    private final Question question;
    private final User user;
    

    public AnswersList(DatabaseHelper databaseHelper, Question question, User user) {
        this.databaseHelper = databaseHelper;
        this.question = question;
        this.user = user;
    }

    public void show(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 10;");

        //Question part
        Label questionTitle = new Label("Q: " + question.getTitle());
        questionTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        Label questionContent = new Label(question.getContent());

        ////////////////NEW: Begin Answer Part (Left VBox) //////////////////////
        VBox answersVBox = new VBox(10);
        
        Label answerSectionLabel = new Label("Answers:");
        answerSectionLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        ListView<HBox> answerListView = new ListView<>();
        answerListView.setPrefWidth(560);
        answerListView.setPrefHeight(250);
        loadAnswers(answerListView, primaryStage);

        //warning layout 
        Label warningLabel = new Label();
        warningLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        TextArea answerInput = new TextArea();
        answerInput.setPromptText("Write your answer...");
        answerInput.setPrefRowCount(6); 
        answerInput.setWrapText(true);

        ListView<HBox> suggesting = new ListView<>();
        suggesting.setPrefWidth(100);
        suggesting.setPrefHeight(98);

        Button submitButton = new Button("Submit");
        
        //Button to access Messaging:
        Button messagesButton = new Button("View Messages for this Question");
        messagesButton.setOnAction(e -> {
			try {
				new PrivateMessages(databaseHelper,question, user).show(primaryStage);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}); 
        
        List<String> practicle =  List.of("is", "a", "the", "some", "any", "just", "of", "in" , "on", "off", "or", "and");

        try {
    		List<Answer> answers = databaseHelper.getAnswersForQuestion(question.getQuestionId());
        	HashMap<Integer, Integer> check = new HashMap<>();
        	for (Answer a : answers) 
        		check.put(a.getAnswerId(), 0);

        	answerInput.textProperty().addListener((observable, oldValue, newValue) -> {
            String[] words = newValue.trim().split("\\s+");
            	if (newValue.endsWith(" ") && words.length > 0) {
                    System.out.println("Input words in the answer contents: " + words[words.length - 1]); // 마지막 단어 출력
                    if (!practicle.contains(words[words.length - 1])) {
                		for (Answer a : answers) {
                			if (a.getAnswerContent().contains(words[words.length - 1]) && check.get(a.getAnswerId()) == 0) {
                        		Label answerLabel = new Label(a.getUserName() + ": " + a.getAnswerContent());
                                HBox answerItem = new HBox(10, answerLabel);
                                suggesting.getItems().add(answerItem);
                	            check.put(a.getAnswerId(), 1);
                    		}
                    	}
                	}
                }
    		});
        	
        } catch (SQLException e){
        	System.err.println("Error showing answers: " + e.getMessage());
        }
        
        submitButton.setOnAction(e -> submitAnswer(answerInput, warningLabel, answerListView, primaryStage, suggesting));
        answersVBox.getChildren().addAll(answerSectionLabel, answerListView, answerInput, submitButton, messagesButton, warningLabel, suggesting);
        //////////////// End Answer Part (Left) //////////////////////
        
        

        //////////////// Begin Review Part (Right) //////////////////////
        VBox reviewsVBox = new VBox(10);
        Label reviewSectionLabel = new Label("Reviews:");
        reviewSectionLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // review list
        ListView<HBox> reviewsListView = new ListView<>();
        reviewsListView.setPrefWidth(240);
        reviewsListView.setPrefHeight(250);
        loadReviews(reviewsListView, primaryStage);
        
        // review input area and submit button (only for reviewer)
        if (user.isReviewer()) {
            TextArea reviewInput = new TextArea();
            reviewInput.setPromptText("Enter your review...");
            reviewInput.setPrefRowCount(4);
            reviewInput.setWrapText(true);
            Button reviewSubmitButton = new Button("Submit Review");
            reviewSubmitButton.setOnAction(e -> {
                String reviewText = reviewInput.getText().trim();
                if (!reviewText.isEmpty()) {
                    try {
                        // use submitReview 
                        boolean success = databaseHelper.submitReview(question.getQuestionId(), reviewText, user.getUserName());
                        if (success) {
                            System.out.println("Review submitted successfully!");
                            reviewInput.clear();
                            loadReviews(reviewsListView, primaryStage); // refresh review list
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Review cannot be empty!");
                    alert.showAndWait();
                }
            });
            reviewsVBox.getChildren().addAll(reviewSectionLabel, reviewsListView, reviewInput, reviewSubmitButton);
        } else {
            reviewsVBox.getChildren().addAll(reviewSectionLabel, reviewsListView);
        }
        //////////////// End Review Part (Right) //////////////////////
        
        //////////////// Combine Answer and Review //////////////////////
        HBox mainContentBox = new HBox(10, answersVBox, reviewsVBox);
        
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> new QuestionList(databaseHelper, user).show(primaryStage));

        //add mainContentBox in layout instead of individual components
        layout.getChildren().addAll(questionTitle, questionContent, mainContentBox, backButton);
        primaryStage.setScene(new Scene(layout, 800, 500));
        primaryStage.setTitle("Question detail");
        
    }

    public void loadAnswers(ListView<HBox> answerListView, Stage primaryStage) {
        try {
            List<Answer> answers = databaseHelper.getAnswersForQuestion(question.getQuestionId());
            answerListView.getItems().clear();
            
            if (answers.isEmpty()) {
                Label noAnswersLabel = new Label("No answers yet.");
                HBox noAnswersBox = new HBox(10, noAnswersLabel);
                answerListView.getItems().add(noAnswersBox);
                return;
            }

            for (Answer a : answers) {
                Label answerLabel = new Label(a.getUserName() + ": " + a.getAnswerContent());
                HBox answerItem = new HBox(10, answerLabel);

                if (a.getUserName().equals(user.getUserName()) || user.isAdmin()) {
                    Button editButton = new Button("Edit");
                    Button deleteButton = new Button("Delete");
                    

                    editButton.setOnAction(e -> showEditPopup(a, answerListView, primaryStage));
                    deleteButton.setOnAction(e -> deleteAnswer(user,a, answerListView, primaryStage));

                    answerItem.getChildren().addAll(editButton, deleteButton);
                }
                answerListView.getItems().add(answerItem);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void loadReviews(ListView<HBox> reviewsListView, Stage primaryStage) {
        try {
            List<Review> reviews = databaseHelper.getReviewsForQuestion(question.getQuestionId());
            reviewsListView.getItems().clear();
            
            if (reviews.isEmpty()) {
                Label noReviewsLabel = new Label("No reviews yet.");
                HBox noReviewsBox = new HBox(10, noReviewsLabel);
                reviewsListView.getItems().add(noReviewsBox);
                return;
            }
            
            for (Review r : reviews) {
                Label reviewLabel = new Label(r.getUserName() + ": " + r.getReviewContent());
                HBox reviewItem = new HBox(10, reviewLabel);
                reviewsListView.getItems().add(reviewItem);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void submitAnswer(TextArea answerInput, Label warningLabel, ListView<HBox> answerListView, Stage primaryStage, ListView<HBox> suggesting) {
        String answerText = answerInput.getText().trim();
        if (!answerText.isEmpty()) {
            try {
            	List<Answer> answers = databaseHelper.getAnswersForQuestion(question.getQuestionId());
            	for (Answer a : answers) {
            		if (a.getAnswerContent().equals(answerText)) {
            			warningLabel.setText("You cannot copy other's answer!");
            			return;
            		}
            	}
                Answer newAnswer = new Answer(question.getQuestionId(), user.getUserName(), answerText);
                databaseHelper.addAnswer(newAnswer);
                answerInput.clear();
                loadAnswers(answerListView, primaryStage);
                warningLabel.setText("");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else { 
        	
        	warningLabel.setText("Answer cannot be empty!");
        }
    }

    
    
    public void showEditPopup(Answer answer, ListView<HBox> answerListView, Stage primaryStage) {
    	Stage popup = new Stage();
        popup.setTitle("Edit Answer");

        VBox popupLayout = new VBox(10);
        popupLayout.setStyle("-fx-padding: 10;");

        TextArea editInput = new TextArea(answer.getAnswerContent());
        editInput.setPrefSize(400, 200); //size
        
        //save
        Button saveButton = new Button("Save Changes");
        
        saveButton.setOnAction(e -> {
            String newContent = editInput.getText().trim();
            if (!newContent.isEmpty()) {
                try {
                    boolean success = databaseHelper.updateAnswer(answer.getAnswerId(), newContent, user);
                    if (success) {
                        loadAnswers(answerListView, primaryStage);
                        popup.close();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }else {
            	Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Input Error");
                alert.setHeaderText(null);
                alert.setContentText("Content cannot be empty!");
                alert.showAndWait();
            }
        });
        popupLayout.getChildren().addAll(new Label("Edit Answer:"), editInput, saveButton);
        Scene popupScene = new Scene(popupLayout, 500, 300);
        popup.setScene(popupScene);
        popup.show();
    }
    
    private void deleteAnswer(User user, Answer answer, ListView<HBox> answerListView, Stage primaryStage) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to remove your name from this answer?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(response -> {
        
        Label thisLabel = new Label(" ");
        thisLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        	if (response == ButtonType.YES) {
        		if(user.isAdmin()) {
        			try {
        				databaseHelper.deleteAnswerPermanently(answer.getAnswerId());
        				loadAnswers(answerListView, primaryStage);
        				//thisLabel.setText("Answer permanently deleted 😊");
        			}catch (SQLException ex) {
                        ex.printStackTrace();
                    }
        			
        		}else {
                	try {
                		databaseHelper.disconnectAnswerFromUser(answer.getAnswerId(), user.getUserName());
                		loadAnswers(answerListView, primaryStage); 
                		//thisLabel.setText("Answer disconnected successfully 😊");
                	} catch (SQLException ex) {
                    ex.printStackTrace();
                	}
        		}
            }
        });
    }
    
    

    
    
}

