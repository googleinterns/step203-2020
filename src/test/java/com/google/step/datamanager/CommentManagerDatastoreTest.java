package com.google.step.datamanager;

import static org.junit.Assert.*;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.step.model.Comment;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;
import java.util.ArrayList;

@RunWith(JUnit4.class)
public final class CommentManagerDatastoreTest {

  private static final long dealId = 2;

  private static final long ID_A = 1;
  private static final long userId_A = 3;
  private static final String content_A = "Hello world";

  private static final long ID_B = 2;
  private static final long userId_B = 4;
  private static final String content_B = "Hello world2";

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
    Comment comment = commentManagerDatastore.createComment(dealId, userId_A, content_A);
    Assert.assertEquals(dealId, comment.dealId);
    Assert.assertEquals(userId_A, comment.userId);
    Assert.assertEquals(content_A, comment.content);
  }

  @Test
  public void testGetComments() {
    Comment comment_A = commentManagerDatastore.createComment(dealId, userId_A, content_A);
    Comment comment_B = commentManagerDatastore.createComment(dealId, userId_B, content_B);
    List<Comment> comments = commentManagerDatastore.getComments(dealId);
    Comment comment_A_Test = comments.get(0);
    Comment comment_B_Test = comments.get(1);
    assertEquals(dealId, comment_A_Test.dealId);
    assertEquals(userId_A, comment_A_Test.userId);
    assertEquals(content_A, comment_A_Test.content);
    assertEquals(dealId, comment_B_Test.dealId);
    assertEquals(userId_B, comment_B_Test.userId);
    assertEquals(content_B, comment_B_Test.content);
  }

  @Test
  public void testDeleteSingleComment() {
    Comment comment_A = commentManagerDatastore.createComment(dealId, userId_A, content_A);
    commentManagerDatastore.deleteComment(comment_A.id);
    Assert.assertTrue(commentManagerDatastore.getComments(dealId).isEmpty());;
  }

  @Test
  public void testDeleteBothComments() {
    Comment comment_A = commentManagerDatastore.createComment(dealId, userId_A, content_A);
    Comment comment_B = commentManagerDatastore.createComment(dealId, userId_B, content_B);
    commentManagerDatastore.deleteComment(comment_A.id);
    commentManagerDatastore.deleteComment(comment_B.id);
    Assert.assertTrue(commentManagerDatastore.getComments(dealId).isEmpty());
  }

  @Test
  public void testDeleteOnlyOneComment() {
    Comment comment_A = commentManagerDatastore.createComment(dealId, userId_A, content_A);
    Comment comment_B = commentManagerDatastore.createComment(dealId, userId_B, content_B);
    commentManagerDatastore.deleteComment(comment_A.id);
    List<Comment> comments = commentManagerDatastore.getComments(dealId);
    Comment comment_B_Test = comments.get(0);
    Assert.assertEquals(dealId, comment_B_Test.dealId);
    Assert.assertEquals(userId_B, comment_B_Test.userId);
    Assert.assertEquals(content_B, comment_B_Test.content);
    Assert.assertEquals(1, comments.size());
  }

  @Test
  public void testUpdateComment() {
    Comment comment_A = commentManagerDatastore.createComment(dealId, userId_A, content_A);
    Comment comment_A_Updated = commentManagerDatastore.updateComment(comment_A.id, "Updated comment");
    List<Comment> comments = commentManagerDatastore.getComments(dealId);
    Comment comment_A_Test = comments.get(0);
    Assert.assertEquals(dealId, comment_A_Test.dealId);
    Assert.assertEquals(userId_A, comment_A_Test.userId);
    Assert.assertEquals("Updated comment", comment_A_Test.content);
  }
}
