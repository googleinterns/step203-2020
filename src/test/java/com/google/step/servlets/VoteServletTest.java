package com.google.step.servlets;

import static com.google.step.TestConstants.EMAIL_A;
import static com.google.step.TestConstants.USER_A;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.step.datamanager.UserManager;
import com.google.step.datamanager.VoteManager;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class VoteServletTest {

  private static final String DEAL_PATH = "/123";
  private static final String DEAL_PATH_INVALID = "/trash";
  private static final String DIR_ONE = "1";
  private static final String DIR_INVALID = "00";

  private VoteServlet servlet;
  private UserService userService;
  private UserManager userManager;
  private VoteManager voteManager;
  private HttpServletResponse response;

  @Before
  public void setUp() throws IOException {
    // mock managers
    userService = mock(UserService.class);
    userManager = mock(UserManager.class);
    voteManager = mock(VoteManager.class);
    servlet = new VoteServlet(userService, userManager, voteManager);

    // mock HttpServletResponse
    response = mock(HttpServletResponse.class);
    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    // behaviour when user is logged in
    when(userService.isUserLoggedIn()).thenReturn(true);
    User currentUser = new User(EMAIL_A, "");
    when(userService.getCurrentUser()).thenReturn(currentUser);
    when(userManager.readUserByEmail(EMAIL_A)).thenReturn(USER_A);
  }

  @Test
  public void testDoPost_userNotLoggedIn_unauthorized() throws IOException {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(userService.isUserLoggedIn()).thenReturn(false);
    when(userService.getCurrentUser()).thenReturn(null);

    servlet.doPost(request, response);

    verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
  }

  @Test
  public void testDoPost_sucess() throws IOException {
    HttpServletRequest request = mock(HttpServletRequest.class);

    when(request.getParameter("dir")).thenReturn(DIR_ONE);
    when(request.getPathInfo()).thenReturn(DEAL_PATH);

    servlet.doPost(request, response);

    verify(response, never()).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    verify(voteManager).vote(anyLong(), eq((long) 123), eq(1));
  }

  @Test
  public void testDoPost_invalidPath_badRequest() throws IOException {
    HttpServletRequest request = mock(HttpServletRequest.class);

    when(request.getParameter("dir")).thenReturn(DIR_ONE);
    when(request.getPathInfo()).thenReturn(DEAL_PATH_INVALID);

    servlet.doPost(request, response);
    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoPost_invalidDir_badRequest() throws IOException {
    HttpServletRequest request = mock(HttpServletRequest.class);

    when(request.getParameter("dir")).thenReturn(DIR_INVALID);
    when(request.getPathInfo()).thenReturn(DEAL_PATH);

    servlet.doPost(request, response);
    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoPost_missingDir_badRequest() throws IOException {
    HttpServletRequest request = mock(HttpServletRequest.class);

    when(request.getParameter("dir")).thenReturn(null);
    when(request.getPathInfo()).thenReturn(DEAL_PATH);

    servlet.doPost(request, response);
    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }
}
