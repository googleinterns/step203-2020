package com.google.step.servlets;

import static com.google.step.TestConstants.BLOBKEY_A;
import static com.google.step.TestConstants.BLOBKEY_URL_A;
import static com.google.step.TestConstants.DEAL_A;
import static com.google.step.TestConstants.DEAL_A_BRIEF_JSON;
import static com.google.step.TestConstants.PLACE_ID_A;
import static com.google.step.TestConstants.PLACE_ID_B;
import static com.google.step.TestConstants.RESTAURANT_A;
import static com.google.step.TestConstants.RESTAURANT_ID_A;
import static com.google.step.TestConstants.RESTAURANT_NAME_A;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.step.datamanager.DealManager;
import com.google.step.datamanager.FollowManager;
import com.google.step.datamanager.RestaurantManager;
import com.google.step.datamanager.RestaurantPlaceManager;
import com.google.step.model.Deal;
import com.google.step.model.Restaurant;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

@RunWith(JUnit4.class)
public class RestaurantServletTest {

  private static final String UPDATE_NAME_A = "UPDATE";

  private RestaurantManager restaurantManager;
  private DealManager dealManager;
  private RestaurantPlaceManager restaurantPlaceManager;
  private FollowManager followManager;

  private RestaurantServlet restaurantServlet;

  @Before
  public void setUp() {
    restaurantManager = mock(RestaurantManager.class);
    dealManager = mock(DealManager.class);
    restaurantPlaceManager = mock(RestaurantPlaceManager.class);
    followManager = mock(FollowManager.class);

    restaurantServlet =
        new RestaurantServlet(
            restaurantManager, dealManager, restaurantPlaceManager, followManager);
  }

  /** Successfully returns a restaurant */
  @Test
  public void testDoGet_success() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getPathInfo()).thenReturn("/1");
    when(restaurantManager.readRestaurant(1)).thenReturn(RESTAURANT_A);
    List<Deal> deals = Arrays.asList(DEAL_A);
    when(dealManager.getDealsOfRestaurant(1)).thenReturn(deals);
    Set<String> placeIds = new HashSet<>(Arrays.asList(PLACE_ID_A, PLACE_ID_B));
    when(restaurantPlaceManager.getPlaceIdsOfRestaurant(RESTAURANT_ID_A)).thenReturn(placeIds);

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    restaurantServlet.doGet(request, response);

    String expected =
        String.format(
            "{id:%d,name:\"%s\",photoUrl:\"%s\",deals: [%s],placeIds:[%s,%s]}",
            RESTAURANT_ID_A,
            RESTAURANT_NAME_A,
            BLOBKEY_URL_A,
            DEAL_A_BRIEF_JSON,
            PLACE_ID_A,
            PLACE_ID_B);

    JSONAssert.assertEquals(expected, stringWriter.toString(), JSONCompareMode.STRICT);
  }

  /** In the case that the restaurant does not exist */
  @Test
  public void testDoGet_notExist() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    when(request.getPathInfo()).thenReturn("/3");
    when(restaurantManager.readRestaurant(3)).thenReturn(null);

    restaurantServlet.doGet(request, response);
    verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
  }

  /** In the case of an invalid ID e.g. String */
  @Test
  public void testDoGet_invalidID() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    when(request.getPathInfo()).thenReturn("abcd");

    restaurantServlet.doGet(request, response);
    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  /** In the case of an empty ID */
  @Test
  public void testDoGet_noId() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    when(request.getPathInfo()).thenReturn("");

    restaurantServlet.doGet(request, response);
    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  /** Successfully updates a restaurant */
  @Test
  public void testDoPut_success() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getPathInfo()).thenReturn("/1");
    when(request.getParameter("name")).thenReturn(UPDATE_NAME_A);
    Restaurant updatedRestaurant =
        Restaurant.createRestaurantWithBlobkey(RESTAURANT_ID_A, UPDATE_NAME_A, BLOBKEY_A);
    when(restaurantManager.updateRestaurant(any(Restaurant.class))).thenReturn(updatedRestaurant);

    List<Deal> deals = Arrays.asList(DEAL_A);
    when(dealManager.getDealsOfRestaurant(1)).thenReturn(deals);
    Set<String> placeIds = new HashSet<>(Arrays.asList(PLACE_ID_A));
    when(restaurantPlaceManager.getPlaceIdsOfRestaurant(RESTAURANT_ID_A)).thenReturn(placeIds);

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    restaurantServlet.doPut(request, response);

    String expected =
        String.format(
            "{id:%d,name:\"%s\",photoUrl:\"%s\", deals:[%s], placeIds:[%s]}",
            RESTAURANT_ID_A, UPDATE_NAME_A, BLOBKEY_URL_A, DEAL_A_BRIEF_JSON, PLACE_ID_A);

    JSONAssert.assertEquals(expected, stringWriter.toString(), JSONCompareMode.STRICT);
  }

  /** In the case of an invalid ID e.g. String */
  @Test
  public void testDoPut_invalidID() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getPathInfo()).thenReturn("/abcd");

    restaurantServlet.doPut(request, response);
    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  /** In the case of an empty ID */
  @Test
  public void testDoPut_noID() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getPathInfo()).thenReturn("/");

    restaurantServlet.doPut(request, response);
    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  /** In the case that the restaurant does not exist */
  @Test
  public void testDoPut_notExist() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getPathInfo()).thenReturn("/100");

    when(request.getParameter("name")).thenReturn(UPDATE_NAME_A);
    when(restaurantManager.updateRestaurant(any(Restaurant.class))).thenReturn(null);

    restaurantServlet.doPut(request, response);

    verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
  }

  /** Successfully deletes a restaurant */
  @Test
  public void testDoDelete_success() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getPathInfo()).thenReturn("/1");

    restaurantServlet.doDelete(request, response);

    verify(response, never()).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    verify(restaurantManager).deleteRestaurant(anyLong());
    verify(restaurantPlaceManager).deletePlacesOfRestaurant(1);
    verify(followManager).deleteFollowersOfRestaurant(1);
  }

  /** Invalid ID is given for deleting e.g. String */
  @Test
  public void testDoDelete_invalidID() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getPathInfo()).thenReturn("/abcd");

    restaurantServlet.doDelete(request, response);

    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoDelete_noID() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getPathInfo()).thenReturn("/");

    restaurantServlet.doDelete(request, response);

    verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }
}
