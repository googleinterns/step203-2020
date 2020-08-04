package com.google.step.servlets;

import static com.google.step.TestConstants.BLOBKEY_A;
import static com.google.step.TestConstants.PLACE_ID_A;
import static com.google.step.TestConstants.RESTAURANT_A;
import static com.google.step.TestConstants.RESTAURANT_A_BRIEF_JSON;
import static com.google.step.TestConstants.RESTAURANT_B;
import static com.google.step.TestConstants.RESTAURANT_B_BRIEF_JSON;
import static com.google.step.TestConstants.RESTAURANT_ID_A;
import static com.google.step.TestConstants.RESTAURANT_NAME_A;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.step.datamanager.RestaurantManager;
import com.google.step.datamanager.RestaurantPlaceManager;
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
import org.mockito.BDDMockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ImageUploader.class)
public class RestaurantPostListServletTest {

  private RestaurantManager mockRestaurantManager;
  private RestaurantPlaceManager mockRestaurantPlaceManager;
  private RestaurantPostListServlet restaurantPostServlet;

  HttpServletRequest mockRequest;

  @Before
  public void setUp() {
    mockRequest = mock(HttpServletRequest.class);

    PowerMockito.mockStatic(ImageUploader.class);
    BDDMockito.given(ImageUploader.getUploadedImageBlobkey(mockRequest, "pic"))
        .willReturn(BLOBKEY_A);

    mockRestaurantManager = mock(RestaurantManager.class);
    mockRestaurantPlaceManager = mock(RestaurantPlaceManager.class);
    restaurantPostServlet =
        new RestaurantPostListServlet(mockRestaurantManager, mockRestaurantPlaceManager);
  }

  /** Successfully creates a new restaurant */
  @Test
  public void testDoPost_success() throws Exception {
    HttpServletResponse mockResponse = mock(HttpServletResponse.class);

    when(mockRequest.getParameter("name")).thenReturn(RESTAURANT_NAME_A);
    when(mockRestaurantManager.createRestaurantWithBlobKey(RESTAURANT_NAME_A, BLOBKEY_A))
        .thenReturn(RESTAURANT_A);
    when(mockRequest.getParameter("places")).thenReturn(PLACE_ID_A);

    restaurantPostServlet.doPost(mockRequest, mockResponse);
    verify(mockResponse).sendRedirect("/restaurant/" + RESTAURANT_ID_A);
    verify(mockRestaurantPlaceManager)
        .updatePlacesOfRestaurant(RESTAURANT_ID_A, Arrays.asList(PLACE_ID_A));
  }

  @Test
  public void testDoPost_missingPlaces() throws Exception {
    HttpServletResponse mockResponse = mock(HttpServletResponse.class);

    when(mockRequest.getParameter("name")).thenReturn(RESTAURANT_NAME_A);
    when(mockRestaurantManager.createRestaurantWithBlobKey(RESTAURANT_NAME_A, BLOBKEY_A))
        .thenReturn(RESTAURANT_A);

    restaurantPostServlet.doPost(mockRequest, mockResponse);
    verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void testDoGet() throws IOException, JSONException {
    HttpServletResponse mockResponse = mock(HttpServletResponse.class);

    when(mockRequest.getParameter("name")).thenReturn(RESTAURANT_NAME_A);
    when(mockRestaurantManager.getAllRestaurants())
        .thenReturn(Arrays.asList(RESTAURANT_A, RESTAURANT_B));

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(mockResponse.getWriter()).thenReturn(writer);

    restaurantPostServlet.doGet(mockRequest, mockResponse);

    String expectedJson = "[" + RESTAURANT_A_BRIEF_JSON + "," + RESTAURANT_B_BRIEF_JSON + "]";
    JSONAssert.assertEquals(expectedJson, stringWriter.toString(), JSONCompareMode.STRICT);
  }
}
