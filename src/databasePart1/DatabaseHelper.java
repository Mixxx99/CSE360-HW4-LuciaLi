package databasePart1;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import aboutReviewer.Review;
import aboutReviewer.ReviewerRequest;
import aboutStaff.StaffMessage;
import javafx.collections.*;

import application.User;
import application.Question;
import application.RoleList;
import application.Answer;
import application.Messages;
import application.Reply;



/**
 * The DatabaseHelper class is responsible for managing the connection to the database,
 * performing operations such as user registration, login validation, and handling invitation codes.
 */
public class DatabaseHelper {

	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/FoundationDatabase";  

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 

	private Connection connection = null;
	private Statement statement = null; 


	/**
	 * Connects to the database and creates tables as needed.
	 * @throws SQLException
	 */
	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			System.out.println("Connecting to database...");
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement(); 
			
			//ADD new tag and tagger column
			//statement.execute("ALTER TABLE questions ADD COLUMN tag VARCHAR(255);");
			//statement.execute("ALTER TABLE questions ADD COLUMN tagger VARCHAR(255);");
			
			// You can use this command to clear the database and restart from fresh.
			//statement.execute("DROP ALL OBJECTS");
		//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!	
			
			createTables();  // Create the necessary tables if they don't exist
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}

	/**
	 * Creates the database tables. These hold: user information, invitation codes,
	 * questions, answers, messages, and requests to become a reviewer.
	 * @throws SQLException
	 */
	private void createTables() throws SQLException {
		System.out.println("Creating tables...");
		String userTable = "CREATE TABLE IF NOT EXISTS cse360users ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
		        + "userName VARCHAR(255) UNIQUE, "
		        + "password VARCHAR(255), "
		        // Roles
		        + "isAdmin BOOLEAN DEFAULT FALSE, "
		        + "isStaff BOOLEAN DEFAULT FALSE, "
		        + "isInstructor BOOLEAN DEFAULT FALSE, "
		        + "isStudent BOOLEAN DEFAULT FALSE, "
		        + "isReviewer BOOLEAN DEFAULT FALSE, "
		        //////////////////////////////
		        + "email VARCHAR(255), "  
		        + "name VARCHAR(255))";
		
		
		statement.execute(userTable);
		
		// Create the invitation codes table
	    String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
	            + "code VARCHAR(10) PRIMARY KEY, "
		    ///////////////////////////////////////
		    /// Roles
	            + "isAdmin BOOLEAN DEFAULT FALSE, "
		        + "isStaff BOOLEAN DEFAULT FALSE, "
		        + "isInstructor BOOLEAN DEFAULT FALSE, "
		        + "isStudent BOOLEAN DEFAULT FALSE, "
		        + "isReviewer BOOLEAN DEFAULT FALSE, "
	    	    ///////////////////////////////////////
	            + "isUsed BOOLEAN DEFAULT FALSE)";
	    statement.execute(invitationCodesTable);
	    
		////////////////////////////////////////////////////////
		///table for questions
	    String Questiontable = "CREATE TABLE IF NOT EXISTS questions ("
	    		+ "question_id INT AUTO_INCREMENT PRIMARY KEY,"
	    		+ "userName VARCHAR(255) DEFAULT NULL,"
	    		+ "questionTitle VARCHAR(255),"
	    		+ "questionContent TEXT,"
	    		+ "tag VARCHAR(255), "       // NEW: Staff's Tag content
	    		+ "tagger VARCHAR(255),"	 // NEW: Staff's name while tagging
	    		+ "FOREIGN KEY (userName) REFERENCES cse360users(userName) ON DELETE SET NULL"
	    		+ ")";
	    statement.execute(Questiontable);
		
		////////////////////////////////////////////////////////
		///table for answer 
	    String AnswersTable = "CREATE TABLE IF NOT EXISTS answers ("
	            + "answer_id INT AUTO_INCREMENT PRIMARY KEY, "
	            + "question_id INT, "
	            + "answerContent TEXT, "
	            + "userName VARCHAR(255) DEFAULT NULL, "
	            + "FOREIGN KEY (question_id) REFERENCES questions(question_id) ON DELETE CASCADE,"
	            + "FOREIGN KEY (userName) REFERENCES cse360users(userName) ON DELETE SET NULL"
	            + ")"; 
	    statement.execute(AnswersTable);


		//table for the messages
	    //drop table for checking
	    //String dropMessagesTable = "DROP TABLE IF EXISTS messages";
	    // statement.execute(dropMessagesTable);
	    
	    String MessagesTable = "CREATE TABLE IF NOT EXISTS messages ("
	            + "message_id INT AUTO_INCREMENT PRIMARY KEY, "
	            + "question_id INT, "
	            + "userName VARCHAR(255) DEFAULT NULL, "
	            + "messageContent TEXT, "
	            + "FOREIGN KEY (question_id) REFERENCES questions(question_id) ON DELETE CASCADE,"
	            + "FOREIGN KEY (userName) REFERENCES cse360users(userName) ON DELETE SET NULL"
	            + ")"; 
	    statement.execute(MessagesTable);
	    
	    //table for requests to become a reviewer
	    String reviewerRequestTable = "CREATE TABLE IF NOT EXISTS reviewerRequests ("
	    		+ "userName VARCHAR(255) DEFAULT NULL,"
	    		+ "requestContent TEXT,"
	    		+ "FOREIGN KEY (userName) REFERENCES cse360users(userName) ON DELETE SET NULL"
	    		+ ")";
	    statement.execute(reviewerRequestTable);
	    
	    //////////////////////////////////////////////////////////////
	    //table for reviewList, reviews connected with reviewers.
	    String reviewTable = "CREATE TABLE IF NOT EXISTS reviews ("
	            + "review_id INT AUTO_INCREMENT PRIMARY KEY, "
	            + "question_id INT, "
	            + "userName VARCHAR(255) DEFAULT NULL, "
	            + "reviewContent TEXT, "
	            + "FOREIGN KEY (question_id) REFERENCES questions(question_id) ON DELETE CASCADE, "
	            + "FOREIGN KEY (userName) REFERENCES cse360users(userName) ON DELETE SET NULL"
	            + ")";
	    statement.execute(reviewTable);
	    
	    ///////////////////////////////////////////////////////////////
	    ///table for staff's private messages(with other staff)
	    String staffMessagesTable = "CREATE TABLE IF NOT EXISTS staffMessages ("
        + "message_id INT AUTO_INCREMENT PRIMARY KEY, "
        + "sender VARCHAR(255) NOT NULL, "
        + "recipient VARCHAR(255) NOT NULL, "
        + "messageContent TEXT, "
        + "sentTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
        + ")";
	    statement.execute(staffMessagesTable);

	}
    
    
    public void disconnectAnswerFromUser(int answerId, String userName) throws SQLException {
        String updateQuery = "UPDATE answers SET userName = 'unknown' WHERE answer_id = ? AND userName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
            pstmt.setInt(1, answerId);
            pstmt.setString(2, userName); 
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Answer ID " + answerId + " has been disconnected from user: " + userName);
            } else {
                System.out.println("Error: Could not disconnect answer. Maybe it's not your answer?");
            }
        }
        
        
    }
    
    public void deleteAnswerPermanently(int answerId) throws SQLException {
        String deleteQuery = "DELETE FROM answers WHERE answer_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
            pstmt.setInt(1, answerId);
            int rowsDeleted = pstmt.executeUpdate();
            
            if (rowsDeleted > 0) {
                System.out.println("Answer ID " + answerId + " has been permanently deleted.");
            } else {
                System.out.println("Error: Answer ID " + answerId + " does not exist or has already been deleted.");
            }
        }
    }
    
    public void disconnectQuestionFromUser(int questionID, String userName) throws SQLException {
        String updateQuery = "UPDATE questions SET userName = 'unknown' WHERE question_id = ? AND userName = ?";
        
    	String checkUnknownUser = "SELECT COUNT(*) FROM cse360users WHERE userName = 'unknown'";
    	try (Statement stmt = connection.createStatement();
    	     ResultSet rs = stmt.executeQuery(checkUnknownUser)) {
    	    if (rs.next() && rs.getInt(1) == 0) {
    	        throw new SQLException("Error: 'unknown' user does not exist in cse360users.");
    	    }
    	}

        try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
            pstmt.setInt(1, questionID);
            pstmt.setString(2, userName);

            int updated = pstmt.executeUpdate();
            if (updated > 0) {
                System.out.println("Question ID " + questionID + " has been disconnected from user: " + userName);
            } else {
                System.out.println("Error: Could not disconnect question. Maybe it's not your question?");
            }
        }
    }
    

    
    public boolean deleteQuestionPermanently(int questionID) throws SQLException {
    	

        String deleteQuery = "DELETE FROM questions WHERE question_id = ?";

        
        
        try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
            pstmt.setInt(1, questionID);
            int rowsDeleted = pstmt.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("Question ID " + questionID + " has been permanently deleted.");
            } else {
                System.out.println("Error: Question ID " + questionID + " does not exist or has already been deleted.");
            }
            
            return rowsDeleted > 0;
        }
    }
	
    public void deleteUser(User user) throws SQLException {

        String deleteQuery = "DELETE FROM cse360users WHERE userName = ?";
        
        
        try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
            pstmt.setString(1, user.getUserName());
            int rowsDeleted = pstmt.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("User " + user.getUserName() + " has been deleted.");
            } else {
                System.out.println("Error: User " + user.getUserName() + " does not exist or has already been deleted.");
            }
        }
    }
	
	
    public boolean updateAnswer(int answerId, String newContent, User user) throws SQLException {
        RoleList userRoles = user.getRoleList();
        String userName = user.getUserName();
        String checkQuery = "SELECT COUNT(*) FROM answers WHERE answer_id = ? AND (userName = ? OR ? = TRUE)";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
            checkStmt.setInt(1, answerId);
            checkStmt.setString(2, userName);
            checkStmt.setBoolean(3, userRoles.getIsAdmin());  
            
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                System.out.println("Error: You are neither the author nor an admin!");
                return false;
            }
        }
        String updateQuery = "UPDATE answers SET answerContent = ? WHERE answer_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
            pstmt.setString(1, newContent);
            pstmt.setInt(2, answerId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0; 
        }
    }

	/**
	 * Updates the given question as specified. It is the caller's responsibility to
	 * determine whether the user performing the edit actually has permission to edit
	 * that question.
	 * @param questionId
	 * @param newTitle
	 * @param newContent
	 * @return whether the question was able to be updated. A false return value may mean
	 * that the question with that id does not exist in the database.
	 * @throws SQLException
	 */
	public boolean updateQuestion(int questionId, String newTitle, String newContent) throws SQLException {
	    // Perform UPDATE
	    String updateQuery = "UPDATE questions SET questionTitle = ?, questionContent = ? WHERE question_id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
	        pstmt.setString(1, newTitle);
	        pstmt.setString(2, newContent);
	        pstmt.setInt(3, questionId);

	        int rowsAffected = pstmt.executeUpdate();
	        return rowsAffected > 0;
	    }
	}

	/**
	 * Checks whether the database is empty (besides the unknown user).
	 * @return whether the database is empty.
	 * @throws SQLException
	 */
	public boolean isDatabaseEmpty() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM cse360users WHERE NOT userName = 'unknown'";
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			return resultSet.getInt("count") == 0;
		}
		return true;
	}
	
	/**
	 * Adds the given user to the database. All of their fields will be copied over.
	 * @param user the user to add.
	 * @throws SQLException
	 */
	public void register(User user) throws SQLException {
	    String insertUser = "INSERT INTO cse360users (userName, password, email, name, "
	    		+ "isAdmin, isInstructor, isStaff, isStudent, isReviewer) "
	    		+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
	        pstmt.setString(1, user.getUserName());
	        pstmt.setString(2, user.getPassword());
	        pstmt.setString(3, user.getEmail());
	        pstmt.setString(4, user.getName());
	        
	        //roles
	        pstmt.setBoolean(5, user.isAdmin());
	        pstmt.setBoolean(6, user.isInstructor());
	        pstmt.setBoolean(7, user.isStaff());
	        pstmt.setBoolean(8, user.isStudent());
	        pstmt.setBoolean(9, user.isReviewer());

	        pstmt.executeUpdate();
	        System.out.println("✅ New user: " + user.getUserName() + " has been added.");
		addUnknownUser();

	    } catch (SQLException e) {
	        System.err.println("Database error: " + e.getMessage());
	        e.printStackTrace();
	    }
	}

	/**
	 * Adds a user with all fields 'unknown' and all roles FALSE. Even if a user knew
	 * about the unknown user, logging in as unknown wouldn't give them access to any
	 * features they shouldn't access.
	 * 
	 * The unknown user must be added after the first user to ensure that the first user
	 * gets the first user screen.
	 * @throws SQLException
	 */
	private void addUnknownUser() throws SQLException {
	    String checkUnknownUser = "SELECT COUNT(*) FROM cse360users WHERE userName = 'unknown'";
	    try (Statement stmt = connection.createStatement();
	         ResultSet rs = stmt.executeQuery(checkUnknownUser)) {
	        if (rs.next() && rs.getInt(1) == 0) { 
	            String insertUnknownUser = "INSERT INTO cse360users (userName, password, email, name,"
	            		+ " isAdmin, isStudent, isStaff, isInstructor, isReviewer) VALUES "
	            		+ "('unknown', 'unknown', 'unknown@example.com', 'Unknown', FALSE, FALSE, "
	            		+ "FALSE, FALSE, FALSE)";
	            stmt.execute(insertUnknownUser);
	            System.out.println("✅ 'unknown' user has been added.");
	        }
	    }
	}
	
	public int addNewQuestion(Question question) throws SQLException {
	    String insertQuery = "INSERT INTO questions (userName, questionTitle, questionContent) VALUES (?, ?, ?)";
	    int generatedId = -1;

	    try (PreparedStatement pstmt = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
	        pstmt.setString(1, question.getUserName());
	        pstmt.setString(2, question.getTitle());
	        pstmt.setString(3, question.getContent());

	        pstmt.executeUpdate();
	        
	 

	        //Retrieve the generated question_id
	        ResultSet rs = pstmt.getGeneratedKeys();
	        if (rs.next()) {
	            generatedId = rs.getInt(1);
	            System.out.println("✅ New Question: " + question.getTitle() +" have been created. Added by: " + question.getUserName() + ".");
	            System.out.println("✅ New Question ID: " + generatedId);
	           
	        }
	    }
	    return generatedId; //Return the correct question_id
	}

///new answers in the database
	public int addAnswer(Answer answer) throws SQLException {
	    String insertQuery = "INSERT INTO answers (question_id, userName, answerContent) VALUES (?, ?, ?)";
	    int generatedId = -1; 

	    try (PreparedStatement pstmt = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
	        pstmt.setInt(1, answer.getQuestionId());
	        pstmt.setString(2, answer.getUserName());
	        pstmt.setString(3, answer.getAnswerContent());

	        pstmt.executeUpdate();

	        ResultSet rs = pstmt.getGeneratedKeys();
	        if (rs.next()) {
	            generatedId = rs.getInt(1);
	            answer.setAnswerId(generatedId);
	            System.out.println("✅ New Answer added. ");
	            System.out.println("✅ New Answer ID: " + generatedId);
	        }
	    }
	    return generatedId;
	}
		
	/**
	 * Validates a user's login credentials.
	 * @param user the user to validate. The object must have a userName and a password.
	 * @return whether the username and password match the information in the database.
	 * @throws SQLException
	 */
	public boolean login(User user) throws SQLException {
		String query = "SELECT * FROM cse360users WHERE userName = ? AND password = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next();
			}
		}
	}
	
	/**
	 * Determines whether a given user is in the database.
	 * @param userName the user to query about.
	 * @return whether the user exists.
	 */
	public boolean doesUserExist(String userName) {
	    String query = "SELECT COUNT(*) FROM cse360users WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            // If the count is greater than 0, the user exists
	            return rs.getInt(1) > 0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false; // If an error occurs, assume user doesn't exist
	}
	
	/**
	 * Retrieves the roles of a user from the database using their UserName.
	 * @param userName the userName to use in the query.
	 * @return a RoleList with that user's roles. The RoleList will need to be associated
	 * with the User object but is otherwise ready to go.
	 */
	public RoleList getUserRoleList(String userName) {
	    String query = "SELECT * FROM cse360users WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            boolean[] roles = new boolean[5];
	            roles[0] = rs.getBoolean("isAdmin");
	            roles[1] = rs.getBoolean("isStaff");
	            roles[2] = rs.getBoolean("isInstructor");
	            roles[3] = rs.getBoolean("isStudent");
	            roles[4] = rs.getBoolean("isReviewer");
	            System.out.println("Roles of " + userName + ": [" + roles[0] + ", "
	            		+ roles[1] + ", " + roles[2] + ", " + roles[3] + ", "
	            		+ roles[4] + "]");
	            return new RoleList(null, this, roles);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null; // If no user exists or an error occurs
	}
	
	///get all Questions 
	public List<Question> getAllQuestions() throws SQLException {
	    List<Question> questions = new ArrayList<>();
	    String query = "SELECT question_id, userName, questionTitle, questionContent, tag, tagger FROM questions";

	    try (PreparedStatement pstmt = connection.prepareStatement(query);
	         ResultSet rs = pstmt.executeQuery()) {
	        
	        while (rs.next()) {
	            questions.add(new Question(
	                rs.getInt("question_id"),
	                rs.getString("userName"),
	                rs.getString("questionTitle"),
	                rs.getString("questionContent"),
	                rs.getString("tag"),
	                rs.getString("tagger")
	            ));
	        }
	    }
	    return questions;
	}

	///get all answers of one question!!
	public List<Answer> getAnswersForQuestion(int questionId) throws SQLException {
	    List<Answer> answers = new ArrayList<>();
	    String query = "SELECT answer_id, question_id, userName, answerContent FROM answers WHERE question_id = ?";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, questionId);
	        ResultSet rs = pstmt.executeQuery();

	        while (rs.next()) {
	            answers.add(new Answer(
	                rs.getInt("answer_id"),
	                rs.getInt("question_id"),
	                rs.getString("userName"),
	                rs.getString("answerContent")
	            ));
	        }
	    }
	    return answers;
	}
	
	/**
	 * Generates an invitation code, which will be associated with the roles given by the
	 * RoleList in the database.
	 * @param roles the roles to associate with the code.
	 * @return the generated invitation code.
	 */
	public String generateInvitationCode(RoleList roles) {
	    String code = UUID.randomUUID().toString().substring(0, 4); // Generate a random 4-character code
		String query = "INSERT INTO InvitationCodes (code, isAdmin, isStaff, "
				+ "isInstructor, isStudent, isReviewer) VALUES (?,?,?,?,?,?)";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.setBoolean(2, roles.getIsAdmin());
	        pstmt.setBoolean(3, roles.getIsStaff());
	        pstmt.setBoolean(4, roles.getIsInstructor());
	        pstmt.setBoolean(5, roles.getIsStudent());
	        pstmt.setBoolean(6, roles.getIsReviewer());
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return code;
	}
	
	/**
	 * Checks that an invitation code exists and is unused. Marks it as used if it exists.
	 * @return whether the code was valid.
	 */
	public boolean validateInvitationCode(String code) {
	    String query = "SELECT * FROM InvitationCodes WHERE code = ? AND isUsed = FALSE";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            // Mark the code as used
	            markInvitationCodeAsUsed(code);
	            return true;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}

	/**
	 * Gets one user's roles as assigned by the admin using their invitation code.
	 * @param code the invitation code.
	 * @return a RoleList object representing the roles.
	 * @throws SQLException
	 */
	public RoleList getRolesByInvitationCode(String code) throws SQLException {
		String query = "SELECT * FROM InvitationCodes WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            boolean[] roles = new boolean[5];
	            roles[0] = rs.getBoolean("isAdmin");
	            roles[1] = rs.getBoolean("isStaff");
	            roles[2] = rs.getBoolean("isInstructor");
	            roles[3] = rs.getBoolean("isStudent");
	            roles[4] = rs.getBoolean("isReviewer");
	            System.out.println("Roles with code " + code + ": [" + roles[0] + ", "
	            		+ roles[1] + ", " + roles[2] + ", " + roles[3] + ", "
	            		+ roles[4] + "]");
	            return new RoleList(null, this, roles);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null; // If no user exists or an error occurs
	}
	
	/**
	 * isUsed is set to true for the given code.
	 * @param code
	 */
	private void markInvitationCodeAsUsed(String code) {
	    String query = "UPDATE InvitationCodes SET isUsed = TRUE WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	// Closes the database connection and statement.
	public void closeConnection() {
		try{ 
			if(statement!=null) statement.close(); 
		} catch(SQLException se2) { 
			se2.printStackTrace();
		} 
		try { 
			if(connection!=null) connection.close(); 
		} catch(SQLException se){ 
			se.printStackTrace(); 
		} 
	}

	/**
	 * Collects all of the users (except 'unknown') in the database and puts them into
	 * an ObservableList.
	 * @param currentUser the user whose "isCurrentUser" attribute should be set to true.
	 * This functionality will be skipped without error if the currentUser is null.
	 * @return the list of users as User objects.
	 */
	public ObservableList<User> getUsers(User currentUser) {
    	ObservableList<User> users = FXCollections.observableArrayList();
    	String query = "SELECT * FROM cse360users WHERE NOT userName = 'unknown'";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        ResultSet rs = pstmt.executeQuery();
	        
	        while (rs.next()) {
                User user = new User(rs.getString("userName"), "", null,
                        rs.getString("email"), rs.getString("name"));
                if (currentUser != null && user.getUserName().equals(currentUser.getUserName())) {
                    user.setCurrentUser();
                }
                
                RoleList roles = getUserRoleList(user.getUserName());
                user.addRoleList(roles);
                users.add(user);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return users;
    }
	
	/**
	 * Updates the given role of the given user to the given value.
	 * @param userName the user whose role should be changed.
	 * @param roleName one of these: "isAdmin", "isReviewer", "isStudent", "isStaff", "isInstructor".
	 * @param newValue the new value of the given role.
	 */
	public void updateUserRole(String userName, String roleName, boolean newValue) {
		String query = "UPDATE cse360users SET " + roleName + " = ? WHERE userName = ?;";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setBoolean(1, newValue);
	        pstmt.setString(2, userName);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	//Delete users, questions, answers for test.
	public void deleteAllUsers() throws SQLException {
	    try (Statement stmt = connection.createStatement()) {

	        stmt.execute("SET REFERENTIAL_INTEGRITY FALSE");
		// delete all data
	        stmt.execute("DELETE FROM answers");
	        stmt.execute("DELETE FROM questions");
	        stmt.execute("DELETE FROM cse360users");
	        // restart AUTO_INCREMENT
	        stmt.execute("ALTER TABLE answers ALTER COLUMN answer_id RESTART WITH 1");
	        stmt.execute("ALTER TABLE questions ALTER COLUMN question_id RESTART WITH 1");
	        stmt.execute("ALTER TABLE cse360users ALTER COLUMN id RESTART WITH 1");

	        stmt.execute("SET REFERENTIAL_INTEGRITY TRUE");
	 

	        System.out.println(" All users, all questions, and all answers have been deleted.");
	        System.out.println(" AUTO_INCREMENT has been reset for all tables.");

	    } catch (SQLException e) {
	        System.err.println("SQL Error: " + e.getMessage());
	        throw e;
	    }
	}

	public void markResolved(Question question, boolean isResolved) throws SQLException {
		
		int questionID = question.getQuestionId();
		String questionTitle = question.getTitle();
		String userName = question.getUserName();
		String content = question.getContent();
		String resolutionText = "   [RESOLVED]";
		int length = questionTitle.length();
		
		if(questionTitle.contains(resolutionText)) {
			isResolved = false;
		}
		
		if(isResolved == true) {
			questionTitle += resolutionText;
			updateQuestion(questionID, questionTitle, content);
			System.out.println("Successfully Marked As Resolved.");
		} else if (isResolved == false){
			questionTitle = questionTitle.substring(0, length-13);
			//questionTitle = questionTitle.replaceAll("\\b" + resolutionText + "\\b", "").trim();
			//questionTitle = questionTitle.replaceAll("(", "").trim();
			//questionTitle = questionTitle.replaceAll(")", "").trim();
			
			updateQuestion(questionID, questionTitle, content);
			System.out.println("Successfully Removed Resolution Mark.");
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////
	
	 public boolean editMessage(int questionID, String newContent, User user) throws SQLException {
	        boolean isAdmin = user.isAdmin();
	        String userName = user.getUserName();
	        String checkQuery = "SELECT COUNT(*) FROM messages WHERE message_id = ? AND (userName = ? OR ? = TRUE)";
	        try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
	            checkStmt.setInt(1, questionID);
	            checkStmt.setString(2, userName);
	            checkStmt.setBoolean(3, isAdmin);  
	            
	            ResultSet rs = checkStmt.executeQuery();
	            if (rs.next() && rs.getInt(1) == 0) {
	                System.out.println("Error: You are neither the author nor an admin!");
	                return false;
	            }
	        }
	        String updateQuery = "UPDATE messages SET messageContent = ? WHERE message_id = ?";
	        try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
	            pstmt.setString(1, newContent);
	            pstmt.setInt(2, questionID);

	            int rowsAffected = pstmt.executeUpdate();
	            return rowsAffected > 0; 
	        }
	    }
	 
	 public List<Messages> getMessageForQuestion(int questionId) throws SQLException {
		    List<Messages> messages = new ArrayList<>();
		    String query = "SELECT message_id, question_id, userName, messageContent FROM messages WHERE question_id = ?";

		    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setInt(1, questionId);
		        ResultSet rs = pstmt.executeQuery();

		        while (rs.next()) {
		        	messages.add(new Messages(
		        		rs.getInt("message_id"),
		                rs.getInt("question_id"),
		                rs.getString("userName"),
		                rs.getString("messageContent")
		            ));
		        }
		    }
		    return messages;
		}
	 
	 public int sendMessage(Messages message) throws SQLException {
		    String insertQuery = "INSERT INTO messages (question_id, userName, messageContent) VALUES (?, ?, ?)";
		    int generatedId = -1; 

		    try (PreparedStatement pstmt = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
		        pstmt.setInt(1, message.getQuestionId());
		        pstmt.setString(2, message.getUserName());
		        pstmt.setString(3, message.getMessages());

		        pstmt.executeUpdate();

		        ResultSet rs = pstmt.getGeneratedKeys();
		        if (rs.next()) {
		            generatedId = rs.getInt(1);
		            message.setMessageId(generatedId);
		            System.out.println("✅ New Message added. ");
		            System.out.println("✅ New Message ID: " + generatedId);
		        }
		    }
		    return generatedId;
		}
	 
	 public void deleteAllMessage(int questionID) throws SQLException {
	        String deleteQuery = "DELETE FROM messages WHERE message_id = ?";

	        try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
	            pstmt.setInt(1, questionID);
	            int rowsDeleted = pstmt.executeUpdate();
	            
	            if (rowsDeleted > 0) {
	                System.out.println("Question ID " + questionID + " has been permanently deleted.");
	            } else {
	                System.out.println("Error: Question ID " + questionID + " does not exist or has already been deleted.");
	            }
	        }
	    }
	 
	 public void printAllMessages() throws SQLException {
		    String query = "SELECT * FROM messages";
		    
		    //print messages received as a form of checking
		    try (Statement stmt = connection.createStatement();
		         ResultSet rs = stmt.executeQuery(query)) {
		        System.out.println("Messages Table Data:");
		        while (rs.next()) {
		            System.out.println("ID: " + rs.getInt("message_id") + ", QuestionID: " + rs.getInt("question_id") +  ", User: " + rs.getString("userName") + ", Content: " + rs.getString("messageContent"));
		        }
		    }
		}
	 
	 public void printTableSchema() throws SQLException {
		    String query = "SHOW COLUMNS FROM messages";
		    try (Statement stmt = connection.createStatement();
		         ResultSet rs = stmt.executeQuery(query)) {
		        System.out.println("Schema of messages table:");
		        while (rs.next()) {
		            System.out.println(rs.getString(1) + " - " + rs.getString(2));
		        }
		    }
		}

	 /**
	  * Inserts the given user's request and the given text into the reviewer requests table.
	  * @param userName the username of the user making the request
	  * @param requestText the user's explanation for their request
	  * @return a helpful error message, or null if the request was successfully added.
	  * @throws SQLException
	  */
	 public String addReviewerRequest(String userName, String requestText) throws SQLException {
		 //check whether user already has request pending
		 String checkQuery = "SELECT COUNT(*) FROM reviewerRequests WHERE userName = ?";
         try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
             checkStmt.setString(1, userName);
            
             ResultSet rs = checkStmt.executeQuery();
             if (rs.next() && rs.getInt(1) > 0) {
                 return "Error: You have already asked to become a reviewer!";
             }
         }
		 
		 String query = "INSERT INTO reviewerRequests (userName, requestContent) VALUES (?, ?)";
		 try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
			 pstmt.setString(1, userName);
			 pstmt.setString(2, requestText);
			 
			 int rowsChanged = pstmt.executeUpdate();
			 if (rowsChanged == 0) {
				 return "Error: A database error prevented your request from sending.";
			 }
			 return null;
		 }
	 }
	 
	 /**
	  * Deletes the reviewer request from the user with the given username from the database.
	  * @param userName the username of the user whose request is being denied.
	  * @return whether the request was successfully deleted.
	  * @throws SQLException
	  */
	 public boolean denyReviewerRequest(String userName) throws SQLException {
		 String query = "DELETE FROM reviewerRequests WHERE userName = ?";
		 try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
			 pstmt.setString(1, userName);
			 
			 int rowsChanged = pstmt.executeUpdate();
			 return rowsChanged > 0;
		 }
		 
	 }
	 
	 /**
	  * Adds the reviewer role to the given student's data and deletes their request.
	  * @param userName the userName of the student.
	  * @return whether the request was successfully processed.
	  * @throws SQLException
	  */
	 public boolean approveReviewerRequest(String userName) throws SQLException {
		 if (denyReviewerRequest(userName)) {
			 updateUserRole(userName, "isReviewer", true);
			 return true;
		 }
		 return false;
	 }
	 
	 /**
	  * Gets all of the reviewer requests from the database and puts them into 
	  * an array of ReviewerRequest objects.
	  * @return the array of reviewer requests.
	  * @throws SQLException
	  */
	 public ObservableList<ReviewerRequest> getReviewerRequests() throws SQLException {
		 ObservableList<ReviewerRequest> requests = FXCollections.observableArrayList();
		 String query = "SELECT * FROM reviewerRequests";
		 try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			 ResultSet rs = pstmt.executeQuery();
	        
			 while (rs.next()) {
				 ReviewerRequest request = new ReviewerRequest(rs.getString("userName"),
						 rs.getString("requestContent"));
                
				 requests.add(request);
			 }
			 return requests;
		 }
	 }
	 
	 /**
	  *	Store the reply of the question from other users.
	  * @questionId original question id
	  * @replyContent reply contents from others
	  * @userName name who wrote the reply
	  * @throws SQLException
	  */
	 public boolean submitReply(int questionId, String replyContent, String userName) throws SQLException {
	    String insertQuery = "INSERT INTO replys (question_id, replyContent, userName) VALUES (?, ?, ?)";

	    try (PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
	        pstmt.setInt(1, questionId);
	        pstmt.setString(2, replyContent);
	        pstmt.setString(3, userName);

	        int rowsAffected = pstmt.executeUpdate();
	        return rowsAffected > 0;
	    }
	}
	 
	public List<Reply> getRepliesForQuestion(int questionId) throws SQLException {
	    List<Reply> replies = new ArrayList<>();
	    String query = "SELECT reply_id, question_id, userName, replyContent FROM replys WHERE question_id = ?";
	
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		pstmt.setInt(1, questionId);
		ResultSet rs = pstmt.executeQuery();
	
		while (rs.next()) {
			replies.add(new Reply(
				rs.getInt("reply_id"),
			rs.getInt("question_id"),
			rs.getString("userName"),
			rs.getString("replyContent")
		    ));
		}
	    }
	    return replies;
	}
	
	/**
	 * START REVIEW database helper
	 *While adding reviews, connect reviews with reviewers
	 */
	public boolean submitReview(int questionId, String reviewContent, String userName) throws SQLException {
	    String insertReview = "INSERT INTO reviews (question_id, userName, reviewContent) VALUES (?, ?, ?)";
	    try (PreparedStatement pstmt = connection.prepareStatement(insertReview)) {
	        pstmt.setInt(1, questionId);
	        pstmt.setString(2, userName);
	        pstmt.setString(3, reviewContent);
	        return pstmt.executeUpdate() > 0;
	    }
	}

	//Get all reviews of a question
	public List<Review> getReviewsForQuestion(int questionId) throws SQLException {
	    List<Review> reviews = new ArrayList<>();
	    String query = "SELECT review_id, question_id, userName, reviewContent FROM reviews WHERE question_id = ?";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, questionId);
	        ResultSet rs = pstmt.executeQuery();

	        while (rs.next()) {
	            reviews.add(new Review(
	                rs.getInt("review_id"),
	                rs.getInt("question_id"),
	                rs.getString("userName"),
	                rs.getString("reviewContent")
	            ));
	        }
	    }
	    return reviews;
	}
	
	// Get all reviews of current reviewer
	// 在 DatabaseHelper.java 中添加：
	public List<Review> getAllReviewsByUser(String userName) throws SQLException {
	    List<Review> reviews = new ArrayList<>();
	    String query = "SELECT review_id, question_id, userName, reviewContent FROM reviews WHERE userName = ?";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        while (rs.next()) {
	            reviews.add(new Review(
	                rs.getInt("review_id"),
	                rs.getInt("question_id"),
	                rs.getString("userName"),
	                rs.getString("reviewContent")
	            ));
	        }
	    }
	    return reviews;
	}

	
	// delete review
	public boolean deleteReview(int reviewId) throws SQLException {
	    String query = "DELETE FROM reviews WHERE review_id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, reviewId);
	        return pstmt.executeUpdate() > 0;
	    }
	}
	
	public Question getQuestionById(int questionId) throws SQLException {
	    String query = "SELECT question_id, userName, questionTitle, questionContent, tag, tagger FROM questions WHERE question_id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, questionId);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            return new Question(
	                rs.getInt("question_id"),
	                rs.getString("userName"),
	                rs.getString("questionTitle"),
	                rs.getString("questionContent"),
	                rs.getString("tag"),
	                rs.getString("tagger")
	            );
	        }
	    } catch (SQLException e) {
	        System.err.println("Database error while fetching question by ID: " + e.getMessage());
	        e.printStackTrace();
	    }
	    return null;
	}
	
	public boolean updateReview(int reviewId, String newContent) throws SQLException {
	    String query = "UPDATE reviews SET reviewContent = ? WHERE review_id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, newContent);
	        pstmt.setInt(2, reviewId);
	        int rowsAffected = pstmt.executeUpdate();
	        return rowsAffected > 0;
	    } catch (SQLException e) {
	        System.err.println("Database error while updating review: " + e.getMessage());
	        e.printStackTrace();
	        return false;
	    }
	}
	
	
	//kaishi
	public List<User> getReviewerList() throws SQLException {
	    List<User> reviewers = new ArrayList<>();
	    String query = "SELECT * FROM cse360users WHERE isReviewer = TRUE";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        ResultSet rs = pstmt.executeQuery();
	        while (rs.next()) {
	            // 此处构造 User 对象请根据你原有的 User 类构造器调整
	            User reviewer = new User(rs.getString("userName"), rs.getString("password"), null, rs.getString("email"), rs.getString("name"));
	            // 可选：若有需要，可以将 reviewer role 数据读取后保存到 User 对象里
	            reviewers.add(reviewer);
	        }
	    }
	    return reviewers;
	}
	
	/**
	 * Retrieves a list of all staff usernames.
	 * @return a List of staff usernames.
	 * @throws SQLException if a database error occurs.
	 */
	public List<String> getStaffList() throws SQLException {
	    List<String> staffNames = new ArrayList<>();
	    String query = "SELECT userName FROM cse360users WHERE isStaff = TRUE";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        ResultSet rs = pstmt.executeQuery();
	        while (rs.next()) {
	            staffNames.add(rs.getString("userName"));
	        }
	    }
	    return staffNames;
	}
	
	
	public boolean banReviewer(String reviewerUserName) throws SQLException {
	    String query = "UPDATE cse360users SET isReviewer = FALSE WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, reviewerUserName);
	        return pstmt.executeUpdate() > 0;
	    }
	}
	
	/**
	 * Submits a private message from one staff member to another.
	 * @param sender the username of the sender.
	 * @param recipient the username of the recipient.
	 * @param message the message content.
	 * @return true if the message is inserted successfully, false otherwise.
	 * @throws SQLException if a database error occurs.
	 */
	public boolean submitStaffMessage(String sender, String recipient, String message) throws SQLException {
	    String insertQuery = "INSERT INTO staffMessages (sender, recipient, messageContent) VALUES (?, ?, ?)";
	    try (PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
	        pstmt.setString(1, sender);
	        pstmt.setString(2, recipient);
	        pstmt.setString(3, message);
	        int rowsAffected = pstmt.executeUpdate();
	        return rowsAffected > 0;
	    }
	}
	
	public List<StaffMessage> getConversation(String user1, String user2) throws SQLException {
	    List<StaffMessage> messages = new ArrayList<>();
	    String query = "SELECT message_id, sender, recipient, messageContent, sentTime " +
	                   "FROM staffMessages " +
	                   "WHERE (sender = ? AND recipient = ?) " +
	                   "   OR (sender = ? AND recipient = ?) " +
	                   "ORDER BY sentTime ASC";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, user1);
	        pstmt.setString(2, user2);
	        pstmt.setString(3, user2);
	        pstmt.setString(4, user1);
	        ResultSet rs = pstmt.executeQuery();
	        while (rs.next()) {
	            messages.add(new StaffMessage(
	                rs.getInt("message_id"),
	                rs.getString("sender"),
	                rs.getString("recipient"),
	                rs.getString("messageContent"),
	                rs.getTimestamp("sentTime")
	            ));
	        }
	    }
	    return messages;
	      
	}
	
	
	/**
	 * Updates the tag for a specific question.
	 * @param questionId the ID of the question.
	 * @param tag the tag text.
	 * @return true if the update is successful, false otherwise.
	 * @throws SQLException if a database error occurs.
	 */
	public boolean tagQuestion(int questionId, String tag, String tagger) throws SQLException {
	    String query = "UPDATE questions SET tag = ?, tagger = ? WHERE question_id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, tag);
	        pstmt.setString(2, tagger);
	        pstmt.setInt(3, questionId);
	        return pstmt.executeUpdate() > 0;
	    }
	}

	



	
	
}
