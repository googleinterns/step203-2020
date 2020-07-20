package com.google.step.servlets;

import static com.google.step.TestConstants.DEAL_A;
import static com.google.step.TestConstants.EMAIL_A;
import static com.google.step.TestConstants.HOME_DEAL_A_JSON;
import static com.google.step.TestConstants.RESTAURANT_A;
import static com.google.step.TestConstants.TAG_A;
import static com.google.step.TestConstants.USER_A;
import static com.google.step.TestConstants.VOTE_A;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.step.datamanager.DealManager;
import com.google.step.datamanager.DealTagManager;
import com.google.step.datamanager.FollowManager;
import com.google.step.datamanager.RestaurantManager;
import com.google.step.datamanager.TagManager;
import com.google.step.datamanager.UserManager;
import com.google.step.datamanager.VoteManager;
import com.google.step.model.Deal;
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
public class HomePageServletTest {

  private HomePageServlet homePageServlet;
  private DealManager mockDealManager;
  private UserManager mockUserManager;
  private VoteManager mockVoteManager;
  private RestaurantManager mockRestaurantManager;
  private DealTagManager mockDealTagManager;
  private TagManager mockTagManager;
  private FollowManager mockFollowManager;
  private UserService mockUserService;

  @Before
  public void setUp() {
    mockDealManager = mock(DealManager.class);
    mockUserManager = mock(UserManager.class);
    mockVoteManager = mock(VoteManager.class);
    mockRestaurantManager = mock(RestaurantManager.class);
    mockDealTagManager = mock(DealTagManager.class);
    mockTagManager = mock(TagManager.class);
    mockUserService = mock(UserService.class);
    mockFollowManager = mock(FollowManager.class);
    homePageServlet =
        new HomePageServlet(
            mockDealManager,
            mockUserManager,
            mockRestaurantManager,
            mockVoteManager,
            mockDealTagManager,
            mockTagManager,
            mockFollowManager,
            mockUserService);
  }

  private void setUpUserAuthentication() {
    User currentUser = new User(EMAIL_A, "");
    when(mockUserService.isUserLoggedIn()).thenReturn(true);
    when(mockUserService.getCurrentUser()).thenReturn(currentUser);
    when(mockUserManager.readUserByEmail(EMAIL_A)).thenReturn(USER_A);
  }

  private void gettingSectionMaps() {
    when(mockUserManager.readUser(anyLong())).thenReturn(USER_A);
    when(mockRestaurantManager.readRestaurant(anyLong())).thenReturn(RESTAURANT_A);
    when(mockTagManager.readTags(anyList())).thenReturn(Arrays.asList(TAG_A));
    when(mockVoteManager.getVotes(anyLong())).thenReturn(VOTE_A);
  }

  @Test
  public void testDoGetAllDeals_success() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    List<Deal> DEALS = new ArrayList<Deal>(Arrays.asList(DEAL_A, DEAL_A, DEAL_A));

    when(request.getParameter("section")).thenReturn(null);

    setUpUserAuthentication();

    when(mockDealManager.getAllDeals()).thenReturn(DEALS);
    when(mockDealManager.getDealsPublishedByUsers(anySet())).thenReturn(DEALS);
    when(mockDealManager.getDealsPublishedByRestaurants(anySet())).thenReturn(DEALS);
    when(mockDealManager.readDeals(anyList())).thenReturn(DEALS);

    gettingSectionMaps();

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    homePageServlet.doGet(request, response);

    String expectedDeals =
        String.format("[%s,%s,%s]", HOME_DEAL_A_JSON, HOME_DEAL_A_JSON, HOME_DEAL_A_JSON);
    String expected =
        String.format(
            "{trending:%s," + "users:%s," + "restaurants:%s," + "tags:%s}",
            expectedDeals, expectedDeals, expectedDeals, expectedDeals);
    JSONAssert.assertEquals(expected, stringWriter.toString(), JSONCompareMode.STRICT);
  }

  @Test
  public void testDoGet_UserLoggedInViewSection_success() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    List<Deal> DEALS = new ArrayList<Deal>(Arrays.asList(DEAL_A, DEAL_A, DEAL_A));

    when(request.getParameter("section")).thenReturn("users");
    setUpUserAuthentication();
    when(mockDealManager.getDealsPublishedByUsers(anySet())).thenReturn(DEALS);

    gettingSectionMaps();

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    homePageServlet.doGet(request, response);

    String expected =
        String.format("[%s,%s,%s]", HOME_DEAL_A_JSON, HOME_DEAL_A_JSON, HOME_DEAL_A_JSON);
    JSONAssert.assertEquals(expected, stringWriter.toString(), JSONCompareMode.STRICT);
  }

  @Test
  public void testDoGet_UserNotLoggedInAllDeals() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    List<Deal> DEALS = new ArrayList<Deal>(Arrays.asList(DEAL_A, DEAL_A, DEAL_A));

    when(request.getParameter("section")).thenReturn(null);
    when(mockUserService.isUserLoggedIn()).thenReturn(false);
    when(mockDealManager.getAllDeals()).thenReturn(DEALS);

    gettingSectionMaps();

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    homePageServlet.doGet(request, response);

    String trendingDeals =
        String.format("[%s,%s,%s]", HOME_DEAL_A_JSON, HOME_DEAL_A_JSON, HOME_DEAL_A_JSON);
    String expected = String.format("{trending:%s}", trendingDeals);
    JSONAssert.assertEquals(expected, stringWriter.toString(), JSONCompareMode.STRICT);
  }

  @Test
  public void testDoGet_UserNotLoggedInViewTrending() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    List<Deal> DEALS = new ArrayList<Deal>(Arrays.asList(DEAL_A, DEAL_A, DEAL_A));

    when(mockUserService.isUserLoggedIn()).thenReturn(false);
    when(request.getParameter("section")).thenReturn("trending");
    when(mockDealManager.getAllDeals()).thenReturn(DEALS);

    gettingSectionMaps();

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    homePageServlet.doGet(request, response);

    String expected =
        String.format("[%s,%s,%s]", HOME_DEAL_A_JSON, HOME_DEAL_A_JSON, HOME_DEAL_A_JSON);

    JSONAssert.assertEquals(expected, stringWriter.toString(), JSONCompareMode.STRICT);
  }

  @Test
  public void testDoGet_UserNotLoggedInViewOtherSection() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(mockUserService.isUserLoggedIn()).thenReturn(false);
    when(request.getParameter("section")).thenReturn("users");

    gettingSectionMaps();

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    homePageServlet.doGet(request, response);
    verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
  }
}
