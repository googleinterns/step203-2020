package com.google.step.servlets;

import static com.google.step.TestConstants.COMMENT_A;
import static com.google.step.TestConstants.COMMENT_A_JSON;
import static com.google.step.TestConstants.COMMENT_ID_A;
import static com.google.step.TestConstants.CONTENT_A;
import static com.google.step.TestConstants.DEAL_ID_A;
import static com.google.step.TestConstants.TIME_A;
import static com.google.step.TestConstants.USER_A;
import static com.google.step.TestConstants.USER_ID_A;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.step.datamanager.CommentManager;
import com.google.step.datamanager.UserManager;
import com.google.step.model.Comment;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

@RunWith(JUnit4.class)
public class CommentServletTest {

  private static final Comment COMMENT_A =
      new Comment(COMMENT_ID_A, DEAL_ID_A, USER_ID_A, CONTENT_A, TIME_A);

  private CommentManager mockCommentManager;
  private UserManager mockUserManager;

  private CommentServlet commentServlet;

  @Before
  public void setUp() {
    mockCommentManager = mock(CommentManager.class);
    mockUserManager = mock(UserManager.class);
    commentServlet = new CommentServlet(mockCommentManager, mockUserManager);

    when(mockUserManager.readUser(USER_ID_A)).thenReturn(USER_A);
  }

  @Test
  public void testDoPut_success() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getPathInfo()).thenReturn("/" + COMMENT_ID_A);
    when(request.getParameter("content")).thenReturn(CONTENT_A);
    when(mockCommentManager.updateComment(COMMENT_ID_A, CONTENT_A)).thenReturn(COMMENT_A);

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    commentServlet.doPut(request, response);

    JSONAssert.assertEquals(COMMENT_A_JSON, stringWriter.toString(), JSONCompareMode.STRICT);
  }

  @Test
  public void testDoPut_invalidID() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getPathInfo()).thenReturn("/abcd");

    commentServlet.doPut(request, response);
    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoPut_noID() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getPathInfo()).thenReturn("/");

    commentServlet.doPut(request, response);
    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoPut_notExist() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getPathInfo()).thenReturn("/100");

    when(request.getParameter("content")).thenReturn(CONTENT_A);
    when(mockCommentManager.updateComment(100, CONTENT_A)).thenReturn(null);

    commentServlet.doPut(request, response);

    verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
  }

  @Test
  public void testDoDelete_success() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getPathInfo()).thenReturn("/" + COMMENT_ID_A);

    commentServlet.doDelete(request, response);

    verify(response, never()).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    verify(mockCommentManager).deleteComment(anyLong());
  }

  @Test
  public void testDoDelete_invalidID() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getPathInfo()).thenReturn("/abcd");

    commentServlet.doDelete(request, response);

    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoDelete_noID() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getPathInfo()).thenReturn("/");

    commentServlet.doDelete(request, response);

    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }
}
