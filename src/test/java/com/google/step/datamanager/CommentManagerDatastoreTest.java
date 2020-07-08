package com.google.step.datamanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.step.model.Comment;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class CommentManagerDatastoreTest {

  private static final long DEALID = 2;

  private static final long USERID_A = 3;
  private static final String CONTENT_A = "Hello world";

  private static final long USERID_B = 4;
  private static final String CONTENT_B = "Hello world2";

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(
          new LocalDatastoreServiceTestConfig().setApplyAllHighRepJobPolicy());

  private final CommentManagerDatastore commentManagerDatastore = new CommentManagerDatastore();

  @Before
  public void setUp() {
    helper.setUp();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void testCreateComment() {
    Comment comment = commentManagerDatastore.createComment(DEALID, USERID_A, CONTENT_A);
    assertEquals(DEALID, comment.dealId);
    assertEquals(USERID_A, comment.userId);
    assertEquals(CONTENT_A, comment.content);
  }

  @Test
  public void testGetComments() {
    Comment comment_A = commentManagerDatastore.createComment(DEALID, USERID_A, CONTENT_A);
    Comment comment_B = commentManagerDatastore.createComment(DEALID, USERID_B, CONTENT_B);
    List<Comment> comments = commentManagerDatastore.getComments(DEALID);
    Comment comment_A_Test = comments.get(0);
    Comment comment_B_Test = comments.get(1);
    assertEquals(DEALID, comment_A_Test.dealId);
    assertEquals(USERID_A, comment_A_Test.userId);
    assertEquals(CONTENT_A, comment_A_Test.content);
    assertEquals(DEALID, comment_B_Test.dealId);
    assertEquals(USERID_B, comment_B_Test.userId);
    assertEquals(CONTENT_B, comment_B_Test.content);
  }

  @Test
  public void testDeleteSingleComment() {
    Comment comment_A = commentManagerDatastore.createComment(DEALID, USERID_A, CONTENT_A);
    commentManagerDatastore.deleteComment(comment_A.id);
    assertTrue(commentManagerDatastore.getComments(DEALID).isEmpty());
  }

  @Test
  public void testDeleteBothComments() {
    Comment comment_A = commentManagerDatastore.createComment(DEALID, USERID_A, CONTENT_A);
    Comment comment_B = commentManagerDatastore.createComment(DEALID, USERID_B, CONTENT_B);
    commentManagerDatastore.deleteComment(comment_A.id);
    commentManagerDatastore.deleteComment(comment_B.id);
    assertTrue(commentManagerDatastore.getComments(DEALID).isEmpty());
  }

  @Test
  public void testDeleteOnlyOneComment() {
    Comment comment_A = commentManagerDatastore.createComment(DEALID, USERID_A, CONTENT_A);
    Comment comment_B = commentManagerDatastore.createComment(DEALID, USERID_B, CONTENT_B);
    commentManagerDatastore.deleteComment(comment_A.id);
    List<Comment> comments = commentManagerDatastore.getComments(DEALID);
    Comment comment_B_Test = comments.get(0);
    assertEquals(DEALID, comment_B_Test.dealId);
    assertEquals(USERID_B, comment_B_Test.userId);
    assertEquals(CONTENT_B, comment_B_Test.content);
    assertEquals(1, comments.size());
  }

  @Test
  public void testUpdateComment() {
    Comment comment_A = commentManagerDatastore.createComment(DEALID, USERID_A, CONTENT_A);
    Comment comment_A_Updated =
        commentManagerDatastore.updateComment(comment_A.id, "Updated comment");
    List<Comment> comments = commentManagerDatastore.getComments(DEALID);
    Comment comment_A_Test = comments.get(0);
    assertEquals(DEALID, comment_A_Test.dealId);
    assertEquals(USERID_A, comment_A_Test.userId);
    assertEquals("Updated comment", comment_A_Test.content);
  }
}