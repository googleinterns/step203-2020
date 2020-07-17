package com.google.step.servlets;

import static com.google.step.TestConstants.BIO_A;
import static com.google.step.TestConstants.BIO_A_NEW;
import static com.google.step.TestConstants.BLOBKEY_A;
import static com.google.step.TestConstants.BLOBKEY_URL_A;
import static com.google.step.TestConstants.EMAIL_A;
import static com.google.step.TestConstants.TAG_A;
import static com.google.step.TestConstants.TAG_B;
import static com.google.step.TestConstants.TAG_C;
import static com.google.step.TestConstants.TAG_ID_A;
import static com.google.step.TestConstants.TAG_ID_B;
import static com.google.step.TestConstants.TAG_ID_C;
import static com.google.step.TestConstants.TAG_LIST_ABC;
import static com.google.step.TestConstants.TAG_NAME_A;
import static com.google.step.TestConstants.TAG_NAME_B;
import static com.google.step.TestConstants.TAG_NAME_C;
import static com.google.step.TestConstants.USERNAME_A;
import static com.google.step.TestConstants.USERNAME_A_NEW;
import static com.google.step.TestConstants.USER_A;
import static com.google.step.TestConstants.USER_B;
import static com.google.step.TestConstants.USER_ID_A;
import static com.google.step.TestConstants.USER_ID_B;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.validateMockitoUsage;
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ImageUploader.class)
public class UserServletTest {

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
  
  @After
  public void validate() {
    validateMockitoUsage();
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

    userServlet.doGet(request, response);

    writer.flush();

    String expected =
        String.format(
            "{id:%d,email:\"%s\",username:\"%s\",bio:\"%s\",picture:\"%s\","
                + "dealsUploaded:[],"
                + "following:[],"
                + "followers:[],"
                + "tagsFollowed:[],"
                + "restaurantsFollowed:[]}",
            USER_ID_A, EMAIL_A, USERNAME_A, BIO_A, BLOBKEY_URL_A);

    JSONAssert.assertEquals(expected, stringWriter.toString(), JSONCompareMode.STRICT);
  }

  @Test
  public void testDoGet_notExist() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    when(request.getPathInfo()).thenReturn("/1000");
    when(mockUserManager.readUser(1000)).thenThrow(new IllegalArgumentException());

    userServlet.doGet(request, response);
    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoGet_invalidId() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    when(request.getPathInfo()).thenReturn("/100x00");

    userServlet.doGet(request, response);

    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoGet_noId() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    when(request.getPathInfo()).thenReturn("");

    userServlet.doGet(request, response);

    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoPost_userNotLoggedIn() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    when(mockUserService.isUserLoggedIn()).thenReturn(false);

    userServlet.doPost(request, response);
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
    when(request.getPathInfo()).thenReturn("/" + String.valueOf(USER_ID_A));
    when(mockUserManager.readUserByEmail(EMAIL_A)).thenReturn(USER_A);
    PowerMockito.mockStatic(ImageUploader.class);
    BDDMockito.given(ImageUploader.getUploadedImageBlobkey(request, "picture"))
        .willReturn(BLOBKEY_A);

    userServlet.doPost(request, response);
    User updatedUser = new User(USER_ID_A, null, USERNAME_A_NEW, BLOBKEY_A, BIO_A_NEW);
    ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
    verify(mockUserManager).updateUser(captor.capture());
    assertEquals(updatedUser, captor.getValue());
    verify(response).sendRedirect("/user/" + USER_ID_A);
  }

  @Test
  public void testDoPost_updateTagsFollowed() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    when(mockUserService.isUserLoggedIn()).thenReturn(true);
    com.google.appengine.api.users.User currentUser =
        new com.google.appengine.api.users.User(EMAIL_A, "");
    when(mockUserService.getCurrentUser()).thenReturn(currentUser);
    when(mockUserManager.readUser(1)).thenReturn(USER_A);
    when(request.getPathInfo()).thenReturn("/" + String.valueOf(USER_ID_A));
    when(mockUserManager.readUserByEmail(EMAIL_A)).thenReturn(USER_A);
    when(request.getParameter("tags")).thenReturn(TAG_LIST_ABC);
    when(mockTagManager.readOrCreateTagByName(TAG_NAME_A)).thenReturn(TAG_A);
    when(mockTagManager.readOrCreateTagByName(TAG_NAME_B)).thenReturn(TAG_B);
    when(mockTagManager.readOrCreateTagByName(TAG_NAME_C)).thenReturn(TAG_C);

    PowerMockito.mockStatic(ImageUploader.class);
    BDDMockito.given(ImageUploader.getUploadedImageBlobkey(request, "picture"))
        .willReturn(BLOBKEY_A);

    userServlet.doPost(request, response);

    verify(response).sendRedirect("/user/" + USER_ID_A);
    verify(mockTagManager).readOrCreateTagByName(TAG_NAME_A);
    verify(mockTagManager).readOrCreateTagByName(TAG_NAME_B);
    verify(mockTagManager).readOrCreateTagByName(TAG_NAME_C);
    verify(mockFollowManager)
        .updateFollowedTagIds(USER_ID_A, Arrays.asList(TAG_ID_A, TAG_ID_B, TAG_ID_C));
  }

  @Test
  public void testDoPost_doNotUpdatePhoto() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    when(mockUserService.isUserLoggedIn()).thenReturn(true);
    com.google.appengine.api.users.User currentUser =
        new com.google.appengine.api.users.User(EMAIL_A, "");
    when(mockUserService.getCurrentUser()).thenReturn(currentUser);
    when(request.getParameter("username")).thenReturn(USERNAME_A_NEW);
    when(request.getParameter("bio")).thenReturn(BIO_A_NEW);
    when(mockUserManager.readUser(1)).thenReturn(USER_A);
    when(request.getPathInfo()).thenReturn("/" + String.valueOf(USER_ID_A));
    when(mockUserManager.readUserByEmail(EMAIL_A)).thenReturn(USER_A);
    PowerMockito.mockStatic(ImageUploader.class);
    BDDMockito.given(ImageUploader.getUploadedImageBlobkey(request, "profile-photo-file"))
        .willReturn(null);

    userServlet.doPost(request, response);
    User updatedUser = new User(USER_ID_A, null, USERNAME_A_NEW, null, BIO_A_NEW);
    ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
    verify(mockUserManager).updateUser(captor.capture());
    assertEquals(updatedUser, captor.getValue());
    verify(response).sendRedirect("/user/" + USER_ID_A);
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
    when(request.getPathInfo()).thenReturn("/" + String.valueOf(USER_ID_B));
    when(mockUserManager.readUserByEmail(EMAIL_A)).thenReturn(USER_A);

    userServlet.doPost(request, response);
    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }
}
