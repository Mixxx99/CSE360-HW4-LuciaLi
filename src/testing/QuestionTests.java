package testing;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import application.Answer;
import application.Question;
import application.RoleList;
import application.User;
import databasePart1.DatabaseHelper;

/**
 * This class contains the tests for the DatabaseHelper that interact with the 
 * Question class.
 * 
 * It creates three test users because questions must be connected to a valid user.
 * At the end, it deletes these users so they don't show up when the application
 * is run. All questions created during the testing process are deleted at the end.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class QuestionTests {
	
	static DatabaseHelper databaseHelper = new DatabaseHelper();
	//don't actually need to store the entire user objects; usernames suffice.
	static String[] usernames = {"admin", "jazmin", "odysseus"};
	static Question q1 = new Question(usernames[1], "How do I see questions?",
			"I can't seem to access them. Does anyone know what I'm doing wrong?");
	static Question q2 = new Question(usernames[0], "What is a question?",
			"Is it a curiosity? A suspicion? A philosophical take?");
	static Question q3 = new Question(usernames[2], "What if I'm the monster?",
			"If I become the monster, will we be able to get home? Penelope...");

	    private User testUser;
	    private User adminUser;
	    private Question testQuestion;
	    private Answer testAnswer;
	
	/**
	 * Connects to the database and adds the test users to it.
	 * 
	 * Since this step is crucial to the validity of the other tests, if the database
	 * throws an exception, the entire testing program will exit with code -1.
	 */
	@BeforeAll
	private static void initialization() {
		boolean connected = false;
		try {
			RoleList admin = new RoleList();
			admin.setRoles("admin, student");
			RoleList student = new RoleList();
			student.setRoles("student");
			System.out.println("Creating users...");
			User adminU = new User(usernames[0], "password", admin);
			User jazmin = new User(usernames[1], "password", student);
			User odysseus = new User(usernames[2], "password", student);
			
			System.out.println("Now, to add the users to the database.");
			
			//connect to the database and make sure there are known users registered to use for the questions
			databaseHelper.connectToDatabase();
			connected = true;
			if (!databaseHelper.doesUserExist(usernames[1])) {
				databaseHelper.register(jazmin);
			}
			if (!databaseHelper.doesUserExist(usernames[0])) {
				databaseHelper.register(adminU);
			}
			if (!databaseHelper.doesUserExist(usernames[2])) {
				databaseHelper.register(odysseus);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			if (connected) cleanup();
			System.exit(-1);
		}
	}
	
	/**
	 * Adds three questions to the database. Checks that this worked by comparing
	 * their ids against the three most recently added questions' ids.
	 */
	@Test
	@Order(1)
	public void addQuestionTest() {
		List<Question> questions = null;
		
		try {
			q1.setQuestionId(databaseHelper.addNewQuestion(q1));
			q2.setQuestionId(databaseHelper.addNewQuestion(q2));
			q3.setQuestionId(databaseHelper.addNewQuestion(q3));
			questions = databaseHelper.getAllQuestions();
		} catch (SQLException e) {
			e.printStackTrace();
			fail("Failed to add a question or get questions--database error.");
		}
		
		int numQuestions = questions.size(); //the fail should prevent it from getting this far if this isn't initialized
		assertEquals(questions.get(numQuestions - 1).getQuestionId(), q3.getQuestionId(),
				"Question 3's id is wrong.");
		assertEquals(questions.get(numQuestions - 2).getQuestionId(), q2.getQuestionId(),
				"Question 2's id is wrong.");
		assertEquals(questions.get(numQuestions - 3).getQuestionId(), q1.getQuestionId(),
				"Question 1's id is wrong.");
	}
	
	/**
	 * Checks that a question is correctly updated in the database.
	 */
	@Test
	@Order(2)
	public void updateQuestionTest() {
		try {
			assertTrue(databaseHelper.updateQuestion(q3.getQuestionId(), "Who are you?", "Nah, don't "
					+ "be modest, I know you're a goddess you are Athena."),
					"Question was not updated.");
			List<Question> questions = databaseHelper.getAllQuestions();
			//assert that content has changed
			String content = questions.get(questions.size() - 1).getContent();
			assertEquals("Nah, don't be modest, I know you're a goddess you are Athena.", content,
					"Question content is incorrect after update.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Checks that a question can be disconnected from its author. Ensures that the 
	 * question remains unchanged except for the username, which should now be "unknown".
	 */
	@Test
	@Order(3)
	public void disconnectQuestionTest() {
		try {
			databaseHelper.disconnectQuestionFromUser(q1.getQuestionId(), q1.getUserName());
			List<Question> questions = databaseHelper.getAllQuestions();
			//assert that content has changed
			Question disconnectedQ = questions.get(questions.size() - 3);
			assertEquals("unknown", disconnectedQ.getUserName(), "Username doesn't equal 'unknown'.");
			assertEquals(q1.getContent(), disconnectedQ.getContent(),
					"Question content is incorrect after disconnect.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Tests that a question that is unresolved is correctly marked as resolved.
	 */
	@Test
	@Order(4)
	public void markResolvedIfUnresolvedTest() {
		try {
			databaseHelper.markResolved(q2, true);
			List<Question> questions = databaseHelper.getAllQuestions();
			//assert that content has changed
			Question resolvedQ = questions.get(questions.size() - 2);
			assertTrue(resolvedQ.getTitle().contains("[RESOLVED]"), "Title doesn't contain '[RESOLVED]'.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Deletes all questions used in these tests, then checks that that they were
	 * actually deleted.
	 */
	@Test
	@Order(5)
	public void deleteQuestionTest() {
		try {
			assertTrue(databaseHelper.deleteQuestionPermanently(q1.getQuestionId()),
					"Question 1 was not deleted.");
			assertTrue(databaseHelper.deleteQuestionPermanently(q2.getQuestionId()),
					"Question 2 was not deleted.");
			assertTrue(databaseHelper.deleteQuestionPermanently(q3.getQuestionId()),
					"Question 3 was not deleted.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
    /**
     * Test 30: Add and Retrieve Answers.
     * Adds an answer to a question and checks if it appears in getAnswersForQuestion().
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    @DisplayName("Add and Retrieve Answers")
    void testAddAndRetrieveAnswers() throws SQLException {
        System.out.println(" ");
        System.out.println("Test30");
        Answer newAnswer = new Answer(testQuestion.getQuestionId(), "testUser", "Another test answer.");
        int answerId = databaseHelper.addAnswer(newAnswer);
        System.out.println("New Answer ID: " + answerId);
        assertTrue(answerId > 0);

        List<Answer> answers = databaseHelper.getAnswersForQuestion(testQuestion.getQuestionId());
        assertTrue(answers.stream().anyMatch(a -> a.getAnswerId() == answerId));
        System.out.println("Test 30: Succeed!");
    }

    
    /**
     * Test 29: Edit Answer (User).
     * Checks if a user can update their own answer.
     * Validates that the updated answer is stored correctly.
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    @DisplayName("Edit Answer user")
    void testEditAnswer1() throws SQLException {
        System.out.println(" ");
        System.out.println("Test 29");
        
      
        testQuestion = new Question("testUser", "Test Question Title", "This is a test question.");
        int questionId = databaseHelper.addNewQuestion(testQuestion);
        testQuestion.setQuestionId(questionId);
        System.out.println("New Question ID: " + questionId);

        testAnswer = new Answer(questionId, "testUser", "This is a test answer.");
        int answerId = databaseHelper.addAnswer(testAnswer);
        testAnswer.setAnswerId(answerId);
        System.out.println("New Answer ID: " + answerId);

        boolean success = databaseHelper.updateAnswer(testAnswer.getAnswerId(), "Updated Answer", testUser);
        System.out.println("Answer Update success: " + success);
        assertTrue(success);

        List<Answer> answers = databaseHelper.getAnswersForQuestion(testAnswer.getQuestionId());
        assertTrue(answers.stream().anyMatch(a -> a.getAnswerContent().equals("Updated Answer")));
        System.out.println("Test 29: Succeed!");
    }
    
    
    @Test
    @DisplayName("User Disconnects Answer")
    void testUserDisconnectsAnswer() throws SQLException {
        System.out.println(" ");
        System.out.println("Test 28");

        databaseHelper.disconnectAnswerFromUser(testAnswer.getAnswerId(), testUser.getUserName());
        System.out.println("Answer ID " + testAnswer.getAnswerId() + " disconnected from user.");

        List<Answer> answers = databaseHelper.getAnswersForQuestion(testAnswer.getQuestionId());
        assertTrue(answers.stream().anyMatch(a -> a.getUserName().equals("unknown")));
        System.out.println("Test 28: Succeed!");
    }

    @Test
    @DisplayName("Admin Deletes Answer Permanently")
    void testAdminDeletesAnswer() throws SQLException {
        System.out.println(" ");
        System.out.println("Test 27");

        Answer tempAnswer = new Answer(testQuestion.getQuestionId(), "testUser", "Temporary Answer");
        int tempAnswerId = databaseHelper.addAnswer(tempAnswer);
        System.out.println("Temporary Answer ID: " + tempAnswerId);

        databaseHelper.deleteAnswerPermanently(tempAnswerId);
        System.out.println("Temporary Answer ID " + tempAnswerId + " deleted.");

        List<Answer> answers = databaseHelper.getAnswersForQuestion(testQuestion.getQuestionId());
        assertFalse(answers.stream().anyMatch(a -> a.getAnswerId() == tempAnswerId));
        System.out.println("Test 27: Succeed!");
    }
	
	/**
	 * Deletes the test users from the database, then closes the connection to the
	 * database.
	 */
	@AfterAll
	private static void cleanup() {
		try {
			databaseHelper.deleteUser(new User(usernames[0], null, null));
			databaseHelper.deleteUser(new User(usernames[1], null, null));
			databaseHelper.deleteUser(new User(usernames[2], null, null));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		databaseHelper.closeConnection();
	}

}
