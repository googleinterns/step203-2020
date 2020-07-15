package com.google.step.servlets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.step.datamanager.RestaurantManager;
import com.google.step.model.Restaurant;
import java.io.PrintWriter;
import java.io.StringWriter;
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

  private static final long ID_A = 1;
  private static final String NAME_A = "A";
  private static final String BLOBKEY_A = "A_BLOB_KEY";
  private static final Restaurant RESTAURANT_A = new Restaurant(ID_A, NAME_A, BLOBKEY_A);

  private static final String UPDATE_NAME_A = "UPDATE";
  private static final Restaurant UPDATE_RESTAURANT_A =
      new Restaurant(ID_A, UPDATE_NAME_A, BLOBKEY_A);

  private RestaurantManager restaurantManager;

  private RestaurantServlet restaurantServlet;

  @Before
  public void setUp() {
    restaurantManager = mock(RestaurantManager.class);
    restaurantServlet = new RestaurantServlet(restaurantManager);
  }

  /** Successfully returns a restaurant */
  @Test
  public void testDoGet_success() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getPathInfo()).thenReturn("/1");
    when(restaurantManager.readRestaurant(1)).thenReturn(RESTAURANT_A);

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    restaurantServlet.doGet(request, response);

    String expected =
        String.format("{id:%d,name:\"%s\",photoBlobkey:\"%s\"}", ID_A, NAME_A, BLOBKEY_A);

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
    when(restaurantManager.updateRestaurant(any(Restaurant.class))).thenReturn(UPDATE_RESTAURANT_A);

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    restaurantServlet.doPut(request, response);

    String expected =
        String.format("{id:%d,name:\"%s\",photoBlobkey:\"%s\"}", ID_A, UPDATE_NAME_A, BLOBKEY_A);

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
