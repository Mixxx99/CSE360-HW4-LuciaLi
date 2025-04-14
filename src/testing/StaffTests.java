package testing;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.*;

import application.Question;
import application.RoleList;
import aboutStaff.StaffMessage;
import application.User;
import databasePart1.DatabaseHelper;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StaffTests {

    static DatabaseHelper db = new DatabaseHelper();
    static Question testQuestion;

    static final String reviewerUser = "reviewerUser";
    static final String staffSender = "staffSender";
    static final String staffRecipient = "staffRecipient";
    static final String taggerUser = "taggerUser";

    @BeforeAll
    public static void setup() {
        try {
            db.connectToDatabase();

            RoleList reviewerRole = new RoleList();
            reviewerRole.setRoles("student, reviewer");
            RoleList staffRole = new RoleList();
            staffRole.setRoles("staff");
            RoleList taggerRole = new RoleList();
            taggerRole.setRoles("staff");

            if (!db.doesUserExist(reviewerUser))
                db.register(new User(reviewerUser, "pass", reviewerRole));
            if (!db.doesUserExist(staffSender))
                db.register(new User(staffSender, "pass", staffRole));
            if (!db.doesUserExist(staffRecipient))
                db.register(new User(staffRecipient, "pass", staffRole));
            if (!db.doesUserExist(taggerUser))
                db.register(new User(taggerUser, "pass", taggerRole));

            testQuestion = new Question(taggerUser, "Need a tag", "Please tag this question.");
            int qid = db.addNewQuestion(testQuestion);
            testQuestion.setQuestionId(qid);

        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    @Test
    @Order(1)
    @DisplayName("Ban a reviewer")
    public void testBanReviewer() {
        try {
            boolean success = db.banReviewer(reviewerUser);
            assertTrue(success);
        } catch (SQLException e) {
            fail("banReviewer threw SQLException: " + e.getMessage());
        }
    }

    @Test
    @Order(2)
    @DisplayName("Submit staff message")
    public void testSubmitStaffMessage() {
        try {
            boolean sent = db.submitStaffMessage(staffSender, staffRecipient, "Hello, teammate!");
            assertTrue(sent);
        } catch (SQLException e) {
            fail("submitStaffMessage threw SQLException: " + e.getMessage());
        }
    }

    @Test
    @Order(3)
    @DisplayName("Get staff conversation")
    public void testGetConversation() {
        try {
            List<StaffMessage> conversation = db.getConversation(staffSender, staffRecipient);
            assertNotNull(conversation);
            assertFalse(conversation.isEmpty());
        } catch (SQLException e) {
            fail("getConversation threw SQLException: " + e.getMessage());
        }
    }

    @Test
    @Order(4)
    @DisplayName("Tag a question")
    public void testTagQuestion() {
        try {
            boolean result = db.tagQuestion(testQuestion.getQuestionId(), "important", taggerUser);
            assertTrue(result);
        } catch (SQLException e) {
            fail("tagQuestion threw SQLException: " + e.getMessage());
        }
    }

    @AfterAll
    public static void cleanup() {
        try {
            db.deleteUser(new User(reviewerUser, null, null));
            db.deleteUser(new User(staffSender, null, null));
            db.deleteUser(new User(staffRecipient, null, null));
            db.deleteUser(new User(taggerUser, null, null));
            db.deleteQuestionPermanently(testQuestion.getQuestionId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.closeConnection();
    }
}