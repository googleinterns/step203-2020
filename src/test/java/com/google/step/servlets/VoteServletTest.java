package com.google.step.servlets;

import static com.google.step.TestConstants.DEAL_ID_A;
import static com.google.step.TestConstants.EMAIL_A;
import static com.google.step.TestConstants.USER_A;
import static com.google.step.TestConstants.USER_ID_A;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.step.datamanager.DealVoteCountManager;
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

  private static final String DEAL_PATH = "/" + DEAL_ID_A;
  private static final String DIR_ONE = "1";
  private static final String DIR_NEG_ONE = "-1";
  private static final String DIR_UNDO = "0";

  private VoteServlet servlet;
  private UserService userService;
  private UserManager userManager;
  private VoteManager voteManager;
  private DealVoteCountManager dealVoteCountManager;
  private HttpServletResponse response;
  private StringWriter stringWriter;
  private PrintWriter writer;

  @Before
  public void setUp() throws IOException {
    // mock managers
    userService = mock(UserService.class);
    userManager = mock(UserManager.class);
    voteManager = mock(VoteManager.class);
    dealVoteCountManager = mock(DealVoteCountManager.class);
    servlet = new VoteServlet(userService, userManager, voteManager, dealVoteCountManager);

    // mock HttpServletResponse
    response = mock(HttpServletResponse.class);
    stringWriter = new StringWriter();
    writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);
  }

  public void setUpUserAuthentication() {
    // behaviour when user is logged in
    when(userService.isUserLoggedIn()).thenReturn(true);
    User currentUser = new User(EMAIL_A, "");
    when(userService.getCurrentUser()).thenReturn(currentUser);
    when(userManager.readUserByEmail(EMAIL_A)).thenReturn(USER_A);
  }

  @Test
  public void testDoPost_userNotLoggedIn_unauthorized() throws IOException {
    HttpServletRequest request = mock(HttpServletRequest.class);

    when(request.getPathInfo()).thenReturn(DEAL_PATH);

    when(userService.isUserLoggedIn()).thenReturn(false);
    when(userService.getCurrentUser()).thenReturn(null);

    servlet.doPost(request, response);

    verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
  }

  @Test
  public void testDoPost_successUserVotedSameDirectionAsBefore() throws IOException {
    setUpUserAuthentication();
    HttpServletRequest request = mock(HttpServletRequest.class);

    when(request.getParameter("dir")).thenReturn(DIR_ONE);
    when(request.getPathInfo()).thenReturn(DEAL_PATH);
    when(voteManager.getDirection(USER_ID_A, DEAL_ID_A)).thenReturn(1);

    servlet.doPost(request, response);

    verify(response).setStatus(HttpServletResponse.SC_ACCEPTED);
    verify(voteManager).vote(eq(USER_ID_A), eq(DEAL_ID_A), eq(1));
    verify(dealVoteCountManager, never()).updateDealVotes(eq(DEAL_ID_A), anyInt());
  }

  @Test
  public void testDoPost_invalidPath_badRequest() throws IOException {
    setUpUserAuthentication();
    HttpServletRequest request = mock(HttpServletRequest.class);

    when(request.getParameter("dir")).thenReturn(DIR_ONE);
    when(request.getPathInfo()).thenReturn("/trash");

    servlet.doPost(request, response);
    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoPost_invalidDir_badRequest() throws IOException {
    setUpUserAuthentication();
    HttpServletRequest request = mock(HttpServletRequest.class);

    when(request.getParameter("dir")).thenReturn("00");
    when(request.getPathInfo()).thenReturn(DEAL_PATH);

    servlet.doPost(request, response);
    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoPost_missingDir_badRequest() throws IOException {
    setUpUserAuthentication();
    HttpServletRequest request = mock(HttpServletRequest.class);

    when(request.getParameter("dir")).thenReturn(null);
    when(request.getPathInfo()).thenReturn(DEAL_PATH);

    servlet.doPost(request, response);
    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoGet_userNotLoggedIn_unauthorized() throws IOException {
    HttpServletRequest request = mock(HttpServletRequest.class);

    when(request.getPathInfo()).thenReturn(DEAL_PATH);

    when(userService.isUserLoggedIn()).thenReturn(false);
    when(userService.getCurrentUser()).thenReturn(null);

    servlet.doGet(request, response);

    verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
  }

  @Test
  public void testDoGet_success() throws IOException {
    setUpUserAuthentication();
    HttpServletRequest request = mock(HttpServletRequest.class);

    when(request.getPathInfo()).thenReturn(DEAL_PATH);
    when(voteManager.getDirection(USER_ID_A, DEAL_ID_A)).thenReturn(1);

    servlet.doGet(request, response);

    verify(voteManager).getDirection(USER_ID_A, DEAL_ID_A);
    verify(response).setStatus(HttpServletResponse.SC_ACCEPTED);
    writer.flush();
    assertEquals("1", stringWriter.toString().trim());
  }

  @Test
  public void testDoPost_successUserChangedDirection() throws IOException {
    setUpUserAuthentication();
    HttpServletRequest request = mock(HttpServletRequest.class);

    when(request.getParameter("dir")).thenReturn(DIR_ONE);
    when(request.getPathInfo()).thenReturn(DEAL_PATH);
    when(voteManager.getDirection(USER_ID_A, DEAL_ID_A)).thenReturn(-1);

    servlet.doPost(request, response);

    verify(response).setStatus(HttpServletResponse.SC_ACCEPTED);
    verify(voteManager).vote(eq(USER_ID_A), eq(DEAL_ID_A), eq(1));
    verify(dealVoteCountManager).updateDealVotes(eq(DEAL_ID_A), eq(2));
  }

  @Test
  public void testDoPost_successUserChangedAnotherDirection() throws IOException {
    setUpUserAuthentication();
    HttpServletRequest request = mock(HttpServletRequest.class);

    when(request.getParameter("dir")).thenReturn(DIR_NEG_ONE);
    when(request.getPathInfo()).thenReturn(DEAL_PATH);
    when(voteManager.getDirection(USER_ID_A, DEAL_ID_A)).thenReturn(1);

    servlet.doPost(request, response);

    verify(response).setStatus(HttpServletResponse.SC_ACCEPTED);
    verify(voteManager).vote(eq(USER_ID_A), eq(DEAL_ID_A), eq(-1));
    verify(dealVoteCountManager).updateDealVotes(eq(DEAL_ID_A), eq(-2));
  }

  @Test
  public void testDoPost_successUserUndoesVote() throws IOException {
    setUpUserAuthentication();
    HttpServletRequest request = mock(HttpServletRequest.class);

    when(request.getParameter("dir")).thenReturn(DIR_UNDO);
    when(request.getPathInfo()).thenReturn(DEAL_PATH);
    when(voteManager.getDirection(USER_ID_A, DEAL_ID_A)).thenReturn(1);

    servlet.doPost(request, response);

    verify(response).setStatus(HttpServletResponse.SC_ACCEPTED);
    verify(voteManager).vote(eq(USER_ID_A), eq(DEAL_ID_A), eq(0));
    verify(dealVoteCountManager).updateDealVotes(eq(DEAL_ID_A), eq(-1));
  }
}
