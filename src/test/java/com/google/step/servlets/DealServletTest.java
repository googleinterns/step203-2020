package com.google.step.servlets;

import static com.google.step.TestConstants.BLOBKEY_A;
import static com.google.step.TestConstants.DATE_A;
import static com.google.step.TestConstants.DATE_B;
import static com.google.step.TestConstants.DEAL_A;
import static com.google.step.TestConstants.DEAL_A_BRIEF_JSON;
import static com.google.step.TestConstants.DEAL_B;
import static com.google.step.TestConstants.DEAL_B_BRIEF_JSON;
import static com.google.step.TestConstants.DEAL_ID_A;
import static com.google.step.TestConstants.DESCRIPTION_A;
import static com.google.step.TestConstants.EMAIL_A;
import static com.google.step.TestConstants.RESTAURANT_A;
import static com.google.step.TestConstants.RESTAURANT_ID_A;
import static com.google.step.TestConstants.SOURCE_A;
import static com.google.step.TestConstants.TAG_NAME_A;
import static com.google.step.TestConstants.TAG_NAME_B;
import static com.google.step.TestConstants.USER_A;
import static com.google.step.TestConstants.USER_B;
import static com.google.step.TestConstants.USER_ID_A;
import static com.google.step.TestConstants.USER_ID_B;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.step.datamanager.DealManager;
import com.google.step.datamanager.FollowManager;
import com.google.step.datamanager.MailManager;
import com.google.step.datamanager.RestaurantManager;
import com.google.step.datamanager.UserManager;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ImageUploader.class)
public class DealServletTest {

  private static final String RESTAURANT_ID_A_STRING = Long.toString(RESTAURANT_ID_A);

  private HttpServletRequest mockRequest;
  private DealServlet servlet;
  private DealManager mockDealManager;
  private UserService mockUserService;
  private UserManager mockUserManager;
  private RestaurantManager mockRestaurantManager;
  private MailManager mockMailManager;
  private FollowManager mockFollowManager;
  private HttpServletResponse mockResponse;
  private StringWriter stringWriter;
  private PrintWriter writer;

  @Before
  public void setUp() throws IOException {
    mockRequest = mock(HttpServletRequest.class);

    PowerMockito.mockStatic(ImageUploader.class);
    BDDMockito.given(ImageUploader.getUploadedImageBlobkey(eq(mockRequest), anyString()))
        .willReturn(BLOBKEY_A);

    mockDealManager = mock(DealManager.class);
    mockUserService = mock(UserService.class);
    mockUserManager = mock(UserManager.class);
    mockRestaurantManager = mock(RestaurantManager.class);
    mockFollowManager = mock(FollowManager.class);
    mockMailManager = mock(MailManager.class);
    User currentUser = new User(EMAIL_A, "");

    // mock response
    mockResponse = mock(HttpServletResponse.class);
    stringWriter = new StringWriter();
    writer = new PrintWriter(stringWriter);
    when(mockResponse.getWriter()).thenReturn(writer);

    // default request parameter for success case
    when(mockRequest.getParameter("description")).thenReturn(DESCRIPTION_A);
    when(mockRequest.getParameter("start")).thenReturn(DATE_A);
    when(mockRequest.getParameter("end")).thenReturn(DATE_B);
    when(mockRequest.getParameter("source")).thenReturn(SOURCE_A);
    when(mockRequest.getParameter("restaurant")).thenReturn(RESTAURANT_ID_A_STRING);
    when(mockRequest.getParameter("tags")).thenReturn("");

    // behaviour when user is logged in
    when(mockUserService.isUserLoggedIn()).thenReturn(true);
    when(mockUserService.getCurrentUser()).thenReturn(currentUser);
    when(mockUserManager.readUserByEmail(EMAIL_A)).thenReturn(USER_A);

    // mock restaurant manager
    when(mockRestaurantManager.readRestaurant(RESTAURANT_ID_A)).thenReturn(RESTAURANT_A);

    servlet =
        new DealServlet(
            mockDealManager,
            mockUserManager,
            mockUserService,
            mockRestaurantManager,
            mockFollowManager,
            mockMailManager);
  }

  @Test
  public void testDoPost_success() throws IOException {
    when(mockDealManager.createDeal(
            eq(DESCRIPTION_A),
            anyString(),
            eq(DATE_A),
            eq(DATE_B),
            eq(SOURCE_A),
            anyLong(),
            eq(RESTAURANT_ID_A),
            eq(new ArrayList<>())))
        .thenReturn(DEAL_A);

    List<Long> followerIds = Arrays.asList(USER_ID_B);
    when(mockFollowManager.getFollowerIdsOfUser(USER_ID_A)).thenReturn(new HashSet<>(followerIds));
    when(mockUserManager.readUsers(followerIds)).thenReturn(Arrays.asList(USER_B));
    when(mockRequest.getParameter("notify-followers")).thenReturn("true");

    servlet.doPost(mockRequest, mockResponse);

    verify(mockDealManager)
        .createDeal(
            eq(DESCRIPTION_A),
            anyString(),
            eq(DATE_A),
            eq(DATE_B),
            eq(SOURCE_A),
            anyLong(),
            eq(RESTAURANT_ID_A),
            eq(new ArrayList<>()));
    verify(mockResponse).sendRedirect(any());

    verify(mockMailManager).sendNewPostNotificationMail(Arrays.asList(USER_B), DEAL_A, USER_A);
  }

  @Test
  public void testDoPost_success_noNotification() throws IOException {
    when(mockDealManager.createDeal(
            eq(DESCRIPTION_A),
            anyString(),
            eq(DATE_A),
            eq(DATE_B),
            eq(SOURCE_A),
            anyLong(),
            eq(RESTAURANT_ID_A),
            eq(new ArrayList<>())))
        .thenReturn(DEAL_A);

    List<Long> followerIds = Arrays.asList(USER_ID_B);
    when(mockFollowManager.getFollowerIdsOfUser(USER_ID_A)).thenReturn(new HashSet<>(followerIds));
    when(mockUserManager.readUsers(followerIds)).thenReturn(Arrays.asList(USER_B));

    servlet.doPost(mockRequest, mockResponse);

    verify(mockDealManager)
        .createDeal(
            eq(DESCRIPTION_A),
            anyString(),
            eq(DATE_A),
            eq(DATE_B),
            eq(SOURCE_A),
            anyLong(),
            eq(RESTAURANT_ID_A),
            eq(new ArrayList<>()));
    verify(mockResponse).sendRedirect(any());
    verify(mockMailManager, never()).sendNewPostNotificationMail(any(), any(), any());
  }

  @Test
  public void testDoPost_userNotLoggedIn_unauthorized() throws IOException {
    when(mockUserService.isUserLoggedIn()).thenReturn(false);
    when(mockUserService.getCurrentUser()).thenReturn(null);

    servlet.doPost(mockRequest, mockResponse);

    verify(mockResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
  }

  @Test
  public void testDoPost_descriptionNull_badRequest() throws IOException {
    when(mockRequest.getParameter("description")).thenReturn(null);

    servlet.doPost(mockRequest, mockResponse);

    verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoPost_descriptionEmpty_badRequest() throws IOException {
    when(mockRequest.getParameter("description")).thenReturn("");

    servlet.doPost(mockRequest, mockResponse);

    verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoPost_dateNull_badRequest() throws IOException {
    when(mockRequest.getParameter("start")).thenReturn(null);

    servlet.doPost(mockRequest, mockResponse);

    verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoPost_dateInvalid_badRequest() throws IOException {
    when(mockRequest.getParameter("end")).thenReturn("trash");

    servlet.doPost(mockRequest, mockResponse);

    verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoPost_dateWrongFormat_badRequest() throws IOException {
    when(mockRequest.getParameter("start")).thenReturn("2020-1-1");

    servlet.doPost(mockRequest, mockResponse);

    verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoPost_dateWrongOrder_badRequest() throws IOException {
    when(mockRequest.getParameter("start")).thenReturn(DATE_B);
    when(mockRequest.getParameter("end")).thenReturn(DATE_A);

    servlet.doPost(mockRequest, mockResponse);

    verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoPost_restaurantInvalid_badRequest() throws IOException {
    when(mockRequest.getParameter("restaurant")).thenReturn("aaa");

    servlet.doPost(mockRequest, mockResponse);

    verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoGet() throws IOException, JSONException {
    when(mockDealManager.getAllDeals()).thenReturn(Arrays.asList(DEAL_A, DEAL_B));

    servlet.doGet(mockRequest, mockResponse);

    verify(mockResponse).setContentType("application/json;");
    verify(mockResponse).setStatus(HttpServletResponse.SC_ACCEPTED);
    String expectedJson = "[" + DEAL_A_BRIEF_JSON + "," + DEAL_B_BRIEF_JSON + "]";
    JSONAssert.assertEquals(expectedJson, stringWriter.toString(), JSONCompareMode.STRICT);
  }

  @Test
  public void testDoPost_restaurantNotFound_badRequest() throws IOException {
    when(mockRequest.getParameter("restaurant")).thenReturn("100");

    servlet.doPost(mockRequest, mockResponse);

    verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoPost_tags() throws IOException {
    when(mockRequest.getParameter("tags")).thenReturn(TAG_NAME_A + "," + TAG_NAME_B);

    when(mockDealManager.createDeal(
            eq(DESCRIPTION_A),
            anyString(),
            eq(DATE_A),
            eq(DATE_B),
            eq(SOURCE_A),
            anyLong(),
            eq(RESTAURANT_ID_A),
            eq(Arrays.asList(TAG_NAME_A, TAG_NAME_B))))
        .thenReturn(DEAL_A);

    servlet.doPost(mockRequest, mockResponse);

    verify(mockDealManager)
        .createDeal(
            eq(DESCRIPTION_A),
            anyString(),
            eq(DATE_A),
            eq(DATE_B),
            eq(SOURCE_A),
            anyLong(),
            eq(RESTAURANT_ID_A),
            eq(Arrays.asList(TAG_NAME_A, TAG_NAME_B)));
    verify(mockResponse).sendRedirect("/deals/" + DEAL_ID_A);
  }
}
