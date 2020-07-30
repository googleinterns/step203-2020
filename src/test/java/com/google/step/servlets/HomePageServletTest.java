package com.google.step.servlets;

import static com.google.step.TestConstants.DEAL_A;
import static com.google.step.TestConstants.DEAL_B;
import static com.google.step.TestConstants.DEAL_C;
import static com.google.step.TestConstants.DEAL_ID_A;
import static com.google.step.TestConstants.DEAL_ID_B;
import static com.google.step.TestConstants.DEAL_ID_C;
import static com.google.step.TestConstants.EMAIL_A;
import static com.google.step.TestConstants.HOME_DEAL_A_JSON;
import static com.google.step.TestConstants.HOME_DEAL_B_JSON;
import static com.google.step.TestConstants.HOME_DEAL_C_JSON;
import static com.google.step.TestConstants.LATITUDE;
import static com.google.step.TestConstants.LONGITUDE;
import static com.google.step.TestConstants.REAL_PLACE_ID_A;
import static com.google.step.TestConstants.REAL_PLACE_ID_B;
import static com.google.step.TestConstants.REAL_PLACE_ID_C;
import static com.google.step.TestConstants.REAL_PLACE_ID_D;
import static com.google.step.TestConstants.RESTAURANT_A;
import static com.google.step.TestConstants.TAG_A;
import static com.google.step.TestConstants.USER_A;
import static com.google.step.TestConstants.VOTE_A;
import static com.google.step.TestConstants.VOTE_B;
import static com.google.step.TestConstants.VOTE_C;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.step.datamanager.DealManager;
import com.google.step.datamanager.DealTagManager;
import com.google.step.datamanager.DealVoteCountManager;
import com.google.step.datamanager.FollowManager;
import com.google.step.datamanager.RestaurantManager;
import com.google.step.datamanager.RestaurantPlaceManager;
import com.google.step.datamanager.TagManager;
import com.google.step.datamanager.UserManager;
import com.google.step.model.Deal;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
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
  private RestaurantManager mockRestaurantManager;
  private DealTagManager mockDealTagManager;
  private TagManager mockTagManager;
  private FollowManager mockFollowManager;
  private DealVoteCountManager mockDealVoteCountManager;
  private RestaurantPlaceManager mockRestaurantPlaceManager;
  private UserService mockUserService;

  @Before
  public void setUp() {
    mockDealManager = mock(DealManager.class);
    mockUserManager = mock(UserManager.class);
    mockRestaurantManager = mock(RestaurantManager.class);
    mockDealTagManager = mock(DealTagManager.class);
    mockTagManager = mock(TagManager.class);
    mockUserService = mock(UserService.class);
    mockFollowManager = mock(FollowManager.class);
    mockDealVoteCountManager = mock(DealVoteCountManager.class);
    mockRestaurantPlaceManager = mock(RestaurantPlaceManager.class);
    homePageServlet =
        new HomePageServlet(
            mockDealManager,
            mockUserManager,
            mockRestaurantManager,
            mockDealTagManager,
            mockTagManager,
            mockFollowManager,
            mockDealVoteCountManager,
            mockRestaurantPlaceManager,
            mockUserService);
  }

  private void setUpUserAuthentication() {
    User currentUser = new User(EMAIL_A, "");
    when(mockUserService.isUserLoggedIn()).thenReturn(true);
    when(mockUserService.getCurrentUser()).thenReturn(currentUser);
    when(mockUserManager.readUserByEmail(EMAIL_A)).thenReturn(USER_A);
  }

  private void gettingSectionMaps_A() {
    when(mockUserManager.readUser(anyLong())).thenReturn(USER_A);
    when(mockRestaurantManager.readRestaurant(anyLong())).thenReturn(RESTAURANT_A);
    when(mockTagManager.readTags(anyList())).thenReturn(Arrays.asList(TAG_A));
    when(mockDealVoteCountManager.getVotes(anyLong())).thenReturn(VOTE_A);
  }

  private void gettingSectionMaps_ABC() {
    when(mockUserManager.readUser(anyLong())).thenReturn(USER_A);
    when(mockRestaurantManager.readRestaurant(anyLong())).thenReturn(RESTAURANT_A);
    when(mockTagManager.readTags(anyList())).thenReturn(Arrays.asList(TAG_A));
    when(mockDealVoteCountManager.getVotes(DEAL_ID_A)).thenReturn(VOTE_A);
    when(mockDealVoteCountManager.getVotes(DEAL_ID_B)).thenReturn(VOTE_B);
    when(mockDealVoteCountManager.getVotes(DEAL_ID_C)).thenReturn(VOTE_C);
  }

  @Test
  public void testDoGetAllDeals_success() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    List<Deal> DEALS_TRENDING =
        Arrays.asList(DEAL_A, DEAL_B, DEAL_C, DEAL_A, DEAL_B, DEAL_C, DEAL_A, DEAL_B, DEAL_C);
    List<Long> DEALIDS =
        Arrays.asList(
            DEAL_ID_A, DEAL_ID_A, DEAL_ID_A, DEAL_ID_A, DEAL_ID_A, DEAL_ID_A, DEAL_ID_A, DEAL_ID_A);
    List<Deal> DEALS =
        Arrays.asList(DEAL_A, DEAL_A, DEAL_A, DEAL_A, DEAL_A, DEAL_A, DEAL_A, DEAL_A);

    when(request.getParameter("section")).thenReturn(null);
    when(request.getParameter("sort")).thenReturn(null);

    setUpUserAuthentication();

    // Trending section
    when(mockDealManager.getAllDeals()).thenReturn(DEALS_TRENDING);

    // Users section
    when(mockDealManager.getDealsPublishedByUsers(anySet(), anyInt(), eq(null)))
        .thenReturn(DEALIDS);

    // Restaurants section
    when(mockDealManager.getDealsPublishedByRestaurants(anySet(), anyInt(), eq(null)))
        .thenReturn(DEALIDS);

    // Tags section
    when(mockDealManager.getDealsWithIds(anySet(), anyInt(), eq(null))).thenReturn(DEALIDS);
    when(mockDealManager.readDeals(anyList())).thenReturn(DEALS);

    gettingSectionMaps_ABC();

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    homePageServlet.doGet(request, response);

    String expectedTrendingDeals =
        String.format(
            "[%s,%s,%s,%s,%s,%s,%s,%s]",
            HOME_DEAL_C_JSON,
            HOME_DEAL_C_JSON,
            HOME_DEAL_C_JSON,
            HOME_DEAL_B_JSON,
            HOME_DEAL_B_JSON,
            HOME_DEAL_B_JSON,
            HOME_DEAL_A_JSON,
            HOME_DEAL_A_JSON);

    String expectedDeals =
        String.format(
            "[%s,%s,%s,%s,%s,%s,%s,%s]",
            HOME_DEAL_A_JSON,
            HOME_DEAL_A_JSON,
            HOME_DEAL_A_JSON,
            HOME_DEAL_A_JSON,
            HOME_DEAL_A_JSON,
            HOME_DEAL_A_JSON,
            HOME_DEAL_A_JSON,
            HOME_DEAL_A_JSON);
    String expected =
        String.format(
            "{\"trending\":%s, \"users\":%s, \"restaurants\":%s, \"tags\":%s}",
            expectedTrendingDeals, expectedDeals, expectedDeals, expectedDeals);
    JSONAssert.assertEquals(expected, stringWriter.toString(), JSONCompareMode.STRICT);
  }

  @Test
  public void testDoGet_UserLoggedInViewSectionNoSort() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    List<Long> DEALIDS = Arrays.asList(DEAL_ID_A, DEAL_ID_A, DEAL_ID_A);
    List<Deal> DEALS = new ArrayList<Deal>(Arrays.asList(DEAL_A, DEAL_A, DEAL_A));

    when(request.getParameter("section")).thenReturn("users");
    when(request.getParameter("sort")).thenReturn(null);
    setUpUserAuthentication();
    when(mockDealManager.getDealsPublishedByUsers(anySet(), anyInt(), eq(null)))
        .thenReturn(DEALIDS);
    when(mockDealManager.readDeals(anyList())).thenReturn(DEALS);

    gettingSectionMaps_A();

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
    when(request.getParameter("sort")).thenReturn(null);
    when(mockUserService.isUserLoggedIn()).thenReturn(false);
    when(mockDealManager.getAllDeals()).thenReturn(DEALS);

    gettingSectionMaps_A();

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    homePageServlet.doGet(request, response);
    String expectedDeals =
        String.format("[%s,%s,%s]", HOME_DEAL_A_JSON, HOME_DEAL_A_JSON, HOME_DEAL_A_JSON);
    String expected = String.format("{\"trending\":%s}", expectedDeals);
    JSONAssert.assertEquals(expected, stringWriter.toString(), JSONCompareMode.STRICT);
  }

  @Test
  public void testDoGet_UserNotLoggedInViewTrending() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    List<Deal> DEALS = new ArrayList<Deal>(Arrays.asList(DEAL_A, DEAL_A, DEAL_A));

    when(mockUserService.isUserLoggedIn()).thenReturn(false);
    when(request.getParameter("section")).thenReturn("trending");
    when(request.getParameter("sort")).thenReturn(null);
    when(mockDealManager.getAllDeals()).thenReturn(DEALS);

    gettingSectionMaps_A();

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
    when(request.getParameter("sort")).thenReturn(null);

    homePageServlet.doGet(request, response);
    verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
  }

  @Test
  public void testDoGet_UserLoggedInViewSectionSortedVotes() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    setUpUserAuthentication();
    when(request.getParameter("section")).thenReturn("users");
    when(request.getParameter("sort")).thenReturn("votes");

    List<Long> DEALIDS = Arrays.asList(DEAL_ID_A, DEAL_ID_A, DEAL_ID_A);
    List<Deal> DEALS = new ArrayList<Deal>(Arrays.asList(DEAL_A, DEAL_A, DEAL_A));

    when(mockDealManager.getDealsPublishedByUsers(anySet(), eq(-1), eq(null))).thenReturn(DEALIDS);
    when(mockDealVoteCountManager.getDealsInOrderOfVotes(anyList(), eq(-1))).thenReturn(DEALIDS);
    when(mockDealManager.readDealsOrder(anyList())).thenReturn(DEALS);

    gettingSectionMaps_A();

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    homePageServlet.doGet(request, response);

    String expected =
        String.format("[%s,%s,%s]", HOME_DEAL_A_JSON, HOME_DEAL_A_JSON, HOME_DEAL_A_JSON);

    JSONAssert.assertEquals(expected, stringWriter.toString(), JSONCompareMode.STRICT);
  }

  @Test
  public void testDoGet_UserLoggedInViewSectionSortedNew() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    setUpUserAuthentication();
    when(request.getParameter("section")).thenReturn("users");
    when(request.getParameter("sort")).thenReturn("new");

    List<Long> DEALIDS = Arrays.asList(DEAL_ID_A, DEAL_ID_A, DEAL_ID_A);
    List<Deal> DEALS = new ArrayList<Deal>(Arrays.asList(DEAL_A, DEAL_A, DEAL_A));

    when(mockDealManager.getDealsPublishedByUsers(anySet(), anyInt(), eq("new")))
        .thenReturn(DEALIDS);
    when(mockDealManager.readDealsOrder(anyList())).thenReturn(DEALS);

    gettingSectionMaps_A();

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    homePageServlet.doGet(request, response);

    String expected =
        String.format("[%s,%s,%s]", HOME_DEAL_A_JSON, HOME_DEAL_A_JSON, HOME_DEAL_A_JSON);

    JSONAssert.assertEquals(expected, stringWriter.toString(), JSONCompareMode.STRICT);
  }

  @Test
  public void testDoGet_UserLoggedInViewOtherSectionSortedTrending() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    setUpUserAuthentication();
    when(request.getParameter("section")).thenReturn("users");
    when(request.getParameter("sort")).thenReturn("trending");

    List<Long> DEALIDS = new ArrayList<Long>(Arrays.asList(DEAL_ID_A, DEAL_ID_B, DEAL_ID_C));
    List<Deal> DEALS = new ArrayList<Deal>(Arrays.asList(DEAL_A, DEAL_B, DEAL_C));

    when(mockDealManager.getDealsPublishedByUsers(anySet(), eq(-1), eq(null))).thenReturn(DEALIDS);
    when(mockDealManager.readDeals(anyList())).thenReturn(DEALS);
    when(mockDealVoteCountManager.getVotes(DEAL_ID_A)).thenReturn(VOTE_A);
    when(mockDealVoteCountManager.getVotes(DEAL_ID_B)).thenReturn(VOTE_B);
    when(mockDealVoteCountManager.getVotes(DEAL_ID_C)).thenReturn(VOTE_C);

    gettingSectionMaps_ABC();

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    homePageServlet.doGet(request, response);

    String expected =
        String.format("[%s,%s,%s]", HOME_DEAL_C_JSON, HOME_DEAL_B_JSON, HOME_DEAL_A_JSON);

    JSONAssert.assertEquals(expected, stringWriter.toString(), JSONCompareMode.STRICT);
  }

  @Test
  public void testDoGet_SectionIsNotValid() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getParameter("section")).thenReturn("trash");

    homePageServlet.doGet(request, response);
    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoGet_SortIsNotValid() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getParameter("section")).thenReturn("trending");
    when(request.getParameter("sort")).thenReturn("trash");

    homePageServlet.doGet(request, response);
    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoGet_UserLoggedInViewSectionSortDirection() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getParameter("section")).thenReturn("users");
    when(request.getParameter("sort")).thenReturn("distance");

    when(request.getParameter("latitude")).thenReturn(LATITUDE);
    when(request.getParameter("longitude")).thenReturn(LONGITUDE);

    setUpUserAuthentication();

    List<Long> DEALIDS = new ArrayList<Long>(Arrays.asList(DEAL_ID_A, DEAL_ID_B));
    List<Deal> DEALS = new ArrayList<Deal>(Arrays.asList(DEAL_A, DEAL_B));

    when(mockDealManager.getDealsPublishedByUsers(anySet(), eq(-1), eq(null))).thenReturn(DEALIDS);

    when(mockRestaurantPlaceManager.getPlaceIdsOfRestaurant(DEAL_A.restaurantId))
        .thenReturn(new HashSet<>(Arrays.asList(REAL_PLACE_ID_A, REAL_PLACE_ID_B)));
    when(mockRestaurantPlaceManager.getPlaceIdsOfRestaurant(DEAL_B.restaurantId))
        .thenReturn(new HashSet<>(Arrays.asList(REAL_PLACE_ID_C, REAL_PLACE_ID_D)));
    when(mockDealManager.readDeals(DEALIDS)).thenReturn(DEALS);

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    gettingSectionMaps_ABC();

    homePageServlet.doGet(request, response);

    String expected = String.format("[%s,%s]", HOME_DEAL_A_JSON, HOME_DEAL_B_JSON);
    JSONAssert.assertEquals(expected, stringWriter.toString(), JSONCompareMode.STRICT);
  }
}
