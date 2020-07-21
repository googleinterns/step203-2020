package com.google.step.servlets;

import static com.google.step.TestConstants.BLOBKEY_URL_A;
import static com.google.step.TestConstants.DEAL_A;
import static com.google.step.TestConstants.DEAL_ID_A;
import static com.google.step.TestConstants.DEAL_ID_B;
import static com.google.step.TestConstants.EMAIL_A;
import static com.google.step.TestConstants.EMAIL_B;
import static com.google.step.TestConstants.RESTAURANT_A;
import static com.google.step.TestConstants.TAG_A;
import static com.google.step.TestConstants.TAG_B;
import static com.google.step.TestConstants.USER_A;
import static com.google.step.TestConstants.USER_B;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.step.datamanager.DealManager;
import com.google.step.datamanager.RestaurantManager;
import com.google.step.datamanager.UserManager;
import com.google.step.datamanager.VoteManager;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

@RunWith(JUnit4.class)
public class DealDetailServletTest {

  private static final int NUM_VOTES = 123;
  private static final String PATH_A = "/" + DEAL_ID_A;
  private static final String PATH_B = "/" + DEAL_ID_B;

  private HttpServletRequest mockRequest;
  private DealDetailServlet servlet;
  private DealManager mockDealManager;
  private UserManager mockUserManager;
  private VoteManager mockVoteManager;
  private RestaurantManager mockRestaurantManager;
  private UserService mockUserService;
  private HttpServletResponse mockResponse;
  private StringWriter stringWriter;
  private PrintWriter writer;

  @Before
  public void setUp() throws IOException {
    mockDealManager = mock(DealManager.class);
    mockUserManager = mock(UserManager.class);
    mockVoteManager = mock(VoteManager.class);
    mockRestaurantManager = mock(RestaurantManager.class);
    mockUserService = mock(UserService.class);
    mockRequest = mock(HttpServletRequest.class);

    // mock response
    mockResponse = mock(HttpServletResponse.class);
    stringWriter = new StringWriter();
    writer = new PrintWriter(stringWriter);
    when(mockResponse.getWriter()).thenReturn(writer);

    // behaviour when user is logged in
    when(mockUserService.isUserLoggedIn()).thenReturn(true);
    User currentUser = new User(EMAIL_A, "");
    when(mockUserService.getCurrentUser()).thenReturn(currentUser);
    when(mockUserManager.readUserByEmail(EMAIL_A)).thenReturn(USER_A);
    when(mockUserManager.readUser(USER_A.id)).thenReturn(USER_A);

    // mock various managers
    when(mockDealManager.readDeal(DEAL_A.id)).thenReturn(DEAL_A);
    when(mockRestaurantManager.readRestaurant(DEAL_A.restaurantId)).thenReturn(RESTAURANT_A);
    when(mockVoteManager.getVotes(DEAL_A.id)).thenReturn(NUM_VOTES);
    when(mockDealManager.getTags(DEAL_A.id)).thenReturn(Arrays.asList(TAG_A, TAG_B));

    servlet =
        new DealDetailServlet(
            mockDealManager,
            mockUserManager,
            mockVoteManager,
            mockRestaurantManager,
            mockUserService);
  }

  @Test
  public void testDoGet_success() throws IOException, JSONException {
    when(mockRequest.getPathInfo()).thenReturn(PATH_A);

    servlet.doGet(mockRequest, mockResponse);

    String expectedJson =
        String.format(
            "{"
                + "    \"id\": %d,"
                + "    \"description\": \"%s\","
                + "    \"start\": \"%s\","
                + "    \"end\": \"%s\","
                + "    \"source\": \"%s\","
                + "    \"image\": \"%s\","
                + "    \"votes\": %d,"
                + "    \"restaurant\": {"
                + "        \"id\": %d,"
                + "        \"name\": \"%s\","
                + "        \"image\": \"%s\""
                + "    },"
                + "    \"poster\": {"
                + "        \"id\": %d,"
                + "        \"username\": \"%s\","
                + "        \"picture\": \"%s\""
                + "    },"
                + "    \"tags\": ["
                + "        {"
                + "            \"name\": \"%s\","
                + "            \"id\": %d"
                + "        },"
                + "        {"
                + "            \"name\": \"%s\","
                + "            \"id\": %d"
                + "        }"
                + "    ]"
                + "}",
            DEAL_A.id,
            DEAL_A.description,
            DEAL_A.start,
            DEAL_A.end,
            DEAL_A.source,
            BLOBKEY_URL_A,
            NUM_VOTES,
            RESTAURANT_A.id,
            RESTAURANT_A.name,
            BLOBKEY_URL_A,
            USER_A.id,
            USER_A.username,
            BLOBKEY_URL_A,
            TAG_A.name,
            TAG_A.id,
            TAG_B.name,
            TAG_B.id);

    verify(mockResponse).setStatus(HttpServletResponse.SC_OK);
    JSONAssert.assertEquals(expectedJson, stringWriter.toString(), JSONCompareMode.STRICT);
  }

  @Test
  public void testDoGet_notFound() throws IOException {
    when(mockRequest.getPathInfo()).thenReturn(PATH_B);

    servlet.doGet(mockRequest, mockResponse);

    verify(mockResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);
  }

  @Test
  public void testDoDelete_success() throws IOException {
    when(mockRequest.getPathInfo()).thenReturn(PATH_A);

    servlet.doDelete(mockRequest, mockResponse);

    verify(mockResponse).setStatus(HttpServletResponse.SC_OK);
    verify(mockDealManager).deleteDeal(DEAL_ID_A);
  }

  @Test
  public void testDoDelete_unauthorised() throws IOException {
    when(mockRequest.getPathInfo()).thenReturn(PATH_A);
    User currentUser = new User(EMAIL_B, "");
    when(mockUserService.getCurrentUser()).thenReturn(currentUser);
    when(mockUserManager.readUserByEmail(EMAIL_B)).thenReturn(USER_B);

    servlet.doDelete(mockRequest, mockResponse);

    verify(mockResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
  }

  @Test
  public void testDoDelete_notFound() throws IOException {
    when(mockRequest.getPathInfo()).thenReturn(PATH_B);

    servlet.doDelete(mockRequest, mockResponse);

    verify(mockResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);
  }
}
