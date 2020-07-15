package com.google.step.servlets;

import static com.google.step.TestConstants.DEAL_A;
import static com.google.step.TestConstants.HOME_DEAL_A_JSON;
import static com.google.step.TestConstants.RESTAURANT_A;
import static com.google.step.TestConstants.TAG_A;
import static com.google.step.TestConstants.TAG_ID_A;
import static com.google.step.TestConstants.USER_A;
import static com.google.step.TestConstants.VOTE_A;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
  private DealManager dealManager;
  private UserManager userManager;
  private VoteManager voteManager;
  private RestaurantManager restaurantManager;
  private DealTagManager dealTagManager;
  private TagManager tagManager;
  private FollowManager followManager;

  @Before
  public void setUp() {
    dealManager = mock(DealManager.class);
    userManager = mock(UserManager.class);
    voteManager = mock(VoteManager.class);
    restaurantManager = mock(RestaurantManager.class);
    dealTagManager = mock(DealTagManager.class);
    tagManager = mock(TagManager.class);
    followManager = mock(FollowManager.class);
    homePageServlet =
        new HomePageServlet(
            dealManager,
            userManager,
            restaurantManager,
            voteManager,
            dealTagManager,
            tagManager,
            followManager);
  }

  @Test
  public void testDoGet_success() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    List<Deal> DEALS = new ArrayList<Deal>(Arrays.asList(DEAL_A, DEAL_A, DEAL_A));

    when(dealManager.getTrendingDeals()).thenReturn(DEALS);
    when(dealManager.getDealsPublishedByFollowedUsers(anyList())).thenReturn(DEALS);
    when(dealManager.getDealsPublishedByFollowedRestaurants(anyList())).thenReturn(DEALS);
    when(dealManager.getDealsPublishedByFollowedTags(anyList())).thenReturn(DEALS);

    when(userManager.readUser(anyLong())).thenReturn(USER_A);
    when(restaurantManager.readRestaurant(anyLong())).thenReturn(RESTAURANT_A);
    when(dealTagManager.getTagIdsOfDeal(anyLong())).thenReturn(Arrays.asList(TAG_ID_A));
    when(tagManager.readTag(anyLong())).thenReturn(TAG_A);
    when(voteManager.getVotes(anyLong())).thenReturn(VOTE_A);
    when(tagManager.readTags(anyList())).thenReturn(Arrays.asList(TAG_A));

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    homePageServlet.doGet(request, response);

    String expectedDeals =
        String.format("[%s,%s,%s]", HOME_DEAL_A_JSON, HOME_DEAL_A_JSON, HOME_DEAL_A_JSON);
    String expected =
        String.format(
            "{popularDeals:%s," + "usersIFollow:%s," + "restaurantsIFollow:%s," + "tagsIFollow:%s}",
            expectedDeals, expectedDeals, expectedDeals, expectedDeals);
    JSONAssert.assertEquals(expected, stringWriter.toString(), JSONCompareMode.STRICT);
  }
}
