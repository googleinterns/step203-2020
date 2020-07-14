package com.google.step.servlets;

import static com.google.step.TestConstants.BLOBKEY_A;
import static com.google.step.TestConstants.BLOBKEY_B;
import static com.google.step.TestConstants.BLOBKEY_C;
import static com.google.step.TestConstants.DATE_A;
import static com.google.step.TestConstants.DATE_B;
import static com.google.step.TestConstants.DESCRIPTION_A;
import static com.google.step.TestConstants.DESCRIPTION_B;
import static com.google.step.TestConstants.DESCRIPTION_C;
import static com.google.step.TestConstants.RESTAURANT_ID_A;
import static com.google.step.TestConstants.RESTAURANT_ID_B;
import static com.google.step.TestConstants.RESTAURANT_ID_C;
import static com.google.step.TestConstants.SOURCE_A;
import static com.google.step.TestConstants.SOURCE_B;
import static com.google.step.TestConstants.SOURCE_C;
import static com.google.step.TestConstants.USER_ID_A;
import static com.google.step.TestConstants.USER_ID_B;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.step.datamanager.DealManager;
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

  @Before
  public void setUp() {
    dealManager = mock(DealManager.class);
    homePageServlet = new HomePageServlet(dealManager);
  }

  @Test
  public void testDoGet_success() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    Deal DEAL_A =
        new Deal(
            DEAL_ID_A,
            DESCRIPTION_A,
            BLOBKEY_A,
            DATE_A,
            DATE_B,
            SOURCE_A,
            USER_ID_A,
            RESTAURANT_ID_A,
            TIME_A);
    Deal DEAL_B =
        new Deal(
            DEAL_ID_B,
            DESCRIPTION_B,
            BLOBKEY_B,
            DATE_A,
            DATE_B,
            SOURCE_B,
            USER_ID_B,
            RESTAURANT_ID_B,
            TIME_B);
    Deal DEAL_C =
        dealManager.createDeal(
            DEAL_ID_C,
            DESCRIPTION_C,
            BLOBKEY_C,
            DATE_A,
            DATE_B,
            SOURCE_C,
            USER_ID_B,
            RESTAURANT_ID_C,
            TIME_C);
    System.out.println("DSF" + DEAL_A);

    List<Deal> TRENDING_DEALS = new ArrayList<Deal>(Arrays.asList(DEAL_A, DEAL_B, DEAL_C));
    List<Deal> DEALS_BY_FOLLOWED_USERS = new ArrayList<Deal>(Arrays.asList(DEAL_B, DEAL_A, DEAL_C));
    List<Deal> DEALS_BY_FOLLOWED_RESTAURANTS =
        new ArrayList<Deal>(Arrays.asList(DEAL_C, DEAL_B, DEAL_A));
    List<Deal> DEALS_BY_FOLLOWED_TAGS = new ArrayList<Deal>(Arrays.asList(DEAL_A, DEAL_C, DEAL_B));

    System.out.println(TRENDING_DEALS);
    when(dealManager.getTrendingDeals()).thenReturn(TRENDING_DEALS);
    when(dealManager.getDealsPublishedByFollowedUsers(1)).thenReturn(DEALS_BY_FOLLOWED_USERS);
    when(dealManager.getDealsPublishedByFollowedRestaurants(1))
        .thenReturn(DEALS_BY_FOLLOWED_RESTAURANTS);
    when(dealManager.getDealsPublishedByFollowedTags(1)).thenReturn(DEALS_BY_FOLLOWED_TAGS);

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    homePageServlet.doGet(request, response);

    String expected = "";
    /*String.format(
    "[{id:%d,dealId:%d,userId:%d,content:\"%s\",timestamp:\"%s\"}",
    COMMENT_ID_A, DEAL_ID_A, USER_ID_A, UPDATE_CONTENT_A, TIME_A);*/
    System.out.println(stringWriter.toString());

    JSONAssert.assertEquals(expected, stringWriter.toString(), JSONCompareMode.STRICT);
  }
}
