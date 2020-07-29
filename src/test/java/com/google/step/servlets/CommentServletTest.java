package com.google.step.servlets;

import static com.google.step.TestConstants.COMMENT_A;
import static com.google.step.TestConstants.COMMENT_A_JSON;
import static com.google.step.TestConstants.COMMENT_ID_A;
import static com.google.step.TestConstants.CONTENT_A;
import static com.google.step.TestConstants.DEAL_ID_A;
import static com.google.step.TestConstants.EMAIL_A;
import static com.google.step.TestConstants.TIME_A;
import static com.google.step.TestConstants.USER_A;
import static com.google.step.TestConstants.USER_ID_A;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.step.datamanager.CommentManager;
import com.google.step.datamanager.UserManager;
import com.google.step.model.Comment;
import java.io.IOException;
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
  private UserService mockUserService;

  HttpServletRequest mockRequest;
  HttpServletResponse mockResponse;
  private StringWriter stringWriter;
  private PrintWriter writer;

  private CommentServlet commentServlet;

  @Before
  public void setUp() throws IOException {
    mockRequest = mock(HttpServletRequest.class);
    mockResponse = mock(HttpServletResponse.class);
    mockCommentManager = mock(CommentManager.class);
    mockUserManager = mock(UserManager.class);
    mockUserService = mock(UserService.class);
    commentServlet = new CommentServlet(mockCommentManager, mockUserManager, mockUserService);

    // behaviour when user is logged in
    when(mockUserService.isUserLoggedIn()).thenReturn(true);
    User currentUser = new User(EMAIL_A, "");
    when(mockUserService.getCurrentUser()).thenReturn(currentUser);
    when(mockUserManager.readUserByEmail(EMAIL_A)).thenReturn(USER_A);
    when(mockUserManager.readUser(USER_A.id)).thenReturn(USER_A);

    when(mockUserManager.readUser(USER_ID_A)).thenReturn(USER_A);

    // mock response
    mockResponse = mock(HttpServletResponse.class);
    stringWriter = new StringWriter();
    writer = new PrintWriter(stringWriter);
    when(mockResponse.getWriter()).thenReturn(writer);
  }

  @Test
  public void testDoPut_success() throws Exception {
    when(mockRequest.getPathInfo()).thenReturn("/" + COMMENT_ID_A);
    when(mockRequest.getParameter("content")).thenReturn(CONTENT_A);
    when(mockCommentManager.readComment(COMMENT_ID_A)).thenReturn(COMMENT_A);
    when(mockCommentManager.updateComment(COMMENT_ID_A, CONTENT_A)).thenReturn(COMMENT_A);

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(mockResponse.getWriter()).thenReturn(writer);

    commentServlet.doPut(mockRequest, mockResponse);

    JSONAssert.assertEquals(COMMENT_A_JSON, stringWriter.toString(), JSONCompareMode.STRICT);
  }

  @Test
  public void testDoPut_notLoggedIn() throws Exception {
    when(mockUserService.isUserLoggedIn()).thenReturn(false);

    commentServlet.doPut(mockRequest, mockResponse);
    verify(mockResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
  }

  @Test
  public void testDoPut_invalidID() throws Exception {
    when(mockRequest.getPathInfo()).thenReturn("/abcd");

    commentServlet.doPut(mockRequest, mockResponse);
    verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoPut_noID() throws Exception {
    when(mockRequest.getPathInfo()).thenReturn("/");

    commentServlet.doPut(mockRequest, mockResponse);
    verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoPut_notExist() throws Exception {
    when(mockRequest.getPathInfo()).thenReturn("/100");

    when(mockRequest.getParameter("content")).thenReturn(CONTENT_A);
    when(mockCommentManager.updateComment(100, CONTENT_A)).thenReturn(null);

    commentServlet.doPut(mockRequest, mockResponse);

    verify(mockResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);
  }

  @Test
  public void testDoDelete_success() throws Exception {
    when(mockRequest.getPathInfo()).thenReturn("/" + COMMENT_ID_A);
    when(mockCommentManager.readComment(COMMENT_ID_A)).thenReturn(COMMENT_A);

    commentServlet.doDelete(mockRequest, mockResponse);

    verify(mockResponse, never()).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    verify(mockCommentManager).deleteComment(COMMENT_ID_A);
  }

  @Test
  public void testDoDelete_invalidID() throws Exception {
    when(mockRequest.getPathInfo()).thenReturn("/abcd");

    commentServlet.doDelete(mockRequest, mockResponse);

    verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoDelete_noID() throws Exception {
    when(mockRequest.getPathInfo()).thenReturn("/");

    commentServlet.doDelete(mockRequest, mockResponse);

    verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }
}
