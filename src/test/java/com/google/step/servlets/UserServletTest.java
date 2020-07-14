package com.google.step.servlets;

import static com.google.step.TestConstants.USER_ID_A;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.appengine.api.users.UserService;
import com.google.step.datamanager.DealManager;
import com.google.step.datamanager.FollowManager;
import com.google.step.datamanager.RestaurantManager;
import com.google.step.datamanager.TagManager;
import com.google.step.datamanager.UserManager;
import com.google.step.model.Deal;
import com.google.step.model.Restaurant;
import com.google.step.model.Tag;
import com.google.step.model.User;
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

  private UserServlet servlet;
  private UserManager userManager;
  private UserService userService;
  private TagManager tagManager;
  private FollowManager followManager;
  private DealManager dealManager;
  private RestaurantManager restaurantManager;

  @Before
  public void setUp() {
    userManager = mock(UserManager.class);
    userService = mock(UserService.class);
    tagManager = mock(TagManager.class);
    followManager = mock(FollowManager.class);
    dealManager = mock(DealManager.class);
    restaurantManager = mock(RestaurantManager.class);
    servlet =
        new UserServlet(
            userManager, userService, dealManager, followManager, tagManager, restaurantManager);
  }

  @Test
  public void testDoGet_success() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    when(request.getPathInfo()).thenReturn("/" + USER_ID_A);
    when(userManager.readUser(USER_ID_A)).thenReturn(USER_A);

    List<Deal> deals = new ArrayList<>();
    when(dealManager.getDealsPublishedByUser(USER_ID_A)).thenReturn(deals);

    List<Long> followingIds = new ArrayList<>();
    when(followManager.getFollowedUserIds(USER_ID_A)).thenReturn(followingIds);
    List<User> following = new ArrayList<>();
    when(userManager.readUsers(followingIds)).thenReturn(following);

    List<Long> followerIds = new ArrayList<>();
    when(followManager.getFollowerIdsOfUser(USER_ID_A)).thenReturn(followerIds);
    List<User> followers = new ArrayList<>();
    when(userManager.readUsers(followerIds)).thenReturn(followers);

    List<Long> tagIds = new ArrayList<>();
    when(followManager.getFollowedTagIds(USER_ID_A)).thenReturn(tagIds);
    List<Tag> tags = new ArrayList<>();
    when(tagManager.readTags(tagIds)).thenReturn(tags);

    List<Long> restaurantIds = new ArrayList<>();
    when(followManager.getFollowedRestaurantIds(USER_ID_A)).thenReturn(restaurantIds);
    List<Restaurant> restaurants = new ArrayList<>();
    when(restaurantManager.readRestaurants(restaurantIds)).thenReturn(restaurants);

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    servlet.doGet(request, response);

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
    when(userManager.readUser(1000)).thenThrow(new IllegalArgumentException());

    servlet.doGet(request, response);
    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoGet_invalidId() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    when(request.getPathInfo()).thenReturn("/100x00");

    servlet.doGet(request, response);

    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoGet_noId() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    when(request.getPathInfo()).thenReturn("");

    servlet.doGet(request, response);

    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoPost_userNotLoggedIn() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    when(userService.isUserLoggedIn()).thenReturn(false);

    servlet.doPost(request, response);
    verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
  }

  @Test
  public void testDoPost_success() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    when(userService.isUserLoggedIn()).thenReturn(true);
    com.google.appengine.api.users.User currentUser =
        new com.google.appengine.api.users.User(EMAIL_A, "");
    when(userService.getCurrentUser()).thenReturn(currentUser);
    when(request.getParameter("username")).thenReturn(USERNAME_A_NEW);
    when(request.getParameter("bio")).thenReturn(BIO_A_NEW);
    when(userManager.readUser(1)).thenReturn(USER_A);
    when(request.getPathInfo()).thenReturn("/" + String.valueOf(ID_A));
    when(userManager.readUserByEmail(EMAIL_A)).thenReturn(USER_A);

    servlet.doPost(request, response);
    User updatedUser = new User(ID_A, null, USERNAME_A_NEW, null, BIO_A_NEW);
    ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
    verify(userManager).updateUser(captor.capture());
    assertEquals(updatedUser, captor.getValue());
    verify(response).sendRedirect("/user/" + ID_A);
  }

  @Test
  public void testDoPost_inconsistentUser() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    when(userService.isUserLoggedIn()).thenReturn(true);
    com.google.appengine.api.users.User currentUser =
        new com.google.appengine.api.users.User(EMAIL_A, "");
    when(userService.getCurrentUser()).thenReturn(currentUser);
    when(request.getParameter("username")).thenReturn(null);
    when(request.getParameter("bio")).thenReturn(BIO_A_NEW);
    when(userManager.readUser(2)).thenReturn(USER_B);
    when(request.getPathInfo()).thenReturn("/" + String.valueOf(ID_B));
    when(userManager.readUserByEmail(EMAIL_A)).thenReturn(USER_A);

    servlet.doPost(request, response);
    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }
}
