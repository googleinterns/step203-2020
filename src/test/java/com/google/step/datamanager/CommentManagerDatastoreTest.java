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
  public void testGetCommentsForDeal() {
    commentManagerDatastore.createComment(DEALID, USERID_A, CONTENT_A);
    commentManagerDatastore.createComment(DEALID, USERID_B, CONTENT_B);
    List<Comment> comments = commentManagerDatastore.getCommentsForDeal(DEALID);
    Comment commentAResult = comments.get(0);
    Comment commentBResult = comments.get(1);
    assertEquals(DEALID, commentAResult.dealId);
    assertEquals(USERID_A, commentAResult.userId);
    assertEquals(CONTENT_A, commentAResult.content);
    assertEquals(DEALID, commentBResult.dealId);
    assertEquals(USERID_B, commentBResult.userId);
    assertEquals(CONTENT_B, commentBResult.content);
  }

  @Test
  public void testDeleteSingleComment() {
    Comment commentA = commentManagerDatastore.createComment(DEALID, USERID_A, CONTENT_A);
    commentManagerDatastore.deleteComment(commentA.id);
    assertTrue(commentManagerDatastore.getCommentsForDeal(DEALID).isEmpty());
  }

  @Test
  public void testDeleteBothComments() {
    Comment commentA = commentManagerDatastore.createComment(DEALID, USERID_A, CONTENT_A);
    Comment commentB = commentManagerDatastore.createComment(DEALID, USERID_B, CONTENT_B);
    commentManagerDatastore.deleteComment(commentA.id);
    commentManagerDatastore.deleteComment(commentB.id);
    assertTrue(commentManagerDatastore.getCommentsForDeal(DEALID).isEmpty());
  }

  @Test
  public void testDeleteOnlyOneComment() {
    Comment commentA = commentManagerDatastore.createComment(DEALID, USERID_A, CONTENT_A);
    commentManagerDatastore.createComment(DEALID, USERID_B, CONTENT_B);
    commentManagerDatastore.deleteComment(commentA.id);
    List<Comment> comments = commentManagerDatastore.getCommentsForDeal(DEALID);
    Comment commentBResult = comments.get(0);
    assertEquals(DEALID, commentBResult.dealId);
    assertEquals(USERID_B, commentBResult.userId);
    assertEquals(CONTENT_B, commentBResult.content);
    assertEquals(1, comments.size());
  }

  @Test
  public void testUpdateComment() {
    Comment commentA = commentManagerDatastore.createComment(DEALID, USERID_A, CONTENT_A);
    commentManagerDatastore.updateComment(commentA.id, "Updated comment");
    List<Comment> comments = commentManagerDatastore.getCommentsForDeal(DEALID);
    Comment commentAResult = comments.get(0);
    assertEquals(DEALID, commentAResult.dealId);
    assertEquals(USERID_A, commentAResult.userId);
    assertEquals("Updated comment", commentAResult.content);
  }
}
