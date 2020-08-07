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
import com.google.step.datamanager.TagManager;
import com.google.step.datamanager.UserManager;
import com.google.step.model.Deal;
import java.io.IOException;
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
  private RestaurantManager mockRestaurantManager;
  private DealTagManager mockDealTagManager;
  private TagManager mockTagManager;
  private FollowManager mockFollowManager;
  private DealVoteCountManager mockDealVoteCountManager;
  private UserService mockUserService;
  private HttpServletRequest mockRequest;
  private HttpServletResponse mockResponse;
  private StringWriter stringWriter;
  private PrintWriter writer;

  @Before
  public void setUp() throws IOException {
    mockDealManager = mock(DealManager.class);
    mockUserManager = mock(UserManager.class);
    mockRestaurantManager = mock(RestaurantManager.class);
    mockDealTagManager = mock(DealTagManager.class);
    mockTagManager = mock(TagManager.class);
    mockUserService = mock(UserService.class);
    mockFollowManager = mock(FollowManager.class);
    mockDealVoteCountManager = mock(DealVoteCountManager.class);
    homePageServlet =
        new HomePageServlet(
            mockDealManager,
            mockUserManager,
            mockRestaurantManager,
            mockDealTagManager,
            mockTagManager,
            mockFollowManager,
            mockDealVoteCountManager,
            mockUserService);
    mockRequest = mock(HttpServletRequest.class);
    mockResponse = mock(HttpServletResponse.class);
    stringWriter = new StringWriter();
    writer = new PrintWriter(stringWriter);
    when(mockResponse.getWriter()).thenReturn(writer);
    when(mockResponse.getWriter()).thenReturn(writer);
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
    when(mockDealVoteCountManager.getVotes(DEAL_ID_A)).thenReturn(VOTE_A);
    when(mockDealVoteCountManager.getVotes(DEAL_ID_B)).thenReturn(VOTE_B);
    when(mockDealVoteCountManager.getVotes(DEAL_ID_C)).thenReturn(VOTE_C);
  }

  @Test
  public void testDoGetAllDeals_success() throws Exception {
    List<Deal> DEALS_TRENDING =
        Arrays.asList(DEAL_A, DEAL_B, DEAL_C, DEAL_A, DEAL_B, DEAL_C, DEAL_A, DEAL_B, DEAL_C);
    List<Long> DEALIDS =
        Arrays.asList(
            DEAL_ID_A, DEAL_ID_A, DEAL_ID_A, DEAL_ID_A, DEAL_ID_A, DEAL_ID_A, DEAL_ID_A, DEAL_ID_A);
    List<Deal> DEALS =
        Arrays.asList(DEAL_A, DEAL_A, DEAL_A, DEAL_A, DEAL_A, DEAL_A, DEAL_A, DEAL_A);

    when(mockRequest.getParameter("section")).thenReturn(null);
    when(mockRequest.getParameter("sort")).thenReturn(null);

    setUpUserAuthentication();

    when(mockDealManager.getAllDeals()).thenReturn(DEALS_TRENDING);
    when(mockDealManager.getDealsPublishedByUsers(anySet(), anyInt())).thenReturn(DEALIDS);
    when(mockDealManager.getDealsPublishedByRestaurants(anySet(), anyInt())).thenReturn(DEALIDS);
    when(mockDealManager.getDealsWithIds(anySet(), anyInt())).thenReturn(DEALIDS);
    when(mockDealManager.readDeals(anyList())).thenReturn(DEALS);

    gettingSectionMaps();

    homePageServlet.doGet(mockRequest, mockResponse);

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
    List<Long> DEALIDS = Arrays.asList(DEAL_ID_A, DEAL_ID_A, DEAL_ID_A);
    List<Deal> DEALS = new ArrayList<Deal>(Arrays.asList(DEAL_A, DEAL_A, DEAL_A));

    when(mockRequest.getParameter("section")).thenReturn("users");
    when(mockRequest.getParameter("sort")).thenReturn(null);
    setUpUserAuthentication();
    when(mockDealManager.getDealsPublishedByUsers(anySet(), anyInt())).thenReturn(DEALIDS);
    when(mockDealManager.readDeals(anyList())).thenReturn(DEALS);

    gettingSectionMaps();

    homePageServlet.doGet(mockRequest, mockResponse);

    String expected =
        String.format("[%s,%s,%s]", HOME_DEAL_A_JSON, HOME_DEAL_A_JSON, HOME_DEAL_A_JSON);
    JSONAssert.assertEquals(expected, stringWriter.toString(), JSONCompareMode.STRICT);
  }

  @Test
  public void testDoGet_UserNotLoggedInAllDeals() throws Exception {
    List<Deal> DEALS = new ArrayList<Deal>(Arrays.asList(DEAL_A, DEAL_A, DEAL_A));

    when(mockRequest.getParameter("section")).thenReturn(null);
    when(mockRequest.getParameter("sort")).thenReturn(null);
    when(mockUserService.isUserLoggedIn()).thenReturn(false);
    when(mockDealManager.getAllDeals()).thenReturn(DEALS);

    gettingSectionMaps();

    homePageServlet.doGet(mockRequest, mockResponse);
    String expectedDeals =
        String.format("[%s,%s,%s]", HOME_DEAL_A_JSON, HOME_DEAL_A_JSON, HOME_DEAL_A_JSON);
    String expected = String.format("{\"trending\":%s}", expectedDeals);
    JSONAssert.assertEquals(expected, stringWriter.toString(), JSONCompareMode.STRICT);
  }

  @Test
  public void testDoGet_UserNotLoggedInViewTrending() throws Exception {
    List<Deal> DEALS = new ArrayList<Deal>(Arrays.asList(DEAL_A, DEAL_A, DEAL_A));

    when(mockUserService.isUserLoggedIn()).thenReturn(false);
    when(mockRequest.getParameter("section")).thenReturn("trending");
    when(mockRequest.getParameter("sort")).thenReturn(null);
    when(mockDealManager.getAllDeals()).thenReturn(DEALS);

    gettingSectionMaps();

    homePageServlet.doGet(mockRequest, mockResponse);

    String expected =
        String.format("[%s,%s,%s]", HOME_DEAL_A_JSON, HOME_DEAL_A_JSON, HOME_DEAL_A_JSON);

    JSONAssert.assertEquals(expected, stringWriter.toString(), JSONCompareMode.STRICT);
  }

  @Test
  public void testDoGet_UserNotLoggedInViewOtherSection() throws Exception {
    when(mockUserService.isUserLoggedIn()).thenReturn(false);
    when(mockRequest.getParameter("section")).thenReturn("users");
    when(mockRequest.getParameter("sort")).thenReturn(null);

    homePageServlet.doGet(mockRequest, mockResponse);
    verify(mockResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
  }

  @Test
  public void testDoGet_UserLoggedInViewSectionSortedVotes() throws Exception {
    setUpUserAuthentication();
    when(mockRequest.getParameter("section")).thenReturn("users");
    when(mockRequest.getParameter("sort")).thenReturn("votes");

    List<Long> DEALIDS = Arrays.asList(DEAL_ID_A, DEAL_ID_A, DEAL_ID_A);
    List<Deal> DEALS = new ArrayList<Deal>(Arrays.asList(DEAL_A, DEAL_A, DEAL_A));

    when(mockDealManager.getDealsPublishedByUsers(anySet(), eq(-1))).thenReturn(DEALIDS);
    when(mockDealVoteCountManager.sortDealsInOrderOfVotes(anyList(), eq(-1))).thenReturn(DEALIDS);
    when(mockDealManager.readDealsOrder(anyList())).thenReturn(DEALS);

    gettingSectionMaps();

    homePageServlet.doGet(mockRequest, mockResponse);

    String expected =
        String.format("[%s,%s,%s]", HOME_DEAL_A_JSON, HOME_DEAL_A_JSON, HOME_DEAL_A_JSON);

    JSONAssert.assertEquals(expected, stringWriter.toString(), JSONCompareMode.STRICT);
  }

  @Test
  public void testDoGet_UserLoggedInViewSectionSortedNew() throws Exception {
    setUpUserAuthentication();
    when(mockRequest.getParameter("section")).thenReturn("users");
    when(mockRequest.getParameter("sort")).thenReturn("new");

    List<Long> DEALIDS = Arrays.asList(DEAL_ID_A, DEAL_ID_A, DEAL_ID_A);
    List<Deal> DEALS = new ArrayList<Deal>(Arrays.asList(DEAL_A, DEAL_A, DEAL_A));

    when(mockDealManager.getDealsPublishedByUsersSortByNew(anySet(), anyInt())).thenReturn(DEALIDS);
    when(mockDealManager.readDealsOrder(anyList())).thenReturn(DEALS);

    gettingSectionMaps();

    homePageServlet.doGet(mockRequest, mockResponse);

    String expected =
        String.format("[%s,%s,%s]", HOME_DEAL_A_JSON, HOME_DEAL_A_JSON, HOME_DEAL_A_JSON);

    JSONAssert.assertEquals(expected, stringWriter.toString(), JSONCompareMode.STRICT);
  }

  @Test
  public void testDoGet_UserLoggedInViewOtherSectionSortedTrending() throws Exception {
    setUpUserAuthentication();
    when(mockRequest.getParameter("section")).thenReturn("users");
    when(mockRequest.getParameter("sort")).thenReturn("trending");

    List<Long> DEALIDS = new ArrayList<Long>(Arrays.asList(DEAL_ID_A, DEAL_ID_B, DEAL_ID_C));
    List<Deal> DEALS = new ArrayList<Deal>(Arrays.asList(DEAL_A, DEAL_B, DEAL_C));

    when(mockDealManager.getDealsPublishedByUsers(anySet(), eq(-1))).thenReturn(DEALIDS);
    when(mockDealManager.readDeals(anyList())).thenReturn(DEALS);

    gettingSectionMaps();

    homePageServlet.doGet(mockRequest, mockResponse);

    String expected =
        String.format("[%s,%s,%s]", HOME_DEAL_C_JSON, HOME_DEAL_B_JSON, HOME_DEAL_A_JSON);

    JSONAssert.assertEquals(expected, stringWriter.toString(), JSONCompareMode.STRICT);
  }

  @Test
  public void testDoGet_SectionIsNotValid() throws Exception {
    when(mockRequest.getParameter("section")).thenReturn("trash");

    homePageServlet.doGet(mockRequest, mockResponse);
    verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoGet_SortIsNotValid() throws Exception {
    when(mockRequest.getParameter("section")).thenReturn("trending");
    when(mockRequest.getParameter("sort")).thenReturn("trash");

    homePageServlet.doGet(mockRequest, mockResponse);
    verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }
}
