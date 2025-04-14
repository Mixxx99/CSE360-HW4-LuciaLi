package application;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javafx.scene.control.*;



/**
 * The insert question page allows the user to create and submit a question. The question
 * contains a title and content.
 */
public class insertQuestion {
	
	private final DatabaseHelper databaseHelper;
	private final User user;
	

    public insertQuestion(DatabaseHelper databaseHelper, User user) {
        this.databaseHelper = databaseHelper;
        this.user = user; 
    }
    
    public void show(Stage primaryStage) {
    	VBox layout = new VBox(10);
	    layout.setStyle("-fx-alignment: center; -fx-padding: 10;");
	    
	    // Label to display Hello user
	    Label header = new Label("Insert your Question header here: ");
	    header.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
	    Label content = new Label("Insert your Question content here: ");
	    content.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
    	
	    //get userName
	    String userName =user.getUserName();
    	System.out.println(userName);
    	

    	//space to add header 
	    TextField headerInput = new TextField();
        headerInput.setPromptText("Enter question title...");
        
        
      //space to add question
        TextArea contentInput = new TextArea();
        contentInput.setPromptText("Enter question content...");
        contentInput.setPrefRowCount(5); 
        
        ////////// space to suggest related questions
	ListView<HBox> questionListView = new ListView<>();
	questionListView.setPrefWidth(100);
	questionListView.setPrefHeight(98);
	   
    	//button save 
        Button saveButton = new Button("Save");
        
        //button back
        Button backButton = new Button("Back");
        
        //message
        Label ErrorMessage = new Label(""); ;
        ErrorMessage.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

	List<String> practicle =  List.of("is", "a", "the", "some", "any", "just", "of", "in" , "on", "off", "or", "and");

	try {
        	List<Question> questions = databaseHelper.getAllQuestions();
        	HashMap<String, Integer> check = new HashMap<>();
        	for (Question q : questions) {
        		check.put(q.getTitle(), 0);
        	}
        	
        	headerInput.textProperty().addListener((observable, oldValue, newValue) -> {
            	String[] words = newValue.trim().split("\\s+");
            	if (newValue.endsWith(" ") && words.length > 0) {
                    System.out.println("Input words in title: " + words[words.length - 1]); //print input words
                    for (Question q : questions) {
                    	if (practicle.contains(words[words.length - 1])) {
                    		continue;
                    	}
                    	else if ((q.getContent().contains(words[words.length - 1]) || q.getTitle().contains(words[words.length - 1])) && check.get(q.getTitle()) == 0) {
                			Label questionLabel = new Label(q.getTitle() + " (by " + q.getUserName() + ")");   
            	            Button viewButton = new Button("View");
            	            HBox questionItem = new HBox(10, questionLabel, viewButton);
            	            viewButton.setOnAction(e -> new AnswersList(databaseHelper, q, user).show(primaryStage));

            	            questionListView.getItems().add(questionItem);
            	            check.put(q.getTitle(), 1);
                		}
                	}
                }
    		});

            contentInput.textProperty().addListener((observable, oldValue, newValue) -> {
            	String[] words = newValue.trim().split("\\s+"); 
                if (newValue.endsWith(" ") && words.length > 0) {
                    System.out.println("Input words in content: " + words[words.length - 1]); 
                    for (Question q : questions) {
                    	if (practicle.contains(words[words.length - 1])) {
                    		continue;
                    	}
                    	else if ((q.getContent().contains(words[words.length - 1]) || q.getTitle().contains(words[words.length - 1])) && check.get(q.getTitle()) == 0) {
                			Label questionLabel = new Label(q.getTitle() + " (by " + q.getUserName() + ")");   
            	            Button viewButton = new Button("View");
            	            HBox questionItem = new HBox(10, questionLabel, viewButton);
            	            viewButton.setOnAction(e -> new AnswersList(databaseHelper, q, user).show(primaryStage));

            	            questionListView.getItems().add(questionItem);
            	            check.put(q.getTitle(), 1);
                		}
                	}
                }
    		});
        	
        }catch (SQLException e){
        	System.err.println("Error showing question: " + e.getMessage());
        }
        
        
        //if button save create a question in database, link to Questions. 
        saveButton.setOnAction(a -> {
            	String title = headerInput.getText();
            	String contentText = contentInput.getText();

		if (!(title.isEmpty() || contentText.isEmpty())) {
		    try {
			List<Question> questions = databaseHelper.getAllQuestions();
                	for (Question q : questions) {
            			if (title.equals(q.getTitle()) || contentText.equals(q.getContent())) {
            				ErrorMessage.setText("You cannot copy other's question!");
                            		return;
            			}
            		}
			Question newQuestion = new Question(userName, title, contentText);
			databaseHelper.addNewQuestion(newQuestion);
			System.out.println("Question saved ~❤");
			
		    } catch (SQLException e) {
			System.err.println("Error saving question: " + e.getMessage());
		    }
 		    new QuestionList(databaseHelper, user).show(primaryStage);
	    	}
		else {
			ErrorMessage.setText("Title or content cannot be empty!");
                	return;
		}
            
            //go to questionList page
        });
        
        
        //back
        backButton.setOnAction(a -> {
        	new StudentHomePage(databaseHelper, user).show(primaryStage);
        });
       
	    	
    	
    	
	    //words & button
	    layout.getChildren().addAll(header,headerInput, content, contentInput, ErrorMessage, questionListView, saveButton, backButton);
	    
	    //scene set up
	    Scene userScene = new Scene(layout, 800, 400);
	    primaryStage.setScene(userScene);
	    primaryStage.setTitle("Add Question");
 
	    
	    }
    
    }
