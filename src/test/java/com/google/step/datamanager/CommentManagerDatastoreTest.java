package com.google.step.datamanager;

import static com.google.step.TestConstants.CONTENT_A;
import static com.google.step.TestConstants.CONTENT_B;
import static com.google.step.TestConstants.DEAL_ID_A;
import static com.google.step.TestConstants.USER_ID_A;
import static com.google.step.TestConstants.USER_ID_B;
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
    Comment comment = commentManagerDatastore.createComment(DEAL_ID_A, USER_ID_A, CONTENT_A);
    assertEquals(DEAL_ID_A, comment.dealId);
    assertEquals(USER_ID_A, comment.userId);
    assertEquals(CONTENT_A, comment.content);
  }

  @Test
  public void testGetCommentsForDeal() {
    commentManagerDatastore.createComment(DEAL_ID_A, USER_ID_A, CONTENT_A);
    commentManagerDatastore.createComment(DEAL_ID_A, USER_ID_B, CONTENT_B);
    List<Comment> comments = commentManagerDatastore.getCommentsForDeal(DEAL_ID_A);

    Comment commentAResult = comments.get(0);
    Comment commentBResult = comments.get(1);

    assertEquals(DEAL_ID_A, commentAResult.dealId);
    assertEquals(USER_ID_B, commentAResult.userId);
    assertEquals(CONTENT_B, commentAResult.content);

    assertEquals(DEAL_ID_A, commentBResult.dealId);
    assertEquals(USER_ID_A, commentBResult.userId);
    assertEquals(CONTENT_A, commentBResult.content);
  }

  @Test
  public void testDeleteSingleComment() {
    Comment commentA = commentManagerDatastore.createComment(DEAL_ID_A, USER_ID_A, CONTENT_A);
    commentManagerDatastore.deleteComment(commentA.id);
    assertTrue(commentManagerDatastore.getCommentsForDeal(DEAL_ID_A).isEmpty());
  }

  @Test
  public void testDeleteBothComments() {
    Comment commentA = commentManagerDatastore.createComment(DEAL_ID_A, USER_ID_A, CONTENT_A);
    Comment commentB = commentManagerDatastore.createComment(DEAL_ID_A, USER_ID_B, CONTENT_B);
    commentManagerDatastore.deleteComment(commentA.id);
    commentManagerDatastore.deleteComment(commentB.id);
    assertTrue(commentManagerDatastore.getCommentsForDeal(DEAL_ID_A).isEmpty());
  }

  @Test
  public void testDeleteOnlyOneComment() {
    Comment commentA = commentManagerDatastore.createComment(DEAL_ID_A, USER_ID_A, CONTENT_A);
    commentManagerDatastore.createComment(DEAL_ID_A, USER_ID_B, CONTENT_B);
    commentManagerDatastore.deleteComment(commentA.id);
    List<Comment> comments = commentManagerDatastore.getCommentsForDeal(DEAL_ID_A);
    Comment commentBResult = comments.get(0);
    assertEquals(DEAL_ID_A, commentBResult.dealId);
    assertEquals(USER_ID_B, commentBResult.userId);
    assertEquals(CONTENT_B, commentBResult.content);
    assertEquals(1, comments.size());
  }

  @Test
  public void testUpdateComment() {
    Comment commentA = commentManagerDatastore.createComment(DEAL_ID_A, USER_ID_A, CONTENT_A);
    commentManagerDatastore.updateComment(commentA.id, "Updated comment");
    List<Comment> comments = commentManagerDatastore.getCommentsForDeal(DEAL_ID_A);
    Comment commentAResult = comments.get(0);
    assertEquals(DEAL_ID_A, commentAResult.dealId);
    assertEquals(USER_ID_A, commentAResult.userId);
    assertEquals("Updated comment", commentAResult.content);
  }
}
