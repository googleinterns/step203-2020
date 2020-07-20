package com.google.step.servlets;

import static com.google.step.TestConstants.COMMENT_A;
import static com.google.step.TestConstants.COMMENT_A_JSON;
import static com.google.step.TestConstants.COMMENT_B;
import static com.google.step.TestConstants.COMMENT_B_JSON;
import static com.google.step.TestConstants.CONTENT_A;
import static com.google.step.TestConstants.DEAL_ID_A;
import static com.google.step.TestConstants.EMAIL_A;
import static com.google.step.TestConstants.EMAIL_B;
import static com.google.step.TestConstants.USER_A;
import static com.google.step.TestConstants.USER_B;
import static com.google.step.TestConstants.USER_ID_A;
import static com.google.step.TestConstants.USER_ID_B;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.step.datamanager.CommentManager;
import com.google.step.datamanager.UserManager;
import com.google.step.model.Comment;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
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

  private CommentManager mockCommentManager;
  private UserService mockUserService;
  private UserManager mockUserManager;

  private CommentGetPostServlet commentGetPostServlet;

  @Before
  public void setUp() {
    mockCommentManager = mock(CommentManager.class);
    mockUserService = mock(UserService.class);
    mockUserManager = mock(UserManager.class);
    commentGetPostServlet =
        new CommentGetPostServlet(mockCommentManager, mockUserService, mockUserManager);

    // mock behaviour when user is logged in
    when(mockUserService.isUserLoggedIn()).thenReturn(true);
    User currentUser = new User(EMAIL_A, "");
    when(mockUserService.getCurrentUser()).thenReturn(currentUser);

    // mock user manager
    when(mockUserManager.readUserByEmail(EMAIL_A)).thenReturn(USER_A);
    when(mockUserManager.readUserByEmail(EMAIL_B)).thenReturn(USER_B);
    when(mockUserManager.readUser(USER_ID_A)).thenReturn(USER_A);
    when(mockUserManager.readUser(USER_ID_B)).thenReturn(USER_B);
    List<Long> userIds = Arrays.asList(USER_ID_A, USER_ID_B);
    when(mockUserManager.readUsers(userIds)).thenReturn(Arrays.asList(USER_A, USER_B));
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

    String expected = "[" + COMMENT_A_JSON + "," + COMMENT_B_JSON + "]";

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
    verify(response).sendRedirect("/deals/" + DEAL_ID_A);
  }
}
