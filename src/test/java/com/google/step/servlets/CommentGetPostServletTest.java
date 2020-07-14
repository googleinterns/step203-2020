package com.google.step.servlets;

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

  private static final long DEALID = 2;

  private static final long ID_A = 1;
  private static final long USERID_A = 3;
  private static final String CONTENT_A = "Hello world";
  private static final Comment COMMENT_A = new Comment(ID_A, DEALID, USERID_A, CONTENT_A, "1");

  private static final long ID_B = 2;
  private static final long USERID_B = 4;
  private static final String CONTENT_B = "Hello world2";
  private static final Comment COMMENT_B = new Comment(ID_B, DEALID, USERID_B, CONTENT_B, "1");

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

    when(request.getParameter("dealId")).thenReturn("2");
    when(mockCommentManager.getCommentsForDeal(2)).thenReturn(comments);

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    commentGetPostServlet.doGet(request, response);

    String commentA =
        String.format(
            "{id:%d,dealId:%d,userId:%d,content:\"%s\"}", ID_A, DEALID, USERID_A, CONTENT_A);
    String commentB =
        String.format(
            "{id:%d,dealId:%d,userId:%d,content:\"%s\"}", ID_B, DEALID, USERID_B, CONTENT_B);
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

    when(request.getParameter("dealId")).thenReturn(Long.toString(DEALID));
    when(request.getParameter("content")).thenReturn(CONTENT_A);
    when(mockCommentManager.createComment(DEALID, USERID_A, CONTENT_A)).thenReturn(COMMENT_A);
    commentGetPostServlet.doPost(request, response);

    verify(response, never()).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    verify(mockCommentManager).createComment(anyLong(), anyLong(), eq(CONTENT_A));
  }
}