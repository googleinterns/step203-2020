package com.google.step.servlets;

import static com.google.step.TestConstants.RESTAURANT_ID_A;
import static org.mockito.Mockito.mock;
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

@RunWith(JUnit4.class)
public class RestaurantPostServletTest {

  private static final long ID_A = 1;
  private static final String NAME_A = "A";
  private static final String BLOBKEY_A = "A_BLOB_KEY";
  private static final Restaurant RESTAURANT_A = new Restaurant(ID_A, NAME_A, BLOBKEY_A);

  private RestaurantManager restaurantManager;

  private RestaurantPostServlet restaurantPostServlet;

  @Before
  public void setUp() {
    restaurantManager = mock(RestaurantManager.class);
    restaurantPostServlet = new RestaurantPostServlet(restaurantManager);
  }

  /** Successfully creates a new restaurant */
  @Test
  public void testDoPost_success() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getParameter("name")).thenReturn(NAME_A);
    when(restaurantManager.createRestaurant(NAME_A, BLOBKEY_A)).thenReturn(RESTAURANT_A);

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    restaurantPostServlet.doPost(request, response);

    verify(response).sendRedirect("/restaurant/" + RESTAURANT_ID_A);
  }
}
