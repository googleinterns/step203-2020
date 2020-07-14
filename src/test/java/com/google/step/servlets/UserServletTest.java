package com.google.step.servlets;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.appengine.api.users.UserService;
import com.google.step.datamanager.FollowManager;
import com.google.step.datamanager.TagManager;
import com.google.step.datamanager.UserManager;
import com.google.step.model.User;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

@RunWith(JUnit4.class)
public class UserServletTest {

  private static final long ID_A = 1;
  private static final long ID_B = 2;

  private static final String EMAIL_A = "testa@example.com";
  private static final String EMAIL_B = "testb@example.com";

  private static final String USERNAME_A = "Alice";
  private static final String USERNAME_A_NEW = "AliceW";
  private static final String USERNAME_B = "Bob";

  private static final String BLOBKEY_A = "a_blob_key";

  private static final String BIO_A = "Hello world.";
  private static final String BIO_A_NEW = "Hi, I'm Alice";
  private static final String BIO_B = "Hello world.";

  private static final User USER_A = new User(ID_A, EMAIL_A, USERNAME_A, BLOBKEY_A, BIO_A);
  private static final User USER_B = new User(ID_B, EMAIL_B, USERNAME_B, BIO_B);

  private UserServlet mockUserServlet;
  private UserManager mockUserManager;
  private UserService mockUserService;
  private TagManager mockTagManager;
  private FollowManager mockFollowManager;

  @Before
  public void setUp() {
    mockUserManager = mock(UserManager.class);
    mockUserService = mock(UserService.class);
    mockTagManager = mock(TagManager.class);
    mockFollowManager = mock(FollowManager.class);
    mockUserServlet =
        new UserServlet(mockUserManager, mockUserService, mockTagManager, mockFollowManager);
  }

  @Test
  public void testDoGet_success() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    when(request.getPathInfo()).thenReturn("/1");
    when(mockUserManager.readUser(1)).thenReturn(USER_A);
    when(mockFollowManager.getFollowedTagIds(1)).thenReturn(new ArrayList<Long>());

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    mockUserServlet.doGet(request, response);

    writer.flush();

    String expected =
        String.format(
            "{id:%d,email:\"%s\",username:\"%s\",bio:\"%s\",photoBlobKey:\"%s\","
                + "dealsUploaded:[],"
                + "following:[],"
                + "followers:[],"
                + "tagsFollowed:[],"
                + "restaurantsFollowed:[]}",
            ID_A, EMAIL_A, USERNAME_A, BIO_A, BLOBKEY_A);

    JSONAssert.assertEquals(expected, stringWriter.toString(), JSONCompareMode.STRICT);
  }

  @Test
  public void testDoGet_notExist() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    when(request.getPathInfo()).thenReturn("/1000");
    when(mockUserManager.readUser(1000)).thenThrow(new IllegalArgumentException());

    mockUserServlet.doGet(request, response);
    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoGet_invalidId() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    when(request.getPathInfo()).thenReturn("/100x00");

    mockUserServlet.doGet(request, response);

    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoGet_noId() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    when(request.getPathInfo()).thenReturn("");

    mockUserServlet.doGet(request, response);

    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoPost_userNotLoggedIn() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    when(mockUserService.isUserLoggedIn()).thenReturn(false);

    mockUserServlet.doPost(request, response);
    verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
  }

  @Test
  public void testDoPost_success() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    when(mockUserService.isUserLoggedIn()).thenReturn(true);
    com.google.appengine.api.users.User currentUser =
        new com.google.appengine.api.users.User(EMAIL_A, "");
    when(mockUserService.getCurrentUser()).thenReturn(currentUser);
    when(request.getParameter("username")).thenReturn(USERNAME_A_NEW);
    when(request.getParameter("bio")).thenReturn(BIO_A_NEW);
    when(mockUserManager.readUser(1)).thenReturn(USER_A);
    when(request.getPathInfo()).thenReturn("/" + String.valueOf(ID_A));
    when(mockUserManager.readUserByEmail(EMAIL_A)).thenReturn(USER_A);

    mockUserServlet.doPost(request, response);
    User updatedUser = new User(ID_A, null, USERNAME_A_NEW, null, BIO_A_NEW);
    ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
    verify(mockUserManager).updateUser(captor.capture());
    assertEquals(updatedUser, captor.getValue());
    verify(response).sendRedirect("/user/" + ID_A);
  }

  @Test
  public void testDoPost_inconsistentUser() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    when(mockUserService.isUserLoggedIn()).thenReturn(true);
    com.google.appengine.api.users.User currentUser =
        new com.google.appengine.api.users.User(EMAIL_A, "");
    when(mockUserService.getCurrentUser()).thenReturn(currentUser);
    when(request.getParameter("username")).thenReturn(null);
    when(request.getParameter("bio")).thenReturn(BIO_A_NEW);
    when(mockUserManager.readUser(2)).thenReturn(USER_B);
    when(request.getPathInfo()).thenReturn("/" + String.valueOf(ID_B));
    when(mockUserManager.readUserByEmail(EMAIL_A)).thenReturn(USER_A);

    mockUserServlet.doPost(request, response);
    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }
}
