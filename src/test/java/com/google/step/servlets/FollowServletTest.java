package com.google.step.servlets;

import static com.google.step.TestConstants.EMAIL_A;
import static com.google.step.TestConstants.RESTAURANT_ID_A;
import static com.google.step.TestConstants.USER_A;
import static com.google.step.TestConstants.USER_ID_A;
import static com.google.step.TestConstants.USER_ID_B;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.repackaged.com.google.api.client.http.HttpStatusCodes;
import com.google.step.datamanager.FollowManager;
import com.google.step.datamanager.UserManager;
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
public class FollowServletTest {

  private static final long ID = 123;

  private FollowServlet servlet;
  private FollowManager mockFollowManager;
  private UserService mockUserService;
  private UserManager mockUserManager;

  @Before
  public void setUp() {
    mockFollowManager = mock(FollowManager.class);
    mockUserService = mock(UserService.class);
    mockUserManager = mock(UserManager.class);

    servlet = new FollowServlet(mockFollowManager, mockUserService, mockUserManager);
  }

  public void setUpUserAuthentication() {
    // behaviour when user is logged in
    when(mockUserService.isUserLoggedIn()).thenReturn(true);
    User currentUser = new User(EMAIL_A, "");
    when(mockUserService.getCurrentUser()).thenReturn(currentUser);
    when(mockUserManager.readUserByEmail(EMAIL_A)).thenReturn(USER_A);
  }

  @Test
  public void testDoPost_restaurant_success() throws IOException {
    setUpUserAuthentication();
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getPathInfo()).thenReturn("/restaurants/" + ID);

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    servlet.doPost(request, response);

    verify(response, never()).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    verify(mockFollowManager).followRestaurant(anyLong(), eq(ID));
  }

  @Test
  public void testDoPost_tag_success() throws IOException {
    setUpUserAuthentication();
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getPathInfo()).thenReturn("/tags/" + ID);

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    servlet.doPost(request, response);

    verify(response, never()).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    verify(mockFollowManager).followTag(anyLong(), eq(ID));
  }

  @Test
  public void testDoPost_user_success() throws IOException {
    setUpUserAuthentication();
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getPathInfo()).thenReturn("/users/" + ID);

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    servlet.doPost(request, response);

    verify(response, never()).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    verify(mockFollowManager).followUser(anyLong(), eq(ID));
  }

  @Test
  public void testDoPost_invalidName_badRequest() throws IOException {
    setUpUserAuthentication();
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getPathInfo()).thenReturn("/trash/123");

    servlet.doPost(request, response);

    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoPost_invalidId_badRequest() throws IOException {
    setUpUserAuthentication();
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getPathInfo()).thenReturn("/tags/123/trash");

    servlet.doPost(request, response);

    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoPost_missingId_badRequest() throws IOException {
    setUpUserAuthentication();
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getPathInfo()).thenReturn("/");

    servlet.doPost(request, response);

    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoPost_emptyPath_badRequest() throws IOException {
    setUpUserAuthentication();
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getPathInfo()).thenReturn("");

    servlet.doPost(request, response);

    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoGet_isFollowingRestaurant() throws IOException {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getParameter("restaurantId")).thenReturn(RESTAURANT_ID_A + "");
    when(request.getParameter("followerId")).thenReturn(USER_ID_A + "");
    when(mockFollowManager.isFollowingRestaurant(USER_ID_A, RESTAURANT_ID_A)).thenReturn(true);

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    servlet.doGet(request, response);

    assertTrue(stringWriter.toString().contains("true"));
  }

  @Test
  public void testDoGet_isFollowingRestaurant_invalidId() throws IOException {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getParameter("restaurantId")).thenReturn(RESTAURANT_ID_A + "xxx");
    when(request.getParameter("followerId")).thenReturn(USER_ID_A + "");
    when(mockFollowManager.isFollowingRestaurant(USER_ID_A, RESTAURANT_ID_A)).thenReturn(true);

    servlet.doGet(request, response);

    verify(response).setStatus(HttpStatusCodes.STATUS_CODE_BAD_REQUEST);
  }

  @Test
  public void testDoGet_isFollowingUser() throws IOException {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getParameter("userId")).thenReturn(USER_ID_A + "");
    when(request.getParameter("followerId")).thenReturn(USER_ID_B + "");
    when(mockFollowManager.isFollowingUser(USER_ID_B, USER_ID_A)).thenReturn(false);

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    servlet.doGet(request, response);

    assertTrue(stringWriter.toString().contains("false"));
  }

  @Test
  public void testDoGet_isFollowing_invalidParamters() throws IOException {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getParameter("followerId")).thenReturn(USER_ID_B + "");

    servlet.doGet(request, response);

    verify(response).setStatus(HttpStatusCodes.STATUS_CODE_BAD_REQUEST);
  }

  public void testDoPost_userNotLoggedIn_unauthorized() throws IOException {
    when(mockUserService.isUserLoggedIn()).thenReturn(false);
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getPathInfo()).thenReturn("/restaurants/" + ID);

    servlet.doPost(request, response);

    verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
  }

  @Test
  public void testDoDelete_userNotLoggedIn_unauthorized() throws IOException {
    when(mockUserService.isUserLoggedIn()).thenReturn(false);
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getPathInfo()).thenReturn("/restaurants/" + ID);

    servlet.doDelete(request, response);

    verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
  }
}
