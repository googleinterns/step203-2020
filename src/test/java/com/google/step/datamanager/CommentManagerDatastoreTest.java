package com.google.step.datamanager;

import static com.google.step.TestConstants.COMMENT_A;
import static com.google.step.TestConstants.CONTENT_A;
import static com.google.step.TestConstants.CONTENT_B;
import static com.google.step.TestConstants.DEAL_ID_A;
import static com.google.step.TestConstants.UPDATED_COMMENT_A;
import static com.google.step.TestConstants.USER_ID_A;
import static com.google.step.TestConstants.USER_ID_B;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.step.model.Comment;
import java.util.ArrayList;
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
    assertEquals(COMMENT_A, comment);
  }

  @Test
  public void testGetCommentsForDeal() {
    Comment commentA = commentManagerDatastore.createComment(DEAL_ID_A, USER_ID_A, CONTENT_A);
    Comment commentB = commentManagerDatastore.createComment(DEAL_ID_A, USER_ID_B, CONTENT_B);
    List<Comment> comments = commentManagerDatastore.getCommentsForDeal(DEAL_ID_A, null).comments;
    assertEquals(2, comments.size());
    assertThat(comments, hasItems(commentA, commentB));
  }

  @Test
  public void testGetComments_limit() {
    List<Comment> commentsAdded = new ArrayList<>();
    for (int i = 0; i < 6; i++) {
      Comment comment = commentManagerDatastore.createComment(DEAL_ID_A, USER_ID_A, CONTENT_A);
      commentsAdded.add(comment);
    }

    List<Comment> commentsQueried =
        commentManagerDatastore.getCommentsForDeal(DEAL_ID_A, null).comments;

    assertEquals(5, commentsQueried.size());
  }

  @Test
  public void testGetComments_token() {
    List<Comment> commentsAdded = new ArrayList<>();
    for (int i = 0; i < 7; i++) {
      Comment comment = commentManagerDatastore.createComment(DEAL_ID_A, USER_ID_A, "content " + i);
      commentsAdded.add(comment);
    }

    CommentsWithToken commentsWithTokenA =
        commentManagerDatastore.getCommentsForDeal(DEAL_ID_A, null);
    List<Comment> commentsQueriedA = commentsWithTokenA.comments;
    String tokenA = commentsWithTokenA.token;

    CommentsWithToken commentsWithTokenB =
        commentManagerDatastore.getCommentsForDeal(DEAL_ID_A, tokenA);
    List<Comment> commentsQueriedB = commentsWithTokenB.comments;
    String tokenB = commentsWithTokenB.token;

    CommentsWithToken commentsWithTokenC =
        commentManagerDatastore.getCommentsForDeal(DEAL_ID_A, tokenB);
    List<Comment> commentsQueriedC = commentsWithTokenC.comments;

    assertEquals(5, commentsQueriedA.size());
    assertEquals(2, commentsQueriedB.size());
    assertEquals(0, commentsQueriedC.size());

    List<Comment> allComments = new ArrayList<>(commentsQueriedA);
    allComments.addAll(commentsQueriedB);
    assertTrue(commentsAdded.containsAll(allComments));
  }

  @Test
  public void testDeleteSingleComment() {
    Comment commentA = commentManagerDatastore.createComment(DEAL_ID_A, USER_ID_A, CONTENT_A);
    commentManagerDatastore.deleteComment(commentA.id);
    assertTrue(commentManagerDatastore.getCommentsForDeal(DEAL_ID_A, null).comments.isEmpty());
  }

  @Test
  public void testDeleteBothComments() {
    Comment commentA = commentManagerDatastore.createComment(DEAL_ID_A, USER_ID_A, CONTENT_A);
    Comment commentB = commentManagerDatastore.createComment(DEAL_ID_A, USER_ID_B, CONTENT_B);
    commentManagerDatastore.deleteComment(commentA.id);
    commentManagerDatastore.deleteComment(commentB.id);
    assertTrue(commentManagerDatastore.getCommentsForDeal(DEAL_ID_A, null).comments.isEmpty());
  }

  @Test
  public void testDeleteOnlyOneComment() {
    Comment commentA = commentManagerDatastore.createComment(DEAL_ID_A, USER_ID_A, CONTENT_A);
    Comment commentB = commentManagerDatastore.createComment(DEAL_ID_A, USER_ID_B, CONTENT_B);
    commentManagerDatastore.deleteComment(commentA.id);
    List<Comment> comments = commentManagerDatastore.getCommentsForDeal(DEAL_ID_A, null).comments;
    assertThat(comments, hasItem(commentB));
    assertEquals(1, comments.size());
  }

  @Test
  public void testUpdateComment() {
    Comment commentA = commentManagerDatastore.createComment(DEAL_ID_A, USER_ID_A, CONTENT_A);
    commentManagerDatastore.updateComment(commentA.id, CONTENT_B);
    List<Comment> comments = commentManagerDatastore.getCommentsForDeal(DEAL_ID_A, null).comments;
    assertEquals(UPDATED_COMMENT_A, comments.get(0));
    assertEquals(1, comments.size());
  }

  @Test
  public void testReadComment() {
    Comment createdComment = commentManagerDatastore.createComment(DEAL_ID_A, USER_ID_A, CONTENT_A);
    Comment comment = commentManagerDatastore.readComment(createdComment.id);

    assertEquals(createdComment, comment);
  }

  @Test
  public void testReadComment_null() {
    Comment comment = commentManagerDatastore.readComment(1);

    assertEquals(null, comment);
  }
}
