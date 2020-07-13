package com.google.step.servlets;

import static com.google.step.TestConstants.COMMENT_ID_A;
import static com.google.step.TestConstants.COMMENT_ID_B;
import static com.google.step.TestConstants.CONTENT_A;
import static com.google.step.TestConstants.CONTENT_B;
import static com.google.step.TestConstants.DEAL_ID_A;
import static com.google.step.TestConstants.TIME_A;
import static com.google.step.TestConstants.TIME_B;
import static com.google.step.TestConstants.USER_ID_A;
import static com.google.step.TestConstants.USER_ID_B;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.step.datamanager.CommentManager;
import com.google.step.model.Comment;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

@RunWith(JUnit4.class)
public class CommentGetPostServletTest {

  private static final Comment COMMENT_A =
      new Comment(COMMENT_ID_A, DEAL_ID_A, USER_ID_A, CONTENT_A, TIME_A);

  private static final Comment COMMENT_B =
      new Comment(COMMENT_ID_B, DEAL_ID_A, USER_ID_B, CONTENT_B, TIME_B);

  private CommentManager mockCommentManager;

  private CommentGetPostServlet commentGetPostServlet;

  @Before
  public void setUp() {
    mockCommentManager = mock(CommentManager.class);
    commentGetPostServlet = new CommentGetPostServlet(mockCommentManager);
  }

  @Test
  public void testDoGet_success() throws Exception {
    List<Comment> comments = new ArrayList<>();
    comments.add(COMMENT_A);
    comments.add(COMMENT_B);
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getParameter("dealId")).thenReturn(Long.toString(DEAL_ID_A));
    when(mockCommentManager.getCommentsForDeal(DEAL_ID_A)).thenReturn(comments);

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    commentGetPostServlet.doGet(request, response);

    String commentA =
        String.format(
            "{id:%d,dealId:%d,userId:%d,content:\"%s\",timestamp:\"%s\"}",
            COMMENT_ID_A, DEAL_ID_A, USER_ID_A, CONTENT_A, TIME_A);
    String commentB =
        String.format(
            "{id:%d,dealId:%d,userId:%d,content:\"%s\",timestamp:\"%s\"}",
            COMMENT_ID_B, DEAL_ID_A, USER_ID_B, CONTENT_B, TIME_B);
    String expected = "[" + commentA + "," + commentB + "]";

    JSONAssert.assertEquals(expected, stringWriter.toString(), JSONCompareMode.STRICT);
  }

  @Test
  public void testDoGet_notExist() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    when(request.getParameter("dealId")).thenReturn("1000");
    when(mockCommentManager.getCommentsForDeal(1000)).thenReturn(new ArrayList<>());

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    commentGetPostServlet.doGet(request, response);

    String expected = "[]";

    JSONAssert.assertEquals(expected, stringWriter.toString(), JSONCompareMode.STRICT);
  }

  @Test
  public void testDoGet_invalidID() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    when(request.getParameter("deaId")).thenReturn("abcd");

    commentGetPostServlet.doGet(request, response);

    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoGet_noId() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    when(request.getParameter("dealId")).thenReturn("");

    commentGetPostServlet.doGet(request, response);
    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoPost_success() throws Exception {
    List<Comment> comments = new ArrayList<>();
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getParameter("dealId")).thenReturn(Long.toString(DEAL_ID_A));
    when(request.getParameter("content")).thenReturn(CONTENT_A);
    when(mockCommentManager.createComment(DEAL_ID_A, USER_ID_A, CONTENT_A)).thenReturn(COMMENT_A);

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    commentGetPostServlet.doPost(request, response);

    verify(response, never()).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    verify(mockCommentManager).createComment(anyLong(), anyLong(), eq(CONTENT_A));

    String expected =
        String.format(
            "[{id:%d,dealId:%d,userId:%d,content:\"%s\",timestamp:\"%s\"}]",
            COMMENT_ID_A, DEAL_ID_A, USER_ID_A, CONTENT_A, TIME_A);

    JSONAssert.assertEquals(expected, stringWriter.toString(), JSONCompareMode.STRICT);
  }
}
