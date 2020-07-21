package com.google.step.servlets;

import static com.google.step.TestConstants.BLOBKEY_A;
import static com.google.step.TestConstants.BLOBKEY_URL_A;
import static com.google.step.TestConstants.RESTAURANT_A;
import static com.google.step.TestConstants.RESTAURANT_ID_A;
import static com.google.step.TestConstants.RESTAURANT_NAME_A;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.step.datamanager.RestaurantManager;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
public class RestaurantPostServletTest {

  private RestaurantManager restaurantManager;
  private RestaurantPostServlet restaurantPostServlet;

  HttpServletRequest mockRequest;

  @Before
  public void setUp() {
    mockRequest = mock(HttpServletRequest.class);

    PowerMockito.mockStatic(ImageUploader.class);
    BDDMockito.given(ImageUploader.getUploadedImageBlobkey(mockRequest, "pic"))
        .willReturn(BLOBKEY_A);

    restaurantManager = mock(RestaurantManager.class);
    restaurantPostServlet = new RestaurantPostServlet(restaurantManager);
  }

  /** Successfully creates a new restaurant */
  @Test
  public void testDoPost_success() throws Exception {
    HttpServletResponse mockResponse = mock(HttpServletResponse.class);

    when(mockRequest.getParameter("name")).thenReturn(RESTAURANT_NAME_A);
    when(restaurantManager.createRestaurant(RESTAURANT_NAME_A, BLOBKEY_A)).thenReturn(RESTAURANT_A);

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(mockResponse.getWriter()).thenReturn(writer);

    restaurantPostServlet.doPost(mockRequest, mockResponse);
    String expected =
        String.format(
            "{id:%d,name:\"%s\",image:\"%s\",deals:[]}",
            RESTAURANT_ID_A, RESTAURANT_NAME_A, BLOBKEY_URL_A);

    JSONAssert.assertEquals(expected, stringWriter.toString(), JSONCompareMode.STRICT);
    verify(mockResponse).sendRedirect("/restaurant/" + RESTAURANT_ID_A);
  }
}
